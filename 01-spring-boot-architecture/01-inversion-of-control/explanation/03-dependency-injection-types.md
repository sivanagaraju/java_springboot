# 03 - Types of Dependency Injection (DI)

> **Python Bridge:** In Python, passing dependencies to `__init__` is standard practice. Spring's Constructor Injection enforces this exact pattern mechanically, ensuring objects cannot exist in an incomplete state.

Dependency Injection is not a framework; it is a design pattern realizing IoC. Spring Boot primarily supports three methods to resolve and inject dependencies. Let's look at why **Constructor Injection** is the only one you should use.

---

## 1. Field Injection (The Anti-Pattern)

**Mechanics:** Injecting the dependency directly onto a private field using `@Autowired`.

```java
@Service
public class OrderService {

    @Autowired
    private PaymentGateway gateway; // ❌ BAD! Field Injection

    public void process() {
        gateway.charge();
    }
}
```

### Why is it terrible?
1. **Hidden Dependencies:** The class doesn't explicitly declare its requirements in its API (constructor).
2. **Immutability is Impossible:** The `gateway` field cannot be marked `final`, meaning it can be modified or nullified at runtime, violating thread safety.
3. **Testing is a Nightmare:** You cannot instantiate this class natively in a Unit Test (`new OrderService()`) because the field is private. The `gateway` will be `null` unless you spin up the entire Spring context or rely on heavy reflection (like Mockito's `@InjectMocks`).
4. **Hides Circular Dependencies:** Field injection happily allows broken architecture (Service A depends on B, B depends on A), which will eventually cause `StackOverflowError` at runtime.

> **Architect Rule:** Never use Field Injection. It has been effectively deprecated by the Spring engineering team.

---

## 2. Setter Injection (The Rare Case)

**Mechanics:** The dependency is injected via a public setter method.

```java
@Service
public class OrderService {
    private PaymentGateway gateway;

    @Autowired
    public void setPaymentGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

### Why use it?
Setter injection should **only** be used if the dependency is purely optional. If `OrderService` can function safely without a `PaymentGateway`, setter injection allows the dependency to be swapped or omitted. However, 99% of dependencies in enterprise apps are mandatory, making setter injection very rare.

---

## 3. Constructor Injection (The Enterprise Standard)

**Mechanics:** The dependency is injected precisely during the physical instantiation of the class via its Constructor.

```java
@Service
public class OrderService {
    
    // ✅ GOOD: The field is final. It is completely Immutable.
    private final PaymentGateway gateway;

    // ✅ GOOD: The class loudly declares its dependencies publicly.
    // Note: If a class has exactly one constructor, @Autowired is optional in modern Spring.
    @Autowired 
    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public void process() {
        gateway.charge();
    }
}
```

### Why is it perfect?
1. **Guaranteed Initialization:** `OrderService` physically cannot be instantiated unless `PaymentGateway` is provided. This completely eliminates `NullPointerException` risks.
2. **Immutability:** You can natively declare the field `final`, cleanly securing the dependency thread-safely.
3. **Painless Unit Testing:** You can write lightning-fast unit tests using raw Java: `new OrderService(new MockGateway())` with zero Spring Boot overhead.

```mermaid
flowchart TD
    subgraph Container[Spring IoC Container]
        A[Create MockGateway] -->|Pass to Constructor| B[Create OrderService]
        C[Create RealGateway] -->|Pass to Constructor| D[Create OrderService]
    end
    
    subgraph Testing[Unit Test (No Spring)]
        E[new MockGateway()] -->|Pass manually| F[new OrderService(mock)]
    end
    
    style Testing fill:#e1f5fe,stroke:#01579b
    style Container fill:#e8f5e9,stroke:#2e7d32
```

---

## Interview Questions

### Conceptual
**Q: Why does the Spring team officially recommend Constructor Injection over Field Injection?**
> **A:** Constructor injection perfectly adheres to Object-Oriented principles. It enforces immutability (fields can be `final`), prevents incomplete object instantiation (no `NullPointerException`), clearly advertises mandatory dependencies through the class API, and makes unit testing trivial without requiring reflection or the Spring Context.

**Q: Can you use `@Autowired` on a `final` field using Field Injection?**
> **A:** No. According to Java language rules, a `final` field must be initialized at the time of declaration or exactly once in the constructor. Field injection happens *after* the object is constructed using reflection, so the field cannot be `final`.

### Scenario/Debug
**Q: You have Service A which requires Service B in its constructor, and Service B which requires Service A in its constructor. What happens on startup?**
> **A:** Spring Boot will crash immediately on startup with a `BeanCurrentlyInCreationException`. This is a strict **Circular Dependency**. Constructor injection exposes this architectural flaw at startup (Fail Fast), which is a massive benefit. The correct fix is to redesign the architecture, likely by extracting the shared logic into a third "Service C" that both A and B can depend on.
