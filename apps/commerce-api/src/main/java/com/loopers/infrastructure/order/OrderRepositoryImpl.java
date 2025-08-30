package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderRepository;
import com.loopers.domain.order.OrderStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public OrderModel save(OrderModel order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<OrderModel> findById(Long id) {
        return orderJpaRepository.findById(id);
    }

    @Override
    public List<OrderModel> findByUserId(Long userId) {
        return orderJpaRepository.findByUserId(userId);
    }

    @Override
    public long countByUserId(Long userId) {
        return orderJpaRepository.countByUserId(userId);
    }

    @Override
    public Optional<OrderModel> findByOrderNo(String orderNo) {
        return orderJpaRepository.findByOrderNo(orderNo);
    }

    @Override
    public List<OrderModel> findPendingOrdersUpdatedBefore(ZonedDateTime updatedBefore) {
        return orderJpaRepository.findPendingUpdatedBefore(OrderStatus.PENDING, updatedBefore);
    }
}
