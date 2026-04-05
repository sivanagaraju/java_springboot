/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_ProducerConsumer.java                            ║
 * ║  MODULE : 00-java-foundation / 08-multithreading                 ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — producer-consumer with BlockingQueue║
 * ║  DEMONSTRATES   : synchronized + wait/notify, BlockingQueue,     ║
 * ║                   volatile, graceful shutdown with poison pill   ║
 * ║  PYTHON COMPARE : queue.Queue vs BlockingQueue; threading.Event  ║
 * ║                                                                  ║
 * ║  PATTERN:                                                        ║
 * ║   Producer ──put──▶ [BoundedBuffer] ──take──▶ Consumer          ║
 * ║   Producer WAITS if full; Consumer WAITS if empty               ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// ── Implementation 1: Manual synchronized + wait/notify ─────────────

/**
 * A thread-safe bounded buffer using Java's low-level wait/notify mechanism.
 *
 * <p>WHY while (not if) for wait(): When a thread wakes from wait(), another
 * thread may have already changed the condition. Using {@code if} creates a
 * "spurious wakeup" bug — the thread proceeds even though the condition
 * is still false. {@code while} re-checks the condition after every wakeup.
 *
 * <p>Python equivalent:
 * <pre>{@code
 *   import queue
 *   buffer = queue.Queue(maxsize=4)  # blocks on full put() / empty get()
 * }</pre>
 *
 * @param <T> the type of items in the buffer
 */
class BoundedBuffer<T> {

    private final int capacity;
    private final LinkedList<T> queue = new LinkedList<>();

    /** Creates a bounded buffer with the given maximum capacity. */
    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Adds an item to the buffer, blocking until space is available.
     *
     * <p>WHY synchronized: protects the queue and the wait/notify coordination.
     * Without synchronized, wait() would throw IllegalMonitorStateException.
     *
     * @param item the item to add
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized void put(T item) throws InterruptedException {
        // WHY while (not if): re-check condition after each wakeup
        // A spurious wakeup or another producer filling the slot would
        // cause if() to proceed on a full buffer.
        while (queue.size() >= capacity) {
            wait(); // WHY wait(): releases lock AND suspends thread atomically
        }
        queue.addLast(item);
        notifyAll(); // WHY notifyAll (not notify): wake ALL waiting consumers
        // notify() might wake another producer instead of a consumer — deadlock risk
    }

    /**
     * Removes and returns an item, blocking until one is available.
     *
     * @return the next item from the buffer
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.removeFirst();
        notifyAll(); // WHY notify producers: space is now available
        return item;
    }

    /** Returns the current number of items in the buffer. */
    public synchronized int size() {
        return queue.size();
    }
}

// ── Implementation 2: BlockingQueue (production-grade) ──────────────

/**
 * Demonstrates the same pattern using {@code ArrayBlockingQueue} —
 * the preferred approach in production code.
 *
 * <p>{@code ArrayBlockingQueue} handles all the synchronized/wait/notify
 * internally. We just call {@code put()} and {@code take()} — same semantics
 * as the manual {@code BoundedBuffer} above, but battle-tested and optimized.
 */

// ── Test Harness ────────────────────────────────────────────────────

/**
 * Runs a producer-consumer scenario with both implementations.
 */
public class Sol01_ProducerConsumer {

    // WHY volatile: running flag is read by multiple threads.
    // volatile ensures each thread sees the latest value — no thread-local caching.
    private static volatile boolean running = true;

    /**
     * Demonstrates the manual BoundedBuffer and BlockingQueue implementations.
     *
     * @param args unused
     * @throws InterruptedException if main thread is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Producer-Consumer Solutions ===\n");

        System.out.println("--- Manual BoundedBuffer (synchronized + wait/notify) ---");
        runWithBoundedBuffer();

        System.out.println("\n--- BlockingQueue (production-grade) ---");
        runWithBlockingQueue();
    }

    /**
     * Producer-consumer demo using the manual BoundedBuffer.
     *
     * @throws InterruptedException if interrupted
     */
    private static void runWithBoundedBuffer() throws InterruptedException {
        BoundedBuffer<String> buffer = new BoundedBuffer<>(4);
        running = true;

        // WHY Runnable lambda: concise way to define thread behavior
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 8 && running; i++) {
                try {
                    String item = "Item-" + i;
                    buffer.put(item);
                    System.out.println("  [Producer] put: " + item
                            + " (buffer size: " + buffer.size() + ")");
                    Thread.sleep(50); // WHY: simulate production time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // WHY restore interrupted status
                }
            }
            running = false;
        }, "Producer");

        Thread consumer = new Thread(() -> {
            while (running || buffer.size() > 0) {
                try {
                    // WHY check running && size: drain remaining items after producer stops
                    if (buffer.size() > 0) {
                        String item = buffer.take();
                        System.out.println("  [Consumer] took: " + item);
                        Thread.sleep(100); // WHY slower consumer: demonstrates blocking
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        producer.join(); // WHY join: wait for producer to finish
        Thread.sleep(500); // WHY: give consumer time to drain remaining items
        consumer.interrupt(); // WHY interrupt: signal consumer to stop
        consumer.join();
        System.out.println("  ✓ BoundedBuffer demo complete");
    }

    /**
     * Producer-consumer demo using {@code ArrayBlockingQueue}.
     *
     * <p>Demonstrates the "poison pill" shutdown pattern — a sentinel value
     * that signals the consumer to stop gracefully.
     *
     * @throws InterruptedException if interrupted
     */
    private static void runWithBlockingQueue() throws InterruptedException {
        // WHY ArrayBlockingQueue(4): bounded, fair=false (default) — higher throughput
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(4);
        final String POISON_PILL = "STOP"; // WHY sentinel: clean shutdown without volatile flag

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    String item = "Task-" + i;
                    queue.put(item); // WHY put(): blocks if full — automatic backpressure
                    System.out.println("  [Producer] produced: " + item);
                    Thread.sleep(30);
                }
                queue.put(POISON_PILL); // WHY: signal consumer to terminate
                System.out.println("  [Producer] sent poison pill");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "BQ-Producer");

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String item = queue.take(); // WHY take(): blocks if empty — no busy-waiting
                    if (POISON_PILL.equals(item)) {
                        System.out.println("  [Consumer] received poison pill, stopping");
                        break; // WHY break: clean exit on sentinel
                    }
                    System.out.println("  [Consumer] consumed: " + item);
                    Thread.sleep(60);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "BQ-Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        System.out.println("  ✓ BlockingQueue demo complete");
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using if() instead of while() for wait()
 *   WRONG: if (queue.isFull()) wait();
 *   PROBLEM: Spurious wakeups — thread wakes without notify, or another
 *            producer fills the slot between notify and this thread running.
 *   RIGHT: while (queue.isFull()) wait();  Always re-check condition.
 *
 * MISTAKE 2: Using notify() instead of notifyAll()
 *   WRONG: notify();
 *   PROBLEM: notify() wakes ONE waiting thread — it might wake another producer
 *            instead of a consumer, causing deadlock if all consumers are waiting.
 *   RIGHT: notifyAll() — safer, wakes all waiters; each re-checks their condition.
 *
 * MISTAKE 3: Calling wait() outside synchronized block
 *   WRONG: buffer.wait();  // without synchronized(buffer)
 *   RESULT: IllegalMonitorStateException at runtime.
 *   RIGHT: synchronized(this) { while(cond) wait(); }
 *
 * MISTAKE 4: Not restoring interrupted status after catching InterruptedException
 *   WRONG: catch (InterruptedException e) { /* ignore *\/ }
 *   PROBLEM: Clears the interrupted flag — callers can't detect the interrupt.
 *   RIGHT: Thread.currentThread().interrupt(); // re-set the flag
 *
 * MISTAKE 5: Not using poison pill for clean shutdown
 *   Setting a volatile flag works but has a race: consumer may not check it
 *   before blocking on take(). Poison pill is simpler — consumer sees it
 *   in-band as a regular message, no race condition.
 * ═══════════════════════════════════════════════════════════════════ */
