package com.loopers.infrastructure.point;

import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;

    @Override
    public Optional<PointModel> findByUser(Long userId) {
        return pointJpaRepository.findByUserId(userId);
    }

    @Override
    public PointModel save(PointModel point) {
        return pointJpaRepository.save(point);
    }
}
