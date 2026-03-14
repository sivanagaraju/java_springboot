# Spring Boot Learning Repository — Complete Folder Structure

> **Your Profile:** Python/FastAPI expert | Data Engineering background | 12 years industry | New Java/Spring closure
> **Goal:** Production REST APIs, Microservices, Enterprise Architecture
> **Style:** Learn by doing — every concept has explanation + working Java + ASCII diagrams + exercises

---

## Repository Root

```
spring-mastery/
├── README.md                          ← Master index: what this repo is, how to navigate
├── IMPLEMENTATION_PLAN.md             ← Linked implementation plan (see separate file)
├── PROGRESS_TRACKER.md                ← Checkboxes per topic — track what you've done
├── setup/
│   ├── README.md                      ← Environment setup guide
│   ├── install-java.md
│   ├── install-intellij.md
│   ├── install-maven.md
│   ├── install-docker.md
│   └── verify-setup.sh                ← Shell script to verify everything is installed
│
├── 00-java-foundation/
├── 01-advanced-java/
├── 02-maven-build-tool/
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
├── 13-testing/
├── 14-microservices/
├── 15-docker/
├── 16-dsa/
├── 17-real-world-projects/
└── resources/
    ├── cheatsheets/
    ├── interview-prep/
    └── architecture-patterns/
```

---

## 00 — Java Foundation

> Coming from Python: Java is strongly-typed, compiled, OOP-first. This module bridges that gap fast.

```
00-java-foundation/
├── README.md                          ← Module overview, Python→Java mental model map
│
├── 01-java-basics/
│   ├── README.md                      ← Topic overview
│   ├── explanation/
│   │   ├── 01-how-java-works.md       ← JDK/JRE/JVM explained with ASCII diagram
│   │   ├── 02-variables-datatypes.md  ← Primitives vs Objects, type system vs Python dynamic
│   │   ├── 03-operators.md            ← Arithmetic, Relational, Logical, Ternary
│   │   ├── 04-control-flow.md         ← if/else, switch, loops — Java vs Python syntax
│   │   ├── HowJavaWorks.java          ← Annotated Java file showing compilation flow
│   │   ├── VariablesDemo.java         ← All primitive types with inline ASCII type chart
│   │   ├── OperatorsDemo.java
│   │   └── ControlFlowDemo.java
│   └── exercises/
│       ├── README.md                  ← What to build, expected output
│       ├── Ex01_TypeConversion.java   ← Try widening/narrowing conversions
│       ├── Ex02_Calculator.java       ← Build a console calculator
│       └── solutions/
│           ├── Ex01_Solution.java
│           └── Ex02_Solution.java
│
├── 02-oop-fundamentals/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-class-and-object.md     ← Class blueprint vs instance; Python class comparison
│   │   ├── 02-constructors.md         ← Default, parameterized, this() chaining
│   │   ├── 03-encapsulation.md        ← Private fields, getters/setters, why it matters
│   │   ├── 04-inheritance.md          ← extends, super, single/multilevel, why no multiple
│   │   ├── 05-polymorphism.md         ← Overloading vs Overriding, dynamic dispatch
│   │   ├── 06-abstraction.md          ← Abstract class vs Interface — when to use which
│   │   ├── 07-access-modifiers.md     ← public/private/protected/default with ASCII grid
│   │   ├── 08-static-members.md       ← Static variables, methods, blocks — class-level state
│   │   ├── ClassAndObjectDemo.java
│   │   ├── InheritanceDemo.java       ← Multi-level chain with ASCII class diagram
│   │   ├── PolymorphismDemo.java      ← Runtime polymorphism with interface
│   │   ├── AbstractDemo.java
│   │   └── InterfaceDemo.java
│   └── exercises/
│       ├── README.md
│       ├── Ex01_BankAccount.java      ← Model a BankAccount with encapsulation
│       ├── Ex02_ShapeHierarchy.java   ← Abstract Shape → Circle, Rectangle, Triangle
│       ├── Ex03_PaymentInterface.java ← Interface for payment methods
│       └── solutions/
│
├── 03-advanced-oop/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-inner-classes.md        ← Static nested, inner, anonymous, local classes
│   │   ├── 02-enums.md                ← Enum as class, enum with methods, switch on enum
│   │   ├── 03-generics.md             ← Type parameters, bounded wildcards, generic methods
│   │   ├── 04-annotations.md          ← Built-in annotations, creating custom annotations
│   │   ├── 05-wrapper-classes.md      ← Autoboxing/unboxing, Integer vs int, null traps
│   │   ├── 06-object-class.md         ← equals(), hashCode(), toString() — why override all 3
│   │   ├── InnerClassDemo.java
│   │   ├── EnumDemo.java              ← Enum with fields and methods (like Python Enum)
│   │   ├── GenericsDemo.java          ← Generic stack/pair with bounded wildcards
│   │   └── AnnotationDemo.java
│   └── exercises/
│       ├── Ex01_GenericPair.java
│       ├── Ex02_StatusEnum.java
│       └── solutions/
│
├── 04-strings-and-arrays/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-strings.md              ← String immutability, String pool, == vs .equals()
│   │   ├── 02-stringbuilder.md        ← StringBuilder vs StringBuffer vs String performance
│   │   ├── 03-arrays.md               ← 1D, 2D, jagged arrays; Array vs Python list
│   │   ├── 04-array-of-objects.md     ← Arrays of reference types, sorting with Comparator
│   │   ├── StringDemo.java
│   │   ├── StringBuilderDemo.java
│   │   └── ArraysDemo.java
│   └── exercises/
│       ├── Ex01_StringManipulation.java
│       ├── Ex02_MatrixOperations.java
│       └── solutions/
│
├── 05-exception-handling/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-exception-hierarchy.md  ← Checked vs Unchecked; try/catch/finally
│   │   ├── 02-custom-exceptions.md    ← Creating domain-specific exceptions
│   │   ├── 03-try-with-resources.md   ← AutoCloseable, Python context manager comparison
│   │   ├── 04-throws-vs-throw.md      ← Declaration vs throwing
│   │   ├── ExceptionHierarchyDemo.java ← ASCII tree of Throwable hierarchy
│   │   ├── CustomExceptionDemo.java
│   │   └── TryWithResourcesDemo.java
│   └── exercises/
│       ├── Ex01_BankTransactionException.java
│       ├── Ex02_FileReaderWithResources.java
│       └── solutions/
│
├── 06-collections/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-collections-overview.md ← ASCII diagram of Collection hierarchy
│   │   ├── 02-list.md                 ← ArrayList vs LinkedList; Python list comparison
│   │   ├── 03-set.md                  ← HashSet/LinkedHashSet/TreeSet; uniqueness
│   │   ├── 04-map.md                  ← HashMap/LinkedHashMap/TreeMap; Python dict comparison
│   │   ├── 05-queue-deque.md          ← Queue, Deque, PriorityQueue
│   │   ├── 06-comparable-comparator.md ← Sorting strategies, lambda comparators
│   │   ├── CollectionHierarchyDemo.java ← ASCII Big Picture diagram in file header
│   │   ├── ListDemo.java
│   │   ├── SetDemo.java
│   │   ├── MapDemo.java
│   │   └── SortingDemo.java
│   └── exercises/
│       ├── Ex01_InventoryWithMap.java
│       ├── Ex02_StudentRanking.java
│       └── solutions/
│
├── 07-functional-java/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-functional-interfaces.md ← Predicate, Function, Consumer, Supplier
│   │   ├── 02-lambda-expressions.md    ← Syntax, closures, Python lambda comparison
│   │   ├── 03-method-references.md     ← 4 types: static, instance, constructor, arbitrary
│   │   ├── 04-stream-api.md            ← map/filter/reduce/collect — Python generator comparison
│   │   ├── 05-optional.md              ← null-safe patterns, Python None comparison
│   │   ├── 06-parallel-streams.md      ← When to use, thread safety considerations
│   │   ├── LambdaDemo.java
│   │   ├── StreamApiDemo.java          ← Chained stream pipeline with ASCII flow diagram
│   │   ├── OptionalDemo.java
│   │   └── ParallelStreamDemo.java
│   └── exercises/
│       ├── Ex01_StreamPipeline.java    ← Filter/map/collect on product list
│       ├── Ex02_OptionalChain.java
│       └── solutions/
│
└── 08-multithreading/
    ├── README.md
    ├── explanation/
    │   ├── 01-threads-basics.md        ← Thread lifecycle ASCII state diagram
    │   ├── 02-runnable-callable.md     ← Runnable vs Callable vs Thread; Python threading comparison
    │   ├── 03-race-conditions.md       ← synchronized keyword, volatile
    │   ├── 04-executor-service.md      ← Thread pools, ExecutorService, Future
    │   ├── 05-concurrent-collections.md ← ConcurrentHashMap, CopyOnWriteArrayList
    │   ├── ThreadLifecycleDemo.java    ← ASCII thread state diagram in comments
    │   ├── RaceConditionDemo.java      ← Before/after synchronized fix
    │   ├── ExecutorServiceDemo.java
    │   └── ConcurrentCollectionsDemo.java
    └── exercises/
        ├── Ex01_ProducerConsumer.java
        ├── Ex02_ThreadPoolExample.java
        └── solutions/
```

---

## 01 — Advanced Java

```
01-advanced-java/
├── README.md
│
├── 01-design-patterns-java/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-singleton.md            ← Thread-safe singleton, enum singleton
│   │   ├── 02-factory.md              ← Factory method, abstract factory
│   │   ├── 03-builder.md              ← Builder pattern — used heavily in Spring
│   │   ├── 04-strategy.md             ← Strategy pattern — key for understanding Spring internals
│   │   ├── 05-observer.md             ← Event-driven design, Spring events connection
│   │   ├── 06-decorator.md            ← Wrapping behavior — foundation for AOP
│   │   ├── 07-proxy.md                ← Static vs Dynamic proxy — JDK Proxy, CGLIB
│   │   ├── SingletonDemo.java
│   │   ├── FactoryPatternDemo.java
│   │   ├── BuilderPatternDemo.java
│   │   ├── StrategyPatternDemo.java
│   │   ├── ObserverPatternDemo.java
│   │   └── ProxyPatternDemo.java      ← ASCII diagram showing proxy wrapping target
│   └── exercises/
│       ├── Ex01_LoggerSingleton.java
│       ├── Ex02_NotificationStrategy.java
│       └── solutions/
│
├── 02-java-io-nio/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-file-io.md              ← File, FileReader, BufferedReader, PrintWriter
│   │   ├── 02-serialization.md        ← Serializable, transient, serialVersionUID
│   │   ├── 03-nio2.md                 ← Path, Files, DirectoryStream, WatchService
│   │   ├── FileIODemo.java
│   │   ├── SerializationDemo.java
│   │   └── NIO2Demo.java
│   └── exercises/
│       ├── Ex01_ConfigFileReader.java
│       └── solutions/
│
└── 03-java-records-sealed/
    ├── README.md
    ├── explanation/
    │   ├── 01-records.md              ← Java records as DTOs — replaces boilerplate classes
    │   ├── 02-sealed-classes.md       ← Sealed interfaces for discriminated unions
    │   ├── 03-pattern-matching.md     ← instanceof pattern matching, switch expressions
    │   ├── RecordsDemo.java
    │   └── SealedClassDemo.java
    └── exercises/
        ├── Ex01_ProductRecord.java
        └── solutions/
```

---

## 02 — Maven Build Tool

```
02-maven-build-tool/
├── README.md
│
├── 01-maven-basics/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-what-is-maven.md        ← Maven vs Gradle vs pip; POM concept
│   │   ├── 02-pom-structure.md        ← groupId/artifactId/version, parent POM
│   │   ├── 03-lifecycle-phases.md     ← validate→compile→test→package→install→deploy
│   │   ├── 04-dependencies.md         ← scope: compile/test/provided/runtime
│   │   ├── 05-plugins.md              ← compiler, surefire, spring-boot plugin
│   │   ├── 06-multi-module.md         ← Parent POM with child modules
│   │   ├── MavenLifecycle.md          ← ASCII diagram of full build lifecycle
│   │   └── sample-pom.xml             ← Annotated POM with comments on every element
│   └── exercises/
│       ├── README.md                  ← Create a multi-module Maven project from scratch
│       └── solutions/
│           └── multi-module-project/
```

---

## 03 — JDBC

```
03-jdbc/
├── README.md                          ← JDBC vs Python psycopg2/SQLAlchemy comparison
│
├── 01-jdbc-basics/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-jdbc-architecture.md    ← ASCII diagram: Java App → JDBC API → Driver → DB
│   │   ├── 02-connection-steps.md     ← DriverManager, Connection, Statement steps
│   │   ├── 03-crud-operations.md      ← executeQuery vs executeUpdate
│   │   ├── 04-prepared-statement.md   ← SQL injection prevention, parameterized queries
│   │   ├── 05-connection-pooling.md   ← HikariCP, c3p0 — why raw connections are bad
│   │   ├── JDBCConnectionDemo.java    ← Annotated with ASCII connection lifecycle
│   │   ├── CRUDWithJDBC.java
│   │   ├── PreparedStatementDemo.java
│   │   └── ConnectionPoolDemo.java
│   └── exercises/
│       ├── README.md                  ← Build a Student CRUD with PostgreSQL
│       ├── Ex01_StudentCRUD.java
│       └── solutions/
```

---

## 04 — Hibernate & JPA

```
04-hibernate-jpa/
├── README.md                          ← ORM concept map; Python SQLAlchemy comparison
│
├── 01-hibernate-basics/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-orm-concept.md          ← Object-Relational Mismatch; why ORM exists
│   │   ├── 02-hibernate-architecture.md ← ASCII: Entity → SessionFactory → Session → DB
│   │   ├── 03-entity-annotations.md   ← @Entity, @Table, @Column, @Id, @GeneratedValue
│   │   ├── 04-session-operations.md   ← save/get/update/delete/merge
│   │   ├── 05-hql.md                  ← Hibernate Query Language vs SQL vs JPQL
│   │   ├── 06-caching.md              ← L1 cache (Session), L2 cache (Ehcache/Redis)
│   │   ├── EntityDemo.java            ← Annotated entity with full ASCII mapping diagram
│   │   ├── HibernateSessionDemo.java
│   │   └── HQLDemo.java
│   └── exercises/
│       ├── Ex01_ProductEntity.java
│       └── solutions/
│
├── 02-jpa-annotations/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-jpa-vs-hibernate.md     ← JPA = specification, Hibernate = implementation
│   │   ├── 02-primary-key-strategies.md ← AUTO, IDENTITY, SEQUENCE, TABLE
│   │   ├── 03-embeddable.md           ← @Embeddable/@Embedded for value objects
│   │   ├── 04-lifecycle-callbacks.md  ← @PrePersist/@PostLoad etc.
│   │   ├── JPAEntityDemo.java
│   │   └── EmbeddableDemo.java
│   └── exercises/
│
├── 03-relationships/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-one-to-one.md           ← @OneToOne; owning vs inverse side; ASCII ER diagram
│   │   ├── 02-one-to-many.md          ← @OneToMany/@ManyToOne; mappedBy; join column
│   │   ├── 03-many-to-many.md         ← @ManyToMany; join table; owning side
│   │   ├── 04-fetch-types.md          ← EAGER vs LAZY — the N+1 problem explained
│   │   ├── 05-cascade-types.md        ← CascadeType: ALL/PERSIST/MERGE/REMOVE
│   │   ├── 06-bidirectional-pitfalls.md ← Infinite recursion, equals/hashCode issues
│   │   ├── OneToOneDemo.java          ← User ↔ Address with ASCII diagram
│   │   ├── OneToManyDemo.java         ← Department → Employees
│   │   ├── ManyToManyDemo.java        ← Students ↔ Courses
│   │   └── FetchTypeDemo.java         ← Demonstrating N+1 problem and fix
│   └── exercises/
│       ├── Ex01_LibraryMapping.java   ← Library, Book, Author, Member relationships
│       └── solutions/
│
└── 04-advanced-jpa/
    ├── README.md
    ├── explanation/
    │   ├── 01-jpql.md                 ← JPQL, named queries, native queries
    │   ├── 02-criteria-api.md         ← Type-safe queries with CriteriaBuilder
    │   ├── 03-pagination.md           ← setFirstResult/setMaxResults
    │   ├── 04-entity-graphs.md        ← Named entity graphs to control fetch
    │   ├── 05-optimistic-locking.md   ← @Version for concurrent updates
    │   ├── JPQLDemo.java
    │   ├── CriteriaAPIDemo.java
    │   └── OptimisticLockingDemo.java
    └── exercises/
        ├── Ex01_SearchWithCriteria.java
        └── solutions/
```

---

## 05 — Spring Core

```
05-spring-core/
├── README.md                          ← "Spring is a DI container" — mental model
│
├── 01-ioc-and-di/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-what-is-ioc.md          ← Inversion of Control; Hollywood Principle
│   │   ├── 02-dependency-injection.md ← Constructor/Setter/Field injection — pros/cons
│   │   ├── 03-spring-container.md     ← BeanFactory vs ApplicationContext; ASCII diagram
│   │   ├── 04-bean-definition.md      ← How Spring creates and manages beans
│   │   ├── 05-python-fastapi-vs-spring.md ← Direct mental model map for you
│   │   ├── IoCConceptDemo.java        ← Before DI vs After DI comparison
│   │   ├── ConstructorInjectionDemo.java  ← With ASCII showing wiring
│   │   ├── SetterInjectionDemo.java
│   │   └── FieldInjectionDemo.java    ← Also shows WHY field injection is discouraged
│   └── exercises/
│       ├── Ex01_ServiceWithDI.java
│       └── solutions/
│
├── 02-bean-configuration/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-component-scanning.md   ← @Component, @Service, @Repository, @Controller
│   │   ├── 02-java-config.md          ← @Configuration + @Bean — explicit wiring
│   │   ├── 03-qualifier-primary.md    ← Resolving ambiguity when multiple beans match
│   │   ├── 04-value-annotation.md     ← @Value for injecting properties
│   │   ├── 05-profiles.md             ← @Profile for env-based bean selection
│   │   ├── ComponentScanDemo.java
│   │   ├── JavaConfigDemo.java
│   │   ├── QualifierDemo.java
│   │   └── ProfileDemo.java
│   └── exercises/
│
├── 03-bean-scopes-lifecycle/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-bean-scopes.md          ← Singleton/Prototype/Request/Session/Application
│   │   ├── 02-lifecycle-hooks.md      ← @PostConstruct/@PreDestroy, InitializingBean
│   │   ├── 03-lazy-initialization.md  ← @Lazy — deferred bean creation
│   │   ├── BeanScopeDemo.java         ← ASCII diagram showing singleton vs prototype
│   │   └── BeanLifecycleDemo.java
│   └── exercises/
│       ├── Ex01_PrototypeScopedBean.java
│       └── solutions/
│
└── 04-spring-events/
    ├── README.md
    ├── explanation/
    │   ├── 01-application-events.md   ← Built-in events: ContextRefreshedEvent etc.
    │   ├── 02-custom-events.md        ← ApplicationEvent, ApplicationEventPublisher
    │   ├── 03-async-events.md         ← @Async event listeners
    │   ├── CustomEventDemo.java
    │   └── AsyncEventDemo.java
    └── exercises/
        ├── Ex01_OrderPlacedEvent.java
        └── solutions/
```

---

## 06 — Spring Boot Fundamentals

```
06-spring-boot-fundamentals/
├── README.md
│
├── 01-spring-boot-intro/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-spring-vs-spring-boot.md ← What Spring Boot adds: auto-config, starters, embedded server
│   │   ├── 02-spring-initializr.md    ← How to use start.spring.io; project structure
│   │   ├── 03-auto-configuration.md   ← How @SpringBootApplication works under the hood
│   │   ├── 04-project-structure.md    ← Package structure best practices
│   │   ├── 05-application-properties.md ← application.properties vs application.yml
│   │   ├── SpringBootAppDemo.java      ← First annotated Spring Boot app
│   │   └── AutoConfigurationExplorer.java ← List all auto-configured beans
│   └── exercises/
│       ├── Ex01_FirstSpringBootApp.md
│       └── solutions/
│
├── 02-starters-and-dependencies/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-spring-boot-starters.md ← spring-boot-starter-web, data-jpa, security etc.
│   │   ├── 02-parent-pom.md           ← Dependency management via spring-boot-starter-parent
│   │   ├── 03-dependency-overriding.md ← How to override managed versions
│   │   └── sample-pom-annotated.xml
│   └── exercises/
│
├── 03-actuator-devtools/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-actuator-overview.md    ← /health, /info, /metrics, /env endpoints
│   │   ├── 02-custom-actuator.md      ← Creating custom health indicators
│   │   ├── 03-securing-actuator.md    ← Exposing only needed endpoints
│   │   ├── 04-devtools.md             ← Auto-restart, live reload
│   │   ├── ActuatorDemo.java
│   │   └── CustomHealthIndicator.java
│   └── exercises/
│
└── 04-configuration-management/
    ├── README.md
    ├── explanation/
    │   ├── 01-externalized-config.md  ← Properties hierarchy: CLI > env > file > defaults
    │   ├── 02-config-properties.md    ← @ConfigurationProperties for type-safe config
    │   ├── 03-profiles-config.md      ← application-dev.yml, application-prod.yml
    │   ├── 04-environment-variables.md ← 12-factor app config approach
    │   ├── AppConfigDemo.java
    │   ├── ConfigPropertiesDemo.java
    │   └── AppConfig.java             ← @ConfigurationProperties POJO
    └── exercises/
        ├── Ex01_MultiEnvConfig.md
        └── solutions/
```

---

## 07 — Spring REST API

```
07-spring-rest-api/
├── README.md                          ← FastAPI vs Spring Boot REST direct comparison
│
├── 01-rest-fundamentals/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-rest-principles.md      ← REST constraints; HTTP methods; status codes
│   │   ├── 02-json-and-jackson.md     ← Jackson ObjectMapper, @JsonProperty, custom serializers
│   │   ├── 03-content-negotiation.md  ← Produces/Consumes; JSON vs XML
│   │   ├── 04-api-design-best-practices.md ← Resource naming, versioning, HATEOAS
│   │   ├── RESTConceptsDemo.java
│   │   └── JacksonDemo.java
│   └── exercises/
│
├── 02-rest-controller/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-rest-controller.md      ← @RestController vs @Controller; @ResponseBody
│   │   ├── 02-request-mappings.md     ← @GetMapping/@PostMapping/@PutMapping/@DeleteMapping/@PatchMapping
│   │   ├── 03-path-variables.md       ← @PathVariable; @RequestParam; optional params
│   │   ├── 04-request-body.md         ← @RequestBody; DTO pattern; validation
│   │   ├── 05-response-entity.md      ← ResponseEntity for full HTTP response control
│   │   ├── 06-http-headers.md         ← Reading/writing headers; @RequestHeader
│   │   ├── BasicRestController.java   ← Annotated with ASCII HTTP flow diagram
│   │   ├── PathVariableDemo.java
│   │   ├── RequestBodyDemo.java
│   │   └── ResponseEntityDemo.java
│   └── exercises/
│       ├── Ex01_ProductController.java
│       └── solutions/
│
├── 03-exception-handling/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-exception-handling-rest.md ← @ExceptionHandler; @ControllerAdvice
│   │   ├── 02-global-exception-handler.md ← Centralized error handling; RFC 7807 Problem Details
│   │   ├── 03-custom-error-response.md ← Standard error response DTO
│   │   ├── 04-validation-errors.md    ← Bean Validation error responses
│   │   ├── GlobalExceptionHandler.java ← Full @ControllerAdvice with all common scenarios
│   │   ├── CustomExceptions.java
│   │   └── ErrorResponse.java         ← Standard error DTO
│   └── exercises/
│       ├── Ex01_GlobalExceptionHandling.java
│       └── solutions/
│
├── 04-validation/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-bean-validation.md      ← @Valid, @NotNull, @Size, @Email, @Pattern
│   │   ├── 02-custom-validators.md    ← @Constraint + ConstraintValidator<A,T>
│   │   ├── 03-method-validation.md    ← @Validated at service level
│   │   ├── 04-groups.md               ← Validation groups for create vs update
│   │   ├── ValidationDemo.java
│   │   └── CustomValidatorDemo.java
│   └── exercises/
│
├── 05-openapi-swagger/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-springdoc-openapi.md    ← Automatic API docs with springdoc-openapi
│   │   ├── 02-api-annotations.md      ← @Operation, @ApiResponse, @Schema
│   │   ├── 03-customizing-docs.md     ← Global info, auth schemes in swagger
│   │   ├── SwaggerConfigDemo.java
│   │   └── AnnotatedController.java
│   └── exercises/
│
├── 06-rest-api-versioning/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-versioning-strategies.md ← URI vs Header vs Query param versioning
│   │   ├── 02-implementing-versioning.md ← Code examples for each strategy
│   │   ├── V1ProductController.java
│   │   └── V2ProductController.java
│   └── exercises/
│
└── 07-full-crud-project/
    ├── README.md                      ← Employee Management REST API — requirements
    ├── employee-api/                  ← Full Spring Boot project
    │   ├── pom.xml
    │   └── src/
    │       ├── main/java/com/learning/employee/
    │       │   ├── EmployeeApiApplication.java
    │       │   ├── controller/
    │       │   │   └── EmployeeController.java
    │       │   ├── service/
    │       │   │   ├── EmployeeService.java     ← Interface
    │       │   │   └── EmployeeServiceImpl.java
    │       │   ├── repository/
    │       │   │   └── EmployeeRepository.java
    │       │   ├── entity/
    │       │   │   └── Employee.java
    │       │   ├── dto/
    │       │   │   ├── EmployeeRequestDTO.java
    │       │   │   └── EmployeeResponseDTO.java
    │       │   └── exception/
    │       │       ├── EmployeeNotFoundException.java
    │       │       └── GlobalExceptionHandler.java
    │       └── resources/
    │           └── application.yml
    └── testing-guide.md               ← How to test with curl + Postman + HTTPie
```

---

## 08 — Spring Data JPA

```
08-spring-data-jpa/
├── README.md
│
├── 01-jpa-repository/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-spring-data-overview.md ← JpaRepository hierarchy ASCII diagram
│   │   ├── 02-repository-interfaces.md ← CrudRepository vs JpaRepository vs PagingAndSorting
│   │   ├── 03-derived-query-methods.md ← findByNameAndAge, findByEmailContaining etc.
│   │   ├── 04-jpql-custom-queries.md  ← @Query with JPQL and native SQL
│   │   ├── 05-pagination-sorting.md   ← Pageable, Sort, Page<T> response
│   │   ├── 06-optional-pattern.java   ← findById().orElseThrow() patterns
│   │   ├── ProductRepository.java     ← Annotated with all query method patterns
│   │   ├── PaginationDemo.java
│   │   └── CustomQueryDemo.java
│   └── exercises/
│       ├── Ex01_ProductRepository.java
│       └── solutions/
│
├── 02-spring-data-rest/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-spring-data-rest.md     ← Auto-exposed REST endpoints from repositories
│   │   ├── 02-hateoas.md              ← HATEOAS, HAL format
│   │   ├── 03-customizing.md          ← @RepositoryRestResource, @RestResource
│   │   └── DataRestDemo.java
│   └── exercises/
│
└── 03-transactions/
    ├── README.md
    ├── explanation/
    │   ├── 01-transaction-management.md ← @Transactional; ACID properties
    │   ├── 02-propagation.md          ← REQUIRED/REQUIRES_NEW/NESTED etc.
    │   ├── 03-isolation-levels.md     ← READ_COMMITTED/REPEATABLE_READ/SERIALIZABLE
    │   ├── 04-readonly-transactions.md ← Performance optimization with readOnly=true
    │   ├── TransactionDemo.java
    │   └── PropagationDemo.java
    └── exercises/
        ├── Ex01_BankTransfer.java     ← Transfer with transaction rollback on failure
        └── solutions/
```

---

## 09 — Spring MVC & Thymeleaf

```
09-spring-mvc-thymeleaf/
├── README.md
│
├── 01-spring-mvc-internals/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-dispatcher-servlet.md   ← Full request lifecycle ASCII diagram
│   │   ├── 02-handler-mapping.md      ← How requests get routed
│   │   ├── 03-view-resolver.md        ← InternalResourceViewResolver, ThymeleafViewResolver
│   │   ├── 04-model-modelattribute.md ← Model, ModelMap, @ModelAttribute
│   │   ├── DispatcherServletFlow.java ← ASCII diagram embedded in comments
│   │   └── MVCDemo.java
│   └── exercises/
│
├── 02-thymeleaf/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-thymeleaf-basics.md     ← th:text, th:href, th:src, th:if, th:each
│   │   ├── 02-form-binding.md         ← th:object, th:field — bidirectional binding
│   │   ├── 03-layout-fragments.md     ← th:fragment, th:replace, th:insert for layout reuse
│   │   ├── 04-thymeleaf-security.md   ← sec:authorize, sec:authentication integration
│   │   └── templates/
│   │       ├── base-layout.html
│   │       ├── list-view.html
│   │       └── form-view.html
│   └── exercises/
│
└── 03-form-validation-mvc/
    ├── README.md
    ├── explanation/
    │   ├── 01-mvc-validation.md       ← @Valid on @ModelAttribute, BindingResult
    │   ├── 02-custom-messages.md      ← messages.properties for error messages
    │   ├── 03-initbinder.md           ← @InitBinder, StringTrimmerEditor
    │   ├── FormValidationController.java
    │   └── templates/
    │       └── registration-form.html
    └── exercises/
        ├── Ex01_RegistrationForm.md
        └── solutions/
```

---

## 10 — Spring Security

```
10-spring-security/
├── README.md                          ← Security threat model; OWASP Top 10 overview
│
├── 01-security-architecture/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-security-filter-chain.md ← ASCII: Request → FilterChain → SecurityContext
│   │   ├── 02-authentication-flow.md  ← AuthenticationManager → Provider → UserDetailsService
│   │   ├── 03-authorization.md        ← AccessDecisionManager, voters
│   │   ├── 04-security-context.md     ← SecurityContextHolder, ThreadLocal storage
│   │   ├── SecurityFilterChainDemo.java ← Shows all default filters in order
│   │   └── SecurityArchitectureDiagram.md ← ASCII deep-dive diagram
│   └── exercises/
│
├── 02-form-based-auth/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-default-security.md     ← What happens without any configuration
│   │   ├── 02-custom-login.md         ← Custom login form, error handling
│   │   ├── 03-logout.md               ← Logout flow, session invalidation
│   │   ├── 04-remember-me.md          ← Remember-me tokens
│   │   ├── SecurityConfigDemo.java    ← Annotated SecurityFilterChain bean
│   │   └── templates/
│   │       └── login.html
│   └── exercises/
│
├── 03-user-management/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-user-details-service.md ← UserDetailsService; UserDetails contract
│   │   ├── 02-in-memory-users.md      ← InMemoryUserDetailsManager (dev only)
│   │   ├── 03-jdbc-authentication.md  ← Default schema + custom schema
│   │   ├── 04-password-encoding.md    ← BCryptPasswordEncoder; why plain text is evil
│   │   ├── 05-user-registration.md    ← Registering users with encrypted passwords
│   │   ├── CustomUserDetailsService.java
│   │   ├── UserEntity.java
│   │   ├── UserRepository.java
│   │   └── RegistrationController.java
│   └── exercises/
│       ├── Ex01_UserRegistrationFlow.md
│       └── solutions/
│
├── 04-method-security/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-method-security.md      ← @PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed
│   │   ├── 02-role-based-access.md    ← ROLE_ prefix; role hierarchy
│   │   ├── 03-expression-based.md     ← SpEL in security expressions
│   │   ├── MethodSecurityDemo.java
│   │   └── RoleBasedController.java
│   └── exercises/
│
└── 05-csrf-cors/
    ├── README.md
    ├── explanation/
    │   ├── 01-csrf.md                 ← CSRF attack; CSRF token; same-site cookies
    │   ├── 02-cors.md                 ← CORS preflight; @CrossOrigin; global CORS config
    │   ├── CSRFDemo.java
    │   └── CORSConfigDemo.java
    └── exercises/
```

---

## 11 — JWT & OAuth2

```
11-jwt-oauth2/
├── README.md
│
├── 01-jwt-deep-dive/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-cryptography-basics.md  ← Symmetric vs asymmetric; signing vs encryption
│   │   ├── 02-jwt-structure.md        ← Header.Payload.Signature; base64; claims
│   │   ├── 03-jwt-flow.md             ← Login → Token → Request flow with ASCII diagram
│   │   ├── 04-jwt-in-spring.md        ← Dependencies, configuration
│   │   ├── 05-jwt-filter.md           ← OncePerRequestFilter implementation
│   │   ├── 06-refresh-tokens.md       ← Access + refresh token pattern
│   │   ├── 07-jwt-security-concerns.md ← Algorithm confusion, token storage, expiry
│   │   ├── JWTUtil.java               ← Token generation, validation, claims extraction
│   │   ├── JWTAuthFilter.java         ← Full JWT filter with ASCII flow in comments
│   │   ├── AuthController.java        ← Login endpoint returning JWT
│   │   └── SecurityConfig.java        ← Stateless session; JWT filter chain
│   └── exercises/
│       ├── Ex01_JWTAuthSystem.md      ← Build complete JWT auth from scratch
│       └── solutions/
│           └── jwt-auth-project/
│
└── 02-oauth2/
    ├── README.md
    ├── explanation/
    │   ├── 01-oauth2-concepts.md      ← OAuth2 grant types; roles; flow ASCII diagram
    │   ├── 02-oidc.md                 ← OpenID Connect on top of OAuth2
    │   ├── 03-social-login.md         ← Google/GitHub login with Spring Security OAuth2
    │   ├── 04-resource-server.md      ← Protecting APIs with JWT Bearer tokens
    │   ├── 05-authorization-server.md ← Spring Authorization Server
    │   ├── OAuth2LoginDemo.java
    │   ├── ResourceServerConfig.java
    │   └── application-oauth.yml      ← OAuth2 client config with placeholders
    └── exercises/
        ├── Ex01_SocialLoginApp.md
        └── solutions/
```

---

## 12 — Spring AOP

```
12-spring-aop/
├── README.md                          ← Cross-cutting concerns; AOP vs OOP
│
├── 01-aop-concepts/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-aop-terminology.md      ← Aspect/Advice/Pointcut/JoinPoint/Weaving — all explained
│   │   ├── 02-spring-aop-vs-aspectj.md ← Proxy-based vs compile-time weaving
│   │   ├── 03-aop-proxy-types.md      ← JDK Proxy vs CGLIB — when each is used
│   │   ├── AOPConceptsDemo.java       ← ASCII diagram: proxy wrapping target bean
│   │   └── AOPArchitectureDiagram.md  ← Full ASCII deep-dive
│   └── exercises/
│
├── 02-advice-types/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-before-advice.md        ← @Before; use cases: logging, validation
│   │   ├── 02-after-returning.md      ← @AfterReturning; post-process return value
│   │   ├── 03-after-throwing.md       ← @AfterThrowing; exception handling/notification
│   │   ├── 04-after-finally.md        ← @After; cleanup regardless of outcome
│   │   ├── 05-around-advice.md        ← @Around; full control; ProceedingJoinPoint
│   │   ├── BeforeAdviceDemo.java
│   │   ├── AfterReturningDemo.java
│   │   ├── AfterThrowingDemo.java
│   │   └── AroundAdviceDemo.java      ← Performance timing, caching, retry patterns
│   └── exercises/
│
├── 03-pointcut-expressions/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-execution-pointcut.md   ← execution() pattern; wildcards
│   │   ├── 02-within-pointcut.md      ← within() for package/class matching
│   │   ├── 03-annotation-pointcut.md  ← @annotation() for custom annotation triggering
│   │   ├── 04-combining-pointcuts.md  ← && || ! operators; reusable @Pointcut declarations
│   │   ├── PointcutDeclarationDemo.java
│   │   └── CombinedPointcutDemo.java
│   └── exercises/
│
└── 04-real-world-aop/
    ├── README.md
    ├── explanation/
    │   ├── 01-logging-aspect.md       ← Centralized logging for all service calls
    │   ├── 02-performance-monitoring.md ← Timing with @Around
    │   ├── 03-security-aspect.md      ← Custom @Auditable annotation + audit log
    │   ├── 04-retry-aspect.md         ← Retry with backoff using @Around
    │   ├── LoggingAspect.java
    │   ├── PerformanceAspect.java
    │   ├── AuditAspect.java
    │   └── RetryAspect.java
    └── exercises/
        ├── Ex01_LoggingAspect.md
        ├── Ex02_RetryMechanism.md
        └── solutions/
```

---

## 13 — Testing

```
13-testing/
├── README.md                          ← Testing pyramid; pytest vs JUnit mental model
│
├── 01-unit-testing-junit5/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-junit5-basics.md        ← @Test, @BeforeEach, @AfterEach, @BeforeAll, @AfterAll
│   │   ├── 02-assertions.md           ← Assertions class; assertAll; assertThrows
│   │   ├── 03-parameterized-tests.md  ← @ParameterizedTest, @CsvSource, @MethodSource
│   │   ├── 04-test-lifecycle.md       ← @TestInstance, test ordering
│   │   ├── 05-nested-tests.md         ← @Nested for grouping related tests
│   │   ├── BasicJUnit5Demo.java       ← Annotated with Python pytest comparison
│   │   ├── AssertionsDemo.java
│   │   └── ParameterizedTestDemo.java
│   └── exercises/
│       ├── Ex01_CalculatorTest.java
│       └── solutions/
│
├── 02-mockito/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-mocking-concept.md      ← What mocking is; when to mock; pytest.mock comparison
│   │   ├── 02-mockito-basics.md       ← @Mock, @InjectMocks, @Spy, @Captor
│   │   ├── 03-stubbing.md             ← when().thenReturn(); doReturn(); doThrow()
│   │   ├── 04-verification.md         ← verify(); times(); ArgumentCaptor
│   │   ├── 05-argument-matchers.md    ← any(), eq(), argThat()
│   │   ├── MockitoBasicsDemo.java
│   │   ├── StubbingDemo.java
│   │   └── ArgumentCaptorDemo.java
│   └── exercises/
│       ├── Ex01_ServiceLayerTest.java
│       └── solutions/
│
├── 03-spring-boot-testing/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-spring-boot-test.md     ← @SpringBootTest; full context vs slice tests
│   │   ├── 02-web-mvc-test.md         ← @WebMvcTest; MockMvc; testing controllers in isolation
│   │   ├── 03-data-jpa-test.md        ← @DataJpaTest; H2 in-memory; Testcontainers
│   │   ├── 04-mockmvc-patterns.md     ← GET/POST/PUT/DELETE request builders; JSON matchers
│   │   ├── 05-test-slices.md          ← @JsonTest, @RestClientTest and other slices
│   │   ├── ControllerLayerTest.java   ← @WebMvcTest example with MockMvc
│   │   ├── ServiceLayerTest.java      ← Pure Mockito unit test
│   │   ├── RepositoryLayerTest.java   ← @DataJpaTest with H2
│   │   └── IntegrationTest.java       ← Full @SpringBootTest
│   └── exercises/
│       ├── Ex01_ControllerTest.java
│       ├── Ex02_RepositoryTest.java
│       └── solutions/
│
└── 04-testcontainers/
    ├── README.md
    ├── explanation/
    │   ├── 01-testcontainers-intro.md ← Real DB in tests; no more H2 mismatch
    │   ├── 02-postgres-container.md   ← @Testcontainers, @Container, PostgreSQLContainer
    │   ├── 03-redis-container.md      ← Redis for integration tests
    │   ├── TestcontainersDemo.java
    │   └── PostgresIntegrationTest.java
    └── exercises/
        ├── Ex01_IntegrationWithRealDB.md
        └── solutions/
```

---

## 14 — Microservices

```
14-microservices/
├── README.md                          ← Monolith → Microservices tradeoffs; when to use
│
├── 01-microservices-concepts/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-microservices-principles.md ← 12-factor app; single responsibility; bounded context
│   │   ├── 02-communication-patterns.md ← Sync (REST/gRPC) vs Async (messaging) — ASCII diagram
│   │   ├── 03-data-management.md      ← Database-per-service; distributed transactions; Saga
│   │   ├── 04-resilience-patterns.md  ← Circuit breaker, retry, bulkhead, timeout
│   │   └── MicroservicesArchitecture.md ← Full ASCII architecture diagram
│   └── exercises/
│
├── 02-service-discovery/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-service-discovery-concept.md ← Client-side vs server-side discovery
│   │   ├── 02-eureka-server.md        ← Spring Cloud Netflix Eureka; setting up registry
│   │   ├── 03-eureka-client.md        ← @EnableEurekaClient; health integration
│   │   ├── 04-load-balancing.md       ← Spring Cloud LoadBalancer; round-robin
│   │   ├── EurekaServerApp.java
│   │   ├── EurekaClientConfig.java
│   │   └── application-eureka.yml
│   └── exercises/
│
├── 03-inter-service-communication/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-rest-template.md        ← RestTemplate (legacy but still used)
│   │   ├── 02-web-client.md           ← WebClient reactive HTTP client
│   │   ├── 03-open-feign.md           ← Declarative HTTP client; @FeignClient
│   │   ├── 04-feign-error-handling.md ← FeignClient fallback; ErrorDecoder
│   │   ├── 05-feign-with-eureka.md    ← Service name resolution through Eureka
│   │   ├── RestTemplateDemo.java
│   │   ├── WebClientDemo.java
│   │   ├── ProductFeignClient.java    ← Annotated Feign interface
│   │   └── FeignFallback.java
│   └── exercises/
│
├── 04-api-gateway/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-api-gateway-concept.md  ← Why a gateway; single entry point; ASCII diagram
│   │   ├── 02-spring-cloud-gateway.md ← Route configuration; predicates; filters
│   │   ├── 03-gateway-filters.md      ← Pre/post filters; rate limiting; auth
│   │   ├── 04-gateway-security.md     ← JWT validation at gateway level
│   │   ├── GatewayApplication.java
│   │   ├── RouteConfig.java
│   │   └── application-gateway.yml
│   └── exercises/
│
├── 05-resilience/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-circuit-breaker.md      ← Resilience4j; CLOSED/OPEN/HALF-OPEN states ASCII
│   │   ├── 02-retry.md                ← @Retry; exponential backoff
│   │   ├── 03-rate-limiter.md         ← @RateLimiter; token bucket algorithm
│   │   ├── 04-bulkhead.md             ← Thread pool isolation
│   │   ├── 05-timeout.md              ← @TimeLimiter; preventing cascading failures
│   │   ├── CircuitBreakerDemo.java
│   │   ├── RetryDemo.java
│   │   └── application-resilience.yml
│   └── exercises/
│
├── 06-distributed-tracing/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-tracing-concepts.md     ← TraceId, SpanId, correlation IDs
│   │   ├── 02-micrometer-tracing.md   ← Spring Boot 3 tracing with Micrometer
│   │   ├── 03-zipkin.md               ← Zipkin server; exporting traces
│   │   └── TracingDemo.java
│   └── exercises/
│
└── 07-microservices-project/
    ├── README.md                      ← E-commerce microservices: Order + Product + User + Gateway
    ├── architecture.md                ← Full ASCII architecture with all services
    ├── eureka-server/
    │   └── (Spring Boot project)
    ├── api-gateway/
    │   └── (Spring Cloud Gateway project)
    ├── user-service/
    │   └── (Spring Boot + JWT project)
    ├── product-service/
    │   └── (Spring Boot + JPA project)
    └── order-service/
        └── (Spring Boot + Feign + Resilience4j project)
```

---

## 15 — Docker

```
15-docker/
├── README.md
│
├── 01-docker-basics/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-containers-vs-vms.md    ← ASCII diagram; why Docker for microservices
│   │   ├── 02-docker-commands.md      ← run/pull/push/build/exec/logs/ps cheat sheet
│   │   ├── 03-dockerfile.md           ← Multi-stage build for Spring Boot
│   │   ├── 04-docker-layers.md        ← Layer caching; optimizing Spring Boot image builds
│   │   ├── Dockerfile.spring-boot     ← Production-grade multi-stage Dockerfile
│   │   └── .dockerignore
│   └── exercises/
│
├── 02-docker-compose/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-compose-basics.md       ← Services, networks, volumes
│   │   ├── 02-spring-with-postgres.md ← Spring Boot + PostgreSQL compose stack
│   │   ├── 03-compose-profiles.md     ← Dev vs test profiles in compose
│   │   ├── docker-compose.yml         ← Spring Boot + PostgreSQL + Redis
│   │   └── docker-compose-dev.yml
│   └── exercises/
│       ├── Ex01_SpringWithDB.md
│       └── solutions/
│
└── 03-spring-docker-integration/
    ├── README.md
    ├── explanation/
    │   ├── 01-spring-docker-compose.md ← Spring Boot 3 Docker Compose support
    │   ├── 02-testcontainers-docker.md ← Testcontainers with Docker
    │   ├── 03-health-checks.md         ← Docker health checks for Spring Boot
    │   └── application-docker.yml
    └── exercises/
```

---

## 16 — DSA (Data Structures & Algorithms)

> Focused on what Java developers need for interviews and understanding framework internals

```
16-dsa/
├── README.md                          ← Big-O quick reference; Python vs Java implementations
│
├── 01-complexity-analysis/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-big-o-notation.md       ← Time/space complexity with ASCII graphs
│   │   ├── 02-common-complexities.md  ← O(1)/O(log n)/O(n)/O(n log n)/O(n²) examples
│   │   └── ComplexityExamples.java
│   └── exercises/
│
├── 02-arrays-and-strings/
│   ├── README.md
│   ├── explanation/
│   │   ├── ArrayAlgorithms.java       ← Search, sort, two-pointer, sliding window
│   │   └── StringAlgorithms.java
│   └── exercises/
│
├── 03-linked-lists/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-linked-list-concept.md  ← ASCII diagram of node links
│   │   ├── LinkedList.java            ← Custom singly linked list implementation
│   │   └── LinkedListAlgorithms.java  ← Reverse, detect cycle, merge sorted
│   └── exercises/
│
├── 04-stacks-and-queues/
│   ├── README.md
│   ├── explanation/
│   │   ├── Stack.java
│   │   ├── Queue.java
│   │   └── CircularQueue.java
│   └── exercises/
│
├── 05-trees/
│   ├── README.md
│   ├── explanation/
│   │   ├── 01-bst-concept.md          ← ASCII tree diagrams
│   │   ├── BinarySearchTree.java
│   │   └── TreeTraversals.java        ← Inorder/preorder/postorder/BFS
│   └── exercises/
│
└── 06-sorting-algorithms/
    ├── README.md
    ├── explanation/
    │   ├── BubbleSort.java
    │   ├── SelectionSort.java
    │   ├── InsertionSort.java
    │   ├── MergeSort.java
    │   ├── QuickSort.java
    │   └── SortingComparison.md       ← When to use which; stability; complexity table
    └── exercises/
```

---

## 17 — Real World Projects

```
17-real-world-projects/
├── README.md
│
├── project-01-employee-management-api/
│   ├── README.md                      ← Requirements, API spec, architecture diagram
│   ├── src/                           ← Complete Spring Boot + JPA + Security project
│   └── testing/
│       ├── postman-collection.json
│       └── test-cases.md
│
├── project-02-ecommerce-api/
│   ├── README.md                      ← Product, Order, Cart, User — full domain model
│   ├── src/                           ← Spring Boot + JPA + JWT + AOP project
│   └── testing/
│
├── project-03-microservices-quiz-app/
│   ├── README.md                      ← Microservices version of the quiz domain
│   ├── question-service/
│   ├── quiz-service/
│   ├── eureka-server/
│   └── api-gateway/
│
└── project-04-job-portal/
    ├── README.md                      ← Full stack: REST API + Thymeleaf + Security
    └── src/
```

---

## Resources

```
resources/
├── cheatsheets/
│   ├── java-vs-python.md              ← Side-by-side syntax comparison
│   ├── spring-annotations.md          ← All key annotations with one-line descriptions
│   ├── jpa-annotations.md             ← Entity mapping quick reference
│   ├── spring-security-flow.md        ← Auth flow quick reference
│   ├── http-status-codes.md           ← Status codes with when to use each
│   └── maven-commands.md              ← Common Maven lifecycle commands
│
├── interview-prep/
│   ├── core-java-questions.md         ← 50 most asked Java questions with answers
│   ├── spring-questions.md            ← 50 most asked Spring/Boot questions
│   ├── hibernate-jpa-questions.md     ← 30 most asked ORM questions
│   ├── microservices-questions.md     ← 30 microservices design questions
│   ├── system-design-spring.md        ← System design with Spring ecosystem
│   └── coding-patterns.md             ← Most common algorithmic patterns for interviews
│
└── architecture-patterns/
    ├── layered-architecture.md        ← Controller → Service → Repository with ASCII
    ├── hexagonal-architecture.md      ← Ports and adapters with Spring
    ├── cqrs-with-spring.md            ← Command/Query segregation
    ├── event-sourcing.md              ← Event store concepts
    └── saga-pattern.md                ← Distributed transaction management
```

---

## Java File Convention

Every `.java` file in the `explanation/` folders follows this structure:

```java
/**
 * ============================================================
 * FILE: BeanScopeDemo.java
 * MODULE: 05-spring-core / 03-bean-scopes-lifecycle
 * ============================================================
 *
 * PURPOSE:
 *   Demonstrate Singleton vs Prototype bean scope behaviour
 *   in Spring and when to choose each.
 *
 * WHY THIS MATTERS:
 *   Singleton is the default — understanding it prevents bugs
 *   where shared state leaks between requests.
 *
 * HOW TO RUN:
 *   mvn spring-boot:run
 *   OR: right-click → Run in IntelliJ
 *
 * EXPECTED OUTPUT:
 *   [Singleton] Same instance: true
 *   [Prototype] Same instance: false
 *
 * ASCII DIAGRAM:
 *
 *   SINGLETON SCOPE
 *   ┌─────────────────────────────────────┐
 *   │  Spring Container                   │
 *   │  ┌─────────────────────┐            │
 *   │  │  singletonBean      │◄─── ref1  │
 *   │  │  (one instance)     │◄─── ref2  │
 *   │  └─────────────────────┘◄─── ref3  │
 *   └─────────────────────────────────────┘
 *
 *   PROTOTYPE SCOPE
 *   ┌─────────────────────────────────────┐
 *   │  Spring Container                   │
 *   │  ┌──────────┐ ┌──────────┐         │
 *   │  │ instance1│ │ instance2│◄── ref2 │
 *   │  └──────────┘ └──────────┘         │
 *   │       ▲                            │
 *   │      ref1                          │
 *   └─────────────────────────────────────┘
 *
 * RELATED FILES:
 *   - BeanLifecycleDemo.java (same folder)
 *   - README.md (module overview)
 * ============================================================
 */
```

---

## Exercise File Convention

Every `ExNN_*.java` in `exercises/` folders follows this structure:

```java
/**
 * ============================================================
 * EXERCISE: Ex01_BankTransfer.java
 * MODULE: 08-spring-data-jpa / 03-transactions
 * DIFFICULTY: Medium
 * ============================================================
 *
 * SCENARIO:
 *   You are building a banking service. Implement a transfer
 *   method that moves money between two accounts. It must be
 *   fully transactional — if the debit succeeds but the credit
 *   fails, the whole operation must roll back.
 *
 * REQUIREMENTS:
 *   1. Create AccountEntity with id, ownerId, balance
 *   2. Create AccountRepository extending JpaRepository
 *   3. Implement transferFunds(Long fromId, Long toId, BigDecimal amount)
 *   4. Annotate with @Transactional correctly
 *   5. Throw InsufficientFundsException if balance < amount
 *   6. Write a unit test to verify rollback on failure
 *
 * HOW TO TEST:
 *   Run Ex01_BankTransferTest.java (in exercises/solutions/)
 *   OR manually POST to /transfer endpoint if you wire it to a controller
 *
 * HINT:
 *   Look at PropagationDemo.java in explanation/ for @Transactional patterns
 * ============================================================
 */

// YOUR CODE HERE
```

---

*Total topics: 16 learning modules + 4 real-world projects + resource library*
*Estimated Java files: ~280+ annotated files across explanation + exercises*
