package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductModel;
import com.loopers.domain.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ProductJpaRepository extends JpaRepository<ProductModel, Long> {
    Page<ProductModel> findByStatus(ProductStatus status, Pageable pageable);
    Page<ProductModel> findByBrandId(Long brandId, Pageable pageable);
    Page<ProductModel> findByBrandIdAndStatus(Long brandId, ProductStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductModel p where p.id = :id")
    Optional<ProductModel> findByIdForUpdate(@Param("id") Long id);
}
