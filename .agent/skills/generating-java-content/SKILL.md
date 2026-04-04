---
name: generating-java-content
description: Generates deep-practitioner explanation markdown files for the spring-mastery
  learning repository. Use when writing or creating any explanation/*.md file for a
  Java, Spring Boot, Hibernate, JDBC, or Spring Security concept. Triggers on phrases
  like "write explanation for", "create the content for", "explain X in detail",
  "write the md file for module X", "document how X works".
---

# Generating Java Content

## Quick start

1. Read [references/content-standards.md](references/content-standards.md) — all 3 mandatory standards
2. Read [references/python-bridge-patterns.md](references/python-bridge-patterns.md) — comparison lookup table
3. Read [references/interview-question-bank.md](references/interview-question-bank.md) — question depth guide
4. Study [examples/good-explanation.md](examples/good-explanation.md) — gold-standard output
5. Check [examples/bad-explanation.md](examples/bad-explanation.md) — what to avoid

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
Content Generation Progress:
- [ ] Step 1: Read content-standards.md
- [ ] Step 2: Read python-bridge-patterns.md for this topic area
- [ ] Step 3: Read interview-question-bank.md
- [ ] Step 4: Write concept explanation (opening paragraph + WHY it exists)
- [ ] Step 5: Write Python Bridge section (comparison table + narrative)
- [ ] Step 6: Add at least one Mermaid diagram (choose correct type)
- [ ] Step 7: Add working Java code with inline comments
- [ ] Step 8: Add real-world use cases (named companies or project types)
- [ ] Step 9: Add anti-patterns / common mistakes section
- [ ] Step 10: Write Interview Questions (Conceptual + Scenario/Debug + Quick Fire)
- [ ] Step 11: Self-review — does every section exist? Is C4 using Rel()?
```

## Feedback loop

After generating, self-review against the "What Immediately Fails the Standard"
table in [references/content-standards.md](references/content-standards.md).
Fix any failures before finishing. Do not deliver a file that fails even one check.

## Mandatory section order

1. Opening: what this concept is + WHY it was invented (not just what it does)
2. Python Bridge — comparison to Python/FastAPI equivalent
3. Mermaid diagram (pick the type that best shows the concept)
4. Working Java code with 4-layer commenting standards applied
5. Real-world use cases (minimum 2 — name the industry/company type and specific pattern)
6. Anti-patterns and common mistakes (minimum 3, each with the correct fix)
7. Interview Questions (Conceptual → Scenario/Debug → Quick Fire)

## Hard rules

- Every file must have a Mermaid diagram — no exceptions
- Every file must have a Python Bridge section — no exceptions
- Every file must end with Interview Questions — no exceptions
- C4 diagrams: use `Rel()` not `->` — see [references/mermaid-c4-fix.md](../enforcing-java-standards/references/mermaid-c4-fix.md)
- Never use `javax.persistence` — always `jakarta.persistence` (Jakarta EE 10)
- Never reference Maven — Gradle only
- MINDMAP.md uses pure Markdown lists — never mermaid mindmap blocks

## Do not use this skill when

- Writing `.java` demo files → use `generating-java-demos`
- Writing `MINDMAP.md` files → use `generating-java-mindmaps`
- Building mini-project scaffolds → use `building-java-miniprojects`
- Writing exercise files → use `writing-java-exercises`
- Generating quizzes or interview prep docs → use `generating-quiz-assessments`
