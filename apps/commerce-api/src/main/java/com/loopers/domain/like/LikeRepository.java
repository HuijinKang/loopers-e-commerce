package com.loopers.domain.like;

import java.util.Optional;

public interface LikeRepository {
    Optional<LikeModel> findByUserIdAndProductId(Long userId, Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    void save(LikeModel like);
    void delete(LikeModel like);
}
