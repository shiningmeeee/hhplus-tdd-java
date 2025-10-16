package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.UserPoint;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    /**
     * 특정 유저의 포인트 정보를 조회합니다.
     *
     * @param id 조회할 사용자 ID
     * @return 해당 사용자의 포인트 정보가 담긴 {@link UserPoint} 객체
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return new UserPoint(0, 0, 0);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회합니다.
     *
     * @param id 조회할 사용자 ID
     * @return 해당 사용자의 포인트 충전/이용 내역 리스트. 각 항목은 {@link PointHistory} 객체입니다.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return List.of();
    }

    /**
     * 특정 유저의 포인트를 충전합니다.
     *
     * 요청 바디로 충전할 금액(amount)을 전달받아 해당 유저의 포인트를 증가시키고,
     * 충전 내역을 {@link PointHistory}로 기록한 후 업데이트된 {@link UserPoint}를 반환합니다.
     *
     * @param id 조회할 사용자 ID
     * @param amount 충전할 포인트 금액 (양수)
     * @return 충전 후의 해당 사용자의 {@link UserPoint}
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new UserPoint(0, 0, 0);
    }

    /**
     * 특정 유저의 포인트를 사용합니다.
     *
     * 요청 바디로 사용 금액(amount)을 전달받아 해당 유저의 포인트를 감소시키고,
     * 사용 내역을 {@link PointHistory}로 기록한 후 업데이트된 {@link UserPoint}를 반환합니다.
     * 잔고가 부족할 경우, 포인트 사용은 실패합니다.
     *
     * @param id 조회할 사용자 ID
     * @param amount 사용할 포인트 금액 (양수)
     * @return 사용 후의 해당 사용자의 {@link UserPoint}
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {        
        return new UserPoint(0, 0, 0);
    }
}