package com.loopers.interfaces.api.order;

import com.loopers.domain.order.Option;

import java.util.List;

public class OrderV1Dto {

    public record CreateOrderRequest(
            String shippingAddress,
            Long issuedCouponId, // nullable
            long usePoint,
            List<OrderItemRequest> items,
            PaymentRequest payment
    ) {
        public record PaymentRequest(
                String cardType,
                String cardNo
        ) {}
        public record OrderItemRequest(
                Long productId,
                Option option,
                int quantity,
                Long price
        ) {}
    }

    public record CreateOrderCommand(
            Long userId,
            String orderNo,
            String shippingAddress,
            Long issuedCouponId,
            long usePoint,
            List<OrderItemCommand> items
    ) {
        public record OrderItemCommand(
                Long productId,
                Option option,
                int quantity,
                Long price
        ) {}
    }

    public record OrderResponse(
            Long id,
            String orderNo,
            Long totalAmount,
            Long discountedAmount
    ) {
        public static OrderResponse from(com.loopers.application.order.OrderInfo info) {
            return new OrderResponse(info.id(), info.orderNo(), info.totalAmount(), info.discountedAmount());
        }
    }
}

