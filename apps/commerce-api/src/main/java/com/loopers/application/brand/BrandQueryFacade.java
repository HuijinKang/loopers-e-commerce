package com.loopers.application.brand;

import com.loopers.domain.brand.BrandDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class BrandQueryFacade {

    private final BrandDomainService brandDomainService;

    @Transactional(readOnly = true)
    public BrandInfo getBrand(Long brandId) {
        return BrandInfo.from(brandDomainService.getBrand(brandId));
    }
}
