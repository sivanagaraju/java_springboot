/**
 * ====================================================================
 * FILE    : MapSetDemo.java
 * MODULE  : 06 — Collections
 * PURPOSE : HashMap/HashSet internals, TreeMap, and LinkedHashMap
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: d = {"key": "value"}    → Java: Map.of("key", "value")
 *   Python: s = {"a", "b"}          → Java: Set.of("a", "b")
 *   Python: d.get("key", default)   → Java: map.getOrDefault("key", default)
 *
 * HASHMAP INTERNAL STRUCTURE:
 *
 *   ┌──── Node<K,V>[] table (bucket array) ────────────────┐
 *   │                                                       │
 *   │  [0]   [1]   [2]   [3]   [4]   ...   [15]           │
 *   │  null  ──▶   null  ──▶   null         null           │
 *   │        │           │                                  │
 *   │   ┌────┴────┐ ┌────┴────┐                            │
 *   │   │"A" → 1 │ │"B" → 2 │                            │
 *   │   │next:null│ │next: ──│──▶ ┌─────────┐             │
 *   │   └─────────┘ └────────┘    │"E" → 5 │  COLLISION!  │
 *   │                              │next:null│             │
 *   │                              └─────────┘             │
 *   └──────────────────────────────────────────────────────┘
 *   Default: 16 buckets, load factor 0.75
 *   Resize when: size > 16 * 0.75 = 12 entries
 *
 * ====================================================================
 */
import java.util.*;

public class MapSetDemo {

    public static void main(String[] args) {

        System.out.println("=== HASHMAP BASICS ===");

        // ── put and get ─────────────────────────────────────────────
        //
        // put("Alice", 95):
        //   1. hashCode("Alice") → some int
        //   2. bucket = hash & (capacity - 1)
        //   3. place Node("Alice", 95) at that bucket
        //
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Alice", 95);
        scores.put("Bob", 87);
        scores.put("Charlie", 92);
        System.out.println("scores: " + scores);
        System.out.println("get(Alice): " + scores.get("Alice"));

        // ── getOrDefault (like Python's dict.get(key, default)) ─────
        System.out.println("get(Dan): " + scores.getOrDefault("Dan", 0));

        // ── putIfAbsent (only insert if key not present) ────────────
        scores.putIfAbsent("Alice", 100);  // Alice exists → NO change
        scores.putIfAbsent("Dan", 88);     // Dan missing → inserted
        System.out.println("After putIfAbsent: " + scores);

        System.out.println("\n=== MAP ITERATION ===");

        // ── Three ways to iterate a Map ─────────────────────────────
        //
        // Python equivalent:
        //   for k, v in d.items(): ...
        //

        // Way 1: entrySet() — most efficient
        System.out.println("entrySet:");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println("  " + entry.getKey() + " → " + entry.getValue());
        }

        // Way 2: forEach (Java 8+)
        System.out.print("forEach: ");
        scores.forEach((name, score) -> System.out.print(name + "=" + score + " "));
        System.out.println();

        // Way 3: keySet (less efficient — extra get() per key)
        System.out.print("keySet: ");
        for (String key : scores.keySet()) {
            System.out.print(key + " ");
        }
        System.out.println();

        System.out.println("\n=== COMPUTE METHODS (Java 8+) ===");

        // ── Word frequency counter ──────────────────────────────────
        //
        // Python: Counter(words) or d[word] = d.get(word, 0) + 1
        // Java:   map.merge(word, 1, Integer::sum)
        //
        String text = "the cat sat on the mat the cat";
        Map<String, Integer> freq = new HashMap<>();
        for (String word : text.split(" ")) {
            freq.merge(word, 1, Integer::sum);
            // merge(key, value, remappingFunction):
            //   key absent? → put(key, value)
            //   key present? → put(key, remappingFunction(oldValue, value))
        }
        System.out.println("Word frequency: " + freq);

        System.out.println("\n=== HASHSET ===");

        // ── HashSet = HashMap<E, DUMMY> ─────────────────────────────
        //
        // INTERNAL:
        //   ┌─────────────────────────────────────┐
        //   │  HashSet<String>                     │
        //   │  └── HashMap<String, Object>         │
        //   │       key = your element             │
        //   │       value = PRESENT (static dummy) │
        //   └─────────────────────────────────────┘
        //
        Set<String> languages = new HashSet<>();
        languages.add("Java");
        languages.add("Python");
        languages.add("Java");  // duplicate — ignored!
        System.out.println("Set: " + languages + " (no duplicates)");
        System.out.println("contains(Java): " + languages.contains("Java"));

        // Set operations (like Python set operations)
        Set<String> backend = Set.of("Java", "Python", "Go");
        Set<String> frontend = Set.of("JavaScript", "TypeScript", "Python");

        // Union
        Set<String> union = new HashSet<>(backend);
        union.addAll(frontend);
        System.out.println("Union:        " + union);

        // Intersection
        Set<String> intersection = new HashSet<>(backend);
        intersection.retainAll(frontend);
        System.out.println("Intersection: " + intersection);

        // Difference
        Set<String> difference = new HashSet<>(backend);
        difference.removeAll(frontend);
        System.out.println("Difference:   " + difference);

        System.out.println("\n=== ORDERING: LinkedHashMap vs TreeMap ===");

        // ── HashMap: NO guaranteed order ────────────────────────────
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Charlie", 3);
        hashMap.put("Alice", 1);
        hashMap.put("Bob", 2);
        System.out.println("HashMap (no order):       " + hashMap);

        // ── LinkedHashMap: INSERTION order ───────────────────────────
        //
        // Adds a doubly-linked list threading through all entries:
        //   bucket[x] → Node("Charlie", ──linked──▶ Node("Alice", ──▶ Node("Bob"
        //
        Map<String, Integer> linkedMap = new LinkedHashMap<>();
        linkedMap.put("Charlie", 3);
        linkedMap.put("Alice", 1);
        linkedMap.put("Bob", 2);
        System.out.println("LinkedHashMap (insertion): " + linkedMap);

        // ── TreeMap: SORTED by key ──────────────────────────────────
        //
        // Internally a Red-Black tree:
        //         ┌────────┐
        //         │ Bob: 2 │
        //         └───┬────┘
        //      ┌──────┴──────┐
        //  ┌───┴────┐  ┌─────┴─────┐
        //  │Alice: 1│  │Charlie: 3│
        //  └────────┘  └──────────┘
        //
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("Charlie", 3);
        treeMap.put("Alice", 1);
        treeMap.put("Bob", 2);
        System.out.println("TreeMap (sorted by key):  " + treeMap);

        System.out.println("\n=== hashCode() / equals() CONTRACT ===");

        // ── Demonstrate the contract with custom objects ────────────
        //
        // CONTRACT:
        //   If a.equals(b) → a.hashCode() == b.hashCode()  (REQUIRED)
        //   If hashCode differs → equals MUST be false
        //   If hashCode same → equals MAY be true or false (collision)
        //
        var map = new HashMap<List<Integer>, String>();
        List<Integer> key1 = List.of(1, 2, 3);
        map.put(key1, "found");

        // Same content, different object — works because List overrides both
        List<Integer> key2 = List.of(1, 2, 3);
        System.out.println("key1.equals(key2): " + key1.equals(key2));
        System.out.println("same hashCode: " + (key1.hashCode() == key2.hashCode()));
        System.out.println("map.get(key2): " + map.get(key2));  // "found"!
    }
}
