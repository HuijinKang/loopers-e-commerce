package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankingEntry;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RankingServiceIntegrationTest {

    @Autowired
    RankingService rankingService;

    @Autowired
    RankingSettings settings;

    @Autowired
    RedisCleanUp redisCleanUp;

    @AfterEach
    void tearDown() { redisCleanUp.truncateAll(); }

    @Nested
    @DisplayName("ZINCRBY & 조회")
    class ZIncr {
        @Test
        @DisplayName("like, order 가중치 반영")
        void weights() {
            // arrange
            Long p1 = 101L; Long p2 = 202L;
            LocalDate today = LocalDate.now();
            // act
            rankingService.onLikeChanged(p1, true);
            rankingService.onLikeChanged(p1, false); // 해제 시 감소
            rankingService.onLikeChanged(p1, true);
            rankingService.onOrderConfirmed(p2);
            // assert
            List<RankingEntry> top = rankingService.top(today, 10);
            assertThat(top).isNotEmpty();
            Double s1 = rankingService.score(today, p1);
            Double s2 = rankingService.score(today, p2);
            assertThat(s1).isEqualTo(settings.weightLike());
            assertThat(s2).isEqualTo(settings.weightOrder());
        }
    }

    @Nested
    @DisplayName("캐리오버")
    class CarryOver {
        @Test
        @DisplayName("어제 점수의 일부가 오늘로 이월된다")
        void carry() {
            // arrange
            Long p1 = 303L; LocalDate y = LocalDate.now().minusDays(1); LocalDate t = LocalDate.now();
            rankingService.onLikeChanged(p1, true); // today init (ignored)
            // 어제 점수 세팅
            rankingService.carryOver(y);
            
            // 직접 증가를 어제 날짜로
            // act
            rankingService.carryOver(t);
            
            // assert
            rankingService.top(t, 10);
        }
    }
}
