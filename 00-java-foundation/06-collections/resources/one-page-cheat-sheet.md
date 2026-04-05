# Collections — One-Page Cheat Sheet

## Collection Interfaces Hierarchy

```
Iterable
└── Collection
    ├── List          (ordered, duplicates allowed)
    │   ├── ArrayList
    │   └── LinkedList
    ├── Set           (unique elements)
    │   ├── HashSet       (unordered)
    │   ├── LinkedHashSet (insertion-ordered)
    │   └── TreeSet       (sorted)
    └── Queue / Deque
        ├── ArrayDeque    (fast stack/queue)
        └── PriorityQueue (heap, min by default)
Map (key-value, NOT Collection)
    ├── HashMap           (unordered)
    ├── LinkedHashMap     (insertion or access ordered)
    ├── TreeMap           (sorted by key)
    └── ConcurrentHashMap (thread-safe)
```

## Quick Selection Guide

| Need | Use |
|------|-----|
| Fast random access | `ArrayList` |
| Fast insert/delete at middle | `LinkedList` |
| Unique elements, fast lookup | `HashSet` |
| Unique + sorted | `TreeSet` |
| Unique + insertion order | `LinkedHashSet` |
| Key-value, fast lookup | `HashMap` |
| Key-value + sorted keys | `TreeMap` |
| Key-value + insertion order | `LinkedHashMap` |
| LRU cache | `LinkedHashMap(cap, 0.75f, true)` |
| Priority ordering | `PriorityQueue` (min-heap) |
| Thread-safe map | `ConcurrentHashMap` |
| Thread-safe sorted map | `ConcurrentSkipListMap` |
| Blocking producer-consumer | `LinkedBlockingQueue` |

## Key Operations

```java
// List
List<String> list = new ArrayList<>();
list.add("x");  list.addAll(other);  list.remove(0);
list.get(0);    list.set(0, "y");    list.size();
list.contains("x");  list.indexOf("x");
list.subList(1, 3);  // view, not copy
Collections.sort(list);
Collections.sort(list, Comparator.reverseOrder());
list.sort((a, b) -> a.compareTo(b));

// Map
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);      map.get("a");        map.remove("a");
map.containsKey("a"); map.getOrDefault("a", 0);
map.putIfAbsent("a", 1);
map.computeIfAbsent("a", k -> expensiveCompute(k));
map.merge("a", 1, Integer::sum);     // increment counter
map.forEach((k, v) -> ...);
map.entrySet().stream().sorted(Map.Entry.comparingByValue())...

// Set
Set<String> set = new HashSet<>(list);  // deduplicate list
set.add("x");  set.remove("x");  set.contains("x");
set.retainAll(other);  // intersection
set.removeAll(other);  // difference

// Immutable collections (Java 9+)
List.of("a", "b");           // null NOT allowed
Set.of("a", "b");
Map.of("k1", 1, "k2", 2);
Map.copyOf(existingMap);     // defensive copy
```

## Sorting with Comparator

```java
// Sort by field
list.sort(Comparator.comparing(Person::getName));
list.sort(Comparator.comparing(Person::getAge).reversed());
list.sort(Comparator.comparing(Person::getDept)
                    .thenComparing(Person::getName));

// 2D array by first column
Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));
// NOTE: avoid (a,b) -> a[0]-b[0] — overflow risk with negatives

// PriorityQueue (min-heap by default)
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
// Max-heap:
PriorityQueue<Integer> maxPq = new PriorityQueue<>(Comparator.reverseOrder());
```

## Python Bridge

| Java | Python |
|------|--------|
| `ArrayList` | `list` |
| `LinkedList` as stack/queue | `collections.deque` |
| `HashMap` | `dict` |
| `HashSet` | `set` |
| `TreeMap` | `dict` (Python 3.7+ insertion-ordered; use `sortedcontainers.SortedDict` for sorted) |
| `PriorityQueue` (min) | `heapq` |
| `Collections.unmodifiableList()` | `tuple` or read-only view |
| `List.of()` | `tuple` (truly immutable) |
| `map.merge(k, 1, Integer::sum)` | `Counter` or `defaultdict(int)` |
| `map.computeIfAbsent(k, fn)` | `dict.setdefault(k, fn())` (eager!) |

## Common Traps

```
TRAP 1: ConcurrentModificationException
  for (String s : list) { list.remove(s); }  // throws!
  Fix: list.removeIf(predicate)  OR  iterator.remove()

TRAP 2: Integer subtraction comparator overflow
  (a, b) -> a - b  // WRONG: Integer.MIN_VALUE - 1 overflows!
  Fix: Integer.compare(a, b)  or  Comparator.naturalOrder()

TRAP 3: HashMap key mutation
  Map<List<Integer>, String> map = new HashMap<>();
  List<Integer> key = new ArrayList<>(List.of(1, 2));
  map.put(key, "v");
  key.add(3);       // mutates key — hashCode changed! get() returns null
  Fix: only use immutable objects as Map keys

TRAP 4: PriorityQueue has no Comparable
  new PriorityQueue<int[]>()  // ClassCastException at runtime
  Fix: always provide Comparator for arrays/custom objects

TRAP 5: List.of() vs Arrays.asList()
  List.of() — fully immutable (no set either)
  Arrays.asList() — fixed size but set() allowed (no add/remove)
  new ArrayList<>(List.of(...)) — fully mutable copy

TRAP 6: TreeSet with custom objects — must implement Comparable
  or provide Comparator — otherwise ClassCastException on insert
```
