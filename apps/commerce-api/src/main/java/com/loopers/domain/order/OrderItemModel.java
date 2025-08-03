package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItemModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderModel order;
    private Long productId;
    @Embedded
    private Option option;
    private int quantity;
    private Long price;

    public OrderItemModel(OrderModel order, Long productId, Option option, int quantity, Long price) {
        this.order = order;
        this.productId = productId;
        this.option = option;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemModel of(OrderModel order, Long productId, Option option, int quantity, Long price) {
        return new OrderItemModel(order, productId, option, quantity, price);
    }

    public Long calculatePrice() {
        return price;
    }

}
