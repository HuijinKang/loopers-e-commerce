package com.loopers.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<ProductModel> findById(Long id);
    List<ProductModel> search(int page, int size, ProductSortType sortType, ProductStatus status);
    ProductModel save(ProductModel product);
}
