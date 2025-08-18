package com.loopers.application.point;

import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.user.UserDomainService;
import com.loopers.domain.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointQueryFacade {

    private final PointDomainService pointDomainService;
    private final UserDomainService userDomainService;

    @Transactional(readOnly = true)
    public PointInfo getPointInfo(String email) {
        UserModel user = userDomainService.getUser(email);
        PointModel point = pointDomainService.getPoint(user.getId());
        return PointInfo.from(point);
    }
}
