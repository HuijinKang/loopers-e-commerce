package com.loopers.application.order;

import com.loopers.domain.coupon.IssuedCouponDomainService;
import com.loopers.domain.order.*;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.product.ProductDomainService;
import com.loopers.interfaces.api.order.OrderV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderDomainService orderDomainService;
    private final OrderItemDomainService orderItemDomainService;
    private final IssuedCouponDomainService issuedCouponDomainService;
    private final ProductDomainService productDomainService;
    private final PointDomainService pointDomainService;

    @Transactional
    public Long placeOrder(OrderV1Dto.CreateOrderCommand command) {
        // 1. 총 주문 금액 계산
        long totalAmount = command.items().stream()
                .mapToLong(item -> item.price() * item.quantity())
                .sum();

        // 2. 쿠폰 할인 적용
        long discountedAmount = totalAmount;
        if (command.issuedCouponId() != null) {
            discountedAmount = issuedCouponDomainService.applyAndUse(
                    command.issuedCouponId(),
                    command.userId(),
                    totalAmount
            );
        }

        // 3. 주문 생성 및 저장
        OrderModel order = orderDomainService.create(
                command.orderNo(),
                command.userId(),
                command.shippingAddress(),
                command.issuedCouponId(),
                totalAmount,
                discountedAmount
        );

        // 4. 주문 아이템 생성 및 저장
        List<OrderItemModel> orderItems = orderItemDomainService.createItems(order, command.items());

        productDomainService.deductStock(orderItems);

        // 6. 포인트 차감
        if (command.usePoint() > 0) {
            pointDomainService.deductPoint(command.userId(), command.usePoint());
        }

        return order.getId();
    }
}
