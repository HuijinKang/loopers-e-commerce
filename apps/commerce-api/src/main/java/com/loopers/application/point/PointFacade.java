package com.loopers.application.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointService;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;
    private final UserService userService;

    public Long chargePoint(String userId, PointV1Dto.ChargeRequest request) {
        return pointService.chargePoint(userId, request.amount());
    }

    public PointInfo getPointInfo(String userId) {
        UserModel user = userService.getUser(userId).orElseThrow(
                () -> new CoreException(ErrorType.BAD_REQUEST, "유저를 찾을 수 없습니다.")
        );
        PointModel point = pointService.getPoint(user.getId()).orElseThrow(
                () -> new CoreException(ErrorType.BAD_REQUEST, "포인트 정보를 찾을 수 없습니다.")
        );
        return PointInfo.from(point);
    }
}
