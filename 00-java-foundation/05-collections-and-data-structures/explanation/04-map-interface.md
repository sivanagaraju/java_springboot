# The Map Interface

A `Map` maps perfectly unique Keys to Values. It is not a `Collection` interface physically, although it is part of the Collections Framework.

## 1. `HashMap` (The Standard Dictionary)

**Backing Data Structure:** An Array of Linked Nodes (Buckets), which auto-upgrade to Red-Black Trees if bucket collisions become severe.

```java
Map<String, User> userCache = new HashMap<>();
userCache.put("u_101", new User("Alice"));
```

**Architectural Mechanics:**
- **Key Resolution:** The map inherently executes `hashCode()` on the Key to determine the array bucket. If multiple distinct Keys map to the exact same bucket, it iterates via `.equals()` to resolve the collision.
- **O(1) Performance:** Reads and writes are completely instantaneous in ideal scenarios.
- **Architect Rule:** Never use a mutable object as a HashMap Key. If the underlying data inside the Key object changes after being inserted, its `hashCode()` changes, permanently stranding the Value inside the map memory, creating an unrecoverable memory leak.

## 2. `TreeMap` (The Sorted Dictionary)

**Backing Data Structure:** A precise Red-Black Tree.

**Architectural Mechanics:**
- Keys must implement `Comparable` structurally.
- Performance degrades to `O(log N)`.
- Extremely useful for reporting dashboards (e.g., storing metrics grouped by perfectly sorted Date timestamps).

## 3. `ConcurrentHashMap` (The Thread-Safe Dynamo)

**Architectural Mechanics:**
- Legacy systems used `Hashtable`, which violently locked the entire Map globally for every single minor thread read/write.
- `ConcurrentHashMap` utilizes "Lock Striping." It logically chunks the array into distinct segments. A thread writing to segment A does not block a thread writing to segment B.
- Reads are almost completely lock-free.
- **Architect Rule:** If a HashMap is shared across multiple HTTP Request threads in Spring Boot, you must use `ConcurrentHashMap` natively to prevent CPU thread deadlocks and unpredictable data corruption.
