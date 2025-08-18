package com.loopers.domain.like;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeDomainService {

    private final LikeRepository likeRepository;

    @Transactional
    public void toggleLike(UserModel user, ProductModel product) {
        Optional<LikeModel> existing = likeRepository.findByUserIdAndProductId(user.getId(), product.getId());

        if (existing.isPresent()) {
            likeRepository.delete(existing.get());
            product.decreaseLikeCount();
        } else {
            likeRepository.save(LikeModel.of(user.getId(), product.getId()));
            product.increaseLikeCount();
        }
    }

    public boolean isLiked(Long userId, Long productId) {
        return likeRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional(readOnly = true)
    public List<Long> getLikedProductIds(Long userId) {
        return likeRepository.findByUserId(userId).stream()
                .map(LikeModel::getProductId)
                .toList();
    }
}

