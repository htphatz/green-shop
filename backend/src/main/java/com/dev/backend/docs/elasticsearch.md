# Advanced Elasticsearch Integration Guide

This document outlines the production-ready, enterprise-grade Elasticsearch search strategies and real-time data synchronization patterns implemented in the **Green Shop** e-commerce backend platform.

---

## 📌 Architecture Overview

```
                                ┌─────────────────────────────────────────────────────────────┐
                                │                    Client Search API Request                │
                                └──────────────────────────────┬──────────────────────────────┘
                                                               │
                                                               ▼
                                                [ 1. Input & Keyword Parsing ]
                                                               │
                                                               ▼
                                                [ 2. Elasticsearch Client Lookup ]
                                                               │
                     ┌─────────────────────────────────────────┴─────────────────────────────────────────┐
                     ▼                                                                                   ▼
       ( Product Full-Text Search )                                                       ( Autocomplete Suggestion )
                     │                                                                                   │
        ┌────────────┴────────────┐                                                         ┌────────────┴────────────┐
        ▼                         ▼                                                         ▼                         ▼
 [ Boosting: Name^3 ]     [ Fuzzy Tolerance ]                                       [ Edge N-Gram Match ]    [ Prefix Suggestions ]
 [ Category^2       ]     [ Filter & Aggs   ]                                       [ Field: name.suggest ]  (Return Top 5 Terms)
 [ Description^1    ]             │
        │                         │
        └────────────┬────────────┘
                     ▼
             ( Return Page Results )

──────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────

                                      [ Async Real-Time Data Synchronization Pipeline ]

 [ Product API Mutation ] ──(Save MySQL)──► [ Kafka Topic: product-sync-topic ] ──► [ Consumer: ProductElasticConsumer ]
                                                                                                    │
                                                                                           (Upsert/Delete Document)
                                                                                                    │
                                                                                                    ▼
                                                                                        [ Elasticsearch Index: products ]
```

---

## 🚀 Key Advanced Elasticsearch Features

### Use Case 1: Advanced E-commerce Product Search Engine (`ProductDocument` & `ProductElasticSearchServiceImpl`)
- **Multi-Field Full-Text Search & Relevancy Boosting**: Assigns higher scoring weights (`name^3`, `categoryName^2`, `description^1`) to matches in product titles over category names and detailed descriptions, ensuring the most relevant products appear first.
- **Vietnamese Language Analyzer (`vietnamese_analyzer`)**: Integrates custom ICU and ASCII folding filters (`asciifolding`, `vietnamese_ascii_folding`) in `product-analyzer.json` to seamlessly handle accented and unaccented Vietnamese search queries (e.g. `"tao do"` matching `"Táo đỏ Organic"`).
- **Fuzzy Search & Typo Tolerance (`fuzziness("AUTO")`)**: Automatically tolerates user spelling errors and typos without returning empty result sets.
- **Instant Autocomplete & Search-as-You-Type**: Evaluates partial prefix inputs (e.g. `"ch"`) against the `name.suggest` sub-field using an `edge_ngram` tokenizer (min 2, max 15 grams) to return up to 5 instant query suggestions before the user finishes typing.
- **Dynamic Faceted Aggregations**: Computes dynamic category distributions (`by_category`) and price statistics (`price_stats` min, max, average) across current query results to populate dynamic UI filter sidebars.
- **Exact Field Filtering & Range Queries**: Combines full-text search with strict boolean filters for category IDs and price range filtering using Elasticsearch 8.x typed `.number()` builders.

---

### Use Case 2: Real-Time Event Synchronization & Batch Reconciliation (`ProductElasticConsumer` & `ProductReindexScheduler`)
- **Decoupled Kafka Streaming**: Product mutations (`CREATE`, `UPDATE`, `DELETE`) publish asynchronous `ProductSyncEvent` messages to the `product-sync-topic` Kafka topic, bypassing blocking database locks.
- **Asynchronous ES Consumer**: `ProductElasticConsumer` consumes Kafka events and executes non-blocking upserts/deletions on the `products` index, maintaining near real-time (NRT) index freshness (< 500ms latency).
- **Chunked Batch Re-indexing (`ProductReindexScheduler`)**: Runs daily at 2:00 AM (`@Scheduled(cron = "0 0 2 * * ?")`), fetching products from MySQL in 500-record chunks to avoid Java Out-Of-Memory (OOM) heap exhaustion.
- **Bulk Save Operations (`saveAll`)**: Batch saves documents to Elasticsearch in single bulk requests, minimizing network I/O round trips and eliminating data drift between MySQL (SSOT) and Elasticsearch.
- **Fault-Tolerant Exception Handling**: Isolates indexing errors to prevent message loss and logs failures for Dead Letter Queue (DLQ) processing.

---

## 🛠️ Configuration Summary

| Use Case | Feature / Component | Class / Config | Benefit |
| :--- | :--- | :--- | :--- |
| **Use Case 1** | Multi-Field Boosting | `ProductElasticSearchServiceImpl` | Ranks title matches highest for superior search relevance. |
| **Use Case 1** | Vietnamese Analyzer | `product-analyzer.json` | Seamlessly handles accented & unaccented Vietnamese search. |
| **Use Case 1** | Edge N-Gram Autocomplete | `ProductDocument` | Delivers sub-millisecond search-as-you-type suggestions. |
| **Use Case 1** | Faceted Aggregations | `ProductElasticSearchServiceImpl` | Dynamically populates UI category & price range filters. |
| **Use Case 2** | Async Event Sync | `ProductElasticConsumer` | Eliminates DB transaction overhead during product updates. |
| **Use Case 2** | Batch Re-indexing | `ProductReindexScheduler` | Guarantees zero data drift between MySQL and Elasticsearch. |
