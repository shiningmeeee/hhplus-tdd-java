package io.hhplus.tdd.point.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.service.impl.DefaultPointService;

public class DefaultPointServiceConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(DefaultPointServiceConcurrencyTest.class);
    
    private PointService pointService;

    @BeforeEach
    void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();
        pointService = new DefaultPointService(userPointTable, pointHistoryTable);
    }

    @Test
    @DisplayName("동시성 테스트: 포인트 충전 후 포인트 조회 비정상")
    void givenSeveralRequests_whenChargePoint_thenReturnWrongHistories() {
        // N개 스레드로 포인트 충전 요청을 동시에 보낸 후 race condition이 발생했는지 확인

        //given
        long id = 1234L;
        int threadCount = 10;
        int chargesPerThread = 100;
        

        //when
        log.info("동시성 테스트 시작 - {} 스레드, 각각 {}번씩 충전", threadCount, chargesPerThread);

        CompletableFuture.allOf(
            IntStream.range(0, threadCount).mapToObj(i -> 
            CompletableFuture.runAsync(() -> {
                log.info("{}번 스레드 실행 시작", i);
                for(int j=0;j<chargesPerThread;j++) {
                    pointService.chargePoint(id, 100L); // 동시성을 고려하지 않은 메소드
                }
                log.info("{}번 스레드 실행 완료", i);
            })
        ).toArray(CompletableFuture[]::new)
        ).join(); // 모든 작업이 끝날 때까지 대기

        log.info("모든 스레드 작업 완료");
        List<PointHistory> pointHistories = pointService.getPointHistories(id);

        //then
        log.info("총 히스토리 개수: {}, 예상: {}", pointHistories.size(), threadCount * chargesPerThread);
        assertThat(pointHistories).isNotNull().hasSizeLessThan(threadCount * 100);

    }

    @Test
    @DisplayName("동시성 테스트: 포인트 충전 후 포인트 조회 정상")
    void givenSeveralRequests_whenChargePoint_thenReturnRightHistories() {
        // N개 스레드로 포인트 충전 요청을 동시에 보낸 후 race condition이 발생했는지 확인

        //given
        // 약 200회 포인트 충전 요청
        long id = 1234L;
        int threadCount = 32;
        int chargesPerThread = 31;

        //when
        log.info("동시성 테스트 시작 - {} 스레드, 각각 {}번씩 충전", threadCount, chargesPerThread);

        CompletableFuture.allOf(
            IntStream.range(0, threadCount).mapToObj(i -> 
            CompletableFuture.runAsync(() -> {
                log.info("{}번 스레드 실행 시작", i);
                for(int j=0;j<chargesPerThread;j++) {
                    pointService.chargePointConcurrently(id, 100L); // 동시성을 고려한 메소드
                }
                log.info("{}번 스레드 실행 완료", i);
            })
        ).toArray(CompletableFuture[]::new)
        ).join(); // 모든 작업이 끝날 때까지 대기

        log.info("모든 스레드 작업 완료");
        List<PointHistory> pointHistories = pointService.getPointHistories(id);

        //then
        log.info("총 히스토리 개수: {}, 예상: {}", pointHistories.size(), threadCount * chargesPerThread);
        assertThat(pointHistories).isNotNull().hasSize(threadCount * chargesPerThread);

    }

}