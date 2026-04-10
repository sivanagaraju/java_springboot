# 02-Dependency-Injection — One-Page Cheat Sheet

## Injection Types Quick Reference

| Type | Syntax | Testable? | Immutable? | When to Use |
|---|---|---|---|---|
| **Constructor** ✅ | Single constructor, no `@Autowired` needed | ✅ `new Service(mock)` | ✅ `final` fields | **Always** — mandatory deps |
| **Setter** ⚠️ | `@Autowired void setX(X x)` | ⚠️ Needs Spring or reflection | ❌ Mutable | Optional dependencies only |
| **Field** ❌ | `@Autowired private X x;` | ❌ Requires reflection | ❌ No `final` | **Never** — anti-pattern |

## Disambiguation Annotations

| Annotation | Where to Use | What It Does | Example |
|---|---|---|---|
| `@Primary` | On the bean class/method | Default when multiple candidates exist | `@Primary @Service class StripePayment` |
| `@Qualifier("name")` | On injection point | Selects specific bean by name | `@Qualifier("paypal") PaymentService svc` |
| `@Profile("dev")` | On bean class/method | Active only in specific profile | `@Profile("test") @Service class MockPayment` |
| `List<T>` injection | Constructor param `List<T>` | Injects ALL implementations of `T` | `List<PaymentService> allProviders` |

## Resolution Priority (ASCII)

```
Spring needs to inject PaymentService. Multiple beans found:

1. @Qualifier("stripe") specified?  → Use that exact bean
2. No @Qualifier — is one @Primary? → Use the @Primary bean
3. No @Primary — match by name?     → Field/param name matches bean name?
4. No match                          → NoUniqueBeanDefinitionException 💥

RULE: Prefer @Qualifier for explicit control. @Primary for "default" choice.
```

## Constructor Injection Patterns

```java
// PATTERN 1: Single constructor (Spring auto-detects, no @Autowired needed)
@Service
public class OrderService {
    private final PaymentService payment;
    private final InventoryService inventory;
    
    public OrderService(PaymentService payment, InventoryService inventory) {
        this.payment = payment;
        this.inventory = inventory;
    }
}

// PATTERN 2: Lombok shortcut (generates constructor for final fields)
@Service
@RequiredArgsConstructor
public class OrderService {
    private final PaymentService payment;
    private final InventoryService inventory;
}

// PATTERN 3: Optional dependency (nullable)
public OrderService(PaymentService payment,
                    @Nullable AuditLogger audit) { ... }
```

## Python → Java Quick Map

| Python / FastAPI | Spring DI |
|---|---|
| `def endpoint(db: Session = Depends(get_db))` | Constructor injection (auto) |
| `db = Depends(get_db)` per request | `@Autowired` singleton, shared across requests |
| `def get_service(type="stripe")` | `@Qualifier("stripe")` |
| `if os.getenv("ENV") == "test": return MockDB()` | `@Profile("test")` |
| No built-in DI container | IoC container manages everything |

## 5 Traps to Avoid

1. **Field injection in production code** → untestable without Spring context, hidden dependencies
2. **Circular dependencies** → `A → B → A` — constructor injection catches this at startup (field injection hides it)
3. **Injecting concrete classes instead of interfaces** → breaks AOP proxies and testability
4. **Using `@Autowired` on single constructor** → unnecessary since Spring 4.3, just noise
5. **Multiple implementations without `@Primary` or `@Qualifier`** → `NoUniqueBeanDefinitionException` at startup
