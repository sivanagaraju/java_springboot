# Interview Question Bank — spring-mastery

Templates and depth guidelines for Senior / Principal Engineer interview questions.
Use this to calibrate the Interview Questions section of every explanation file.

## Contents

- The Three Interviewer Personas
- Question Quality Rubric (Bad vs Good)
- Question Patterns by Topic Area
- Answer Depth Guide
- Quick Fire Question Templates

---

## The Three Interviewer Personas

### Senior Engineer (3–6 years Spring experience)
Tests: "Have you actually run this in production? Do you understand the failure modes?"
Red flags: Textbook answers with no failure scenario awareness, not knowing where
to look when something breaks, answering "what" without knowing "why".

### Lead Engineer (6–10 years, team decision-maker)
Tests: "Can you make architectural decisions your team can build on?"
Red flags: Over-engineering, inability to justify choices, no opinion on when
NOT to use a pattern, can't explain trade-offs.

### Principal / Staff Engineer (10+ years, cross-team impact)
Tests: "Can you change how the organisation builds software?"
Red flags: Local optimization thinking, no awareness of org-level costs,
solutions that work for one service but fail across a platform.

---

## Question Quality Rubric

### FORBIDDEN — Definitional questions (never write these)
- "What is @Transactional?"
- "What does @Autowired do?"
- "What is JPA?"
- "Name 3 features of Spring Boot"

These test memorisation, not engineering competence.

### GOOD — Senior level (scenario-based)
- "Your @Transactional method is not rolling back on exception. Give me the
  3 most likely causes and how you'd diagnose each."
- "A developer on your team is getting LazyInitializationException in production
  but not in tests. Walk me through the 2 root causes of this."
- "Your service has a memory leak. Heap dumps show lots of `$Proxy` objects.
  What caused this and how do you fix it?"

### GOOD — Lead/Principal level (design + architecture)
- "You have 50 engineers writing Spring Boot services. 6 months in, you notice
  inconsistent error handling, different transaction strategies, and varied
  security patterns. What do you build to address this systemically?"
- "A `@Transactional` annotation is on a private method. The developer swears
  the transaction isn't working. Without looking at the code, explain the 2
  mechanisms that could cause this."
- "We need to add request logging to every endpoint across 30 microservices.
  What are your 3 approaches, ranked by team overhead and correctness?"

---

## Question Patterns by Topic Area

### Spring IoC / DI
**Senior:** "You have a circular dependency between ServiceA and ServiceB.
What are the 3 ways to resolve it and which is preferred?"
> 1. Refactor to extract a third service (best — fixes design flaw)
> 2. Use `@Lazy` on one injection point (workaround)
> 3. Use setter injection instead of constructor injection (workaround)
> Constructor injection intentionally fails fast on circular deps — that's a feature.

**Lead:** "Your team uses field injection everywhere (@Autowired on fields). You want
to move to constructor injection. How do you migrate 200 classes safely?"
> 1. Enable Checkstyle/PMD rule to flag new field injections
> 2. Add Lombok @RequiredArgsConstructor to each class incrementally
> 3. Remove @Autowired field + add final field — tests verify correctness

### JPA / Hibernate
**Senior:** "You're getting N+1 queries in production. How do you diagnose and fix it?"
> Enable `spring.jpa.show-sql=true` or add a query count assertion in tests.
> N+1 happens when you load a collection lazily inside a loop. Fix with:
> `@EntityGraph`, `JOIN FETCH` in JPQL, or `@BatchSize(size=50)`.

**Senior:** "A developer sets `@Transactional(readOnly=true)` on a write method.
What happens?"
> With Hibernate: dirty checking is disabled (performance gain). But if the method
> does write, the changes may silently not be flushed. Some databases also reject
> writes on read-only connections. It causes a silent data loss bug.

**Lead:** "You need to process 1 million rows from a database. What are your options
and what are the failure modes of each?"
> 1. Load all into memory → OutOfMemoryError for large datasets
> 2. Spring Batch with chunked reader → correct, adds dependency
> 3. `Stream<Entity>` from Spring Data → requires open Hibernate session for full stream
> 4. Pagination with `Pageable` → N round trips, risk of data shifting between pages

### Spring Security
**Senior:** "JWT is accepted but the user gets 403. What are the 5 things you check?"
> 1. Token has the right role/authority claim
> 2. Role matches `hasRole("ADMIN")` vs `hasAuthority("ROLE_ADMIN")` prefix issue
> 3. SecurityContext not populated (filter not running before auth check)
> 4. CORS pre-flight OPTIONS request failing
> 5. Method-level `@PreAuthorize` conflicting with URL-level config

**Lead:** "When should you use Spring Security method security (@PreAuthorize) vs
URL security (requestMatchers)?"
> URL security: coarse-grained, for public/private route patterns.
> Method security: fine-grained, for row-level or field-level access control.
> Rule: use URL security as the first gate, method security for business rules.
> Never rely only on method security — requires AOP proxy (fails on self-invocation).

### @Transactional
**Senior:** "A @Transactional method calls another @Transactional method in the SAME
class. Will the inner method run in its own transaction?"
> No. Spring's @Transactional works via AOP proxy. When a method calls another method
> on `this`, the proxy is bypassed — the inner method runs in the outer transaction
> regardless of its Propagation setting. Fix: extract to a separate Spring bean.

**Lead:** "What is the performance impact of @Transactional(readOnly=true)?"
> Hibernate disables dirty checking (no need to compare entity snapshots at flush).
> The JDBC driver may set the connection to read-only, allowing DB-level optimizations.
> For heavy read endpoints, this can reduce CPU 10–30%.

### Testing
**Senior:** "What's the difference between @SpringBootTest and @WebMvcTest?"
> @SpringBootTest loads the full Spring context — slow, tests everything.
> @WebMvcTest loads only the web layer (controllers, filters, converters) — fast,
> services must be mocked with @MockBean. Use @WebMvcTest for controller unit tests,
> @SpringBootTest for integration tests.

---

## Answer Depth Guide

### Senior level answer (5–6 sentences)
1. Direct answer (1 sentence)
2. WHY this happens — the mechanism (2 sentences)
3. How you diagnose/observe it (1 sentence)
4. The fix + why the fix works (1–2 sentences)

### Lead/Principal level answer (8–12 sentences)
1. Frame the architectural concern, not just the technical detail
2. Present 2–3 options with explicit trade-offs
3. State which you'd recommend and under what conditions
4. Address what changes at scale or over time
5. Note what you'd monitor after implementing

---

## Quick Fire Question Templates

Quick Fire questions are short, factual, and fast — no multi-sentence answers needed.
Format exactly as:
```markdown
### Quick Fire

- Is `EntityManager` thread-safe? *(No — each thread/request needs its own)*
- What is the default fetch type for `@ManyToOne`? *(EAGER)*
- What is the default fetch type for `@OneToMany`? *(LAZY)*
- Can you use `@Transactional` on a private method? *(No — AOP proxy can't intercept it)*
- What does `spring.jpa.hibernate.ddl-auto=validate` do? *(Validates schema matches entities; fails startup if not)*
```

Always have 3–5 quick fire questions. They test the "reflexes" — the things a
practitioner knows without needing to think about them.
