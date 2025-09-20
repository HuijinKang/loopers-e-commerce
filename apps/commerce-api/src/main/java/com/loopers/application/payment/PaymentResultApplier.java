package com.loopers.application.payment;

import com.loopers.application.payment.dto.PgPaymentResult;
import com.loopers.domain.order.OrderDomainService;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.order.OrderItemModel;
import com.loopers.application.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentResultApplier {

    private final OrderDomainService orderDomainService;
    private final RankingService rankingService;

    @Transactional
    public void apply(String orderNo, PgPaymentResult.Status status) {
        OrderModel order = orderDomainService.getOrderByOrderNo(orderNo);
        if (order.getOrderStatus() != OrderStatus.PENDING) return;
        if (status == null || status == PgPaymentResult.Status.PENDING) return;
        if (status == PgPaymentResult.Status.SUCCESS) {
            order.process();
            for (OrderItemModel item : orderDomainService.getOrderItems(order.getId())) {
                for (int i = 0; i < Math.max(1, item.getQuantity()); i++) {
                    rankingService.onOrderConfirmed(item.getProductId());
                }
            }
        } else if (status == PgPaymentResult.Status.FAILED) {
            order.cancel();
        }
    }
}
