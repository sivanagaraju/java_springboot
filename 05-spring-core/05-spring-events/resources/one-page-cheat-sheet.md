# 05-Spring-Events — One-Page Cheat Sheet

## Event System Quick Reference

| Concept | What It Does | Python Equivalent |
|---|---|---|
| `ApplicationEvent` | Base class for custom events | Django `Signal` |
| `ApplicationEventPublisher` | Publishes events to all listeners | `signal.send()` |
| `@EventListener` | Method-level listener annotation | `@receiver(signal)` |
| `@TransactionalEventListener` | Only fires after transaction commits | No equivalent |
| `@Async` + `@EventListener` | Non-blocking async listener | `asyncio.create_task()` |
| `ApplicationReadyEvent` | Fired when app is fully started | `@app.on_event("startup")` |

## Built-in Spring Boot Events (Timeline)

```
ApplicationStartingEvent          ← before ANYTHING
ApplicationEnvironmentPreparedEvent ← Environment ready
ApplicationContextInitializedEvent  ← Context created, beans NOT loaded
ApplicationPreparedEvent           ← Beans loaded, NOT started
ContextRefreshedEvent              ← Context refreshed, beans initialized
ApplicationStartedEvent            ← Started but CommandLineRunners not run
ApplicationReadyEvent              ← FULLY READY ✅ — use this for startup tasks
ApplicationFailedEvent             ← Startup failed
```

## Custom Event Pattern (ASCII)

```
OrderService.placeOrder()
    │
    ├── eventPublisher.publishEvent(new OrderPlacedEvent(order))
    │
    ▼ Spring dispatches to ALL @EventListener methods
    │
    ├── InventoryListener.onOrderPlaced(event)  → deducts stock
    ├── EmailListener.onOrderPlaced(event)       → sends confirmation
    └── AuditListener.onOrderPlaced(event)       → logs to audit trail

LOOSE COUPLING: OrderService doesn't know about listeners!
```

## Event Types and Usage

```java
// OPTION 1: Record (Java 16+ — recommended)
public record OrderPlacedEvent(Long orderId, String customer, double total) {}

// OPTION 2: Extending ApplicationEvent (legacy — not needed since Spring 4.2)
public class OrderPlacedEvent extends ApplicationEvent { ... }

// OPTION 3: Generic event with ResolvableTypeProvider (advanced)
public class EntityCreatedEvent<T> implements ResolvableTypeProvider { ... }
```

## Sync vs Async Events

| Feature | Sync (default) | Async (`@Async`) |
|---|---|---|
| Thread | Same thread as publisher | Separate thread pool |
| Exception handling | Propagates to publisher | Lost unless configured |
| Transaction | Same transaction | No transaction context |
| Order guarantee | `@Order(1)` works | No ordering guarantee |
| Use case | Business validation | Notifications, logging |

## @TransactionalEventListener Phases

| Phase | When It Fires | Use Case |
|---|---|---|
| `AFTER_COMMIT` (default) | Transaction committed successfully | Send email, publish to queue |
| `AFTER_ROLLBACK` | Transaction rolled back | Compensation, alerting |
| `AFTER_COMPLETION` | Always after commit or rollback | Cleanup, logging |
| `BEFORE_COMMIT` | Just before commit | Final validation |

## Python → Java Quick Map

| Python / Django | Spring Events |
|---|---|
| `post_save = Signal()` | `public record OrderCreatedEvent(...)` |
| `post_save.send(sender=Order, instance=order)` | `publisher.publishEvent(new OrderCreatedEvent(order))` |
| `@receiver(post_save, sender=Order)` | `@EventListener` on method with event param type |
| `asyncio.create_task(notify(order))` | `@Async @EventListener` |
| No equivalent | `@TransactionalEventListener(phase = AFTER_COMMIT)` |

## 5 Traps to Avoid

1. **Async listeners silently swallow exceptions** → configure `AsyncUncaughtExceptionHandler`
2. **`@TransactionalEventListener` with no transaction** → listener NEVER fires (use `fallbackExecution = true`)
3. **Publishing events in `@PostConstruct`** → some listeners may not be registered yet
4. **Circular event chains** → Event A triggers Event B which triggers Event A → StackOverflowError
5. **Heavy processing in sync listeners** → blocks the publisher thread; use `@Async` for I/O-intensive work
