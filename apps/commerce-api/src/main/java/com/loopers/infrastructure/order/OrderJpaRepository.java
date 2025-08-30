package com.loopers.infrastructure.order;

import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderModel, Long> {
    List<OrderModel> findByUserId(Long userId);
    long countByUserId(Long userId);
    Optional<OrderModel> findByOrderNo(String orderNo);
    @Query("select o from OrderModel o where o.orderStatus = :status and o.updatedAt < :updatedBefore")
    List<OrderModel> findPendingUpdatedBefore(@Param("status") OrderStatus status, @Param("updatedBefore") java.time.ZonedDateTime updatedBefore);
}
