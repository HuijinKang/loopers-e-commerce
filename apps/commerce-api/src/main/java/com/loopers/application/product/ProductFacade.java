package com.loopers.application.product;

import com.loopers.domain.product.ProductDomainService;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.ProductStatus;
import com.loopers.interfaces.api.product.ProductV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductDomainService productDomainService;

    public List<ProductV1Dto.ProductSummaryResponse> getProducts(int page, int size, ProductSortType sortType, ProductStatus status) {
        return productDomainService.getProducts(page, size, sortType, status)
                .stream()
                .map(ProductV1Dto.ProductSummaryResponse::from)
                .toList();
    }
}
