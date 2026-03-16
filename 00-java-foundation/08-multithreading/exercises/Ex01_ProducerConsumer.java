/**
 * ====================================================================
 * FILE    : Ex01_ProducerConsumer.java
 * MODULE  : 08 — Multithreading
 * PURPOSE : Classic concurrent pattern — producer/consumer
 * ====================================================================
 *
 * EXERCISES:
 *
 *   Implement the classic Producer-Consumer pattern using a shared
 *   bounded buffer.
 *
 *   1. Implement BoundedBuffer<T>:
 *      - Fixed-capacity queue (use LinkedList internally)
 *      - put(item): adds item, BLOCKS if buffer is full
 *      - take():    removes item, BLOCKS if buffer is empty
 *      - Thread-safe using synchronized + wait/notify
 *
 *   2. Create Producer thread:
 *      - Generates items (e.g., "Item-1", "Item-2", ...)
 *      - Puts each into the buffer
 *      - Sleeps briefly between items
 *
 *   3. Create Consumer thread:
 *      - Takes items from buffer
 *      - Processes each (e.g., prints it)
 *      - Sleeps briefly between items
 *
 * PATTERN DIAGRAM:
 *
 *   ┌──────────┐     ┌─────────────────────┐     ┌──────────┐
 *   │ Producer │────▶│  Bounded Buffer     │────▶│ Consumer │
 *   │          │ put │  [__][__][__][__]    │take │          │
 *   │ (Thread) │     │  capacity = 4        │     │ (Thread) │
 *   └──────────┘     └─────────────────────┘     └──────────┘
 *
 *   Rules:
 *     - Producer WAITS if buffer is full
 *     - Consumer WAITS if buffer is empty
 *     - Key pattern: while(condition) wait() → NOT if(condition) wait()
 *
 * WAIT/NOTIFY MECHANISM:
 *
 *   synchronized(buffer) {
 *       while (buffer.isFull()) {
 *           buffer.wait();    ← release lock, sleep until notified
 *       }
 *       buffer.add(item);
 *       buffer.notifyAll();   ← wake up waiting consumers
 *   }
 *
 * BONUS: Implement using java.util.concurrent.BlockingQueue
 *        (ArrayBlockingQueue handles all synchronization for you!)
 *
 * ====================================================================
 */
import java.util.*;

public class Ex01_ProducerConsumer {

    // TODO: Implement a thread-safe bounded buffer
    static class BoundedBuffer<T> {
        private final int capacity;
        private final LinkedList<T> queue = new LinkedList<>();

        public BoundedBuffer(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void put(T item) throws InterruptedException {
            // TODO: wait while full, then add and notifyAll
        }

        public synchronized T take() throws InterruptedException {
            // TODO: wait while empty, then remove and notifyAll
            return null;
        }

        public synchronized int size() {
            return queue.size();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BoundedBuffer<String> buffer = new BoundedBuffer<>(4);

        // TODO: Create and start Producer thread
        // TODO: Create and start Consumer thread
        // TODO: Let them run for a few seconds, then stop

        System.out.println("=== Producer-Consumer Exercise ===");
        System.out.println("Implement BoundedBuffer, Producer, and Consumer threads.");
        System.out.println("See the comments above for detailed instructions.");
    }
}
