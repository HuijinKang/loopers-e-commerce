package com.loopers.application.like;

import com.loopers.application.product.cache.ProductCacheKey;
import com.loopers.application.product.cache.ProductCachePort;
import com.loopers.domain.like.LikeDomainService;
import com.loopers.domain.product.ProductDomainService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import com.loopers.application.like.event.ProductLikedEvent;
import com.loopers.application.audit.UserActionEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeDomainService likeDomainService;
    private final UserDomainService userDomainService;
    private final ProductDomainService productDomainService;
    private final ProductCachePort productCachePort;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void toggleLike(String email, Long productId) {
        UserModel user = userDomainService.getUser(email);
        ProductModel product = productDomainService.getProductForUpdate(productId);

        likeDomainService.toggleLike(user, product);
        productCachePort.evict(ProductCacheKey.detail(productId));
        eventPublisher.publishEvent(ProductLikedEvent.of(user.getId(), productId, true));
        eventPublisher.publishEvent(UserActionEvent.of("PRODUCT_LIKE_TOGGLED", user.getEmail(), String.valueOf(productId), "liked=true"));
    }
}
