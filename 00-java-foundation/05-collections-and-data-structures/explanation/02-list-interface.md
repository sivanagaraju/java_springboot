# The List Interface

A `List` is a perfectly ordered collection of elements. Duplicates are permitted. Elements are physically accessed via a zero-based integer index.

## 1. `ArrayList` (The Default Standard)

**Backing Data Structure:** A dynamically resizing native primitive Array.

```java
List<String> userNames = new ArrayList<>();
userNames.add("Alice"); // Adds to the end: O(1)
userNames.add(0, "Bob"); // Slides Alice right, adds Bob to front: O(N)
```

**Architectural Mechanics:**
- When an `ArrayList` reaches its physical capacity, it secretly allocates a brand new Array 50% larger than the current one.
- It then executes `System.arraycopy()` natively copying all old memory into the new block.
- **Architect Rule:** If allocating 10,000 entities, initialize precisely via `new ArrayList<>(10000)` to aggressively bypass native array reallocation penalties.

## 2. `LinkedList` (The Niche Pipeline)

**Backing Data Structure:** A Doubly-Linked sequence of disjointed memory objects (Nodes).

```java
List<String> pipeline = new LinkedList<>();
pipeline.add("Data1");
```

**Architectural Mechanics:**
- Nodes live scattered wildly across the Heap. They possess "next" and "previous" pointers natively.
- **Architect Rule:** Almost never logically use `LinkedList`. CPU Cache Locality favors massive contiguous memory blocks (`ArrayList`). Iterating a `LinkedList` natively causes massive CPU cache misses, drastically stalling hardware processors compared to arrays.

## Thread Safety

Both `ArrayList` and `LinkedList` are completely utterly unsafe for concurrency natively. If Thread A modifies a list while Thread B iterates it, the JVM instantly throws a `ConcurrentModificationException`.

For heavily threaded architectures, use `CopyOnWriteArrayList`.
**Mechanics:** On every single write mutation, it clones the entire underlying physical array. Reads are blindingly fast and totally lock-free natively. Writes are monstrously heavy mechanically.
