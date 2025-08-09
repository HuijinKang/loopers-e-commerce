package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderModel create(
            String orderNo,
            Long userId,
            String shippingAddress,
            Long issuedCouponId,
            long totalAmount,
            long discountedAmount
    ) {
        OrderModel order = OrderModel.of(
                orderNo,
                userId,
                totalAmount,
                discountedAmount,
                issuedCouponId,
                shippingAddress,
                OrderStatus.PENDING
        );
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public OrderModel getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow();
    }

    @Transactional(readOnly = true)
    public java.util.List<OrderItemModel> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public java.util.List<OrderModel> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
