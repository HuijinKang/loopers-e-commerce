package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingEntry;
import com.loopers.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final RankingSettings rankingSettings;

    public void onView(Long productId) {
        increment(LocalDate.now(), productId, rankingSettings.weightView());
    }

    public void onLikeChanged(Long productId, boolean liked) {
        double delta = liked ? rankingSettings.weightLike() : -rankingSettings.weightLike();
        increment(LocalDate.now(), productId, delta);
    }

    public void onOrderConfirmed(Long productId) {
        increment(LocalDate.now(), productId, rankingSettings.weightOrder());
    }

    public List<RankingEntry> top(LocalDate date, int limit) {
        return rankingRepository.top(date, limit);
    }

    public Long rank(LocalDate date, Long productId) {
        return rankingRepository.rank(date, productId);
    }

    public Double score(LocalDate date, Long productId) {
        return rankingRepository.score(date, productId);
    }

    public void carryOver(LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
        rankingRepository.carryOver(today, yesterday, rankingSettings.carryOverWeight(), rankingSettings.ttlSeconds());
    }

    private void increment(LocalDate date, Long productId, double delta) {
        rankingRepository.incrementScore(date, productId, delta, rankingSettings.ttlSeconds());
    }
}
