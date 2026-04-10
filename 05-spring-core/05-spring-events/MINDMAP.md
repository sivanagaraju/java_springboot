# Spring Events

## Built-in Events
- ContextRefreshedEvent — context initialized/refreshed
- ContextStartedEvent — context started
- ContextStoppedEvent — context stopped
- ContextClosedEvent — context closed
- ApplicationReadyEvent — app fully started
- ApplicationFailedEvent — startup failed

## Custom Events
- Legacy approach
  - Extend ApplicationEvent
  - ApplicationEventPublisher.publishEvent()
- Modern approach (Spring 4.2+)
  - Any POJO as event
  - @EventListener on handler method
  - No ApplicationEvent inheritance needed
- Event with data
  - Record as event (Java 21)
  - Immutable by default

## Async Events
- @Async + @EventListener
  - Non-blocking — publisher doesn't wait
  - Separate thread pool
  - Error handling — AsyncUncaughtExceptionHandler
- @EnableAsync
  - Required on @Configuration
  - Configurable thread pool
  - @Async("customExecutor") for named pools
- Ordering
  - @Order on @EventListener
  - Lower value = higher priority

## Event-Driven Architecture
- Benefits
  - Loose coupling — publisher doesn't know listeners
  - Open/Closed — add listeners without changing publisher
  - Testable — verify events published
- When NOT to use
  - Simple CRUD — overkill
  - Synchronous required — events are fire-and-forget
  - Transaction boundary — events may execute in different tx
- @TransactionalEventListener
  - AFTER_COMMIT — execute after tx commits
  - AFTER_ROLLBACK — execute after tx fails
  - BEFORE_COMMIT — execute before commit
  - AFTER_COMPLETION — execute after any outcome
