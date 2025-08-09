package com.loopers.application.brand;

import com.loopers.domain.brand.BrandDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BrandFacade {

    private final BrandDomainService brandDomainService;

    public BrandInfo getBrand(Long brandId) {
        return BrandInfo.from(brandDomainService.getBrand(brandId));
    }
}


