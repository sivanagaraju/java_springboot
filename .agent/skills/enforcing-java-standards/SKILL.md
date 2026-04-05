---
name: enforcing-java-standards
description: Enforces project-wide coding and content standards for the spring-mastery
  learning repository. Use when editing any .java file, any explanation/*.md file,
  any ARCHITECTURE.md, README.md, or MINDMAP.md. Triggers on phrases like "write Java
  code", "create a demo", "edit this file", "does this follow the standard", "check
  this against the project rules", "write explanation for X".
---

# Enforcing Java Standards

## Quick start

Read these files before generating anything:

1. [references/stack-constraints.md](references/stack-constraints.md) — Java 21, Spring Boot 3.2, Gradle rules
2. [references/java-commenting-standard.md](references/java-commenting-standard.md) — 4-layer comment format with templates
3. [references/content-standards.md](references/content-standards.md) — 3 mandatory standards for every .md file
4. [references/python-bridge.md](references/python-bridge.md) — Python/FastAPI comparison map
5. [references/mermaid-c4-fix.md](references/mermaid-c4-fix.md) — C4 diagram relationship syntax

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
Standards Enforcement Progress:
- [ ] Step 1: Identify file type (.java / explanation.md / MINDMAP.md / ARCHITECTURE.md)
- [ ] Step 2: Read the relevant reference file for that type
- [ ] Step 3: Apply Layer 1 file header (if .java)
- [ ] Step 4: Apply Layer 2 class-level comment (if .java)
- [ ] Step 5: Apply Layer 3 method-level comments (if .java)
- [ ] Step 6: Add Mermaid diagram (if .md explanation file)
- [ ] Step 7: Add Python Bridge comparison (both .java and .md)
- [ ] Step 8: Add Interview Questions section (if .md explanation file)
- [ ] Step 9: Verify C4 diagram uses Rel() not -> (if any C4 diagram present)
- [ ] Step 10: Self-review against content-standards.md checklist
```

## Feedback loop

After generating, verify:
- `.java` file: does every public method have a Layer 3 javadoc? Does the file header have an ASCII diagram?
- `.md` file: does it have a Mermaid diagram, a Python Bridge section, and an Interview Questions section?
- `MINDMAP.md`: is it pure Markdown lists (not mermaid mindmap blocks)?

If any check fails, fix before finishing.

## Which reference to read for which file type

| File type | Reference to read |
|-----------|------------------|
| Any `.java` | [references/java-commenting-standard.md](references/java-commenting-standard.md) |
| `explanation/*.md` | [references/content-standards.md](references/content-standards.md) |
| `MINDMAP.md` | Use `generating-java-mindmaps` skill instead |
| `ARCHITECTURE.md` | [references/content-standards.md](references/content-standards.md) — architecture section |
| C4 diagram in any file | [references/mermaid-c4-fix.md](references/mermaid-c4-fix.md) |

## Hard rules (memorise these)

- Java version: 21 LTS — use records, sealed classes, switch expressions, text blocks, pattern matching
- Build tool: Gradle (Groovy DSL) only — never Maven, never bare `gradle`
- Package base: `com.learning`
- Database: PostgreSQL (Docker) for integration, H2 for unit tests
- Spring Boot: 3.2.x (Spring Framework 6) — never reference Spring 5 patterns
- ASCII diagrams are MANDATORY in Java file headers — Mermaid cannot render in .java comments
- Every concept must have a Python/FastAPI comparison

## Sub-topic Support Pack

For substantial deepening, also create/update these files in the specific
sub-topic folder under `resources/`:

- `progressive-quiz-drill.md`
- `one-page-cheat-sheet.md`
- `top-resource-guide.md`

`top-resource-guide.md` must be a curated list of external learning resources
only: books, official docs, blogs, and videos. Do not use internal repo links
as the primary content of that file.

## Do not use this skill when

- Writing MINDMAP.md files → use `generating-java-mindmaps` skill
- Writing full explanation/*.md content from scratch → use `generating-java-content` skill
- Writing demo Java files → use `generating-java-demos` skill
- Building mini-projects → use `building-java-miniprojects` skill
- Writing exercises → use `writing-java-exercises` skill
- Generating quizzes or interview prep → use `generating-quiz-assessments` skill
