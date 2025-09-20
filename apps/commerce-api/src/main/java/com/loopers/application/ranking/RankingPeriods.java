package com.loopers.application.ranking;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

public final class RankingPeriods {

    private RankingPeriods() {}

    public static String toYearWeek(LocalDate date) {
        int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        return date.getYear() + "W" + String.format("%02d", week);
    }

    public static String toYearMonth(LocalDate date) {
        return date.getYear() + String.format("%02d", date.getMonthValue());
    }
}
