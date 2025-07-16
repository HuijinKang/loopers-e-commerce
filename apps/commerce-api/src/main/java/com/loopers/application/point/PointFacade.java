package com.loopers.application.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointService;
import com.loopers.interfaces.api.point.PointV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final PointService pointService;

    public void chargePoint(String userId, PointV1Dto.ChargeRequest request) {
        pointService.chargePoint(userId, request.amount());
    }

    public PointInfo getPoint(String userId) {
        PointModel pointModel = pointService.getPoint(userId);
        return PointInfo.from(pointModel);
    }
}
