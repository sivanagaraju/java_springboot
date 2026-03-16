# 06 — Collections & Data Structures

## Why This Matters

Collections are Java's equivalent of Python's built-in `list`, `dict`, `set`, and `tuple`. In Spring Boot, you use collections everywhere — controller parameters, service return types, database query results, configuration properties.

The difference: Java collections are **typed**, **interface-driven**, and have **multiple implementations** with different performance characteristics. Choosing the right collection is an actual interview question.

## Python → Java Quick Map

| Python | Java | Notes |
|--------|------|-------|
| `list` | `ArrayList<T>` | Random access, resizable |
| `collections.deque` | `LinkedList<T>` | Fast insert/remove at both ends |
| `dict` | `HashMap<K,V>` | O(1) get/put, no order |
| `collections.OrderedDict` | `LinkedHashMap<K,V>` | Insertion order preserved |
| `sorted(dict)` | `TreeMap<K,V>` | Sorted by key (Red-Black tree) |
| `set` | `HashSet<T>` | O(1) add/contains |
| `tuple` | `Collections.unmodifiableList()` | Immutable view |
| `for x in list` | `for (T x : list)` | Enhanced for loop |
| `[x*2 for x in list]` | `list.stream().map(x -> x*2).toList()` | Stream API (topic 07) |

## Sub-topics

| # | File | Concept |
|---|------|---------|
| 1 | `01-collection-framework.md` | Interface hierarchy: Collection, List, Set, Map |
| 2 | `02-list-implementations.md` | ArrayList vs LinkedList — when to use which |
| 3 | `03-set-and-map.md` | HashSet, HashMap internals, TreeMap/TreeSet |
| 4 | `CollectionFrameworkDemo.java` | Interface-based programming demo |
| 5 | `ListDemo.java` | ArrayList vs LinkedList performance |
| 6 | `MapSetDemo.java` | HashMap/HashSet operations and collision handling |

## Exercises

- `Ex01_FrequencyCounter.java` — Word frequency using HashMap
- `Ex02_LRUCache.java` — Build an LRU cache using LinkedHashMap

## Study Path

1. Read `01-collection-framework.md` → understand the interface hierarchy
2. Run `CollectionFrameworkDemo.java` → see interfaces in action
3. Read `02-list-implementations.md` → ArrayList vs LinkedList
4. Run `ListDemo.java` → benchmark both implementations
5. Read `03-set-and-map.md` → HashMap internals
6. Run `MapSetDemo.java` → see hashing in action
7. Complete exercises
