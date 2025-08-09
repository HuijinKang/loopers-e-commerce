package com.loopers.domain.brand;

import java.util.Optional;

public interface BrandRepository {
    BrandModel save(BrandModel brandModel);
    Optional<BrandModel> findById(Long id);
}
