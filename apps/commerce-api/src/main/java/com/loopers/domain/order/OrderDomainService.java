package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final OrderRepository orderRepository;

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
}
