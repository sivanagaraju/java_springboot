# Project Instructions — spring-mastery Learning Repository

This file is loaded at every agent session. It contains the non-negotiable rules
that govern every file generated in this repository. Read this before anything else.

## Contents

- Mission and Learner Profile
- Stack Constraints (never violate these)
- Three Content Standards (mandatory for every .md file)
- Java Commenting Standard (mandatory for every .java file)
- Python Bridge Rule (mandatory everywhere)
- API-First Mindset
- Staff Engineer Interview Standard
- What Good Looks Like vs What Fails

---

## Mission and Learner Profile

This is a **learning repository** for:
- Background: Python/FastAPI engineer, Data Engineering, 12 years industry experience
- Goal: Transition to Java/Spring Boot at a senior level
- Interview targets: **Amazon, Meta, Google, Microsoft, Netflix, Stripe — Staff Engineer role**
- Build tool: Gradle (Groovy DSL) throughout — never Maven
- Everything must be written at **"Deep Practitioner" (Senior/Staff Engineer)** level

The learner already thinks in systems. Explain WHY Java/Spring does things differently
from Python, not just HOW. Connect every concept to real production consequences.

---

## Stack Constraints (never violate these)

| Constraint | Value |
|---|---|
| Java Version | 21 LTS |
| Spring Boot | 3.2.x (Spring Framework 6) |
| Build Tool | Gradle (Groovy DSL) — never Maven |
| Database | PostgreSQL (Docker) + H2 for unit tests |
| Package Base | `com.learning` |
| Jakarta EE | Always `jakarta.*` — never `javax.*` |
| Spring Security | Lambda DSL only (Spring Security 6) |

---

## Three Content Standards (mandatory for every explanation/*.md file)

### Standard 1 — Mermaid Diagram

Every `explanation/*.md` must have at least one Mermaid diagram. Choose the type
that makes the concept clearest. Supported types:

`flowchart` `sequenceDiagram` `erDiagram` `classDiagram` `stateDiagram-v2`
`C4Context` `C4Container` `C4Component` `timeline` `quadrantChart`
`xychart-beta` `block-beta` `gantt` `journey` `pie` `gitGraph`

**C4 critical rule:** Never use `->` or `-->` inside C4Context, C4Container, or
C4Component blocks. Always use `Rel(from, to, "label")`.
See `workflows/mermaid-c4-fix.md` for the full fix procedure.

### Standard 2 — Markmap MINDMAP.md

Every module gets a `MINDMAP.md` using **pure Markdown lists**.
Do NOT use mermaid mindmap blocks — they do not render in the VS Code Markmap extension.

### Standard 3 — Interview Questions

Every `explanation/*.md` ends with `## Interview Questions` (3–8 per file).
Grouped: **Conceptual → Scenario/Debug → Quick Fire**. Every question has an answer.
Questions must be scenario-based, not definitional. "What is @Transactional?" is forbidden.

---

## Java Commenting Standard (mandatory for every .java file)

Every `.java` file uses exactly 4 comment layers. ASCII diagrams are MANDATORY in
Layers 1, 2, and 3 — Mermaid cannot render inside Java comments.

**Layer 1 — File Header** (`╔══╗` box format):
Fields: FILE, MODULE, GRADLE, PURPOSE, WHY IT EXISTS, PYTHON COMPARE, USE CASES,
ASCII DIAGRAM (of the concept's architecture or flow), HOW TO RUN, EXPECTED OUTPUT,
RELATED FILES.

**Layer 2 — Class-Level Javadoc:**
Responsibility description + Python/FastAPI equivalent in `<pre>` block + ASCII
diagram showing where this class fits in the Spring layered architecture.

**Layer 3 — Method-Level Javadoc (every public method):**
Contract description + Python equivalent + ASCII flow diagram for non-trivial methods
+ `@param` / `@return` / `@throws` tags.

**Layer 4 — Inline Comments (complex logic only):**
Only for non-obvious business rules, gotchas, and "why not the obvious approach" notes.
Never for code that explains itself.

---

## Python Bridge Rule (mandatory everywhere)

Every concept must be compared to its Python/FastAPI equivalent. This is not optional.

In `.java` files: Layer 1 PYTHON COMPARE field, Layer 2 Python `<pre>` block,
Layer 3 Python equivalent in method javadoc.

In `explanation/*.md` files: dedicated `## Python Bridge` section with comparison
table (concept | Python | Java) plus a narrative paragraph explaining the philosophical
difference in approach, not just syntax.

---

## API-First Mindset

Spring Boot is fundamentally an API platform. Every concept must be taught in context
of how it affects API design, behaviour, and client contracts.

When generating any content touching REST, Security, JWT, Microservices, or Data access:
- Show the HTTP request/response in examples (not just internal Java code)
- Explain the client-visible consequence of each design decision
- Include API-level failure modes (what does the client receive when X breaks?)
- Connect to real API standards: REST constraints, HTTP semantics, OpenAPI/Swagger

Key API topics that recur throughout every module:
- Idempotency (POST vs PUT vs PATCH; retry safety; Stripe's idempotency key pattern)
- Error response format (RFC 7807 Problem Details — the standard Netflix, Stripe use)
- Pagination (cursor-based vs offset — why Netflix uses cursor, when offset is fine)
- Versioning (URI /v1/ vs header-based vs media type — trade-offs at scale)
- Rate limiting (token bucket vs sliding window — how API gateways implement it)
- API security (JWT, OAuth2 scopes, API keys — when to use which)

---

## Staff Engineer Interview Standard

This repo is preparation for **Staff Engineer interviews at Amazon, Meta, Google,
Microsoft, Netflix, and Stripe**. Interview content must go beyond "how does X work"
to "how would you design X for 100M users" and "what breaks first and why".

Every module that touches APIs, Security, Data, or Distributed Systems must include
at least one question at the Staff Engineer design/architecture level. Use the
following framing for Staff Engineer questions:

- **Design questions:** "Design a JWT authentication system for a platform with
  100M users. Walk through the trade-offs of stateless vs stateful tokens."
- **Scale questions:** "Your REST API handles 10K RPS. You add a new endpoint that
  accidentally loads all records for a user. What breaks and in what order?"
- **Trade-off questions:** "Netflix moved internal service-to-service communication
  from REST to gRPC. What drove that decision and when would you NOT make that change?"
- **Incident questions:** "A rate limiter bug at Stripe caused legitimate customers
  to be blocked during Black Friday. How do you design a rate limiter that fails open
  safely?"

Companies and their specific API/architecture patterns to reference:
- **Amazon:** SQS idempotency, API Gateway throttling, DynamoDB conditional writes
- **Meta/Facebook:** GraphQL vs REST, TAO (cache layer), Thrift RPC
- **Google:** Protocol Buffers, Cloud Endpoints, SLO/SLI/SLA definitions
- **Microsoft:** Azure API Management, MSAL/OAuth2, long-polling vs webhooks
- **Netflix:** Zuul/API Gateway, Hystrix circuit breaker, Chaos Engineering, gRPC for internal
- **Stripe:** Idempotency keys on POST endpoints, webhook signature verification, API versioning

---

## What Good Looks Like vs What Fails

### Good
- Opens with WHY the concept was invented, not a definition
- Python Bridge section with comparison table AND philosophical difference narrative
- Mermaid diagram that makes the concept visually obvious (right type chosen)
- Interview questions that a Staff Engineer interviewer would actually ask
- API-level consequence explained ("the client receives a 401 because...")
- Real company pattern referenced with specific detail

### Fails
- Opens with "X is a Java feature that..."
- No Python Bridge section
- No Mermaid diagram
- Interview questions that test memorization ("What is @Transactional?")
- Code examples with no HTTP context when the concept is API-facing
- `javax.persistence` instead of `jakarta.persistence`
- Maven references in any form
