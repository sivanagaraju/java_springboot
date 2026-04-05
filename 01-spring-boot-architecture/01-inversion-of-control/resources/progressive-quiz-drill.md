# IoC & Dependency Injection — Progressive Quiz Drill

```mermaid
flowchart LR
    A[Core Recall] --> B[Apply and Compare]
    B --> C[Debug the Bug]
    C --> D[Staff-Level Scenario]
```

## Round 1 — Core Recall

**Q1.** What does "Inversion of Control" mean and what problem does it solve over `new`?

**Q2.** Why is constructor injection preferred over field injection (`@Autowired` on a field)?

**Q3.** What is the default bean scope in Spring and what does it imply about shared state?

**Q4.** Why must the field be `final` when using constructor injection?

**Q5.** What is the difference between `@Component`, `@Service`, and `@Repository`?

---

## Round 2 — Apply and Compare

**Q6.** You need to swap a `PaymentGateway` implementation from Stripe to PayPal in tests. Which Spring mechanism lets you do this without changing the service class?

**Q7.** A `UserService` has two implementations of `NotificationSender`: `EmailSender` and `SmsSender`. Both are `@Component` beans. Spring fails to start with `NoUniqueBeanDefinitionException`. What are the two ways to resolve this?

**Q8.** You have a stateless `PriceCalculatorService`. Should it use `@Scope("singleton")` or `@Scope("prototype")`? Why?

**Q9.** Compare Spring's `@Autowired` constructor injection to Python FastAPI's `Depends()`. What role does each framework play?

---

## Round 3 — Debug the Bug

**Q10.** What is wrong here?

```java
@Service
public class OrderService {
    @Autowired
    private final PaymentGateway gateway; // field is final
}
```

**Q11.** Why does this `@Transactional` not work?

```java
@Service
public class InvoiceService {
    public void generateInvoice() {
        this.markAsSent(); // calls internal method
    }

    @Transactional
    public void markAsSent() { }
}
```

**Q12.** A singleton `SessionService` stores user data in an instance field `currentUser`. In load testing, users see each other's data. Identify the root cause.

---

## Round 4 — Staff-Level Scenario

**Q13.** A microservice processes financial transactions. The team uses field injection (`@Autowired`) throughout because it requires less boilerplate. You're doing a security audit. What are the three concrete risks this introduces and how would you migrate the codebase?

**Q14.** Your Spring Boot app has 500 `@Service` beans. Startup takes 90 seconds. The team wants to reduce it. Which Spring Boot 3 feature eliminates runtime reflection-based DI wiring and how does it work?

---

## Answer Key

### Round 1
**A1.** Instead of objects creating their own dependencies with `new`, an external container (Spring IoC) creates and wires them. Eliminates tight coupling and enables swapping implementations without code changes.

**A2.** Constructor injection allows `final` fields (immutability), makes dependencies explicit, and enables testing without Spring context. Field injection hides dependencies, prevents `final`, and requires a Spring container to test.

**A3.** Singleton — one instance per ApplicationContext. It means the bean MUST be stateless or all requests will share and potentially corrupt the same state.

**A4.** `final` guarantees the dependency is set exactly once at construction and cannot be accidentally overwritten later. Mutable injected fields can be re-assigned, causing subtle bugs.

**A5.** Functionally identical — all register beans for component scanning. `@Repository` adds exception translation (converts DB exceptions to Spring's DataAccessException). `@Service` is semantic documentation. `@Component` is generic.

### Round 2
**A6.** Define a `PaymentGateway` interface. Annotate the prod implementation with `@Primary` or use `@Profile("prod")` / `@Profile("test")` to control which registers. Inject the interface — Spring picks the active one.

**A7.** (1) Annotate one with `@Primary` to make it the default. (2) Use `@Qualifier("emailSender")` on the injection point to specify which to inject.

**A8.** Singleton — it holds no state, so one instance shared across all callers is safe and efficient. Prototype would create a new instance per injection point — wasteful for a stateless service.

**A9.** Both invert control: the framework resolves dependencies, not the caller. Spring `@Autowired` wires at startup from a registered bean graph. FastAPI `Depends()` resolves per-request from a function dependency graph.

### Round 3
**A10.** `@Autowired` on a field cannot inject into a `final` field because Spring sets fields via reflection after object construction — by then `final` is already set to its default (null). Fix: remove `final` OR use constructor injection.

**A11.** `this.markAsSent()` bypasses the AOP proxy. Spring wraps `@Transactional` beans in a proxy — calling a method on `this` skips the proxy and the transaction never starts. Fix: extract `markAsSent()` to a separate `@Service` bean and inject it.

**A12.** `currentUser` is mutable instance state on a Singleton bean. All requests share the same object — when one user writes to `currentUser`, every other concurrent user sees that value. Fix: pass `currentUser` as a method parameter or use `@RequestScope`.

### Round 4
**A13.** Risks: (1) Non-testability — field injection requires Spring context for unit tests, preventing fast isolated tests. (2) Hidden dependencies — callers can't see dependencies from the constructor signature. (3) `null` injection risk — a mis-configured field stays null until the first call, not at startup. Migration: add constructors accepting all injected fields, annotate, make fields `final`, remove `@Autowired` from fields.

**A14.** Spring AOT (Ahead-of-Time) compilation in Spring Boot 3. At build time, AOT processes the bean definitions, generates the application context initialization code statically, and eliminates the runtime reflection scanning. Result: faster startup and GraalVM native image support.
