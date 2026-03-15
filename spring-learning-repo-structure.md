# Spring Boot Mastery — Complete Folder Structure (v2)

> **Profile:** Python/FastAPI engineer | Data Engineering | 12 years industry | New Java/Spring closure
> **Stack:** Java 21 + Spring Boot 3 + Spring Framework 6 + Gradle + PostgreSQL + Docker
> **Philosophy:** Every concept explained via Python comparison → use case origin story → ASCII diagram → working code → mini-project

---

## Three Repo-Wide Standards (Added to Every Module)

### Standard 1 — Mermaid Diagrams in Every `.md` File

Every `explanation/*.md` uses whichever diagram type best represents the concept. Supported types and when to use each:

| Mermaid Type | Syntax | Best For |
|---|---|---|
| `flowchart` | `flowchart TD` | Request flows, decision trees, algorithm steps, if/else logic |
| `sequenceDiagram` | `sequenceDiagram` | Service calls, auth flows, event chains, HTTP request lifecycle |
| `erDiagram` | `erDiagram` | JPA entity relationships, database schemas |
| `classDiagram` | `classDiagram` | OOP hierarchies, interface trees, design patterns |
| `stateDiagram-v2` | `stateDiagram-v2` | Bean lifecycle, circuit breaker states, thread states, order status |
| `mindmap` | `mindmap` | Module/topic overview inside README and MINDMAP files |
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

Each `.md` file annotation in the folder structure below shows the recommended Mermaid type(s) for that file.

### Standard 2 — MINDMAP.md Files

- Every **module** `README.md` contains an inline `mindmap` Mermaid diagram
- Every **sub-topic** with 3+ distinct concepts gets a dedicated `MINDMAP.md` file
- Every **mini-project** gets a `MINDMAP.md` showing which concepts it exercises

### Standard 3 — Interview Questions

- Each `explanation/*.md` file ends with an `## Interview Questions` section (3–8 Q&A per file)
- Questions are grouped: **Conceptual** → **Scenario/Debug** → **Quick Fire**
- All questions from all files are **aggregated** into `resources/interview-prep/` topic files
- Interview prep files are NOT written directly — they are built from per-topic file questions

---

## Repository Root

```
spring-mastery/
├── README.md                              ← Master index + how to navigate this repo
├── IMPLEMENTATION_PLAN.md                 ← Phased learning plan (see separate file)
├── PROGRESS_TRACKER.md                    ← Checkbox per topic — mark as done
├── build.gradle                           ← Root Gradle build (for multi-module projects)
├── settings.gradle                        ← Module declarations
│
├── setup/
│   ├── README.md                          ← Full environment setup guide
│   ├── install-java.md                    ← JDK 21 via SDKMAN
│   ├── install-intellij.md                ← IntelliJ setup + recommended plugins
│   ├── install-gradle.md                  ← Gradle wrapper setup
│   ├── install-docker.md                  ← Docker Desktop
│   ├── gradle-vs-maven.md                 ← Why Gradle; comparison with Maven/pip
│   └── verify-setup.sh                    ← Prints Java/Gradle/Docker versions
│
├── 00-java-foundation/
├── 01-advanced-java/
├── 02-gradle-build-tool/
├── 03-jdbc/
├── 04-hibernate-jpa/
├── 05-spring-core/
├── 06-spring-boot-fundamentals/
├── 07-spring-rest-api/
├── 08-spring-data-jpa/
├── 09-spring-mvc-thymeleaf/
├── 10-spring-security/
├── 11-jwt-oauth2/
├── 12-spring-aop/
├── 13-drools-rules-engine/
├── 14-testing/
├── 15-microservices/
├── 16-docker/
├── 17-real-world-projects/
└── resources/
    ├── cheatsheets/
    ├── interview-prep/
    └── architecture-patterns/
```

---

## Gradle Root Build Files

```
build.gradle
settings.gradle
gradle/
└── wrapper/
    ├── gradle-wrapper.jar
    └── gradle-wrapper.properties
gradlew                                    ← Unix Gradle wrapper
gradlew.bat                                ← Windows Gradle wrapper
```

**Template `build.gradle` for every Spring Boot module:**

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.learning'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '21'

repositories {
    mavenCentral()
}

dependencies {
    // Web + REST
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // JPA + Database
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'org.postgresql:postgresql'

    // Security
    implementation 'org.springframework.boot:spring-boot-starter-security'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'

    // Validation
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Actuator
    implementation 'org.springframework.boot:spring-boot-starter-actuator'

    // Testing
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testImplementation 'org.testcontainers:junit-jupiter'
    testImplementation 'org.testcontainers:postgresql'

    // Dev Tools
    developmentOnly 'org.springframework.boot:spring-boot-devtools'

    // Lombok (reduces boilerplate)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## 00 — Java Foundation

> **Python bridge:** Java is Python with static types, explicit compilation, and OOP at its core.
> Every file has Python comparison comments so you always have an anchor.

```
00-java-foundation/
├── README.md                              ← Module overview + Python→Java mental model map [mindmap diagram inside]
├── MINDMAP.md                             ← Full Java Foundation concept mindmap (mindmap)
├── build.gradle                           ← Standalone Gradle build for pure Java demos
│
├── 01-java-basics/
│   ├── README.md                          ← WHY: Java type system prevents runtime surprises Python has [mindmap inside]
│   ├── MINDMAP.md                         ← Java Basics mindmap: JVM/types/operators/control-flow
│   ├── explanation/
│   │   ├── 01-how-java-works.md           ← JDK/JRE/JVM + compilation pipeline [flowchart + C4Context] + interview Q&A
│   │   ├── 02-variables-datatypes.md      ← Primitives vs Python dynamic types [classDiagram primitives+wrappers] + interview Q&A
│   │   ├── 03-operators.md                ← Arithmetic/Relational/Logical/Bitwise/Ternary [flowchart precedence] + interview Q&A
│   │   ├── 04-control-flow.md             ← if/else/switch/for/while vs Python [stateDiagram-v2 loop states] + interview Q&A
│   │   ├── HowJavaWorks.java              ← Annotated: shows compilation → .class → JVM flow
│   │   ├── VariablesDemo.java             ← All types + autoboxing trap + ASCII type chart
│   │   ├── OperatorsDemo.java             ← All operators with Python equivalents in comments
│   │   └── ControlFlowDemo.java           ← Switch expressions (Java 14+) vs Python match
│   └── exercises/
│       ├── README.md
│       ├── Ex01_TypeConversion.java       ← Widening/narrowing; int→double, double→int trap
│       ├── Ex02_Calculator.java           ← Console calculator using Scanner
│       └── solutions/
│
├── 02-oop-fundamentals/
│   ├── README.md                          ← WHY: Python classes are loose; Java enforces contracts [mindmap inside]
│   ├── MINDMAP.md                         ← OOP mindmap: encapsulation/inheritance/polymorphism/abstraction
│   ├── explanation/
│   │   ├── 01-class-and-object.md         ← Blueprint vs instance; Python class vs Java class [classDiagram + sequenceDiagram new→heap] + interview Q&A
│   │   ├── 02-constructors.md             ← Default/parameterized/__init__ comparison; this() [sequenceDiagram chaining] + interview Q&A
│   │   ├── 03-encapsulation.md            ← private fields + getters/setters; Python _convention [classDiagram] + interview Q&A
│   │   ├── 04-inheritance.md              ← extends/super vs Python class(Parent); no multiple [classDiagram hierarchy] + interview Q&A
│   │   ├── 05-polymorphism.md             ← Overloading vs Overriding; runtime dispatch [sequenceDiagram dispatch] + interview Q&A
│   │   ├── 06-abstraction.md              ← Abstract class vs Interface [classDiagram + flowchart when-to-choose] + interview Q&A
│   │   ├── 07-access-modifiers.md         ← public/private/protected/default [classDiagram visibility grid] + interview Q&A
│   │   ├── 08-static-members.md           ← Class-level state; static blocks; Python @classmethod [classDiagram] + interview Q&A
│   │   ├── ClassAndObjectDemo.java        ← Vehicle class with ASCII class-to-object diagram
│   │   ├── InheritanceDemo.java           ← Animal→Dog→GuideDog with ASCII hierarchy
│   │   ├── PolymorphismDemo.java          ← Runtime dispatch; interface polymorphism
│   │   ├── AbstractDemo.java              ← Abstract Shape with area() — when to use abstract
│   │   └── InterfaceDemo.java             ← Payable interface; multiple interface impl
│   └── exercises/
│       ├── README.md
│       ├── Ex01_BankAccount.java          ← Encapsulated BankAccount; deposit/withdraw/balance
│       ├── Ex02_ShapeHierarchy.java       ← Abstract Shape → Circle, Rectangle, Triangle
│       ├── Ex03_PaymentInterface.java     ← Payable; CreditCard, UPI, NetBanking impls
│       └── solutions/
│
├── 03-advanced-oop/
│   ├── README.md                          ← WHY: These features power Spring's internal machinery [mindmap inside]
│   ├── MINDMAP.md                         ← Advanced OOP mindmap: generics/enums/annotations/inner classes
│   ├── explanation/
│   │   ├── 01-inner-classes.md            ← Static nested/inner/anonymous — when Spring uses each [classDiagram 4 types] + interview Q&A
│   │   ├── 02-enums.md                    ← Enum as full class; Python Enum comparison [stateDiagram-v2 OrderStatus] + interview Q&A
│   │   ├── 03-generics.md                 ← <T>, bounded wildcards; List<? extends Number> [classDiagram bounds + flowchart wildcard] + interview Q&A
│   │   ├── 04-annotations.md              ← @interface; meta-annotations; retention policy [classDiagram + sequenceDiagram processing] + interview Q&A
│   │   ├── 05-wrapper-classes.md          ← Autoboxing traps; Integer cache; null NPE danger [flowchart cache range] + interview Q&A
│   │   ├── 06-object-class.md             ← equals()/hashCode()/toString() contract [flowchart contract enforcement] + interview Q&A
│   │   ├── InnerClassDemo.java            ← Builder pattern using inner class
│   │   ├── EnumDemo.java                  ← OrderStatus enum with transitions and methods
│   │   ├── GenericsDemo.java              ← Generic Repository<T,ID> — preview of Spring Data
│   │   └── AnnotationDemo.java            ← Custom @Retry annotation — preview of Spring AOP
│   └── exercises/
│       ├── Ex01_GenericPair.java          ← Generic Pair<K,V> with equals/hashCode
│       ├── Ex02_StatusEnum.java           ← OrderStatus with allowed transitions
│       └── solutions/
│
├── 04-strings-and-arrays/
│   ├── README.md                          ← [mindmap inside]
│   ├── MINDMAP.md                         ← Strings/Arrays mindmap
│   ├── explanation/
│   │   ├── 01-strings.md                  ← Immutability; String pool; == vs .equals() trap [flowchart pool lookup + xychart-beta concat perf] + interview Q&A
│   │   ├── 02-stringbuilder.md            ← StringBuilder vs StringBuffer; Python list.join() [xychart-beta benchmark] + interview Q&A
│   │   ├── 03-arrays.md                   ← Fixed-size typed arrays; Python list comparison [classDiagram] + interview Q&A
│   │   ├── StringDemo.java                ← String pool ASCII diagram + common methods
│   │   ├── StringBuilderDemo.java         ← Performance comparison: String concat vs Builder
│   │   └── ArraysDemo.java                ← 1D/2D/jagged; Arrays utility class
│   └── exercises/
│       ├── Ex01_StringManipulation.java
│       ├── Ex02_MatrixOperations.java
│       └── solutions/
│
├── 05-exception-handling/
│   ├── README.md                          ← WHY: Java forces you to handle errors; Python is optional [mindmap inside]
│   ├── MINDMAP.md                         ← Exception hierarchy mindmap: Throwable/Error/Exception/Checked/Unchecked
│   ├── explanation/
│   │   ├── 01-exception-hierarchy.md      ← Throwable→Error/Exception→Checked/Unchecked [classDiagram tree + flowchart handle-or-declare] + interview Q&A
│   │   ├── 02-custom-exceptions.md        ← Domain exceptions; checked vs unchecked choice [flowchart when-to-check] + interview Q&A
│   │   ├── 03-try-with-resources.md       ← AutoCloseable = Python 'with' statement [sequenceDiagram close order] + interview Q&A
│   │   ├── 04-throws-vs-throw.md          ← throws (declare) vs throw (raise) [flowchart] + interview Q&A
│   │   ├── ExceptionHierarchyDemo.java    ← ASCII Throwable tree + checked vs unchecked demo
│   │   ├── CustomExceptionDemo.java       ← ProductNotFoundException, InsufficientStockException
│   │   └── TryWithResourcesDemo.java      ← DB connection auto-close pattern
│   └── exercises/
│       ├── Ex01_BankTransactionException.java
│       ├── Ex02_FileReaderWithResources.java
│       └── solutions/
│
├── 06-collections/
│   ├── README.md                          ← WHY: Enterprise Java lives in Collections; wrong choice = perf bug [mindmap inside]
│   ├── MINDMAP.md                         ← Full Collection hierarchy mindmap: List/Set/Map/Queue with implementations
│   ├── explanation/
│   │   ├── 01-collections-overview.md     ← ASCII hierarchy: Collection/Map tree [classDiagram full hierarchy + quadrantChart when-to-use] + interview Q&A
│   │   ├── 02-list.md                     ← ArrayList (Python list) vs LinkedList; when which [xychart-beta get/add/remove perf] + interview Q&A
│   │   ├── 03-set.md                      ← HashSet (Python set) vs LinkedHashSet vs TreeSet [classDiagram] + interview Q&A
│   │   ├── 04-map.md                      ← HashMap (Python dict) internals; LinkedHashMap vs TreeMap [flowchart hashCode→bucket→node] + interview Q&A
│   │   ├── 05-queue-deque.md              ← Queue/Deque/PriorityQueue; Python deque comparison [classDiagram] + interview Q&A
│   │   ├── 06-comparable-comparator.md    ← Comparable (natural order) vs Comparator (custom) [classDiagram] + interview Q&A
│   │   ├── CollectionHierarchyDemo.java   ← ASCII Big-O table for all collections in header
│   │   ├── ListDemo.java                  ← ArrayList ops with Python list equivalents in comments
│   │   ├── SetDemo.java                   ← HashSet uniqueness; TreeSet sorted iteration
│   │   ├── MapDemo.java                   ← HashMap CRUD; iteration patterns; Python dict compare
│   │   └── SortingDemo.java               ← Comparator.comparing() = Python key= lambda
│   └── exercises/
│       ├── Ex01_InventoryWithMap.java      ← Product inventory: stock count operations
│       ├── Ex02_StudentRanking.java        ← Sort students by GPA then name
│       └── solutions/
│
├── 07-functional-java/
│   ├── README.md                          ← WHY: Modern Java is functional; Spring uses lambdas everywhere [mindmap inside]
│   ├── MINDMAP.md                         ← Functional Java mindmap: lambdas/streams/optionals/method refs
│   ├── explanation/
│   │   ├── 01-functional-interfaces.md    ← Predicate/Function/Consumer/Supplier = Python callables [classDiagram] + interview Q&A
│   │   ├── 02-lambda-expressions.md       ← () → {} syntax; closures; Python lambda comparison [flowchart syntax forms] + interview Q&A
│   │   ├── 03-method-references.md        ← 4 types; Class::method; Python functools.partial [classDiagram 4 types] + interview Q&A
│   │   ├── 04-stream-api.md               ← filter/map/reduce/collect = Python generators [flowchart pipeline + sequenceDiagram lazy eval] + interview Q&A
│   │   ├── 05-optional.md                 ← null-safe pipeline; Python Optional[T] type hint [flowchart orElseThrow chain] + interview Q&A
│   │   ├── 06-parallel-streams.md         ← When to use; thread safety traps [xychart-beta seq vs parallel benchmark] + interview Q&A
│   │   ├── LambdaDemo.java                ← All functional interfaces with Python comments
│   │   ├── StreamApiDemo.java             ← Full pipeline with ASCII data-flow diagram
│   │   ├── OptionalDemo.java              ← ofNullable → map → orElseThrow patterns
│   │   └── ParallelStreamDemo.java        ← Performance benchmark: sequential vs parallel
│   └── exercises/
│       ├── Ex01_StreamPipeline.java        ← Filter orders > $100, map to DTO, collect
│       ├── Ex02_OptionalChain.java         ← Safe user lookup chain without null checks
│       └── solutions/
│
└── 08-multithreading/
    ├── README.md                          ← WHY: Spring Boot handles requests concurrently by default [mindmap inside]
    ├── MINDMAP.md                         ← Multithreading mindmap: Thread/Runnable/ExecutorService/locks/concurrent collections
    ├── explanation/
    │   ├── 01-threads-basics.md           ← Thread lifecycle [stateDiagram-v2 NEW→RUNNABLE→BLOCKED→WAITING→TERMINATED] + interview Q&A
    │   ├── 02-runnable-callable.md        ← Runnable vs Callable vs Thread; Python threading [classDiagram] + interview Q&A
    │   ├── 03-race-conditions.md          ← synchronized; volatile; Python GIL comparison [sequenceDiagram race condition] + interview Q&A
    │   ├── 04-executor-service.md         ← ThreadPoolExecutor; Executors factory; Python ThreadPool [flowchart submit→queue→worker→Future] + interview Q&A
    │   ├── 05-concurrent-collections.md  ← ConcurrentHashMap; CopyOnWriteArrayList [classDiagram] + interview Q&A
    │   ├── ThreadLifecycleDemo.java       ← ASCII: NEW→RUNNABLE→BLOCKED→WAITING→TERMINATED
    │   ├── RaceConditionDemo.java         ← Counter without sync (broken) vs with sync (fixed)
    │   ├── ExecutorServiceDemo.java       ← FixedThreadPool; submit() + Future.get()
    │   └── ConcurrentCollectionsDemo.java ← ConcurrentHashMap vs synchronized HashMap
    └── exercises/
        ├── Ex01_ProducerConsumer.java      ← BlockingQueue producer-consumer pattern
        ├── Ex02_ThreadPoolExample.java     ← Process 1000 tasks with ExecutorService
        └── solutions/

── MINI-PROJECT after 00-java-foundation ──────────────────────────────────────────
mini-project-00-library-cli/
├── README.md                              ← Build a Library CLI: Books, Members, Borrowing
├── src/main/java/com/learning/library/
│   ├── LibraryApp.java                    ← Main entry; console menu loop
│   ├── model/
│   │   ├── Book.java                      ← Entity with generics in collections
│   │   ├── Member.java                    ← Inherits from Person abstract class
│   │   └── BorrowRecord.java              ← Tracks who borrowed what when
│   ├── service/
│   │   ├── LibraryService.java            ← Interface with default methods
│   │   └── LibraryServiceImpl.java        ← Implements borrow/return/search
│   └── exception/
│       ├── BookNotFoundException.java
│       └── BookAlreadyBorrowedException.java
├── src/test/java/
│   └── LibraryServiceTest.java            ← JUnit5 unit tests — no Spring, pure Java
└── build.gradle
```

---

## 01 — Advanced Java

```
01-advanced-java/
├── README.md                              ← [mindmap inside: Spring IS design patterns]
├── MINDMAP.md                             ← Advanced Java mindmap: design patterns / IO / modern features
│
├── 01-design-patterns-java/
│   ├── README.md                          ← WHY: Spring IS design patterns — know them before Spring [mindmap inside]
│   ├── MINDMAP.md                         ← Design Patterns mindmap: Creational/Structural/Behavioral + Spring usage
│   ├── explanation/
│   │   ├── 01-singleton.md                ← Thread-safe singleton; enum singleton; Spring beans ARE singletons [classDiagram + sequenceDiagram double-checked locking] + interview Q&A
│   │   ├── 02-factory.md                  ← Factory method; BeanFactory in Spring uses this [classDiagram Creator→Product] + interview Q&A
│   │   ├── 03-builder.md                  ← Builder pattern; UriComponentsBuilder, MockMvcRequestBuilders [classDiagram fluent builder] + interview Q&A
│   │   ├── 04-strategy.md                 ← Swappable algorithms; Spring PasswordEncoder uses this [classDiagram Context→Strategy] + interview Q&A
│   │   ├── 05-observer.md                 ← Event + listener; Spring ApplicationEvent uses this [sequenceDiagram publisher→listeners] + interview Q&A
│   │   ├── 06-template-method.md          ← JdbcTemplate, RestTemplate use this internally [classDiagram abstract→concrete] + interview Q&A
│   │   ├── 07-decorator.md                ← Wrapping behaviour; Python @functools.wraps equivalent [classDiagram Component→Decorator] + interview Q&A
│   │   ├── 08-proxy.md                    ← JDK Proxy vs CGLIB; CRITICAL — Spring AOP runs on this [classDiagram proxy wrapping + flowchart JDK vs CGLIB selection] + interview Q&A
│   │   ├── SingletonDemo.java
│   │   ├── FactoryPatternDemo.java
│   │   ├── BuilderPatternDemo.java
│   │   ├── StrategyPatternDemo.java
│   │   ├── ObserverPatternDemo.java       ← Custom event bus — preview of Spring Events
│   │   ├── TemplateMethodDemo.java
│   │   └── ProxyPatternDemo.java          ← JDK Proxy wrapping target; ASCII proxy diagram
│   └── exercises/
│       ├── Ex01_LoggerSingleton.java
│       ├── Ex02_NotificationStrategy.java ← Email/SMS/Push strategy
│       └── solutions/
│
├── 02-java-io-nio/
│   ├── README.md                          ← [mindmap inside]
│   ├── explanation/
│   │   ├── 01-file-io.md                  ← File/FileReader/BufferedReader; Python open() comparison [flowchart read pipeline] + interview Q&A
│   │   ├── 02-serialization.md            ← Serializable; transient; serialVersionUID [sequenceDiagram serialize→deserialize] + interview Q&A
│   │   ├── 03-nio2.md                     ← Path/Files; modern Java IO [flowchart NIO2 vs IO] + interview Q&A
│   │   ├── FileIODemo.java
│   │   ├── SerializationDemo.java
│   │   └── NIO2Demo.java
│   └── exercises/
│
└── 03-modern-java-features/
    ├── README.md                          ← WHY: Spring Boot 3 requires Java 17+; these features are used [mindmap inside]
    ├── MINDMAP.md                         ← Modern Java mindmap: records/sealed/pattern matching/text blocks
    ├── explanation/
    │   ├── 01-records.md                  ← Java records as immutable DTOs; replaces boilerplate POJOs [classDiagram Record vs POJO] + interview Q&A
    │   ├── 02-sealed-classes.md           ← Closed type hierarchies; exhaustive switch [classDiagram sealed hierarchy] + interview Q&A
    │   ├── 03-pattern-matching.md         ← instanceof pattern matching; switch expressions [flowchart switch expression] + interview Q&A
    │   ├── 04-text-blocks.md              ← Multiline strings; great for SQL and JSON in tests [flowchart] + interview Q&A
    │   ├── RecordsDemo.java               ← ProductDTO as record; compare to 20-line POJO
    │   ├── SealedClassDemo.java           ← Result<T> sealed hierarchy; Python Union type compare
    │   └── PatternMatchingDemo.java
    └── exercises/
        ├── Ex01_ProductRecord.java
        └── solutions/
```

---

## 02 — Gradle Build Tool

```
02-gradle-build-tool/
├── README.md                              ← WHY Gradle over Maven; Python poetry/pip comparison [mindmap inside]
├── MINDMAP.md                             ← Gradle mindmap: tasks/dependencies/plugins/lifecycle/wrapper
│
├── 01-gradle-basics/
│   ├── README.md                          ← [mindmap inside]
│   ├── MINDMAP.md                         ← Gradle Basics mindmap
│   ├── explanation/
│   │   ├── 01-gradle-vs-maven.md          ← Groovy DSL vs XML; incremental builds; build cache [timeline Gradle history + quadrantChart speed/flexibility] + interview Q&A
│   │   ├── 02-build-gradle-structure.md   ← plugins/group/version/dependencies/tasks explained [flowchart 3 build phases: Init→Config→Execute] + interview Q&A
│   │   ├── 03-dependency-scopes.md        ← implementation/testImplementation/runtimeOnly/api [classDiagram scope relationships] + interview Q&A
│   │   ├── 04-gradle-tasks.md             ← build/test/bootRun/clean/dependencies tasks [flowchart task DAG] + interview Q&A
│   │   ├── 05-multi-module-gradle.md      ← settings.gradle; include ':module-name'; shared deps [C4Context root+modules] + interview Q&A
│   │   ├── 06-gradle-wrapper.md           ← gradlew; why wrapper is committed to git [flowchart] + interview Q&A
│   │   ├── 07-dependency-management.md    ← BOM (Bill of Materials); Spring dependency-management plugin [flowchart BOM resolution] + interview Q&A
│   │   ├── annotated-build.gradle         ← Every line commented with WHY
│   │   └── annotated-settings.gradle
│   └── exercises/
│       ├── README.md                      ← Create multi-module Gradle project with shared lib
│       └── solutions/
│           └── multi-module-demo/
│               ├── settings.gradle
│               ├── common-lib/
│               │   └── build.gradle
│               └── web-app/
│                   └── build.gradle
│
└── 02-gradle-advanced/
    ├── README.md
    ├── explanation/
    │   ├── 01-custom-tasks.md             ← Writing custom Gradle tasks in Groovy [flowchart] + interview Q&A
    │   ├── 02-build-profiles.md           ← Dev/test/prod config with Gradle [flowchart profile selection] + interview Q&A
    │   ├── 03-gradle-spring-plugin.md     ← spring-boot plugin: bootJar/bootRun/bootBuildImage [flowchart plugin task chain] + interview Q&A
    │   └── CustomTaskDemo.gradle
    └── exercises/
```

---

## 03 — JDBC

```
03-jdbc/
├── README.md                              ← Python psycopg2 vs Java JDBC — direct side-by-side [mindmap inside]
├── MINDMAP.md                             ← JDBC mindmap: Connection/Statement/ResultSet/Pool/Transaction
│
├── 01-jdbc-basics/
│   ├── README.md                          ← WHY JDBC: foundation for understanding what JPA eliminates [mindmap inside]
│   ├── explanation/
│   │   ├── 01-jdbc-architecture.md        ← ASCII: App → JDBC API → Driver Manager → DB Driver → DB [C4Context + flowchart connection lifecycle] + interview Q&A
│   │   ├── 02-connection-steps.md         ← DriverManager.getConnection(); Class.forName() [sequenceDiagram] + interview Q&A
│   │   ├── 03-statement-types.md          ← Statement vs PreparedStatement vs CallableStatement [classDiagram hierarchy] + interview Q&A
│   │   ├── 04-crud-with-jdbc.md           ← executeQuery (SELECT) vs executeUpdate (DML) [flowchart] + interview Q&A
│   │   ├── 05-resultset-processing.md     ← ResultSet cursor; column access by name vs index [sequenceDiagram cursor iteration] + interview Q&A
│   │   ├── 06-prepared-statement.md       ← SQL injection prevention; parameterized queries [sequenceDiagram attack vs safe] + interview Q&A
│   │   ├── 07-connection-pooling.md       ← HikariCP (Spring Boot default); why raw connections are bad [flowchart borrow→use→return + xychart-beta raw vs pool time] + interview Q&A
│   │   ├── 08-jdbc-transactions.md        ← conn.setAutoCommit(false); commit/rollback [sequenceDiagram begin→SQL1→SQL2→commit/rollback] + interview Q&A
│   │   ├── JDBCConnectionDemo.java        ← Annotated with ASCII connection lifecycle diagram
│   │   ├── StatementTypesDemo.java        ← Statement → PreparedStatement evolution with WHY
│   │   ├── CRUDWithJDBC.java              ← Full CRUD: 80 lines just for one table
│   │   ├── PreparedStatementDemo.java     ← SQL injection attack demo + fix
│   │   ├── ConnectionPoolDemo.java        ← HikariCP config; pool monitoring
│   │   └── JDBCTransactionDemo.java       ← Manual commit/rollback with ASCII flow
│   └── exercises/
│       ├── README.md                      ← Student CRUD with PostgreSQL via JDBC
│       ├── Ex01_StudentCRUD.java
│       ├── Ex02_BatchInsert.java          ← Insert 1000 rows with addBatch()/executeBatch()
│       └── solutions/
```

---

## 04 — Hibernate & JPA

```
04-hibernate-jpa/
├── README.md                              ← Python SQLAlchemy vs Java Hibernate — mental model map [mindmap inside]
├── MINDMAP.md                             ← Hibernate/JPA mindmap: ORM/Session/Annotations/Relationships/Cache/JPQL
│
├── 01-hibernate-basics/
│   ├── README.md                          ← WHY Hibernate: eliminates JDBC boilerplate; object-relational bridge [mindmap inside]
│   ├── MINDMAP.md                         ← Hibernate internals mindmap: SessionFactory/Session/L1-L2 cache/HQL
│   ├── explanation/
│   │   ├── 01-orm-concept.md              ← Object-Relational Mismatch; impedance mismatch explained [flowchart Object↔ORM↔Relational + erDiagram class→table] + interview Q&A
│   │   ├── 02-hibernate-architecture.md   ← ASCII: Entity → SessionFactory → Session → JDBC → DB [C4Component + sequenceDiagram persist→flush→SQL] + interview Q&A
│   │   ├── 03-hibernate-config.md         ← hibernate.cfg.xml vs persistence.xml vs Spring auto-config [flowchart config resolution] + interview Q&A
│   │   ├── 04-session-operations.md       ← save/persist/get/load/update/merge/delete semantics [stateDiagram-v2 entity states] + interview Q&A
│   │   ├── 05-hql.md                      ← HQL vs SQL vs JPQL; from clause uses class name [flowchart HQL→SQL translation] + interview Q&A
│   │   ├── 06-first-level-cache.md        ← Session-scoped L1 cache; identity map [sequenceDiagram first call→DB, second→cache] + interview Q&A
│   │   ├── 07-second-level-cache.md       ← SessionFactory-scoped L2; Ehcache/Redis providers [C4Container L1→L2→Redis] + interview Q&A
│   │   ├── HibernateSessionDemo.java      ← Open session → persist → query → close with ASCII flow
│   │   ├── HQLDemo.java                   ← HQL SELECT/UPDATE/DELETE with named params
│   │   └── CacheDemo.java                 ← L1 cache hit demo; L2 with Ehcache
│   └── exercises/
│       ├── Ex01_ProductPersist.java       ← Save/find/update/delete Product entity
│       └── solutions/
│
├── 02-jpa-annotations/
│   ├── README.md                          ← JPA = specification, Hibernate = implementation; like WSGI vs Gunicorn [mindmap inside]
│   ├── MINDMAP.md                         ← JPA Annotations mindmap: @Entity/@Table/@Column/@Id/@GeneratedValue/@Embeddable/@Inheritance
│   ├── explanation/
│   │   ├── 01-jpa-vs-hibernate.md         ← JPA spec vs Hibernate impl; why code to JPA interface [classDiagram spec→impl] + interview Q&A
│   │   ├── 02-entity-annotations.md       ← @Entity @Table @Column @Id @GeneratedValue with defaults [erDiagram entity→table + classDiagram annotations] + interview Q&A
│   │   ├── 03-primary-key-strategies.md   ← AUTO/IDENTITY/SEQUENCE/TABLE — which to use when [flowchart strategy decision tree] + interview Q&A
│   │   ├── 04-column-constraints.md       ← nullable/unique/length/precision in @Column [erDiagram] + interview Q&A
│   │   ├── 05-embeddable.md               ← @Embeddable/@Embedded for value objects (Address in User) [erDiagram embedded columns] + interview Q&A
│   │   ├── 06-lifecycle-callbacks.md      ← @PrePersist/@PostLoad/@PreUpdate/@PreRemove [stateDiagram-v2 entity lifecycle with callback hooks] + interview Q&A
│   │   ├── 07-inheritance-mapping.md      ← SINGLE_TABLE/JOINED/TABLE_PER_CLASS strategies [erDiagram per strategy + quadrantChart tradeoffs] + interview Q&A
│   │   ├── EntityAnnotationsDemo.java     ← Fully annotated Product entity with all annotations explained
│   │   ├── EmbeddableDemo.java            ← User + embedded Address + embedded ContactInfo
│   │   └── InheritanceDemo.java           ← Payment (JOINED) → CreditCard/BankTransfer/UPI
│   └── exercises/
│       ├── Ex01_OrderEntity.java          ← Order with embedded ShippingAddress
│       └── solutions/
│
├── 03-relationships/
│   ├── README.md                          ← WHY: 80% of JPA bugs are in relationships; master this [mindmap inside]
│   ├── MINDMAP.md                         ← JPA Relationships mindmap: OneToOne/OneToMany/ManyToMany/FetchType/Cascade/N+1
│   ├── explanation/
│   │   ├── 01-one-to-one.md               ← @OneToOne; owning vs inverse; @JoinColumn [erDiagram User↔UserProfile + sequenceDiagram bidirectional sync] + interview Q&A
│   │   ├── 02-one-to-many.md              ← @OneToMany/@ManyToOne; mappedBy; parent-child [erDiagram Department→Employees] + interview Q&A
│   │   ├── 03-many-to-many.md             ← @ManyToMany; @JoinTable; owning side; extra columns [erDiagram Students↔Courses join table] + interview Q&A
│   │   ├── 04-fetch-types.md              ← EAGER vs LAZY; N+1 problem explained; FetchType defaults [sequenceDiagram N+1 11 queries vs JOIN FETCH 1 query + xychart-beta query count scaling] + interview Q&A
│   │   ├── 05-cascade-types.md            ← ALL/PERSIST/MERGE/REMOVE/REFRESH/DETACH — when each [flowchart cascade decision] + interview Q&A
│   │   ├── 06-bidirectional-pitfalls.md   ← Infinite recursion; equals/hashCode; @JsonManagedReference [sequenceDiagram infinite recursion] + interview Q&A
│   │   ├── 07-entity-state-lifecycle.md   ← Transient→Persistent→Detached→Removed [stateDiagram-v2 all transitions] + interview Q&A
│   │   ├── OneToOneDemo.java              ← User ↔ UserProfile with ASCII ER + sequence diagram
│   │   ├── OneToManyDemo.java             ← Department → Employees; orphanRemoval explained
│   │   ├── ManyToManyDemo.java            ← Students ↔ Courses with enrolment date
│   │   └── FetchTypeDemo.java             ← N+1 problem: see 11 queries → fix with JOIN FETCH: 1 query
│   └── exercises/
│       ├── Ex01_LibraryMapping.java       ← Library/Book/Author/Member with all relationship types
│       ├── Ex02_ECommerceMapping.java     ← Order/OrderItem/Product/Customer
│       └── solutions/
│
└── 04-advanced-jpa/
    ├── README.md                          ← [mindmap inside]
    ├── MINDMAP.md                         ← Advanced JPA mindmap: JPQL/Native/Criteria/Projections/Pagination/Locking
    ├── explanation/
    │   ├── 01-jpql.md                     ← JPQL; named queries; @NamedQuery [flowchart JPQL→SQL] + interview Q&A
    │   ├── 02-native-queries.md           ← @Query(nativeQuery=true); when to drop to SQL [flowchart when-native decision] + interview Q&A
    │   ├── 03-criteria-api.md             ← Type-safe programmatic queries; CriteriaBuilder [flowchart dynamic WHERE construction] + interview Q&A
    │   ├── 04-projections.md              ← Interface projections; class projections (DTOs directly) [classDiagram projection types] + interview Q&A
    │   ├── 05-pagination-jpql.md          ← setFirstResult/setMaxResults; Pageable [sequenceDiagram pagination flow] + interview Q&A
    │   ├── 06-entity-graphs.md            ← @NamedEntityGraph; control fetch without changing mapping [flowchart graph vs fetch type] + interview Q&A
    │   ├── 07-optimistic-locking.md       ← @Version; OptimisticLockException; concurrent edit [sequenceDiagram two concurrent updates conflict] + interview Q&A
    │   ├── 08-pessimistic-locking.md      ← PESSIMISTIC_WRITE; SELECT FOR UPDATE [sequenceDiagram lock→update→release] + interview Q&A
    │   ├── JPQLDemo.java                  ← Parameterized JPQL; JOIN FETCH; aggregate functions
    │   ├── CriteriaAPIDemo.java           ← Dynamic search with multiple optional filters
    │   ├── ProjectionDemo.java            ← Interface projection vs DTO projection comparison
    │   └── LockingDemo.java               ← Optimistic lock conflict demo with retry
    └── exercises/
        ├── Ex01_DynamicSearch.java        ← Product search: name/category/price range all optional
        └── solutions/
```

---

## 05 — Spring Core

```
05-spring-core/
├── README.md                              ← The mental model: Spring is a smart object factory [mindmap inside]
├── MINDMAP.md                             ← Spring Core mindmap: IoC/DI/Beans/Scopes/Events/SpEL
│
├── 01-ioc-and-di/
│   ├── README.md                          ← WHY IoC: testability, decoupling, replaceability [mindmap inside]
│   ├── MINDMAP.md                         ← IoC & DI mindmap: BeanFactory/ApplicationContext/injection types
│   ├── explanation/
│   │   ├── 01-what-is-ioc.md              ← Hollywood Principle; manual wiring pain → Spring solves it [flowchart manual vs IoC + C4Context App/Container/Beans] + interview Q&A
│   │   ├── 02-dependency-injection.md     ← Constructor vs Setter vs Field injection; pros/cons each [classDiagram 3 types + sequenceDiagram constructor wiring at startup] + interview Q&A
│   │   ├── 03-spring-container.md         ← BeanFactory vs ApplicationContext; ASCII container diagram [C4Component ApplicationContext internals] + interview Q&A
│   │   ├── 04-bean-definition.md          ← How Spring reads, creates, wires beans at startup [flowchart scan→register→instantiate→inject] + interview Q&A
│   │   ├── 05-python-fastapi-vs-spring.md ← Direct map: FastAPI Depends() ↔ Spring @Autowired
│   │   ├── IoCConceptDemo.java            ← Manual wiring (100 lines) vs Spring wiring (10 lines) side-by-side
│   │   ├── ConstructorInjectionDemo.java  ← Best practice; immutable; testable; ASCII wiring diagram
│   │   ├── SetterInjectionDemo.java       ← Optional deps; circular dependency break
│   │   └── FieldInjectionDemo.java        ← Why NOT to use; breaks testability; shown then fixed
│   └── exercises/
│       ├── Ex01_ServiceLayerDI.java       ← Wire NotificationService with EmailSender + SMSSender
│       └── solutions/
│
├── 02-bean-configuration/
│   ├── README.md                          ← [mindmap inside]
│   ├── MINDMAP.md                         ← Bean Config mindmap: @Component/@Bean/@Profile/@Conditional/@Qualifier
│   ├── explanation/
│   │   ├── 01-component-scanning.md       ← @Component/@Service/@Repository/@Controller — semantics [flowchart scan→register] + interview Q&A
│   │   ├── 02-java-config.md              ← @Configuration + @Bean; when explicit beats scanning [sequenceDiagram config class processing] + interview Q&A
│   │   ├── 03-qualifier-primary.md        ← Multiple beans same type; @Qualifier name; @Primary fallback [flowchart resolution order] + interview Q&A
│   │   ├── 04-value-annotation.md         ← @Value("${property}"); SpEL expressions; default values [flowchart property resolution] + interview Q&A
│   │   ├── 05-profiles.md                 ← @Profile("dev"); environment-based beans; Python env vars compare [flowchart active profile→bean selection] + interview Q&A
│   │   ├── 06-conditional-beans.md        ← @Conditional; @ConditionalOnProperty; @ConditionalOnClass [flowchart conditional chain] + interview Q&A
│   │   ├── ComponentScanDemo.java         ← @ComponentScan basePackages; exclusion filters
│   │   ├── JavaConfigDemo.java            ← @Configuration class wiring; third-party bean registration
│   │   ├── QualifierDemo.java             ← Two DataSource beans; @Qualifier to select
│   │   ├── ProfileDemo.java               ← dev/prod beans; test-only beans
│   │   └── ConditionalDemo.java           ← @ConditionalOnProperty for feature flags
│   └── exercises/
│       ├── Ex01_MultiDataSource.java      ← Primary + secondary DB; @Qualifier selection
│       └── solutions/
│
├── 03-bean-scopes-lifecycle/
│   ├── README.md                          ← WHY: Wrong scope = shared mutable state = concurrency bug [mindmap inside]
│   ├── MINDMAP.md                         ← Bean Scopes mindmap: Singleton/Prototype/Request/Session + lifecycle hooks
│   ├── explanation/
│   │   ├── 01-bean-scopes.md              ← Singleton/Prototype/Request/Session/Application [stateDiagram-v2 Singleton vs Prototype lifecycle + flowchart scope decision] + interview Q&A
│   │   ├── 02-lifecycle-hooks.md          ← @PostConstruct (setup) / @PreDestroy (cleanup) [sequenceDiagram full 12-phase lifecycle] + interview Q&A
│   │   ├── 03-lazy-initialization.md      ← @Lazy; startup time; on-demand creation [sequenceDiagram eager vs lazy] + interview Q&A
│   │   ├── 04-scope-proxy.md              ← Injecting prototype into singleton with @ScopedProxy [sequenceDiagram proxy intercepts per-call] + interview Q&A
│   │   ├── BeanScopeDemo.java             ← Singleton=shared instance; Prototype=new per inject; ASCII
│   │   ├── BeanLifecycleDemo.java         ← ASCII full lifecycle: instantiate→inject→@PostConstruct→use→@PreDestroy
│   │   └── ScopeProxyDemo.java            ← Prototype scoped service injected into singleton controller
│   └── exercises/
│       ├── Ex01_RequestScopedCart.java    ← Shopping cart as request-scoped bean
│       └── solutions/
│
├── 04-spring-events/
│   ├── README.md                          ← WHY: Loose coupling between modules; Python signal/blinker compare [mindmap inside]
│   ├── MINDMAP.md                         ← Spring Events mindmap: built-in events/custom events/async/@TransactionalEventListener
│   ├── explanation/
│   │   ├── 01-built-in-events.md          ← ContextRefreshedEvent/ContextClosedEvent/ApplicationReadyEvent [sequenceDiagram startup event sequence] + interview Q&A
│   │   ├── 02-custom-events.md            ← Extend ApplicationEvent; publish via ApplicationEventPublisher [sequenceDiagram publish→2 async listeners] + interview Q&A
│   │   ├── 03-event-listeners.md          ← @EventListener; @TransactionalEventListener [sequenceDiagram AFTER_COMMIT timing] + interview Q&A
│   │   ├── 04-async-events.md             ← @Async @EventListener; non-blocking event handling [sequenceDiagram async thread dispatch] + interview Q&A
│   │   ├── CustomEventDemo.java           ← OrderPlacedEvent → EmailListener + InventoryListener
│   │   └── AsyncEventDemo.java            ← @Async listener; ThreadPoolTaskExecutor config
│   └── exercises/
│       ├── Ex01_UserRegisteredEvent.java  ← UserRegistered → WelcomeEmail + AuditLog async
│       └── solutions/
│
└── 05-spring-expression-language/
    ├── README.md                          ← WHY: SpEL powers @Value, @PreAuthorize, @Cacheable [mindmap inside]
    ├── explanation/
    │   ├── 01-spel-basics.md              ← #{...} syntax; property access; method calls [flowchart SpEL expression parsing] + interview Q&A
    │   ├── 02-spel-in-annotations.md      ← @Value("#{systemProperties['os.name']}") [flowchart resolution] + interview Q&A
    │   ├── 03-spel-in-security.md         ← @PreAuthorize("hasRole('ADMIN') and #id == authentication.id") [sequenceDiagram SpEL eval in security] + interview Q&A
    │   └── SpELDemo.java                  ← ExpressionParser; parsing and evaluating expressions
    └── exercises/

── MINI-PROJECT after 05-spring-core ──────────────────────────────────────────────
mini-project-01-notification-engine/
├── README.md                              ← Pure Spring Core: no Spring Boot, no HTTP
├── src/main/java/com/learning/notify/
│   ├── NotificationApp.java               ← AnnotationConfigApplicationContext main
│   ├── config/
│   │   └── AppConfig.java                 ← @Configuration with all beans
│   ├── service/
│   │   ├── NotificationService.java       ← Interface
│   │   ├── EmailNotificationService.java  ← @Service @Profile("email")
│   │   └── SMSNotificationService.java    ← @Service @Profile("sms")
│   ├── event/
│   │   ├── AlertEvent.java                ← Custom ApplicationEvent
│   │   └── AlertEventListener.java        ← @EventListener
│   └── config/
│       └── application.properties
└── src/test/
    └── NotificationServiceTest.java
```

---

## 06 — Spring Boot Fundamentals

```
06-spring-boot-fundamentals/
├── README.md                              ← Spring Boot = Spring + Auto-config + Embedded Server + Starters [mindmap inside]
├── MINDMAP.md                             ← Spring Boot mindmap: internals/starters/config/actuator/beans-deep-dive
│
├── 01-spring-boot-internals/
│   ├── README.md                          ← WHY: Know the magic to debug when it breaks [mindmap inside]
│   ├── MINDMAP.md                         ← Spring Boot Internals mindmap: @SpringBootApplication/auto-config/fat-jar/startup
│   ├── explanation/
│   │   ├── 01-spring-vs-springboot.md     ← What Boot adds; XML hell → zero config [timeline Spring→Boot + flowchart] + interview Q&A
│   │   ├── 02-springbootapplication.md    ← @SpringBootApplication = @Configuration + @ComponentScan + @EnableAutoConfiguration [flowchart decomposed annotation] + interview Q&A
│   │   ├── 03-auto-configuration.md       ← spring.factories / AutoConfiguration.imports; conditional beans [sequenceDiagram classpath→conditional→configure + flowchart @ConditionalOnClass] + interview Q&A
│   │   ├── 04-spring-factories.md         ← How Spring Boot discovers auto-config classes [flowchart discovery mechanism] + interview Q&A
│   │   ├── 05-embedded-server.md          ← Tomcat/Jetty/Undertow; no WAR deployment [C4Container embedded server] + interview Q&A
│   │   ├── 06-fat-jar.md                  ← Executable JAR; BOOT-INF; nested JARs [block-beta JAR structure] + interview Q&A
│   │   ├── 07-startup-sequence.md         ← ASCII: main() → SpringApplication.run() → refresh → ready [sequenceDiagram full startup] + interview Q&A
│   │   ├── SpringBootAppDemo.java          ← First app; listing all auto-configured beans
│   │   └── AutoConfigExplorer.java        ← Print all auto-config conditions at startup
│   └── exercises/
│
├── 02-starters-and-dependencies/
│   ├── README.md                          ← WHY: Starters solve dependency version hell; like pyproject.toml [mindmap inside]
│   ├── explanation/
│   │   ├── 01-spring-boot-starters.md     ← starter-web/data-jpa/security/test/actuator — what each pulls in [flowchart starter→transitive deps] + interview Q&A
│   │   ├── 02-parent-gradle-plugin.md     ← spring-boot Gradle plugin; dependency-management BOM [flowchart BOM version resolution] + interview Q&A
│   │   ├── 03-version-overriding.md       ← How to pin a specific dependency version [flowchart override resolution] + interview Q&A
│   │   ├── 04-transitive-dependencies.md  ← ./gradlew dependencies; reading the tree [flowchart dependency tree] + interview Q&A
│   │   └── annotated-build.gradle         ← Complete annotated build.gradle with every dependency explained
│   └── exercises/
│
├── 03-configuration-management/
│   ├── README.md                          ← WHY: 12-factor app config; one binary, many environments [mindmap inside]
│   ├── MINDMAP.md                         ← Config Management mindmap: YAML/profiles/properties/env vars/config server
│   ├── explanation/
│   │   ├── 01-application-yml.md          ← YAML structure; properties hierarchy; relaxed binding [flowchart YAML→properties binding] + interview Q&A
│   │   ├── 02-config-hierarchy.md         ← CLI args > env vars > application-{profile}.yml > application.yml [flowchart priority resolution] + interview Q&A
│   │   ├── 03-configuration-properties.md ← @ConfigurationProperties — type-safe config class [classDiagram POJO with prefix] + interview Q&A
│   │   ├── 04-profiles-config.md          ← application-dev.yml/application-prod.yml; @ActiveProfiles [flowchart profile activation] + interview Q&A
│   │   ├── 05-env-variables.md            ← SPRING_DATASOURCE_URL overrides datasource.url [flowchart env binding] + interview Q&A
│   │   ├── 06-config-server.md            ← Spring Cloud Config Server concept (intro) [C4Context config server arch] + interview Q&A
│   │   ├── AppConfigDemo.java             ← @Value vs @ConfigurationProperties comparison
│   │   └── DatabaseConfig.java            ← @ConfigurationProperties(prefix="app.db") POJO
│   └── exercises/
│       ├── Ex01_MultiEnvConfig.md         ← Same app; dev points to H2; prod points to PostgreSQL
│       └── solutions/
│
├── 04-actuator-devtools/
│   ├── README.md                          ← WHY: Production observability; health checks for K8s probes [mindmap inside]
│   ├── explanation/
│   │   ├── 01-actuator-endpoints.md       ← /health /info /metrics /env /beans /mappings explained [flowchart endpoint→data] + interview Q&A
│   │   ├── 02-custom-health-indicator.md  ← HealthIndicator interface; custom checks [classDiagram HealthIndicator] + interview Q&A
│   │   ├── 03-securing-actuator.md        ← Expose only /health and /info publicly; secure rest [flowchart endpoint security config] + interview Q&A
│   │   ├── 04-micrometer-metrics.md       ← Counter/Gauge/Timer; Prometheus integration [C4Context Micrometer→Prometheus→Grafana] + interview Q&A
│   │   ├── 05-devtools.md                 ← Auto-restart; live reload; classpath monitoring [sequenceDiagram file change→restart] + interview Q&A
│   │   ├── ActuatorDemo.java              ← Custom health + custom info contributor
│   │   └── CustomMetricsDemo.java         ← MeterRegistry; counting API calls per endpoint
│   └── exercises/
│
└── 05-spring-boot-beans-deep-dive/
    ├── README.md                          ← WHY: Beans ARE Spring; misconfigured beans = silent bugs [mindmap inside]
    ├── MINDMAP.md                         ← Bean Deep Dive mindmap: BPP/BFPP/Aware/FactoryBean/circular deps/full lifecycle
    ├── explanation/
    │   ├── 01-bean-lifecycle-complete.md  ← Full lifecycle [stateDiagram-v2 all 12 phases + sequenceDiagram BPP wrapping] + interview Q&A
    │   ├── 02-beanpostprocessor.md        ← BeanPostProcessor; before/after init hooks; how AOP uses this [sequenceDiagram BPP intercept] + interview Q&A
    │   ├── 03-beanfactorypostprocessor.md ← Modify bean definitions before instantiation [sequenceDiagram BFPP before instantiation] + interview Q&A
    │   ├── 04-aware-interfaces.md         ← BeanNameAware/ApplicationContextAware/EnvironmentAware [classDiagram Aware hierarchy] + interview Q&A
    │   ├── 05-factorybean.md              ← FactoryBean<T>; how Spring Data repositories are created [sequenceDiagram FactoryBean→getObject] + interview Q&A
    │   ├── 06-bean-dependency-resolution.md ← How Spring resolves; circular deps; @Lazy break [flowchart resolution + circular dep fix] + interview Q&A
    │   ├── FullBeanLifecycleDemo.java     ← Implements every lifecycle interface; prints each phase
    │   ├── BeanPostProcessorDemo.java     ← Custom BPP that logs every bean creation
    │   └── FactoryBeanDemo.java           ← Custom FactoryBean creating connection pool
    └── exercises/
        ├── Ex01_CustomBeanPostProcessor.java ← BPP that validates @NotNull fields on beans
        └── solutions/

── MINI-PROJECT after 06-spring-boot-fundamentals ─────────────────────────────────
mini-project-02-config-demo-app/
├── README.md                              ← App that changes behaviour based on active profile
├── src/main/java/com/learning/config/
│   ├── ConfigDemoApp.java
│   ├── config/
│   │   ├── AppProperties.java             ← @ConfigurationProperties(prefix="app")
│   │   └── DataSourceConfig.java          ← Profile-based DataSource beans
│   ├── controller/
│   │   └── InfoController.java            ← /info returns active profile + config values
│   └── health/
│       └── ExternalServiceHealthIndicator.java
└── src/main/resources/
    ├── application.yml
    ├── application-dev.yml
    └── application-prod.yml
```

---

## 07 — Spring REST API

```
07-spring-rest-api/
├── README.md                              ← FastAPI vs Spring Boot REST — direct comparison table [mindmap inside]
├── MINDMAP.md                             ← Spring REST mindmap: REST principles/controllers/DTOs/validation/exceptions/swagger/versioning
│
├── 01-rest-fundamentals/
│   ├── README.md                          ← WHY REST: stateless, uniform interface, scalable
│   ├── explanation/
│   │   ├── 01-rest-principles.md          ← 6 REST constraints; resource-based URLs; stateless [mindmap 6 constraints + flowchart good REST decision] + interview Q&A
│   │   ├── 02-http-methods-semantics.md   ← GET/POST/PUT/PATCH/DELETE — idempotency; safety [quadrantChart idempotent/safe matrix] + interview Q&A
│   │   ├── 03-http-status-codes.md        ← 2xx/3xx/4xx/5xx with when-to-use for each
│   │   ├── 04-json-and-jackson.md         ← Jackson ObjectMapper; @JsonProperty; custom serializer [sequenceDiagram JSON→Jackson→DTO→Jackson→JSON] + interview Q&A
│   │   ├── 05-content-negotiation.md      ← Accept/Content-Type headers; produces/consumes
│   │   ├── 06-api-design-best-practices.md ← Resource naming; versioning; error responses; pagination
│   │   ├── JacksonDemo.java               ← @JsonIgnore/@JsonAlias/@JsonFormat; custom ser/deser
│   │   └── ContentNegotiationDemo.java    ← Produce JSON + XML from same endpoint
│   └── exercises/
│
├── 02-rest-controller/
│   ├── README.md                          ← WHY: @RestController = @Controller + @ResponseBody; saves boilerplate
│   ├── explanation/
│   │   ├── 01-rest-controller.md          ← @RestController vs @Controller; when each [flowchart request→DispatcherServlet→HandlerMapping→Controller] + interview Q&A
│   │   ├── 02-request-mappings.md         ← @GetMapping/@PostMapping/@PutMapping/@PatchMapping/@DeleteMapping
│   │   ├── 03-path-variables.md           ← @PathVariable; multiple vars; regex constraints
│   │   ├── 04-request-params.md           ← @RequestParam; required/defaultValue; multi-value
│   │   ├── 05-request-body.md             ← @RequestBody; DTO class; Jackson deserialization
│   │   ├── 06-response-entity.md          ← ResponseEntity<T>; status/headers/body control [flowchart response building] + interview Q&A
│   │   ├── 07-http-headers.md             ← @RequestHeader; @ResponseHeader; custom headers
│   │   ├── 08-request-multipart.md        ← @RequestPart; file upload with MultipartFile
│   │   ├── BasicRestController.java       ← Annotated with ASCII HTTP request→response flow
│   │   ├── PathVariableDemo.java          ← /products/{id}/reviews/{reviewId}
│   │   ├── RequestBodyDemo.java           ← CreateProductRequest DTO with validation
│   │   ├── ResponseEntityDemo.java        ← Different status codes; Location header on create
│   │   └── FileUploadController.java      ← MultipartFile; save to disk; return URL
│   └── exercises/
│       ├── Ex01_ProductController.java    ← CRUD endpoints for Product resource
│       └── solutions/
│
├── 03-dto-pattern/
│   ├── README.md                          ← WHY DTOs: decouple API contract from DB entity; hide fields
│   ├── explanation/
│   │   ├── 01-dto-pattern.md              ← Request DTO / Response DTO / entity separation; Pydantic compare
│   │   ├── 02-model-mapper.md             ← ModelMapper; MapStruct; manual mapping comparison
│   │   ├── 03-dto-validation.md           ← Validate on DTO not entity; @Valid; groups
│   │   ├── ProductRequestDTO.java         ← Incoming create/update fields + validation annotations
│   │   ├── ProductResponseDTO.java        ← Outgoing fields; no password/internal fields
│   │   ├── ProductMapper.java             ← Manual mapper; entity↔DTO conversion methods
│   │   └── DTOValidationDemo.java
│   └── exercises/
│
├── 04-exception-handling/
│   ├── README.md                          ← WHY: Consistent error responses are a contract with your clients
│   ├── explanation/
│   │   ├── 01-exception-handling-rest.md  ← @ExceptionHandler per controller; limitations [sequenceDiagram exception→ControllerAdvice→error response] + interview Q&A
│   │   ├── 02-controller-advice.md        ← @ControllerAdvice = global handler; @RestControllerAdvice
│   │   ├── 03-problem-details.md          ← RFC 7807 ProblemDetail; Spring 6 built-in support
│   │   ├── 04-standard-error-response.md  ← Standard ErrorResponse DTO with timestamp/path/message
│   │   ├── 05-validation-errors.md        ← MethodArgumentNotValidException; per-field errors
│   │   ├── GlobalExceptionHandler.java    ← Full @RestControllerAdvice: all common exception types
│   │   ├── AppExceptions.java             ← All custom exceptions: ResourceNotFound/Conflict/Business
│   │   ├── ErrorResponse.java             ← Standard error DTO (Pydantic model comparison)
│   │   └── ValidationErrorResponse.java   ← Field-level validation errors list
│   └── exercises/
│       ├── Ex01_GlobalExceptionHandling.java
│       └── solutions/
│
├── 05-validation/
│   ├── README.md                          ← WHY: Never trust client input; validate at boundary
│   ├── explanation/
│   │   ├── 01-bean-validation.md          ← @NotNull/@NotBlank/@Size/@Email/@Pattern/@Min/@Max/@Positive [flowchart @Valid→ConstraintViolation→400] + interview Q&A
│   │   ├── 02-custom-validators.md        ← @Constraint + ConstraintValidator<Annotation, Type>
│   │   ├── 03-cross-field-validation.md   ← @AssertTrue on class level; password=confirmPassword
│   │   ├── 04-validation-groups.md        ← @Validated(CreateGroup.class) vs UpdateGroup.class
│   │   ├── 05-method-validation.md        ← @Validated at service level; @NotNull on params
│   │   ├── ValidationAnnotationsDemo.java ← All built-in validators with failure messages
│   │   ├── CustomValidatorDemo.java       ← @PhoneNumber custom validator
│   │   └── CrossFieldValidationDemo.java  ← @PasswordMatch class-level constraint
│   └── exercises/
│
├── 06-openapi-swagger/
│   ├── README.md                          ← WHY: FastAPI does this for free; Spring needs springdoc; same result
│   ├── explanation/
│   │   ├── 01-springdoc-openapi.md        ← springdoc-openapi dependency; /swagger-ui.html [sequenceDiagram OpenAPI spec generation] + interview Q&A
│   │   ├── 02-api-annotations.md          ← @Operation/@ApiResponse/@Schema/@Parameter
│   │   ├── 03-global-api-info.md          ← @OpenAPIDefinition; API title/version/contact
│   │   ├── 04-auth-in-swagger.md          ← JWT Bearer in Swagger UI; securitySchemes
│   │   ├── SwaggerConfig.java             ← OpenAPI bean with JWT security scheme
│   │   └── AnnotatedProductController.java ← Every endpoint fully documented
│   └── exercises/
│
├── 07-rest-api-versioning/
│   ├── README.md                          ← WHY: APIs evolve; breaking changes need versioning strategy
│   ├── explanation/
│   │   ├── 01-versioning-strategies.md    ← URI (/v1/) vs Header (X-API-Version) vs Accept (application/vnd.v1+json) [quadrantChart versioning strategy tradeoffs] + interview Q&A
│   │   ├── 02-uri-versioning.md           ← Most common; /api/v1/ vs /api/v2/
│   │   ├── 03-header-versioning.md        ← Content negotiation via custom header
│   │   ├── V1ProductController.java       ← v1 contracts
│   │   └── V2ProductController.java       ← v2 with breaking changes
│   └── exercises/
│
└── 08-full-crud-project/
    ├── README.md                          ← Employee Management REST API spec + architecture
    ├── employee-service/                  ← Full Spring Boot project
    │   ├── build.gradle
    │   └── src/
    │       ├── main/java/com/learning/employee/
    │       │   ├── EmployeeServiceApp.java
    │       │   ├── controller/
    │       │   │   └── EmployeeController.java   ← @RestController with full CRUD
    │       │   ├── service/
    │       │   │   ├── EmployeeService.java      ← Interface
    │       │   │   └── EmployeeServiceImpl.java
    │       │   ├── repository/
    │       │   │   └── EmployeeRepository.java   ← JpaRepository
    │       │   ├── entity/
    │       │   │   └── Employee.java             ← @Entity with all annotations
    │       │   ├── dto/
    │       │   │   ├── EmployeeCreateRequest.java
    │       │   │   ├── EmployeeUpdateRequest.java
    │       │   │   └── EmployeeResponse.java
    │       │   └── exception/
    │       │       ├── EmployeeNotFoundException.java
    │       │       └── GlobalExceptionHandler.java
    │       └── resources/
    │           ├── application.yml
    │           ├── application-dev.yml
    │           └── application-prod.yml
    └── testing-guide.md                   ← curl + HTTPie + Postman examples for every endpoint

── MINI-PROJECT after 07-spring-rest-api ──────────────────────────────────────────
(same as 08-full-crud-project above — it IS the mini-project for this phase)
```

---

## 08 — Spring Data JPA

```
08-spring-data-jpa/
├── README.md                              ← Python SQLAlchemy session vs Spring Data repository pattern [mindmap inside]
├── MINDMAP.md                             ← Spring Data JPA mindmap: JpaRepository/derived queries/pagination/specifications/@Transactional
│
├── 01-spring-data-overview/
│   ├── README.md                          ← WHY Spring Data: eliminates DAO boilerplate; repo abstraction
│   ├── explanation/
│   │   ├── 01-repository-hierarchy.md     ← ASCII: Repository→CrudRepository→PagingAndSorting→JpaRepository [classDiagram full hierarchy] + interview Q&A
│   │   ├── 02-jpa-repository.md           ← JpaRepository<Entity, ID>; built-in methods
│   │   ├── 03-derived-query-methods.md    ← Method name parsing; findByNameAndAgeGreaterThan [flowchart method name→SQL parsing] + interview Q&A
│   │   ├── 04-jpql-queries.md             ← @Query JPQL; named params; modifying queries
│   │   ├── 05-native-queries.md           ← @Query(nativeQuery=true); when to use
│   │   ├── 06-projections.md              ← Interface projections; reduce fields returned
│   │   ├── 07-pagination-sorting.md       ← Pageable; PageRequest.of(); Page<T> response [sequenceDiagram controller→Pageable→repo→Page→DTO] + interview Q&A
│   │   ├── 08-specifications.md           ← JpaSpecificationExecutor; dynamic query building [flowchart optional filters→Spec.and()→WHERE clause] + interview Q&A
│   │   ├── ProductRepository.java         ← Annotated with all query types demonstrated
│   │   ├── PaginationDemo.java            ← Page + Sort; converting to PageResponse DTO
│   │   ├── ProjectionDemo.java            ← Full entity vs interface projection vs DTO
│   │   └── SpecificationDemo.java         ← Dynamic product search with multiple optional filters
│   └── exercises/
│       ├── Ex01_OrderRepository.java      ← Order search: customer/status/date range/amount
│       └── solutions/
│
├── 02-transactions/
│   ├── README.md                          ← WHY: Data consistency; partial failure recovery
│   ├── explanation/
│   │   ├── 01-transactional-annotation.md ← @Transactional; what it wraps; commit/rollback rules [sequenceDiagram proxy intercept→begin→method→commit/rollback] + interview Q&A
│   │   ├── 02-propagation-types.md        ← REQUIRED/REQUIRES_NEW/NESTED/SUPPORTS/NOT_SUPPORTED [flowchart propagation decision tree + sequenceDiagram REQUIRES_NEW nested tx] + interview Q&A
│   │   ├── 03-isolation-levels.md         ← READ_UNCOMMITTED/COMMITTED/REPEATABLE_READ/SERIALIZABLE [quadrantChart isolation vs performance] + interview Q&A
│   │   ├── 04-rollback-rules.md           ← Default: RuntimeException rolls back; checked does NOT
│   │   ├── 05-readonly-transactions.md    ← readOnly=true optimization; Hibernate flush mode NEVER
│   │   ├── 06-self-invocation-trap.md     ← Calling @Transactional method from same class bypasses proxy [sequenceDiagram self-call bypasses proxy] + interview Q&A
│   │   ├── 07-transactional-events.md     ← @TransactionalEventListener; AFTER_COMMIT phase
│   │   ├── TransactionDemo.java           ← Transfer with rollback; ASCII transaction flow
│   │   ├── PropagationDemo.java           ← REQUIRED vs REQUIRES_NEW in bank transfer
│   │   └── SelfInvocationTrap.java        ← Broken example + fix using ApplicationContext
│   └── exercises/
│       ├── Ex01_BankTransfer.java         ← Transactional transfer; rollback on insufficient funds
│       ├── Ex02_NestedTransactions.java   ← REQUIRES_NEW for audit logging independent of main tx
│       └── solutions/
│
└── 03-spring-data-rest/
    ├── README.md                          ← WHY: Zero-code REST for simple CRUD; HATEOAS
    ├── explanation/
    │   ├── 01-spring-data-rest.md         ← @RepositoryRestResource; auto-exposed endpoints
    │   ├── 02-hateoas.md                  ← HAL format; _links; self/collection relations
    │   ├── 03-customizing.md              ← @RestResource(exported=false); custom base path
    │   ├── DataRestDemo.java              ← Three annotations to get full CRUD + pagination
    │   └── application-datarest.yml       ← spring.data.rest config options
    └── exercises/

── MINI-PROJECT after 08-spring-data-jpa ──────────────────────────────────────────
mini-project-03-product-catalogue/
├── README.md                              ← Product Catalogue API with search, pagination, categories
├── src/main/java/com/learning/catalogue/
│   ├── entity/
│   │   ├── Product.java                   ← @Entity with Category @ManyToOne
│   │   └── Category.java
│   ├── repository/
│   │   ├── ProductRepository.java         ← Derived queries + @Query + Specification
│   │   └── CategoryRepository.java
│   ├── service/
│   │   └── ProductService.java            ← @Transactional service; pagination
│   ├── controller/
│   │   └── ProductController.java         ← /products?page=0&size=10&sort=price,desc
│   └── dto/
│       ├── ProductCreateRequest.java
│       ├── ProductResponse.java
│       └── PageResponse.java              ← Wrapper DTO for paginated responses
└── src/test/
    └── ProductRepositoryTest.java         ← @DataJpaTest with H2
```

---

## 09 — Spring MVC & Thymeleaf

```
09-spring-mvc-thymeleaf/
├── README.md                              ← Jinja2 vs Thymeleaf; DispatcherServlet internals [mindmap inside]
├── MINDMAP.md                             ← Spring MVC mindmap: DispatcherServlet/HandlerMapping/ViewResolver/Thymeleaf/forms
│
├── 01-spring-mvc-internals/
│   ├── README.md                          ← WHY: Server-side rendering for admin dashboards, legacy apps
│   ├── explanation/
│   │   ├── 01-dispatcher-servlet.md       ← Full request lifecycle ASCII: browser→DS→HandlerMapping→Controller→View
│   │   ├── 02-handler-mapping.md          ← RequestMappingHandlerMapping; how routes are indexed
│   │   ├── 03-view-resolver.md            ← ThymeleafViewResolver; template resolution
│   │   ├── 04-model-attributes.md         ← Model/ModelMap/@ModelAttribute; data to view
│   │   └── DispatcherServletFlowDemo.java ← ASCII embedded; show all interceptors firing
│   └── exercises/
│
├── 02-thymeleaf/
│   ├── README.md                          ← Jinja2 vs Thymeleaf — direct syntax comparison
│   ├── explanation/
│   │   ├── 01-thymeleaf-basics.md         ← th:text/th:href/th:src/th:if/th:unless/th:each
│   │   ├── 02-form-binding.md             ← th:object/th:field bidirectional model binding
│   │   ├── 03-layout-fragments.md         ← th:fragment/th:replace — Jinja2 block/extends compare
│   │   ├── 04-thymeleaf-security.md       ← sec:authorize; display based on role; Python Jinja2 compare
│   │   └── templates/
│   │       ├── layouts/base.html
│   │       ├── products/list.html
│   │       └── products/form.html
│   └── exercises/
│
└── 03-form-validation-mvc/
    ├── README.md
    ├── explanation/
    │   ├── 01-mvc-validation.md           ← @Valid on @ModelAttribute; BindingResult
    │   ├── 02-custom-messages.md          ← messages.properties; MessageSource
    │   ├── FormController.java
    │   └── templates/
    │       └── registration-form.html
    └── exercises/
```

---

## 10 — Spring Security

```
10-spring-security/
├── README.md                              ← OWASP Top 10; Security threat model; WHY Spring Security [mindmap inside]
├── MINDMAP.md                             ← Spring Security mindmap: filter chain/authentication/authorization/UserDetails/BCrypt/CSRF/CORS
│
├── 01-security-architecture/
│   ├── README.md                          ← The most important thing: understand the filter chain first
│   ├── explanation/
│   │   ├── 01-security-filter-chain.md    ← ASCII: Request → 15 default filters → SecurityContextHolder → Controller [flowchart all 15 filters + sequenceDiagram each filter decision] + interview Q&A
│   │   ├── 02-authentication-flow.md      ← ASCII: Request → UsernamePasswordFilter → AuthManager → Provider → UserDetailsService [sequenceDiagram full auth chain] + interview Q&A
│   │   ├── 03-authorization-flow.md       ← AuthorizationFilter; AccessDecisionManager; voters
│   │   ├── 04-security-context.md         ← SecurityContextHolder; ThreadLocal; async propagation
│   │   ├── 05-principal-authentication.md ← Authentication object; principal; granted authorities
│   │   ├── SecurityFilterChainDemo.java   ← Print all 15 default filters in correct order
│   │   └── SecurityArchitectureDiagram.md ← Deep ASCII diagram of full auth flow
│   └── exercises/
│
├── 02-form-based-auth/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-default-security.md         ← What Spring Security does with zero config
│   │   ├── 02-security-config.md          ← SecurityFilterChain bean; HttpSecurity API
│   │   ├── 03-custom-login.md             ← loginPage/loginProcessingUrl/defaultSuccessUrl
│   │   ├── 04-logout.md                   ← logoutUrl; invalidate session; clear cookies
│   │   ├── 05-remember-me.md              ← rememberMe(); persistent token; secure cookie
│   │   ├── SecurityConfigDemo.java        ← Annotated SecurityFilterChain with every option
│   │   └── templates/
│   │       ├── login.html
│   │       └── dashboard.html
│   └── exercises/
│
├── 03-user-management/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-user-details-service.md     ← UserDetailsService contract; loadUserByUsername
│   │   ├── 02-user-details.md             ← UserDetails interface; authorities list; account flags
│   │   ├── 03-jdbc-authentication.md      ← Default schema; custom schema; JdbcUserDetailsManager
│   │   ├── 04-password-encoding.md        ← BCryptPasswordEncoder; why 10+ rounds; PasswordEncoder interface [flowchart register→encode/login→matches + xychart-beta BCrypt rounds vs time] + interview Q&A
│   │   ├── 05-user-registration.java      ← Encode on save; never store plaintext
│   │   ├── UserEntity.java                ← JPA entity implementing UserDetails
│   │   ├── UserRepository.java
│   │   ├── CustomUserDetailsService.java  ← Loads UserEntity from DB; maps to Spring UserDetails
│   │   └── RegistrationController.java
│   └── exercises/
│       ├── Ex01_UserRegistrationFlow.md
│       └── solutions/
│
├── 04-method-security/
│   ├── README.md                          ← WHY: URL security is coarse; method security is fine-grained
│   ├── explanation/
│   │   ├── 01-enable-method-security.md   ← @EnableMethodSecurity (Spring 6); replaces @EnableGlobalMethodSecurity
│   │   ├── 02-pre-post-authorize.md       ← @PreAuthorize/@PostAuthorize with SpEL expressions
│   │   ├── 03-secured-roles-allowed.md    ← @Secured/@RolesAllowed — simpler but less powerful
│   │   ├── 04-pre-post-filter.md          ← @PreFilter/@PostFilter on collections
│   │   ├── MethodSecurityDemo.java        ← All four annotations on same service
│   │   └── SecuredApiController.java      ← Role + ownership checks: hasRole('ADMIN') or #id == auth.id
│   └── exercises/
│
└── 05-csrf-cors/
    ├── README.md
    ├── explanation/
    │   ├── 01-csrf-attack.md              ← What CSRF is; why cookies are the vector; ASCII attack diagram [sequenceDiagram CSRF attack + journey user→attacker link→bank action] + interview Q&A
    │   ├── 02-csrf-token.md               ← Double-submit cookie; synchronizer token; when to disable
    │   ├── 03-cors.md                     ← Same-origin policy; preflight; @CrossOrigin; global CORS config [sequenceDiagram preflight OPTIONS→allow→actual request] + interview Q&A
    │   ├── CSRFDemo.java                  ← CSRF attack simulation + token-based fix
    │   └── CORSConfig.java                ← CorsConfigurationSource bean; allowed origins/methods/headers
    └── exercises/

── MINI-PROJECT after 10-spring-security ──────────────────────────────────────────
mini-project-04-secured-employee-api/
├── README.md                              ← Add full security to the Employee API from Phase 4
├── src/main/java/com/learning/employee/
│   ├── (all files from 07-full-crud-project, extended with:)
│   ├── security/
│   │   ├── SecurityConfig.java            ← SecurityFilterChain; role-based URL access
│   │   ├── UserEntity.java
│   │   ├── UserRepository.java
│   │   ├── CustomUserDetailsService.java
│   │   └── RegistrationController.java
│   └── controller/
│       └── EmployeeController.java        ← GET=public; POST/PUT=MANAGER; DELETE=ADMIN
└── src/test/
    └── SecurityIntegrationTest.java       ← @SpringBootTest security tests with @WithMockUser
```

---

## 11 — JWT & OAuth2

```
11-jwt-oauth2/
├── README.md                              ← Stateless auth; JWT vs session cookies; OAuth2 social login [mindmap inside]
├── MINDMAP.md                             ← JWT & OAuth2 mindmap: JWT structure/flow/refresh/security + OAuth2 grant types/OIDC
│
├── 01-jwt-deep-dive/
│   ├── README.md                          ← WHY JWT: stateless auth for microservices; vs session cookies
│   ├── explanation/
│   │   ├── 01-cryptography-basics.md      ← Symmetric (HS256) vs Asymmetric (RS256); signing vs encryption
│   │   ├── 02-jwt-structure.md            ← Header.Payload.Signature; base64url; standard claims (iss/sub/exp/iat) [flowchart base64url(header).base64url(payload).HMAC] + interview Q&A
│   │   ├── 03-jwt-flow.md                 ← ASCII: Login → Token → Request → Validate → Response [sequenceDiagram full login→token→use cycle] + interview Q&A
│   │   ├── 04-jjwt-library.md             ← io.jsonwebtoken:jjwt 0.12.x API; Jwts.builder(); parser()
│   │   ├── 05-jwt-filter.md               ← OncePerRequestFilter; extract→validate→setAuthentication
│   │   ├── 06-refresh-tokens.md           ← Access (15min) + Refresh (7days); rotation strategy [sequenceDiagram access expired→refresh→rotate] + interview Q&A
│   │   ├── 07-jwt-security-issues.md      ← alg:none attack; token storage (HttpOnly cookie vs localStorage)
│   │   ├── 08-stateless-security-config.md ← SessionCreationPolicy.STATELESS; disable CSRF for APIs
│   │   ├── JWTUtil.java                   ← generateToken/validateToken/extractClaims; annotated
│   │   ├── JWTAuthFilter.java             ← Full OncePerRequestFilter with ASCII flow in header
│   │   ├── AuthController.java            ← /api/auth/register + /api/auth/login → returns JWT
│   │   ├── SecurityConfig.java            ← Stateless; JWT filter in chain; permit /auth/**
│   │   └── RefreshTokenService.java       ← Store refresh token in DB; rotation on use
│   └── exercises/
│       ├── Ex01_JWTAuthSystem.md          ← Build complete JWT auth: register, login, protected endpoints
│       └── solutions/
│           └── jwt-auth-project/          ← Full working project
│
└── 02-oauth2/
    ├── README.md                          ← WHY OAuth2: delegate auth to trusted provider; Python authlib compare
    ├── explanation/
    │   ├── 01-oauth2-concepts.md          ← Roles: Resource Owner/Client/Auth Server/Resource Server [sequenceDiagram Auth Code flow + C4Context 4 OAuth2 roles] + interview Q&A
    │   ├── 02-oauth2-grant-types.md       ← Authorization Code (web) / Client Credentials (M2M) / Device
    │   ├── 03-oauth2-flow.md              ← ASCII: User → Client → Auth Server → Token → Resource Server
    │   ├── 04-oidc.md                     ← OpenID Connect = OAuth2 + identity; id_token; userinfo endpoint
    │   ├── 05-social-login.md             ← spring-security-oauth2-client; Google/GitHub registration
    │   ├── 06-resource-server.md          ← Protecting APIs with JWT Bearer; spring-security-oauth2-resource-server
    │   ├── 07-spring-authorization-server.md ← Your own OAuth2 server; client credentials flow
    │   ├── OAuth2LoginConfig.java         ← Google + GitHub OAuth2 client config
    │   ├── ResourceServerConfig.java      ← jwt() decoder; role extraction from claims
    │   └── application-oauth.yml          ← client-id/secret placeholders; issuer-uri
    └── exercises/
        ├── Ex01_GoogleLogin.md
        └── solutions/

── MINI-PROJECT after 11-jwt-oauth2 ───────────────────────────────────────────────
mini-project-05-jwt-secured-api/
├── README.md                              ← Full JWT auth + CRUD API; test with Postman collection
├── src/main/java/com/learning/jwtapi/
│   ├── auth/                              ← register/login/refresh
│   ├── product/                           ← CRUD protected by JWT roles
│   └── config/                            ← SecurityConfig + JWT beans
├── src/test/
│   └── AuthFlowIntegrationTest.java       ← Full flow: register→login→use token→refresh→logout
└── postman/
    └── jwt-api-collection.json
```

---

## 12 — Spring AOP

```
12-spring-aop/
├── README.md                              ← WHY AOP: cross-cutting concerns; Python decorator comparison [mindmap inside]
├── MINDMAP.md                             ← Spring AOP mindmap: Aspect/Advice/Pointcut/JoinPoint/proxy mechanics/real-world aspects
│
├── 01-aop-concepts/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-aop-terminology.md          ← Aspect/Advice/Pointcut/JoinPoint/Weaving; Python @decorator compare [mindmap AOP terms + flowchart call→proxy→advice→target] + interview Q&A
│   │   ├── 02-spring-aop-vs-aspectj.md    ← Proxy-based (Spring AOP) vs compile/load-time (AspectJ)
│   │   ├── 03-proxy-mechanics.md          ← JDK Proxy (interface) vs CGLIB (class); which Spring uses when [flowchart JDK vs CGLIB selection + sequenceDiagram self-invocation bypass] + interview Q&A
│   │   ├── 04-self-invocation-limitation.md ← Calling @Transactional/@Cacheable from same class bypasses AOP
│   │   ├── AOPConceptsDemo.java           ← ASCII: method call → proxy → advice → target
│   │   └── AOPArchitectureDiagram.md      ← Full deep-dive ASCII diagram
│   └── exercises/
│
├── 02-advice-types/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-before-advice.md            ← @Before; use cases; ProceedingJoinPoint not available
│   │   ├── 02-after-returning.md          ← @AfterReturning; returning= attribute; modify not possible
│   │   ├── 03-after-throwing.md           ← @AfterThrowing; throwing= attribute; notification not suppression
│   │   ├── 04-after-finally.md            ← @After; runs regardless; like finally block
│   │   ├── 05-around-advice.md            ← @Around; ProceedingJoinPoint.proceed(); most powerful [sequenceDiagram before proceed→call→after proceed] + interview Q&A
│   │   ├── BeforeAdviceDemo.java          ← Log method entry with class + method + args
│   │   ├── AfterReturningDemo.java        ← Audit return values; mask sensitive fields
│   │   ├── AfterThrowingDemo.java         ← Alert on specific exception types
│   │   └── AroundAdviceDemo.java          ← Performance timing; caching; circuit breaking
│   └── exercises/
│
├── 03-pointcut-expressions/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-execution-pointcut.md       ← execution(* com.app.service.*.*(..)) pattern breakdown
│   │   ├── 02-within-annotation.md        ← within(); @within(); @annotation() for custom triggers
│   │   ├── 03-args-this-target.md         ← args()/this()/target() for type/param matching
│   │   ├── 04-combining-pointcuts.md      ← @Pointcut declarations; && || ! composition
│   │   ├── PointcutDeclarationsDemo.java  ← Reusable @Pointcut methods; combined expressions
│   │   └── CustomAnnotationPointcut.java  ← @Auditable annotation + pointcut matching it
│   └── exercises/
│
└── 04-real-world-aop/
    ├── README.md
    ├── explanation/
    │   ├── 01-logging-aspect.md           ← Log all service method entry/exit/args/return/duration
    │   ├── 02-performance-aspect.md       ← @Around timing; threshold alert if slow
    │   ├── 03-audit-aspect.md             ← @Auditable; capture who did what when
    │   ├── 04-retry-aspect.md             ← @Retryable; exponential backoff; max attempts
    │   ├── 05-caching-with-aop.md         ← @Cacheable/@CacheEvict/@CachePut; Spring Cache abstraction
    │   ├── LoggingAspect.java             ← Production-grade; MDC correlation ID
    │   ├── PerformanceAspect.java         ← Micrometer timer + slow-method alert
    │   ├── AuditAspect.java               ← @Auditable + AuditLog entity save
    │   ├── RetryAspect.java               ← ProceedingJoinPoint; retry loop; backoff sleep
    │   └── CachingAspect.java             ← Custom caching with ConcurrentHashMap
    └── exercises/
        ├── Ex01_LoggingAspect.md
        ├── Ex02_RetryMechanism.md
        └── solutions/

── MINI-PROJECT after 12-spring-aop ───────────────────────────────────────────────
mini-project-06-audited-service/
├── README.md                              ← Add AOP to the Product Catalogue from Phase 5
├── src/main/java/com/learning/audited/
│   ├── aspect/
│   │   ├── LoggingAspect.java
│   │   ├── PerformanceAspect.java
│   │   └── AuditAspect.java
│   ├── annotation/
│   │   └── Auditable.java                 ← Custom @Auditable(action="CREATE_PRODUCT")
│   └── entity/
│       └── AuditLog.java                  ← Persisted audit trail
└── src/test/
    └── AuditAspectTest.java
```

---

## 13 — Drools Rules Engine

```
13-drools-rules-engine/
├── README.md                              ← WHY Drools: business rules outside code; compliance/pricing/eligibility [mindmap inside]
├── MINDMAP.md                             ← Drools mindmap: Rete/facts/DRL syntax/KieSession/stateless vs stateful/decision tables
│
├── 01-drools-concepts/
│   ├── README.md                          ← WHY CAME: Insurance/banking/telecom rule complexity; change without deploy
│   ├── explanation/
│   │   ├── 01-what-is-drools.md           ← Rules engine concept; Rete algorithm; facts/rules/agenda [flowchart Facts→Rete→Agenda→Execute + C4Context Spring/Drools/KieSession/rules + timeline Drools history] + interview Q&A
│   │   ├── 02-drools-vs-if-else.md        ← 500 if-else vs 500 rules; maintainability comparison
│   │   ├── 03-use-cases.md                ← Loan eligibility/discount engine/fraud detection/compliance
│   │   ├── 04-drools-architecture.md      ← ASCII: Facts→Working Memory→Rete Network→Agenda→Rule Execution
│   │   ├── 05-kie-api.md                  ← KieServices/KieContainer/KieSession — the API
│   │   └── DroolsConceptsDemo.java        ← Print Rete network structure; session lifecycle
│   └── exercises/
│
├── 02-drl-syntax/
│   ├── README.md                          ← DRL = Drools Rule Language; Java-like syntax
│   ├── explanation/
│   │   ├── 01-drl-basics.md               ← rule/when/then blocks; fact pattern matching [flowchart condition→action evaluation] + interview Q&A
│   │   ├── 02-conditions.md               ← Patterns; constraints; bindings ($var)
│   │   ├── 03-actions.md                  ← modify/insert/retract/update in then block
│   │   ├── 04-rule-attributes.md          ← salience (priority); no-loop; agenda-group; activation-group
│   │   ├── 05-accumulate.md               ← Aggregate functions in rules; sum/count/min/max
│   │   ├── BasicRules.drl                 ← Hello World in Drools; annotated DRL
│   │   ├── DiscountRules.drl              ← Tiered discount by order total
│   │   └── EligibilityRules.drl           ← Loan eligibility multi-condition rules
│   └── exercises/
│
├── 03-spring-boot-drools/
│   ├── README.md                          ← Integrate Drools into Spring Boot; inject KieSession
│   ├── explanation/
│   │   ├── 01-drools-spring-setup.md      ← build.gradle dependencies; KieContainer bean
│   │   ├── 02-stateless-vs-stateful.md    ← StatelessKieSession (one-shot) vs KieSession (persistent facts) [sequenceDiagram stateless: insert→fire→discard vs stateful: insert→fire→modify→fire] + interview Q&A
│   │   ├── 03-facts-as-pojos.md           ← Java POJOs as Drools facts; @PropertyReactive
│   │   ├── 04-rule-execution-service.md   ← Service wrapper around KieSession
│   │   ├── 05-testing-rules.md            ← Unit test rules in isolation; assert outcomes
│   │   ├── DroolsConfig.java              ← @Configuration; KieContainer bean from classpath
│   │   ├── RuleEngineService.java         ← Stateless session; insert facts; fireAllRules; return
│   │   ├── DiscountFact.java              ← POJO fact: customer tier, order total, computed discount
│   │   └── LoanEligibilityFact.java       ← POJO fact: income/credit/employment → approved/rejected
│   └── exercises/
│       ├── Ex01_DiscountEngine.java       ← Customer tier + order amount → discount percentage
│       ├── Ex02_FraudDetection.java       ← Transaction patterns → fraud score
│       └── solutions/
│
└── 04-advanced-drools/
    ├── README.md
    ├── explanation/
    │   ├── 01-decision-tables.md          ← Excel spreadsheet as rules; business user friendly
    │   ├── 02-rule-flow.md                ← Conditional rule execution; agenda groups; ruleflow-group
    │   ├── 03-complex-event-processing.md ← CEP; temporal reasoning; event streams
    │   ├── DiscountDecisionTable.xlsx      ← Excel decision table for discount rules
    │   └── ComplexEventDemo.java
    └── exercises/

── MINI-PROJECT after 13-drools ───────────────────────────────────────────────────
mini-project-07-loan-approval-engine/
├── README.md                              ← Loan eligibility REST API powered by Drools rules
├── src/main/java/com/learning/loan/
│   ├── controller/
│   │   └── LoanController.java            ← POST /loan/apply → eligibility result
│   ├── service/
│   │   └── LoanEligibilityService.java    ← Calls RuleEngineService with LoanApplication facts
│   ├── rules/
│   │   └── LoanEligibilityRules.drl       ← All rules; change rules without changing Java
│   ├── model/
│   │   ├── LoanApplication.java           ← Fact POJO
│   │   └── EligibilityResult.java
│   └── config/
│       └── DroolsConfig.java
└── src/test/
    └── LoanRulesTest.java                 ← Test each rule in isolation
```

---

## 14 — Testing

```
14-testing/
├── README.md                              ← Testing pyramid; pytest vs JUnit5 direct comparison [mindmap inside]
├── MINDMAP.md                             ← Testing mindmap: JUnit5/Mockito/@WebMvcTest/@DataJpaTest/@SpringBootTest/Testcontainers
│
├── 01-junit5/
│   ├── README.md                          ← WHY: Python pytest ≈ JUnit5; same concepts different syntax
│   ├── explanation/
│   │   ├── 01-junit5-basics.md            ← @Test @BeforeEach @AfterEach @BeforeAll @AfterAll [flowchart test lifecycle @BeforeAll→@BeforeEach→@Test→@AfterEach→@AfterAll] + interview Q&A
│   │   ├── 02-assertions.md               ← assertThat/assertEquals/assertThrows/assertAll; AssertJ
│   │   ├── 03-parameterized-tests.md      ← @ParameterizedTest @CsvSource @MethodSource = pytest.mark.parametrize
│   │   ├── 04-test-lifecycle.md           ← @TestInstance(PER_CLASS); test ordering
│   │   ├── 05-nested-tests.md             ← @Nested = pytest class grouping
│   │   ├── 06-test-exceptions.md          ← assertThrows; expected message check
│   │   ├── JUnit5BasicsDemo.java          ← Side-by-side Python pytest in comments
│   │   ├── AssertionsDemo.java            ← AssertJ fluent assertions vs JUnit plain
│   │   └── ParameterizedDemo.java         ← CSV source + method source examples
│   └── exercises/
│       ├── Ex01_CalculatorTest.java
│       └── solutions/
│
├── 02-mockito/
│   ├── README.md                          ← Python unittest.mock vs Mockito — side-by-side
│   ├── explanation/
│   │   ├── 01-mocking-concept.md          ← What to mock; mock vs spy; when NOT to mock [pie test composition: SUT/mocks/assertions] + interview Q&A
│   │   ├── 02-mock-spy-captor.md          ← @Mock @Spy @Captor @InjectMocks annotations
│   │   ├── 03-stubbing.md                 ← when/thenReturn/thenThrow/thenAnswer; Python MagicMock
│   │   ├── 04-verification.md             ← verify(); times(); never(); inOrder()
│   │   ├── 05-argument-matchers.md        ← any()/eq()/argThat(); ArgumentCaptor
│   │   ├── 06-mockito-strict.md           ← Unnecessary stubbing detection; strict mocks
│   │   ├── MockitoBasicsDemo.java         ← Python mock comparison in every comment
│   │   ├── StubbingDemo.java              ← thenReturn/thenThrow/thenAnswer
│   │   └── ArgumentCaptorDemo.java        ← Capture and assert exact args passed to mock
│   └── exercises/
│       ├── Ex01_ServiceLayerTest.java     ← Test ProductService; mock ProductRepository
│       └── solutions/
│
├── 03-spring-boot-testing/
│   ├── README.md                          ← Test slices vs full context; speed tradeoffs
│   ├── explanation/
│   │   ├── 01-springboottest.md           ← Full context; actual server or MockMvc; slow [pie test slice coverage: unit/@WebMvcTest/@DataJpaTest/@SpringBootTest] + interview Q&A
│   │   ├── 02-webmvctest.md               ← Controller slice only; MockMvc; fast; no DB
│   │   ├── 03-datajpatest.md              ← JPA slice; H2 in-memory; no web layer
│   │   ├── 04-mockmvc-patterns.md         ← GET/POST builders; jsonPath matchers; status checks [sequenceDiagram MockMvc→DispatcherServlet→Controller→mocked Service→assert] + interview Q&A
│   │   ├── 05-test-slices.md              ← @JsonTest @RestClientTest other slices
│   │   ├── 06-test-configuration.md       ← @TestConfiguration; override beans in tests
│   │   ├── ControllerSliceTest.java       ← @WebMvcTest ProductController; mock service
│   │   ├── ServiceUnitTest.java           ← Pure Mockito; no Spring; fast
│   │   ├── RepositorySliceTest.java       ← @DataJpaTest; H2; test query methods
│   │   └── FullIntegrationTest.java       ← @SpringBootTest; real HTTP; real DB
│   └── exercises/
│       ├── Ex01_ControllerTest.java       ← @WebMvcTest for ProductController; all endpoints
│       ├── Ex02_RepositoryTest.java       ← @DataJpaTest; findByCategory; pagination
│       └── solutions/
│
└── 04-testcontainers/
    ├── README.md                          ← WHY: H2 ≠ PostgreSQL; test against real DB
    ├── explanation/
    │   ├── 01-testcontainers-intro.md     ← Docker containers in tests; @Testcontainers @Container [sequenceDiagram @Testcontainers→Docker pull→start→DynamicPropertySource→test→stop] + interview Q&A
    │   ├── 02-postgres-container.md       ← PostgreSQLContainer; DynamicPropertySource
    │   ├── 03-shared-containers.md        ← Singleton container for test suite; startup time
    │   ├── 04-redis-kafka-containers.md   ← Redis/Kafka containers for integration tests
    │   ├── PostgresIntegrationTest.java   ← Real PostgreSQL; DynamicPropertySource
    │   └── SharedContainerBase.java       ← Abstract base class; container started once
    └── exercises/
        ├── Ex01_RealDBIntegration.md
        └── solutions/
```

---

## 15 — Microservices

```
15-microservices/
├── README.md                              ← Monolith→SOA→Microservices evolution; tradeoffs [mindmap inside]
├── MINDMAP.md                             ← Microservices mindmap: principles/Eureka/Feign/Gateway/resilience/tracing/distributed patterns
│
├── 01-microservices-concepts/
│   ├── README.md                          ← WHY: Independent deploy; team autonomy; fault isolation
│   ├── explanation/
│   │   ├── 01-principles.md               ← 12-factor app; single responsibility; bounded context; DDD [C4Context full system view + mindmap microservices principles] + interview Q&A
│   │   ├── 02-communication-patterns.md   ← Sync REST/gRPC vs Async messaging; ASCII comparison [sequenceDiagram sync call + sequenceDiagram async queue + quadrantChart sync vs async tradeoffs] + interview Q&A
│   │   ├── 03-data-per-service.md         ← Database-per-service; shared DB antipattern
│   │   ├── 04-distributed-tracing.md      ← TraceId/SpanId; correlation; Zipkin
│   │   ├── 05-service-mesh-intro.md       ← Istio/Linkerd concept; beyond this course
│   │   └── MicroservicesArchitecture.md   ← Full ASCII architecture of our learning project
│   └── exercises/
│
├── 02-service-discovery/
│   ├── README.md                          ← WHY: Services don't have fixed IPs; dynamic discovery needed
│   ├── explanation/
│   │   ├── 01-discovery-concept.md        ← Client-side vs server-side; DNS vs registry [sequenceDiagram service register→query Eureka→call + C4Container Eureka/ServiceA/ServiceB] + interview Q&A
│   │   ├── 02-eureka-server.md            ← @EnableEurekaServer; dashboard at 8761
│   │   ├── 03-eureka-client.md            ← @EnableDiscoveryClient; heartbeat; eviction
│   │   ├── 04-health-integration.md       ← Actuator /health feeds Eureka status
│   │   ├── EurekaServerApp.java           ← Minimal server; annotated
│   │   └── eureka-server/build.gradle     ← Dependency: spring-cloud-starter-netflix-eureka-server
│   └── exercises/
│
├── 03-inter-service-communication/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-webflux-webclient.md        ← Reactive WebClient; non-blocking HTTP; Python httpx async compare
│   │   ├── 02-open-feign.md               ← Declarative HTTP client; @FeignClient("service-name") [sequenceDiagram Feign→Eureka lookup→HTTP→decode→return] + interview Q&A
│   │   ├── 03-feign-config.md             ← FeignClient config: timeout/logger/interceptors
│   │   ├── 04-feign-error-handling.md     ← FallbackFactory; ErrorDecoder; retry
│   │   ├── 05-feign-with-eureka.md        ← Name resolves via Eureka; load balanced
│   │   ├── WebClientDemo.java             ← Reactive non-blocking HTTP call
│   │   ├── ProductFeignClient.java        ← @FeignClient("product-service") annotated interface
│   │   ├── FeignClientConfig.java         ← Timeout + retry + logging config
│   │   └── FeignFallbackFactory.java      ← Return defaults when service is down
│   └── exercises/
│
├── 04-api-gateway/
│   ├── README.md                          ← WHY: Single entry point; cross-cutting concerns at edge
│   ├── explanation/
│   │   ├── 01-gateway-concept.md          ← ASCII: Client → Gateway → [Service A / Service B / Service C] [C4Context client→gateway→services + flowchart request processing in gateway] + interview Q&A
│   │   ├── 02-spring-cloud-gateway.md     ← Route predicates; filters; GatewayFilterFactory
│   │   ├── 03-route-config.md             ← YAML routes: Path(/api/products/**) → product-service
│   │   ├── 04-gateway-filters.md          ← AddRequestHeader/RewritePath/CircuitBreaker filters
│   │   ├── 05-gateway-jwt.md              ← JWT validation filter at gateway; downstream gets user info
│   │   ├── GatewayApp.java
│   │   ├── GatewayConfig.java             ← Programmatic route config
│   │   ├── JWTGatewayFilter.java          ← Validate JWT; forward X-User-Id header
│   │   └── application-gateway.yml        ← Route definitions
│   └── exercises/
│
├── 05-resilience/
│   ├── README.md                          ← WHY: Cascading failures kill microservices; resilience is mandatory
│   ├── explanation/
│   │   ├── 01-circuit-breaker.md          ← Resilience4j; CLOSED→OPEN→HALF_OPEN ASCII state machine [stateDiagram-v2 CLOSED→OPEN→HALF_OPEN with transition conditions + sequenceDiagram failure threshold exceeded] + interview Q&A
│   │   ├── 02-retry.md                    ← @Retry; exponential backoff; maxAttempts [sequenceDiagram fail→wait→retry1→fail→wait longer→retry2→success] + interview Q&A
│   │   ├── 03-rate-limiter.md             ← @RateLimiter; token bucket; SemaphoreBasedRL
│   │   ├── 04-bulkhead.md                 ← ThreadPoolBulkhead; isolate thread pools per service
│   │   ├── 05-timelimiter.md              ← @TimeLimiter; cancel if takes > N seconds
│   │   ├── 06-fallback-patterns.md        ← Static fallback / cache fallback / degraded response
│   │   ├── CircuitBreakerDemo.java        ← ASCII state machine in comments; slow calls trigger open
│   │   ├── RetryDemo.java                 ← Retry with RandomizedWaitDuration backoff
│   │   └── application-resilience.yml     ← Full resilience4j config for all services
│   └── exercises/
│
├── 06-distributed-tracing/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-tracing-concepts.md         ← TraceId (request) + SpanId (service call); B3 propagation
│   │   ├── 02-micrometer-tracing.md       ← Spring Boot 3; Micrometer Tracing + Brave/OTel bridge
│   │   ├── 03-zipkin.md                   ← Zipkin server via Docker; export to Zipkin
│   │   └── TracingDemo.java               ← TraceId in logs; propagate via Feign headers
│   └── exercises/
│
└── 07-microservices-project/
    ├── README.md                          ← E-Commerce Microservices: full architecture
    ├── architecture.md                    ← ASCII: all 5 services + gateway + eureka
    ├── docker-compose.yml                 ← Spin up entire system
    ├── eureka-server/
    ├── api-gateway/
    ├── user-service/                      ← Registration/login/JWT
    ├── product-service/                   ← Product CRUD + Drools pricing
    └── order-service/                     ← Place order; calls product-service via Feign
```

---

## 16 — Docker

```
16-docker/
├── README.md                              ← Containers vs VMs; Docker for Spring Boot; Docker Compose [mindmap inside]
├── MINDMAP.md                             ← Docker mindmap: images/containers/Dockerfile/multi-stage/Compose/volumes/health checks
│
├── 01-docker-basics/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-containers-vs-vms.md        ← Shared kernel; layers; Python virtualenv analogy [block-beta VM stack vs Container stack + timeline physical→VM→containers→K8s] + interview Q&A
│   │   ├── 02-dockerfile-spring-boot.md   ← Multi-stage build; eclipse-temurin base; layer order [flowchart build stage→runtime stage→minimal image] + interview Q&A
│   │   ├── 03-docker-commands.md          ← run/build/push/exec/logs/ps/stop/rm cheat sheet
│   │   ├── 04-layer-caching.md            ← Deps layer before code layer; cache-friendly ordering
│   │   ├── Dockerfile                     ← Multi-stage production Dockerfile; annotated
│   │   └── .dockerignore
│   └── exercises/
│
├── 02-docker-compose/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-compose-basics.md           ← services/networks/volumes; Python docker-compose compare [C4Container Spring/PostgreSQL/Redis + sequenceDiagram docker-compose up sequence] + interview Q&A
│   │   ├── 02-spring-postgres-redis.md    ← Three-service stack; healthcheck; depends_on
│   │   ├── 03-compose-profiles.md         ← profiles: [dev] vs profiles: [prod]
│   │   ├── docker-compose.yml             ← Spring Boot + PostgreSQL + Redis
│   │   └── docker-compose-dev.yml
│   └── exercises/
│       ├── Ex01_SpringWithDB.md
│       └── solutions/
│
└── 03-spring-docker-integration/
    ├── README.md
    ├── explanation/
    │   ├── 01-spring-docker-compose.md    ← Spring Boot 3.1+ auto Docker Compose support
    │   ├── 02-health-checks.md            ← HEALTHCHECK in Dockerfile; K8s readiness/liveness
    │   ├── 03-environment-config.md       ← Env vars override application.yml; 12-factor
    │   └── application-docker.yml
    └── exercises/
```

---

## 17 — Real World Projects

```
17-real-world-projects/
├── README.md                              ← Real-world project index; what each project covers
│
├── project-01-employee-management/
│   ├── README.md                          ← Spec: Employee CRUD + dept + search + pagination
│   ├── src/                               ← Spring Boot + JPA + Validation + Exception handling
│   ├── testing/
│   │   ├── postman-collection.json
│   │   └── curl-examples.sh
│   └── architecture.md                    ← ASCII: Controller→Service→Repository→DB
│
├── project-02-ecommerce-platform/
│   ├── README.md                          ← Product/Cart/Order/User + JWT + AOP logging + Drools pricing
│   ├── src/
│   │   ├── main/java/com/learning/ecom/
│   │   │   ├── product/                   ← Product CRUD + search
│   │   │   ├── cart/                      ← Cart management (request-scoped service)
│   │   │   ├── order/                     ← Order placement + @Transactional
│   │   │   ├── user/                      ← JWT auth + registration
│   │   │   ├── pricing/                   ← Drools discount engine
│   │   │   └── aspect/                    ← Logging + audit AOP
│   │   └── resources/rules/
│   │       └── discount-rules.drl
│   └── testing/
│
├── project-03-microservices-ecommerce/
│   ├── README.md                          ← Same domain, decomposed into microservices
│   ├── architecture.md                    ← Full ASCII architecture diagram
│   ├── docker-compose.yml                 ← Start all services
│   ├── eureka-server/
│   ├── api-gateway/                       ← JWT validation + routing
│   ├── user-service/                      ← Auth + user management
│   ├── product-service/                   ← Products + Drools pricing
│   └── order-service/                     ← Orders + Feign calls to product-service
│
└── project-04-job-portal/
    ├── README.md                          ← Job Portal: MVC + Thymeleaf + Security + JPA
    └── src/
```

---

## Resources

```
resources/
├── cheatsheets/
│   ├── java-vs-python.md                  ← Full syntax side-by-side comparison
│   ├── gradle-commands.md                 ← ./gradlew tasks; common command reference
│   ├── spring-annotations.md              ← Every annotation with one-line description + package
│   ├── jpa-annotations.md                 ← Entity mapping quick reference
│   ├── spring-security-flow.md            ← Auth flow quick reference
│   ├── jwt-claims-reference.md            ← Standard claims + custom claims guide
│   ├── drools-drl-syntax.md               ← DRL quick reference
│   ├── mermaid-diagram-types.md           ← All 16 Mermaid types with syntax + when to use
│   └── http-status-codes.md               ← Status codes with when to use each
│
├── interview-prep/
│   │
│   │   NOTE: Every file here is AGGREGATED from the ## Interview Questions
│   │   sections at the end of each explanation/*.md file. Never write
│   │   questions directly here — write them in sub-topic files first.
│   │
│   ├── 01-core-java-questions.md          ← ~80 Q&A from 00-java-foundation + 01-advanced-java
│   │   Topics: JVM/types/OOP/generics/collections/streams/threads/design patterns
│   │
│   ├── 02-gradle-and-build-questions.md   ← ~15 Q&A from 02-gradle-build-tool
│   │   Topics: Gradle lifecycle/scopes/tasks/multi-module/BOM
│   │
│   ├── 03-jdbc-questions.md               ← ~20 Q&A from 03-jdbc
│   │   Topics: DriverManager/PreparedStatement/connection pool/HikariCP/transactions
│   │
│   ├── 04-hibernate-jpa-questions.md      ← ~45 Q&A from 04-hibernate-jpa
│   │   Topics: ORM/Session/entity annotations/relationships/N+1/cache/locking/JPQL
│   │
│   ├── 05-spring-core-questions.md        ← ~35 Q&A from 05-spring-core
│   │   Topics: IoC/DI/BeanFactory/bean lifecycle/scopes/events/SpEL
│   │
│   ├── 06-spring-boot-questions.md        ← ~30 Q&A from 06-spring-boot-fundamentals
│   │   Topics: auto-config/starters/embedded server/config management/actuator/beans deep dive
│   │
│   ├── 07-spring-rest-questions.md        ← ~35 Q&A from 07-spring-rest-api
│   │   Topics: REST principles/controllers/DTOs/validation/exception handling/swagger/versioning
│   │
│   ├── 08-spring-data-jpa-questions.md    ← ~30 Q&A from 08-spring-data-jpa
│   │   Topics: JpaRepository/derived queries/pagination/specifications/@Transactional/propagation
│   │
│   ├── 09-spring-security-questions.md    ← ~40 Q&A from 10-spring-security + 11-jwt-oauth2
│   │   Topics: filter chain/authentication/authorization/UserDetailsService/BCrypt/JWT/OAuth2
│   │
│   ├── 10-spring-aop-questions.md         ← ~20 Q&A from 12-spring-aop
│   │   Topics: AOP terms/advice types/pointcut expressions/proxy mechanics/self-invocation
│   │
│   ├── 11-drools-questions.md             ← ~15 Q&A from 13-drools-rules-engine
│   │   Topics: Rete algorithm/facts/DRL syntax/KieSession/stateless vs stateful
│   │
│   ├── 12-testing-questions.md            ← ~25 Q&A from 14-testing
│   │   Topics: JUnit5/Mockito/@WebMvcTest/@DataJpaTest/Testcontainers/test slices
│   │
│   ├── 13-microservices-questions.md      ← ~40 Q&A from 15-microservices
│   │   Topics: principles/Eureka/Feign/API Gateway/circuit breaker/resilience/tracing
│   │
│   ├── 14-docker-questions.md             ← ~15 Q&A from 16-docker
│   │   Topics: container vs VM/Dockerfile/multi-stage/Docker Compose/health checks
│   │
│   └── 15-system-design-questions.md      ← Architecture + system design scenarios
│       Topics: layered arch/hexagonal/CQRS/Saga/event-driven/microservices design
│
└── architecture-patterns/
    ├── layered-architecture.md            ← Controller→Service→Repository [flowchart + C4Component] with interview Q&A
    ├── hexagonal-architecture.md          ← Ports and adapters with Spring [C4Component] with interview Q&A
    ├── cqrs-with-spring.md               ← Command/Query segregation [flowchart] with interview Q&A
    ├── saga-pattern.md                    ← Distributed transaction management [sequenceDiagram choreography vs orchestration] with interview Q&A
    └── event-driven-spring.md             ← Spring Events + messaging patterns [sequenceDiagram] with interview Q&A
```

---

## Java File Convention (Gradle + Full Comments)

Every `.java` file in `explanation/` uses this exact structure:

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : BeanScopeDemo.java                                     ║
 * ║  MODULE : 06-spring-boot-fundamentals / 05-spring-boot-beans     ║
 * ║  GRADLE : ./gradlew :06-spring-boot-fundamentals:run             ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE                                                          ║
 * ║    Demonstrate Singleton vs Prototype scope. Understanding this   ║
 * ║    prevents shared-state bugs in concurrent request handling.     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  WHY THIS FEATURE EXISTS                                          ║
 * ║    Problem: Every object created per-request wastes memory.       ║
 * ║    Problem: Shared objects cause concurrency bugs if stateful.    ║
 * ║    Solution: Configurable scope — singleton for stateless         ║
 * ║              services; prototype for stateful objects.            ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PYTHON / FASTAPI EQUIVALENT                                      ║
 * ║    Python: No equivalent — all objects are mutable references.   ║
 * ║    FastAPI: Depends(use_cache=True) ≈ singleton per app.         ║
 * ║    The key difference: Spring enforces scope at container level.  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  USE CASES                                                        ║
 * ║    Singleton  : Services, Repositories, Controllers (stateless)  ║
 * ║    Prototype  : Beans that hold per-operation state               ║
 * ║    Request    : Web layer beans scoped to HTTP request            ║
 * ║    Session    : Shopping cart scoped to user session              ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                    ║
 * ║                                                                   ║
 * ║  SINGLETON SCOPE                    PROTOTYPE SCOPE              ║
 * ║  ┌─────────────────────┐            ┌────────────────────────┐  ║
 * ║  │  Spring Container   │            │  Spring Container       │  ║
 * ║  │  ┌───────────────┐  │            │  ┌────────┐ ┌────────┐ │  ║
 * ║  │  │  OneInstance  │◄─┼── ref1    │  │ inst-1 │ │ inst-2 │ │  ║
 * ║  │  │  (shared)     │◄─┼── ref2    │  └────────┘ └────────┘ │  ║
 * ║  │  └───────────────┘◄─┼── ref3    │      ▲           ▲     │  ║
 * ║  └─────────────────────┘            │     ref1        ref2   │  ║
 * ║                                     └────────────────────────┘  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN                                                       ║
 * ║    ./gradlew :06-spring-boot-fundamentals:bootRun                ║
 * ║    OR: IntelliJ → right-click → Run 'main()'                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXPECTED OUTPUT                                                  ║
 * ║    [SINGLETON] bean1 == bean2: true   (same instance)            ║
 * ║    [PROTOTYPE] bean1 == bean2: false  (different instances)      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  RELATED FILES                                                    ║
 * ║    BeanLifecycleDemo.java   — full lifecycle hooks               ║
 * ║    ScopeProxyDemo.java      — prototype in singleton via proxy   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springboot.beans;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * SingletonService - stateless service bean.
 *
 * <p>Python equivalent: A module-level object. Once imported, it's the same
 * instance everywhere. Spring singleton works the same way.
 *
 * <p>Default scope: @Component without @Scope is always SINGLETON.
 * Spring creates ONE instance and returns that same instance to every
 * class that @Autowires it.
 *
 * ASCII:
 * <pre>
 *   ClassA @Autowired ──┐
 *   ClassB @Autowired ──┼──► [ singletonService instance ]
 *   ClassC @Autowired ──┘
 * </pre>
 */
@Component // No @Scope needed — Singleton is the default
public class SingletonService {

    /**
     * Counter field to prove singleton behaviour.
     *
     * <p>If this were a prototype, each caller would see count=0.
     * Since it's singleton, all callers share the same count.
     *
     * <p>WARNING: This is why you must NOT store request-specific state
     * in a singleton bean — it leaks between requests!
     */
    private int callCount = 0;

    /**
     * Increments and returns the shared call counter.
     *
     * <p>Python equivalent:
     * <pre>
     *   # Module level (effectively singleton)
     *   call_count = 0
     *   def get_count():
     *       global call_count
     *       call_count += 1
     *       return call_count
     * </pre>
     *
     * @return current call count across ALL callers
     */
    public int incrementAndGet() {
        return ++callCount; // Thread-unsafe intentionally — demo only
    }
}
```

---

## Exercise File Convention

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  EXERCISE : Ex01_BankTransfer.java                               ║
 * ║  MODULE   : 08-spring-data-jpa / 02-transactions                 ║
 * ║  DIFFICULTY: Medium                                               ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  SCENARIO                                                         ║
 * ║    Build a banking transfer service. Money must move from         ║
 * ║    account A to account B atomically. If credit fails after       ║
 * ║    debit, the whole operation must roll back.                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PYTHON COMPARISON                                                ║
 * ║    Python SQLAlchemy equivalent:                                  ║
 * ║      with session.begin():                                        ║
 * ║          from_acc.balance -= amount                               ║
 * ║          to_acc.balance += amount                                 ║
 * ║    Spring @Transactional does the same with a proxy wrapper.      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  REQUIREMENTS                                                     ║
 * ║    1. Create AccountEntity: id, ownerId, balance (BigDecimal)    ║
 * ║    2. Create AccountRepository extends JpaRepository             ║
 * ║    3. Implement transferFunds(fromId, toId, amount)              ║
 * ║    4. Annotate correctly with @Transactional                     ║
 * ║    5. Throw InsufficientFundsException if balance < amount       ║
 * ║    6. Write test verifying rollback on failure                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO TEST                                                      ║
 * ║    ./gradlew test --tests "*BankTransferTest"                    ║
 * ║    OR: Run Ex01_BankTransferTest.java in IntelliJ                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HINT                                                             ║
 * ║    @Transactional rolls back on RuntimeException by default.     ║
 * ║    Make InsufficientFundsException extend RuntimeException.      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

// YOUR CODE HERE
```

---

*Modules: 16 learning modules + 7 mini-projects + 3 real-world projects + resource library*
*Java files: ~320+ annotated files across all explanation + exercise folders*
*Build system: Gradle throughout — ./gradlew bootRun for every Spring module*
