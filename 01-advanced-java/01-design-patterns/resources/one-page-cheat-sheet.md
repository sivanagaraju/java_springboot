# Design Patterns — One-Page Cheat Sheet

## Pattern Quick Reference

| Pattern | Category | Intent | Spring Example |
|---------|----------|--------|----------------|
| **Singleton** | Creational | One instance per context | `@Component` (default scope) |
| **Factory Method** | Creational | Subclass decides which object to create | `@Bean` method in `@Configuration` |
| **Builder** | Creational | Step-by-step construction with validation | `ResponseEntity.ok().header().body()` |
| **Observer** | Behavioral | Notify observers of state changes | `@EventListener`, `ApplicationEventPublisher` |
| **Strategy** | Behavioral | Swap algorithm at runtime | `AuthenticationProvider`, `Comparator` |
| **Template Method** | Behavioral | Fixed algorithm skeleton, variable steps | `JdbcTemplate`, `RestTemplate` |
| **Decorator** | Structural | Wrap object to add behavior | Java I/O streams, `HandlerInterceptor` |
| **Adapter** | Structural | Bridge incompatible interfaces | `HandlerAdapter`, payment gateway wrappers |

---

## Fast Rules

```
Singleton:
  ✅ Use @Component — Spring manages it
  ✅ Enum for non-Spring singletons
  ❌ Never lazy static without volatile

Factory Method:
  ✅ When concrete class unknown at compile time
  ✅ New product types must not change existing code
  ❌ Not just a static create() method (that's Simple Factory)

Builder:
  ✅ > 3 parameters, especially optional ones
  ✅ Immutable objects
  ✅ Validate in build(), not setters
  ❌ @Builder + JPA @Entity = compatibility issues (use @Builder(toBuilder=true))

Observer:
  ✅ Decouple side effects from core logic
  ✅ @Async for non-critical listeners (email, analytics)
  ❌ Don't use for critical, transactional logic in listeners

Strategy:
  ✅ @FunctionalInterface → use lambdas
  ✅ Inject strategy via constructor (Spring DI)
  ❌ Don't use when algorithm never changes

Template Method:
  ✅ make() final — protect the skeleton!
  ✅ Hook methods for optional extension points
  ❌ Prefer Strategy if algorithm must swap at runtime

Decorator:
  ✅ Add cross-cutting concerns (logging, caching, retry)
  ✅ Compose independently
  ❌ Don't confuse with Proxy (Decorator adds; Proxy controls access)

Adapter:
  ✅ Third-party SDK with different interface
  ✅ Object Adapter (composition) > Class Adapter (inheritance)
  ❌ Not for simplifying an API (that's Facade)
```

---

## Python Bridge

| Java Pattern | Python Equivalent |
|---|---|
| `@Component` Singleton | Module-level object |
| `@FunctionalInterface` Strategy | Any callable / lambda |
| `@Builder` | `@dataclass` with keyword args |
| `@EventListener` Observer | Django signals / FastAPI BackgroundTasks |
| Java I/O Decorator | Python `@decorator` syntax |
| Adapter class | Wrapper class / duck typing often eliminates need |

---

## Common Traps

```
TRAP 1: Mutable Singleton State
  @Component class with non-final fields → race condition!
  Fix: make all fields final, use method parameters for state

TRAP 2: Forgetting volatile in DCL Singleton
  JVM instruction reordering → partially constructed object visible
  Fix: always use enum singleton or eager initialization

TRAP 3: Synchronous @EventListener for I/O
  Email listener on same thread → adds latency to order placement
  Fix: @Async on I/O-bound listeners

TRAP 4: Observer ordering dependency
  Listener A must run before Listener B → wrong pattern
  Fix: use @Order(1) and @Order(2) or make one listener invoke the other

TRAP 5: Builder with JPA Entity
  @Builder generates all-args constructor, breaks JPA requirement
  Fix: @Builder + @NoArgsConstructor + @AllArgsConstructor (Lombok combo)
```

---

## Key Interview Questions

**Q: Singleton — GoF vs Spring?**
GoF: one instance per ClassLoader. Spring: one instance per ApplicationContext.

**Q: Strategy vs Template Method?**
Strategy: composition (has-a), swap at runtime. Template: inheritance (is-a), fixed at compile time.

**Q: Decorator vs Proxy?**
Decorator adds behavior. Proxy controls access (lazy, security, remote). Structurally identical; intent differs.

**Q: Factory Method vs Abstract Factory?**
Factory Method: one product type, subclass decides concrete class. Abstract Factory: family of related products.
