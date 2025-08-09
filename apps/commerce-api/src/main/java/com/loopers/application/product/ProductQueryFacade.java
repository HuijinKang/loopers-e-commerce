package com.loopers.application.product;

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

    @Transactional(readOnly = true)
    public List<ProductV1Dto.ProductSummaryResponse> getProducts(int page, int size, ProductSortType sortType, ProductStatus status) {
        return productDomainService.getProducts(page, size, sortType, status)
                .stream()
                .map(ProductV1Dto.ProductSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductV1Dto.ProductSummaryResponse getProduct(Long productId) {
        return ProductV1Dto.ProductSummaryResponse.from(productDomainService.getProduct(productId));
    }
}
