package com.loopers.domain.point;

import com.loopers.domain.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;

    @Transactional
    public PointModel chargePoint(UserModel user, Long amount) {

        PointModel point = pointRepository.findByUser(user.getId())
                .orElseGet(() -> {
                    PointModel newPoint = PointModel.of(user, 0L);
                    pointRepository.save(newPoint);
                    return newPoint;
                });

        point.charge(amount);

        return point;
    }

    @Transactional(readOnly = true)
    public Optional<PointModel> getPoint(Long userId) {
        return pointRepository.findByUser(userId);
    }
}
