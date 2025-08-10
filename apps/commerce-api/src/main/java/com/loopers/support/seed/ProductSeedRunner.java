package com.loopers.support.seed;

import com.loopers.domain.brand.BrandModel;
import com.loopers.infrastructure.brand.BrandJpaRepository;
import com.loopers.domain.product.ProductModel;
import com.loopers.infrastructure.product.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Profile("seed")
@Component
@RequiredArgsConstructor
public class ProductSeedRunner implements CommandLineRunner {

    private final BrandJpaRepository brandJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public void run(String... args) {
        int totalProducts = parseEnv("SEED_PRODUCTS", 100_000);
        int brandCount = parseEnv("SEED_BRANDS", 1_000);
        int batchSize = parseEnv("SEED_BATCH_SIZE", 1_000);

        log.info("[SEED] start - brands={}, products={}, batchSize={}", brandCount, totalProducts, batchSize);

        // Seed brands
        if (brandJpaRepository.count() < brandCount) {
            List<BrandModel> brands = new ArrayList<>(brandCount);
            for (int i = 1; i <= brandCount; i++) {
                brands.add(BrandModel.of("브랜드-" + i));
            }
            brandJpaRepository.saveAllAndFlush(brands);
            log.info("[SEED] brands inserted: {}", brands.size());
        } else {
            log.info("[SEED] brands already present: {}", brandJpaRepository.count());
        }

        // Seed products
        long existing = productJpaRepository.count();
        if (existing >= totalProducts) {
            log.info("[SEED] products already present: {} (skip)", existing);
            return;
        }

        List<ProductModel> buffer = new ArrayList<>(batchSize);
        for (int i = 1; i <= totalProducts; i++) {
            long brandId = ThreadLocalRandom.current().nextLong(1, brandCount + 1L);
            long price = ThreadLocalRandom.current().nextLong(1_000L, 1_000_001L);
            int stock = ThreadLocalRandom.current().nextInt(10, 101);

            ProductModel p = ProductModel.of(brandId, "상품-" + i, price, stock);

            // likeCount: 0 ~ 100 사이 임의 설정 (increase 호출로 도메인 규칙 준수)
            int likes = ThreadLocalRandom.current().nextInt(0, 101);
            for (int k = 0; k < likes; k++) {
                p.increaseLikeCount();
            }

            buffer.add(p);

            if (buffer.size() >= batchSize) {
                productJpaRepository.saveAllAndFlush(buffer);
                buffer.clear();
                if (i % (batchSize * 10) == 0) {
                    log.info("[SEED] products inserted: {}", i);
                }
            }
        }

        if (!buffer.isEmpty()) {
            productJpaRepository.saveAllAndFlush(buffer);
            log.info("[SEED] products inserted (final batch): {}", buffer.size());
        }

        log.info("[SEED] done - total products now: {}", productJpaRepository.count());
    }

    private int parseEnv(String key, int defaultValue) {
        String v = System.getenv(key);
        if (v == null || v.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
