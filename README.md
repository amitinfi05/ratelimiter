# Distributed Rate Limiter — Spring Boot + Redis + Lua + Docker

## Project Overview

This project is a **high-performance distributed rate limiter** built using Spring Boot, Redis, Lua scripting, and Docker. It demonstrates how to control API request rates for users or IP addresses, ensuring fair usage and preventing abuse.

Key features include:

* Token Bucket algorithm for rate limiting.
* Atomic operations using Lua scripts in Redis.
* Metrics collection (allowed/blocked requests).
* Dashboard endpoints for monitoring token usage and request statistics.
* IP-based and user-plan-based rate limiting.

---

## Motivation

In modern applications, APIs must be protected from excessive requests that can overload servers. This project shows how to implement a **concurrency-safe distributed rate limiter**, a common requirement in real-world high-traffic systems.

---

## Tech Stack

* **Java 17** with Spring Boot
* **Redis** for fast, in-memory storage
* **Lua** for atomic token bucket operations
* **Docker** for containerized Redis
* **Maven** for project management

---

## Setup & Run

### 1️⃣ Clone the repository

```bash
git clone https://github.com/amitinfi05/ratelimiter.git
cd ratelimiter
```

### 2️⃣ Run Redis using Docker

```bash
docker run --name redis-rate-limit -p 6379:6379 -d redis
```

### 3️⃣ Run Spring Boot application

```bash
mvn spring-boot:run
```

By default, the API runs on **[http://localhost:8080](http://localhost:8080)**.

---

## API Endpoints

### 1️⃣ Manual rate limit (JSON request)

```http
POST /api/rate-limiter/check
Content-Type: application/json
```

**Request JSON**

```json
{
  "key": "kumar",
  "capacity": 5,
  "refillPerSecond": 1,
  "cost": 1.0
}
```

**Response JSON**

```json
{
  "allowed": true,
  "tokensLeft": 4,
  "ttlMillis": 5000
}
```

---

### 2️⃣ IP-based rate limiting

```http
POST /api/rate-limiter/check/ip
```

Automatically uses the client’s IP as the key.

---

### 3️⃣ User Plan-based rate limiting

```http
POST /api/rate-limiter/check/user/{userId}?plan=FREE
```

Supports plans: `FREE`, `PRO`, `PREMIUM`.

---

### 4️⃣ Metrics API

```http
GET /api/rate-limiter/metrics/{key}
```

Returns:

```json
{
  "allowed": 10,
  "blocked": 2,
  "lastAccess": "1690000000000"
}
```

---

### 5️⃣ Dashboard API

```http
GET /api/rate-limiter/dashboard/{key}
```

Returns current token count + metrics.

---

## Project Structure

```
src/main/java/com/example/ratelimiter
├── config        # Redis and Lua script configuration
├── controller    # API controllers
├── dto           # Request/Response objects
├── service       # Rate limiter logic + metrics
├── Plan.java     # Enum for user plans
├── PlanConfig.java # Predefined plan configurations
src/main/resources
├── token_bucket.lua # Lua script for atomic token bucket
├── application.properties / application.yml
```

---

## Future Improvements

* Add **user authentication integration** for per-user limits.
* Add **rate limits per API endpoint**.
* Add **prometheus metrics & Grafana dashboard**.
* Dockerize the Spring Boot app for full container deployment.
* Support **dynamic plan updates** without redeploying.

---

## License

MIT License

---

## How to Contribute

1. Fork the repository.
2. Create a feature branch.
3. Commit changes with clear messages.
4. Open a Pull Request.

---

This README ensures anyone can **quickly run, test, and understand your rate limiter project**, making it resume-ready and attractive to recruiters.
