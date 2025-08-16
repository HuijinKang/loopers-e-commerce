package com.loopers.application.product;

import com.loopers.application.product.cache.ProductCacheKey;
import com.loopers.application.product.cache.ProductCacheService;
import com.loopers.domain.product.ProductDomainService;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.ProductStatus;
import com.loopers.interfaces.api.product.ProductV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductQueryFacade {

    private final ProductDomainService productDomainService;
    private final ProductCacheService productCacheService;

    @Transactional(readOnly = true)
    public List<ProductV1Dto.ProductSummaryResponse> getProducts(int page, int size, ProductSortType sortType, ProductStatus status, Long brandId) {
        String key = ProductCacheKey.list(page, size, sortType, status, brandId);
        return productCacheService.getProductList(
                key,
                () -> productDomainService.getProducts(page, size, sortType, status, brandId)
                        .stream()
                        .map(ProductV1Dto.ProductSummaryResponse::from)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public ProductV1Dto.ProductSummaryResponse getProduct(Long productId) {
        String key = ProductCacheKey.detail(productId);
        return productCacheService.getProduct(
                key,
                () -> ProductV1Dto.ProductSummaryResponse.from(productDomainService.getProduct(productId))
        );
    }
}
