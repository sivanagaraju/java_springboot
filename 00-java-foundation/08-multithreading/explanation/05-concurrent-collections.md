# Concurrent Collections — Thread-Safe Data Structures

## Python → Java Mental Map

| Python | Java |
|--------|------|
| `queue.Queue` (thread-safe) | `ConcurrentLinkedQueue`, `BlockingQueue` |
| `threading.Lock` + `dict` | `ConcurrentHashMap` (lock-free reads) |
| No direct equivalent | `CopyOnWriteArrayList` (snapshot iteration) |
| `collections.OrderedDict` (with Lock) | `ConcurrentSkipListMap` (sorted + concurrent) |

---

## 1. Why Regular Collections Fail in Multi-Threaded Code

```
Thread 1: map.put("A", 1)          Thread 2: map.put("B", 2)
     │                                  │
     ▼                                  ▼
  Read size (5)                      Read size (5)
  Calculate bucket                   Calculate bucket
  Write to bucket [3]                Write to bucket [3]  ← COLLISION!
  Update size to 6                   Update size to 6     ← LOST UPDATE!

  Expected size: 7    Actual size: 6  ← WRONG!

Even worse: concurrent resize triggers infinite loop (Java 7 HashMap)
```

### The Solutions

```
┌─────────────────────┬───────────────────────────────────────┐
│ Approach            │ Trade-off                             │
├─────────────────────┼───────────────────────────────────────┤
│ synchronized block  │ Simple but slow (one thread at a time)│
│ Collections.sync*() │ Wrapper, still coarse-grained locking │
│ ConcurrentHashMap   │ ✅ Fine-grained locking, high perf   │
│ CopyOnWriteArrayList│ ✅ Best for read-heavy workloads     │
│ BlockingQueue       │ ✅ Producer-consumer pattern          │
└─────────────────────┴───────────────────────────────────────┘
```

---

## 2. ConcurrentHashMap — The Workhorse

```
Internal structure (Java 8+):

ConcurrentHashMap uses CAS + synchronized per-bucket:

┌──────┬──────┬──────┬──────┬──────┬──────┐
│ [0]  │ [1]  │ [2]  │ [3]  │ [4]  │ ...  │  buckets (Node[])
│      │  │   │      │  │   │      │      │
│      │  ▼   │      │  ▼   │      │      │
│      │ K:V  │      │ K:V  │      │      │
│      │  │   │      │  │   │      │      │
│      │  ▼   │      │  ▼   │      │      │
│      │ K:V  │      │TreeBin│     │      │
└──────┴──────┴──────┴──────┴──────┴──────┘

Key design decisions:
1. NO global lock — each bucket has its own lock
2. Reads are LOCK-FREE (volatile reads)
3. Bucket > 8 entries → converts to Red-Black tree (like HashMap)
4. CAS (Compare-And-Swap) for single-slot updates

vs. Hashtable:
┌─────────────────┬──────────────────┬────────────────────┐
│ Feature         │ Hashtable        │ ConcurrentHashMap  │
├─────────────────┼──────────────────┼────────────────────┤
│ Lock scope      │ Entire table     │ Per bucket         │
│ Null key/value  │ ❌ No            │ ❌ No              │
│ Read blocking   │ ✅ Yes (synced)  │ ❌ No (lock-free)  │
│ Performance     │ Poor             │ Excellent          │
│ Legacy          │ ✅ (avoid it)    │ ✅ Use this        │
└─────────────────┴──────────────────┴────────────────────┘
```

### Atomic Operations (the real power)

```java
// ── These are THREAD-SAFE as single atomic operations ──

// Put only if key is absent
map.putIfAbsent("key", "value");

// Compute atomically
map.compute("counter", (key, oldVal) ->
    oldVal == null ? 1 : oldVal + 1);

// Merge values
map.merge("counter", 1, Integer::sum);

// Replace only if current value matches
map.replace("key", "oldValue", "newValue");

// ⚠️ TRAP: These are NOT atomic compound operations:
if (!map.containsKey("key")) {   // Thread 2 might insert here!
    map.put("key", "value");     // Race condition!
}
// ✅ Use putIfAbsent() instead
```

---

## 3. CopyOnWriteArrayList — Read-Optimized

```
Write operation creates a NEW array copy:

State 1: [A, B, C]  ← all readers see this
                          │
Thread-1: add("D")        │ Thread-2: iterating [A, B, C]
     │                     │ (still sees OLD snapshot)
     ▼                     │
State 2: [A, B, C, D]  ← new array created
                          │
                     Thread-2 finishes iteration
                     Thread-3: iterating [A, B, C, D]
                               (sees NEW snapshot)

✅ Reads: ZERO locking, iterate over snapshot
❌ Writes: EXPENSIVE (full array copy every time)

Best for:
- Event listener lists (rarely modified, frequently iterated)
- Configuration (loaded once, read many times)
- Observer pattern (Spring's ApplicationEventMulticaster)

Worst for:
- Frequently modified lists
- Large lists (copy cost grows with size)
```

---

## 4. BlockingQueue — Producer-Consumer Pattern

```
┌──────────┐     ┌──────────────────────┐     ┌──────────┐
│ Producer │────→│   BlockingQueue      │────→│ Consumer │
│ Thread   │ put │  [E1] [E2] [E3] [ ] │ take│ Thread   │
└──────────┘     └──────────────────────┘     └──────────┘
                        capacity = 4

put() blocks if queue is FULL   (back-pressure!)
take() blocks if queue is EMPTY (wait for work!)

Implementations:
┌─────────────────────┬────────────────────────────────┐
│ Implementation      │ Use Case                       │
├─────────────────────┼────────────────────────────────┤
│ ArrayBlockingQueue  │ Bounded, fairness option       │
│ LinkedBlockingQueue │ Optionally bounded, higher perf│
│ PriorityBlockingQ   │ Priority ordering + blocking   │
│ SynchronousQueue    │ Direct handoff (capacity = 0)  │
│ DelayQueue          │ Elements available after delay  │
└─────────────────────┴────────────────────────────────┘
```

### API Comparison

```
┌───────────┬──────────────┬──────────────┬──────────────┐
│ Operation │ Throws       │ Returns      │ Blocks       │
├───────────┼──────────────┼──────────────┼──────────────┤
│ Insert    │ add(e)       │ offer(e)     │ put(e)       │
│ Remove    │ remove()     │ poll()       │ take()       │
│ Examine   │ element()    │ peek()       │ N/A          │
└───────────┴──────────────┴──────────────┴──────────────┘
```

---

## 5. ConcurrentSkipListMap — Sorted + Concurrent

```
Skip List structure (probabilistic balanced tree alternative):

Level 3:  HEAD ─────────────────────────────→ 50 ──→ NIL
Level 2:  HEAD ────────→ 20 ─────────────────→ 50 ──→ NIL
Level 1:  HEAD ────→ 10 → 20 ────→ 30 ────→ 50 ──→ NIL
Level 0:  HEAD → 5 → 10 → 20 → 25 → 30 → 50 → 60 → NIL

Search for 25:
  Start at Level 3: HEAD → 50 (too far) → drop down
  Level 2: HEAD → 20 (ok) → 50 (too far) → drop down
  Level 1: 20 → 30 (too far) → drop down
  Level 0: 20 → 25 → FOUND!

O(log n) search, insert, delete — like TreeMap but concurrent
No global lock needed — CAS operations at each level
```

---

## 6. Spring Boot Connection

```
Spring uses concurrent collections extensively:

1. ConcurrentHashMap:
   - Bean definition registry
   - Request mapping cache
   - Session storage

2. CopyOnWriteArrayList:
   - ApplicationEventMulticaster (listener list)
   - HandlerInterceptor chains

3. BlockingQueue:
   - ThreadPoolTaskExecutor's work queue
   - @Async method execution queue
   - Spring Integration message channels

4. Configuration:
   spring:
     task:
       execution:
         pool:
           queue-capacity: 100    ← LinkedBlockingQueue size
           core-size: 5
           max-size: 10
```

---

## 🎯 Interview Questions

**Q1: Why does ConcurrentHashMap not allow null keys or values?**
> Because `get(key)` returning `null` would be ambiguous — is the key absent, or is the value `null`? In a concurrent context, you can't safely do `containsKey()` then `get()` (race condition). HashMap allows nulls because it's single-threaded and this check is safe.

**Q2: When would you choose CopyOnWriteArrayList over a synchronized list?**
> When reads vastly outnumber writes. CopyOnWriteArrayList gives lock-free reads and snapshot iteration (no ConcurrentModificationException). But every write copies the entire array, so it's terrible for write-heavy workloads. Classic use case: event listener lists.

**Q3: What is the difference between `Collections.synchronizedMap()` and `ConcurrentHashMap`?**
> `synchronizedMap` wraps every method with a global `synchronized` block — only one thread can access the map at a time. `ConcurrentHashMap` uses fine-grained per-bucket locking (Java 8+: CAS + synchronized), allowing multiple threads to read and write different segments concurrently. Also, `ConcurrentHashMap` provides atomic compound operations like `putIfAbsent`, `compute`, `merge`.
