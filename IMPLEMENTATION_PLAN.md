# Spring Mastery — Master Implementation Plan (Source of Truth)

> **Profile:** Python/FastAPI engineer | Data Engineering | 12 years industry | New Java/Spring
> **Stack:** Java 21 + Spring Boot 3 + Spring Framework 6 + Gradle + PostgreSQL + Docker
> **Philosophy:** Python comparison → WHY invented → ASCII + Mermaid diagrams → code → mindmap → interview questions → mini-project

> [!IMPORTANT]
> This plan is the **single source of truth** for the entire project. It synthesizes:
>
> - [spring-learning-implementation-plan.md](file:///c:/Users/sivan/Learning/Code/java_springboot/spring-learning-implementation-plan.md) — Phase-by-phase learning with code examples
> - [spring-learning-repo-structure.md](file:///c:/Users/sivan/Learning/Code/java_springboot/spring-learning-repo-structure.md) — Complete folder tree with every file name

---

## The Learning Loop (Repeat for EVERY Topic)

```
┌──────────────────────────────────────────────────────────────────┐
│  THE LEARNING LOOP (repeat for every topic)                       │
│                                                                    │
│  1. READ   README.md + MINDMAP.md  → big picture + visual map    │
│  2. READ   explanation/*.md        → concept + ASCII + Mermaid   │
│  3. ANSWER interview questions     → test understanding NOW       │
│  4. READ   explanation/*.java      → see it in annotated code     │
│  5. RUN    the demo file           → ./gradlew :module:run        │
│  6. BUILD  the mini-project        → apply concepts end-to-end   │
│  7. WRITE  exercises               → build from scratch           │
│  8. CHECK  PROGRESS_TRACKER        → tick the checkbox            │
└──────────────────────────────────────────────────────────────────┘
```

> [!CAUTION]
> Never skip phases — Spring is layered, and each phase is a prerequisite for the next.

---

## Four Content Standards (The Deep Practitioner Method)

These are **not optional**. Every `.md` and `.java` file follows these. When in doubt, over-document.

### Standard 1 — Mermaid Diagrams in Every `.md` File

Every `explanation/*.md` must include ≥1 Mermaid diagram. Pick the type that makes the concept clearest:

| Mermaid Type | Syntax | Best For |
|---|---|---|
| `flowchart` | `flowchart TD` | Request flows, decision trees, algorithm steps, if/else logic |
| `sequenceDiagram` | `sequenceDiagram` | Service calls, auth flows, event chains, HTTP request lifecycle |
| `erDiagram` | `erDiagram` | JPA entity relationships, database schemas |
| `classDiagram` | `classDiagram` | OOP hierarchies, interface trees, design patterns |
| `stateDiagram-v2` | `stateDiagram-v2` | Bean lifecycle, circuit breaker states, thread states, order status |
| `gitGraph` | `gitGraph` | Git branching strategies, version workflows |
| `C4Context` | `C4Context` | System-level view: users, services, external systems |
| `C4Container` | `C4Container` | Container view: Spring Boot app, DB, Redis, broker |
| `C4Component` | `C4Component` | Internal Spring Boot component layers |
| `gantt` | `gantt` | Learning timelines, project milestones |
| `journey` | `journey` | User journeys: login, checkout, registration flows |
| `pie` | `pie` | Test pyramid proportions, dependency categories |
| `quadrantChart` | `quadrantChart` | Tech comparison matrices, performance tradeoffs |
| `xychart-beta` | `xychart-beta` | Benchmarks, performance charts, scaling data |
| `timeline` | `timeline` | Technology history, Spring version evolution |
| `block-beta` | `block-beta` | Block architecture diagrams, Docker Compose stacks |

Each `.md` file annotation in the folder structure shows the recommended Mermaid type(s) for that file.

**Rule:** Use multiple diagrams per file when one type is not enough.

### Standard 2 — Markmap Files (`MINDMAP.md`)

> [!WARNING]
> Do NOT use `mermaid` codeblocks with `mindmap`. Use **pure Markdown lists** (Markmap format) so VS Code Markmap extension renders them. Avoid inline HTML tags to ensure compatibility with high-speed rendering.

**Where to create:**

- Every **module** `README.md` links to its `MINDMAP.md`
- Every **sub-topic** with 3+ concepts gets a standalone `MINDMAP.md`
- Every **mini-project** gets a `MINDMAP.md` showing which concepts it exercises

### Standard 3 — Interview Questions in Every Sub-topic `.md`

- Every `explanation/*.md` ends with `## Interview Questions` (3–8 Q&A per file)
- Questions grouped: **Conceptual** → **Scenario/Debug** → **Quick Fire**
- Every question has an answer immediately below
- Questions go easier → harder within the file
- Aggregate from all files feeds `resources/interview-prep/*.md`
- **Never write questions directly in interview-prep — always originate in sub-topic files**

### Standard 4 — Python → Java Mental Model Parity

Every explanation/*.md must include a **Python → Java** comparison section. This is NOT optional.

- Map FastAPI/Flask concepts to the current Spring concept.
- Highlight where Java differs significantly (e.g., Types, Multi-threading, DI Scope).
- Provide a code snippet comparison block (Python vs. Modern Java).

---

## Java Commenting Standard (4 Layers — Non-Negotiable)

### Layer 1 — File Header (every `.java` file)

```
╔═══════════════════════════════════════════════════════╗
║ FILE, MODULE, GRADLE cmd, PURPOSE, WHY IT EXISTS,     ║
║ PYTHON COMPARE, USE CASES, ARCHITECTURAL ASCII DIAGRAM (Mandatory),             ║
║ HOW TO RUN, EXPECTED OUTPUT, RELATED FILES            ║
╚═══════════════════════════════════════════════════════╝
```

### Layer 2 — Class-Level (every class)

What class does, Python equivalent, layer responsibility, why this annotation

### Layer 3 — Method-Level (every public method)

Why this method, Python equivalent, flow ASCII, params, returns, warnings

### Layer 4 — Inline (complex logic only)

Non-obvious algorithm steps, business rules, gotcha warnings. **NOT** for obvious code.

---

## Gradle Command Reference

```bash
./gradlew :module:bootRun                    # Start Spring Boot app
./gradlew :module:run --args="ClassName"     # Run pure Java demo
./gradlew test                               # All tests
./gradlew :module:test                       # Module tests
./gradlew :module:test --tests "*ClassName"  # Specific test
./gradlew :module:bootJar                    # Build fat JAR
./gradlew :module:dependencies               # Dependency tree
./gradlew dependencyUpdates                  # Check outdated deps
./gradlew clean                              # Remove build/
./gradlew :module:bootBuildImage             # Docker via Buildpacks
./gradlew :module:test --continuous          # Re-run on file change
```

---

## Python → Java Mental Model (Read First)

```
Python world you know:          Java world you're entering:
────────────────────────────    ────────────────────────────
python script.py                javac Script.java → .class → java Script
pip install requests            ./gradlew dependencies (Gradle resolves)
requirements.txt                build.gradle dependencies { }
pyproject.toml                  build.gradle + settings.gradle
uvicorn main:app                ./gradlew bootRun (embedded Tomcat)
pytest                          ./gradlew test (JUnit5)
.env file                       application.yml + environment variables
FastAPI app = FastAPI()         @SpringBootApplication main class
@app.get("/")                   @GetMapping("/") on @RestController method
Depends(get_db)                 @Autowired (constructor injection preferred)
```N

---

## Phase 0 — Environment & Repository Bootstrap

**Duration:** 1–2 days | **Folder:** `setup/`

**Install:** JDK 21 via SDKMAN → IntelliJ IDEA + Lombok/Spring Boot plugins → Docker Desktop → Root Gradle multi-module project → PostgreSQL via Docker

**Verify:** `java -version`, `gradle --version`, `docker --version`

**Root files:** `README.md`, `IMPLEMENTATION_PLAN.md`, `PROGRESS_TRACKER.md`, `build.gradle`, `settings.gradle`

---

## Phase 1 — Java Foundation

**Duration:** 3 weeks | **Folders:** `00-java-foundation/` (8 sub-topics), `01-advanced-java/` (2 sub-topics)

### Why You Cannot Skip This

| Java | Python | Why it matters for Spring |
|------|--------|--------------------------|
| `List<String>` only holds Strings | `list` holds anything | Spring Data returns `List<Product>`, not `List` |
| Checked exceptions must be caught | All optional | Spring wraps into RuntimeException |
| `interface` = strict contract | Duck typing | Spring depends on interfaces for proxying |
| `synchronized` keyword | GIL (not same) | Spring handles concurrent requests per bean scope |
| `static` = class-level | Module-level vars | Spring beans are NOT static; context manages them |

### Week 1 — Core Language + OOP

| Day | Topic | File(s) | Key Insight |
|-----|-------|---------|-------------|
| 1 | JVM flow + types | `HowJavaWorks.java`, `VariablesDemo.java` | `int` is not `Integer`; autoboxing trap |
| 2 | Control flow | `ControlFlowDemo.java` | Java 14+ switch expression vs Python match |
| 3 | Classes + constructors | `ClassAndObjectDemo.java` | `this()` chaining vs Python `__init__` |
| 4 | Encapsulation + Inheritance | `InheritanceDemo.java` | `extends` is single; `super()` must be first |
| 5 | Polymorphism + Interfaces | `PolymorphismDemo.java`, `InterfaceDemo.java` | Interface = contract; not Python duck typing |
| 6 | Abstract + Access modifiers | `AbstractDemo.java` | abstract when sharing state; interface for contract |
| 7 | Exercises | `Ex01_BankAccount`, `Ex02_ShapeHierarchy` | `./gradlew :00-java-foundation:run` |

### Week 2 — Advanced OOP + Collections + Functional

| Day | Topic | File(s) | Key Insight |
|-----|-------|---------|-------------|
| 8 | Generics | `GenericsDemo.java` | `List<String>` vs `List<Object>` NOT same at compile time |
| 9 | Collections | `ListDemo.java`, `MapDemo.java` | HashMap=Python dict; ArrayList=Python list |
| 10 | Sorting | `SortingDemo.java` | `Comparator.comparing()` = Python `key=lambda` |
| 11 | Exceptions | `ExceptionHierarchyDemo.java` | Checked exceptions Java-specific; Spring converts them |
| 12 | Lambdas | `LambdaDemo.java` | `Predicate<T>` = Python `Callable[[T], bool]` |
| 13 | Stream API | `StreamApiDemo.java` | `.filter().map().collect()` = Python `filter()/map()/list()` |
| 14 | Optional | `OptionalDemo.java` | `Optional<T>` prevents NPE; like Python `Optional[T]` hint |

### Week 3 — Concurrency + Design Patterns

| Day | Topic | File(s) | Key Insight |
|-----|-------|---------|-------------|
| 15 | Threads | `ThreadLifecycleDemo.java` | Spring Boot handles each request in a thread |
| 16 | ExecutorService | `ExecutorServiceDemo.java` | Thread pools; never `new Thread()` in production |
| 17 | Proxy pattern | `ProxyPatternDemo.java` | **Critical**: Spring AOP = JDK Proxy + CGLIB |
| 18 | Builder + Strategy | `BuilderPatternDemo.java` | Spring uses Builder everywhere; Strategy = @Bean selection |
| 19 | Modern Java | `RecordsDemo.java` | Java records = Python dataclasses; use for DTOs |
| 20–21 | Design patterns | All patterns | Observer = Spring Events; Factory = BeanFactory |

**Mini-Project:** `mini-project-00-library-cli/` — OOP + Collections + Streams + Exceptions + JUnit5. No Spring. Validates Java foundation.

---

## Phase 2 — Gradle + JDBC + Hibernate

**Duration:** 1 week | **Folders:** `02-gradle-build-tool/`, `03-jdbc/`, `04-hibernate-jpa/`

### Gradle = `pip + poetry + make` combined

| Gradle Scope | Python Equivalent | Purpose |
|---|---|---|
| `implementation` | `pip install requests` (in requirements.txt) | Runtime dependency |
| `testImplementation` | `pip install pytest` (in dev-requirements.txt) | Test-only |
| `runtimeOnly` | No direct equivalent | Needed at runtime, not compile |
| `compileOnly` | No direct equivalent | Compile-time only (e.g., Lombok) |

### JDBC — Build `CRUDWithJDBC.java`

Write ~100 lines of boilerplate for one table. Then see JPA do it in 5 lines. **The contrast IS the lesson.**

### Hibernate/JPA — Your SQLAlchemy

| Concept | What It Is | Why It Matters | Python Equivalent |
|---|---|---|---|
| N+1 Problem | 1 query parents + N queries children | Kills performance silently | SQLAlchemy lazy loading same |
| EAGER vs LAZY | When related objects fetched | Wrong default = perf bug | `lazy=True` in SQLAlchemy |
| @Transactional | Session boundary | When Hibernate can write | `with session.begin():` |
| L1 Cache | Session-level identity map | Same ID = same object ref | SQLAlchemy session identity map |
| Cascade | Operations propagate to children | Delete parent → delete children | `cascade="all, delete-orphan"` |

**Demonstrate N+1 explicitly** with `FetchTypeDemo.java` — show 11 queries vs 1 query with `JOIN FETCH`.

**Docker PostgreSQL for all work:** `docker run --name spring-pg -e POSTGRES_DB=learning -p 5432:5432 -d postgres:15`

---

## Phase 3 — Spring Core

**Duration:** 1 week | **Folder:** `05-spring-core/`

### The One Mental Model

Spring is a **smart object factory**. You describe what you need. Spring creates it, wires it, manages its lifecycle. You **never** call `new SomeService()` in production code.

**Key difference from FastAPI:** FastAPI injects PER REQUEST (fresh db session). Spring injects AT STARTUP (same service instance for all requests). Spring can also inject per-request via `@RequestScope`.

### Day-by-Day with WHY

| Day | Build | WHY This Feature Was Invented |
|-----|-------|-------------------------------|
| 1 | `IoCConceptDemo.java` | Without IoC: `new ServiceA(new ServiceB(new ServiceC()))` — rigid coupling |
| 2 | `ConstructorInjectionDemo.java` | Setter injection made circular deps common; constructor exposes them |
| 3 | `ComponentScanDemo.java` | Without scanning: register every bean manually in XML — 500+ line XML files |
| 4 | `JavaConfigDemo.java` | XML configs had no IDE support, no refactoring, no type checking |
| 5 | `BeanScopeDemo.java` | Singleton wrong for stateful beans; prototype wrong for stateless services |
| 6 | `BeanLifecycleDemo.java` | Need DB connections at start, close at shutdown — lifecycle hooks |
| 7 | `ConditionalDemo.java` | Different beans for dev/prod/test without code change — profiles |

### Spring Context Startup Sequence (ASCII)

```

./gradlew bootRun → JVM starts → SpringApplication.run()
  → ApplicationContext created
  → @ComponentScan: find @Component/@Service/@Repository/@Controller
  → @Configuration @Bean methods called
  → Dependency injection: wire all beans (constructor first)
  → @PostConstruct on each bean
  → ApplicationReadyEvent → app running
  → Handle requests...
  → On shutdown: @PreDestroy → graceful stop

```

**Mini-Project:** `mini-project-01-notification-engine/` — Pure Spring Core, no HTTP, no DB. @Configuration, @Bean, @Service, @Profile, @PostConstruct, ApplicationEvent.

---

## Phase 4 — Spring Boot Fundamentals

**Duration:** 1 week | **Folder:** `06-spring-boot-fundamentals/`

### What Boot Adds Over Spring

| Raw Spring | Spring Boot | WHY |
|---|---|---|
| Configure Tomcat manually | Embedded auto-configured | No WAR, just `java -jar app.jar` |
| Declare every dep version | Parent BOM manages versions | No version conflicts |
| Configure Jackson/DataSource/etc | Auto-configuration | Classpath scan → configure automatically |

### Full Bean Lifecycle (12 Phases — Must Know for Debugging)

```

@ComponentScan → find → register BeanDefinition
  → BeanFactoryPostProcessor (modify defs BEFORE instantiation)
  → Constructor called (instantiation)
  → BeanPostProcessor.postProcessBeforeInitialization()
  → @PostConstruct
  → InitializingBean.afterPropertiesSet()
  → Custom init-method → BEAN READY
  → BeanPostProcessor.postProcessAfterInitialization()
    ← THIS IS WHERE AOP PROXY IS CREATED
  → Bean in use (handling requests)
  → @PreDestroy (on shutdown)
  → DisposableBean.destroy()

```

### Config Resolution Order (higher wins)

```

1. CLI args: java -jar app.jar --server.port=9090
2. System env vars: SERVER_PORT=9090
3. application-{profile}.yml
4. application.yml
5. @ConfigurationProperties
6. @Value("${port:8080}") defaults

```

**Mini-Project:** `mini-project-02-config-demo-app/` — Same app behaves differently in dev (H2, verbose) vs prod (PostgreSQL, minimal). `./gradlew bootRun --args="--spring.profiles.active=prod"`

---

## Phase 5 — Spring REST API

**Duration:** 2 weeks | **Folder:** `07-spring-rest-api/`

### FastAPI ↔ Spring Boot REST — Complete Comparison

| Concept | FastAPI | Spring Boot |
|---|---|---|
| App | `app = FastAPI()` | `@SpringBootApplication` |
| Route | `@app.get("/items/{id}")` | `@GetMapping("/items/{id}")` |
| Path param | `def get(id: int)` | `@PathVariable Long id` |
| Query param | `def get(q: str = None)` | `@RequestParam(required=false) String q` |
| Request body | `def create(item: ItemSchema)` | `@RequestBody ItemRequest request` |
| Response | `response_model=ItemResponse` | `ResponseEntity<ItemResponse>` |
| Validation | Pydantic automatic | `@Valid` on `@RequestBody` |
| 404 | `raise HTTPException(404)` | `throw new ResourceNotFoundException()` |
| Error handler | `@app.exception_handler(...)` | `@RestControllerAdvice` |
| Middleware | `@app.middleware("http")` | `OncePerRequestFilter` |
| DI | `Depends(get_db)` | Constructor injection |
| Docs | Built-in `/docs` | `springdoc-openapi` at `/swagger-ui.html` |

### Request Flow (ASCII)

```

POST /api/products
  → DispatcherServlet → HandlerMapping → FilterChain
  → ArgumentResolver (JSON → DTO) → Validator (@Valid checks)
  → [if invalid → MethodArgumentNotValidException → GlobalExceptionHandler → 400]
  → ProductController → ProductService → ProductRepository
  → MessageConverter (Response → JSON) → 201 Created

```

### Week 1 — Controller + Validation + Exceptions (Days 1–7)

### Week 2 — Swagger + Versioning + Full CRUD Project (Days 8–14)

**Mini-Project:** `08-full-crud-project/` — Complete Employee API: all CRUD + pagination + search. Test with curl commands.

---

## Phase 6 — Spring Data JPA

**Duration:** 1 week | **Folder:** `08-spring-data-jpa/`

### Derived Query Methods — Method Names Become SQL

```java
List<Product> findByName(String name);
// → SELECT * FROM products WHERE name = ?

List<Product> findByNameContainingIgnoreCase(String name);
// → WHERE LOWER(name) LIKE LOWER('%'||?||'%')

Page<Product> findByCategory(String cat, Pageable pageable);
// → WHERE category = ? LIMIT ? OFFSET ?
```

### @Transactional — The Self-Invocation Trap

```
WHAT IT DOES: Proxy intercepts → open tx → call method → commit/rollback

THE TRAP: methodA() calls this.methodB() — "this" = real object, NOT proxy
         → @Transactional on methodB() is IGNORED
FIX: Inject self, or extract to separate service class.
```

**Mini-Project:** `mini-project-03-product-catalogue/` — Pagination, Specifications, @DataJpaTest.

---

## Phase 7 — Spring Security + JWT/OAuth2

**Duration:** 2 weeks | **Folders:** `10-spring-security/`, `11-jwt-oauth2/`

### Security Filter Chain (15 Filters — Draw From Memory)

```
DisableEncodeUrlFilter → WebAsyncManager → SecurityContextPersistence
→ HeaderWriter → CORS → CSRF → Logout
→ UsernamePasswordAuthFilter → BasicAuth
→ RequestCache → SecurityContextHolder → RememberMe
→ Anonymous → ExceptionTranslation → AuthorizationFilter
→ Your Controller
```

**Python equivalent:** `@app.middleware("http")` — Spring Security = 15 of these stacked in correct order.

### JWT Stateful vs Stateless Flow

```
STATEFUL: POST /login → session in memory → Cookie: JSESSIONID
  Problem: Server A knows session, Server B doesn't → no horizontal scale

STATELESS: POST /login → sign JWT → {token: "eyJh..."}
  → Bearer eyJh... → validate signature (no DB lookup)
  Any service can validate → horizontally scalable
```

### Week 1 — Form auth + UserDetails + BCrypt (Days 1–7)

### Week 2 — JWT + Refresh tokens + OAuth2 (Days 8–14)

**Mini-Projects:** `mini-project-04-secured-employee-api` + `mini-project-05-jwt-secured-api`

---

## Phase 8 — Spring AOP

**Duration:** 1 week | **Folder:** `12-spring-aop/`

### AOP = Python Decorators at Container Level

Python: `@log_calls` must be manually added to every function.
Spring AOP: `@Around("execution(* com.learning.service.*.*(..))")` applies to ALL service methods automatically.

### Proxy Mechanics (Critical)

```
WITHOUT AOP: Controller → ProductService → Repository
WITH AOP:    Controller → [PROXY] → ProductService → Repository
             Proxy intercepts → opens tx → calls advice → calls method → commits

SELF-INVOCATION BREAKS AOP: this.methodB() bypasses proxy
```

**Build:** LoggingAspect, PerformanceAspect, AuditAspect, RetryAspect
**Mini-Project:** `mini-project-06-audited-service/`

---

## Phase 9 — Drools Rules Engine

**Duration:** 1 week | **Folder:** `13-drools-rules-engine/`

### WHY Drools — 50 if-else rules → DRL files

Without Drools: 50+ if-else statements. Business changes threshold? Redeploy Java.
With Drools: `.drl` file. Business analyst changes `600 → 650`. No Java redeploy.

**Industry:** Insurance (premiums), Banking (loans), E-Commerce (pricing), Healthcare (drug checks)

**Mini-Project:** `mini-project-07-loan-approval-engine/` — REST API evaluating DRL rules.

---

## Phase 10 — Testing

**Duration:** 1 week | **Folder:** `14-testing/`

### Testing Pyramid

```
        /\        E2E Tests (few, slow) — Postman/Selenium
       /  \       Integration — @SpringBootTest + Testcontainers
      /----\      Slice — @WebMvcTest / @DataJpaTest
     /------\     Unit — JUnit5 + Mockito (no Spring, fast)
```

**Python equiv:** Unit → pytest+mock | Slice → TestClient(app) | Integration → pytest+real DB

### Mockito vs Python `unittest.mock`

`@Mock` = `MagicMock()` | `when().thenReturn()` = `.return_value` | `verify()` = `.assert_called_once_with()`

---

## Phase 11 — Microservices

**Duration:** 3 weeks | **Folder:** `15-microservices/`

**Week 1:** Two apps + Feign + Eureka
**Week 2:** API Gateway + Resilience4j + Zipkin
**Week 3:** Full 5-service project

### Feign > RestTemplate — Declarative HTTP Client

```java
@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping("/api/products/{id}")
    Product findById(@PathVariable Long id);
}
// Handles URL resolution, serialization, retry, fallback
```

### Circuit Breaker States

```
CLOSED (normal) → failure rate > 50% → OPEN (all fail fast)
  → after 60s → HALF-OPEN (limited probes)
  → success → CLOSED | failure → OPEN
```

---

## Phase 12 — Docker

**Duration:** 3–4 days | **Folder:** `16-docker/`

Multi-stage Dockerfile: JDK builder → JRE runtime (small image). Docker Compose for full stack (app + PostgreSQL + Redis). Non-root user for security.

---

## Phase 13 — Real World Projects

**Duration:** 4–6 weeks | **Folder:** `17-real-world-projects/`

| # | Project | Duration | Stack |
|---|---|---|---|
| 1 | Employee Management | 2–3 days | Boot + REST + JPA + Validation |
| 2 | E-Commerce Platform | 1–1.5 weeks | Everything through AOP + Drools + JWT |
| 3 | Microservices E-Commerce | 1.5–2 weeks | Microservices + Eureka + Gateway + Feign + Resilience4j + Docker |
| 4 | Job Portal | 1 week | Spring MVC + Thymeleaf + Security + JPA |

> Build without referring to earlier code — treat each as a real work task.

---

## Overall Timeline

```
Week 1–3  : Phase 1 (Java Foundation) + Phase 0 (Setup)
Week 4    : Phase 2 (Gradle + JDBC + Hibernate)
Week 5    : Phase 3 (Spring Core) + mini-project-01
Week 6    : Phase 4 (Spring Boot Fundamentals) + mini-project-02
Week 7–8  : Phase 5 (Spring REST API) + CRUD project
Week 9    : Phase 6 (Spring Data JPA) + mini-project-03
Week 10–11: Phase 7 (Spring Security + JWT) + mini-project-04 + 05
Week 12   : Phase 8 (Spring AOP) + mini-project-06
Week 13   : Phase 9 (Drools) + mini-project-07
Week 14   : Phase 10 (Testing)
Week 15–17: Phase 11 (Microservices)
Week 16   : Phase 12 (Docker — parallel with Phase 11)
Week 17–20: Phase 13 (Real World Projects)
```

---

## Mermaid Diagram Guide — Per-File Reference

> Full per-file diagram mapping: [implementation-plan lines 1839–1989](file:///c:/Users/sivan/Learning/Code/java_springboot/spring-learning-implementation-plan.md#L1839-L1989)

| Phase | Key Diagram Types |
|---|---|
| 1 Java | flowchart, classDiagram, sequenceDiagram, stateDiagram-v2, mindmap |
| 2 Gradle/JDBC/Hibernate | timeline, quadrantChart, C4Context, erDiagram, xychart-beta |
| 3 Spring Core | flowchart, C4Component, stateDiagram-v2, sequenceDiagram |
| 4 Spring Boot | flowchart, sequenceDiagram, stateDiagram-v2, mindmap |
| 5 REST API | mindmap, sequenceDiagram, flowchart |
| 6 Data JPA | classDiagram, flowchart, sequenceDiagram, quadrantChart |
| 7 Security/JWT | flowchart, sequenceDiagram, xychart-beta, journey, C4Context |
| 8 AOP | mindmap, flowchart, sequenceDiagram |
| 9 Drools | flowchart, C4Context, timeline, sequenceDiagram |
| 10 Testing | flowchart, pie, sequenceDiagram |
| 11 Microservices | C4Context, C4Container, sequenceDiagram, stateDiagram-v2 |
| 12 Docker | block-beta, timeline, flowchart, C4Container |

---

## MINDMAP.md Creation Schedule

| Phase | Files to Create |
|---|---|
| 0 | `setup/MINDMAP.md` |
| 1 | `00-java-foundation/MINDMAP.md`, `02-oop/MINDMAP.md`, `06-collections/MINDMAP.md`, `07-functional/MINDMAP.md`, `08-multithreading/MINDMAP.md`, `01-design-patterns/MINDMAP.md` |
| 2 | `02-gradle-build-tool/MINDMAP.md`, `04-hibernate-jpa/03-relationships/MINDMAP.md` |
| 3 | `05-spring-core/MINDMAP.md`, `01-ioc-and-di/MINDMAP.md`, `03-bean-scopes-lifecycle/MINDMAP.md` |
| 4 | `06-spring-boot-fundamentals/MINDMAP.md`, `05-beans-deep-dive/MINDMAP.md` |
| 5 | `07-spring-rest-api/MINDMAP.md` |
| 6 | `08-spring-data-jpa/MINDMAP.md`, `02-transactions/MINDMAP.md` |
| 7 | `10-spring-security/MINDMAP.md` ← **most important**, `11-jwt-oauth2/MINDMAP.md` |
| 8 | `12-spring-aop/MINDMAP.md` |
| 9 | `13-drools-rules-engine/MINDMAP.md` |
| 10 | `14-testing/MINDMAP.md` |
| 11 | `15-microservices/MINDMAP.md`, `07-project/architecture-mindmap.md` |
| 12 | `16-docker/MINDMAP.md` |

---

## Interview Prep Strategy

1. Every sub-topic `.md` has `## Interview Questions`
2. After finishing each phase: re-read → answer out loud → identify gaps
3. Copy struggled questions into `resources/interview-prep/`
4. Revisit 3 days later (spaced repetition)
5. Start in earnest after Phase 7

| Module | Questions |
|---|---|
| Core Java | ~80 |
| Hibernate/JPA | ~45 |
| Spring Core | ~35 |
| Spring Boot | ~30 |
| REST API | ~35 |
| Spring Data JPA | ~30 |
| Security + JWT | ~65 |
| AOP | ~20 |
| Drools | ~15 |
| Testing | ~25 |
| Microservices | ~40 |
| **Total** | **~420** |

---

## Resources Directory

```
resources/
├── cheat-sheets/
│   ├── gradle-commands.md, spring-annotations.md, jpa-annotations.md
│   ├── spring-security-flow.md, jwt-claims-reference.md
│   ├── drools-drl-syntax.md, mermaid-diagram-types.md, http-status-codes.md
├── interview-prep/           ← Aggregated from sub-topic files (~420 Q&A)
│   ├── 01-core-java-questions.md ... 15-system-design-questions.md
└── architecture-patterns/
    ├── layered-architecture.md, hexagonal-architecture.md
    ├── cqrs-with-spring.md, saga-pattern.md, event-driven-spring.md
```

---

## Progress Tracker

> [!TIP]
> Full checkbox tracker: [implementation-plan lines 1679–1788](file:///c:/Users/sivan/Learning/Code/java_springboot/spring-learning-implementation-plan.md#L1679-L1788). Copy into `PROGRESS_TRACKER.md` at repo root.

---

## Verification Criteria

- **Each module:** `./gradlew :module:run` or `:module:bootRun` executes successfully
- **Each mini-project:** Compiles, runs, passes all tests
- **Content:** Every `explanation/*.md` has ≥1 Mermaid + `## Interview Questions`
- **Java files:** Follow 4-layer commenting standard with Python comparisons
- **MINDMAP.md:** Created per schedule above
- **Real-world projects:** Full test suites green, Docker Compose running where applicable

---
