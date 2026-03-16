/**
 * ====================================================================
 * FILE    : ThreadBasicsDemo.java
 * MODULE  : 08 — Multithreading
 * PURPOSE : Thread creation, lifecycle, joining, and daemon threads
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: threading.Thread(target=fn).start()
 *   Java:   new Thread(() -> fn()).start()
 *   KEY DIFF: Python GIL = fake parallelism. Java = TRUE parallelism.
 *
 * THREAD LIFECYCLE:
 *
 *   ┌─────┐   start()   ┌──────────┐   schedule  ┌─────────┐
 *   │ NEW │────────────▶│ RUNNABLE │───────────▶│ RUNNING │
 *   └─────┘             └──────────┘            └────┬────┘
 *                             ▲                      │
 *                             │  notify/release      │
 *                             │                      ▼
 *                        ┌────────────┐         ┌─────────────┐
 *                        │  BLOCKED / │         │ TERMINATED  │
 *                        │  WAITING   │         │ (run done)  │
 *                        └────────────┘         └─────────────┘
 *
 * THREAD MEMORY MODEL:
 *
 *   ┌──────────────── SHARED HEAP ───────────────────┐
 *   │  Objects, arrays, fields                        │
 *   ├────────────┬────────────┬────────────┬──────────┤
 *   │  Thread-1  │  Thread-2  │  main      │  ...     │
 *   │  [Stack]   │  [Stack]   │  [Stack]   │          │
 *   │  locals    │  locals    │  locals    │          │
 *   └────────────┴────────────┴────────────┴──────────┘
 *
 * ====================================================================
 */
public class ThreadBasicsDemo {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=== THREAD CREATION: 3 WAYS ===");
        System.out.println("Main thread: " + Thread.currentThread().getName());

        // ── Way 1: Extend Thread (avoid in production) ──────────────
        class CounterThread extends Thread {
            @Override
            public void run() {
                for (int i = 1; i <= 3; i++) {
                    System.out.println("  [extends Thread] " + getName() + ": " + i);
                    try { Thread.sleep(100); } catch (InterruptedException e) { break; }
                }
            }
        }
        CounterThread t1 = new CounterThread();
        t1.setName("Counter-A");
        t1.start();

        // ── Way 2: Implement Runnable (preferred) ───────────────────
        Runnable task = () -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  [Runnable]       " 
                        + Thread.currentThread().getName() + ": " + i);
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        };
        Thread t2 = new Thread(task, "Counter-B");
        t2.start();

        // ── Way 3: Lambda (most concise) ────────────────────────────
        Thread t3 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  [Lambda]         " 
                        + Thread.currentThread().getName() + ": " + i);
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        }, "Counter-C");
        t3.start();

        // Wait for all 3 to finish before proceeding
        t1.join();
        t2.join();
        t3.join();
        System.out.println("All counter threads finished.\n");

        System.out.println("=== JOIN: Waiting for Threads ===");

        // ── join() blocks the calling thread until target finishes ──
        //
        // EXECUTION FLOW:
        //   main: start worker → continue → join (BLOCK) → resume
        //   worker:                start → work → work → DONE
        //                                                  ↑
        //                                          main unblocks here
        //
        Thread worker = new Thread(() -> {
            System.out.println("  Worker started...");
            try { Thread.sleep(500); } catch (InterruptedException e) { return; }
            System.out.println("  Worker finished!");
        }, "Worker");

        worker.start();
        System.out.println("Main waiting for worker (join)...");
        worker.join();  // main blocks here
        System.out.println("Main continues after worker done.\n");

        System.out.println("=== THREAD STATES ===");

        // ── Observing thread states ─────────────────────────────────
        Thread observed = new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) { return; }
        }, "Observed");

        System.out.println("Before start: " + observed.getState());  // NEW
        observed.start();
        System.out.println("After start:  " + observed.getState());  // RUNNABLE
        Thread.sleep(50);
        System.out.println("While sleep:  " + observed.getState());  // TIMED_WAITING
        observed.join();
        System.out.println("After join:   " + observed.getState());  // TERMINATED
        System.out.println();

        System.out.println("=== DAEMON THREADS ===");

        // ── Daemon threads die when all user threads finish ─────────
        //
        // USE CASE: background tasks (GC, logging, heartbeats)
        //
        // ┌──────────────────────────────────────────────────┐
        // │  User threads → JVM stays alive                   │
        // │  Daemon threads → JVM kills them on exit          │
        // │  setDaemon(true) MUST be called BEFORE start()   │
        // └──────────────────────────────────────────────────┘
        //
        Thread daemon = new Thread(() -> {
            while (true) {
                System.out.println("  [Daemon] heartbeat...");
                try { Thread.sleep(200); } catch (InterruptedException e) { break; }
            }
        }, "Daemon-Heartbeat");
        daemon.setDaemon(true);
        daemon.start();

        System.out.println("Is daemon: " + daemon.isDaemon());
        Thread.sleep(500);  // Let daemon run a few iterations
        System.out.println("Main ending — daemon will be killed automatically.\n");

        System.out.println("=== INTERRUPT: Cooperative Cancellation ===");

        // ── interrupt() signals a thread to stop (cooperative) ──────
        //
        // The thread must CHECK for interruption:
        //   1. Thread.sleep() throws InterruptedException
        //   2. Thread.interrupted() returns true
        //
        Thread longTask = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("  [Long task] Interrupted! Cleaning up...");
                    return;
                }
                System.out.println("  [Long task] Working... " + i);
                try { Thread.sleep(100); } catch (InterruptedException e) {
                    System.out.println("  [Long task] Sleep interrupted! Stopping.");
                    return;
                }
            }
        }, "Long-Task");

        longTask.start();
        Thread.sleep(350);  // Let it run a few iterations
        longTask.interrupt();  // Signal cancellation
        longTask.join();
        System.out.println("Long task stopped cleanly.");
    }
}
