package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.List;

public interface RankingRepository {

    void incrementScore(LocalDate date, Long productId, double delta, long ttlSeconds);

    List<RankingEntry> top(LocalDate date, int limit);

    Long rank(LocalDate date, Long productId);

    Double score(LocalDate date, Long productId);

    void carryOver(LocalDate today, LocalDate yesterday, double weight, long ttlSeconds);
}
