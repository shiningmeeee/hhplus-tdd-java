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
    public UserPoint getUserPoint(long id) throws IllegalArgumentException {
        UserPoint userPoint = userPointTable.selectById(id);
        if (userPoint == null) {
            throw new IllegalArgumentException("User not found");
        }
        return userPoint;
    }
}
