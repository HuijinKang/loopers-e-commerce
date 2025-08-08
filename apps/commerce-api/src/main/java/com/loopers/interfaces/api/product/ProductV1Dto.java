package com.loopers.interfaces.api.product;

import com.loopers.domain.product.ProductModel;

public class ProductV1Dto{

    public record ProductSummaryResponse(
            Long id,
            String name,
            Long price,
            int likeCount
    ) {
        public static ProductSummaryResponse from(ProductModel model) {
            return new ProductSummaryResponse(
                    model.getId(),
                    model.getName(),
                    model.getPrice(),
                    model.getLikeCount()
            );
        }
    }
}



