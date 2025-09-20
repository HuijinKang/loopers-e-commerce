package com.loopers.application.ranking;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ranking")
public record RankingSettings(
        double weightView,
        double weightLike,
        double weightOrder,
        double carryOverWeight,
        long ttlSeconds
) {}
