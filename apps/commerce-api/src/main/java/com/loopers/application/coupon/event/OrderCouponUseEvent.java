package com.loopers.application.coupon.event;

public record OrderCouponUseEvent(
    String orderNo, 
    Long issuedCouponId, 
    Long userId, 
    long orderAmount
) {
    public static OrderCouponUseEvent of(String orderNo, Long issuedCouponId, Long userId, long orderAmount) {
        return new OrderCouponUseEvent(orderNo, issuedCouponId, userId, orderAmount);
    }
}
