package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemDomainService {

    private final OrderItemRepository orderItemRepository;

    /**
     * 주문 항목을 생성하고 저장한다.
     */
    public void createOrderItem(OrderModel order, Long productId, Option option, int quantity, Long price) {
        OrderItemModel orderItem = OrderItemModel.of(order, productId, option, quantity, price);
        orderItemRepository.save(orderItem);
    }
}
