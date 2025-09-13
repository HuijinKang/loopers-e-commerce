package com.loopers.application.coupon.event;

import com.loopers.domain.coupon.IssuedCouponDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCouponUseEventListener {

    private final IssuedCouponDomainService issuedCouponDomainService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderCouponUseEvent event) {
        try {
            issuedCouponDomainService.applyAndUseWithLock(event.issuedCouponId(), event.userId(), event.orderAmount());
        } catch (Exception ex) {
            log.warn("Coupon apply failed for orderNo={} couponId={}", event.orderNo(), event.issuedCouponId(), ex);
        }
    }
}
