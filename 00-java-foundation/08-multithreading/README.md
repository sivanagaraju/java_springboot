# 08 - Multithreading & Concurrency

## Why This Matters

Java was built for multithreading from day one. In Spring Boot, every HTTP request runs on a thread from a pool. Understanding threads is essential for:
- Avoiding race conditions in shared state
- Understanding why Spring beans are singletons
- Writing performant async operations
- Debugging production issues like deadlocks and thread starvation

## Python -> Java Quick Map

| Python | Java | Notes |
|--------|------|-------|
| `threading.Thread(target=fn)` | `new Thread(runnable)` | Create thread |
| `thread.start()` | `thread.start()` | Begin execution |
| `threading.Lock()` | `synchronized` / `ReentrantLock` | Mutual exclusion |
| GIL (Global Interpreter Lock) | No GIL, true parallelism | Java threads run in parallel |
| `concurrent.futures.ThreadPoolExecutor` | `ExecutorService` | Thread pool |
| `asyncio` | `CompletableFuture` | Async programming |

> Critical difference: Python has the GIL, meaning only one thread runs Python code at a time. Java has true parallelism, so multiple threads execute simultaneously on multiple CPU cores. That makes Java threading more powerful and more dangerous.

## Concurrency Flow

```mermaid
flowchart LR
    A[HTTP request or batch job] --> B[ExecutorService]
    B --> C[Bounded worker threads]
    C --> D[Future or CompletableFuture result]
    D --> E[shutdown and awaitTermination]
```

## Sub-topics

| # | File | Concept |
|---|------|---------|
| 1 | `01-thread-basics.md` | Thread lifecycle, creation, Thread vs Runnable |
| 2 | `02-synchronization.md` | synchronized, volatile, race conditions |
| 3 | `03-executors-and-futures.md` | CompletableFuture composition and async chaining |
| 4 | `04-executor-service.md` | Thread pools, Future, submit, invokeAll, graceful shutdown |
| 5 | `ThreadBasicsDemo.java` | Thread creation, lifecycle, and joining |
| 6 | `SynchronizationDemo.java` | Race conditions and synchronized fixes |
| 7 | `ExecutorServiceDemo.java` | Bounded executor usage and shutdown lifecycle |
| 8 | `ExecutorDemo.java` | Thread pools and CompletableFuture |

## Support Pack

Use these subtopic-level study aids to reinforce the concepts:

- [Progressive Quiz Drill](resources/progressive-quiz-drill.md)
- [One-Page Cheat Sheet](resources/one-page-cheat-sheet.md)
- [Top Resource Guide](resources/top-resource-guide.md)

## Exercises

- `Ex01_ProducerConsumer.java` - Classic concurrent pattern
- `Ex02_ParallelFileProcessor.java` - Real-world multithreaded I/O

## Study Path

1. Read `01-thread-basics.md` -> understand thread lifecycle
2. Run `ThreadBasicsDemo.java` -> see threads in action
3. Read `02-synchronization.md` -> learn why shared state is dangerous
4. Run `SynchronizationDemo.java` -> watch race conditions happen
5. Read `03-executors-and-futures.md` -> learn async composition
6. Read `04-executor-service.md` -> understand the core executor lifecycle
7. Run `ExecutorServiceDemo.java` -> use thread pools properly
8. Run `ExecutorDemo.java` -> see CompletableFuture composition
9. Complete exercises
