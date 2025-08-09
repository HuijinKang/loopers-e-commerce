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
    
    private CouponModel(String code, String description, CouponType type, Long amount, Integer percent) {
        this.code = code;
        this.description = description;
        this.type = type;
        this.amount = amount;
        this.percent = percent;
    }

    public static CouponModel of(String code, String description, CouponType type, Long amount, Integer percent) {
        return new CouponModel(code, description, type, amount, percent);
    }

    public static CouponModel ofFixed(String code, String description, Long amount) {
        return new CouponModel(code, description, CouponType.FIXED, amount, null);
    }

    public static CouponModel ofPercent(String code, String description, Integer percent) {
        return new CouponModel(code, description, CouponType.PERCENT, null, percent);
    }

    public long calculateDiscount(long orderAmount) {
        if (type == CouponType.FIXED) {
            if (amount == null || amount <= 0) {
                throw new CoreException(ErrorType.BAD_REQUEST, "정액 쿠폰 금액이 올바르지 않습니다.");
            }
            return Math.max(orderAmount - amount, 0);
        } else if (type == CouponType.PERCENT) {
            if (percent == null || percent <= 0 || percent > 100) {
                throw new CoreException(ErrorType.BAD_REQUEST, "정률 쿠폰 비율이 올바르지 않습니다.");
            }
            return Math.max(orderAmount * (100 - percent) / 100, 0);
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "잘못된 쿠폰 유형입니다.");
    }
}
