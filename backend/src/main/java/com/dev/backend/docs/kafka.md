# Advanced Apache Kafka Integration Guide

This document outlines the production-ready, enterprise-grade Apache Kafka features implemented in the **Green Shop** e-commerce backend platform.

---

## 📌 Architecture Overview

```
[ Order Service ] ──(Async Callback + Partition Key: orderId)──► [ Kafka Topic: order-confirmed (3 Partitions) ]
                                                                       │
                                              ┌────────────────────────┴────────────────────────┐
                                              ▼                                                 ▼
                               [ Consumer Group: email-group ]                  [ Error Handling (3 Retries) ]
                                              │                                                 │
                                     (Redis Idempotency Check)                                  ▼
                                              │                                [ Dead Letter Topic: order-confirmed.DLT ]
                                              ▼                                                 │
                                      [ Send Email & Manual Ack ] ◄─────────────────────────────┘
```

---

## 🚀 Key Advanced Kafka Features

### 1. Reliable Producer Configuration (`KafkaProducerConfig`)
- **`acks = "all"`**: Ensures all In-Sync Replicas (ISR) acknowledge the message write before returning success to prevent data loss.
- **`enable.idempotence = true`**: Guarantees exactly-once message delivery from the producer side, preventing duplicate messages caused by network retries.
- **Retries & Backoff**: Configured with `retries = 3` and `retry.backoff.ms = 1000` to automatically recover from transient network failures.
- **Performance Batching**: Configured `linger.ms = 10` and `batch.size = 16384` (16KB) to optimize throughput.

---

### 2. Message Partition Key & Async Callback (`OrderServiceImpl`)
- **Partition Key (`partitionKey = orderId`)**: All events belonging to the same order are routed to the exact same Kafka partition, ensuring **strict event ordering** per order.
- **Async Callback (`CompletableFuture.whenComplete()`)**: Message publishing is non-blocking. The HTTP API returns a response to the client immediately (~5ms latency) while an asynchronous callback handles log tracing or fallback error logging.

---

### 3. Programmatic Multi-Partition Provisioning (`KafkaTopicConfig`)
- Uses Spring Kafka `NewTopic` beans to automatically provision topics (`order-confirmed`, `order-events`, `order-confirmed.DLT`) with **3 partitions** upon application startup, eliminating manual CLI topic creation.

---

### 4. Resilient Consumer Container Factory (`KafkaConsumerConfig`)
- **Manual Acknowledgment (`AckMode.MANUAL_IMMEDIATE`)**: Disabled auto-commit (`ENABLE_AUTO_COMMIT_CONFIG = false`). Offsets are committed manually only after business execution succeeds.
- **Consumer Concurrency (`concurrency = 3`)**: Spawns 3 concurrent consumer threads per listener container to process messages across partitions in parallel.
- **Automated Retries & Dead Letter Queue (DLQ / `.DLT`)**: Configured `DefaultErrorHandler` with a 3-retry limit (2-second fixed backoff). Unhandled failures automatically forward to `order-confirmed.DLT` via `DeadLetterPublishingRecoverer`.

---

### 5. Idempotent Consumer & DLT Alarm Listener (`EmailService`)
- **Redis Idempotency Control**: Employs Redis `SETNX` (`setIfAbsent`) keyed by `eventId` (7-day TTL). If Kafka re-delivers a message during network partition recovery, duplicate execution is blocked.
- **Manual Offset Commitment (`ack.acknowledge()`)**: Explicitly commits offset after successful email dispatch via Brevo client. On exception, the Redis key is evicted and the exception re-thrown to trigger retry backoff.
- **Dead Letter Topic Listener (`@KafkaListener(topics = "order-confirmed.DLT")`)**: Listens to exhausted failed messages to emit critical alert logs for admin monitoring.

---

## 🛠️ Configuration Summary

| Feature | Class / Config | Benefit |
| :--- | :--- | :--- |
| **Producer Acks & Idempotence** | `KafkaProducerConfig` | Zero data loss, zero producer duplicate events. |
| **Partition Keying** | `OrderServiceImpl` | Guarantees strict message ordering per order ID. |
| **Async Callback** | `OrderServiceImpl` | Ultra-low API response latency (~5ms). |
| **Manual Acknowledgment** | `KafkaConsumerConfig` | Prevents premature offset commits during failures. |
| **Retry & DLQ Routing** | `KafkaConsumerConfig` | Automatic recovery + isolation of poisonous messages. |
| **Redis Idempotency** | `EmailService` | Guarantees exactly-once email delivery to users. |
