package com.example.ratelimiter.dto;

public class RateLimitRequest {

    private String key;
    private int capacity = 10;
    private double refillPerSecond = 1.0;
    private double cost = 1.0;

    public RateLimitRequest() {}

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public double getRefillPerSecond() { return refillPerSecond; }
    public void setRefillPerSecond(double refillPerSecond) { this.refillPerSecond = refillPerSecond; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
