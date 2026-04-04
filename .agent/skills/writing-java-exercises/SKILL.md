---
name: writing-java-exercises
description: Writes Java exercise files and solution files for the spring-mastery learning
  repository. Use when creating exercise *.java files, exercise README.md files, or
  solution files for any Java or Spring Boot topic. Triggers on phrases like "write
  exercise for", "create exercise file", "generate exercises for module X", "write
  the solution for exercise X", "add exercises to topic X".
---

# Writing Java Exercises

## Quick start

1. Read [references/exercise-standards.md](references/exercise-standards.md) — format, difficulty levels, solution rules
2. Study [examples/Ex01_ConnectionPool.java](examples/Ex01_ConnectionPool.java) — gold-standard exercise
3. Study [examples/Sol01_ConnectionPool.java](examples/Sol01_ConnectionPool.java) — gold-standard solution

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
Exercise Writing Progress:
- [ ] Step 1: Read exercise-standards.md
- [ ] Step 2: Choose difficulty level (1 Guided / 2 Practitioner / 3 Production)
- [ ] Step 3: Write exercise file with ╔══╗ header and TODO markers
- [ ] Step 4: Verify exercise compiles (no syntax errors in scaffolding)
- [ ] Step 5: Write solution file in solutions/ subfolder
- [ ] Step 6: Add WHY comments to every non-obvious solution decision
- [ ] Step 7: Add "COMMON MISTAKES" block at end of solution file
- [ ] Step 8: Verify solution compiles and produces correct output
- [ ] Step 9: Check — no javax.*, no Maven, uses Java 21 features appropriately
```

## Feedback loop

After writing the solution, mentally verify:
- Does the solution actually solve what the exercise asks?
- Does every `// TODO` in the exercise have a corresponding implementation in the solution?
- Are there WHY comments explaining every non-obvious decision in the solution?
- Is the COMMON MISTAKES block at the end of the solution file?

Fix any failures before finishing.

## Difficulty levels

- **Level 1 (Guided, <20 min):** Scaffolded class with clear TODOs. 1–3 missing pieces.
- **Level 2 (Practitioner, 20–45 min):** Skeleton with method stubs. Learner writes all logic.
- **Level 3 (Production, 45–90 min):** Scenario description only. Learner designs + implements.

Each sub-topic needs at least one exercise per level where the complexity warrants it.

## Hard rules

- Every exercise `.java` file must compile (no syntax errors in the scaffolding)
- Every exercise must have a solution file in `solutions/` — no orphaned exercises
- Solution files must be fully runnable against a local Docker + Gradle setup
- No `javax.*` — always `jakarta.*`
- Exercise files get the 4-layer comment treatment on provided code
- Solution files have WHY comments on every non-obvious decision
- Solution files end with a `/* COMMON MISTAKES */` block

## Do not use this skill when

- Writing `explanation/*.md` → use `generating-java-content`
- Writing demo Java files → use `generating-java-demos`
- Writing `MINDMAP.md` → use `generating-java-mindmaps`
- Building mini-project scaffolds → use `building-java-miniprojects`
- Generating quizzes → use `generating-quiz-assessments`
