package com.loopers.application.like;

import com.loopers.application.product.cache.ProductCacheKey;
import com.loopers.application.product.cache.ProductCachePort;
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
    private final ProductCachePort productCachePort;

    @Transactional
    public void toggleLike(String email, Long productId) {
        UserModel user = userDomainService.getUser(email);
        ProductModel product = productDomainService.getProductForUpdate(productId);

        likeDomainService.toggleLike(user, product);
        productCachePort.evict(ProductCacheKey.detail(productId));
    }
}
