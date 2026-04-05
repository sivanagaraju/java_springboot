# Multithreading — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting.

## Exercises

### Ex01 — Producer-Consumer (`Ex01_ProducerConsumer.java`)
Implement `BoundedBuffer<T>` with `put()` (blocks when full) and `take()` (blocks when empty) using `synchronized` + `wait()`/`notifyAll()`.

**Skills:** `synchronized`, `wait()`, `notifyAll()`, spurious wakeup guard (`while` not `if`), thread coordination
**Time estimate:** 35 minutes

### Ex02 — Parallel File Processor (`Ex02_ParallelFileProcessor.java`)
Process multiple "files" in parallel using `CompletableFuture.supplyAsync()`, `thenApply()`, `allOf()`, and `exceptionally()` for error handling.

**Skills:** `CompletableFuture`, `ExecutorService`, `thenApply`, `allOf`, `exceptionally`, structured concurrency
**Time estimate:** 30 minutes

## Solutions
See `solutions/` directory after attempting.

## Connection to Spring Boot
- Ex01 → `BlockingQueue` / `ThreadPoolExecutor`'s work queue — the same pattern Spring's thread pool uses
- Ex02 → Spring's `@Async` uses `CompletableFuture` under the hood; `@Async` methods should return `CompletableFuture<T>`
- Spring Boot 3.2 virtual threads make Ex02's thread-pool management less necessary — but the async composition patterns remain
