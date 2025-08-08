package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.CouponModel;
import com.loopers.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    @Override
    public Optional<CouponModel> findById(Long id) {
        return couponJpaRepository.findById(id);
    }

    @Override
    public Optional<CouponModel> findByCodeAndActiveTrue(String code) {
        return couponJpaRepository.findByCodeAndActiveTrue(code);
    }
}
