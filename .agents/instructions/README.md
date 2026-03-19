# Project-Level Instructions — Spring Mastery Learning Repo

## 🎯 Mission

This is a **learning repository** for a Python/FastAPI engineer (12 years industry experience) transitioning to Java/Spring Boot. Every file must be written at a **"Deep Practitioner" (Senior Engineer)** level with exhaustive explanations.

## 📐 Architecture Constraints

| Constraint | Value |
|---|---|
| **Java Version** | 21 LTS |
| **Spring Boot** | 3.2.x (Spring Framework 6) |
| **Build Tool** | Gradle (Groovy DSL) — never Maven |
| **Database** | PostgreSQL (Docker) + H2 for tests |
| **Package Base** | `com.learning` |

## 📝 Three Content Standards (Non-Negotiable)

### Standard 1 — Mermaid Diagrams
Every `explanation/*.md` file must include at least one Mermaid diagram. Choose from: `flowchart`, `sequenceDiagram`, `erDiagram`, `classDiagram`, `stateDiagram-v2`, `C4Context`, `C4Container`, `C4Component`, `timeline`, `quadrantChart`, `xychart-beta`, `block-beta`, `gantt`, `journey`, `pie`, `gitGraph`.

### Standard 2 — Markmap MINDMAP.md Files
Every module gets a `MINDMAP.md` using **pure Markdown lists** (NOT mermaid mindmap blocks). The VS Code Markmap extension must be able to render them.

### Standard 3 — Interview Questions
Every `explanation/*.md` ends with `## Interview Questions` (3–8 per file). Grouped: **Conceptual → Scenario/Debug → Quick Fire**. Each question has an answer immediately below.

## ✍️ Java Commenting Standard (4 Layers)

1. **File Header:** Metadata box — FILE, MODULE, GRADLE, PURPOSE, WHY IT EXISTS, PYTHON COMPARE, USE CASES, ASCII DIAGRAM, HOW TO RUN, EXPECTED OUTPUT, RELATED FILES.
2. **Class-Level:** Responsibility, Python equivalent, ASCII layer diagram.
3. **Method-Level:** Contract (inputs/outputs), Python equivalent, flow ASCII, `@param`/`@return`/`@throws`.
4. **Inline:** Only for non-obvious/complex logic. Never for obvious code.

## 🐍 Python Bridge Rule

Every concept must be compared to the Python/FastAPI equivalent. This includes:
- Java comments comparing to Python syntax
- Markdown files showing side-by-side comparisons
- WHY the Java way exists (what problem it solves that Python handles differently)
