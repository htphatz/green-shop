# Green Shop - E-Commerce Backend System

[![Java Version](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.8-black.svg)](https://kafka.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-Redisson-red.svg)](https://redis.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

A high-performance E-Commerce RESTful API backend system built on **Java 21**, **Spring Boot 3.4**, **Apache Kafka (KRaft)**, **Redis (Redisson)**, and **MySQL**. The system is engineered using **Event-Driven Architecture (EDA)** to optimize inventory handling, automated shipping, online payments, and multi-tier caching.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Features](#2-features)
3. [Tech Stack](#3-tech-stack)
4. [Architecture](#4-architecture)
5. [Project Structure](#5-project-structure)
6. [Authentication & Authorization](#6-authentication--authorization)
7. [API Documentation](#7-api-documentation)
8. [Getting Started](#8-getting-started)
9. [Testing](#9-testing)
10. [Docker / Deployment](#10-docker--deployment)
11. [Technical Highlights](#11-technical-highlights)
12. [Future Improvements](#12-future-improvements)

---

## 1. Project Overview

**Green Shop** is an e-commerce backend platform focused on **catalog & merchandise management, inventory/warehouse control, and order fulfillment automation**:

- **Catalog & Merchandise Management**: Controls detailed product metadata, category classification, Cloudinary media storage, and real-time inventory quantity tracking.
- **Order & Stock Control**: Manages the complete order lifecycle (`PENDING` -> `CONFIRMED` -> `SHIPPED` -> `DELIVERED` / `CANCELLED`), automatically checking and deducting stock securely upon purchase.
- **Logistics & Settlement**: Integrates Giao Hàng Nhanh (GHN) shipping services for fee calculation and administrative boundary mapping, along with VNPay online payment settlement via HMAC-SHA512 checksums.
- **Promotions & Notifications**: Handles discount vouchers and automatically triggers order confirmation email events via Apache Kafka & Brevo API.

---

## 2. Features

- **Auth & Security**: Email/Password & Google OAuth2 login, JWT Access/Refresh tokens, Token Revocation List, and Redis-backed rate limiting for brute-force prevention.
- **Catalog & Caching**: Product CRUD operations, Redis-cached pagination/searching, and Write-Back product view counter sync.
- **Orders & Vouchers**: Transactional checkout, automated inventory deduction, and fixed/percentage discount vouchers.
- **Third-Party Integrations**: VNPay payment gateway, GHN shipping fee calculation (OpenFeign), and asynchronous email dispatching (Kafka + Brevo).
- **Revenue Analytics**: Financial reporting aggregated by day, month, year, or custom date range.

---

## 3. Tech Stack

| Component | Technology / Library |
| :--- | :--- |
| **Language & Core** | Java 21 LTS, Spring Boot 3.4.0 |
| **Database & ORM** | MySQL 8.0, Spring Data JPA / Hibernate |
| **Caching & Locking** | Redis, Redisson 3.41.0, Jedis 5.2.0 |
| **Message Broker** | Apache Kafka 3.8 (KRaft Mode) |
| **Security & Auth** | Spring Security 6, OAuth2 Resource Server, Nimbus JWT |
| **HTTP Client & Mapper** | Spring Cloud OpenFeign (2024.0.0), MapStruct 1.6.2, Lombok |
| **Integrations** | Cloudinary (Image), Brevo (Email), VNPay (Payment), GHN (Shipping) |
| **Tools & Container** | Docker, Docker Compose, Springdoc OpenAPI 2.7 (Swagger) |

---

## 4. Architecture

```
                    +---------------------------------------+
                    |             Client / Web UI           |
                    +---------------------------------------+
                                        |
                                    HTTP / REST
                                        v
                    +---------------------------------------+
                    |       Spring Security & JWT Filter    |
                    +---------------------------------------+
                                        |
                                        v
                    +---------------------------------------+
                    |     Controller -> Service Layer       |
                    +---------------------------------------+
                     /                  |                  \
                    /                   |                   \
                   v                    v                    v
      +------------------+    +------------------+    +-------------------+
      |   MySQL (JPA)    |    |   Redis Cache    |    |   Apache Kafka    |
      |   (Persistent)   |    | (Redisson Lock)  |    |  (Event Producer) |
      +------------------+    +------------------+    +-------------------+
                                                                |
                                                                v
                                                      +-------------------+
                                                      |  Kafka Consumers  |
                                                      +-------------------+
                                                       /                 \
                                                      v                   v
                                      +------------------+     +-------------------+
                                      | Inventory Update |     | Brevo Email API   |
                                      +------------------+     +-------------------+
```

### Key Design Patterns
1. **Cache-Aside Pattern**: Queries Redis before hitting MySQL; uses Redisson Distributed Lock to rebuild cache on miss.
2. **Write-Back Pattern**: Atomically increments product view counts in Redis Hash (`product:views`) and flushes to MySQL periodically via Cron Scheduler.
3. **Idempotent Consumer**: Prevents duplicate email dispatches using Redis `SETNX` (7-day TTL).

---

## 5. Project Structure

```
green-shop/
├── .github/                       # CI/CD Workflows & repository configs
├── backend/
│   ├── .env                       # Environment variables configuration file
│   ├── Dockerfile                 # Docker build script
│   ├── docker-compose.yml         # Container orchestration (MySQL, Redis, Kafka)
│   ├── pom.xml                    # Maven dependencies build manifest
│   └── src/
│       ├── main/java/com/dev/backend/
│       │   ├── config/            # Configurations (Security, Redis, Kafka, OpenAPI)
│       │   ├── controller/        # REST Controller API Endpoints
│       │   ├── dto/               # Request/Response DTOs & Kafka Events
│       │   ├── entity/            # JPA Entities (User, Product, Order, etc.)
│       │   ├── mapper/            # MapStruct mappers
│       │   ├── payment/           # VNPay payment configuration & callbacks
│       │   ├── repository/        # Spring Data Repositories & Feign Clients
│       │   ├── scheduler/         # Background Sync & Cleanup tasks
│       │   └── service/           # Business Logic Interfaces & Implementations
│       └── test/                  # Unit & Integration Tests
```

---

## 6. Authentication & Authorization

- **Dual Authentication**: Local Auth (Email/BCrypt Password) + OAuth2 Google.
- **JWT Mechanism**: Access Token (2h) + Refresh Token (3h) with Revocation Blacklist support.
- **Brute-Force Protection**: Tracks failed login attempts per email in Redis (`failed_login:<email>`). Temporarily locks accounts for 10 minutes after 5 failures.

---

## 7. API Documentation

- **Swagger UI**: `http://localhost:8080/api-docs.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

| Group | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/auth/login`, `/auth/register`, `/auth/refresh`, `/auth/logout` | Session management & authentication |
| **Product** | `GET`, `POST` | `/products`, `/products/{id}` | Search/Fetch products (Cached), create product |
| **Order** | `POST`, `GET`, `PUT` | `/orders`, `/orders/my-orders`, `/orders/{id}/status` | Checkout, track & update order status |
| **Payment** | `POST`, `GET` | `/payment/create-vnpay-payment`, `/payment/vn-pay-callback` | VNPay URL generation & IPN callback |
| **GHN** | `GET`, `POST` | `/ghn/province`, `/ghn/shipping-order` | GHN administrative boundaries & shipping fee |
| **Revenue** | `GET` | `/revenue/period` | Financial revenue reporting |

---

## 8. Getting Started

### Environment Template (`backend/.env`)
```env
DBMS_CONNECTION=your_db_connection_url
DBMS_USERNAME=your_db_username
DBMS_PASSWORD=your_db_password
MYSQL_DATABASE=your_mysql_database
MYSQL_USER=your_mysql_user
MYSQL_PASSWORD=your_mysql_password
MYSQL_ROOT_PASSWORD=your_mysql_root_password

REDIS_HOST=your_redis_host
REDIS_PORT=your_redis_port
KAFKA_SERVER=your_kafka_server

API_SECRET_CLOUDINARY=your_cloudinary_secret
SECRET_KEY_VNPAY=your_vnpay_secret
TOKEN_GHN=your_ghn_token
API_KEY_BREVO=your_brevo_api_key
```

### Execution Steps
1. Start infrastructure services (MySQL, Redis, Kafka):
   ```bash
   cd backend
   docker-compose up -d
   ```
2. Launch Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## 9. Testing

Run all Unit & Integration tests using JUnit 5 and Mockito:
```bash
./mvnw test
```

---

## 10. Docker / Deployment

Run infrastructure services via Docker Compose:
```bash
docker-compose up -d
```
Build production Docker image:
```bash
docker build -t green-shop-backend:latest .
```

---

## 11. Technical Highlights

- **Cache Penetration**: Stores Sentinel NULL Values (`@@NULL@@`) in Redis with a 3-minute TTL on database misses.
- **Cache Breakdown**: Uses Redisson Distributed Locks with Double-Check Locking to ensure single-thread cache rebuilding.
- **Cache Avalanche**: Applies Random Jitter (10m base TTL + 0-5m random padding) to prevent synchronized expiration.
- **Kafka Resilience**: Idempotent email processing using Redis `SETNX`, manual offset commits (`MANUAL_IMMEDIATE`), and Dead Letter Topics (`.DLT`) for error recovery.

---

## 12. Future Improvements

- [ ] Full-text search expansion using ElasticSearch index.
- [ ] Microservices decomposition (Auth, Order, Catalog, Notification services).
- [ ] JVM, Redis, and Kafka metrics monitoring with Prometheus & Grafana.
