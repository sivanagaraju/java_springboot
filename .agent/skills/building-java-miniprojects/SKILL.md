---
name: building-java-miniprojects
description: Scaffolds complete mini-project folders for the spring-mastery learning
  repository. Use when building a full mini-project that includes working Java source
  files, ARCHITECTURE.md, README.md, and MINDMAP.md. Triggers on phrases like "build
  mini-project", "scaffold the project for module X", "create the mini-project for X",
  "generate the project folder for X".
---

# Building Java Mini-Projects

## Quick start

1. Read [references/miniproject-checklist.md](references/miniproject-checklist.md) — full deliverable spec
2. Study [examples/EmployeeJdbcProject.md](examples/EmployeeJdbcProject.md) — annotated project reference

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
Mini-Project Build Progress:
- [ ] Step 1: Read miniproject-checklist.md
- [ ] Step 2: Define project: name, primary concept, Spring module, Gradle command
- [ ] Step 3: Write ARCHITECTURE.md (C4 Mermaid + ASCII pipeline + Design Decisions)
- [ ] Step 4: Scaffold package structure (com.learning.<module>)
- [ ] Step 5: Write entity/model classes with 4-layer commenting
- [ ] Step 6: Write repository/DAO layer with 4-layer commenting
- [ ] Step 7: Write service layer with 4-layer commenting and @Transactional
- [ ] Step 8: Write controller layer (if Spring REST project)
- [ ] Step 9: Write application entry point with run instructions
- [ ] Step 10: Write README.md (setup + run + what to observe + extension exercises)
- [ ] Step 11: Write MINDMAP.md (pure Markdown lists, not mermaid)
- [ ] Step 12: Final check — no javax.*, C4 uses Rel(), no Maven refs
```

## Feedback loop

After writing each file:
- Java files: do all public methods have Layer 3 javadoc?
- ARCHITECTURE.md: does it have BOTH a C4 Mermaid diagram AND an ASCII pipeline?
- README.md: can a beginner run this in under 10 minutes with the instructions given?
- MINDMAP.md: does it contain NO mermaid blocks?

Fix failures before moving to the next file.

## Hard rules

- Every Java file needs all 4 comment layers
- ARCHITECTURE.md must have BOTH C4 Mermaid (with Rel()) AND ASCII data flow
- README.md must have exact copy-pasteable Gradle commands (no `...` placeholders)
- MINDMAP.md must be pure Markdown lists (no mermaid blocks)
- Package: `com.learning.<module-name>`
- No `javax.*` — always `jakarta.*`
- No Maven — Gradle only

## Do not use this skill when

- Writing standalone `explanation/*.java` demos → use `generating-java-demos`
- Writing `explanation/*.md` content → use `generating-java-content`
- Writing `MINDMAP.md` for a topic folder → use `generating-java-mindmaps`
- Writing exercise files → use `writing-java-exercises`
