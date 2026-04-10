# Workspace Standard: Antigravity Agent Configuration

This file provides overarching system context to ensure all AI agents maintain consistency with the **Spring Mastery Learning Journey**.

## 🌲 AI Directory Structure
- **`.agents/instructions/`**: Project mission, architectural constraints, and content standards.
- **`.agents/skills/`**: Capability packets like **`java-standards`** (Java 21 enforcement, Mermaid C4 syntax, commenting layers).
- **`.agents/workflows/`**: Step-by-step fix/maintenance sequences (e.g., **`mermaid_c4.md`**).
- **`.agents/prompts/`**: Reusable project-specific prompts and review templates.

## 🎯 Global Directives
1.  **Java 21 LTS:** This project uses Java 21 with Spring Boot 3.2+ and Spring Framework 6. Modern Java features (records, sealed classes, pattern matching, text blocks) are explicitly taught and should be used where appropriate.
2.  **Gradle (Groovy DSL):** All builds use Gradle — never Maven. Use `./gradlew` commands.
3.  **Mermaid C4 Syntax:** Never use standard arrows (`->`) in C4 blocks. Always use `Rel()`.
4.  **Python Mastery Bridge:** All documentation must compare Java concepts to Python/FastAPI equivalents to aid learning.
5.  **4-Layer Commenting:** Every Java file must have File Header, Class description, Method contracts, and minimal inline comments.
6.  **3 Content Standards:** Every `.md` file must include Mermaid diagrams. Every module needs a `MINDMAP.md` (Markmap format). Every explanation file ends with Interview Questions.
7.  **Resources Support Pack:** Every sub-topic with 3+ explanation files must have a `resources/` folder containing: `one-page-cheat-sheet.md`, `progressive-quiz-drill.md` (4 rounds), `top-resource-guide.md` (external links + concepts checklist). Reference: `04-hibernate-jpa/01-hibernate-basics/resources/`.

## 📂 Active Layers (Spring Boot Architecture)
- **Controller:** REST API endpoints in `/controller` (`@RestController`).
- **Service:** Business logic in `/service` (Interface-backed, `@Service`).
- **Repository:** Data access in `/repository` (Spring Data JPA, `JpaRepository`).
- **Entity / Model:** JPA entities in `/entity` or `/model` (`@Entity`).
- **DTO:** Request/Response data transfer objects in `/dto`.
- **Config:** Configuration classes in `/config` (`@Configuration`).
- **Exception:** Domain exceptions + `@RestControllerAdvice` in `/exception`.
- **Security:** Auth configuration in `/security` (Spring Security + JWT).
