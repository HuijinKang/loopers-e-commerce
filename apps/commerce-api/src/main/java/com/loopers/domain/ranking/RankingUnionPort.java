package com.loopers.domain.ranking;

import java.time.LocalDate;
import java.util.Map;

public interface RankingUnionPort {
    Map<Long, Double> aggregateScores(LocalDate fromInclusive, LocalDate toInclusive);
}
