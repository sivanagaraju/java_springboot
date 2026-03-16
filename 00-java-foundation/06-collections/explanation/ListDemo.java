/**
 * ====================================================================
 * FILE    : ListDemo.java
 * MODULE  : 06 — Collections
 * PURPOSE : ArrayList vs LinkedList — operations and performance
 * ====================================================================
 *
 * ARRAYLIST vs LINKEDLIST MEMORY:
 *
 *   ArrayList:
 *   ┌────────────────────────────────┐
 *   │  Object[] elementData          │
 *   │  ┌───┬───┬───┬───┬───┬───┐   │
 *   │  │ A │ B │ C │ D │   │   │   │  ← contiguous array (cache-friendly)
 *   │  └───┴───┴───┴───┴───┴───┘   │
 *   │  size=4   capacity=6          │
 *   └────────────────────────────────┘
 *
 *   LinkedList:
 *   ┌──────┐     ┌──────┐     ┌──────┐     ┌──────┐
 *   │prev:∅│◄────│      │◄────│      │◄────│      │
 *   │ "A"  │────▶│ "B"  │────▶│ "C"  │────▶│ "D"  │────▶∅
 *   └──────┘     └──────┘     └──────┘     └──────┘
 *    head                                    tail
 *   Each node ~48 bytes —— scattered in memory (cache-hostile)
 *
 * ====================================================================
 */
import java.util.*;

public class ListDemo {

    public static void main(String[] args) {

        System.out.println("=== ARRAYLIST BASICS ===");

        // ── Creation patterns ───────────────────────────────────────
        //
        // MEMORY AFTER new ArrayList<>():
        //   elementData = Object[10]  (default capacity = 10)
        //   size = 0
        //
        ArrayList<String> fruits = new ArrayList<>();
        fruits.add("Apple");     // [Apple, _, _, _, _, _, _, _, _, _]
        fruits.add("Banana");    // [Apple, Banana, _, _, _, _, _, _, _, _]
        fruits.add("Cherry");    // [Apple, Banana, Cherry, ...]
        System.out.println("fruits: " + fruits);
        System.out.println("size: " + fruits.size() + " (capacity is internal, not visible)");

        // ── Index-based operations: O(1) ────────────────────────────
        System.out.println("\nget(0): " + fruits.get(0));     // O(1) random access
        fruits.set(1, "Blueberry");                             // O(1) replace
        System.out.println("After set(1, Blueberry): " + fruits);

        // ── Insert at middle: O(n) shift ────────────────────────────
        //
        // BEFORE: [Apple, Blueberry, Cherry]
        // add(1, "Avocado"):
        //   Step 1: shift right from index 1
        //           [Apple, _, Blueberry, Cherry]
        //   Step 2: insert at index 1
        //           [Apple, Avocado, Blueberry, Cherry]
        //
        fruits.add(1, "Avocado");
        System.out.println("After add(1, Avocado): " + fruits);

        // ── Remove: O(n) shift ──────────────────────────────────────
        fruits.remove(0);  // shifts all elements left
        System.out.println("After remove(0): " + fruits);

        System.out.println("\n=== LINKEDLIST AS DEQUE ===");

        // ── LinkedList implements both List AND Deque ────────────────
        //
        // AFTER addFirst("X"), addLast("Z"):
        //   ┌───┐     ┌───┐     ┌───┐
        //   │ X │────▶│ A │────▶│ Z │
        //   └───┘     └───┘     └───┘
        //   head                 tail
        //
        LinkedList<String> deque = new LinkedList<>();
        deque.add("A");
        deque.addFirst("X");  // O(1) — add to head
        deque.addLast("Z");   // O(1) — add to tail
        System.out.println("Deque: " + deque);

        System.out.println("peekFirst: " + deque.peekFirst());  // see head without removing
        System.out.println("peekLast:  " + deque.peekLast());   // see tail without removing
        System.out.println("pollFirst: " + deque.pollFirst());  // remove from head
        System.out.println("After pollFirst: " + deque);

        System.out.println("\n=== PERFORMANCE COMPARISON ===");

        final int N = 100_000;

        // ── Test 1: Add to end (both are fast) ─────────────────────
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        long start = System.nanoTime();
        for (int i = 0; i < N; i++) arrayList.add(i);
        long arrayAddTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < N; i++) linkedList.add(i);
        long linkedAddTime = System.nanoTime() - start;

        System.out.println("Add " + N + " elements:");
        System.out.printf("  ArrayList:  %,d ns%n", arrayAddTime);
        System.out.printf("  LinkedList: %,d ns%n", linkedAddTime);

        // ── Test 2: Random access (ArrayList wins massively) ────────
        //
        // ArrayList: array[index] → O(1)
        // LinkedList: traverse from head/tail → O(n)
        //
        start = System.nanoTime();
        for (int i = 0; i < 10_000; i++) arrayList.get(i);
        long arrayGetTime = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < 10_000; i++) linkedList.get(i);
        long linkedGetTime = System.nanoTime() - start;

        System.out.println("Random access (10,000 gets):");
        System.out.printf("  ArrayList:  %,d ns%n", arrayGetTime);
        System.out.printf("  LinkedList: %,d ns  (%.0fx slower!)%n",
                linkedGetTime, (double) linkedGetTime / arrayGetTime);

        System.out.println("\n=== USEFUL LIST OPERATIONS ===");

        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob", "Alice"));

        // Sorting
        Collections.sort(names);
        System.out.println("Sorted: " + names);

        // Sublist (view, not copy!)
        List<String> sub = names.subList(1, 3);
        System.out.println("Sublist [1,3): " + sub);

        // Contains & indexOf
        System.out.println("Contains 'Bob': " + names.contains("Bob"));
        System.out.println("indexOf 'Alice': " + names.indexOf("Alice"));
        System.out.println("lastIndexOf 'Alice': " + names.lastIndexOf("Alice"));

        // Convert between List and Array
        String[] array = names.toArray(new String[0]);  // List → Array
        List<String> back = Arrays.asList(array);        // Array → fixed-size List
        List<String> mutable = new ArrayList<>(Arrays.asList(array));  // mutable copy
        System.out.println("Array↔List conversion works: " + mutable);
    }
}
