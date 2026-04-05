# Progressive Quiz Drill — Collections

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** `HashMap` allows one `null` key; `Hashtable` does not. What else distinguishes them? Which should you use in 2024?

**Q2.** You insert elements 1, 2, 3 into a `HashSet` and then a `TreeSet`. Are the iteration orders the same? What guarantees does each provide?

**Q3.** `List.of("a", "b")` vs `Collections.unmodifiableList(new ArrayList<>(...))` — what is the difference in mutability behavior?

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** Implement an LRU (Least Recently Used) cache of size N using only JDK classes. What class do you use and what constructor parameter enables LRU behavior? Show the complete implementation.

**Q5.** You need to count the frequency of words in a document. Show two implementations: (1) manual HashMap loop, (2) Stream + `Collectors.groupingBy`. For 10 million words, which is faster and why?

**Q6.** A `ConcurrentHashMap` is used to cache results. Two threads simultaneously check-then-put the same key. Show the bug and the atomic fix using `computeIfAbsent`.

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code:
```java
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    if (s.equals("b")) list.remove(s);
}
```
What exception is thrown? What are two ways to fix it?

**Q8.** Code:
```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Alice", 87);
System.out.println(scores.size());
```
What does this print? Is this a bug or expected behavior?

**Q9.** Code:
```java
PriorityQueue<int[]> pq = new PriorityQueue<>();
pq.offer(new int[]{3, 1});
pq.offer(new int[]{1, 2});
int[] top = pq.poll();
```
This throws a `ClassCastException`. Why? How do you fix it?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're building a leaderboard service for a gaming platform. Requirements: (1) `O(log n)` rank lookup by score; (2) `O(1)` score update by player ID; (3) top-K players query in `O(k log n)`; (4) thread-safe for concurrent updates; (5) 1 million players. Design the data structure combination, explain time/space complexity, and discuss what `ConcurrentSkipListMap` offers vs a sorted `ArrayList` + `ReentrantReadWriteLock`.

---

## Answer Key

**A1.** Differences: `HashMap` is unsynchronized (single-thread); `Hashtable` is synchronized on every method (coarse-grained, slow). `HashMap` allows null key/values; `Hashtable` throws `NullPointerException`. `HashMap` fails fast on structural modification during iteration; `Hashtable` uses `Enumeration` (legacy). Use `HashMap` for single-threaded code, `ConcurrentHashMap` for concurrent code. `Hashtable` is obsolete — never use in new code.

**A2.** `HashSet` iteration order: unpredictable, based on hash codes. After inserting 1,2,3 you might iterate in any order. `TreeSet` uses red-black tree sorted by natural ordering — iterating always gives `1, 2, 3` in ascending order. They are not the same. `LinkedHashSet` maintains insertion order if you need predictable iteration without sorting overhead.

**A3.** `List.of()`: structurally immutable AND element-immutable — no add, remove, OR set. Also disallows null elements. `Collections.unmodifiableList(new ArrayList<>(...))`: structurally immutable (no add/remove) but the underlying list can be mutated via the original reference. Additionally, `List.of()` returns a value-based object suitable for caching; `unmodifiableList` wraps and delegates. Use `List.of()` for truly immutable lists, `List.copyOf()` for defensive copies.

**A4.** `LinkedHashMap` with `accessOrder = true` constructor: `new LinkedHashMap<K,V>(capacity, 0.75f, true)`. Override `removeEldestEntry`: `protected boolean removeEldestEntry(Map.Entry<K,V> eldest) { return size() > maxSize; }`. Full implementation:
```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;
    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true);  // true = access-order
        this.maxSize = maxSize;
    }
    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > maxSize;
    }
}
```
Thread-safe version: wrap with `Collections.synchronizedMap()` or use `ConcurrentLinkedHashMap` (Guava).

**A5.** Manual HashMap: `for (String word : words) { map.merge(word, 1, Integer::sum); }`. Stream: `words.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))`. Performance: for 10M words, the manual loop is faster — Streams have boxing overhead (`Long` vs `int`), object creation for collectors, and parallel overhead only helps if truly CPU-bound. `merge()` is particularly efficient: single lookup + update. For max performance at scale: manual loop with `HashMap` and `merge()`. For readability in normal cases: streams are preferred.

**A6.** Bug: `if (!cache.containsKey(key)) { cache.put(key, compute(key)); }` — two threads both see missing key, both compute, second put overwrites first (wasted computation, possibly inconsistent state). Fix: `cache.computeIfAbsent(key, k -> compute(k))` — atomic check-then-put guaranteed by `ConcurrentHashMap`. Note: `computeIfAbsent` holds a lock only on the relevant segment/bucket — high concurrency for different keys.

**A7.** `ConcurrentModificationException` — modifying a collection while iterating with enhanced for-loop (uses `Iterator` internally, fail-fast detection via `modCount`). Fix 1: use `Iterator` explicitly: `Iterator<String> it = list.iterator(); while (it.hasNext()) { if (it.next().equals("b")) it.remove(); }`. Fix 2: use `removeIf`: `list.removeIf(s -> s.equals("b"))` — cleanest approach. Fix 3: collect to a new list: `list = list.stream().filter(s -> !s.equals("b")).collect(Collectors.toList())`.

**A8.** Prints `1`. `HashMap` keys are unique — `put("Alice", 87)` replaces the existing value for key "Alice". Not a bug — this is expected Map semantics: `put` returns the OLD value (95 here) which is usually discarded. If you need to detect overwrites: `if (scores.putIfAbsent("Alice", 87) != null) { /* already existed */ }`.

**A9.** `PriorityQueue` requires elements to implement `Comparable` or a `Comparator` to be provided. `int[]` does not implement `Comparable` — when the queue tries to compare two `int[]` elements for ordering, it throws `ClassCastException`. Fix: provide a `Comparator`: `new PriorityQueue<>((a, b) -> a[0] - b[0])` to sort by first element ascending. Min-heap by second element: `new PriorityQueue<>((a, b) -> a[1] - b[1])`.

**A10.** Data structure combination: `ConcurrentHashMap<String, Integer>` (playerId → score) for O(1) updates; `ConcurrentSkipListMap<Integer, Set<String>>` (score → players) for O(log n) ranked access. Update: atomically update score in HashMap, then update SkipListMap (remove from old score bucket, add to new). Top-K: `skMap.descendingMap().entrySet()` stream, flat-map players, limit K → O(k log n). `ConcurrentSkipListMap` vs sorted `ArrayList + ReadWriteLock`: SkipListMap allows lock-free reads and fine-grained writes using CAS; `ArrayList` would require full write lock blocking all reads during sort. SkipListMap is better for high-concurrency read-write mix. Space: O(n) for both structures combined.
