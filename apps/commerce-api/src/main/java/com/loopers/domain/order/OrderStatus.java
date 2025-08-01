package com.loopers.domain.order;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PENDING("주문 대기"),
    PROCESSING("주문 처리 중"),
    SHIPPED("배송 중"),
    DELIVERED("배송 완료"),
    CANCELED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

}
