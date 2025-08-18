package com.loopers.application.product.cache;

import com.loopers.domain.product.ProductSortType;
import com.loopers.domain.product.ProductStatus;

public final class ProductCacheKey {

    private ProductCacheKey() {}

    public static String detail(Long productId) {
        return "product:detail:" + productId;
    }

    public static String list(Integer page, Integer size, ProductSortType sortType, ProductStatus status, Long brandId) {
        String brand = brandId == null ? "all" : brandId.toString();
        String stat = status == null ? "all" : status.name();
        String sort = sortType == null ? "latest" : sortType.name();
        return String.format("product:list:%s:%s:%s:%d:%d", brand, stat, sort, page, size);
    }
}


