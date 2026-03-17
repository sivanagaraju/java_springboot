# Parallel Streams — When to Go Parallel (and When NOT to)

## Python → Java Mental Map

| Python | Java |
|--------|------|
| `multiprocessing.Pool.map()` | `stream.parallel().map()` |
| `concurrent.futures.ProcessPoolExecutor` | Uses `ForkJoinPool.commonPool()` |
| GIL limits true parallelism | JVM has true multi-threading |

---

## 1. Sequential vs Parallel Pipeline

```
Sequential Stream:
┌────────┐    ┌────────┐    ┌────────┐    ┌────────┐
│ Source  │───→│ filter │───→│  map   │───→│ reduce │
│ [1..N] │    │        │    │        │    │        │
└────────┘    └────────┘    └────────┘    └────────┘
                    Single thread processes all elements

Parallel Stream:
┌────────┐    ┌─── Thread 1 ──── filter → map ───┐
│ Source  │───→│─── Thread 2 ──── filter → map ───│───→ reduce (merge)
│ [1..N] │    │─── Thread 3 ──── filter → map ───│
└────────┘    └─── Thread 4 ──── filter → map ───┘
                    ForkJoinPool splits work across CPU cores
```

### How It Works Under the Hood

```
ForkJoinPool.commonPool()  (default parallelism = CPU cores - 1)

Step 1: SPLIT (Fork)
┌──────────────────────────────┐
│        [1, 2, 3, 4, 5, 6, 7, 8]         │
│           ╱                    ╲          │
│    [1, 2, 3, 4]          [5, 6, 7, 8]    │
│      ╱      ╲              ╱      ╲      │
│  [1, 2]  [3, 4]        [5, 6]  [7, 8]   │
└──────────────────────────────┘

Step 2: COMPUTE (per chunk)
  Each sub-array processed independently

Step 3: MERGE (Join)
  Results combined: [r1,r2] + [r3,r4] + [r5,r6] + [r7,r8]
```

---

## 2. API — Creating Parallel Streams

```java
// Method 1: Convert existing stream
list.stream().parallel()

// Method 2: Create directly
list.parallelStream()

// Method 3: From array
Arrays.stream(array).parallel()

// Check if parallel
stream.isParallel()  // true/false
```

---

## 3. When to Use (and NOT Use) Parallel Streams

```
┌─────────────────────────────────────────────────────────────────┐
│                    DECISION FLOWCHART                            │
│                                                                 │
│  Is the data source > 10,000 elements?                         │
│      NO  → ❌ Use sequential (overhead > benefit)              │
│      YES ↓                                                      │
│                                                                 │
│  Is the operation CPU-intensive (not I/O)?                      │
│      NO  → ❌ Use sequential (I/O blocks ForkJoinPool!)        │
│      YES ↓                                                      │
│                                                                 │
│  Is the source easily splittable?                               │
│      ArrayList/array → ✅ Great split                          │
│      LinkedList      → ❌ Terrible split (O(n) to find middle) │
│      HashSet         → ⚠️ OK split                            │
│      YES ↓                                                      │
│                                                                 │
│  Is the operation stateless & independent?                      │
│      NO  → ❌ Use sequential (shared state = bugs)             │
│      YES → ✅ USE PARALLEL                                     │
└─────────────────────────────────────────────────────────────────┘
```

### Source Splittability Rankings

```
┌─────────────────┬────────────┬─────────────────────────┐
│ Source           │ Rating     │ Why                     │
├─────────────────┼────────────┼─────────────────────────┤
│ ArrayList        │ ⭐⭐⭐ Best│ Random access, easy split│
│ int[] / long[]   │ ⭐⭐⭐ Best│ Contiguous memory        │
│ IntStream.range  │ ⭐⭐⭐ Best│ Predictable size         │
│ HashSet/HashMap  │ ⭐⭐ Good  │ Bucket-based splitting   │
│ TreeSet/TreeMap  │ ⭐⭐ Good  │ Balanced tree splitting  │
│ LinkedList       │ ⭐ Bad     │ Must traverse to split   │
│ Stream.iterate   │ ⭐ Bad     │ Sequential dependency    │
│ BufferedReader   │ ⭐ Bad     │ I/O bound, can't split   │
└─────────────────┴────────────┴─────────────────────────┘
```

---

## 4. Common Traps

### Trap 1: Shared Mutable State

```java
// ❌ WRONG — race condition on shared list
List<Integer> results = new ArrayList<>();
IntStream.rangeClosed(1, 1000).parallel()
    .forEach(results::add);  // ArrayList is NOT thread-safe!
// results.size() might be < 1000, or ArrayIndexOutOfBoundsException

// ✅ CORRECT — use collect (thread-safe accumulation)
List<Integer> results = IntStream.rangeClosed(1, 1000)
    .parallel()
    .boxed()
    .collect(Collectors.toList());
```

### Trap 2: Order-Dependent Operations

```java
// ❌ Parallel + findFirst is SLOWER than sequential
// (must coordinate across threads to find "first")
stream.parallel().findFirst();

// ✅ Use findAny for parallel (any thread can return)
stream.parallel().findAny();

// ❌ forEachOrdered on parallel negates parallelism
stream.parallel().forEachOrdered(System.out::println);  // sequential printing

// ✅ Use forEach if order doesn't matter
stream.parallel().forEach(System.out::println);  // interleaved but fast
```

### Trap 3: Blocking the Common ForkJoinPool

```
⚠️ ALL parallel streams share the SAME ForkJoinPool.commonPool()

If one parallel stream does I/O:
┌────────────────────────────────────────────┐
│  Thread 1: HTTP call... (blocked 2 sec)    │
│  Thread 2: HTTP call... (blocked 2 sec)    │
│  Thread 3: HTTP call... (blocked 2 sec)    │
│  Thread 4: HTTP call... (blocked 2 sec)    │
│  ALL THREADS BLOCKED → other parallel      │
│  streams in entire JVM are STARVED!        │
└────────────────────────────────────────────┘

Fix: Use custom ForkJoinPool for I/O tasks
```

```java
// ✅ Custom pool to avoid starving common pool
ForkJoinPool customPool = new ForkJoinPool(10);
List<String> results = customPool.submit(() ->
    urls.parallelStream()
        .map(this::fetchUrl)  // I/O operations
        .collect(Collectors.toList())
).get();
customPool.shutdown();
```

---

## 5. Reduction in Parallel — Associativity Matters

```java
// Reduction must be ASSOCIATIVE for parallel correctness
// Associative: (a ⊕ b) ⊕ c == a ⊕ (b ⊕ c)

// ✅ Addition is associative
stream.parallel().reduce(0, Integer::sum);
// (1+2) + (3+4) == 1 + (2+3) + 4 == 10  ✓

// ❌ Subtraction is NOT associative
stream.parallel().reduce(0, (a, b) -> a - b);
// (1-2) - (3-4) ≠ 1 - (2-3) - 4  ✗ — WRONG RESULTS!
```

---

## 🎯 Interview Questions

**Q1: Why should you avoid using parallel streams for I/O operations?**
> Parallel streams use the common `ForkJoinPool` shared by the entire JVM. If threads are blocked on I/O (HTTP calls, file reads), all other parallel stream operations across the application are starved. Use a custom `ForkJoinPool` or `CompletableFuture` for I/O.

**Q2: What makes ArrayList better than LinkedList for parallel streams?**
> ArrayList splits in O(1) by calculating the midpoint index. LinkedList must traverse to find the middle, making it O(n) per split. This negates the benefit of parallelism.

**Q3: What property must a reduce operation have to work correctly with parallel streams?**
> It must be **associative**: `(a ⊕ b) ⊕ c == a ⊕ (b ⊕ c)`. The identity value must also be correct. Non-associative operations (subtraction, division) produce wrong results in parallel because chunks are reduced independently and then combined.
