# Content Standards — spring-mastery Explanation Files

Every `explanation/*.md` file must satisfy ALL items in this checklist before delivery.

## Contents

- Mandatory Sections (in order)
- Python Bridge Format Rules
- Mermaid Diagram Requirements
- Interview Questions Format
- Real-World Use Cases Standard
- Anti-Patterns Format
- What Immediately Fails the Standard

---

## Mandatory Sections (in order)

### 1. Opening Explanation — WHY First

Do not open with a definition. Open with WHY the concept was invented — what problem
existed before it, what was painful about the old way. Then define what it is.

Bad opening: "JDBC is Java's standard API for connecting to relational databases."
Good opening: "Before JDBC, every Java application that needed a database had to use a
vendor-specific, proprietary API. Switching from Oracle to MySQL meant rewriting all your
database code. JDBC was created to end this — one standard interface, every database vendor
writes a driver to implement it."

Minimum length: 3 substantial paragraphs.

### 2. Python Bridge

Immediately after the opening explanation. Show the concept's Python/FastAPI equivalent.
Must include:
- A comparison table (concept | Python | Java)
- A narrative paragraph explaining the KEY difference in mental model, not just syntax

### 3. Mermaid Diagram

At least one. Choose the type that makes the concept most clear. See diagram type
guide below. Multiple diagrams encouraged for complex topics.

### 4. Working Java Code

Runnable Java code with 4-layer commenting standards applied (even in .md files,
show the commenting pattern so readers learn it). Use Java 21 features where relevant.
No pseudocode unless clearly labelled. No `javax.*` — use `jakarta.*`.

### 5. Real-World Use Cases

Minimum 2. Name the industry vertical or company type and describe the specific pattern:
- "E-commerce: Stripe uses connection pooling (HikariCP) to cap Postgres connections
  at 20 per service pod. Without this, 50 concurrent checkouts would open 50 connections,
  exhausting the database's `max_connections` limit."
- "Banking: Every fund transfer in systems like Robinhood runs inside `@Transactional` —
  if the debit succeeds but the credit fails (rare network partition), the entire operation
  rolls back atomically. Without this, the money would simply disappear."

Do NOT write "Many companies use X for Y." Name the domain, describe the specific consequence.

### 6. Anti-Patterns (minimum 3)

Each anti-pattern must follow this format:
1. **Name** (bold heading)
2. What developers mistakenly do (code snippet)
3. Why it causes problems in production
4. The correct approach (code snippet)

### 7. Interview Questions

See Interview Questions Format section below.

---

## Python Bridge Format Rules

Always present as a two-part structure:

**Part 1 — Comparison table:**
```markdown
| Concept | Python / FastAPI | Java / Spring |
|---------|-----------------|--------------|
| [row]   | [Python form]   | [Java form]  |
```

**Part 2 — Mental model narrative:**
One paragraph explaining the philosophical or architectural difference — not just
that the syntax is different, but WHY Java does it differently and what problem
that solves for large-scale, enterprise codebases.

---

## Mermaid Diagram Requirements

Pick the type that makes the concept most clear:

| Concept type | Best Mermaid type |
|-------------|------------------|
| Request flows, auth flows, service calls | `sequenceDiagram` |
| Decision logic, algorithm steps | `flowchart TD` |
| Entity relationships, DB schema | `erDiagram` |
| Class hierarchies, OOP structures | `classDiagram` |
| Bean lifecycle, entity states, circuit breaker | `stateDiagram-v2` |
| System architecture | `C4Context` |
| Container view (app + DB + Redis) | `C4Container` |
| Internal layers (Controller + Service + Repo) | `C4Component` |
| Tech evolution, Spring version history | `timeline` |
| Performance data, benchmarks | `xychart-beta` |

**C4 Rule:** Use `Rel(from, to, "label")` not `->` inside any C4 block.

---

## Interview Questions Format

```markdown
---

## Interview Questions

### Conceptual

**Q1: [Tests understanding of WHY the concept exists]**
> [2–3 sentence answer covering origin problem + solution]

**Q2: [Tests understanding of difference between X and Y]**
> X does ___. Y does ___. Choose X when ___, Y when ___.

### Scenario / Debug

**Q3: [A production bug or unexpected behaviour scenario]**
> [Diagnostic reasoning — 3 most likely causes, step-by-step]

**Q4: [A design decision scenario]**
> [Trade-off reasoning with recommendation]

### Quick Fire

- [Short question]? *([One-line answer])*
- [Short question]? *([One-line answer])*
- [Short question]? *([One-line answer])*
```

Rules:
- Minimum 3, maximum 8 questions per file
- Groups MUST appear in order: Conceptual → Scenario/Debug → Quick Fire
- Every question has an answer — no unanswered questions
- Questions must be scenario-based or design-based — never definitional
  ("What is @Transactional?" is definitional and forbidden)

---

## Real-World Use Cases Standard

Structure each use case as: **Domain → Problem → How This Concept Solves It → Consequence if Skipped**

Example structure:
"In **fintech systems** (Robinhood, Stripe), every payment operation runs inside
`@Transactional`. The problem: a fund transfer involves two writes — debit and credit.
If the debit commits but the credit fails due to a transient database error, money
disappears. `@Transactional` wraps both writes in one atomic unit: if either fails,
both roll back. Without this guarantee, financial reconciliation would be required
after every outage."

---

## What Immediately Fails the Standard

| Failure | Standard Violated |
|---------|------------------|
| No Mermaid diagram | Standard 1 — mandatory |
| `->` inside C4Context/C4Container/C4Component | Standard 1 — C4 rule |
| No Python Bridge section | Standard 2 — mandatory |
| No Interview Questions section | Standard 3 — mandatory |
| Fewer than 3 interview questions | Standard 3 — minimum |
| Questions not grouped correctly | Standard 3 — format |
| Any unanswered question | Standard 3 — every Q has A |
| "Many companies use X" (no specific domain/consequence) | Use cases standard |
| Fewer than 3 anti-patterns | Anti-patterns standard |
| `javax.persistence` imports | Stack constraint — Jakarta EE 10 |
| Maven references | Stack constraint — Gradle only |
| MINDMAP.md uses mermaid mindmap block | Standard 2 — must be Markdown lists |
| Opening with a dry definition instead of WHY | Content quality standard |
