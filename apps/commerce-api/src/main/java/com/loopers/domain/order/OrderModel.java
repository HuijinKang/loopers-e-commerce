package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderModel extends BaseEntity {

    private Long userId;
    private Long totalAmount;
    private String shippingAddress;
    private OrderStatus orderStatus;

    public OrderModel(Long userId, Long totalAmount, String shippingAddress, OrderStatus orderStatus) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
    }

    public static OrderModel of(Long userId, Long totalAmount, String shippingAddress, OrderStatus orderStatus) {
        return new OrderModel(userId, totalAmount, shippingAddress, orderStatus);
    }

    public void updateOrderStatus(OrderStatus newStatus) {
        this.orderStatus = newStatus;
    }
}
