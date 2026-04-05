# Multithreading Top Resource Guide

Curated external resources for thread pools, futures, and executor lifecycle.

```mermaid
flowchart LR
    A[Official docs] --> B[Practical tutorial]
    B --> C[Video]
    C --> D[Book]
```

## Official Docs

1. [Oracle: Executor Interfaces](https://docs.oracle.com/javase/tutorial/essential/concurrency/exinter.html)
2. [Oracle: Concurrency Utilities Overview](https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/overview.html)
3. [Oracle: Java Concurrency Utilities](https://docs.oracle.com/javase/8/docs/technotes/guides/concurrency/)
4. [ExecutorService Javadoc](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/util/concurrent/ExecutorService.html)

## Practical Reading

1. [Baeldung: A Guide to the Java ExecutorService](https://www.baeldung.com/java-executor-service-tutorial)
2. [Baeldung: ExecutorService - Waiting for Threads to Finish](https://www.baeldung.com/java-executor-wait-for-threads)
3. [Baeldung: Difference Between execute() and submit()](https://www.baeldung.com/java-execute-vs-submit-executor-service)
4. [DZone: Java Multi-Threading With the ExecutorService](https://dzone.com/articles/java-concurrency-multi-threading-with-executorserv)

## Video

1. [Jakob Jenkov: Thread Pools in Java](https://www.youtube.com/watch?v=ZcKt5FYd3bU)
2. [Coding with John: Multithreading in Java Explained in 10 Minutes](https://www.youtube.com/watch?v=R_MbozD32eo)

## Book

1. *Java Concurrency in Practice* by Brian Goetz
2. *Concurrent Programming in Java* by Doug Lea

## Python Bridge

| Java concept | Python analog |
|---|---|
| `ExecutorService` | `ThreadPoolExecutor` |
| `Future` | `concurrent.futures.Future` |
| thread pool shutdown | executor context management |

