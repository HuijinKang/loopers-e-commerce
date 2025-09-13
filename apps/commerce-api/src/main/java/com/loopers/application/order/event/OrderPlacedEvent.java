package com.loopers.application.order.event;

public record OrderPlacedEvent(
        Long userId,
        String orderNo,
        String cardType,
        String cardNo
) {
    public static OrderPlacedEvent of(Long userId, String orderNo, String cardType, String cardNo) {
        return new OrderPlacedEvent(userId, orderNo, cardType, cardNo);
    }
}
