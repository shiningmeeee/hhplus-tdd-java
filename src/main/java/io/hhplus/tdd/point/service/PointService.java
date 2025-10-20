package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;

import java.util.List;

public interface PointService {

    UserPoint getUserPoint(long id);

    UserPoint chargePoint(long id, long amount);

    UserPoint chargePointConcurrently(long id, long amount);

    UserPoint usePoint(long id, long amount) throws IllegalArgumentException;

    List<PointHistory> getPointHistories(long id);

    PointHistory insertHistory(PointHistory pointHistory);

}
