package com.loopers.domain.like;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeDomainService {

    private final LikeRepository likeRepository;

    public void toggleLike(UserModel user, ProductModel product) {
        Optional<LikeModel> existing = likeRepository.findByUserAndProduct(user, product);

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            product.decreaseLikeCount();
        } else {
            likeRepository.save(LikeModel.of(user, product));
            product.increaseLikeCount();
        }
    }

    public boolean isLiked(UserModel user, ProductModel product) {
        return likeRepository.existsByUserAndProduct(user, product);
    }
}

