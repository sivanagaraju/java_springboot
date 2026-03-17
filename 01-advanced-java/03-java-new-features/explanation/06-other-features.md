# Sequenced Collections & Other Java 21 Features

## 1. Sequenced Collections (Java 21)

```
BEFORE Java 21:
┌──────────────────────────────────────────────────────┐
│ Getting first/last element was INCONSISTENT:          │
│                                                        │
│ List:       list.get(0)      list.get(list.size()-1)  │
│ Deque:      deque.getFirst() deque.getLast()           │
│ SortedSet:  sortedSet.first()sortedSet.last()         │
│ LinkedHS:   iterator().next()  ???  (no last!)        │
│                                                        │
│ 4 different APIs for the same concept!                 │
└──────────────────────────────────────────────────────┘

AFTER Java 21:
┌──────────────────────────────────────────────────────┐
│ SequencedCollection interface — unified first/last:    │
│                                                        │
│ collection.getFirst()    // any sequenced collection  │
│ collection.getLast()     // any sequenced collection  │
│ collection.reversed()   // reversed view              │
│                                                        │
│ Works on: List, Deque, LinkedHashSet, SortedSet,      │
│           LinkedHashMap (SequencedMap), SortedMap      │
└──────────────────────────────────────────────────────┘
```

```java
// Unified API across all sequenced collections
List<String> list = List.of("A", "B", "C");
list.getFirst();  // "A"
list.getLast();   // "C"
list.reversed();  // [C, B, A] (view, not copy!)

LinkedHashSet<String> set = new LinkedHashSet<>(List.of("X", "Y", "Z"));
set.getFirst();  // "X" (insertion order)
set.getLast();   // "Z"

// SequencedMap
LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
map.put("a", 1); map.put("b", 2); map.put("c", 3);
map.firstEntry();  // a=1
map.lastEntry();   // c=3
map.sequencedKeySet().reversed();  // [c, b, a]
```

---

## 2. Collection Factory Methods (Java 9+)

```java
// Immutable collections — clean creation
var list = List.of("a", "b", "c");           // ❌ list.add("d") → UnsupportedOperationException
var set  = Set.of(1, 2, 3);                  // ❌ set.remove(1) → UnsupportedOperationException
var map  = Map.of("k1", "v1", "k2", "v2");  // up to 10 entries
var bigMap = Map.ofEntries(
    Map.entry("key1", "val1"),
    Map.entry("key2", "val2")
);

// Mutable copy from immutable
var mutableList = new ArrayList<>(List.of("a", "b", "c"));
mutableList.add("d");  // ✅ works

// Collectors (Java 10+)
var unmodifiable = stream.collect(Collectors.toUnmodifiableList());
```

---

## 3. Module System Overview (Java 9)

```
module-info.java:
┌──────────────────────────────────────────────────────┐
│ module com.myapp {                                    │
│     requires java.net.http;          // depends on    │
│     requires java.sql;                                │
│     exports com.myapp.api;           // public API    │
│     opens com.myapp.model to jackson;// reflection    │
│ }                                                     │
│                                                        │
│ Most Spring Boot apps DON'T use modules yet.          │
│ Know the concept, don't worry about daily use.        │
└──────────────────────────────────────────────────────┘
```

---

## 🎯 Interview Questions

**Q1: What problem does SequencedCollection solve?**
> Before Java 21, getting the first/last element used different APIs for each collection type. `SequencedCollection` provides a unified `getFirst()`, `getLast()`, and `reversed()` across all ordered collections.

**Q2: What happens if you modify a collection created with List.of()?**
> `UnsupportedOperationException`. Factory methods (`List.of()`, `Set.of()`, `Map.of()`) create immutable collections. This is a design feature — immutable collections are thread-safe and predictable.

**Q3: Should new Spring Boot projects use the Java Module System?**
> Most Spring Boot projects don't use modules because Spring relies heavily on reflection (which modules restrict), and the classpath model works fine for most applications. Modules are more relevant for library authors and large modular systems.
