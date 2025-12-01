package com.example.ratelimiter.controller;

import com.example.ratelimiter.dto.RateLimitRequest;
import com.example.ratelimiter.dto.RateLimitResponse;
import com.example.ratelimiter.Plan;
import com.example.ratelimiter.service.RateLimiterService;
import com.example.ratelimiter.PlanConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rate-limiter")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    // ----------------------------------------------
    // 1️⃣ Manual rate limit using JSON request (your original API)
    // ----------------------------------------------
    @PostMapping("/check")
    public ResponseEntity<RateLimitResponse> check(@RequestBody RateLimitRequest req) {
        RateLimitResponse res = rateLimiterService.tryConsume(req);
        return ResponseEntity.ok(res);
    }

    // ----------------------------------------------
    // 2️⃣ IP-based rate limiting
    // ----------------------------------------------
    @PostMapping("/check/ip")
    public ResponseEntity<RateLimitResponse> checkByIp(HttpServletRequest request) {
        String ip = getClientIP(request);

        RateLimitRequest req = new RateLimitRequest();
        req.setKey("ip:" + ip);
        req.setCapacity(10);
        req.setRefillPerSecond(5);
        req.setCost(1.0);

        return ResponseEntity.ok(rateLimiterService.tryConsume(req));
    }

    private String getClientIP(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff == null) return req.getRemoteAddr();
        return xff.split(",")[0];
    }

    // ----------------------------------------------
    // 3️⃣ User Plan Limiter (Free / Pro / Premium)
    // ----------------------------------------------
    @PostMapping("/check/user/{userId}")
    public ResponseEntity<RateLimitResponse> checkUserPlan(
            @PathVariable String userId,
            @RequestParam Plan plan
    ) {
        RateLimitRequest req = PlanConfig.getConfig(userId, plan);
        return ResponseEntity.ok(rateLimiterService.tryConsume(req));
    }

    // ----------------------------------------------
    // 4️⃣ Metrics Dashboard
    // ----------------------------------------------
    @GetMapping("/metrics/{key}")
    public Map<String, Object> metrics(@PathVariable String key) {
        return rateLimiterService.getMetrics(key);
    }

    // ----------------------------------------------
    // 5️⃣ Dashboard view (tokens + metrics)
    // ----------------------------------------------
    @GetMapping("/dashboard/{key}")
    public Map<String, Object> dashboard(@PathVariable String key) {
        return rateLimiterService.getDashboardInfo(key);
    }
}
