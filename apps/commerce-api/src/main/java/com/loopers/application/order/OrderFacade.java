package com.loopers.application.order;

import com.loopers.application.order.event.OrderPlacedEvent;
import com.loopers.application.audit.UserActionEvent;
import com.loopers.domain.order.*;
import com.loopers.application.coupon.event.OrderCouponUseEvent;
import com.loopers.domain.point.PointDomainService;
import com.loopers.domain.product.ProductDomainService;
import com.loopers.domain.user.UserDomainService;
import com.loopers.domain.user.UserModel;
import com.loopers.application.payment.PgPaymentPort;
import com.loopers.application.payment.dto.PgPaymentCommand;
import com.loopers.interfaces.api.order.OrderV1Dto;
import com.loopers.application.payment.dto.PgPaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final OrderDomainService orderDomainService;
    private final OrderItemDomainService orderItemDomainService;
    private final ProductDomainService productDomainService;
    private final PointDomainService pointDomainService;
    private final UserDomainService userDomainService;
    private final PgPaymentPort pgPaymentPort;
    private final ApplicationEventPublisher eventPublisher;

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
        // AFTER_COMMIT 결제 요청 이벤트 발행
        eventPublisher.publishEvent(OrderPlacedEvent.of(
                user.getId(),
                orderNo,
                request.payment().cardType(),
                request.payment().cardNo()
        ));
        eventPublisher.publishEvent(UserActionEvent.of("ORDER_PLACED", user.getEmail(), orderNo, "items=" + items.size()));
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
            eventPublisher.publishEvent(OrderCouponUseEvent.of(command.orderNo(), command.issuedCouponId(), command.userId(), totalAmount));
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
            PgPaymentResult result = pgPaymentPort.requestPayment(pgCommand);
            // 요청 자체가 수락되지 않아 transactionKey가 없으면 즉시 취소 처리 (영구 PENDING 방지)
            if (result == null || result.getTransactionKey() == null) {
                try { orderDomainService.cancelIfPending(orderNo); } catch (Exception ignore) {}
            }
        } catch (Exception ex) {
            // 초기 결제 요청이 즉시 실패한 경우, 콜백으로 회복될 가능성이 낮다면 주문을 취소하여 영구 PENDING을 방지.
            try {
                orderDomainService.cancelIfPending(orderNo);
            } catch (Exception ignore) {}
        }
    }
}
