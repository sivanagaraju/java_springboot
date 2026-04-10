# 03-Bean-Lifecycle — Progressive Quiz Drill

## Round 1 — Core Recall (8 questions)
Answer from memory, then check the key.

**Q1:** What are the 5 bean scopes in Spring? Which is the default?
**Q2:** In what order do `@PostConstruct`, `InitializingBean.afterPropertiesSet()`, and a custom `init-method` execute?
**Q3:** You mark a bean as `@Scope("prototype")`. Does Spring call `@PreDestroy` on it?
**Q4:** At which lifecycle phase is the AOP proxy created?
**Q5:** Why should you NOT put heavy logic in a bean's constructor?
**Q6:** What is a `BeanPostProcessor` and when does it run?
**Q7:** What is a `BeanFactoryPostProcessor` and how is it different from `BeanPostProcessor`?
**Q8:** You inject a `prototype` bean into a `singleton` bean. How many instances of the prototype does the singleton use throughout its lifetime?

### Answer Key — Round 1
A1: `singleton` (default), `prototype`, `request`, `session`, `application`. Singleton creates ONE instance per ApplicationContext.
A2: `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → custom `init-method`. Always this order.
A3: **No!** Spring does NOT manage the complete lifecycle of prototype beans. It creates them but never tracks or destroys them. `@PreDestroy` is NEVER called. You must destroy prototypes yourself.
A4: Phase 10 — `BeanPostProcessor.postProcessAfterInitialization()`. This is AFTER `@PostConstruct` and initialization. The bean you interact with is usually a PROXY, not the raw object.
A5: At constructor time, dependencies may not be fully injected yet (especially with setter injection). Use `@PostConstruct` instead — it runs AFTER all DI is done, guaranteeing all dependencies are available.
A6: A `BeanPostProcessor` is a framework hook that intercepts EVERY bean's initialization. It runs `postProcessBeforeInitialization()` before `@PostConstruct` and `postProcessAfterInitialization()` after init methods. Spring uses it internally for AOP, `@Autowired`, `@Scheduled`, etc.
A7: `BeanFactoryPostProcessor` runs BEFORE any bean is instantiated — it modifies `BeanDefinition`s (metadata). `BeanPostProcessor` runs DURING bean initialization — it modifies bean instances. BFPP examples: `PropertySourcesPlaceholderConfigurer` resolves `@Value("${}")` placeholders.
A8: Exactly ONE. The prototype is injected once when the singleton is created. There is no "refresh". This is the Prototype-in-Singleton trap. Fix: inject `ObjectFactory<T>` or `Provider<T>`.

---

## Round 2 — Apply & Compare (3 questions)

**Q1:** Translate this Python context manager pattern to Spring lifecycle hooks:
```python
class ConnectionPool:
    def __enter__(self):
        self.pool = create_pool(size=10)
        print("Pool started")
        return self
    
    def __exit__(self, *args):
        self.pool.close()
        print("Pool closed")
```

**Q2:** In Python, module-level objects are singletons by default. In Spring, what makes a bean a singleton? And what makes a bean a prototype?

**Q3:** You have a Python background where `__del__` is unreliable for cleanup. Is Spring's `@PreDestroy` more reliable? Under what conditions?

### Answer Key — Round 2
A1:
```java
@Service
public class ConnectionPool {
    private HikariDataSource pool;
    
    @PostConstruct  // equivalent to __enter__
    public void init() {
        pool = new HikariDataSource(config);
        System.out.println("Pool started");
    }
    
    @PreDestroy  // equivalent to __exit__
    public void cleanup() {
        pool.close();
        System.out.println("Pool closed");
    }
}
```
Key difference: Python context managers are request-scoped (`with` block). Spring `@PostConstruct`/`@PreDestroy` are application-scoped (startup/shutdown).

A2: `@Scope("singleton")` (or no scope annotation — it's the default). Spring creates ONE instance at startup, caches it, and returns the same instance for every injection. `@Scope("prototype")` — Spring creates a NEW instance every time `getBean()` is called or the bean is injected. After creation, Spring forgets about it.

A3: Yes, `@PreDestroy` is much more reliable than Python's `__del__`. Spring guarantees `@PreDestroy` runs on graceful shutdown (SIGTERM, `context.close()`). BUT: it does NOT run on `kill -9`, OutOfMemoryError, or prototype beans. For critical cleanup, also register a JVM shutdown hook.

---

## Round 3 — Debug the Bug (3 scenarios)

**Scenario 1:** Your `@PostConstruct` method calls another method on `this` that has `@Transactional`. The transaction never opens. Why?

**Scenario 2:** You create a `BeanPostProcessor` that logs every bean name. But your `@Value` properties are `null` inside the BPP. Why?

**Scenario 3:** Your singleton bean injected with a prototype-scoped `RequestContext` always returns the SAME user ID, even for different HTTP requests. What's wrong?

### Answer Key — Round 3
S1: At `@PostConstruct` time (Phase 7), the AOP proxy hasn't been created yet (that happens at Phase 10). So `this.method()` calls the RAW object, NOT the proxy. `@Transactional` requires the proxy to intercept the call. Fix: move the code to `ApplicationReadyEvent` listener, or extract the transactional logic to a separate bean.

S2: `BeanPostProcessor` is instantiated very early in the container lifecycle — before regular `@Value` resolution. BPPs themselves should avoid depending on other beans or `@Value`. If you need config, implement `EnvironmentAware` or `BeanFactoryAware` on the BPP itself.

S3: Prototype-in-Singleton trap! The `RequestContext` prototype was injected ONCE when the singleton was created. All requests share the same instance. Fix: inject `ObjectFactory<RequestContext>` and call `objectFactory.getObject()` on each request to get a fresh instance.

---

## Round 4 — Staff-Level Scenarios (2 questions)

**Q1 — Custom BeanPostProcessor:**
Design a `BeanPostProcessor` that measures the initialization time of every bean and logs any bean that takes >100ms to initialize. What lifecycle methods do you use, and what data structure tracks the timing?

**Q2 — Graceful Shutdown:**
Your Spring Boot app processes async tasks via `@Async`. On shutdown, some tasks are interrupted mid-execution. How do you implement graceful shutdown that waits for in-flight tasks to complete (up to 30 seconds)?

### Answer Key — Round 4

A1:
```java
@Component
public class InitTimerBPP implements BeanPostProcessor {
    private final Map<String, Long> startTimes = new ConcurrentHashMap<>();
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        startTimes.put(name, System.nanoTime());
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        long elapsed = (System.nanoTime() - startTimes.remove(name)) / 1_000_000;
        if (elapsed > 100) {
            log.warn("⚠️ Slow bean init: {} took {}ms", name, elapsed);
        }
        return bean;
    }
}
```
Uses `ConcurrentHashMap` because BPP methods may be called from different threads during parallel bean init.

A2: Configure `TaskExecutor` with graceful shutdown:
```java
@Configuration
public class AsyncConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        return executor;
    }
}
```
Also add to `application.yml`: `server.shutdown=graceful` and `spring.lifecycle.timeout-per-shutdown-phase=30s`. On SIGTERM: (1) stop accepting new requests, (2) wait for in-flight tasks up to 30s, (3) force shutdown if timeout exceeded.
