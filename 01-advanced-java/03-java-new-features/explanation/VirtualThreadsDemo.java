import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  VIRTUAL THREADS — Demo                                     ║
 * ║  Lightweight concurrency with Project Loom (Java 21)        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  Platform Thread:  ~1MB stack, OS-managed, limited       │
 * │  Virtual Thread:   ~1KB, JVM-managed, millions possible  │
 * │                                                          │
 * │  Virtual thread blocks on I/O → JVM unmounts it          │
 * │  Carrier OS thread runs another virtual thread            │
 * │  Result: massive concurrency without thread pools         │
 * └──────────────────────────────────────────────────────────┘
 */
public class VirtualThreadsDemo {

    // Simulated I/O-bound work
    static String fetchData(int id) {
        try {
            Thread.sleep(Duration.ofMillis(100));  // simulate DB/HTTP call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Data-" + id;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("═══ VIRTUAL THREADS DEMO ═══\n");

        // 1. Creating virtual threads
        System.out.println("1. Single Virtual Thread:");
        Thread vt = Thread.ofVirtual().name("my-vthread").start(() -> {
            System.out.println("  Running in: " + Thread.currentThread());
            System.out.println("  Is virtual: " + Thread.currentThread().isVirtual());
        });
        vt.join();

        // 2. Platform vs Virtual thread comparison
        System.out.println("\n2. Platform vs Virtual — 1000 tasks:");

        // Platform threads
        Instant start = Instant.now();
        try (var executor = Executors.newFixedThreadPool(100)) {
            IntStream.range(0, 1000).forEach(i ->
                executor.submit(() -> fetchData(i)));
        }
        Duration platformTime = Duration.between(start, Instant.now());

        // Virtual threads
        start = Instant.now();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 1000).forEach(i ->
                executor.submit(() -> fetchData(i)));
        }
        Duration virtualTime = Duration.between(start, Instant.now());

        System.out.println("  Platform (100-thread pool): " + platformTime.toMillis() + "ms");
        System.out.println("  Virtual  (per-task):        " + virtualTime.toMillis() + "ms");
        System.out.println("  Virtual is ~" + (platformTime.toMillis() / Math.max(virtualTime.toMillis(), 1)) + "x faster");

        // 3. Structured concurrency pattern
        System.out.println("\n3. Fan-out Pattern:");
        var results = new ConcurrentLinkedQueue<String>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(1, 6)
                .mapToObj(i -> executor.submit(() -> fetchData(i)))
                .toList();

            for (var future : futures) {
                results.add(future.get());
            }
        }
        System.out.println("  Results: " + results);

        // 4. Thread identity
        System.out.println("\n4. Virtual Thread Identity:");
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 3; i++) {
                executor.submit(() -> {
                    Thread t = Thread.currentThread();
                    System.out.printf("  %s | virtual=%s%n", t.getName(), t.isVirtual());
                });
            }
        }
        Thread.sleep(200);

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Use newVirtualThreadPerTaskExecutor() for I/O-bound work.");
        System.out.println("Don't pool virtual threads — create one per task.");
        System.out.println("Spring Boot 3.2+: spring.threads.virtual.enabled=true");
    }
}
