package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.product.ProductModel;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductModel product;

    @Embedded
    private Option option;

    private Long quantity;

    private Long price;

    public OrderItemModel(OrderModel order, ProductModel product, Option option, Long quantity, Long price) {
        this.order = order;
        this.product = product;
        this.option = option;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemModel of(OrderModel order, ProductModel product, Option option, Long quantity, Long price) {
        return new OrderItemModel(order, product, option, quantity, price);
    }

    // TODO: 도메인 로직
}
