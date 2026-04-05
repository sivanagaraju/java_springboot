# Multithreading Progressive Quiz Drill

```mermaid
flowchart LR
    A[Core Recall] --> B[Apply and Compare]
    B --> C[Debug the Bug]
    C --> D[Staff-Level Scenario]
```

## Round 1 - Core Recall

**Q1.** Why is a thread pool better than creating a new thread for every task?

**Q2.** What problem does `ExecutorService` solve beyond plain `Thread`?

**Q3.** Why are `Future` and timeout handling important in production?

**Q4.** Why do synchronized blocks matter when state is shared?

## Round 2 - Apply and Compare

**Q5.** Would you use `ExecutorService` or raw threads for a burst of small background tasks? Why?

**Q6.** You need to wait for several worker tasks and then shut down cleanly. Which executor methods matter most?

**Q7.** You have a read-heavy shared list. Would you choose `ArrayList` with no lock or a concurrent alternative? Why?

## Round 3 - Debug the Bug

**Q8.** What is wrong here?

```java
for (Runnable task : tasks) {
    new Thread(task).start();
}
```

**Q9.** Why is this dangerous?

```java
private int counter = 0;
public void increment() {
    counter++;
}
```

Multiple threads call `increment()` concurrently.

**Q10.** What can go wrong here?

```java
Future<String> future = pool.submit(task);
System.out.println(future.get());
```

The task can block for a long time or fail silently.

## Round 4 - Staff-Level Scenario

**Q11.** A Spring Boot service sees thread starvation under load. What thread-pool and blocking questions would you ask first?

**Q12.** A team sees flaky tests only when they run in parallel. Which concurrency concepts would you inspect first?

---

## Answer Key

### Round 1 - Core Recall

**A1.** A thread pool bounds resource usage and reuses workers. That reduces overhead and makes latency more predictable.

**A2.** `ExecutorService` separates task submission from thread mechanics. It adds lifecycle management, futures, cancellation, and bulk execution methods.

**A3.** Futures let you observe completion, fetch results, and enforce time limits. Without them, a slow or stuck task can silently degrade the whole system.

**A4.** Synchronization protects shared mutable state from race conditions. Without it, updates can be lost or visibility can become inconsistent.

### Round 2 - Apply and Compare

**A5.** Use `ExecutorService`. It is safer, bounded, easier to shut down, and more appropriate for repeated short tasks.

**A6.** `invokeAll()`, `submit()`, `shutdown()`, `awaitTermination()`, and optionally `shutdownNow()` for forced cleanup.

**A7.** Use a concurrent alternative or proper synchronization. A plain `ArrayList` is not safe when multiple threads can write or iterate concurrently.

### Round 3 - Debug the Bug

**A8.** It creates uncontrolled raw threads. That makes resource usage hard to bound and shutdown harder to manage.

**A9.** `counter++` is not atomic. Threads can overwrite each other’s updates and the final count can be wrong.

**A10.** The call can block the calling thread forever if the task stalls. Without timeout handling, the application can appear hung.

### Round 4 - Staff-Level Scenario

**A11.** Ask about pool sizing, task length, blocking I/O, queue depth, and shutdown discipline. Starvation often comes from too many blocking tasks and too few workers.

**A12.** Inspect shared mutable state, synchronization, executor usage, and timing assumptions in tests. Parallel tests often expose hidden race conditions and leaked state.
