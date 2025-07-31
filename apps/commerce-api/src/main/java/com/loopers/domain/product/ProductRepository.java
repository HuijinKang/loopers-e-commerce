package com.loopers.domain.product;

import java.util.Optional;

public interface ProductRepository {
    Optional<ProductModel> findById(Long id);
    ProductModel save(ProductModel product);
}
