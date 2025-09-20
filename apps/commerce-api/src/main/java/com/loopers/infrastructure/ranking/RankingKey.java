package com.loopers.infrastructure.ranking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

final class RankingKey {
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyyMMdd");
    private RankingKey() {}
    static String daily(LocalDate date) { return "rank:all:" + date.format(F); }
}
