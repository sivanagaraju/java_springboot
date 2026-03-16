/**
 * ====================================================================
 * FILE    : CollectionFrameworkDemo.java
 * MODULE  : 06 — Collections
 * PURPOSE : Demonstrate the collection interface hierarchy with code
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: list, dict, set — concrete types
 *   Java:   List, Map, Set — INTERFACES with multiple implementations
 *   Python: duck typing ("if it quacks like a list...")
 *   Java:   interface typing ("if it implements List...")
 *
 * INTERFACE HIERARCHY:
 *
 *                    Iterable<E>
 *                        │
 *                    Collection<E>
 *                   ╱      │      ╲
 *              List<E>   Set<E>  Queue<E>
 *              │  │      │  │      │
 *         ArrayList│  HashSet│  ArrayDeque
 *           LinkedList  TreeSet  PriorityQueue
 *
 *               Map<K,V>  ← SEPARATE hierarchy!
 *              ╱    │    ╲
 *        HashMap  TreeMap  LinkedHashMap
 *
 * ====================================================================
 */
import java.util.*;

public class CollectionFrameworkDemo {

    public static void main(String[] args) {

        System.out.println("=== CODING TO INTERFACES ===");

        // ── The power of interface-based programming ────────────────
        //
        // BEFORE (coupled to implementation):
        //   ArrayList<String> names = new ArrayList<>();
        //   → locked to ArrayList, can't swap
        //
        // AFTER (coded to interface):
        //   List<String> names = new ArrayList<>();
        //   → can swap to LinkedList without changing ANY other code
        //
        //   ┌──────────────┐
        //   │  List<String> │  ← your variable type (INTERFACE)
        //   └──────┬───────┘
        //          │
        //   ┌──────┴───────┐
        //   │  ArrayList   │  ← actual object (IMPLEMENTATION)
        //   └──────────────┘
        //
        List<String> names = new ArrayList<>();
        names.add("Alice");
        names.add("Bob");
        names.add("Charlie");
        System.out.println("List (ArrayList): " + names);

        // Swap implementation — NO code changes needed!
        List<String> linkedNames = new LinkedList<>(names);
        System.out.println("List (LinkedList): " + linkedNames);

        System.out.println("\n=== GENERICS: TYPE SAFETY ===");

        // ── Without generics (raw type) — DANGEROUS ────────────────
        //
        //   List rawList = new ArrayList();
        //   rawList.add("text");
        //   rawList.add(42);  ← compiles! runtime ClassCastException!
        //
        // ── With generics — compile-time safety ─────────────────────
        //
        //   List<String> safeList = new ArrayList<>();
        //   safeList.add("text");  ✅
        //   safeList.add(42);      ❌ COMPILE ERROR — caught early!
        //
        List<String> safeList = new ArrayList<>();
        safeList.add("Hello");
        // safeList.add(42);  // Uncomment to see compile error
        String first = safeList.get(0);  // No cast needed!
        System.out.println("Type-safe get: " + first);

        System.out.println("\n=== THE DIAMOND OPERATOR <> ===");

        // ── Java 7+ inference ───────────────────────────────────────
        Map<String, List<Integer>> verbose = new HashMap<String, List<Integer>>();
        Map<String, List<Integer>> concise = new HashMap<>();  // <> infers types
        System.out.println("Diamond operator avoids repeating types");

        System.out.println("\n=== IMMUTABLE COLLECTIONS (Java 9+) ===");

        // ── Factory methods for quick creation ──────────────────────
        //
        //   List.of()  → unmodifiable List
        //   Set.of()   → unmodifiable Set
        //   Map.of()   → unmodifiable Map
        //
        //   PYTHON EQUIVALENT:
        //   tuple = (1, 2, 3)             → List.of(1, 2, 3)
        //   frozenset({1, 2, 3})          → Set.of(1, 2, 3)
        //   MappingProxyType({"a": 1})    → Map.of("a", 1)
        //
        List<String> immutableList = List.of("A", "B", "C");
        Set<Integer> immutableSet = Set.of(1, 2, 3);
        Map<String, Integer> immutableMap = Map.of("x", 1, "y", 2);

        System.out.println("Immutable List: " + immutableList);
        System.out.println("Immutable Set:  " + immutableSet);
        System.out.println("Immutable Map:  " + immutableMap);

        try {
            immutableList.add("D");  // UnsupportedOperationException!
        } catch (UnsupportedOperationException e) {
            System.out.println("Cannot modify immutable: " + e.getClass().getSimpleName());
        }

        System.out.println("\n=== ITERATION PATTERNS ===");

        List<String> cities = List.of("Tokyo", "Paris", "NYC", "London");

        // Pattern 1: Enhanced for loop (most common)
        System.out.print("Enhanced for: ");
        for (String city : cities) { System.out.print(city + " "); }
        System.out.println();

        // Pattern 2: forEach with lambda
        System.out.print("forEach:      ");
        cities.forEach(city -> System.out.print(city + " "));
        System.out.println();

        // Pattern 3: Iterator (needed for safe removal)
        //
        // ITERATOR FLOW:
        //   ┌───────────────────────────────────┐
        //   │  hasNext()? → YES → next() → use  │──┐
        //   │              NO  → STOP            │  │
        //   └───────────────────────────────────┘  │
        //             ▲                             │
        //             └─────────────────────────────┘
        //
        List<String> mutable = new ArrayList<>(cities);
        Iterator<String> it = mutable.iterator();
        while (it.hasNext()) {
            String city = it.next();
            if (city.length() <= 3) {
                it.remove();  // safe removal during iteration
            }
        }
        System.out.println("After removing short names: " + mutable);

        // Pattern 4: removeIf (Java 8+ — cleaner than iterator)
        List<String> mutable2 = new ArrayList<>(cities);
        mutable2.removeIf(city -> city.length() <= 3);
        System.out.println("removeIf result (same): " + mutable2);

        System.out.println("\n=== COLLECTIONS UTILITY CLASS ===");

        List<Integer> numbers = new ArrayList<>(List.of(5, 3, 8, 1, 9, 2));
        System.out.println("Original: " + numbers);

        Collections.sort(numbers);
        System.out.println("Sorted:   " + numbers);

        Collections.reverse(numbers);
        System.out.println("Reversed: " + numbers);

        System.out.println("Max: " + Collections.max(numbers));
        System.out.println("Min: " + Collections.min(numbers));
        System.out.println("Frequency of 5: " + Collections.frequency(numbers, 5));

        // Wrap as unmodifiable (defensive copy pattern)
        List<Integer> readOnly = Collections.unmodifiableList(numbers);
        System.out.println("Unmodifiable view: " + readOnly);
    }
}
