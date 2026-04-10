# 01-IoC-Container — Progressive Quiz Drill

## Round 1 — Core Recall (8 questions)
Answer from memory, then check the key.

**Q1:** What does IoC stand for and what is "inverted"?
**Q2:** What is the difference between `BeanFactory` and `ApplicationContext`?
**Q3:** Name the 3 configuration styles in Spring and their chronological order.
**Q4:** You have `@SpringBootApplication` on your main class. What 3 annotations does it combine?
**Q5:** In XML config, how do you wire one bean into another's constructor?
**Q6:** What happens if you put `@SpringBootApplication` class in the `com` package?
**Q7:** Can you have multiple `ApplicationContext` instances in one JVM?
**Q8:** What is the default bean creation strategy of `ApplicationContext` — lazy or eager?

### Answer Key — Round 1
A1: Inversion of Control. The "control" of object creation and wiring is inverted — instead of YOUR code creating dependencies with `new`, the CONTAINER creates and injects them.
A2: `BeanFactory` is lazy (creates beans on first `getBean()` call) and provides basic DI only. `ApplicationContext` extends `BeanFactory` with eager initialization, events, AOP, i18n, and environment support. Always use `ApplicationContext`.
A3: XML Config (2004) → Annotation Config with `@Component`/`@Autowired` (2007, Spring 2.5) → Java Config with `@Configuration`/`@Bean` (2014, Spring 4.0+)
A4: `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`
A5: `<constructor-arg ref="otherBeanId"/>` inside the `<bean>` definition
A6: It scans ALL classes in ALL packages on the classpath — startup takes minutes and may pull in unintended beans from third-party JARs.
A7: Yes — parent-child contexts. Spring Boot creates one by default, but in web apps you can have a root context and a servlet context. Microservices typically have one.
A8: Eager — all singleton beans are created at startup. This is why `ApplicationContext` catches wiring errors immediately rather than at runtime.

---

## Round 2 — Apply & Compare (3 questions)
Translate between Python/FastAPI and Spring.

**Q1:** Translate this FastAPI dependency pattern to Spring IoC:
```python
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@app.get("/users")
def get_users(db: Session = Depends(get_db)):
    return db.query(User).all()
```

**Q2:** In FastAPI, you start the app with `uvicorn main:app --reload`. What is the Spring Boot equivalent and what does it do behind the scenes?

**Q3:** Python uses explicit imports (`from services import UserService`). How does Spring "import" services, and why is this considered better for large applications?

### Answer Key — Round 2
A1:
```java
@Configuration
public class DbConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig);
    }
}

@RestController
public class UserController {
    private final UserRepository userRepo; // injected by Spring
    
    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    
    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepo.findAll();
    }
}
```
Key difference: FastAPI creates a NEW db session per request via `yield`. Spring injects a SINGLETON repository that uses connection pooling — one instance handles ALL requests.

A2: `./gradlew :module:bootRun` — Behind the scenes: JVM starts → `SpringApplication.run()` → creates `ApplicationContext` → `@ComponentScan` discovers beans → DI wires dependencies → `@PostConstruct` runs → embedded Tomcat starts on port 8080 → `ApplicationReadyEvent` fires. DevTools enables hot-reload (like `--reload`).

A3: Spring uses `@ComponentScan` — it scans the classpath for classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`. Benefits: (1) no manual import statements for every service, (2) adding a new service requires zero changes to existing code, (3) decouples modules — you don't need to know the concrete class, just the interface.

---

## Round 3 — Debug the Bug (3 scenarios)

**Scenario 1:** Your Spring Boot app starts but your `@Service` class is never instantiated. You verified the annotation is present. The `@SpringBootApplication` class is in `com.myapp` but your service is in `com.other.services`. What's wrong?

**Scenario 2:** You have two `@Configuration` classes, each defining a `@Bean DataSource dataSource()` method. The app starts fine but connects to the wrong database. What happened?

**Scenario 3:** A colleague replaces `ApplicationContext` with `BeanFactory` for "performance". Now your `@Scheduled` tasks and `@EventListener` methods stop working. Why?

### Answer Key — Round 3
S1: `@ComponentScan` only scans the package of the `@SpringBootApplication` class and its sub-packages. `com.other.services` is NOT a sub-package of `com.myapp`. Fix: either move the service to `com.myapp.services`, or add `@ComponentScan(basePackages = {"com.myapp", "com.other"})`.

S2: Spring registered both beans but used the last one processed (bean definition overriding). Fix: use `@Primary` on the preferred DataSource, or `@Qualifier("prodDb")` to disambiguate. Better: set `spring.main.allow-bean-definition-overriding=false` (Spring Boot 2.1+ default) to catch this at startup.

S3: `BeanFactory` is a basic container — it does NOT support `ApplicationEventPublisher` (events), `ScheduledExecutorService` integration (`@Scheduled`), or AOP proxies (`@Async`). These features require `ApplicationContext`. This is why you should ALWAYS use `ApplicationContext`.

---

## Round 4 — Staff-Level Scenarios (2 questions)

**Q1 — Container Hierarchy:**
Your microservice needs to load some beans from a shared library JAR and some from local configuration. The shared beans should NOT see local beans, but local beans should be able to inject shared beans. How do you structure this with ApplicationContext?

**Q2 — Startup Performance:**
Your Spring Boot app takes 45 seconds to start because it has 2000+ beans. What strategies would you use to reduce startup time while keeping eager initialization for critical beans?

### Answer Key — Round 4

A1: Use parent-child `ApplicationContext`:
- **Parent context**: loads shared library `@Configuration` classes via `@Import`
- **Child context**: loads local `@Configuration` — can inject beans from parent
- Parent beans cannot see child beans (one-way visibility)
- In Spring Boot: `SpringApplicationBuilder.parent(SharedConfig.class).child(LocalConfig.class).run(args)`

A2: Strategies for reducing startup time:
1. **Lazy initialization**: `spring.main.lazy-initialization=true` — beans created on first use, not startup. Mark critical beans with `@Lazy(false)` to keep them eager.
2. **Spring AOT** (Ahead-of-Time): compile-time bean processing, eliminates reflection at startup
3. **GraalVM Native Image**: compiles to native binary, ~100ms startup
4. **Profile-based exclusion**: `@ConditionalOnProperty` to skip non-essential beans in specific environments
5. **Virtual Threads** (Java 21): `spring.threads.virtual.enabled=true` — doesn't help startup time directly but improves throughput once running
6. **Reduce component scanning**: explicit `@Import` instead of broad `@ComponentScan`
