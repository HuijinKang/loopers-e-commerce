package com.loopers.application.ranking;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class RankingCarryOverScheduler {

    private final RankingService rankingService;

    // 매일 00:00:10 에 전날 점수 일부를 carry-over
    @Scheduled(cron = "10 0 0 * * *")
    public void carryOverDaily() {
        rankingService.carryOver(LocalDate.now());
    }
}
