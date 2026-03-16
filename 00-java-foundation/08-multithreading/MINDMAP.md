# Multithreading & Concurrency — Concept Map

```markmap
# Multithreading

## Thread Basics
- **Thread Lifecycle**
  - NEW → RUNNABLE → RUNNING
  - BLOCKED / WAITING / TIMED_WAITING
  - TERMINATED
- **Creation**
  - Extend `Thread` (avoid)
  - Implement `Runnable` (preferred)
  - Lambda `() -> { ... }` (modern)
- **Key Methods**
  - `start()` → begin execution
  - `join()` → wait for completion
  - `sleep()` → pause current thread
  - `interrupt()` → signal cancellation
- **Daemon vs User threads**

## Synchronization
- **Race Condition**
  - Two threads modify same variable
  - Result depends on timing (non-deterministic)
- **synchronized**
  - Method level: `synchronized void add()`
  - Block level: `synchronized(lock) { ... }`
  - Intrinsic lock (monitor) per object
- **volatile**
  - Visibility guarantee only
  - No atomicity (read-modify-write still unsafe)
- **Atomic Classes**
  - `AtomicInteger`, `AtomicLong`
  - Lock-free thread safety
- **Deadlock**
  - Thread A holds lock1, waits for lock2
  - Thread B holds lock2, waits for lock1
  - Both wait forever!

## Executors (Modern API)
- **Thread Pools**
  - `Executors.newFixedThreadPool(n)`
  - `Executors.newCachedThreadPool()`
  - `Executors.newSingleThreadExecutor()`
  - `Executors.newVirtualThreadPerTaskExecutor()` (Java 21+)
- **Future<T>**
  - Result of async computation
  - `get()` blocks until done
- **CompletableFuture<T>**
  - Non-blocking composition
  - `thenApply`, `thenCompose`, `thenCombine`
  - `exceptionally()` for error handling
  - `allOf`, `anyOf` for multiple futures

## Spring Connection
- HTTP requests → thread per request
- `@Async` annotation
- Thread-safe singleton beans
- Virtual threads in Spring Boot 3.2+

## Interview Hot Spots
- Race condition example
- synchronized vs volatile vs Atomic
- What causes deadlock?
- Thread pool sizing strategies
- Why Spring beans must be thread-safe
```
