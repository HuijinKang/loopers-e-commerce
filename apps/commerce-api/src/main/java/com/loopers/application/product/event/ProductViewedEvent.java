package com.loopers.application.product.event;

public record ProductViewedEvent(Long productId) {
    public static ProductViewedEvent of(Long productId) { return new ProductViewedEvent(productId); }
}
