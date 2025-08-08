package com.loopers.domain.coupon;

import java.util.Optional;

public interface CouponRepository {
    Optional<CouponModel> findById(Long id);
    Optional<CouponModel> findByCodeAndActiveTrue(String code);
}
