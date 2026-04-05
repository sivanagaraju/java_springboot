# Quiz Standards — spring-mastery

All quizzes, self-assessments, and interview prep documents follow these standards.

## Contents

- File Locations by Output Type
- Five Question Sections (all required)
- Answer Key Format
- Question Quality Rubric
- Interview Prep Doc Format
- Phase Assessment Format
- What Immediately Fails the Standard

---

## File Locations by Output Type

| Output type | File location | Naming convention |
|------------|--------------|------------------|
| Module quiz | `XX-module/resources/quiz.md` or `exercises/quiz.md` | `quiz.md` |
| Progressive quiz drill | `XX-module/YY-subtopic/resources/progressive-quiz-drill.md` | `progressive-quiz-drill.md` |
| Interview prep | `resources/interview-prep/<topic>.md` | `spring-security.md`, `jpa-hibernate.md` |
| Phase assessment | `resources/assessments/phase-N-check.md` | `phase-2-check.md` |

---

## Five Question Sections (all required for a module quiz)

### Section 1 — Conceptual (minimum 5 questions)

Tests WHY understanding — not memorisation. Questions must start with:
"Why does...", "What problem does... solve?", "What is the difference between...
and why does the difference matter?", "When would you choose X over Y?"

Format:
```markdown
**Q1.** Why does `@Transactional` use an AOP proxy instead of modifying
your class directly?

**Q2.** What problem does HikariCP solve that `DriverManager.getConnection()`
does not?
```

### Section 2 — Code Reading (minimum 3 questions)

Show a code snippet. Ask the learner to predict what it does, what output it
produces, or whether it will compile.

```markdown
**Q6.** What does this code print? Explain why.

```java
@Service
public class OrderService {
    @Transactional
    public void process(Long id) {
        this.validate(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void validate(Long id) {
        System.out.println("Validating in new transaction: " + id);
    }
}
```

A) "Validating in new transaction: 123" in a separate transaction
B) "Validating in new transaction: 123" in the same transaction as process()
C) A compilation error
D) A NullPointerException
```

### Section 3 — Spot the Bug (minimum 3 questions)

Show broken code. The learner must identify WHAT is wrong and WHY it is wrong.
The bug must be real and production-relevant — not a typo.

```markdown
**Q9.** This code has a bug that causes silent data loss in production.
Identify the bug and explain what happens.

```java
@Transactional(readOnly = true)
public Product updatePrice(Long id, BigDecimal newPrice) {
    Product product = repo.findById(id).orElseThrow();
    product.setPrice(newPrice);
    return product;
}
```
```

### Section 4 — Scenario / Debug (minimum 3 questions)

A production situation is described. The learner must diagnose or design a solution.

```markdown
**Q12.** A senior engineer reports that a bank transfer passes all unit tests
but occasionally fails in production — sometimes money is debited from one
account but not credited to the other. What is the most likely cause and
how do you fix it?
```

### Section 5 — Quick Fire (minimum 5 questions)

Short factual questions. One-line answers. Used for "reflexes" testing.

```markdown
**Q15.** Is `EntityManager` thread-safe?
**Q16.** What is the default fetch type for `@OneToMany`?
**Q17.** Can `@Transactional` be placed on a private method? What happens?
**Q18.** What Spring Boot property disables Hibernate schema DDL auto-generation?
**Q19.** What does `spring.jpa.open-in-view=false` do?
```

---

## Progressive Quiz Drill Format

Use this format when a sub-topic needs a staged self-check before or alongside the
main quiz. It is shorter than a full module quiz, but it must still feel progressive.

Recommended structure:
- Round 1 â€” Core recall: 4â€“5 questions
- Round 2 â€” Apply and compare: 3â€“4 questions
- Round 3 â€” Debug and failure modes: 3â€“4 questions
- Round 4 â€” Staff-level scenario: 2â€“3 questions

Rules:
- Total length is usually 10â€“15 questions
- Answers must stay in a single Answer Key at the end
- Difficulty must increase from round to round
- Use real module concepts and avoid trivia
- If the sub-topic is being deeply expanded, generate this file plus the one-page
  cheat sheet and top resource guide in the same sub-topic `resources/` folder
- `top-resource-guide.md` must be a curated external learning list only: books,
  official docs, blogs, and videos. Do not use internal repo links as the primary
  content of that file.

---

## Answer Key Format

Always placed at the END of the file, clearly separated:

```markdown
---

## Answer Key

> ⚠️ Do not scroll to this section until you have answered all questions.

### Section 1 — Conceptual

**A1.** Spring uses AOP proxy because...
[Full answer — 3–5 sentences minimum for conceptual questions]

**A2.** HikariCP solves the connection cost problem...
[Full answer]

...

### Section 2 — Code Reading

**A6.** Answer: **B** — "Validating in new transaction" but in THE SAME transaction.
Explanation: `this.validate(id)` is a self-invocation — it calls the method directly
on the `this` reference, bypassing Spring's AOP proxy entirely. The
`REQUIRES_NEW` propagation setting is irrelevant because the proxy is never
involved in the call. The output appears normally, but no new transaction is created.
To get the REQUIRES_NEW behaviour, extract `validate()` to a separate Spring bean.

...

### Section 3 — Spot the Bug

**A9.** Bug: `@Transactional(readOnly=true)` on a write method.
`readOnly=true` disables Hibernate dirty checking. The `product.setPrice(newPrice)`
mutation is applied in memory, but Hibernate never generates the UPDATE SQL because
it skips the dirty-check at flush time. The method returns the in-memory modified object,
giving the impression the update worked. The database row is unchanged.
Fix: Remove `readOnly=true` — use default `@Transactional` for write methods.

...

### Section 5 — Quick Fire

**A15.** No — EntityManager is NOT thread-safe. Each request/transaction needs its own instance.
**A16.** LAZY
**A17.** Nothing happens. @Transactional on private methods is silently ignored — Spring's AOP proxy cannot intercept private methods.
**A18.** `spring.jpa.hibernate.ddl-auto=none` (or `validate` to check schema matches entities)
**A19.** Closes the Hibernate session at the end of the transaction boundary (inside the service), not at the end of the HTTP request. Prevents lazy loading in view/controller layers — forces proper DTO mapping.
```

---

## Interview Prep Doc Format

For `resources/interview-prep/<topic>.md`:

```markdown
# Spring Security — Interview Preparation

> Aggregated from all `explanation/*.md` files in `10-spring-security/`.
> Questions are grouped by seniority level. Work through Foundation first.

## Foundation (Junior / 1–2 years Spring)

Questions that test whether the candidate has used Spring Security, not just read about it.

**Q1.** What is the Spring Security filter chain and why does it run before your controllers?
> [Answer]

...

## Intermediate (Mid-level / 3–5 years)

**QN.** [More specific, mechanism-focused questions]

...

## Senior (6–10 years, has designed security architectures)

**QN.** [Architecture and trade-off questions]

...

## Principal / Staff (cross-team, platform-level)

**QN.** [Org-level design questions, failure-mode analysis]

...

## Quick Reference — Common Gotchas

| Gotcha | Correct behaviour |
|--------|------------------|
| @PreAuthorize on private method | Silently ignored |
| Self-invocation with @Transactional | Proxy bypassed — transaction not applied |
| hasRole("ADMIN") vs hasAuthority("ROLE_ADMIN") | Different — hasRole adds ROLE_ prefix automatically |
```

---

## Phase Assessment Format

For `resources/assessments/phase-N-check.md`:

```markdown
# Phase 2 Assessment — Gradle + JDBC + Hibernate

Complete this assessment after finishing Phase 2 modules.

## Self-Scoring Rubric

| Score | Interpretation |
|-------|---------------|
| 40/45+ | Ready to move to Phase 3 |
| 30–39 | Review the modules you missed, then re-test |
| <30 | Re-read Phase 2 explanation files before proceeding |

## Time Limit Suggestion

Give yourself 60 minutes. If you spend more than 5 minutes on a single question,
mark it and move on — come back after finishing the rest.

[Questions follow in the same 5-section format]
```

---

## What Immediately Fails the Standard

| Failure | Standard violated |
|---------|------------------|
| Answers appear inline next to questions | Answer visibility rule |
| "Spot the Bug" has no real bug | Question quality |
| Code snippets have syntax errors | Code quality |
| Conceptual questions test memorisation ("What is X?") | Question quality |
| No Answer Key section | Format requirement |
| Fewer than 5 Quick Fire questions | Section minimum |
| `javax.*` in any code snippet | Stack constraint |
| Answer Key answers are one sentence for conceptual questions | Depth requirement |
