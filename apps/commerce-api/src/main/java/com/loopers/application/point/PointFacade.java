package com.loopers.application.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserDomainService;
import com.loopers.interfaces.api.point.PointV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final PointDomainService pointDomainService;
    private final UserDomainService userDomainService;

    @Transactional
    public PointInfo chargePoint(String userId, PointV1Dto.ChargeRequest request) {
        UserModel user = userDomainService.getUser(userId);
        PointModel point = pointDomainService.chargePoint(user, request.amount());

        return PointInfo.from(point);
    }
}
