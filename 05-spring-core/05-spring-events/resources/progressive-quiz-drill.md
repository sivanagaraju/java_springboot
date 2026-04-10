# 05-Spring-Events — Progressive Quiz Drill

## Round 1 — Core Recall (8 questions)
Answer from memory, then check the key.

**Q1:** What are the 3 components of Spring's event system?
**Q2:** Do you need to extend `ApplicationEvent` for custom events in Spring 4.2+?
**Q3:** What is the default behavior of `@EventListener` — sync or async?
**Q4:** Name 3 built-in Spring Boot lifecycle events in startup order.
**Q5:** What does `@TransactionalEventListener(phase = AFTER_COMMIT)` guarantee?
**Q6:** How do you make an event listener run on a separate thread?
**Q7:** Can one `@EventListener` method listen to multiple event types?
**Q8:** What happens if an async event listener throws an exception?

### Answer Key — Round 1
A1: (1) Event object (POJO or record), (2) Publisher (`ApplicationEventPublisher`), (3) Listener (`@EventListener` method).
A2: No! Since Spring 4.2, any POJO can be an event. Records work perfectly: `public record OrderPlaced(Long id) {}`. Extending `ApplicationEvent` is legacy.
A3: **Synchronous** — the listener runs on the SAME thread as the publisher. The publisher blocks until ALL sync listeners complete.
A4: `ApplicationStartingEvent` → `ApplicationEnvironmentPreparedEvent` → `ApplicationPreparedEvent` → `ContextRefreshedEvent` → `ApplicationStartedEvent` → `ApplicationReadyEvent`
A5: The listener ONLY fires after the surrounding database transaction commits successfully. If the transaction rolls back, the listener is SKIPPED. Perfect for "send email after order is saved".
A6: Add `@Async` alongside `@EventListener`. Also requires `@EnableAsync` on a `@Configuration` class and a `TaskExecutor` bean.
A7: Yes — use `@EventListener({OrderCreatedEvent.class, OrderUpdatedEvent.class})` or use a common base type/interface.
A8: By default, the exception is logged but swallowed — the publisher never knows. Fix: implement `AsyncUncaughtExceptionHandler` and register it via `AsyncConfigurer`.

---

## Round 2 — Apply & Compare (3 questions)

**Q1:** Translate this Django signals pattern to Spring Events:
```python
from django.db.models.signals import post_save
from django.dispatch import receiver

@receiver(post_save, sender=Order)
def send_confirmation_email(sender, instance, created, **kwargs):
    if created:
        send_email(instance.customer_email, "Order confirmed!")

@receiver(post_save, sender=Order)
def update_inventory(sender, instance, created, **kwargs):
    if created:
        for item in instance.items:
            deduct_stock(item.product_id, item.quantity)
```

**Q2:** In Python `asyncio`, you fire and forget with `asyncio.create_task()`. How do you achieve the same in Spring?

**Q3:** Django signals have a `sender` parameter to filter which model triggers the signal. How does Spring Events handle this filtering?

### Answer Key — Round 2
A1:
```java
// Event
public record OrderCreatedEvent(Long orderId, String customerEmail, List<OrderItem> items) {}

// Publisher (in OrderService)
@Service
public class OrderService {
    private final ApplicationEventPublisher publisher;
    
    public Order placeOrder(OrderRequest req) {
        Order order = orderRepo.save(req.toOrder());
        publisher.publishEvent(new OrderCreatedEvent(order.getId(), order.getEmail(), order.getItems()));
        return order;
    }
}

// Listeners (separate classes — loose coupling!)
@Component
public class EmailListener {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        sendEmail(event.customerEmail(), "Order confirmed!");
    }
}

@Component
public class InventoryListener {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        event.items().forEach(item -> deductStock(item.productId(), item.quantity()));
    }
}
```

A2: `@Async @EventListener` — Spring dispatches to a thread pool. The publisher continues immediately:
```java
@Async
@EventListener
public void sendNotification(OrderCreatedEvent event) {
    // runs on a separate thread — publisher doesn't wait
    emailService.send(event.customerEmail(), "Order placed!");
}
```

A3: Spring uses **type-based filtering**. The `@EventListener` method parameter type determines which events it receives. For conditional filtering:
```java
@EventListener(condition = "#event.total > 1000")
public void onHighValueOrder(OrderCreatedEvent event) { }
```

---

## Round 3 — Debug the Bug (3 scenarios)

**Scenario 1:** You add `@TransactionalEventListener` to send an email after an order is saved. But the listener NEVER fires, even though the order saves successfully. There are no errors. What's wrong?

**Scenario 2:** Your async event listener works in dev but in production, events are processed very slowly. Looking at thread dumps, all async listener threads are blocked waiting for database connections. Why?

**Scenario 3:** You publish an event inside `@PostConstruct` of a bean. Only 2 of 5 listeners fire. The other 3 are never called. Why?

### Answer Key — Round 3
S1: `@TransactionalEventListener` requires an ACTIVE transaction at the time of publishing. If `OrderService.placeOrder()` is NOT annotated with `@Transactional`, there's no transaction context — the listener is silently skipped. Fix: either (a) add `@Transactional` to `placeOrder()`, or (b) use `fallbackExecution = true` on the listener to fire even without a transaction.

S2: Async listeners run on a separate thread pool — but they use the SAME database connection pool as the main threads. If the pool is 10 connections and 10 async listeners each grab a connection, the main threads starve. Fix: (1) increase connection pool size, (2) limit the async thread pool size, (3) use a separate DataSource for async operations.

S3: When `@PostConstruct` runs, not all beans may be fully initialized yet. Some `@EventListener` methods on beans that haven't been created yet won't receive the event. Fix: publish the event from an `ApplicationReadyEvent` listener instead — all beans are guaranteed to be ready by then.

---

## Round 4 — Staff-Level Scenarios (2 questions)

**Q1 — Event-Driven Order Pipeline:**
Design an event-driven order processing system with these requirements: (1) Order placed → payment processed → inventory updated → email sent, (2) payment failure should NOT affect other orders, (3) email must only send after payment commits. Define the events, listeners, and explain sync vs async choices.

**Q2 — Event Store Pattern:**
Your team wants to implement an audit trail that records every domain event for compliance. Design a Spring-based event store that: (1) persists every event to a database table, (2) doesn't slow down the main business flow, (3) can replay events for debugging.

### Answer Key — Round 4

A1:
```
Events: OrderPlacedEvent → PaymentProcessedEvent → InventoryUpdatedEvent
                         → PaymentFailedEvent

Flow:
  OrderService.placeOrder()
    → publishes OrderPlacedEvent [sync — in same tx]
    
  PaymentListener: @TransactionalEventListener(phase = AFTER_COMMIT)
    → processes payment
    → publishes PaymentProcessedEvent or PaymentFailedEvent
    
  InventoryListener: @EventListener [sync — needs strong consistency]
    → deducts stock on PaymentProcessedEvent
    
  EmailListener: @Async @TransactionalEventListener(phase = AFTER_COMMIT)
    → sends confirmation email on PaymentProcessedEvent
    → sends failure email on PaymentFailedEvent
```
Key decisions: Payment listener is `@TransactionalEventListener` (only process after order is committed). Inventory is sync (stock consistency). Email is async + transactional (don't block, don't email before commit).

A2:
```java
@Component
public class EventStoreListener {
    private final EventStoreRepository repo;
    
    @Async  // don't slow down business flow
    @EventListener
    public void persistEvent(Object event) {
        repo.save(new StoredEvent(
            UUID.randomUUID(),
            event.getClass().getSimpleName(),
            objectMapper.writeValueAsString(event),
            Instant.now()
        ));
    }
}

@Entity
public class StoredEvent {
    @Id private UUID id;
    private String eventType;
    @Column(columnDefinition = "jsonb") private String payload;
    private Instant occurredAt;
}
```
Replay: query by eventType and time range, deserialize payload, re-publish. This is a simplified Event Sourcing pattern without full CQRS.
