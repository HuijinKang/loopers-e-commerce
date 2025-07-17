package com.loopers.domain.point;

import java.util.Optional;

public interface PointRepository {
    Optional<PointModel> findByUser(Long userId);

    void save(PointModel point);
}
