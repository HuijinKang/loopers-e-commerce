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

    public PointInfo chargePoint(String userId, PointV1Dto.ChargeRequest request) {
        UserModel user = userService.findUser(userId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );
        PointModel point = pointService.chargePoint(user, request.amount());

        return PointInfo.from(point);
    }

    public PointInfo getPointInfo(String userId) {
        UserModel user = userService.findUser(userId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "유저를 찾을 수 없습니다.")
        );
        PointModel point = pointService.findPoint(user.getId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "포인트 정보를 찾을 수 없습니다.")
        );
        return PointInfo.from(point);
    }
}
