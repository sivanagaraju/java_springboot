/**
 * ====================================================================
 * FILE    : SynchronizationDemo.java
 * MODULE  : 08 — Multithreading
 * PURPOSE : Race conditions, synchronized, volatile, AtomicInteger
 * ====================================================================
 *
 * RACE CONDITION (counter++):
 *
 *   counter++ = READ → ADD → WRITE  (3 operations, not atomic!)
 *
 *   Thread A                Thread B
 *   ────────                ────────
 *   READ counter → 5
 *                           READ counter → 5  ← stale!
 *   ADD: 5 + 1 = 6
 *                           ADD: 5 + 1 = 6
 *   WRITE counter = 6
 *                           WRITE counter = 6  ← overwrites!
 *
 *   Expected: 7    Actual: 6    ← LOST UPDATE
 *
 * SOLUTIONS COMPARED:
 *
 *   ┌──────────────┬──────────┬──────────┬─────────────┐
 *   │              │ Visible? │ Atomic?  │ Performance │
 *   ├──────────────┼──────────┼──────────┼─────────────┤
 *   │ (none)       │    ✗     │    ✗     │ Fastest     │
 *   │ volatile     │    ✓     │    ✗     │ Fast        │
 *   │ synchronized │    ✓     │    ✓     │ Slowest     │
 *   │ Atomic*      │    ✓     │    ✓     │ Fast (CAS)  │
 *   └──────────────┴──────────┴──────────┴─────────────┘
 *
 * ====================================================================
 */
import java.util.concurrent.atomic.AtomicInteger;

public class SynchronizationDemo {

    // ── Shared counters for comparison ──────────────────────────────
    static int unsafeCounter = 0;
    static int syncCounter   = 0;
    static AtomicInteger atomicCounter = new AtomicInteger(0);

    static final Object lock = new Object();  // monitor object

    static final int INCREMENTS = 100_000;
    static final int THREADS    = 4;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== RACE CONDITION DEMO ===");
        System.out.println("Each test: " + THREADS + " threads × "
                + INCREMENTS + " increments = expected "
                + (THREADS * INCREMENTS) + "\n");

        // ── Test 1: No synchronization (BROKEN) ────────────────────
        //
        // WHAT HAPPENS:
        //   counter++ = READ → ADD → WRITE
        //   Threads interleave these 3 steps → lost updates
        //
        Thread[] unsafeThreads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) {
                    unsafeCounter++;  // NOT THREAD-SAFE!
                }
            });
            unsafeThreads[i].start();
        }
        for (Thread t : unsafeThreads) t.join();
        System.out.println("Unsafe:       " + unsafeCounter
                + " (wrong! lost ~"
                + ((THREADS * INCREMENTS) - unsafeCounter) + " increments)");

        // ── Test 2: synchronized block (CORRECT, but slow) ─────────
        //
        // HOW IT WORKS:
        //   ┌─────────────────────────────────────────────────┐
        //   │ synchronized(lock) {                             │
        //   │   // Only ONE thread executes this block         │
        //   │   syncCounter++;  // safe: read-add-write atomic│
        //   │ }                                                │
        //   │ lock released → next waiting thread enters       │
        //   └─────────────────────────────────────────────────┘
        //
        Thread[] syncThreads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            syncThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) {
                    synchronized (lock) {
                        syncCounter++;
                    }
                }
            });
            syncThreads[i].start();
        }
        for (Thread t : syncThreads) t.join();
        System.out.println("Synchronized: " + syncCounter + " (correct!)");

        // ── Test 3: AtomicInteger (CORRECT, and fast!) ──────────────
        //
        // HOW IT WORKS (CAS = Compare And Swap):
        //   1. Read current value (5)
        //   2. Computer new value (6)
        //   3. CAS: "if still 5, set to 6" → success!
        //      If another thread changed it → retry from step 1
        //   No locking needed → no blocking → better throughput
        //
        Thread[] atomicThreads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            atomicThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENTS; j++) {
                    atomicCounter.incrementAndGet();
                }
            });
            atomicThreads[i].start();
        }
        for (Thread t : atomicThreads) t.join();
        System.out.println("Atomic:       " + atomicCounter.get() + " (correct!)");

        System.out.println("\n=== VOLATILE DEMO ===");

        // ── volatile: visibility-only guarantee ─────────────────────
        //
        // WITHOUT volatile: Thread B might cache 'running' and never
        //   see Thread A set it to false (infinite loop!)
        //
        // WITH volatile: every read/write goes to MAIN MEMORY
        //
        class VolatileRunner implements Runnable {
            volatile boolean running = true;  // remove 'volatile' to see the bug!

            @Override
            public void run() {
                int iterations = 0;
                while (running) {
                    iterations++;
                }
                System.out.println("  Stopped after " + iterations + " iterations");
            }
        }

        VolatileRunner runner = new VolatileRunner();
        Thread volThread = new Thread(runner, "Volatile-Worker");
        volThread.start();

        Thread.sleep(100);
        runner.running = false;  // main thread signals stop
        volThread.join(1000);    // wait max 1 second

        if (volThread.isAlive()) {
            System.out.println("  ⚠️ Thread still running! (would happen without volatile)");
            volThread.interrupt();
        }

        System.out.println("\n=== DEADLOCK EXAMPLE ===");

        // ── Deadlock: 2 threads, 2 locks, circular dependency ───────
        //
        // Thread A: lock1 → lock2
        // Thread B: lock2 → lock1
        //
        // ┌──────────────────┐   waits   ┌──────────────────┐
        // │ Thread A         │───────────▶│ lock2 (held by B)│
        // │ holds lock1      │           │                  │
        // └──────────────────┘           └──────────────────┘
        //         ▲                              │
        //         │         waits                │
        //         │◀─────────────────────────────│
        // ┌──────────────────┐           ┌──────────────────┐
        // │ lock1 (held by A)│           │ Thread B         │
        // │                  │           │ holds lock2      │
        // └──────────────────┘           └──────────────────┘
        //
        // RESULT: Both wait FOREVER.
        //
        Object lockA = new Object();
        Object lockB = new Object();

        Thread deadA = new Thread(() -> {
            synchronized (lockA) {
                System.out.println("  [A] Holding lockA, waiting for lockB...");
                try { Thread.sleep(50); } catch (InterruptedException e) { return; }
                // In a real deadlock, this would block forever:
                // synchronized (lockB) { System.out.println("  [A] Got both!"); }
                System.out.println("  [A] (skipping lockB to avoid actual deadlock in demo)");
            }
        }, "Deadlock-A");

        Thread deadB = new Thread(() -> {
            synchronized (lockB) {
                System.out.println("  [B] Holding lockB, waiting for lockA...");
                try { Thread.sleep(50); } catch (InterruptedException e) { return; }
                // synchronized (lockA) { System.out.println("  [B] Got both!"); }
                System.out.println("  [B] (skipping lockA to avoid actual deadlock in demo)");
            }
        }, "Deadlock-B");

        deadA.start();
        deadB.start();
        deadA.join();
        deadB.join();

        System.out.println("\n  FIX: Always acquire locks in CONSISTENT ORDER (lockA → lockB)");
    }
}
