package io.hhplus.tdd.point.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.impl.DefaultPointService;
import io.hhplus.tdd.database.UserPointTable;
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
        //
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
    void givenNotExistingId_whenGetUserPoint_thenThrowIllegalArgumentException() {
        // user table에 존재하지 않는 id인 경우 포인트를 조회하는 테스트입니다

        //given
        long id = 1234L;
        
        // stub 설정 - userPointTable.selectById()가 null 반환하도록 설정
        when(userPointTable.selectById(id)).thenReturn(null);
        
        //when & then
        assertThrows(IllegalArgumentException.class, () -> pointService.getUserPoint(id));
    }

}
