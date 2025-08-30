package com.loopers.infrastructure.payment.feign;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PgFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> new RuntimeException("PG error: status=" + response.status());
    }
}
