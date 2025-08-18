package com.loopers.application.order;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;

import java.util.List;

public record OrderInfo(
        Long id,
        String orderNo,
        Long userId,
        Long totalAmount,
        Long discountedAmount,
        Long usedCouponId,
        String shippingAddress,
        OrderStatus orderStatus,
        List<OrderItemInfo> items
) {
    public static OrderInfo of(OrderModel order, List<OrderItemModel> items) {
        return new OrderInfo(
                order.getId(),
                order.getOrderNo(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getDiscountedAmount(),
                order.getUsedCouponId(),
                order.getShippingAddress(),
                order.getOrderStatus(),
                items.stream().map(OrderItemInfo::from).toList()
        );
    }

    public record OrderItemInfo(Long productId, int quantity, Long price) {
        public static OrderItemInfo from(OrderItemModel m) {
            return new OrderItemInfo(m.getProductId(), m.getQuantity(), m.getPrice());
        }
    }
}
