# Progressive Quiz Drill — Design Patterns

Work through each round in order. Don't skip ahead.

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** You're reviewing a PR where a developer created a `LoggingService` class with a public constructor and a static `getInstance()` method using `if (instance == null) { instance = new LoggingService(); }`. No `synchronized` or `volatile`. What's the bug, and what are two ways to fix it?

**Q2.** A `@Configuration` class has a `@Bean` method that returns a `new HikariDataSource()`. A teammate says "this is just Factory Method." A Staff Engineer disagrees. Who's right, and what's the precise GoF term for what `@Bean` actually represents?

**Q3.** You see: `new DataInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream("data.gz"))))`. Name the GoF pattern, identify the component interface, and explain why this is better than inheritance.

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** Your team is building a payment service. You need to support Stripe, PayPal, and Square, each with completely different SDKs. In 6 months you'll add Braintree. Design the class structure using the correct pattern(s). Name each class and its GoF role.

**Q5.** A Spring `@Service` has a method that: saves an order to DB, sends a confirmation email, updates inventory, and fires an analytics event — all in one method. Which pattern refactors this? Write the core event class and one listener using Spring's implementation. Why does `@Async` matter here?

**Q6.** Your shipping calculator has a `calculate(type, weight)` method with 5 `if/else` branches. Junior asks: "Should I use Strategy or Factory Method?" Explain the difference and which pattern is more appropriate here, and why.

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code review:
```java
public class Config {
    private static Config instance;
    public static synchronized Config getInstance() {
        if (instance == null) instance = new Config();
        return instance;
    }
}
```
This is thread-safe. But a Staff Engineer says there are two remaining vulnerabilities. What are they?

**Q8.** Code review:
```java
abstract class ReportGenerator {
    public void generate() {
        fetchData();
        process();
        output();
    }
    abstract void fetchData();
    abstract void process();
    void output() { System.out.println("Done"); }
}
```
A subclass overrides `generate()` entirely. What principle is violated, and what's the fix?

**Q9.** A Builder's `build()` method doesn't validate required fields. Two weeks later, production crashes with a `NullPointerException` inside `processOrder(user)` because `user.email` is null. Where is the real bug? How should Builder validate?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're designing a notification system for an e-commerce platform. Requirements: (1) Support Email, SMS, Push, Slack today; (2) Add new channels in < 1 day without touching existing code; (3) Some notifications need retries; (4) Audit log of all notifications sent. Apply at least 3 design patterns. Justify each choice, identify trade-offs, and explain how this maps to Spring's event system.

---

## Answer Key

**A1.** Race condition — two threads can both see `instance == null` and create two instances. Fix 1: `synchronized` on the method (but slow). Fix 2: DCL with `volatile`: `private static volatile LoggingService instance` + double-checked locking. Best fix: use eager initialization (`private static final INSTANCE = new LoggingService()`) or enum singleton.

**A2.** The teammate is partially right — `@Bean` creates objects. But it's not GoF Factory Method (which requires an interface hierarchy of factories). The `@Bean` method IS a factory method (lowercase), and `ApplicationContext` is the **Abstract Factory** / **Dependency Injection Container**. In pure GoF terms: `@Bean` = Factory Method; `@Configuration` class = ConcreteCreator.

**A3.** Decorator pattern. Component interface: `InputStream`. `FileInputStream` is the ConcreteComponent. `FilterInputStream` is the abstract Decorator. `BufferedInputStream`, `GZIPInputStream`, `DataInputStream` are concrete Decorators. Better than inheritance because combinations are combinatorial — you'd need `BufferedGZIPDataFileInputStream` as a class. Composition composes at runtime.

**A4.** Adapter for each SDK: `StripeAdapter implements PaymentProcessor`, `PayPalAdapter implements PaymentProcessor`. Factory Method to create them: `PaymentProcessorFactory` with concrete factories per provider. Adding Braintree = new `BraintreeAdapter` + `BraintreeFactory`. No existing code changes.

**A5.** Observer / Spring Events. `OrderPlacedEvent extends ApplicationEvent`. `EmailService`, `InventoryService`, `AnalyticsService` each have `@EventListener`. Inventory/Analytics should be `@Async` — they're non-critical side effects that shouldn't slow the order placement transaction. If email synchronous listener fails, it rolls back the order — probably wrong; consider `@TransactionalEventListener(phase=AFTER_COMMIT)`.

**A6.** Strategy. Factory Method creates *objects*; Strategy swaps *algorithms*. The shipping calculation IS the algorithm. Inject a `ShippingStrategy` into the calculator. With Java 8+, each strategy can be a lambda. Factory Method would be used to *create* the right `ShippingStrategy` instance based on the shipping type.

**A7.** (1) Reflection attack: `Constructor.newInstance()` can bypass the private constructor. Fix: add `if (instance != null) throw new RuntimeException()` in constructor. (2) Serialization attack: if `Config implements Serializable`, deserialization creates a new instance. Fix: implement `readResolve()` returning `instance`. Best solution: use enum singleton which is immune to both.

**A8.** Hollywood Principle / Template Method violated. Subclasses should override steps, not the template method. Fix: make `generate()` final. The template method MUST be final to enforce the algorithm skeleton.

**A9.** The real bug is in `build()` — it should validate: `if (email == null || email.isBlank()) throw new IllegalStateException("Email required")`. NPE inside business logic is a symptom; missing validation at object construction is the root cause. Fail fast at the construction boundary.

**A10.** Patterns: (1) **Factory Method** — `NotificationFactory.create(type)` for extensibility; (2) **Observer/Spring Events** — decouple senders from channels; (3) **Decorator** — wrap base notification service with `RetryDecorator` and `AuditDecorator`; (4) **Strategy** — each channel (`EmailStrategy`, `SlackStrategy`) is a strategy. Spring mapping: Factory = `@Bean` per channel type; Observer = `@EventListener` per channel; Decorator = `@Around` AOP for retry/audit. Trade-off: too many abstractions for 4 channels — balance complexity with value.
