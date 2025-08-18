package com.loopers.infrastructure.coupon;

import com.loopers.domain.coupon.IssuedCouponModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IssuedCouponJpaRepository extends JpaRepository<IssuedCouponModel, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ic from IssuedCouponModel ic join fetch ic.coupon where ic.id = :id")
    Optional<IssuedCouponModel> findByIdForUpdate(Long id);

    Optional<IssuedCouponModel> findByIdAndUserId(Long id, Long userId);
}
