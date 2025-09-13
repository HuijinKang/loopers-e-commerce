package com.loopers.application.like.event;

public record ProductLikedEvent(
        Long userId,
        Long productId,
        boolean liked
) {
    public static ProductLikedEvent of(Long userId, Long productId, boolean liked) {
        return new ProductLikedEvent(userId, productId, liked);
    }
}
