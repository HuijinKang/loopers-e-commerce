package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class IssuedCouponDomainService {

    private final IssuedCouponRepository issuedCouponRepository;

    @Transactional
    public long applyAndUseWithLock(Long issuedCouponId, Long userId, long orderAmount) {
        IssuedCouponModel coupon = issuedCouponRepository.findByIdForUpdate(issuedCouponId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        if (!coupon.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.FORBIDDEN, "해당 쿠폰은 사용자에게 발급되지 않았습니다.");
        }

        coupon.validateUsable();
        long discounted = coupon.apply(orderAmount);
        coupon.markUsed(LocalDateTime.now());

        return discounted;
    }

    @Transactional(readOnly = true)
    public long previewDiscountAmount(Long issuedCouponId, Long userId, long orderAmount) {
        IssuedCouponModel coupon = issuedCouponRepository.findById(issuedCouponId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        if (!coupon.getUserId().equals(userId)) {
            throw new CoreException(ErrorType.FORBIDDEN, "해당 쿠폰은 사용자에게 발급되지 않았습니다.");
        }

        coupon.validateUsable();
        return coupon.apply(orderAmount);
    }
}
