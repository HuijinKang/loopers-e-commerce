package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductQueryFacade;
import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.ProductStatus;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductV1Controller implements ProductV1ApiSpec {

    private final ProductQueryFacade productQueryFacade;

    @Override
    @GetMapping
    public ApiResponse<List<ProductV1Dto.ProductSummaryResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") ProductSortType sortType,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) Long brandId
    ) {
        return ApiResponse.success(
                productQueryFacade.getProducts(page, size, sortType, status, brandId)
        );
    }

    @Override
    @GetMapping("/{productId}")
    public ApiResponse<ProductV1Dto.ProductSummaryResponse> getProduct(@PathVariable Long productId) {
        return ApiResponse.success(productQueryFacade.getProduct(productId));
    }
}
