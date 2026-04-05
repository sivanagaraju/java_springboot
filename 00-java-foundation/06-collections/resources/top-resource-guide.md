# Top Resource Guide — Collections

## Official Documentation

- **[Java Collections Framework Overview](https://docs.oracle.com/en/java/docs/api/java.base/java/util/doc-files/coll-overview.html)** — Official overview: hierarchy, interfaces, implementations
- **[Collections Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/Collections.html)** — All utility methods: sort, binarySearch, frequency, nCopies, unmodifiableX
- **[Map Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/Map.html)** — `merge`, `computeIfAbsent`, `getOrDefault`, `putIfAbsent`
- **[ConcurrentHashMap Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)** — Thread-safety guarantees, atomic operations

## Books

1. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 43: Prefer method references to lambdas (applies to `Comparator.comparing`)
   - Item 54: Return empty collections or arrays, not nulls
   - Item 55: Return optionals judiciously
   - Item 65: Prefer interfaces to reflection (use `List` not `ArrayList` as variable type)

2. **Java Concurrency in Practice** — Brian Goetz
   - Chapter 5: Building Blocks — `ConcurrentHashMap`, `BlockingQueue`, `CopyOnWriteArrayList`

## Blog Posts

- **[Baeldung: Java Collections](https://www.baeldung.com/java-collections)** — Comprehensive overview with examples
- **[Baeldung: HashMap in Java](https://www.baeldung.com/java-hashmap)** — Internal structure, load factor, resizing, `equals`/`hashCode` contract
- **[Baeldung: Comparator and Comparable](https://www.baeldung.com/java-comparator-comparable)** — Natural ordering, `Comparator.comparing`, chaining
- **[Baeldung: LRU Cache in Java](https://www.baeldung.com/java-lru-cache)** — `LinkedHashMap` access-order implementation

## Key Concepts to Verify You Know

- [ ] `HashMap` internal structure: array of buckets → linked list → treeify at 8 nodes (`TREEIFY_THRESHOLD`)
- [ ] Why mutable objects as `HashMap` keys break lookups — `hashCode` changes, key becomes unreachable
- [ ] `equals`/`hashCode` contract: objects that are `equals` MUST have the same `hashCode` — violation causes silent bugs
- [ ] `LinkedHashMap(cap, 0.75f, true)` — the `true` flag enables access-order (LRU), not insertion-order
- [ ] `Collections.unmodifiableList` wraps — mutation via original reference still possible. `List.copyOf()` is a true defensive copy
- [ ] `ConcurrentModificationException` is fail-fast, not guaranteed — do not rely on it for synchronization
- [ ] `PriorityQueue.poll()` removes min; to get max use `Comparator.reverseOrder()` — it is NOT a sorted list
