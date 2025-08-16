package com.loopers.application.product.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.interfaces.api.product.ProductV1Dto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Component
public class ProductCacheService {

    private static final Duration TTL_DETAIL = Duration.ofSeconds(120);
    private static final Duration TTL_LIST = Duration.ofSeconds(30);

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductCacheService(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public ProductV1Dto.ProductSummaryResponse getProduct(String key, Supplier<ProductV1Dto.ProductSummaryResponse> loader) {
        try {
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return objectMapper.readValue(cached, ProductV1Dto.ProductSummaryResponse.class);
            }
        } catch (Exception ignored) {}

        ProductV1Dto.ProductSummaryResponse loaded = loader.get();
        try {
            String json = objectMapper.writeValueAsString(loaded);
            redisTemplate.opsForValue().set(key, json, TTL_DETAIL);
        } catch (Exception ignored) {}
        return loaded;
    }

    public List<ProductV1Dto.ProductSummaryResponse> getProductList(String key, Supplier<List<ProductV1Dto.ProductSummaryResponse>> loader) {
        try {
            String cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<>() {
                });
            }
        } catch (Exception ignored) {}

        List<ProductV1Dto.ProductSummaryResponse> loaded = loader.get();
        if (loaded == null) loaded = Collections.emptyList();
        try {
            String json = objectMapper.writeValueAsString(loaded);
            redisTemplate.opsForValue().set(key, json, TTL_LIST);
        } catch (Exception ignored) {}
        return loaded;
    }

    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ignored) {}
    }
}
