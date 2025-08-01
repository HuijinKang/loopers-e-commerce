package com.loopers.infrastructure.like;

import com.loopers.domain.like.LikeModel;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;


    @Override
    public Optional<LikeModel> findByUserIdAndProductId(Long userId, Long productId) {
        return likeJpaRepository.findByUserIdAndProductId(userId, productId);
    }

    @Override
    public boolean existsByUserAndProduct(UserModel user, ProductModel product) {
        return likeJpaRepository.existsByUserAndProduct(user, product);
    }

    @Override
    public void save(LikeModel like) {
        likeJpaRepository.save(like);
    }

    @Override
    public void delete(LikeModel like) {
        likeJpaRepository.delete(like);
    }
}

