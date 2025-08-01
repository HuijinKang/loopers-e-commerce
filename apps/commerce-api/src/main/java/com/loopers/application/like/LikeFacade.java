package com.loopers.application.like;

import com.loopers.domain.like.LikeDomainService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
// import application 들어가면 안됨
@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeDomainService likeDomainService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void toggleLike(String userId, Long productId) {
        UserModel user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        likeDomainService.toggleLike(user, product);
    }

    @Transactional(readOnly = true)
    public boolean isLiked(String userId, Long productId) {
        UserModel user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        ProductModel product = productRepository.findById(productId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));

        return likeDomainService.isLiked(user, product);
    }
}
