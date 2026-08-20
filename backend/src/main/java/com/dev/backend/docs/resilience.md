# Advanced Resilience4j Fault Tolerance & Rate Limiting Guide

This document outlines the production-ready, enterprise-grade fault tolerance patterns (Circuit Breaker) and rate limiting strategies implemented in the **Green Shop** e-commerce backend platform using **Resilience4j**.

---

## 📌 Architecture Overview

```
                                ┌─────────────────────────────────────────────────────────────┐
                                │                    Client API Request                       │
                                └──────────────────────────────┬──────────────────────────────┘
                                                               │
                                                               ▼
                                                [ 1. Spring Security & JWT ]
                                                               │
                                                               ▼
                                                [ 2. Resilience4j Rate Limiter ]
                                                (5 req/sec limit on Auth endpoints)
                                                               │
                                      ┌────────────────────────┴────────────────────────┐
                                      ▼                                                 ▼
                             ( Under Limit <= 5 )                               ( Exceeded Limit > 5 )
                                      │                                                 │
                                      ▼                                                 ▼
                         [ 3. Controller Execution ]                       [ GlobalExceptionHandler ]
                                      │                                    (HTTP 429 Too Many Requests)
                                      ▼
                        [ 4. External Feign Client Call ]
                                 (GHN Shipping)
                                      │
                                      ▼
                       [ 5. Resilience4j Circuit Breaker ]
                         (Sliding Window: 10 calls,
                          Failure Threshold: >= 50%)
                                      │
             ┌────────────────────────┴────────────────────────┐
             ▼                                                 ▼
     ( State: CLOSED )                                  ( State: OPEN )
             │                                                 │
             ▼                                                 ▼
  [ Call External API ]                              [ Trigger Fallback ]
  (Return Actual Result)                             (Return Default 30,000 VND)
```

---

## 🚀 Key Advanced Resilience Features

### 1. Inbound API Protection (`@RateLimiter`)
- **Brute-Force & Spam Prevention**: Applied `@RateLimiter(name = "apiRateLimiter")` to critical authentication endpoints (`/auth/login`, `/auth/register` in `AuthController`).
- **Sliding Window Rate Limiting**: Enforces a maximum limit of **5 requests per second** (`limit-for-period: 5`, `limit-refresh-period: 1s`).
- **Immediate Non-Blocking Rejection**: Configured with `timeout-duration: 0ms`. Any request exceeding the rate limit is immediately rejected without thread blocking.
- **Centralized Exception Handling**: Handled via `GlobalExceptionHandler` catching `RequestNotPermitted` to return a clean HTTP `429 Too Many Requests` API response.

---

### 2. Outbound Service Fault Isolation (`@CircuitBreaker`)
- **Cascading Failure Prevention**: Applied `@CircuitBreaker(name = "ghnService", fallbackMethod = "getShippingFeeFallback")` to external shipping integrations in `GHNController`.
- **Count-Based Sliding Window**: Tracks the outcome of the last **10 calls** (`sliding-window-size: 10`, `sliding-window-type: COUNT_BASED`).
- **Failure & Slow Call Thresholds**:
  - **Failure Threshold**: Opens the circuit if $\ge 50\%$ of calls fail (`failure-rate-threshold: 50`).
  - **Slow Call Threshold**: Considers calls taking $> 2\text{ seconds}$ as slow (`slow-call-duration-threshold: 2s`) and opens the circuit if $\ge 50\%$ of calls are slow (`slow-call-rate-threshold: 50`).
- **State Transition Control**:
  - **OPEN State Duration**: Stays in `OPEN` state for **10 seconds** (`wait-duration-in-open-state: 10s`), short-circuiting all incoming requests immediately.
  - **HALF-OPEN Trial Calls**: Permits **3 trial calls** (`permitted-number-of-calls-in-half-open-state: 3`) in `HALF-OPEN` state to test service recovery before transitioning back to `CLOSED`.

---

### 3. Graceful Fallback Strategy
- **Service Continuity**: When GHN API experiences downtime or high latency, the `getShippingFeeFallback` method is executed seamlessly.
- **Predictable Degraded Response**: Returns a flat shipping fee of `30,000 VND` with HTTP 200 OK and an informative message, ensuring the checkout flow remains uninterrupted for customers.

---

### 4. Global Exception Mapping (`GlobalExceptionHandler`)
- **`RequestNotPermitted`**: Maps to HTTP `429 TOO_MANY_REQUESTS` (`"Too many requests. Please try again later."`).
- **`CallNotPermittedException`**: Maps to HTTP `503 SERVICE_UNAVAILABLE` when the Circuit Breaker is `OPEN` (`"Third-party service is temporarily unavailable. Please try again later."`).

---

## 🛠️ Configuration Summary

| Feature | Component / Class | Configuration / Property | Benefit |
| :--- | :--- | :--- | :--- |
| **API Rate Limiting** | `AuthController` | `limit-for-period: 5`, `limit-refresh-period: 1s` | Prevents Brute-Force attacks and server request spamming. |
| **Circuit Breaker** | `GHNController` | `sliding-window-size: 10`, `failure-rate-threshold: 50` | Isolates third-party API failures and prevents thread pool exhaustion. |
| **Slow Call Detection** | `application.yml` | `slow-call-duration-threshold: 2s` | Automatically trips circuit breaker if external calls hang or lag. |
| **Automatic Fallback** | `GHNController` | `getShippingFeeFallback()` | Guarantees uninterrupted customer checkout during partner outages. |
| **Global Error Handling**| `GlobalExceptionHandler` | `@ExceptionHandler(RequestNotPermitted.class)` | Standardizes HTTP 429 and 503 error responses across the platform. |
