---
name: designing-api-systems
description: Generates API architecture explanations, system design content, and
  staff engineer interview preparation for the spring-mastery repository. Use when
  explaining REST API design, JWT auth systems, rate limiting, idempotency, API
  versioning, microservice communication, or any system design topic. Also triggers
  when generating interview prep for Amazon, Meta, Google, Microsoft, Netflix, or
  Stripe staff engineer roles. Triggers on phrases like "explain API design for X",
  "write the JWT deep dive", "generate staff engineer questions for", "design a
  system for", "what would Netflix/Stripe/Amazon do for X", "explain rate limiting",
  "write the API architecture section".
---

# Designing API Systems

## Quick start

1. Read [references/api-design-principles.md](references/api-design-principles.md) — REST, HTTP semantics, idempotency, pagination, versioning
2. Read [references/staff-engineer-patterns.md](references/staff-engineer-patterns.md) — company-specific patterns (Amazon/Meta/Google/Netflix/Stripe)
3. Read [references/jwt-oauth2-deep-dive.md](references/jwt-oauth2-deep-dive.md) — stateless auth, token lifecycle, security pitfalls
4. Study [examples/jwt-system-design.md](examples/jwt-system-design.md) — gold-standard staff engineer answer

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
API Systems Content Progress:
- [ ] Step 1: Identify topic area (REST design / JWT / OAuth2 / rate limiting / versioning / microservices)
- [ ] Step 2: Read the relevant reference file for the topic
- [ ] Step 3: Write WHY this concept exists (not a definition)
- [ ] Step 4: Write Python/FastAPI bridge comparison
- [ ] Step 5: Add Mermaid diagram (sequenceDiagram for flows, C4Container for systems)
- [ ] Step 6: Show concrete Spring Boot implementation (annotated Java code)
- [ ] Step 7: Show HTTP request/response examples (not just internal Java)
- [ ] Step 8: Write real-world company pattern (Amazon/Stripe/Netflix/Meta/Google)
- [ ] Step 9: Write production failure modes (what breaks and what the client sees)
- [ ] Step 10: Write Interview Questions (Senior + Staff Engineer level)
- [ ] Step 11: Add system design question with full worked answer (if applicable)
```

## Feedback loop

After generating, verify:
- Is there at least one HTTP request/response example showing the client's perspective?
- Does the content go beyond "how it works in Spring" to "how you'd design it at scale"?
- Is there at least one named company pattern with specific implementation details?
- Does the Interview Questions section include at least one Staff Engineer design question?

Fix any failure before finishing.

## Mandatory elements for every API topic

### 1. Client perspective first
Show what the API looks like from outside — HTTP method, URL, headers, request body,
response body, error responses. Then explain how Spring Boot implements it internally.
Never explain the internal implementation without showing the external contract.

### 2. HTTP semantics, not just Spring annotations
- GET must be idempotent and safe — explain consequences if it's not
- POST is not idempotent — explain why this matters for retries
- PUT is idempotent, PATCH is not guaranteed — explain Stripe's approach
- 4xx vs 5xx — explain what each means for the client's retry logic

### 3. Production failure modes
Every API topic must address: "what happens when this breaks in production, and what
does the client experience?" This is the difference between theory and engineering.

### 4. Company-specific patterns
Use real patterns from the target companies. See references/staff-engineer-patterns.md.
Do not write "companies use X" — name the company, name the specific design decision.

### 5. Staff Engineer design questions
Every content file that touches system design must include at least one question
framed at the Staff Engineer level — design for scale, failure modes, trade-offs,
alternatives considered and rejected.

## Topic routing — which reference to read first

| Topic | Read first |
|-------|-----------|
| REST principles, HTTP methods, status codes | [references/api-design-principles.md](references/api-design-principles.md) |
| JWT, refresh tokens, token security | [references/jwt-oauth2-deep-dive.md](references/jwt-oauth2-deep-dive.md) |
| Rate limiting, idempotency, pagination design | [references/api-design-principles.md](references/api-design-principles.md) |
| System design (auth system, API gateway, etc.) | [references/staff-engineer-patterns.md](references/staff-engineer-patterns.md) |
| Microservice communication (REST vs gRPC vs messaging) | [references/staff-engineer-patterns.md](references/staff-engineer-patterns.md) |
| API versioning strategies | [references/api-design-principles.md](references/api-design-principles.md) |

## Do not use this skill when

- Writing explanation files about Spring internals with no API angle → use `generating-java-content`
- Writing `.java` demo files without system design context → use `generating-java-demos`
- Writing `MINDMAP.md` files → use `generating-java-mindmaps`
- Writing exercise files → use `writing-java-exercises`
- Building mini-project scaffolds → use `building-java-miniprojects`
