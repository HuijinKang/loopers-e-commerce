package com.loopers.domain.like;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.UserModel;

import java.util.Optional;

public interface LikeRepository {
    Optional<LikeModel> findByUserIdAndProductId(Long user, Long product);
    boolean existsByUserAndProduct(UserModel user, ProductModel product);
    void save(LikeModel like);
    void delete(LikeModel like);
}
