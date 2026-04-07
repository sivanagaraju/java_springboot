# 01-Hibernate-Basics — Top Resource Guide

External learning resources only. No internal repo links.

## Official Documentation

- **Hibernate 6 User Guide** — Chapters 2 (Domain Model), 3 (Bootstrap), 4 (Persistence Contexts)
  https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html

- **Jakarta Persistence 3.1 Specification** — Sections 2 (Entities), 3 (Entity Operations)
  https://jakarta.ee/specifications/persistence/3.1/jakarta-persistence-spec-3.1.html

## Books

- **"Java Persistence with Spring Data and Hibernate"** by Catalin Tudose (Manning, 2023)
  — Best book for Spring Boot context. Chapters 3-6 cover everything in this sub-module.
  — Focus: entity mapping, lifecycle, CRUD with JPA + Spring Data

- **"High-Performance Java Persistence"** by Vlad Mihalcea (leanpub, 2016)
  — The definitive Hibernate performance book. Start with Part 1 (Persistence Context).
  — https://vladmihalcea.com/books/high-performance-java-persistence/

## Blogs — Highest Signal

- **Vlad Mihalcea's blog** (vladmihalcea.com) — authoritative, written by a former Hibernate team member
  - "A beginner's guide to entity state transitions with JPA and Hibernate"
  - "The best way to use the JPA GenerationType.AUTO with Hibernate"
  - "How does the JPA @Transient annotation work"

- **Thorben Janssen** (thoughts-on-java.org)
  - "5 things you need to know when using the JPA @Transient annotation"
  - "Hibernate Tips: What's the difference between persist, save, merge and update?"

## Videos

- **Thorben Janssen YouTube channel** — "JPA & Hibernate" playlist
  Good visual walkthroughs of entity lifecycle and annotations.

## Concepts Checklist — Before Moving to 02-Relationships

- [ ] Can draw the 4 entity lifecycle state transitions from memory
- [ ] Know the difference between `persist()` and `merge()`
- [ ] Understand why `EnumType.STRING` not `ORDINAL`
- [ ] Can explain what `hbm2ddl.auto=create` does to a production database
- [ ] Understand dirty checking — no explicit `save()` needed for persistent entities
- [ ] Can explain what `@Transient` does and when to use it
- [ ] Know why SessionFactory is heavyweight (one per app) but Session is lightweight
