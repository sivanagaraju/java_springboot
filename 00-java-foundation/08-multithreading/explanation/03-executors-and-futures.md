# Executors, Future, and CompletableFuture

## Why Not Create Threads Manually?

Creating threads directly (`new Thread(...)`) is like allocating memory manually in C — it works, but it's error-prone and wasteful.

```
PROBLEM WITH RAW THREADS:

  for (int i = 0; i < 10000; i++) {
      new Thread(task).start();  // 10,000 OS threads!
  }

  → Thread creation is EXPENSIVE (1-2 MB stack per thread)
  → Too many threads → context switching overhead
  → No reuse, no bounds, no error handling

  ┌────────────────────────────────────────────────────────┐
  │  SOLUTION: Thread Pools                                 │
  │                                                         │
  │  Create N worker threads, reuse them for M tasks       │
  │  (where M >> N)                                        │
  │                                                         │
  │  Task Queue:  [task1][task2][task3][task4][task5]...    │
  │                  │       │       │                      │
  │  Workers:    [Worker1][Worker2][Worker3]                │
  │               (reused)  (reused) (reused)              │
  └────────────────────────────────────────────────────────┘
```

## ExecutorService: The Thread Pool API

```java
// Create a fixed pool with 4 threads
ExecutorService pool = Executors.newFixedThreadPool(4);

// Submit tasks
pool.submit(() -> System.out.println("Task 1"));
pool.submit(() -> System.out.println("Task 2"));

// MUST shut down when done (or threads keep JVM alive)
pool.shutdown();
```

### Pool Types

```
┌────────────────────────────────┬──────────────────────────────────┐
│  Factory Method                │  Behavior                        │
├────────────────────────────────┼──────────────────────────────────┤
│  newFixedThreadPool(n)         │  Exactly N threads, queue excess │
│  newCachedThreadPool()         │  Creates threads as needed,      │
│                                │  recycles idle threads           │
│  newSingleThreadExecutor()     │  One thread, tasks run in order  │
│  newScheduledThreadPool(n)     │  Supports delayed/periodic tasks │
│  newVirtualThreadPerTaskExecutor()│ Java 21+ virtual threads      │
└────────────────────────────────┴──────────────────────────────────┘

SPRING BOOT DEFAULT:
  Tomcat thread pool: 200 threads (one per HTTP request)
  → This is why Spring singletons MUST be thread-safe
```

## Future\<T>: Result of Async Computation

```java
ExecutorService pool = Executors.newFixedThreadPool(2);

// Submit a Callable<T> (like Runnable but returns a value)
Future<Integer> future = pool.submit(() -> {
    Thread.sleep(2000);
    return 42;
});

// Do other work while task runs...

// Get result (BLOCKS until complete)
Integer result = future.get();           // blocks forever
Integer result = future.get(5, TimeUnit.SECONDS);  // blocks with timeout
```

```
FUTURE LIMITATIONS:

  ┌────────────────────────────────────────────────────────┐
  │  future.get() BLOCKS the calling thread                 │
  │  → Defeats the purpose of async!                        │
  │                                                         │
  │  Cannot:                                                │
  │    - Chain futures together                             │
  │    - Handle errors elegantly                            │
  │    - Combine multiple futures                           │
  │    - React when future completes                        │
  │                                                         │
  │  SOLUTION: CompletableFuture (Java 8+)                  │
  └────────────────────────────────────────────────────────┘
```

## CompletableFuture: Non-Blocking Composition

```java
CompletableFuture<String> future = CompletableFuture
    .supplyAsync(() -> fetchFromDatabase())     // runs on ForkJoinPool
    .thenApply(data -> transform(data))         // chain: transform result
    .thenApply(String::toUpperCase)             // chain: another transform
    .exceptionally(ex -> "Error: " + ex);       // handle errors
```

### Key Operations

```
COMPLETABLEFUTURE PIPELINE:

  supplyAsync(supplier)     ← Start async task
       │
       ▼
  thenApply(fn)             ← Transform: T → R  (like map)
       │
       ▼
  thenCompose(fn)           ← Chain: T → CF<R>  (like flatMap)
       │
       ▼
  thenCombine(other, fn)    ← Combine two futures
       │
       ▼
  exceptionally(fn)         ← Handle errors
       │
       ▼
  thenAccept(consumer)      ← Consume result (void)

  ┌──────────────────────────────────────────────────────────┐
  │  thenApply  vs  thenCompose:                              │
  │    thenApply:   fn returns R        → CF<R>               │
  │    thenCompose: fn returns CF<R>    → CF<R> (flattened)   │
  │    (Same as map vs flatMap in streams!)                   │
  └──────────────────────────────────────────────────────────┘
```

### Combining Multiple Futures

```java
// Wait for ALL to complete
CompletableFuture<Void> all = CompletableFuture.allOf(future1, future2, future3);

// Wait for ANY to complete (first one wins)
CompletableFuture<Object> any = CompletableFuture.anyOf(future1, future2, future3);

// Combine two results
CompletableFuture<String> combined = 
    fetchUser(id).thenCombine(fetchOrders(id), (user, orders) ->
        user.getName() + " has " + orders.size() + " orders"
    );
```

## Python Comparison

```python
# Python async equivalent:
import asyncio

async def fetch_data():
    await asyncio.sleep(1)
    return "data"

async def process():
    data = await fetch_data()           # like thenApply
    result = transform(data)
    return result

# Java equivalent:
# CompletableFuture.supplyAsync(this::fetchData)
#     .thenApply(this::transform)
```

---

## Interview Questions

**Q1: Why should you use ExecutorService instead of creating threads directly?**
> Thread creation is expensive (~1 MB stack per thread). ExecutorService pools and reuses threads, controls concurrency level, and provides lifecycle management (`shutdown()`). Production rule: never create threads with `new Thread()` — always use a pool. In Spring Boot, the container manages thread pools for you.

**Q2: What is the difference between `Future` and `CompletableFuture`?**
> `Future.get()` blocks the calling thread. `CompletableFuture` supports non-blocking composition with `thenApply`, `thenCompose`, `thenCombine`, and error handling with `exceptionally`. `CompletableFuture` is to `Future` what `Promise` is to callbacks in JavaScript — it enables fluent async pipelines.

**Q3: What thread pool does Spring Boot use for HTTP requests?**
> Tomcat uses a thread pool (default 200 threads). Each HTTP request gets its own thread from this pool. This is why Spring singleton beans MUST be thread-safe — multiple request threads access the same bean instance simultaneously. Spring Boot 3.2+ supports virtual threads (Project Loom) which can handle millions of concurrent connections.
