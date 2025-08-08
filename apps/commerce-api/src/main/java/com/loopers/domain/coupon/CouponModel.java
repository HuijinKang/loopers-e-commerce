package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponModel extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    private Long amount;

    private Integer percent;

    private boolean active = true;

    public long calculateDiscount(long orderAmount) {
        if (type == CouponType.FIXED) {
            return Math.max(orderAmount - amount, 0);
        } else if (type == CouponType.PERCENT) {
            return Math.max(orderAmount * (100 - percent) / 100, 0);
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 쿠폰 유형입니다.");
    }
}
