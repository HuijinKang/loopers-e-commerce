package com.loopers.domain.order;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    OrderModel save(OrderModel order);
    Optional<OrderModel> findById(Long id);
    List<OrderModel> findByUserId(Long userId);
    long countByUserId(Long userId);
    Optional<OrderModel> findByOrderNo(String orderNo);
    List<OrderModel> findPendingOrdersUpdatedBefore(ZonedDateTime updatedBefore);
}
