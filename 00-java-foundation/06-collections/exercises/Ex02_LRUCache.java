/**
 * ====================================================================
 * FILE    : Ex02_LRUCache.java
 * MODULE  : 06 — Collections
 * PURPOSE : Build an LRU Cache using LinkedHashMap
 * ====================================================================
 *
 * LRU (Least Recently Used) Cache:
 *   - Fixed capacity
 *   - When full, evict the LEAST recently accessed entry
 *   - O(1) get and put operations
 *
 * HOW LinkedHashMap ENABLES LRU:
 *
 *   LinkedHashMap(capacity, loadFactor, accessOrder=true)
 *   → Entries re-ordered on every get() (most recent → tail)
 *   → Override removeEldestEntry() → auto-evict when size > max
 *
 * VISUALIZATION:
 *
 *   Cache capacity = 3
 *
 *   put(A,1): [A:1]
 *   put(B,2): [A:1, B:2]
 *   put(C,3): [A:1, B:2, C:3]  ← FULL
 *   
 *   get(A):   [B:2, C:3, A:1]  ← A moved to tail (most recent)
 *   
 *   put(D,4): [C:3, A:1, D:4]  ← B evicted (was head = least recent)
 *             ^^^^^^
 *             B was least recently used → EVICTED
 *
 * ====================================================================
 */
import java.util.*;

public class Ex02_LRUCache {

    /**
     * Implement an LRU cache using LinkedHashMap.
     *
     * Requirements:
     *   - Constructor takes maxCapacity
     *   - get(key) → returns value or -1 if not found
     *   - put(key, value) → inserts/updates, evicts LRU if full
     *   - Both operations are O(1)
     *
     * HINT: Extend LinkedHashMap and override removeEldestEntry():
     *
     *   new LinkedHashMap<K, V>(capacity, 0.75f, true) {
     *       @Override
     *       protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
     *           return size() > maxCapacity;
     *       }
     *   };
     *
     * Python equivalent:
     *   from functools import lru_cache  (but for function results)
     *   or: from collections import OrderedDict
     *       class LRU(OrderedDict):
     *           def __getitem__(self, key):
     *               self.move_to_end(key)
     *               return super().__getitem__(key)
     */

    // TODO: Implement LRUCache class

    // BONUS: Implement without using LinkedHashMap
    //   - Use HashMap + doubly-linked list
    //   - HashMap for O(1) lookup
    //   - Doubly-linked list for O(1) insertion/removal order
    //
    //   ┌──────────────────────────────────────────────┐
    //   │  HashMap<K, Node<K,V>>                       │
    //   │    ↓ maps keys to linked list nodes           │
    //   │                                              │
    //   │  HEAD ←→ [B:2] ←→ [C:3] ←→ [A:1] ←→ TAIL  │
    //   │  LRU ←──────────────────────────── MRU      │
    //   └──────────────────────────────────────────────┘

    public static void main(String[] args) {
        System.out.println("=== LRU Cache Exercise ===\n");

        // TODO: Create LRU cache with capacity 3
        // TODO: Put entries: (1,"A"), (2,"B"), (3,"C")
        // TODO: Get key 1 (moves to most recent)
        // TODO: Put (4,"D") — should evict key 2  
        // TODO: Get key 2 — should return -1 (evicted)
        // TODO: Print cache state after each operation

        System.out.println("Implement the LRU Cache above!");
    }
}
