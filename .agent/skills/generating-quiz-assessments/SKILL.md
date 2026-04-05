---
name: generating-quiz-assessments
description: Generates quizzes, self-assessments, and aggregated interview preparation
  documents for the spring-mastery learning repository. Use when creating a self-check
  quiz for a module, building the resources/interview-prep/ topic files, or generating
  a knowledge check after completing a phase. Triggers on phrases like "generate quiz
  for module X", "create self-assessment for phase X", "build interview prep for topic X",
  "create the quiz for X", "aggregate interview questions for X".
---

# Generating Quiz Assessments

## Quick start

1. Read [references/quiz-standards.md](references/quiz-standards.md) — format, question types, answer format
2. Study [examples/quiz-jdbc-fundamentals.md](examples/quiz-jdbc-fundamentals.md) — gold-standard quiz output

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
Quiz Generation Progress:
- [ ] Step 1: Read quiz-standards.md
- [ ] Step 2: Identify output type (module quiz / interview-prep doc / phase assessment)
- [ ] Step 3: Identify all explanation/*.md files the quiz should cover
- [ ] Step 4: Write Section 1 — Conceptual (WHY questions, min 5)
- [ ] Step 5: Write Section 2 — Code Reading (read code, predict output, min 3)
- [ ] Step 6: Write Section 3 — Spot the Bug (broken code, find the issue, min 3)
- [ ] Step 7: Write Section 4 — Scenario/Debug (production scenario, min 3)
- [ ] Step 8: Write Section 5 — Quick Fire (short factual, min 5)
- [ ] Step 9: Write Answer Key at the end (clearly separated from questions)
- [ ] Step 10: Verify — no answers visible before the Answer Key section
```

## Feedback loop

After generating, check:
- Are answers hidden until the Answer Key section? (No inline spoilers)
- Does every question have an answer in the Answer Key?
- Are the Spot the Bug questions actually buggy? (Test the logic mentally)
- Does the quiz cover all major concepts from the explanation files?

Fix any failures before finishing.

## Three output types — choose the right one

### Type 1: Module Quiz (`exercises/quiz.md` or `resources/quizzes/`)
- Covers one sub-topic or module
- 20–30 questions total
- All 5 sections present
- Used for self-check after studying the module

### Type 2: Interview Prep Doc (`resources/interview-prep/<topic>.md`)
- Aggregates the Interview Questions from ALL explanation files in a topic
- Adds company-scenario questions not found in individual files
- Grouped by: Foundation → Intermediate → Senior → Principal level
- Used for interview preparation sessions

### Type 3: Phase Assessment (`resources/assessments/phase-N-check.md`)
- Covers an entire learning phase (e.g., Phase 2: Gradle + JDBC + Hibernate)
- 40–50 questions, heavier on scenario/debug
- Includes a self-scoring rubric
- Used at phase completion to verify readiness before the next phase

## Progressive Quiz Drill

Use `resources/progressive-quiz-drill.md` when a sub-topic needs a deeper staged
self-check. This is a support artifact, not a replacement for the module quiz.

Format:
- Round 1 â€” Core recall: definitions, defaults, naming, simple mechanics
- Round 2 â€” Apply and compare: choose between two options and explain why
- Round 3 â€” Debug the bug: broken code or failure mode analysis
- Round 4 â€” Staff-level scenario: trade-offs, scale, reliability, production impact

Rules:
- Keep the drill short and progressive, usually 10â€“15 questions total
- Do not show answers inline; keep a single Answer Key at the end
- Each round must be harder than the one before it
- Reuse the module's real concepts, not generic trivia

For substantial deepening, create the full support pack alongside the drill in the
same sub-topic `resources/` folder: `resources/one-page-cheat-sheet.md` and
`resources/top-resource-guide.md`. The top resource guide must be curated external
learning resources only: books, official docs, blogs, and videos.

## Hard rules

- Answers must NEVER appear inline next to questions — always in a separate Answer Key section
- Every "Spot the Bug" question must contain actual, real bugs — not contrived ones
- Code in questions must be real Java (compiles, no syntax errors)
- Questions must not repeat interview questions already in the explanation files verbatim — rephrase or add new angles
- No `javax.*` in any code snippet — always `jakarta.*`
- Scenario questions must name a real industry vertical (fintech, e-commerce, healthcare, etc.)

## Do not use this skill when

- Writing `explanation/*.md` files → use `generating-java-content`
- Writing exercise `.java` files → use `writing-java-exercises`
- Writing `MINDMAP.md` → use `generating-java-mindmaps`
- Building mini-project scaffolds → use `building-java-miniprojects`
