package com.loopers.domain.brand;

import org.springframework.transaction.annotation.Transactional;

public interface BrandRepository {

    @Transactional
    BrandModel save(BrandModel brandModel);
}
