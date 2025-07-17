package com.loopers.domain.point;

import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long chargePoint(String userId, Long amount) {

        if (amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "충전 금액은 0보다 커야 합니다.");
        }

        UserModel user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "유저를 찾을 수 없습니다."));

        PointModel point = pointRepository.findByUser(user.getId())
                .orElseGet(() -> {
                    PointModel newPoint = PointModel.of(user, 0L);
                    pointRepository.save(newPoint);
                    return newPoint;
                });

        point.charge(amount);

        return point.getAmount();
    }

    @Transactional(readOnly = true)
    public Optional<PointModel> getPoint(Long userId) {
        return pointRepository.findByUser(userId);
    }
}
