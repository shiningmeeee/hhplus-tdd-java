package io.hhplus.tdd.point.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.impl.DefaultPointService;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.enums.TransactionType;
import io.hhplus.tdd.database.PointHistoryTable;

@ExtendWith(MockitoExtension.class)
public class DefaultPointServiceTest {

    @Mock
    private UserPointTable userPointTable;
    
    @Mock
    private PointHistoryTable pointHistoryTable;
    
    private PointService pointService;

    @BeforeEach
    void setUp() {
        pointService = new DefaultPointService(userPointTable, pointHistoryTable);
    }

    @Test
    @DisplayName("id에 대해 보유한 포인트를 조회한다")
    void givenId_whenGetUserPoint_thenReturnUserPoint() {
        // 기본적인 포인트 조회 기능 테스트입니다

        //given
        long id = 1234L;
        UserPoint expectedUserPoint = new UserPoint(id, 1000L, System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(expectedUserPoint);

        //when
        UserPoint userPoint = pointService.getUserPoint(id);

        //then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("존재하지 않는 id에 대해 보유한 포인트를 조회한다")
    void givenNotExistingId_whenGetUserPoint_thenReturnZero() {
        // user table에 존재하지 않는 id인 경우 포인트를 조회하는 테스트입니다

        //given
        long id = 1234L;
        UserPoint emptyUserPoint = UserPoint.empty(id);
        when(userPointTable.selectById(id)).thenReturn(emptyUserPoint);
        
        //when
        UserPoint userPoint = pointService.getUserPoint(id);

        //then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(emptyUserPoint.point());
    }

    @Test
    @DisplayName("id에 대해 포인트를 처음 충전하면 id를 저장하고 포인트를 충전한다")
    void givenNewId_whenChargePoint_thenSaveIdAndReturnUserPoint() {
        // 처음 포인트를 충전하는 경우 user table에 id를 저장합니다
        // 포인트를 충전한 후 업데이트된 포인트를 반환하는 테스트입니다

        //given
        long id = 1234L;
        long chargeAmount = 1000L;

        //when
        UserPoint expectedUserPoint = new UserPoint(id, chargeAmount, System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(UserPoint.empty(id));
        when(userPointTable.insertOrUpdate(id, chargeAmount)).thenReturn(expectedUserPoint);
        UserPoint userPoint = pointService.chargePoint(id, chargeAmount);

        //then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(expectedUserPoint.point());
    }

    @Test
    @DisplayName("포인트를 보유한 id에 대해 포인트를 충전한다")
    void givenId_whenChargePoint_thenSaveIdAndReturnUserPoint() {
        // 포인트를 충전한 후 업데이트된 포인트를 반환하는 테스트입니다

        //given
        long id = 1234L;
        long chargeAmount = 1000L;
        UserPoint asisUserPoint = new UserPoint(id, 500L, System.currentTimeMillis());

        //when
        UserPoint expectedUserPoint = new UserPoint(id, asisUserPoint.point() + chargeAmount, System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(asisUserPoint);
        when(userPointTable.insertOrUpdate(id, asisUserPoint.point() + chargeAmount)).thenReturn(expectedUserPoint);
        UserPoint userPoint = pointService.chargePoint(id, chargeAmount);

        //then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(expectedUserPoint.point());
    }

    @Test
    @DisplayName("포인트를 보유하지 않은 id에 대해 포인트를 사용한다")
    void givenNoPoint_whenUsePoint_thenThrowException() {
        // 포인트를 보유하지 않은 id에 대해 포인트를 사용하는 테스트입니다

        //given
        long id = 1234L;
        long useAmount = 100L;
        UserPoint emptyUserPoint = UserPoint.empty(id);

        //when
        when(userPointTable.selectById(id)).thenReturn(emptyUserPoint);

        //then
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoint(id, useAmount);
        });
    }

    @Test
    @DisplayName("포인트를 보유한 id에 대해 포인트를 사용한다 - 잔액이 남는 경우")
    void givenPointUseAmount_whenUsePoint_thenReturnRemainUserPoint() {
        // 보유한 포인트를 사용하고 잔액을 반환하는 테스트입니다 - 잔액이 남는 경우

        //given
        long id = 1234L;
        long useAmount = 300L;
        UserPoint asisUserPoint = new UserPoint(id, 1000L, System.currentTimeMillis());

        //when
        UserPoint expectedUserPoint = new UserPoint(id, asisUserPoint.point() - useAmount , System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(asisUserPoint);
        when(userPointTable.insertOrUpdate(id, expectedUserPoint.point())).thenReturn(expectedUserPoint);
        UserPoint userPoint = pointService.usePoint(id, useAmount);

        //then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(expectedUserPoint.point());

    }

    @Test
    @DisplayName("포인트를 보유한 id에 대해 포인트를 사용한다 - 잔액이 남지 않는 경우")
    void givenPointUseAmount_whenUsePoint_thenReturnZeroPoint() {
        // 보유한 포인트를 사용하고 잔액이 남지 않는 경우에 대한 테스트입니다
        // id가 없는 경우와 잔액이 남지 않는 경우 selectById() 결과가 동일하기 때문에 사용 후 잔액이 남지 않는 경우를 테스트합니다

        //given
        long id = 1234L;
        long useAmount = 1000L;
        UserPoint asisUserPoint = new UserPoint(id, 1000L, System.currentTimeMillis());

        //when
        UserPoint expectedUserPoint = new UserPoint(id, asisUserPoint.point() - useAmount , System.currentTimeMillis());
        when(userPointTable.selectById(id)).thenReturn(asisUserPoint);
        when(userPointTable.insertOrUpdate(id, expectedUserPoint.point())).thenReturn(expectedUserPoint);
        UserPoint userPoint = pointService.usePoint(id, useAmount);

        //then
        assertThat(userPoint).isNotNull();
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(0);

    }

    @Test
    @DisplayName("id에 대해 보유한 포인트보다 더 많은 포인트 사용을 시도한다")
    void givenUsePointOverAmount_whenUsePoint_thenThrowException() {
        // 보유한 포인트보다 더 많은 양을 사용하는 테스트입니다

        //given
        long id = 1234L;
        long useAmount = 1000L;
        UserPoint asisUserPoint = new UserPoint(id, 500L, System.currentTimeMillis());

        //when
        when(userPointTable.selectById(id)).thenReturn(asisUserPoint);

        //then
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.usePoint(id, useAmount);
        });
    }

    @Test
    @DisplayName("id에 대해 포인트 내역을 추가한다")
    void givenUseAmount_whenAddUseHistory_thenReturnHistories() {
        // id에 대해 포인트 내역을 추가하는 테스트입니다
        
        //given
        long id = 1234L;
        long userId = 11111L;
        long useAmount = 100L;
        PointHistory pointHistory = new PointHistory(id, userId, useAmount, TransactionType.USE, System.currentTimeMillis());
        when(pointHistoryTable.insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis()))
            .thenReturn(pointHistory);

        //when
        PointHistory expectedPointHistory = pointService.insertHistory(pointHistory);

        //then
        verify(pointHistoryTable, times(1)).insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());
        assertThat(expectedPointHistory).isNotNull();
        assertThat(expectedPointHistory.userId()).isEqualTo(pointHistory.userId());
        assertThat(expectedPointHistory.amount()).isEqualTo(pointHistory.amount());
        assertThat(expectedPointHistory.type()).isEqualTo(pointHistory.type());
        assertThat(expectedPointHistory.updateMillis()).isEqualTo(pointHistory.updateMillis());

    }

    @Test
    @DisplayName("id에 대해 포인트 내역을 조회한다")
    void givenNoHistories_whenGetPointHistories_thenReturnEmptyHistories() {
        // PointHistoryTable에서 id에 대한 포인트 내역을 조회하는 테스트입니다

        //given
        long id = 1234L;
        long userId = 11111L;
        long useAmount = 100L, chargeAmount = 200L;
        PointHistory usePointHistory = new PointHistory(id, userId, useAmount, TransactionType.USE, System.currentTimeMillis());
        PointHistory chargePointHistory = new PointHistory(id, userId, chargeAmount, TransactionType.CHARGE, System.currentTimeMillis());
        when(pointHistoryTable.insert(usePointHistory.userId(), usePointHistory.amount(), usePointHistory.type(), usePointHistory.updateMillis()))
            .thenReturn(usePointHistory);
        when(pointHistoryTable.insert(chargePointHistory.userId(), chargePointHistory.amount(), chargePointHistory.type(), chargePointHistory.updateMillis()))
            .thenReturn(chargePointHistory);
        List<PointHistory> expectedHistories = List.of(usePointHistory, chargePointHistory);
        when(pointHistoryTable.selectAllByUserId(id)).thenReturn(expectedHistories);

        //when
        pointService.insertHistory(usePointHistory);
        pointService.insertHistory(chargePointHistory);
        List<PointHistory> pointHistories = pointService.getPointHistories(id);

        System.out.println("pointHistories = " + pointHistories);

        //then
        assertThat(pointHistories).isNotNull().hasSize(2);
        assertThat(pointHistories)
            .extracting(PointHistory::userId, PointHistory::amount, PointHistory::type)
            .containsExactly(
                tuple(userId, useAmount, TransactionType.USE),
                tuple(userId, chargeAmount, TransactionType.CHARGE)
            );
        
    }

}