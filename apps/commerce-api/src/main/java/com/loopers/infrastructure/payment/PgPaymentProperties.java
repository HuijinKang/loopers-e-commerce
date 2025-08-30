package com.loopers.infrastructure.payment;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "pg")
public class PgPaymentProperties {
    private final String baseUrl;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public PgPaymentProperties(String baseUrl, Integer connectTimeoutMs, Integer readTimeoutMs) {
        this.baseUrl = baseUrl != null ? baseUrl : "http://localhost:8082";
        this.connectTimeoutMs = connectTimeoutMs != null ? connectTimeoutMs : 1000;
        this.readTimeoutMs = readTimeoutMs != null ? readTimeoutMs : 3000;
    }
}
