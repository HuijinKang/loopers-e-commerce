package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final OrderRepository orderRepository;

    public OrderModel createOrder(Long userId, List<OrderItemModel> orderItems, String shippingAddress) {

        Long totalAmount = orderItems.stream()
                .mapToLong(OrderItemModel::calculatePrice)
                .sum();

        OrderModel order = OrderModel.of(userId, totalAmount, shippingAddress, OrderStatus.PENDING);

        return orderRepository.save(order);
    }
}
