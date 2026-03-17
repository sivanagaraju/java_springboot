/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║      CONCURRENT COLLECTIONS DEMO — Thread-Safe Structures   ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Demonstrates:                                              ║
 * ║  1. ConcurrentHashMap atomic operations                     ║
 * ║  2. Why HashMap fails under concurrency                     ║
 * ║  3. CopyOnWriteArrayList for read-heavy workloads          ║
 * ║  4. BlockingQueue for producer-consumer pattern             ║
 * ║  5. ConcurrentSkipListMap for sorted concurrent access     ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ConcurrentCollectionsDemo {

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 1: HashMap Fails Under Concurrency          │
    // └──────────────────────────────────────────────────────┘

    /**
     * HashMap race condition:
     *
     * Thread 1: put("A", 1)         Thread 2: put("B", 2)
     *      │                              │
     *      ▼                              ▼
     *   size=1000                      size=1000
     *   hash("A")=bucket[3]           hash("B")=bucket[3]
     *   ┌───┬───┬───┬───┬───┐
     *   │   │   │   │ ? │   │  ← both write same bucket!
     *   └───┴───┴───┴───┴───┘
     *   size → 1001                    size → 1001  ← LOST UPDATE!
     *
     * Expected: 2000 entries → Actual: ~1850 (lost entries)
     */
    static void demonstrateHashMapFailure() throws InterruptedException {
        System.out.println("═══ HashMap Failure Under Concurrency ═══");

        Map<Integer, Integer> unsafeMap = new HashMap<>();
        int threadCount = 10;
        int insertsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            new Thread(() -> {
                for (int i = 0; i < insertsPerThread; i++) {
                    unsafeMap.put(threadId * insertsPerThread + i, i);
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        System.out.println("HashMap size: " + unsafeMap.size()
            + " (expected " + (threadCount * insertsPerThread)
            + ", likely LESS due to race condition)");

        // ✅ ConcurrentHashMap — always correct
        Map<Integer, Integer> safeMap = new ConcurrentHashMap<>();
        CountDownLatch latch2 = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            new Thread(() -> {
                for (int i = 0; i < insertsPerThread; i++) {
                    safeMap.put(threadId * insertsPerThread + i, i);
                }
                latch2.countDown();
            }).start();
        }

        latch2.await();
        System.out.println("ConcurrentHashMap size: " + safeMap.size()
            + " (expected " + (threadCount * insertsPerThread) + ")");
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 2: ConcurrentHashMap Atomic Operations      │
    // └──────────────────────────────────────────────────────┘

    /**
     * ConcurrentHashMap atomic operations vs check-then-act:
     *
     * ❌ Non-atomic (race condition):
     *   if (!map.containsKey(k))    Thread 2 inserts here!
     *       map.put(k, v);          Overwritten!
     *
     * ✅ Atomic (single operation):
     *   map.putIfAbsent(k, v);      Cannot be interrupted
     *   map.compute(k, remapper);   Read + transform + write atomically
     *   map.merge(k, v, remapper);  Combine old + new atomically
     */
    static void demonstrateAtomicOperations() throws InterruptedException {
        System.out.println("\n═══ ConcurrentHashMap Atomic Operations ═══");

        ConcurrentHashMap<String, AtomicInteger> wordCount =
            new ConcurrentHashMap<>();

        String[] words = {
            "hello", "world", "hello", "java", "world", "hello",
            "concurrent", "java", "hello", "world"
        };

        // ── putIfAbsent + increment ──
        System.out.println("--- Word Frequency Counter ---");
        for (String word : words) {
            wordCount.putIfAbsent(word, new AtomicInteger(0));
            wordCount.get(word).incrementAndGet();
        }
        wordCount.forEach((word, count) ->
            System.out.println("  " + word + ": " + count));

        // ── compute: atomic read-modify-write ──
        ConcurrentHashMap<String, Integer> scores = new ConcurrentHashMap<>();
        scores.put("Alice", 80);

        // Multiple threads safely updating same key
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                scores.compute("Alice", (key, val) -> val + 1);
                latch.countDown();
            }).start();
        }
        latch.await();
        System.out.println("Alice score after 10 increments: " + scores.get("Alice")
            + " (expected 90)");

        // ── merge: combine values ──
        ConcurrentHashMap<String, Integer> inventory = new ConcurrentHashMap<>();
        inventory.merge("apples", 5, Integer::sum);   // apples = 5
        inventory.merge("apples", 3, Integer::sum);   // apples = 8
        inventory.merge("bananas", 2, Integer::sum);   // bananas = 2
        System.out.println("Inventory: " + inventory);
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 3: CopyOnWriteArrayList                     │
    // └──────────────────────────────────────────────────────┘

    /**
     * CopyOnWriteArrayList — snapshot iteration.
     *
     * State: [A, B, C]
     *           │
     * Iterator starts ──────→ sees snapshot [A, B, C]
     *           │
     * add("D") copies → [A, B, C, D]  (new array)
     *           │
     * Iterator still sees [A, B, C]  ← no ConcurrentModificationException!
     *           │
     * New iterator sees [A, B, C, D]
     *
     * ✅ Perfect for event listeners (add/remove rare, notify frequent)
     * ❌ Terrible for large lists with frequent writes
     */
    static void demonstrateCopyOnWriteArrayList() {
        System.out.println("\n═══ CopyOnWriteArrayList ═══");

        CopyOnWriteArrayList<String> listeners = new CopyOnWriteArrayList<>();
        listeners.add("Listener-1");
        listeners.add("Listener-2");
        listeners.add("Listener-3");

        // Safe to modify during iteration — NO ConcurrentModificationException
        System.out.println("--- Safe iteration during modification ---");
        for (String listener : listeners) {
            System.out.println("  Notifying: " + listener);
            if (listener.equals("Listener-2")) {
                listeners.add("Listener-4");  // Safe! Iterator sees snapshot
                System.out.println("  (added Listener-4 during iteration)");
            }
        }
        System.out.println("After iteration, list: " + listeners);

        // Compare: ArrayList would throw ConcurrentModificationException
        System.out.println("\n--- ArrayList comparison ---");
        List<String> arrayList = new ArrayList<>(List.of("A", "B", "C"));
        try {
            for (String item : arrayList) {
                if (item.equals("B")) arrayList.add("D");  // BOOM!
            }
        } catch (ConcurrentModificationException e) {
            System.out.println("  ArrayList threw ConcurrentModificationException!");
        }
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 4: BlockingQueue — Producer-Consumer        │
    // └──────────────────────────────────────────────────────┘

    /**
     * BlockingQueue producer-consumer pattern:
     *
     * ┌──────────┐   put()   ┌──────────────┐   take()   ┌──────────┐
     * │ Producer ├──────────→│ [E1][E2][E3] │──────────→│ Consumer │
     * └──────────┘           └──────────────┘           └──────────┘
     *                         capacity = 5
     *
     * put() BLOCKS if queue is full  (back-pressure!)
     * take() BLOCKS if queue is empty (waits for work!)
     *
     * This is the foundation of:
     * - ThreadPoolExecutor's work queue
     * - Spring's @Async task execution
     * - Message-driven architectures
     */
    static void demonstrateBlockingQueue() throws InterruptedException {
        System.out.println("\n═══ BlockingQueue Producer-Consumer ═══");

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);

        // Producer thread
        Thread producer = new Thread(() -> {
            String[] tasks = {"Task-A", "Task-B", "Task-C", "Task-D", "DONE"};
            for (String task : tasks) {
                try {
                    System.out.println("  [Producer] Putting: " + task);
                    queue.put(task);  // blocks if queue full
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String task = queue.take();  // blocks if queue empty
                    if ("DONE".equals(task)) break;
                    System.out.println("  [Consumer] Processing: " + task);
                    Thread.sleep(200);  // simulate work
                }
                System.out.println("  [Consumer] Received DONE signal, stopping.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 5: ConcurrentSkipListMap — Sorted + Safe   │
    // └──────────────────────────────────────────────────────┘

    /**
     * ConcurrentSkipListMap = ConcurrentHashMap + TreeMap
     *
     * Skip list levels (probabilistic balancing):
     *
     * Level 2: ──────→ 20 ────────────→ 50 ──→ NIL
     * Level 1: ──→ 10 → 20 ────→ 30 ──→ 50 ──→ NIL
     * Level 0: 5 → 10 → 20 → 25 → 30 → 50 → 60 → NIL
     *
     * O(log n) for get, put, remove
     * No global lock — uses CAS at each level
     * Maintains sorted order + thread safety
     */
    static void demonstrateConcurrentSkipListMap() {
        System.out.println("\n═══ ConcurrentSkipListMap ═══");

        ConcurrentSkipListMap<String, Integer> sortedMap =
            new ConcurrentSkipListMap<>();

        sortedMap.put("Charlie", 78);
        sortedMap.put("Alice", 95);
        sortedMap.put("Eve", 88);
        sortedMap.put("Bob", 82);
        sortedMap.put("Dave", 91);

        // Always sorted by key
        System.out.println("Sorted entries:");
        sortedMap.forEach((name, score) ->
            System.out.println("  " + name + ": " + score));

        // NavigableMap operations (like TreeMap)
        System.out.println("First: " + sortedMap.firstEntry());
        System.out.println("Last:  " + sortedMap.lastEntry());
        System.out.println("Sub-map [Bob..Dave]: " +
            sortedMap.subMap("Bob", true, "Dave", true));
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  MAIN — Run All Demos                                │
    // └──────────────────────────────────────────────────────┘

    public static void main(String[] args) throws Exception {
        demonstrateHashMapFailure();
        demonstrateAtomicOperations();
        demonstrateCopyOnWriteArrayList();
        demonstrateBlockingQueue();
        demonstrateConcurrentSkipListMap();
    }
}
