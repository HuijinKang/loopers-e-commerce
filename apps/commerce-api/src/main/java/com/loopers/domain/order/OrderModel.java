package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderModel extends BaseEntity {

    private String orderNo;

    private Long userId;

    private Long totalAmount;

    private Long discountedAmount;

    private Long usedCouponId;

    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public OrderModel(String orderNo, Long userId, Long totalAmount, Long discountedAmount, Long usedCouponId, String shippingAddress, OrderStatus orderStatus) {
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.discountedAmount = discountedAmount;
        this.usedCouponId = usedCouponId;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
    }

    public static OrderModel of(String orderNo, Long userId, Long totalAmount, Long discountedAmount, Long usedCouponId, String shippingAddress, OrderStatus orderStatus) {
        return new OrderModel(orderNo, userId, totalAmount, discountedAmount, usedCouponId, shippingAddress, orderStatus);
    }

    public void process() {
        this.orderStatus = OrderStatus.PROCESSING;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELED;
    }
}
