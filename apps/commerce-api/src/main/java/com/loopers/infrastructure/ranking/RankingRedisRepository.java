package com.loopers.infrastructure.ranking;

import com.loopers.domain.ranking.RankingEntry;
import com.loopers.domain.ranking.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RankingRedisRepository implements RankingRepository {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void incrementScore(LocalDate date, Long productId, double delta, long ttlSeconds) {
        String key = RankingKey.daily(date);
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), delta);
        if (ttlSeconds > 0) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public List<RankingEntry> top(LocalDate date, int limit) {
        String key = RankingKey.daily(date);
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, Math.max(0, limit - 1));
        List<RankingEntry> result = new ArrayList<>();
        if (tuples == null) return result;
        for (ZSetOperations.TypedTuple<String> t : tuples) {
            if (t.getValue() == null || t.getScore() == null) continue;
            Double s = t.getScore();
            if (s == null) continue;
            result.add(RankingEntry.of(Long.valueOf(t.getValue()), s));
        }
        return result;
    }

    @Override
    public Long rank(LocalDate date, Long productId) {
        String key = RankingKey.daily(date);
        Long r = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(productId));
        if (r == null) return null;
        return r + 1; // 1-based rank
    }

    @Override
    public Double score(LocalDate date, Long productId) {
        String key = RankingKey.daily(date);
        return redisTemplate.opsForZSet().score(key, String.valueOf(productId));
    }

    @Override
    public void carryOver(LocalDate today, LocalDate yesterday, double weight, long ttlSeconds) {
        String dst = RankingKey.daily(today);
        String src = RankingKey.daily(yesterday);

        // Fallback: 직접 합산 (Lettuce 드라이버 ZUNIONSTORE 가 가용하지 않은 환경 고려)
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeWithScores(src, 0, -1);
        if (tuples != null) {
            for (ZSetOperations.TypedTuple<String> t : tuples) {
                String member = t.getValue();
                Double sc = t.getScore();
                if (member == null || sc == null) continue;
                double inc = sc * weight;
                redisTemplate.opsForZSet().incrementScore(dst, member, inc);
            }
        }
        if (ttlSeconds > 0) {
            redisTemplate.expire(dst, ttlSeconds, TimeUnit.SECONDS);
        }
    }
}
