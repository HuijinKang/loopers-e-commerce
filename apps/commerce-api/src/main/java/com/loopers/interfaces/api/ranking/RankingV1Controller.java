package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingPeriods;
import com.loopers.application.ranking.RankingService;
import com.loopers.domain.ranking.RankingEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rankings")
@RequiredArgsConstructor
public class RankingV1Controller {

    private final RankingService rankingService;

    @GetMapping("/top")
    public List<RankingEntry> top(
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date,
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return switch (period) {
            case "weekly" -> rankingService.topWeekly(RankingPeriods.toYearWeek(date), limit);
            case "monthly" -> rankingService.topMonthly(RankingPeriods.toYearMonth(date), limit);
            default -> rankingService.top(date, limit);
        };
    }

    @GetMapping("/{productId}")
    public Long rank(
            @PathVariable Long productId,
            @RequestParam @DateTimeFormat(pattern = "yyyyMMdd") LocalDate date,
            @RequestParam(defaultValue = "daily") String period
    ) {
        if ("daily".equals(period)) {
            return rankingService.rank(date, productId);
        }
        return rankingService.rank(date, productId);
    }
}
