

Paste this into `PROGRESS_TRACKER.md`:

## Setup
- [ ] JDK 21 installed (sdk install java 21.0.3-tem)
- [ ] IntelliJ IDEA installed + plugins (Lombok, Spring Boot)
- [ ] Docker Desktop running
- [ ] Root Gradle repo initialized

## Phase 1 — Java Foundation
- [ ] 00-java-foundation/01-java-basics
- [ ] 00-java-foundation/02-oop-fundamentals
- [ ] 00-java-foundation/03-advanced-oop
- [ ] 00-java-foundation/04-strings-and-arrays
- [ ] 00-java-foundation/05-exception-handling
- [ ] 00-java-foundation/06-collections
- [ ] 00-java-foundation/07-functional-java
- [ ] 00-java-foundation/08-multithreading
- [ ] 01-advanced-java/01-design-patterns-java
- [ ] 01-advanced-java/03-modern-java-features
- [ ] MINI-PROJECT: mini-project-00-library-cli RUNS + TESTS PASS

## Phase 2 — Build Tools + Persistence
- [ ] 02-gradle-build-tool complete
- [ ] 03-jdbc complete
- [ ] 04-hibernate-jpa/01-hibernate-basics
- [ ] 04-hibernate-jpa/02-jpa-annotations
- [ ] 04-hibernate-jpa/03-relationships (N+1 problem demonstrated)
- [ ] 04-hibernate-jpa/04-advanced-jpa

## Phase 3 — Spring Core
- [ ] 05-spring-core/01-ioc-and-di
- [ ] 05-spring-core/02-bean-configuration
- [ ] 05-spring-core/03-bean-scopes-lifecycle
- [ ] 05-spring-core/04-spring-events
- [ ] 05-spring-core/05-spring-expression-language
- [ ] MINI-PROJECT: mini-project-01-notification-engine RUNS

## Phase 4 — Spring Boot Fundamentals
- [ ] 06-spring-boot-fundamentals/01-spring-boot-internals
- [ ] 06-spring-boot-fundamentals/02-starters-and-dependencies
- [ ] 06-spring-boot-fundamentals/03-configuration-management
- [ ] 06-spring-boot-fundamentals/04-actuator-devtools
- [ ] 06-spring-boot-fundamentals/05-spring-boot-beans-deep-dive
- [ ] MINI-PROJECT: mini-project-02-config-demo-app RUNS (dev + prod profiles)

## Phase 5 — Spring REST API
- [ ] 07-spring-rest-api/01-rest-fundamentals
- [ ] 07-spring-rest-api/02-rest-controller
- [ ] 07-spring-rest-api/03-dto-pattern
- [ ] 07-spring-rest-api/04-exception-handling
- [ ] 07-spring-rest-api/05-validation
- [ ] 07-spring-rest-api/06-openapi-swagger
- [ ] 07-spring-rest-api/07-rest-api-versioning
- [ ] MINI-PROJECT: 07-spring-rest-api/08-full-crud-project RUNNING + ALL CURL TESTS PASS

## Phase 6 — Spring Data JPA
- [ ] 08-spring-data-jpa/01-spring-data-overview
- [ ] 08-spring-data-jpa/02-transactions (self-invocation trap understood)
- [ ] 08-spring-data-jpa/03-spring-data-rest
- [ ] MINI-PROJECT: mini-project-03-product-catalogue RUNNING + @DataJpaTest PASS

## Phase 7 — Spring Security + JWT
- [ ] 10-spring-security/01-security-architecture (can draw filter chain from memory)
- [ ] 10-spring-security/02-form-based-auth
- [ ] 10-spring-security/03-user-management
- [ ] 10-spring-security/04-method-security
- [ ] 10-spring-security/05-csrf-cors
- [ ] MINI-PROJECT: mini-project-04-secured-employee-api RUNNING + SECURITY TESTS PASS
- [ ] 11-jwt-oauth2/01-jwt-deep-dive
- [ ] 11-jwt-oauth2/02-oauth2
- [ ] MINI-PROJECT: mini-project-05-jwt-secured-api RUNNING + POSTMAN COLLECTION PASSES

## Phase 8 — Spring AOP
- [ ] 12-spring-aop/01-aop-concepts (proxy mechanics understood)
- [ ] 12-spring-aop/02-advice-types
- [ ] 12-spring-aop/03-pointcut-expressions
- [ ] 12-spring-aop/04-real-world-aop
- [ ] MINI-PROJECT: mini-project-06-audited-service RUNNING + AUDIT LOG POPULATES

## Phase 9 — Drools
- [ ] 13-drools-rules-engine/01-drools-concepts
- [ ] 13-drools-rules-engine/02-drl-syntax
- [ ] 13-drools-rules-engine/03-spring-boot-drools
- [ ] 13-drools-rules-engine/04-advanced-drools
- [ ] MINI-PROJECT: mini-project-07-loan-approval-engine RUNNING + RULE CHANGES WORK WITHOUT JAVA REDEPLOY

## Phase 10 — Testing
- [ ] 14-testing/01-junit5
- [ ] 14-testing/02-mockito
- [ ] 14-testing/03-spring-boot-testing
- [ ] 14-testing/04-testcontainers

## Phase 11 — Microservices
- [ ] 15-microservices/01-microservices-concepts
- [ ] 15-microservices/02-service-discovery (Eureka running)
- [ ] 15-microservices/03-inter-service-communication (Feign working)
- [ ] 15-microservices/04-api-gateway (routes working)
- [ ] 15-microservices/05-resilience (circuit breaker demonstrated)
- [ ] 15-microservices/07-microservices-project (all services running via docker-compose)

## Phase 12 — Docker
- [ ] 16-docker/01-docker-basics (Dockerfile built + pushed)
- [ ] 16-docker/02-docker-compose (full stack running)
- [ ] 16-docker/03-spring-docker-integration

## Real World Projects
- [ ] project-01-employee-management COMPLETE + ALL TESTS GREEN
- [ ] project-02-ecommerce-platform COMPLETE + ALL TESTS GREEN
- [ ] project-03-microservices-ecommerce COMPLETE + DOCKER COMPOSE RUNNING
- [ ] project-04-job-portal COMPLETE + ALL TESTS GREEN

---

## Interview Prep Checklist (start after Phase 7)

Questions you must answer cold:

**Core Java**

- How does HashMap work internally (hashCode → bucket index → linked list/tree)?
- What is the contract between `equals()` and `hashCode()`? What breaks if you override only one?
- What is a `ConcurrentModificationException`? How does `ConcurrentHashMap` avoid it?
- Explain the `volatile` keyword. When is it not enough?
- What is the difference between `Comparable` and `Comparator`?

**Spring Core**

- Explain IoC and DI. What problem do they solve? (don't just define — explain the pain before Spring)
- Constructor vs setter vs field injection — pros and cons of each
- What is the difference between `@Component`, `@Service`, `@Repository`? Are they interchangeable?
- What is the full bean lifecycle? Name 5 phases in order.
- What is a `BeanPostProcessor`? How does Spring AOP use it?
- Explain `@Transactional` self-invocation. Why does it break? How do you fix it?

**JPA/Hibernate**

- What is the N+1 problem? How do you detect it? How do you fix it?
- What is the difference between EAGER and LAZY fetching? What are the defaults?
- What happens in a `@Transactional` method — open to close? What triggers rollback?
- Explain optimistic vs pessimistic locking. When to use each?

**Spring Security**

- Draw the Spring Security filter chain (15 filters in order)
- How does JWT authentication work? Explain the filter you write
- What is CSRF? When should you disable it?
- What is the difference between authentication and authorization?

**Microservices**

- What is service discovery? Client-side vs server-side?
- Explain circuit breaker states: CLOSED → OPEN → HALF-OPEN
- What is an API Gateway? What problems does it solve?
- How do you handle distributed transactions? (Saga pattern)
- What is the difference between synchronous REST and asynchronous messaging?


---

## Mermaid Diagrams — Phase-by-Phase Creation Guide

Use this table to know exactly which Mermaid diagram types to create in each phase and why:

### Phase 1 — Java Foundation

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-how-java-works.md` | `flowchart` + `C4Context` | `.java → javac → .class → JVM → output`; JDK/JRE/JVM system view |
| `02-variables-datatypes.md` | `classDiagram` | Primitive types + Wrapper class hierarchy |
| `01-class-and-object.md` | `classDiagram` + `sequenceDiagram` | Blueprint → 3 instances; `new` → constructor → heap |
| `04-inheritance.md` | `classDiagram` | Animal → Dog → GuideDog with overridden methods |
| `05-polymorphism.md` | `sequenceDiagram` | Runtime dispatch to correct overridden method |
| `06-abstraction.md` | `classDiagram` + `flowchart` | Abstract class vs Interface side-by-side; decision tree |
| `01-collections-overview.md` | `classDiagram` | Full Iterable → Collection → List/Set/Queue tree |
| `04-map.md` | `flowchart` | HashMap internals: hashCode → bucket → linked list/tree |
| `01-threads-basics.md` | `stateDiagram-v2` | NEW → RUNNABLE → BLOCKED → WAITING → TIMED_WAITING → TERMINATED |
| `04-stream-api.md` | `flowchart` | Source → intermediate ops (lazy) → terminal op → result |
| `01-exception-hierarchy.md` | `classDiagram` | Throwable → Error / Exception → Checked / Unchecked |
| Module `README.md` | `mindmap` | All 8 sub-topics of Java Foundation as a mindmap |
| `MINDMAP.md` (02-oop) | `mindmap` | All OOP pillars: encap/inherit/poly/abstraction + sub-concepts |
| `MINDMAP.md` (06-collections) | `mindmap` | Full collection taxonomy + use-case leaves |

### Phase 2 — Gradle + JDBC + Hibernate

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-gradle-vs-maven.md` | `timeline` + `quadrantChart` | Gradle vs Maven history; speed/flexibility/learning matrix |
| `02-build-gradle-structure.md` | `flowchart` | Gradle 3 phases: Initialization → Configuration → Execution |
| `05-multi-module-gradle.md` | `C4Context` | Root project + child modules system context |
| `01-jdbc-architecture.md` | `C4Context` + `flowchart` | App/JDBC API/Driver/DB; connection lifecycle |
| `03-statement-types.md` | `classDiagram` | Statement → PreparedStatement → CallableStatement |
| `07-connection-pooling.md` | `flowchart` + `xychart-beta` | Borrow from pool flow; pool vs raw connection time benchmark |
| `08-jdbc-transactions.md` | `sequenceDiagram` | begin → SQL1 → SQL2 → commit/rollback |
| `02-hibernate-architecture.md` | `C4Component` + `sequenceDiagram` | SessionFactory/Session/Transaction; persist → flush → SQL |
| `04-fetch-types.md` | `sequenceDiagram` + `xychart-beta` | N+1 query sequence; query count scaling chart |
| `01-one-to-one.md` | `erDiagram` | User ↔ UserProfile entity relationship |
| `02-one-to-many.md` | `erDiagram` | Department → Employees with FK annotation |
| `03-many-to-many.md` | `erDiagram` | Students ↔ Courses with join table |
| `07-optimistic-locking.md` | `sequenceDiagram` | Two concurrent updates; second gets OptimisticLockException |

### Phase 3 — Spring Core

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-what-is-ioc.md` | `flowchart` + `C4Context` | Manual wiring pain → Spring container wires it; App/Container/Beans |
| `02-dependency-injection.md` | `classDiagram` + `sequenceDiagram` | 3 injection types; constructor injection wiring at startup |
| `03-spring-container.md` | `C4Component` | ApplicationContext internals: BeanFactory/Environment/EventPublisher |
| `01-bean-scopes.md` | `stateDiagram-v2` + `flowchart` | Singleton vs Prototype lifecycle; scope selection decision tree |
| `02-lifecycle-hooks.md` | `sequenceDiagram` | Full 12-phase bean lifecycle sequence |
| `01-component-scanning.md` | `flowchart` | @ComponentScan → find annotations → register definitions |
| `02-custom-events.md` | `sequenceDiagram` | Event published → 2 async listeners execute independently |
| Module `README.md` | `mindmap` | Spring Core: IoC / DI / Beans / Scopes / Events / SpEL |

### Phase 4 — Spring Boot Fundamentals

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `02-springbootapplication.md` | `flowchart` | @SpringBootApplication decomposed into 3 meta-annotations |
| `03-auto-configuration.md` | `sequenceDiagram` + `flowchart` | Classpath scan → conditional → configure; @ConditionalOnClass decision |
| `07-startup-sequence.md` | `sequenceDiagram` | main() → SpringApplication.run() → context refresh → ready |
| `01-bean-lifecycle-complete.md` | `stateDiagram-v2` + `sequenceDiagram` | All 12 phases as state machine; BeanPostProcessor wrapping |
| `02-config-hierarchy.md` | `flowchart` | CLI args > env vars > profile yml > yml > defaults resolution |
| Module `README.md` | `mindmap` | Boot: Auto-config / Starters / Embedded Server / Config / Actuator / Beans |

### Phase 5 — Spring REST API

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-rest-principles.md` | `mindmap` | 6 REST constraints as mindmap branches |
| `04-json-and-jackson.md` | `sequenceDiagram` | JSON body → Jackson → DTO → business logic → DTO → Jackson → JSON |
| `01-rest-controller.md` | `flowchart` | HTTP request → DispatcherServlet → HandlerMapping → Controller |
| `02-controller-advice.md` | `sequenceDiagram` | Exception thrown → propagates → @ControllerAdvice → error response |
| `01-bean-validation.md` | `flowchart` | @Valid → ConstraintViolation → MethodArgumentNotValidException → 400 |
| Module `README.md` | `mindmap` | REST: Principles / Controller / DTO / Validation / Exceptions / Swagger / Versioning |

### Phase 6 — Spring Data JPA

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-repository-hierarchy.md` | `classDiagram` | Repository → CrudRepository → PagingAndSorting → JpaRepository |
| `03-derived-query-methods.md` | `flowchart` | Method name parsing: find + By + Property + Condition |
| `07-pagination-sorting.md` | `sequenceDiagram` | Controller → Pageable → Service → Repo → Page<T> → PageResponse DTO |
| `08-specifications.md` | `flowchart` | Optional filters → Specification.and() → dynamic WHERE |
| `01-transactional-annotation.md` | `sequenceDiagram` | Proxy intercepts → begin tx → method → commit or rollback |
| `02-propagation-types.md` | `flowchart` + `sequenceDiagram` | Propagation decision tree; REQUIRES_NEW nested tx sequence |
| `03-isolation-levels.md` | `quadrantChart` | Isolation levels: consistency vs performance 2×2 |

### Phase 7 — Spring Security + JWT

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-security-filter-chain.md` | `flowchart` + `sequenceDiagram` | All 15 filters in order; each filter decision (pass/reject) |
| `02-authentication-flow.md` | `sequenceDiagram` | Full auth: request → Filter → Manager → Provider → UserDetailsService → Context |
| `04-password-encoding.md` | `flowchart` + `xychart-beta` | Register (encode) → login (matches) flow; BCrypt rounds vs time chart |
| `01-csrf-attack.md` | `sequenceDiagram` + `journey` | CSRF attack sequence; user journey from login to attack |
| `03-cors.md` | `sequenceDiagram` | Preflight OPTIONS → server responds → actual request |
| `02-jwt-structure.md` | `flowchart` | base64url(header) + base64url(payload) + HMAC signature |
| `03-jwt-flow.md` | `sequenceDiagram` | Login → sign token → store; subsequent request → extract → validate |
| `06-refresh-tokens.md` | `sequenceDiagram` | Access expired → send refresh → rotate → new access token |
| `01-oauth2-concepts.md` | `sequenceDiagram` + `C4Context` | Auth Code flow; 4 OAuth2 roles context diagram |
| Module `README.md` (security) | `mindmap` | Security: Filter Chain / Authentication / Authorization / Users / CSRF / CORS |
| Module `README.md` (jwt) | `mindmap` | JWT: Structure / Flow / Refresh / Security issues / OAuth2 |

### Phase 8 — AOP

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-aop-terminology.md` | `mindmap` + `flowchart` | All 7 AOP terms as mindmap; method call → proxy → advice chain → target |
| `03-proxy-mechanics.md` | `flowchart` + `sequenceDiagram` | JDK vs CGLIB selection; self-invocation bypass sequence |
| `05-around-advice.md` | `sequenceDiagram` | Around: before proceed → call → after proceed |

### Phase 9 — Drools

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-what-is-drools.md` | `flowchart` + `C4Context` + `timeline` | Facts → Rete → Agenda → Execute; Spring/Drools context; Drools history |
| `01-drl-basics.md` | `flowchart` | Rule evaluation: when condition matches → then action fires |
| `02-stateless-vs-stateful.md` | `sequenceDiagram` | Stateless: insert → fire → discard; Stateful: insert → fire → modify → fire again |

### Phase 10 — Testing

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-junit5-basics.md` | `flowchart` | Test lifecycle: @BeforeAll → @BeforeEach → @Test → @AfterEach → @AfterAll |
| `01-mocking-concept.md` | `pie` | Unit test composition: SUT / mocks / assertions |
| `01-springboottest.md` | `pie` | Test slice coverage: unit / @WebMvcTest / @DataJpaTest / @SpringBootTest |
| `04-mockmvc-patterns.md` | `sequenceDiagram` | MockMvc → DispatcherServlet → Controller → mocked Service → assert response |
| `01-testcontainers-intro.md` | `sequenceDiagram` | @Testcontainers → Docker pull → start → DynamicPropertySource → test → stop |

### Phase 11 — Microservices

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-principles.md` | `C4Context` + `mindmap` | Full system: users/gateway/services/DBs; microservices principles mindmap |
| `02-communication-patterns.md` | `sequenceDiagram` × 2 + `quadrantChart` | Sync REST call; async queue call; sync vs async comparison matrix |
| `01-discovery-concept.md` | `sequenceDiagram` + `C4Container` | Register → query Eureka → call; Eureka/ServiceA/ServiceB containers |
| `02-open-feign.md` | `sequenceDiagram` | Feign call → Eureka → HTTP → decode → return |
| `01-gateway-concept.md` | `C4Context` + `flowchart` | Client → Gateway → services; request processing flow |
| `01-circuit-breaker.md` | `stateDiagram-v2` + `sequenceDiagram` | CLOSED → OPEN → HALF_OPEN transitions; failure sequence |
| `02-retry.md` | `sequenceDiagram` | Fail → wait → retry1 → fail → wait longer → retry2 → success |
| Project architecture | `C4Context` + `C4Container` | Full microservices project: all 5 services + gateway + eureka |

### Phase 12 — Docker

| Sub-topic file | Diagram type | What to draw |
|---|---|---|
| `01-containers-vs-vms.md` | `block-beta` + `timeline` | VM stack vs Container stack comparison; evolution from physical to K8s |
| `02-dockerfile-spring-boot.md` | `flowchart` | Multi-stage: build stage (JDK) → runtime stage (JRE) → small image |
| `01-compose-basics.md` | `C4Container` + `sequenceDiagram` | Spring Boot/PostgreSQL/Redis containers; docker-compose up sequence |

---

## Mindmap Creation Schedule

Build these mindmap files as you complete each phase. Do not skip them — they become your study material before interviews:


Phase 0  → setup/MINDMAP.md                      (toolchain overview)
Phase 1  → 00-java-foundation/MINDMAP.md          (complete Java map)
           00-java-foundation/02-oop/MINDMAP.md
           00-java-foundation/06-collections/MINDMAP.md
           00-java-foundation/07-functional/MINDMAP.md
           00-java-foundation/08-multithreading/MINDMAP.md
           01-advanced-java/01-design-patterns/MINDMAP.md
Phase 2  → 02-gradle-build-tool/MINDMAP.md
           04-hibernate-jpa/03-relationships/MINDMAP.md  (most complex topic)
Phase 3  → 05-spring-core/MINDMAP.md
           05-spring-core/01-ioc-and-di/MINDMAP.md
           05-spring-core/03-bean-scopes-lifecycle/MINDMAP.md
Phase 4  → 06-spring-boot-fundamentals/MINDMAP.md
           06-spring-boot-fundamentals/05-beans-deep-dive/MINDMAP.md
Phase 5  → 07-spring-rest-api/MINDMAP.md
Phase 6  → 08-spring-data-jpa/MINDMAP.md
           08-spring-data-jpa/02-transactions/MINDMAP.md
Phase 7  → 10-spring-security/MINDMAP.md           ← most important mindmap
           11-jwt-oauth2/MINDMAP.md
Phase 8  → 12-spring-aop/MINDMAP.md
Phase 9  → 13-drools-rules-engine/MINDMAP.md
Phase 10 → 14-testing/MINDMAP.md
Phase 11 → 15-microservices/MINDMAP.md
           15-microservices/07-project/architecture-mindmap.md
Phase 12 → 16-docker/MINDMAP.md


---

## Interview Questions Strategy

Every sub-topic `.md` ends with its own interview questions. As you finish each phase, do this:


1. Re-read all interview questions in that phase's modules
2. Answer them out loud (not by reading — actually answer)
3. For any you cannot answer: go back to the explanation file
4. Copy the questions you struggled with into resources/interview-prep/
5. Revisit those questions 3 days later (spaced repetition)


**The aggregate interview banks in `resources/interview-prep/` are built from all per-file questions. Never write questions there directly — always write them in the sub-topic file first, then aggregate.**

Total interview questions across all sub-topics: ~400+

| Module | Approx Questions |
|---|---|
| 00-java-foundation | 80 |
| 04-hibernate-jpa | 45 |
| 05-spring-core | 35 |
| 06-spring-boot-fundamentals | 30 |
| 07-spring-rest-api | 35 |
| 08-spring-data-jpa | 30 |
| 10-spring-security | 40 |
| 11-jwt-oauth2 | 25 |
| 12-spring-aop | 20 |
| 13-drools | 15 |
| 14-testing | 25 |
| 15-microservices | 40 |
| **Total** | **~420** |
