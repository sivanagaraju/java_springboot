# 08 — Multithreading & Concurrency

## Why This Matters

Java was built for multithreading from day one — every Java program runs on the JVM which manages multiple threads. In Spring Boot, every HTTP request runs on its own thread from a thread pool. Understanding threads is essential for:
- Avoiding race conditions in shared state
- Understanding why Spring beans are singletons (thread safety!)
- Writing performant async operations
- Debugging production issues (deadlocks, thread starvation)

## Python → Java Quick Map

| Python | Java | Notes |
|--------|------|-------|
| `threading.Thread(target=fn)` | `new Thread(runnable)` | Create thread |
| `thread.start()` | `thread.start()` | Begin execution |
| `threading.Lock()` | `synchronized` / `ReentrantLock` | Mutual exclusion |
| GIL (Global Interpreter Lock) | **No GIL!** True parallelism | Java threads run in parallel |
| `concurrent.futures.ThreadPoolExecutor` | `ExecutorService` | Thread pool |
| `asyncio` | `CompletableFuture` | Async programming |

> **Critical difference:** Python has the GIL, meaning only one thread runs Python code at a time. Java has TRUE parallelism — multiple threads execute simultaneously on multiple CPU cores. This makes Java threading both more powerful and more dangerous.

## Sub-topics

| # | File | Concept |
|---|------|---------|
| 1 | `01-thread-basics.md` | Thread lifecycle, creation, Thread vs Runnable |
| 2 | `02-synchronization.md` | synchronized, volatile, race conditions |
| 3 | `03-executors-and-futures.md` | Thread pools, ExecutorService, CompletableFuture |
| 4 | `ThreadBasicsDemo.java` | Thread creation, lifecycle, and joining |
| 5 | `SynchronizationDemo.java` | Race conditions and synchronized fixes |
| 6 | `ExecutorDemo.java` | Thread pools and CompletableFuture |

## Exercises

- `Ex01_ProducerConsumer.java` — Classic concurrent pattern
- `Ex02_ParallelFileProcessor.java` — Real-world multithreaded I/O

## Study Path

1. Read `01-thread-basics.md` → understand thread lifecycle
2. Run `ThreadBasicsDemo.java` → see threads in action
3. Read `02-synchronization.md` → learn why shared state is dangerous
4. Run `SynchronizationDemo.java` → watch race conditions happen
5. Read `03-executors-and-futures.md` → the modern approach
6. Run `ExecutorDemo.java` → use thread pools properly
7. Complete exercises
