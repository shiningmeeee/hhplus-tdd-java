package io.hhplus.tdd.point.service.impl;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.enums.TransactionType;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultPointService implements PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public DefaultPointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint getUserPoint(long id){
        UserPoint userPoint = userPointTable.selectById(id);
        return userPoint;
    }

    @Override
    public UserPoint chargePoint(long id, long amount) {
        UserPoint userPoint = userPointTable.selectById(id);
        long updatedPoint = userPoint.point() + amount;
        UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, updatedPoint);
        //pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return updatedUserPoint;
    }

    @Override
    public UserPoint usePoint(long id, long amount) throws IllegalArgumentException {

        UserPoint userPoint = userPointTable.selectById(id);
        if(userPoint.point() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        long pointAmountAfterUse = userPoint.point() - amount;
        UserPoint userPointAfterUse = userPointTable.insertOrUpdate(id, pointAmountAfterUse);

        return userPointAfterUse;
    }

    @Override
    public List<PointHistory> getPointHistories(long id) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(id);
        return pointHistories;
    }

    @Override
    public PointHistory insertHistory(PointHistory pointHistory) {
        PointHistory insertedPointHistory = pointHistoryTable.insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());
        return insertedPointHistory;
    }

}
