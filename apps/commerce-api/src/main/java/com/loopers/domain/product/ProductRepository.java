package com.loopers.domain.product;

import java.util.Optional;

public interface ProductRepository {
    Optional<ProductModel> findById(Long id);
    void save(ProductModel product);
}
