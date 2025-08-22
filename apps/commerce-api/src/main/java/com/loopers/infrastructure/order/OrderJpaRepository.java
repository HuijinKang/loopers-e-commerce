package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderModel, Long> {
    List<OrderModel> findByUserId(Long userId);
    long countByUserId(Long userId);
    Optional<OrderModel> findByOrderNo(String orderNo);
}
