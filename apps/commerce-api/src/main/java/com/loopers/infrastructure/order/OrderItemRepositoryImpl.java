package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public void save(OrderItemModel orderItem) {
        orderItemJpaRepository.save(orderItem);
    }

    @Override
    public List<OrderItemModel> saveAll(List<OrderItemModel> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }

    @Override
    public List<OrderItemModel> findByOrderId(Long orderId) {
        return orderItemJpaRepository.findByOrderId(orderId);
    }
}
