package com.example.ratelimiter.service;

import com.example.ratelimiter.dto.RateLimitRequest;
import com.example.ratelimiter.dto.RateLimitResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<List> tokenBucketScript;

    public RateLimiterService(StringRedisTemplate redis,
                              DefaultRedisScript<List> tokenBucketScript) {
        this.redis = redis;
        this.tokenBucketScript = tokenBucketScript;
    }

    // ----------------------------------------------
    // 1️⃣ MAIN RATE LIMIT EXECUTION
    // ----------------------------------------------
    public RateLimitResponse tryConsume(RateLimitRequest req) {

        String key = req.getKey();
        int capacity = req.getCapacity();
        double rps = req.getRefillPerSecond();
        double cost = req.getCost();
        long now = System.currentTimeMillis();

        List<String> argv = List.of(
                String.valueOf(capacity),
                String.valueOf(rps),
                String.valueOf(now),
                String.valueOf(cost)
        );

        List<Object> result = redis.execute(
                tokenBucketScript,
                Collections.singletonList(key),
                argv.toArray()
        );

        if (result == null || result.size() < 3) {
            return new RateLimitResponse(false, 0.0, 0L);
        }

        boolean allowed = "1".equals(result.get(0).toString());
        double tokensLeft = Double.parseDouble(result.get(1).toString());
        long ttlMillis = Long.parseLong(result.get(2).toString());

        // UPDATE METRICS
        updateMetrics(key, allowed);

        return new RateLimitResponse(allowed, tokensLeft, ttlMillis);
    }

    // ----------------------------------------------
    // 2️⃣ METRICS STORAGE
    // ----------------------------------------------
    private void updateMetrics(String key, boolean allowed) {
        String base = "metrics:" + key;

        if (allowed) {
            redis.opsForValue().increment(base + ":allowed", 1);
        } else {
            redis.opsForValue().increment(base + ":blocked", 1);
        }

        redis.opsForValue().set(base + ":lastAccess", String.valueOf(System.currentTimeMillis()));
    }

    // ----------------------------------------------
    // 3️⃣ METRICS FETCH API
    // ----------------------------------------------
    public Map<String, Object> getMetrics(String key) {
        String base = "metrics:" + key;

        Map<String, Object> map = new HashMap<>();
        map.put("allowed", redis.opsForValue().get(base + ":allowed"));
        map.put("blocked", redis.opsForValue().get(base + ":blocked"));
        map.put("lastAccess", redis.opsForValue().get(base + ":lastAccess"));
        return map;
    }

    // ----------------------------------------------
    // 4️⃣ Dashboard API (tokens + metrics)
    // ----------------------------------------------
    public Map<String, Object> getDashboardInfo(String key) {
        Map<String, Object> map = new HashMap<>();

        map.put("tokens", redis.opsForHash().get(key, "tokens"));
        map.put("lastTimestamp", redis.opsForHash().get(key, "ts"));

        String base = "metrics:" + key;
        map.put("allowed", redis.opsForValue().get(base + ":allowed"));
        map.put("blocked", redis.opsForValue().get(base + ":blocked"));
        map.put("lastAccess", redis.opsForValue().get(base + ":lastAccess"));

        return map;
    }
}
