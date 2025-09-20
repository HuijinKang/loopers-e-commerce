package com.loopers.domain.ranking;

public record RankingEntry(Long productId, double score) {
    public static RankingEntry of(Long productId, double score) { return new RankingEntry(productId, score); }
}
