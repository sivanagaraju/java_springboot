# The Set Interface

A `Set` is a unique collection of elements. Duplicates are rejected.

## 1. `HashSet` (The Lookup Engine)

**Backing Data Structure:** It is backed by a `HashMap` under the hood.

```java
Set<String> processedIds = new HashSet<>();
processedIds.add("TXN-101"); // Returns true
processedIds.add("TXN-101"); // Returns false, duplicate rejected.
```

**Architectural Mechanics:**
- It uses `hashCode()` on the object to locate the physical bucket.
- Performance is `O(1)`.
- **Architect Rule:** You must strictly override `hashCode()` and `equals()` inside Domain Objects to use a `HashSet`. If you use the native Object methods, identical objects with different memory addresses will be permitted as duplicates.
- The iteration order of a `HashSet` is undefined and unpredictable.

## 2. `TreeSet` (The Sorted Set)

**Backing Data Structure:** A Red-Black tree.

```java
Set<Integer> sortedNumbers = new TreeSet<>();
sortedNumbers.add(50);
sortedNumbers.add(10);
sortedNumbers.add(100);
// Iteration output: 10, 50, 100
```

**Architectural Mechanics:**
- Objects must implement the `Comparable` interface.
- If objects do not implement `Comparable`, you must provide a custom `Comparator` via the constructor.
- Performance is `O(log N)` for read and write operations.
- The iteration order is always perfectly sorted.

## 3. `LinkedHashSet` (The Insertion Order Set)

**Backing Data Structure:** A `HashMap` combined with a Doubly-Linked List.

**Architectural Mechanics:**
- Maintains the exact order in which elements were added.
- Slightly slower than `HashSet` due to maintaining the linked list pointers.
- Highly useful when you need distinct unique elements but must preserve chronological processing order.
