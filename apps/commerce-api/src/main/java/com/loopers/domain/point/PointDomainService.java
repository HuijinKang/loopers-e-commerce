package com.loopers.domain.point;

import com.loopers.domain.user.UserModel;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointDomainService {

    private final PointRepository pointRepository;

    @Transactional
    public PointModel chargePoint(UserModel user, Long amount) {

        PointModel point = pointRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    PointModel newPoint = PointModel.of(user.getId(), 0L);
                    return pointRepository.save(newPoint);
                });

        point.charge(amount);

        return point;
    }

    @Transactional
    public void deductPoint(Long userId, Long amount) {
        PointModel point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "포인트 정보 없음"));

        point.deduct(amount);
    }


    @Transactional(readOnly = true)
    public PointModel getPoint(Long userId) {
        return pointRepository.findByUserId(userId).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND, "포인트 정보를 찾을 수 없습니다.")
        );
    }
}
