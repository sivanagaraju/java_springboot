# Spring Mastery — Claude Code Instructions

## Who is learning

Python/FastAPI engineer, 12 years industry, transitioning to Java/Spring Boot.
Interview targets: Amazon, Meta, Google, Microsoft, Netflix, Stripe — Staff Engineer role.

## Stack (never violate these)

- Java 21 LTS, Spring Boot 3.2.x, Gradle (Groovy DSL) — never Maven
- PostgreSQL (Docker) + H2 for tests
- Package: com.learning
- Always jakarta.*— never javax.*
- Spring Security 6: Lambda DSL only

## .agent folder

All skills are in .agent/skills/. Use the correct skill for each task:

- Explanation .md files → generating-java-content
- MINDMAP.md files → generating-java-mindmaps
- Java demo files → generating-java-demos
- Exercise files → writing-java-exercises
- Mini-projects → building-java-miniprojects
- Quizzes and interview prep → generating-quiz-assessments
- API design, JWT, system design → designing-api-systems
- Standards enforcement on any file → enforcing-java-standards

## Non-negotiable content standards

1. Every explanation/*.md: Mermaid diagram + Python Bridge + Interview Questions
2. Every MINDMAP.md: pure Markdown lists only — never mermaid mindmap blocks
3. Every .java file: all 4 comment layers (file header + class + method + inline)
4. C4 diagrams: Rel(from, to, "label") — never -> inside C4 blocks
5. Interview questions: scenario-based only — never definitional ("What is X?")

## Repo structure

See spring-learning-repo-structure.md and spring-learning-implementation-plan.md
for the full 18-module plan. Complete one sub-topic at a time — never skip.
