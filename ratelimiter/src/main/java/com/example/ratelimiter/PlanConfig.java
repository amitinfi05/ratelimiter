package com.example.ratelimiter;

import com.example.ratelimiter.Plan;
import com.example.ratelimiter.dto.RateLimitRequest;


public class PlanConfig {

    public static RateLimitRequest getConfig(String userId, Plan plan) {
        RateLimitRequest req = new RateLimitRequest();
        req.setKey("user:" + userId);

        switch (plan) {
            case FREE:
                req.setCapacity(10);
                req.setRefillPerSecond(0.17); // 10/min
                break;

            case PRO:
                req.setCapacity(100);
                req.setRefillPerSecond(1.67); // 100/min
                break;

            case PREMIUM:
                req.setCapacity(1000);
                req.setRefillPerSecond(16.67); // 1000/min
                break;
        }
        return req;
    }
}
