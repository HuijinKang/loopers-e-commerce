package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "issued_coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuedCouponModel extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponModel coupon;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private IssuedCouponStatus status;

    private LocalDateTime usedAt;

    @Version
    private Long version;

    public long apply(long orderAmount) {
        return coupon.calculateDiscount(orderAmount);
    }

    public void markUsed(LocalDateTime now) {
        if (this.status == IssuedCouponStatus.USED) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용된 쿠폰입니다.");
        }
        this.status = IssuedCouponStatus.USED;
        this.usedAt = now;
    }

    public void validateUsable() {
        if (this.status != IssuedCouponStatus.ISSUED) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰 상태입니다.");
        }
        if (!coupon.isActive()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "비활성화된 쿠폰입니다.");
        }
    }
}
