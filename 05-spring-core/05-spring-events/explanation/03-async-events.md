# 03 — Async Events

## The Problem With Synchronous Events

By default, Spring events are **synchronous** — the publisher waits for ALL listeners to complete:

```
Publisher → Listener1 (200ms) → Listener2 (500ms) → Publisher continues
Total: 700ms added to publisher's execution time!
```

## The Solution: @Async Events

```java
// 1. Enable async processing
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("async-event-");
        return executor;
    }
}

// 2. Mark listener as async
@Component
public class EmailListener {
    @Async
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        // Runs in separate thread — publisher doesn't wait!
        sendEmail(event.customer(), "Order confirmed!");
    }
}
```

## Sync vs Async Comparison

```mermaid
sequenceDiagram
    participant P as Publisher
    participant L1 as Listener 1
    participant L2 as Listener 2

    rect rgb(255, 235, 238)
        Note over P,L2: Synchronous (default)
        P->>L1: handleEvent (blocks)
        L1-->>P: done (200ms)
        P->>L2: handleEvent (blocks)
        L2-->>P: done (500ms)
        Note over P: Total wait: 700ms
    end

    rect rgb(232, 245, 233)
        Note over P,L2: Async (@Async)
        P->>L1: handleEvent (fire & forget)
        P->>L2: handleEvent (fire & forget)
        Note over P: Total wait: ~0ms
        L1-->>L1: done (200ms, own thread)
        L2-->>L2: done (500ms, own thread)
    end
```

## Error Handling in Async Events

```java
// Async errors don't propagate to the publisher!
// Configure an error handler:
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, params) -> {
            log.error("Async event error in {}: {}", method.getName(), throwable.getMessage());
            // Alert, retry, store in dead letter queue, etc.
        };
    }
}
```

## Python Comparison

```python
# Python asyncio equivalent
import asyncio

async def on_order_created(event):
    await send_email(event.customer)

# Fire and forget
asyncio.create_task(on_order_created(event))  # ~ @Async @EventListener
```

## Interview Questions

### Conceptual

**Q1: What happens if an @Async event listener throws an exception?**
> The exception is NOT propagated to the publisher (it's in a different thread). You must configure an `AsyncUncaughtExceptionHandler` to handle it — otherwise the error is silently swallowed.

### Scenario/Debug

**Q2: Your @Async listener runs in the same thread as the publisher. Why?**
> You forgot `@EnableAsync` on a @Configuration class. Without it, @Async annotations are ignored and Spring executes synchronously.

### Quick Fire

**Q3: Can you control the thread pool for @Async events?**
> Yes — define a `TaskExecutor` bean, or use `@Async("myExecutor")` to target a specific named executor.
