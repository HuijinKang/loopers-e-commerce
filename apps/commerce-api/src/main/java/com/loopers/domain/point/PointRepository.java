package com.loopers.domain.point;

import java.util.Optional;

public interface PointRepository {
    Optional<PointModel> findByUser(Long userId);

    PointModel save(PointModel point);
}
