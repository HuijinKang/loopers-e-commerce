package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.IssuedCouponModel;
import com.loopers.domain.coupon.IssuedCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IssuedCouponRepositoryImpl implements IssuedCouponRepository {

    private final IssuedCouponJpaRepository issuedCouponJpaRepository;

    @Override
    public Optional<IssuedCouponModel> findById(Long id) {
        return issuedCouponJpaRepository.findById(id);
    }

    @Override
    public Optional<IssuedCouponModel> findByIdAndUserId(Long id, Long userId) {
        return issuedCouponJpaRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public Optional<IssuedCouponModel> findByIdForUpdate(Long id) {
        return issuedCouponJpaRepository.findByIdForUpdate(id);
    }

    @Override
    public IssuedCouponModel save(IssuedCouponModel issuedCouponModel) {
        return issuedCouponJpaRepository.save(issuedCouponModel);
    }
}
