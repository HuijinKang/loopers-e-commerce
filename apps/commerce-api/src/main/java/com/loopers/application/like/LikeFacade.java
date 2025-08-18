package com.loopers.application.like;

import com.loopers.application.product.cache.ProductCacheKey;
import com.loopers.application.product.cache.ProductCacheService;
import com.loopers.domain.like.LikeDomainService;
import com.loopers.domain.product.ProductDomainService;
import com.loopers.domain.product.ProductModel;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final LikeDomainService likeDomainService;
    private final UserDomainService userDomainService;
    private final ProductDomainService productDomainService;
    private final ProductCacheService productCacheService;

    @Transactional
    public void toggleLike(String email, Long productId) {
        UserModel user = userDomainService.getUser(email);
        ProductModel product = productDomainService.getProductForUpdate(productId);

        likeDomainService.toggleLike(user, product);

        productCacheService.evict(ProductCacheKey.detail(productId));
    }
}
