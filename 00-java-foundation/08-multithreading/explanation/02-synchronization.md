# Synchronization: Protecting Shared State

## The Problem: Race Conditions

When two threads read-modify-write the same variable, the result depends on timing:

```
RACE CONDITION EXAMPLE (counter++):

  counter++ is NOT atomic. It's actually 3 operations:
    1. READ   counter → register (value = 5)
    2. ADD    register + 1       (value = 6)
    3. WRITE  register → counter (counter = 6)

  TWO THREADS incrementing counter = 5:

  Thread A                    Thread B
  ────────                    ────────
  READ counter → 5
                              READ counter → 5   ← SAME VALUE!
  ADD: 5 + 1 = 6
                              ADD: 5 + 1 = 6
  WRITE counter = 6
                              WRITE counter = 6   ← OVERWRITES A's work!

  Expected: 7     Actual: 6     ← LOST UPDATE!

  ┌───────────────────────────────────────────────────┐
  │ This is the #1 concurrency bug.                    │
  │ It's non-deterministic — sometimes it "works."    │
  │ That's what makes it so dangerous.                 │
  └───────────────────────────────────────────────────┘
```

## Solution 1: `synchronized` Keyword

```java
// Method-level synchronization
synchronized void increment() {
    counter++;  // Only ONE thread can execute this at a time
}

// Block-level synchronization (more granular)
void increment() {
    synchronized(this) {  // lock on 'this' object
        counter++;
    }
}
```

```
HOW synchronized WORKS:

  Every Java object has an INTRINSIC LOCK (monitor):

  ┌────────────────────────────────────────────────────┐
  │  Object "myCounter"                                 │
  │  ┌──────────────┐                                   │
  │  │ intrinsic    │                                    │
  │  │ lock: [ ]    │  ← empty = available               │
  │  └──────────────┘                                   │
  │  ┌──────────────┐                                   │
  │  │ data: 5      │                                    │
  │  └──────────────┘                                   │
  └────────────────────────────────────────────────────┘

  Thread A enters synchronized(myCounter):
    → lock: [Thread A]  ← A holds the lock
    → A reads, modifies, writes safely

  Thread B tries to enter synchronized(myCounter):
    → lock is held by A → B is BLOCKED
    → B waits until A releases the lock

  Thread A exits synchronized block:
    → lock: [ ]  ← released
    → B wakes up, acquires lock, proceeds
```

## Solution 2: `volatile` Keyword

`volatile` guarantees **visibility** but NOT atomicity:

```
volatile int flag = 0;

  WITHOUT volatile:
  ┌──────────┐     ┌─────────────┐     ┌──────────┐
  │ Thread A │     │ Main Memory │     │ Thread B │
  │ cache: 0 │     │   flag: 1   │     │ cache: 0 │ ← stale!
  └──────────┘     └─────────────┘     └──────────┘
  Thread A writes flag=1. Thread B still sees 0 (cached).

  WITH volatile:
  ┌──────────┐     ┌─────────────┐     ┌──────────┐
  │ Thread A │     │ Main Memory │     │ Thread B │
  │ no cache │←───▶│   flag: 1   │←───▶│ no cache │
  └──────────┘     └─────────────┘     └──────────┘
  Every read/write goes directly to main memory.

  ┌────────────────────────────────────────────────────┐
  │  volatile:                                          │
  │    ✓ Visibility: other threads see updates          │
  │    ✗ Atomicity: counter++ still broken              │
  │    Use for: flags, status variables, published refs │
  └────────────────────────────────────────────────────┘
```

## Solution 3: Atomic Classes (Best for Counters)

```java
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();  // Thread-safe, lock-free!

// Uses CPU-level Compare-And-Swap (CAS):
// "If value is still X, change it to Y. Otherwise, retry."
// No locking → no blocking → better performance under contention.
```

## Deadlock

```
DEADLOCK: Two threads, two locks, circular wait

  Thread A:                         Thread B:
  ────────                          ────────
  synchronized(lock1) {             synchronized(lock2) {
      // ... holds lock1 ...            // ... holds lock2 ...
      synchronized(lock2) {             synchronized(lock1) {
          // WAITS for lock2 ←──────────── WAITS for lock1
          // ↑ Thread B holds it          // ↑ Thread A holds it
      }                                }
  }                                 }

  → BOTH threads wait FOREVER. JVM does NOT detect or fix this.

  ┌───────────────────────────────────────────────────┐
  │  PREVENTION:                                       │
  │  1. Always acquire locks in the SAME ORDER         │
  │  2. Use tryLock() with timeout (ReentrantLock)    │
  │  3. Avoid nested locks when possible               │
  │  4. Use higher-level concurrency utilities         │
  └───────────────────────────────────────────────────┘
```

## synchronized vs volatile vs Atomic

```
┌──────────────┬────────────┬────────────┬──────────────────────┐
│  Feature     │synchronized│  volatile  │  AtomicInteger       │
├──────────────┼────────────┼────────────┼──────────────────────┤
│  Visibility  │     ✓      │     ✓      │     ✓                │
│  Atomicity   │     ✓      │     ✗      │     ✓                │
│  Blocking    │     Yes    │     No     │     No (CAS)         │
│  Performance │   Slowest  │   Fastest  │     Fast             │
│  Use case    │  Complex   │  Flags,    │  Counters,           │
│              │  critical  │  status    │  simple state        │
│              │  sections  │  variables │  updates             │
└──────────────┴────────────┴────────────┴──────────────────────┘
```

---

## Interview Questions

**Q1: What is a race condition? Give a real example.**
> A race condition occurs when the correctness of a program depends on the relative timing of thread execution. Classic example: two threads incrementing a shared counter. `counter++` is not atomic (read-modify-write), so threads can overwrite each other's work. In Spring Boot, a non-thread-safe singleton bean with mutable fields causes race conditions.

**Q2: What is the difference between synchronized and volatile?**
> `synchronized` provides both atomicity AND visibility — only one thread executes the block, and changes are visible to other threads upon release. `volatile` provides ONLY visibility — every read/write goes to main memory, but compound operations (counter++) are still unsafe. Use `volatile` for simple flags; use `synchronized` for critical sections.

**Q3: How do you detect and prevent deadlock?**
> Detection: Thread dumps (`jstack`), JMX, or VisualVM show BLOCKED threads and lock owners. Prevention: (1) acquire locks consistently in the same order, (2) use `ReentrantLock.tryLock(timeout)` to avoid infinite waiting, (3) minimize synchronized scope, (4) prefer concurrent collections and atomic classes over manual locking.
