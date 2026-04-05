/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_LRUCache.java                                    ║
 * ║  MODULE : 00-java-foundation / 06-collections                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — LRU Cache via LinkedHashMap         ║
 * ║  DEMONSTRATES   : access-order mode, removeEldestEntry, O(1)    ║
 * ║  PYTHON COMPARE : OrderedDict move_to_end vs Java access-order  ║
 * ║                                                                  ║
 * ║  LRU EVICTION VISUAL:                                            ║
 * ║   put(A) put(B) put(C): [A:head] ←→ [B] ←→ [C:tail]            ║
 * ║   get(A) → A moves to tail: [B] ←→ [C] ←→ [A:MRU]              ║
 * ║   put(D) → B evicted (head=LRU): [C] ←→ [A] ←→ [D:MRU]        ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.*;

/**
 * LRU (Least Recently Used) Cache with O(1) get and put.
 *
 * <p>Implementation 1 uses {@code LinkedHashMap} with access-order mode —
 * the JDK does the heavy lifting. Implementation 2 (Bonus) shows the
 * manual doubly-linked list + HashMap approach used in interviews.
 *
 * <p>WHY LinkedHashMap works: in access-order mode ({@code accessOrder=true}),
 * every {@code get()} and {@code put()} moves the accessed entry to the TAIL
 * of the internal doubly-linked list. The HEAD is always the least recently
 * used entry. {@code removeEldestEntry()} is called after every {@code put()}
 * — returning {@code true} evicts the head (LRU).
 *
 * <p>Python equivalent:
 * <pre>{@code
 *   from collections import OrderedDict
 *   class LRU(OrderedDict):
 *       def __getitem__(self, key):
 *           self.move_to_end(key)
 *           return super().__getitem__(key)
 * }</pre>
 */
class LRUCache {

    // WHY maxCapacity stored separately: LinkedHashMap doesn't expose it,
    // so removeEldestEntry needs to compare size() against it
    private final int maxCapacity;
    private final Map<Integer, Integer> cache;

    /**
     * Creates an LRU cache with the given capacity.
     *
     * @param maxCapacity maximum number of entries before eviction
     */
    public LRUCache(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        // WHY anonymous subclass: to override removeEldestEntry().
        // LinkedHashMap itself doesn't expose a public setter for this behavior.
        // Constructor args: initialCapacity, loadFactor, accessOrder(true=LRU)
        this.cache = new LinkedHashMap<>(maxCapacity, 0.75f, true) {
            /**
             * Called after every put(). Returns true when capacity exceeded
             * — LinkedHashMap automatically removes the eldest (head = LRU) entry.
             */
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                return size() > maxCapacity;
            }
        };
    }

    /**
     * Returns the value for the given key, or -1 if not present.
     *
     * <p>WHY getOrDefault (not get): map.get(key) returns null for absent keys.
     * Since values are Integer (reference type), null is ambiguous — it could
     * mean "not found" or a null value. -1 is our sentinel for "not found".
     *
     * <p>In access-order mode, get() also moves the entry to the tail (MRU position).
     *
     * @param key the cache key
     * @return cached value, or -1 if absent
     */
    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    /**
     * Inserts or updates a key-value pair.
     *
     * <p>If the cache is full after this put, {@code removeEldestEntry} evicts
     * the head (least recently used). put() also moves the entry to MRU position.
     *
     * @param key   the cache key
     * @param value the value to cache
     */
    public void put(int key, int value) {
        cache.put(key, value);
    }

    /** Returns the current number of cached entries. */
    public int size() {
        return cache.size();
    }

    /** Returns a snapshot of the cache for debugging (shows LRU→MRU order). */
    @Override
    public String toString() {
        return "LRUCache" + cache.toString();
    }
}

// ── Bonus: Manual Implementation ────────────────────────────────────

/**
 * LRU Cache using a HashMap + doubly-linked list.
 *
 * <p>This is the typical interview implementation expected at FAANG.
 * Demonstrates the same O(1) guarantees without relying on LinkedHashMap.
 *
 * <p>Architecture:
 * <pre>
 *   HashMap: key → Node (O(1) lookup)
 *   DLL:     HEAD ←→ [LRU] ←→ ... ←→ [MRU] ←→ TAIL
 *            Sentinel nodes avoid null checks at boundaries
 * </pre>
 */
class LRUCacheManual {

    /** Doubly-linked list node. */
    private static class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    // WHY sentinel head/tail: eliminates null checks at list boundaries.
    // head.next = LRU (oldest), tail.prev = MRU (newest).
    private final Node head = new Node(0, 0); // sentinel LRU end
    private final Node tail = new Node(0, 0); // sentinel MRU end

    /**
     * Creates a manual LRU cache.
     *
     * @param capacity maximum entries before eviction
     */
    public LRUCacheManual(int capacity) {
        this.capacity = capacity;
        // WHY link head ↔ tail: sentinel nodes always connected;
        // real nodes inserted between them
        head.next = tail;
        tail.prev = head;
    }

    /** Returns value for key, or -1 if absent. Moves node to MRU position. */
    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToTail(node); // WHY: mark as recently used
        return node.value;
    }

    /** Inserts/updates key. Evicts LRU if over capacity. */
    public void put(int key, int value) {
        Node existing = map.get(key);
        if (existing != null) {
            existing.value = value;
            moveToTail(existing); // WHY: update makes it most recently used
            return;
        }
        Node node = new Node(key, value);
        map.put(key, node);
        addToTail(node); // WHY: new entry is MRU

        if (map.size() > capacity) {
            // WHY head.next: sentinel head's next is the least recently used node
            Node lru = head.next;
            removeNode(lru);
            map.remove(lru.key); // WHY also remove from map: O(1) cleanup
        }
    }

    /** Removes a node from the doubly-linked list. */
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /** Inserts a node just before the tail sentinel (MRU position). */
    private void addToTail(Node node) {
        node.prev = tail.prev;
        node.next = tail;
        tail.prev.next = node;
        tail.prev = node;
    }

    /** Moves an existing node to the MRU position. */
    private void moveToTail(Node node) {
        removeNode(node);
        addToTail(node);
    }
}

/**
 * Test harness for both LRU cache implementations.
 */
public class Sol02_LRUCache {

    /**
     * Demonstrates LRU eviction behavior.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== LRU Cache Solution ===\n");

        System.out.println("--- LinkedHashMap Implementation ---");
        LRUCache cache = new LRUCache(3);

        cache.put(1, 10);
        cache.put(2, 20);
        cache.put(3, 30);
        System.out.println("After put(1,10) put(2,20) put(3,30): " + cache);

        System.out.println("get(1) = " + cache.get(1)); // 10 — moves 1 to MRU
        System.out.println("After get(1): " + cache); // 2 is now LRU

        cache.put(4, 40); // should evict 2 (LRU)
        System.out.println("After put(4,40): " + cache);
        System.out.println("get(2) = " + cache.get(2)); // -1 — evicted

        System.out.println("get(3) = " + cache.get(3)); // 30
        System.out.println("get(4) = " + cache.get(4)); // 40
        System.out.println("get(1) = " + cache.get(1)); // 10

        System.out.println("\n--- Manual DLL+HashMap Implementation ---");
        LRUCacheManual manual = new LRUCacheManual(3);
        manual.put(1, 10);
        manual.put(2, 20);
        manual.put(3, 30);
        System.out.println("get(1) = " + manual.get(1)); // 10
        manual.put(4, 40);                                // evicts 2
        System.out.println("get(2) = " + manual.get(2)); // -1 (evicted)
        System.out.println("get(3) = " + manual.get(3)); // 30
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using insertionOrder (default) instead of accessOrder
 *   WRONG: new LinkedHashMap<>(cap, 0.75f)         // insertion-order — NOT LRU
 *   RIGHT: new LinkedHashMap<>(cap, 0.75f, true)   // access-order — LRU
 *   The third boolean argument is the critical difference.
 *
 * MISTAKE 2: Forgetting removeEldestEntry() override
 *   Without it, LinkedHashMap grows unboundedly even in access-order mode.
 *   The override is what triggers automatic eviction.
 *
 * MISTAKE 3: maxCapacity not accessible in removeEldestEntry
 *   removeEldestEntry is an instance method of the anonymous subclass.
 *   It CAN access outer class fields (maxCapacity) via closure.
 *   Don't make maxCapacity a local variable — it must be effectively final.
 *
 * MISTAKE 4: Manual impl — removing from map but not from list (memory leak)
 *   When evicting, both removeNode(lru) AND map.remove(lru.key) are needed.
 *   Forgetting map.remove means the HashMap grows without bound.
 *
 * MISTAKE 5: Thread safety
 *   Both implementations are NOT thread-safe.
 *   For concurrent use: wrap with Collections.synchronizedMap() or
 *   use Caffeine cache library (production-grade LRU with concurrency support).
 * ═══════════════════════════════════════════════════════════════════ */
