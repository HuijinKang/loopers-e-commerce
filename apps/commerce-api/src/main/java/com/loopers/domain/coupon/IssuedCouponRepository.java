package com.loopers.domain.coupon;

import java.util.Optional;

public interface IssuedCouponRepository {
    Optional<IssuedCouponModel> findById(Long id);
    Optional<IssuedCouponModel> findByIdAndUserId(Long id, Long userId);
    Optional<IssuedCouponModel> findByIdForUpdate(Long id); // 비관적 락용
    IssuedCouponModel save(IssuedCouponModel entity);
}
