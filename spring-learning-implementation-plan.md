# Spring Mastery — Implementation Plan

> **Who this is for:** Python/FastAPI engineer | Data Engineering background | Targeting production Java/Spring backend role  
> **Style:** Deep understanding, not tutorial-following. Every concept internalized through working code + diagrams.  
> **Testing approach:** Every layer tested independently — see Testing Strategy section below

---

## How This Plan Works

Each phase builds on the last. Never skip a phase — Java compiles differently, Spring wires differently, and the gaps **will** catch you in a real codebase. The plan is ordered the way a senior engineer would teach you: foundation → plumbing → framework → production concerns.

Each phase entry tells you:
- What to build in the folder structure
- Why it matters in enterprise Spring context
- How to test it (no deployment needed for most phases)
- What the Python equivalent is, where relevant

---

## Phase 0 — Environment & Repository Bootstrap
**Duration:** 1–2 days  
**Folder:** `setup/`

### What to do

1. Install JDK 21 (LTS) via SDKMAN: `sdk install java 21-tem`
2. Install IntelliJ IDEA Community (free) — use it as your IDE. It is vastly better than VS Code for Java.
3. Install Maven: `sdk install maven`
4. Install Docker Desktop
5. Run `setup/verify-setup.sh` — this should print Java version, Maven version, Docker version
6. Create the root folder `spring-mastery/` and initialize a Git repo
7. Create `README.md` and `PROGRESS_TRACKER.md` with all module checkboxes

### Mental model to internalize
Java is compiled → bytecode → JVM runs bytecode. Unlike Python, you cannot run a `.java` file directly. The flow is always:
```
.java source → javac compiler → .class bytecode → JVM executes
```
Maven wraps this: `mvn compile` compiles, `mvn test` tests, `mvn package` creates a runnable `.jar`.

---

## Phase 1 — Java Foundation
**Duration:** 2–3 weeks  
**Folder:** `00-java-foundation/`  
**Prerequisite:** None — this IS the foundation

### Why you cannot skip this (even with Python experience)
Java is not Python with semicolons. The differences that matter for Spring are:
- **Static typing** — every variable/param/return has a declared type at compile time
- **Generics** — `List<String>` is not the same as `List<Object>`. Spring uses generics everywhere.
- **Checked exceptions** — some exceptions must be declared or caught. Spring wraps most into unchecked.
- **Object lifecycle** — Java has no GC-free reference counting. Spring's bean lifecycle matters.
- **Interfaces are contracts** — not Python's duck typing. Spring depends on this heavily.

### Topic order and what to actually build

**Week 1: OOP + Types**

| Day | Topic | File to create | Key thing to understand |
|-----|-------|---------------|------------------------|
| 1 | JVM/JDK/JRE + variables + types | `HowJavaWorks.java`, `VariablesDemo.java` | Primitives (int/long/boolean) vs reference types (Integer/String/Object) |
| 2 | Control flow + operators | `ControlFlowDemo.java` | switch expressions (Java 14+), enhanced for loop |
| 3 | OOP: Class/Object/Constructor | `ClassAndObjectDemo.java` | `this` keyword, constructor chaining |
| 4 | Encapsulation + Inheritance | `InheritanceDemo.java` | `private`/`protected`; `extends`; `super()` |
| 5 | Polymorphism + Interfaces | `PolymorphismDemo.java`, `InterfaceDemo.java` | Runtime dispatch; `implements` multiple interfaces |
| 6 | Abstract classes + Access modifiers | `AbstractDemo.java` | abstract vs interface — know the rule: use abstract when sharing state |
| 7 | Exercises + review | `Ex01_BankAccount.java`, `Ex02_ShapeHierarchy.java` | Build, test manually by running main() |

**Week 2: Advanced OOP + Collections + Functional**

| Day | Topic | File | Key insight |
|-----|-------|------|-------------|
| 8 | Generics | `GenericsDemo.java` | `<T>`, `<T extends Number>`, `List<? extends Animal>` |
| 9 | Collections: List/Set/Map | `ListDemo.java`, `MapDemo.java` | HashMap is your Python dict. HashSet is your Python set. |
| 10 | Comparable + Comparator | `SortingDemo.java` | `Comparator.comparing()` replaces Python's `key=` |
| 11 | Exception Handling | `ExceptionHierarchyDemo.java`, `CustomExceptionDemo.java` | Checked vs Unchecked; `try-with-resources` ≈ Python `with` |
| 12 | Lambda + Functional Interfaces | `LambdaDemo.java` | `() -> {}` syntax; `Predicate<T>`, `Function<T,R>` |
| 13 | Stream API | `StreamApiDemo.java` | `.filter().map().collect()` ≈ Python generator + list comprehension |
| 14 | Exercises | `Ex01_StreamPipeline.java` | Filter products, map to DTO, collect to list |

**Week 3: Concurrency + Design Patterns**

| Day | Topic | File | Key insight |
|-----|-------|------|-------------|
| 15 | Multithreading basics | `ThreadLifecycleDemo.java` | Thread states; `synchronized`; why volatile matters |
| 16 | ExecutorService + Future | `ExecutorServiceDemo.java` | Thread pools; don't create raw Thread in production |
| 17 | Proxy pattern | `ProxyPatternDemo.java` | **Critical** — Spring AOP and Spring Data use JDK Proxy/CGLIB |
| 18 | Builder + Strategy patterns | `BuilderPatternDemo.java`, `StrategyPatternDemo.java` | Spring uses Builder everywhere; Strategy = swappable algorithms |
| 19 | Optional | `OptionalDemo.java` | `Optional.ofNullable().map().orElseThrow()` ≈ Python's None-safe patterns |
| 20 | Java Records | `RecordsDemo.java` | Use records for DTOs in Spring Boot 3+ |
| 21 | Review + all exercises | — | Run all exercise files, verify output |

### How to test in this phase
Every class has a `main()` method. Run it from IntelliJ (right-click → Run) or from terminal:
```bash
cd 00-java-foundation
mvn compile
mvn exec:java -Dexec.mainClass="com.learning.basics.VariablesDemo"
```
No Spring, no server, no HTTP. Just Java running Java.

---

## Phase 2 — Maven + JDBC + Hibernate
**Duration:** 1 week  
**Folders:** `02-maven-build-tool/`, `03-jdbc/`, `04-hibernate-jpa/`

### Maven — what you actually need to know

You will use Maven as your `pip + requirements.txt + build system`. The key things:

```xml
<!-- This is your requirements.txt equivalent -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <!-- No <version> needed — Spring Boot parent manages it -->
</dependency>
```

Build the `sample-pom.xml` with annotations explaining every element. Run `mvn dependency:tree` to understand transitive dependencies.

### JDBC — know it to understand why JPA exists

JDBC is Java's `psycopg2`. It is verbose and repetitive. You need to understand it so you appreciate what JPA solves:
- Build `PreparedStatementDemo.java` — see how much code one query takes
- Then build the JPA equivalent — see how much it eliminates
- This comparison lives in `04-hibernate-jpa/01-hibernate-basics/explanation/01-orm-concept.md`

### Hibernate/JPA — the ORM layer

This is your SQLAlchemy. Key things to master **before Spring Data JPA**:

1. `@Entity` mapping — how Java class becomes DB table
2. The relationship annotations — `@OneToMany`, `@ManyToOne`, `@ManyToMany`
3. The N+1 problem — **you will be asked this in every interview**. Know it cold.
4. EAGER vs LAZY fetch — default for `@OneToMany` is LAZY. Default for `@ManyToOne` is EAGER.
5. `@Transactional` — when Session is open, when it's closed

Build the `FetchTypeDemo.java` file to demonstrate N+1. Then build the solution using `JOIN FETCH` in JPQL.

### Testing in this phase
Use a real PostgreSQL via Docker:
```bash
docker run --name learning-pg -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:15
```
All JDBC and Hibernate exercises connect to this. Verify with:
```bash
docker exec -it learning-pg psql -U postgres -c "\l"
```

---

## Phase 3 — Spring Core
**Duration:** 1 week  
**Folder:** `05-spring-core/`

### The single most important mental model

Spring is a DI container. That's it. Everything else is built on top. Your FastAPI dependency injection system is conceptually similar — Spring just does it at class instantiation time, not at function call time.

```
FastAPI:                          Spring:
@app.get("/")                     @RestController
async def route(db: Dep = ...):   @GetMapping("/")
    # db injected per request      public String route(
                                       @Autowired Service s) {
                                   // s injected at startup
```

### Topic order

| Day | What to build | Core concept |
|-----|--------------|--------------|
| 1 | `IoCConceptDemo.java` | Before-DI (manual wiring) vs After-DI (Spring wires it) |
| 2 | `ConstructorInjectionDemo.java` | Constructor injection is the RIGHT way — makes testing easy |
| 3 | `ComponentScanDemo.java` | `@Component` stereotype; package scan; how Spring finds beans |
| 4 | `JavaConfigDemo.java` | `@Configuration` + `@Bean` — explicit wiring for third-party classes |
| 5 | `BeanScopeDemo.java` | Singleton (default) vs Prototype; when scope matters |
| 6 | `BeanLifecycleDemo.java` | `@PostConstruct` for initialization; `@PreDestroy` for cleanup |
| 7 | `ProfileDemo.java` + Exercises | `@Profile("dev")` beans; test-only beans |

### Key file to write: `05-python-fastapi-vs-spring.md`
Write this yourself as part of learning. Map every Spring concept to its FastAPI/Python equivalent. This will be your mental anchor for the rest of the course.

---

## Phase 4 — Spring Boot + REST API
**Duration:** 2 weeks  
**Folders:** `06-spring-boot-fundamentals/`, `07-spring-rest-api/`

### Spring Boot is convention over configuration

Spring Boot does three things:
1. **Auto-configuration** — sees `spring-boot-starter-web` on classpath → configures Tomcat, Jackson, MVC
2. **Starters** — curated dependency bundles with compatible versions
3. **Embedded server** — no war file, no Tomcat installation. Just `java -jar app.jar`

### REST API — your FastAPI comparison

| FastAPI | Spring Boot REST |
|---------|-----------------|
| `@app.get("/items/{id}")` | `@GetMapping("/items/{id}")` |
| `async def get_item(id: int)` | `public Item getItem(@PathVariable int id)` |
| `item: ItemSchema` | `@RequestBody ItemDTO item` |
| `HTTPException(404)` | `throw new ItemNotFoundException(id)` |
| `@router.prefix("/api/v1")` | `@RequestMapping("/api/v1")` on class |
| `Depends(get_db)` | `@Autowired ItemRepository repo` |
| `JSONResponse` | `ResponseEntity<ItemDTO>` |
| `response_model=ItemResponse` | Return `ItemResponseDTO` from method |

### Week 1: Core REST

| Day | Build | Key concept |
|-----|-------|-------------|
| 1 | First Spring Boot app from initializr | Auto-config; project structure; `application.yml` |
| 2 | `BasicRestController.java` | `@RestController`, `@GetMapping`, return JSON automatically |
| 3 | `PathVariableDemo.java` | `@PathVariable`, `@RequestParam`, optional params |
| 4 | `RequestBodyDemo.java` | `@RequestBody`, DTO classes, Jackson deserialization |
| 5 | `ResponseEntityDemo.java` | `ResponseEntity.ok()`, `.status()`, `.notFound().build()` |
| 6 | `GlobalExceptionHandler.java` | `@ControllerAdvice` + `@ExceptionHandler` centralized error handling |
| 7 | `ValidationDemo.java` | `@Valid`, `@NotNull`, `@Size`, custom `@Constraint` |

### Week 2: Advanced REST + Full Project

| Day | Build | Key concept |
|-----|-------|-------------|
| 8 | `SwaggerConfigDemo.java` | Springdoc OpenAPI — auto-generated docs like FastAPI does out of the box |
| 9 | API versioning | URI versioning (`/v1/`, `/v2/`) — enterprise standard |
| 10–14 | `07-full-crud-project/employee-api/` | Complete REST CRUD: Controller → Service → Repository → DB |

### Testing REST endpoints in this phase

**Option 1: curl (fastest)**
```bash
# GET
curl -s http://localhost:8080/api/employees | jq .

# POST
curl -s -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"John","salary":75000}' | jq .

# PUT
curl -s -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","salary":80000}' | jq .

# DELETE
curl -s -X DELETE http://localhost:8080/api/employees/1
```

**Option 2: HTTPie (cleaner, like Postman from terminal)**
```bash
pip install httpie
http POST localhost:8080/api/employees name="John" salary:=75000
```

**Option 3: Postman**
Import the `testing/postman-collection.json` file from the project folder. All endpoints pre-configured.

**Option 4: Spring MockMvc (automated — covered in Phase 7)**
```java
mockMvc.perform(get("/api/employees/1"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.name").value("John"));
```

---

## Phase 5 — Spring Data JPA
**Duration:** 1 week  
**Folder:** `08-spring-data-jpa/`

### What Spring Data JPA eliminates

All the JDBC boilerplate + most Hibernate session management. You write:
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p WHERE p.price < :maxPrice")
    List<Product> findCheaperThan(@Param("maxPrice") BigDecimal maxPrice);
}
```
That's it. Spring generates the implementation.

### Topic order

| Day | Build | Key concept |
|-----|-------|-------------|
| 1 | `ProductRepository.java` | Derived query methods; how Spring parses method names |
| 2 | `PaginationDemo.java` | `Pageable`, `PageRequest.of(0, 10, Sort.by("name"))` |
| 3 | `CustomQueryDemo.java` | `@Query` JPQL; native queries; projections |
| 4 | `TransactionDemo.java` | `@Transactional` at service level; propagation |
| 5 | `PropagationDemo.java` | `REQUIRED` vs `REQUIRES_NEW` — understand nested transactions |
| 6 | `BankTransfer.java` exercise | Transactional rollback; exception types that trigger rollback |
| 7 | Spring Data REST | Auto-expose endpoints; HATEOAS |

---

## Phase 6 — Spring Security + JWT + OAuth2
**Duration:** 2 weeks  
**Folders:** `10-spring-security/`, `11-jwt-oauth2/`

### The most complex phase — take your time

Spring Security has a steep learning curve because it is highly configurable and the defaults change between versions. Master the mental model first:

```
Every HTTP Request
        │
        ▼
┌───────────────────────────────────────────────────────────┐
│              Security Filter Chain (ordered)               │
│  UsernamePasswordAuthenticationFilter                      │
│  BasicAuthenticationFilter                                 │
│  JwtAuthenticationFilter  ← you will write this           │
│  ExceptionTranslationFilter                                │
│  FilterSecurityInterceptor                                 │
└───────────────────────────────────────────────────────────┘
        │
        ▼
   SecurityContext (ThreadLocal — per request)
        │
        ▼
   Your Controller
```

### Week 1: Foundation Auth

| Day | Build | Key concept |
|-----|-------|-------------|
| 1 | `SecurityFilterChainDemo.java` | Print all default filters; understand the chain |
| 2 | Form-based auth with custom login page | `HttpSecurity.formLogin()` configuration |
| 3 | `CustomUserDetailsService.java` | Load users from DB; `UserDetails` contract |
| 4 | `RegistrationController.java` | BCrypt password hashing; user registration |
| 5 | `MethodSecurityDemo.java` | `@PreAuthorize("hasRole('ADMIN')")` |
| 6 | CSRF and CORS | Why CSRF matters; disable safely for stateless APIs |
| 7 | Exercise: secure the employee API | Role-based access to CRUD endpoints |

### Week 2: JWT + OAuth2

| Day | Build | Key concept |
|-----|-------|-------------|
| 8 | `JWTUtil.java` | Generate + validate tokens; HMAC SHA256 signing |
| 9 | `JWTAuthFilter.java` | `OncePerRequestFilter`; extract token from header; set SecurityContext |
| 10 | `AuthController.java` | `/api/auth/login` endpoint returning JWT |
| 11 | Stateless security config | `SessionCreationPolicy.STATELESS`; no CSRF for JWT |
| 12 | Refresh tokens | Access token (15 min) + refresh token (7 days) pattern |
| 13 | OAuth2 social login | Google OAuth2 with `spring-security-oauth2-client` |
| 14 | Exercise: Full JWT auth system | Register → Login → JWT → Protected endpoints |

### How to test security

```bash
# Get JWT token
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"secret"}' | jq -r .token)

# Use token
curl -s localhost:8080/api/employees \
  -H "Authorization: Bearer $TOKEN" | jq .

# Test unauthorized
curl -s localhost:8080/api/employees  # Should return 401
```

---

## Phase 7 — Spring AOP
**Duration:** 1 week  
**Folder:** `12-spring-aop/`

### What AOP solves

Cross-cutting concerns: logging, security checks, performance monitoring, retry logic, audit trailing. Without AOP you copy-paste this into every method. With AOP it lives in one place.

```
Without AOP:                    With AOP:
void processOrder() {           void processOrder() {
  log.info("start");               // just business logic
  checkPermission();               doActualWork();
  long start = now();           }
  try {
    doActualWork();             @Before("execution(* processOrder(..))")
  } finally {                   void logBefore() { log.info("start"); }
    log.info(now()-start);
  }                             @Around("execution(* processOrder(..))")
}                               void timing(ProceedingJoinPoint pjp) { ... }
```

### Topic order

| Day | Build | Key concept |
|-----|-------|-------------|
| 1 | `AOPConceptsDemo.java` | Aspect/Advice/Pointcut/JoinPoint — all five terms with examples |
| 2 | `BeforeAdviceDemo.java` | `@Before`; logging method calls with method name + args |
| 3 | `AroundAdviceDemo.java` | `@Around`; `ProceedingJoinPoint.proceed()`; timing wrapper |
| 4 | `AfterReturningDemo.java` + `AfterThrowingDemo.java` | Post-processing; exception notification |
| 5 | `PointcutDeclarationDemo.java` | `@Pointcut` declarations; combining with `&&` `||` |
| 6 | `LoggingAspect.java` + `PerformanceAspect.java` | Real production-grade aspects |
| 7 | `AuditAspect.java` + `RetryAspect.java` | Custom `@Auditable` annotation; retry with backoff |

### Why proxy understanding matters
Spring AOP only works on Spring-managed beans. If you call a method on `this` inside a bean, the proxy is bypassed. This is the most common AOP bug. Write a demo showing this.

---

## Phase 8 — Testing
**Duration:** 1 week  
**Folder:** `13-testing/`

### Testing pyramid for Spring Boot

```
         /\
        /  \
       / E2E \      ← Few, slow, full stack (Postman/Selenium)
      /--------\
     /Integration\  ← @SpringBootTest, @WebMvcTest, Testcontainers
    /------------\
   /  Unit Tests  \ ← JUnit5 + Mockito, no Spring context
  /________________\
```

The bottom two layers are what you automate. Python pytest maps to JUnit5. `unittest.mock` maps to Mockito.

### Topic order

| Day | Build | Key concept |
|-----|-------|-------------|
| 1 | `BasicJUnit5Demo.java` | `@Test`, `@BeforeEach`, `assertThrows`, `assertAll` |
| 2 | `ParameterizedTestDemo.java` | `@ParameterizedTest` + `@CsvSource` ≈ pytest parametrize |
| 3 | `MockitoBasicsDemo.java` | `@Mock`, `@InjectMocks`; when/thenReturn ≈ MagicMock |
| 4 | `ArgumentCaptorDemo.java` | Capture args passed to mock; `verify()` call count |
| 5 | `ControllerLayerTest.java` | `@WebMvcTest`; MockMvc; test controller WITHOUT Spring Data |
| 6 | `RepositoryLayerTest.java` | `@DataJpaTest`; H2 in-memory; test queries |
| 7 | `PostgresIntegrationTest.java` | Testcontainers with real PostgreSQL |

### The testing philosophy
- **Service layer** → pure Mockito unit tests. No Spring context. Fast.
- **Controller layer** → `@WebMvcTest`. Only web layer. Fast.
- **Repository layer** → `@DataJpaTest`. Only JPA layer. Medium.
- **Integration** → `@SpringBootTest` + Testcontainers. Full stack. Slow, run less often.

---

## Phase 9 — Microservices
**Duration:** 3 weeks  
**Folder:** `14-microservices/`

### The right sequence

Microservices without service discovery, gateway, and resilience is just "distributed monolith." Build in this order:

**Week 1: Core Services**

| Day | Build | Key concept |
|-----|-------|-------------|
| 1–2 | `question-service/` + `quiz-service/` | Two independent Spring Boot apps on different ports |
| 3 | `RestTemplateDemo.java` → `WebClientDemo.java` | Why WebClient is preferred (non-blocking) |
| 4 | `ProductFeignClient.java` | Declarative HTTP client; `@FeignClient("product-service")` |
| 5–6 | Feign with Eureka | Resolve service names, not hardcoded URLs |
| 7 | Manual testing | Call service-to-service; see it work |

**Week 2: Infrastructure**

| Day | Build | Key concept |
|-----|-------|-------------|
| 8–9 | `EurekaServerApp.java` | Service registry; register both services |
| 10–11 | `GatewayApplication.java` | Single entry point; route config; path rewriting |
| 12 | Gateway filters | Auth header forwarding; rate limiting filter |
| 13 | `CircuitBreakerDemo.java` | Resilience4j; fallback method when service is down |
| 14 | `RetryDemo.java` + `TimeLimiterDemo.java` | Retry with backoff; timeout handling |

**Week 3: Full Project**

Build `17-real-world-projects/project-03-microservices-quiz-app/` end to end:
- Eureka Server (port 8761)
- API Gateway (port 8080) — JWT validation here
- User Service (port 8081) — registration/login
- Question Service (port 8082) — question CRUD
- Quiz Service (port 8083) — calls Question Service via Feign

### How to run and test microservices

```bash
# Start all with Docker Compose
docker-compose up -d

# Or start individually
cd eureka-server && mvn spring-boot:run &
cd question-service && mvn spring-boot:run &
cd quiz-service && mvn spring-boot:run &
cd api-gateway && mvn spring-boot:run

# All traffic goes through gateway
curl http://localhost:8080/api/questions
curl http://localhost:8080/api/quiz/1
```

---

## Phase 10 — Docker
**Duration:** 3–4 days  
**Folder:** `15-docker/`

### What you need as a Spring developer

You already know Docker conceptually. Focus on Spring-specific patterns:

1. **Multi-stage Dockerfile** — don't ship Maven + JDK in your image
2. **Layer optimization** — dependencies change less than code; order layers accordingly
3. **Docker Compose for dev** — Spring Boot + PostgreSQL + Redis in one command
4. **Testcontainers** — real DB containers in your tests (already covered in Phase 8)

**The production-grade Dockerfile:**
```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q  # Cache dependencies as layer
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2: Runtime (tiny image — no Maven, no JDK)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## Phase 11 — DSA
**Duration:** 2 weeks (parallel with other phases is OK)  
**Folder:** `16-dsa/`

### What to focus on for enterprise Java interviews

| Priority | Topic | Why |
|----------|-------|-----|
| Must know | Big-O analysis | Asked in every interview round |
| Must know | Arrays + two-pointer/sliding window | Most common LeetCode patterns |
| Must know | HashMap/HashSet problems | Frequency count, two-sum, etc. |
| Must know | Binary search | Used in library code; shows algorithmic thinking |
| Must know | Linked list basics | Reverse, detect cycle |
| Should know | Stacks/Queues | Valid parentheses, BFS/DFS |
| Should know | BST operations | In-order traversal = sorted output |
| Should know | Merge/Quick sort | Understanding sort stability |
| Nice to have | Dynamic programming | Only for senior roles |

**Do not over-invest here.** DSA is 20% of a Java backend interview. Spring/architecture is 80%. Get DSA to "can solve medium problems" and move on.

**Recommended daily practice:** 1 problem per day on LeetCode while learning Spring. Use Java.

---

## Phase 12 — Real World Projects
**Duration:** 4–6 weeks  
**Folder:** `17-real-world-projects/`

Build these in order. Each one adds to the previous:

### Project 1: Employee Management REST API
**What it is:** CRUD REST API with Spring Boot + JPA + MySQL + Validation + Exception Handling  
**What you practice:** Layers (Controller/Service/Repository), DTOs, global error handling  
**Test it:** Postman collection + MockMvc tests  
**Duration:** 3–4 days

### Project 2: E-Commerce API
**What it is:** Product + Order + Cart + User with JWT auth, AOP logging, Spring Security  
**What you practice:** Everything from phases 4–7 together  
**Test it:** Full test suite: unit + integration + Testcontainers  
**Duration:** 1–1.5 weeks

### Project 3: Microservices Quiz App
**What it is:** Decomposed version of the quiz domain into 3 services + Eureka + Gateway  
**What you practice:** Inter-service communication, resilience, distributed concerns  
**Test it:** Docker Compose; hit API Gateway; verify services call each other  
**Duration:** 1.5–2 weeks

### Project 4: Job Portal
**What it is:** Spring MVC + Thymeleaf + Security + JPA — traditional web app  
**What you practice:** Server-side rendering; form binding; Thymeleaf templates  
**Test it:** Browser testing + `@WebMvcTest`  
**Duration:** 1 week

---

## Overall Timeline (Aggressive, for job-ready in ~16 weeks)

```
Week 1–3   : Phase 0 + Phase 1 (Java Foundation)
Week 4     : Phase 2 (Maven + JDBC + Hibernate basics)
Week 5     : Phase 3 (Spring Core — IoC, DI, Beans)
Week 6–7   : Phase 4 (Spring Boot + REST API)
Week 8     : Phase 5 (Spring Data JPA)
Week 9–10  : Phase 6 (Spring Security + JWT + OAuth2)
Week 11    : Phase 7 (Spring AOP)
Week 12    : Phase 8 (Testing — JUnit5 + Mockito + MockMvc)
Week 13–15 : Phase 9 (Microservices)
Week 14    : Phase 10 (Docker — parallel with Phase 9)
Week 15–16 : Phase 11 (DSA — parallel with projects)
Week 13–18 : Phase 12 (Real World Projects — start after Phase 8)
```

---

## How To Use This Repo Day-to-Day

### The learning loop for each topic

```
1. READ the README.md of the module (5 min — big picture)
2. READ the explanation/*.md file (10–15 min — concept + diagrams)
3. READ the explanation/*.java file (15 min — annotated code)
4. RUN the Java file (2 min — see it work)
5. READ the exercise README.md (5 min — understand the task)
6. CODE the exercise from scratch (30–60 min)
7. RUN your exercise, compare output to solution
8. CHECK PROGRESS_TRACKER.md — mark complete
```

### Terminal commands you'll use constantly

```bash
# Start a new Spring Boot project (use Spring Initializr web UI, then)
cd my-new-project && mvn spring-boot:run

# Run tests
mvn test

# Run a specific test class
mvn test -Dtest=EmployeeControllerTest

# Package
mvn clean package

# Run the jar directly
java -jar target/myapp-0.0.1-SNAPSHOT.jar

# View all Spring beans loaded
# Add to main: ApplicationContext ctx = SpringApplication.run(...)
# ctx.getBeanDefinitionNames() → print all

# Watch logs in real time (after jar is running)
tail -f logs/application.log
```

### How to test a concept without a server

For phases 1–3 (pure Java + Spring Core), every demo file runs standalone:
```bash
# Right-click → Run 'main()' in IntelliJ
# OR:
mvn exec:java -Dexec.mainClass="com.learning.spring.BeanScopeDemo"
```

For phases 4+ (Spring Boot with HTTP), start the app and hit it with curl/HTTPie/Postman. Write MockMvc tests for automated coverage.

---

## Interview Preparation (Start after Phase 6)

Build `resources/interview-prep/` in parallel with projects. The questions to know cold:

**Java Core**
- Difference between `==` and `.equals()` for Strings
- What is the contract between `equals()` and `hashCode()`?
- What is a `ConcurrentModificationException`? How do you avoid it?
- Explain `volatile` keyword
- What are checked vs unchecked exceptions?
- How does HashMap work internally (hashCode → bucket → linked list/tree)?

**Spring**
- Explain IoC and DI — what problem do they solve?
- What is the difference between `@Component`, `@Service`, `@Repository`?
- What is the difference between `@Bean` and `@Component`?
- How does Spring Boot auto-configuration work?
- What is `@Transactional` and what can go wrong with it (self-invocation)?
- Explain Spring Security filter chain
- What is the N+1 problem? How do you fix it?

**Microservices**
- What is service discovery and why is it needed?
- Explain the circuit breaker pattern with states
- How would you handle a distributed transaction?
- What is an API Gateway? What does it do?
- Explain the difference between synchronous and asynchronous communication

---

## Progress Tracker Template

Copy this to `PROGRESS_TRACKER.md` and check boxes as you go:

```markdown
## Phase 0 — Setup
- [ ] JDK 21 installed and verified
- [ ] IntelliJ IDEA configured
- [ ] Maven installed
- [ ] Docker running
- [ ] Git repo initialized

## Phase 1 — Java Foundation
- [ ] 01-java-basics complete
- [ ] 02-oop-fundamentals complete
- [ ] 03-advanced-oop complete
- [ ] 04-strings-and-arrays complete
- [ ] 05-exception-handling complete
- [ ] 06-collections complete
- [ ] 07-functional-java complete
- [ ] 08-multithreading complete

## Phase 2 — Build Tools + Persistence
- [ ] 02-maven-build-tool complete
- [ ] 03-jdbc complete
- [ ] 04-hibernate-jpa/01-hibernate-basics complete
- [ ] 04-hibernate-jpa/02-jpa-annotations complete
- [ ] 04-hibernate-jpa/03-relationships complete
- [ ] 04-hibernate-jpa/04-advanced-jpa complete

## Phase 3 — Spring Core
- [ ] 05-spring-core/01-ioc-and-di complete
- [ ] 05-spring-core/02-bean-configuration complete
- [ ] 05-spring-core/03-bean-scopes-lifecycle complete

## Phase 4 — Spring Boot REST
- [ ] 06-spring-boot-fundamentals complete
- [ ] 07-spring-rest-api/01-05 complete
- [ ] 07-spring-rest-api/07-full-crud-project BUILT AND RUNNING

## Phase 5 — Spring Data JPA
- [ ] 08-spring-data-jpa/01-jpa-repository complete
- [ ] 08-spring-data-jpa/03-transactions complete

## Phase 6 — Security
- [ ] 10-spring-security complete
- [ ] 11-jwt-oauth2/01-jwt-deep-dive complete
- [ ] 11-jwt-oauth2/02-oauth2 complete

## Phase 7 — AOP
- [ ] 12-spring-aop complete

## Phase 8 — Testing
- [ ] 13-testing/01-unit-testing-junit5 complete
- [ ] 13-testing/02-mockito complete
- [ ] 13-testing/03-spring-boot-testing complete
- [ ] 13-testing/04-testcontainers complete

## Phase 9 — Microservices
- [ ] 14-microservices concepts
- [ ] 14-microservices/02-service-discovery complete
- [ ] 14-microservices/03-inter-service-communication complete
- [ ] 14-microservices/04-api-gateway complete
- [ ] 14-microservices/05-resilience complete

## Phase 10 — Docker
- [ ] 15-docker/01-docker-basics complete
- [ ] 15-docker/02-docker-compose complete

## Phase 11 — DSA
- [ ] 16-dsa/01-complexity-analysis complete
- [ ] 16-dsa/02-arrays-and-strings complete
- [ ] 16-dsa/03-linked-lists complete
- [ ] 16-dsa/04-stacks-and-queues complete
- [ ] 16-dsa/05-trees complete
- [ ] 16-dsa/06-sorting-algorithms complete

## Real World Projects
- [ ] project-01-employee-management-api COMPLETE + TESTED
- [ ] project-02-ecommerce-api COMPLETE + TESTED
- [ ] project-03-microservices-quiz-app COMPLETE + RUNNING
- [ ] project-04-job-portal COMPLETE + TESTED

## Interview Prep
- [ ] resources/cheatsheets all written
- [ ] resources/interview-prep/core-java-questions done
- [ ] resources/interview-prep/spring-questions done
- [ ] resources/interview-prep/microservices-questions done
```
