package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

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
    public OrderModel getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<OrderItemModel> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<OrderModel> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<OrderModel> findPendingOrdersUpdatedBefore(ZonedDateTime updatedBefore) {
        return orderRepository.findPendingOrdersUpdatedBefore(updatedBefore);
    }
}
