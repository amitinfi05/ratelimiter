package com.example.ratelimiter.dto;

public class RateLimitResponse {

    private boolean allowed;
    private double tokensLeft;
    private long ttlMillis;

    public RateLimitResponse() {}

    public RateLimitResponse(boolean allowed, double tokensLeft, long ttlMillis) {
        this.allowed = allowed;
        this.tokensLeft = tokensLeft;
        this.ttlMillis = ttlMillis;
    }

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }

    public double getTokensLeft() { return tokensLeft; }
    public void setTokensLeft(double tokensLeft) { this.tokensLeft = tokensLeft; }

    public long getTtlMillis() { return ttlMillis; }
    public void setTtlMillis(long ttlMillis) { this.ttlMillis = ttlMillis; }

}
