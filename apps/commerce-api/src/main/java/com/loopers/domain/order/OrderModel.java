package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.domain.user.UserModel;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 - [ ]  주문은 여러 상품을 포함할 수 있으며, 각 상품의 수량을 명시한다
 - [ ]  주문 시 상품의 재고 차감, 유저 포인트 차감 등을 수행한다
 - [ ]  재고 부족, 포인트 부족 등 예외 흐름을 고려해 설계되었다
 - [ ]  단위 테스트에서 정상 주문 / 예외 주문 흐름을 모두 검증했다
 */

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderModel extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    private Long totalAmount;

    private String shippingAddress;

    private OrderStatus orderStatus;

    public OrderModel(UserModel user, Long totalAmount, String shippingAddress, OrderStatus orderStatus) {
        this.user = user;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.orderStatus = orderStatus;
    }

    public static OrderModel of(UserModel user, Long totalAmount, String shippingAddress, OrderStatus orderStatus) {
        return new OrderModel(user, totalAmount, shippingAddress, orderStatus);
    }

    // TODO: 도메인 로직

}
