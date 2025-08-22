package com.loopers.application.order;

import com.loopers.domain.coupon.IssuedCouponDomainService;
import com.loopers.domain.order.*;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.product.ProductDomainService;
import com.loopers.domain.user.UserDomainService;
import com.loopers.domain.user.UserModel;
import com.loopers.application.payment.PgPaymentPort;
import com.loopers.application.payment.dto.PgPaymentCommand;
import com.loopers.interfaces.api.order.OrderV1Dto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.CompletableFuture;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderDomainService orderDomainService;
    private final OrderItemDomainService orderItemDomainService;
    private final IssuedCouponDomainService issuedCouponDomainService;
    private final ProductDomainService productDomainService;
    private final PointDomainService pointDomainService;
    private final UserDomainService userDomainService;
    private final PgPaymentPort pgPaymentPort;

    @Transactional
    public Long placeOrder(String userId, OrderV1Dto.CreateOrderRequest request) {
        UserModel user = userDomainService.getUser(userId);
        String orderNo = "ORD-" + System.currentTimeMillis();
        List<OrderV1Dto.CreateOrderCommand.OrderItemCommand> items = request.items().stream()
                .map(i -> new OrderV1Dto.CreateOrderCommand.OrderItemCommand(i.productId(), i.option(), i.quantity(), i.price()))
                .toList();
        OrderV1Dto.CreateOrderCommand command = new OrderV1Dto.CreateOrderCommand(
                user.getId(),
                orderNo,
                request.shippingAddress(),
                request.issuedCouponId(),
                request.usePoint(),
                items
        );
        Long orderId = placeOrder(command);
        // 트랜잭션 밖 비동기 결제 요청: 저장된 주문의 할인 금액 사용
        CompletableFuture.runAsync(() -> requestPgPaymentWithPersistedAmount(user.getId(), orderNo, request));
        return orderId;
    }

    @Transactional
    public Long placeOrder(OrderV1Dto.CreateOrderCommand command) {
        // 1. 총 주문 금액 계산
        long totalAmount = command.items().stream()
                .mapToLong(item -> item.price() * item.quantity())
                .sum();

        // 2. 쿠폰 할인 적용
        long discountedAmount = totalAmount;
        if (command.issuedCouponId() != null) {
            discountedAmount = issuedCouponDomainService.applyAndUseWithLock(
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
        // 재고 차감은 락 기반으로 처리
        productDomainService.deductStock(orderItems);

        // 6. 포인트 차감 (선차감 방식 유지)
        if (command.usePoint() > 0) {
            pointDomainService.deductPoint(command.userId(), command.usePoint());
        }

        return order.getId();
    }

    private void requestPgPaymentWithPersistedAmount(Long userId, String orderNo, OrderV1Dto.CreateOrderRequest req) {
        OrderModel saved = orderDomainService.getOrderByOrderNo(orderNo);
        long discounted = saved.getDiscountedAmount();
        PgPaymentCommand.CreateTransaction pgCommand = new PgPaymentCommand.CreateTransaction(
                String.valueOf(userId),
                orderNo,
                req.payment().cardType(),
                req.payment().cardNo(),
                discounted,
                "http://localhost:8080/api/v1/payments/callback"
        );
        try {
            pgPaymentPort.requestPayment(pgCommand);
        } catch (Exception ignored) {
        }
    }
}
