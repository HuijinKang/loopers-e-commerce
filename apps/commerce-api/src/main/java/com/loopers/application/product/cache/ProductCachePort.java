package com.loopers.application.product.cache;

import com.loopers.interfaces.api.product.ProductV1Dto;

import java.util.List;
import java.util.function.Supplier;

public interface ProductCachePort {
    ProductV1Dto.ProductSummaryResponse getProduct(String key, Supplier<ProductV1Dto.ProductSummaryResponse> loader);
    List<ProductV1Dto.ProductSummaryResponse> getProductList(String key, Supplier<List<ProductV1Dto.ProductSummaryResponse>> loader);
    void evict(String key);
}
