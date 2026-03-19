---
name: java-standards
description: "Core project-specific rules for Java 21 coding, content standards, and Mermaid C4 documentation. Use when editing Java files, markdown explanation files, or ARCHITECTURE.md to enforce version compatibility and content quality."
---

# Java & Content Standards Skill

## 🎯 Purpose
To keep this repository following a "Deep Practitioner" (Senior Engineer) standard while ensuring strict compatibility with the **Java 21 LTS** + **Spring Boot 3.2+** + **Spring Framework 6** stack.

## 🚀 When to Use
- **Trigger:** Editing ANY `.java` file.
- **Trigger:** Editing ANY `explanation/*.md` file.
- **Trigger:** Editing `ARCHITECTURE.md`, `README.md`, or `MINDMAP.md`.
- **Trigger:** Creating new modules or sub-topics.

## 📜 Key Rules

### 1. Java 21 LTS Environment
- **Required:** Use modern Java features where taught: `record`, `sealed class`, `switch` expressions (`->`), text blocks (`"""`), pattern matching (`instanceof` with binding).
- **Required:** `sourceCompatibility = '21'` in every `build.gradle`.
- **Required:** Use Spring Boot 3.2.x starters (require Java 17+).

### 2. Gradle (Groovy DSL) — Never Maven
- **Required:** Always use `./gradlew` commands (never `mvn` or `gradle` directly).
- **Required:** Use the Gradle wrapper (`gradlew` / `gradlew.bat`).
- **Build:** `./gradlew :module-name:bootRun` for Spring Boot apps.
- **Test:** `./gradlew :module-name:test` for running tests.
- **Pure Java:** `./gradlew :module-name:run` for non-Spring demos.

### 3. Mermaid C4 Rendering Safety
- **Anti-Pattern (Critical Error):** Never use `->` or `-->` inside `C4Context`, `C4Container`, or `C4Component` blocks.
- **Success Pattern:** Only use `Rel(from, to, "label")`.
- **Reason:** Standard arrows break the C4-specific Mermaid parser.

### 4. 4-Layer Commenting Standard (Every .java File)

> **CRITICAL: ASCII diagrams are MANDATORY in Layers 1, 2, and 3.** ASCII diagrams in `.java` files complement Mermaid diagrams in `.md` files. Mermaid cannot render inside Java comments, so ASCII art is required.

#### Layer 1 — File Header (EVERY .java file)

Use the `╔══╗` box format with an ASCII diagram showing the concept's architecture or flow:

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ClassName.java                                        ║
 * ║  MODULE : XX-module-name / YY-subtopic                          ║
 * ║  GRADLE : ./gradlew :XX-module-name:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : One sentence — what this file demonstrates    ║
 * ║  WHY IT EXISTS  : What problem existed before this feature      ║
 * ║  PYTHON COMPARE : Python/FastAPI equivalent pattern             ║
 * ║  USE CASES      : 3–4 real-world scenarios                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                   ║
 * ║                                                                   ║
 * ║    Java Application                                               ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    [ JDBC API ]  ← Abstraction layer                             ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    [ DB Driver ] ← Implementation (PostgreSQL/MySQL)             ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    [ Database ]                                                   ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : exact Gradle command                           ║
 * ║  EXPECTED OUTPUT: what you should see in console                 ║
 * ║  RELATED FILES  : other files in this module to read next        ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
```

#### Layer 2 — Class-Level Javadoc (EVERY class)

Must include:
- Responsibility description
- Python equivalent in `<pre>` block
- ASCII architecture diagram showing where the class fits

```java
/**
 * ProductService — business logic layer for Product domain.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class ProductService:
 *       def __init__(self, repo: ProductRepo = Depends()):
 *           self.repo = repo
 * </pre>
 *
 * <p><b>ASCII — Layered Architecture:</b>
 * <pre>
 *   HTTP Request
 *       │
 *       ▼
 *   [ Controller ]   ← validates input
 *       │
 *       ▼
 *   [ Service ]      ← business rules  ← YOU ARE HERE
 *       │
 *       ▼
 *   [ Repository ]   ← data access
 *       │
 *       ▼
 *   [ Database ]
 * </pre>
 */
```

#### Layer 3 — Method-Level Javadoc (EVERY public method)

Must include:
- Contract description
- Python equivalent
- ASCII flow diagram for non-trivial methods
- `@param`, `@return`, `@throws`

```java
/**
 * Transfer money between accounts atomically.
 *
 * <p><b>Flow:</b>
 * <pre>
 *   transfer(fromId, toId, amount)
 *       │
 *       ▼
 *   Load both accounts ──→ Not found? → throw
 *       │
 *       ▼
 *   Check balance ──→ Insufficient? → throw
 *       │
 *       ▼
 *   Debit from + Credit to
 *       │
 *       ▼
 *   JPA dirty check → UPDATE SQL at commit
 * </pre>
 */
```

#### Layer 4 — Inline Comments (complex logic ONLY)

Only for non-obvious business rules, gotchas, or "why" explanations. Never for obvious code.

### 5. ASCII Diagram Types for Java Files

Choose the ASCII diagram type that best represents the concept:

| ASCII Type | When to Use | Example |
|---|---|---|
| **Vertical flow** (`│ ▼`) | Request flows, algorithm steps, pipelines | JDBC connection flow |
| **Layered stack** (`[ Layer ]`) | Architecture layers, Spring Boot layers | Controller → Service → Repository |
| **Decision tree** (`──→ condition? → result`) | Conditional logic, save vs update | `save()` INSERT vs UPDATE decision |
| **State machine** (`State1 → State2`) | Entity lifecycle, bean lifecycle | JPA entity states |
| **Table** (aligned columns) | Comparison tables, type mappings | Java type → SQL type mapping |
| **Box diagram** (`┌─────┐`) | Component boundaries | Module structure |

### 6. Three Content Standards (Every .md File)

#### Standard 1 — Mermaid Diagrams
Every `explanation/*.md` must include at least one Mermaid diagram. Pick the type that best represents the concept: `flowchart`, `sequenceDiagram`, `classDiagram`, `erDiagram`, `stateDiagram-v2`, `C4Context`, `C4Container`, `C4Component`, `timeline`, `quadrantChart`, `xychart-beta`, `block-beta`, `gantt`, `journey`, `pie`.

#### Standard 2 — MINDMAP.md (Markmap format)
Every module and sub-topic with 3+ concepts gets a `MINDMAP.md` using **pure Markdown lists** (NOT mermaid mindmap blocks). Must render in VS Code Markmap extension.

#### Standard 3 — Interview Questions
Every `explanation/*.md` ends with `## Interview Questions`:
- Minimum 3, maximum 8 per file.
- Grouped: **Conceptual → Scenario/Debug → Quick Fire**.
- Every question has an answer or hint immediately below.

### 7. Spring Boot Architecture Layers
Files must follow the standard Spring Boot package structure:
- `/controller` — `@RestController` REST endpoints
- `/service` — `@Service` business logic (interface-backed)
- `/repository` — `JpaRepository` data access
- `/entity` or `/model` — `@Entity` JPA entities
- `/dto` — Request/Response DTOs
- `/config` — `@Configuration` classes
- `/exception` — Custom exceptions + `@RestControllerAdvice`
- `/security` — Spring Security + JWT

## 🐍 The Python Bridge
Every concept must compare Java to Python/FastAPI equivalents:
- **DI:** `Depends(get_db)` ↔ `@Autowired` (constructor injection)
- **ORM:** SQLAlchemy session ↔ Spring Data `JpaRepository`
- **Validation:** Pydantic validators ↔ Bean Validation (`@NotNull`, `@Size`)
- **Auth middleware:** `@app.middleware("http")` ↔ `OncePerRequestFilter`
- **Testing:** `pytest` fixtures ↔ JUnit 5 `@BeforeEach`
- **Config:** `os.environ.get()` ↔ `@Value("${property}")` / `@ConfigurationProperties`
