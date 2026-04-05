# Spring Data JPA — Top Resource Guide

Curated external resources to master Spring Data JPA and Hibernate beyond this module.

---

## Official Documentation

| Resource | URL | Best For |
|---|---|---|
| Spring Data JPA Reference | https://docs.spring.io/spring-data/jpa/docs/current/reference/html/ | Complete reference for repositories, queries, auditing |
| Hibernate ORM User Guide | https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/ | Deep Hibernate internals: caching, dirty checking, N+1 |
| Jakarta Persistence API (JPA 3.1) | https://jakarta.ee/specifications/persistence/3.1/ | Spec-level reference for annotations and lifecycle |

---

## Books

| Title | Author | Why Read It |
|---|---|---|
| *High-Performance Java Persistence* | Vlad Mihalcea | The definitive book on JPA performance. N+1, caching, batch inserts, fetch strategies. Staff-level required reading. |
| *Spring Data: Modern Data Access for Enterprise Java* | Mark Pollack et al. | Covers all Spring Data modules including JPA, JDBC, MongoDB |
| *Pro JPA 2 in Java EE 8* | Mike Keith & Merrick Schincariol | Deep JPA specification coverage — entity lifecycle, relationships, JPQL |

---

## Blog Posts & Articles

| Title | Source | Why Read It |
|---|---|---|
| "The Best Way to Map @OneToMany" | Vlad Mihalcea (vladmihalcea.com) | Explains bidirectional vs unidirectional, cascade mistakes, batch inserts |
| "N+1 Query Problem with JPA and Hibernate" | Vlad Mihalcea | Complete N+1 analysis with JPQL, EntityGraph, and JOIN FETCH fixes |
| "Spring Data JPA Tutorial" | Baeldung | Comprehensive derived queries, @Query, pagination coverage |
| "JPA Pitfalls: Common Mistakes and How to Fix Them" | Thorben Janssen (thoughts-on-java.org) | Best practical anti-pattern coverage |
| "Hibernate Performance Tuning" | Thorben Janssen | Practical: batch fetching, query plans, slow query analysis |

Search: "site:vladmihalcea.com" for the definitive deep-dives on JPA internals.

---

## Videos

| Title | Channel | Why Watch |
|---|---|---|
| *Hibernate & Spring Data JPA* (full course) | Amigoscode (YouTube) | Project-based, covers entity mapping through advanced JPQL |
| *Spring Data JPA Tutorial* | Java Brains (YouTube) | Concept-first, clear explanation of derived queries and repositories |
| *JPA Performance Anti-Patterns* | Vlad Mihalcea (YouTube) | Conference talks on N+1, batch insert optimization |

---

## Tools

| Tool | Purpose |
|---|---|
| `spring.jpa.show-sql=true` | Print all SQL to console (development only) |
| `spring.jpa.properties.hibernate.format_sql=true` | Pretty-print SQL |
| `logging.level.org.hibernate.type=TRACE` | Log bound parameter values |
| p6spy | SQL logging with timing (production-safe alternative) |
| Hibernate Statistics | `spring.jpa.properties.hibernate.generate_statistics=true` |
