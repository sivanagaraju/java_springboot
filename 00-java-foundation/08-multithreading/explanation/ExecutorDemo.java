/**
 * ====================================================================
 * FILE    : ExecutorDemo.java
 * MODULE  : 08 — Multithreading
 * PURPOSE : Thread pools, ExecutorService, CompletableFuture
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: concurrent.futures.ThreadPoolExecutor(max_workers=4)
 *   Java:   Executors.newFixedThreadPool(4)
 *
 * THREAD POOL ARCHITECTURE:
 *
 *   ┌─────────────────────────────────────────────────┐
 *   │  Task Queue:  [T1] [T2] [T3] [T4] [T5] ...     │
 *   │                 │    │    │                      │
 *   │                 ▼    ▼    ▼                      │
 *   │  Workers:  [Worker1][Worker2][Worker3]           │
 *   │              │ done │ done │ done                │
 *   │              ▼      ▼      ▼                     │
 *   │  Reuse:   [T4]   [T5]   [T6]  ...               │
 *   │                                                  │
 *   │  N workers handle M tasks (M >> N)               │
 *   └─────────────────────────────────────────────────┘
 *
 * COMPLETABLEFUTURE PIPELINE:
 *
 *   supplyAsync(supplier)    → start async
 *       │
 *   thenApply(fn)            → transform (like stream.map)
 *       │
 *   thenCompose(fn)          → chain (like stream.flatMap)
 *       │
 *   exceptionally(fn)        → handle errors
 *       │
 *   thenAccept(consumer)     → consume result
 *
 * ====================================================================
 */
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class ExecutorDemo {

    public static void main(String[] args) throws Exception {

        System.out.println("=== EXECUTOR SERVICE: Thread Pool ===\n");

        // ── Fixed thread pool: 3 workers handle 6 tasks ─────────────
        ExecutorService pool = Executors.newFixedThreadPool(3);

        System.out.println("Submitting 6 tasks to pool of 3 threads:");

        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            pool.submit(() -> {
                String thread = Thread.currentThread().getName();
                System.out.println("  Task " + taskId + " on " + thread);
                try { Thread.sleep(300); } catch (InterruptedException e) { return; }
                System.out.println("  Task " + taskId + " done on " + thread);
            });
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("Pool shut down.\n");

        System.out.println("=== FUTURE: Async Result ===\n");

        // ── Submit Callable (returns a value) ───────────────────────
        ExecutorService pool2 = Executors.newFixedThreadPool(2);

        Future<String> future = pool2.submit(() -> {
            Thread.sleep(500);
            return "Result from async computation";
        });

        System.out.println("Future submitted, doing other work...");
        System.out.println("isDone: " + future.isDone());

        // get() BLOCKS until result is ready
        String result = future.get(2, TimeUnit.SECONDS);
        System.out.println("Result: " + result);
        System.out.println("isDone: " + future.isDone());

        pool2.shutdown();
        pool2.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("\n=== COMPLETABLEFUTURE: Non-Blocking Chains ===\n");

        // ── Simple pipeline ─────────────────────────────────────────
        //
        // FLOW: fetch user → transform name → print result
        //
        CompletableFuture<String> pipeline = CompletableFuture
                .supplyAsync(() -> {
                    // Simulate DB call
                    System.out.println("  [1] Fetching user on "
                            + Thread.currentThread().getName());
                    sleep(200);
                    return "alice";
                })
                .thenApply(name -> {
                    // Transform
                    System.out.println("  [2] Transforming on "
                            + Thread.currentThread().getName());
                    return name.substring(0, 1).toUpperCase() + name.substring(1);
                })
                .thenApply(name -> {
                    System.out.println("  [3] Adding greeting on "
                            + Thread.currentThread().getName());
                    return "Hello, " + name + "!";
                });

        System.out.println("Pipeline result: " + pipeline.get());

        System.out.println("\n=== ERROR HANDLING ===\n");

        // ── exceptionally: handle errors in the chain ───────────────
        CompletableFuture<String> errorPipeline = CompletableFuture
                .supplyAsync(() -> {
                    if (true) throw new RuntimeException("Database connection failed!");
                    return "data";
                })
                .thenApply(String::toUpperCase)         // skipped due to error
                .exceptionally(ex -> {
                    System.out.println("  Caught: " + ex.getMessage());
                    return "FALLBACK VALUE";
                });

        System.out.println("Error pipeline: " + errorPipeline.get());

        System.out.println("\n=== COMBINING FUTURES ===\n");

        // ── thenCombine: combine results of 2 independent futures ───
        //
        // PARALLEL EXECUTION:
        //   fetchUser ──┐
        //               ├──▶ combine results
        //   fetchOrder ─┘
        //
        CompletableFuture<String> fetchUser = CompletableFuture.supplyAsync(() -> {
            sleep(300);
            return "Alice";
        });

        CompletableFuture<Integer> fetchOrderCount = CompletableFuture.supplyAsync(() -> {
            sleep(200);
            return 5;
        });

        CompletableFuture<String> combined = fetchUser.thenCombine(
                fetchOrderCount,
                (user, count) -> user + " has " + count + " orders"
        );
        System.out.println("Combined: " + combined.get());

        // ── allOf: wait for ALL futures ─────────────────────────────
        System.out.println("\nWaiting for all tasks...");
        List<CompletableFuture<String>> tasks = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            final int id = i;
            tasks.add(CompletableFuture.supplyAsync(() -> {
                sleep(100 * id);
                return "Task-" + id + " done";
            }));
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(
                tasks.toArray(new CompletableFuture[0])
        );

        all.join();  // wait for all
        tasks.forEach(t -> System.out.println("  " + t.join()));

        System.out.println("\n=== REAL-WORLD PATTERN ===\n");

        // ── Common Spring-like pattern ──────────────────────────────
        //
        // Service method:
        //   1. Fetch user profile  (async)
        //   2. Fetch user orders   (async, parallel with #1)
        //   3. Combine into response DTO
        //   4. Handle errors with fallback
        //
        CompletableFuture<String> response = CompletableFuture
                .supplyAsync(() -> fetchProfile("U001"))
                .thenCombine(
                        CompletableFuture.supplyAsync(() -> fetchOrders("U001")),
                        (profile, orders) -> "{ profile: " + profile + ", orders: " + orders + " }"
                )
                .exceptionally(ex -> "{ error: '" + ex.getMessage() + "' }");

        System.out.println("API Response: " + response.get());
    }

    // ── Helper methods ──────────────────────────────────────────────
    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    static String fetchProfile(String userId) {
        sleep(200);
        return "{ name: 'Alice', role: 'admin' }";
    }

    static String fetchOrders(String userId) {
        sleep(150);
        return "[ Order#1, Order#2, Order#3 ]";
    }
}
