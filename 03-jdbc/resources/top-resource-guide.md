# JDBC Top Resource Guide

This file is for outside resources only: books, official docs, blogs, and videos for JDBC, drivers, SQL access, and pooling.

```mermaid
flowchart LR
    A[Books] --> B[Official Docs]
    B --> C[Blogs and Articles]
    C --> D[Videos]
```

## Books

| Resource | Why it helps |
|---|---|
| JDBC API Tutorial and Reference | classic JDBC-focused grounding |
| High-Performance Java Persistence by Vlad Mihalcea | useful even in JDBC-first work because it explains SQL and connection costs clearly |
| PostgreSQL Up and Running | practical database-side context for JDBC learners using Postgres |

## Official Docs

| Resource | Why it helps |
|---|---|
| Java SE JDBC API docs | primary API contract for connection, statements, and result sets |
| PostgreSQL JDBC driver docs | important for driver behavior, connection options, and tuning |
| HikariCP documentation | primary source for pooling configuration and operational advice |

## Blogs and Articles

| Resource | Why it helps |
|---|---|
| Baeldung JDBC articles | approachable explanations for statement, result set, and transaction basics |
| Vlad Mihalcea blog | strong for connection and SQL performance thinking |
| HikariCP wiki and issue discussions | useful when tuning pools or understanding limits |

## Videos

| Resource | Why it helps |
|---|---|
| database access talks from Devoxx / Spring I/O | helpful for practical persistence and connection lessons |
| PostgreSQL conference talks on performance | useful for the database side of JDBC decisions |
| JDBC and connection-pool walkthroughs on Java Brains | good for learners bridging from framework use to lower-level mechanics |

## Suggested Order

1. Learn the JDBC API surface from the official docs.
2. Pair that with a practical JDBC or database book.
3. Use blogs and videos when you need operational intuition about transactions or pools.

## Interview Questions

1. Why should JDBC learners study both the Java API and the database-side behavior?
2. Which external resource type is most useful when debugging pooling problems: docs, blogs, books, or videos?
