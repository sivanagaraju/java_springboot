/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║         PARALLEL STREAMS DEMO — Fork/Join in Action         ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Demonstrates:                                              ║
 * ║  1. Sequential vs Parallel performance comparison           ║
 * ║  2. Thread observation (which thread runs what)             ║
 * ║  3. Common traps (shared state, ordering)                   ║
 * ║  4. Custom ForkJoinPool for isolation                       ║
 * ║  5. Correct parallel reduction                              ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class ParallelStreamDemo {

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 1: Sequential vs Parallel — Performance     │
    // └──────────────────────────────────────────────────────┘

    /**
     * Performance comparison on CPU-bound work.
     *
     * Sequential:              Parallel:
     * ┌────────────────┐       ┌────────────────┐
     * │ Thread: main   │       │ Thread: FJ-1   │ chunk [0..25k]
     * │ Process 100k   │       │ Thread: FJ-2   │ chunk [25k..50k]
     * │ elements one   │       │ Thread: FJ-3   │ chunk [50k..75k]
     * │ by one         │       │ Thread: main   │ chunk [75k..100k]
     * └────────────────┘       └────────────────┘
     * Time: ~400ms              Time: ~120ms (on 4 cores)
     */
    static void performanceComparison() {
        System.out.println("═══ Performance Comparison ═══");

        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 100_000; i++) numbers.add(i);

        // Simulate CPU-intensive work
        java.util.function.Function<Integer, Double> heavyComputation =
            n -> Math.sqrt(Math.pow(n, 2) + Math.log(n + 1));

        // Sequential
        long start = System.nanoTime();
        numbers.stream()
               .map(heavyComputation)
               .reduce(Double::sum);
        long seqTime = System.nanoTime() - start;

        // Parallel
        start = System.nanoTime();
        numbers.parallelStream()
               .map(heavyComputation)
               .reduce(Double::sum);
        long parTime = System.nanoTime() - start;

        System.out.printf("Sequential: %,d ns%n", seqTime);
        System.out.printf("Parallel:   %,d ns%n", parTime);
        System.out.printf("Speedup:    %.2fx%n", (double) seqTime / parTime);
        System.out.println("CPU cores:  " + Runtime.getRuntime().availableProcessors());
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 2: Thread Observation — Who Does What?      │
    // └──────────────────────────────────────────────────────┘

    /**
     * Parallel streams use ForkJoinPool.commonPool().
     *
     * ┌─────────────────────────────────────────────────────┐
     * │          ForkJoinPool.commonPool()                   │
     * │  ┌──────────┐  ┌──────────┐  ┌──────────┐         │
     * │  │ Worker-1 │  │ Worker-2 │  │ Worker-3 │  + main  │
     * │  └──────────┘  └──────────┘  └──────────┘         │
     * │  Default size = Runtime.availableProcessors() - 1  │
     * │  ALL parallel streams in JVM share this pool!      │
     * └─────────────────────────────────────────────────────┘
     */
    static void threadObservation() {
        System.out.println("\n═══ Thread Observation ═══");

        System.out.println("Common pool size: " +
            ForkJoinPool.commonPool().getParallelism());

        List<String> items = List.of("A", "B", "C", "D", "E", "F", "G", "H");

        System.out.println("--- Sequential ---");
        items.stream().forEach(item ->
            System.out.printf("  %s on %s%n", item, Thread.currentThread().getName())
        );

        System.out.println("--- Parallel ---");
        items.parallelStream().forEach(item ->
            System.out.printf("  %s on %s%n", item, Thread.currentThread().getName())
        );
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 3: TRAP — Shared Mutable State              │
    // └──────────────────────────────────────────────────────┘

    /**
     * ❌ Race condition with ArrayList:
     *
     * Thread-1: reads size=5, writes to index 5
     * Thread-2: reads size=5, writes to index 5  ← COLLISION!
     *
     * ┌───┬───┬───┬───┬───┬───┬────┐
     * │ 1 │ 2 │ 3 │ 4 │ 5 │???│    │  ← data corruption
     * └───┴───┴───┴───┴───┴───┴────┘
     *
     * ✅ Fix: Use collect() — thread-safe by design
     */
    static void sharedStateTrap() {
        System.out.println("\n═══ Shared Mutable State Trap ═══");

        // ❌ WRONG — race condition
        List<Integer> unsafeResults = new ArrayList<>();
        try {
            IntStream.rangeClosed(1, 10_000)
                .parallel()
                .forEach(unsafeResults::add);
            System.out.println("Unsafe list size: " + unsafeResults.size()
                + " (expected 10000)");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Unsafe list CRASHED: " + e.getMessage());
        }

        // ✅ CORRECT — collect is thread-safe
        List<Integer> safeResults = IntStream.rangeClosed(1, 10_000)
            .parallel()
            .boxed()
            .collect(Collectors.toList());
        System.out.println("Safe list size: " + safeResults.size()
            + " (expected 10000)");
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 4: Custom ForkJoinPool for Isolation        │
    // └──────────────────────────────────────────────────────┘

    /**
     * Problem: All parallel streams share commonPool.
     * If one does I/O, it starves others.
     *
     * Solution: Submit to custom pool.
     *
     * ┌──────────────────┐     ┌──────────────────┐
     * │  commonPool (3)  │     │  customPool (10)  │
     * │  CPU-bound work  │     │  I/O-bound work   │
     * │  (unaffected)    │     │  (isolated)       │
     * └──────────────────┘     └──────────────────┘
     */
    static void customForkJoinPool() throws Exception {
        System.out.println("\n═══ Custom ForkJoinPool ═══");

        ForkJoinPool customPool = new ForkJoinPool(4);

        try {
            List<String> results = customPool.submit(() ->
                List.of("url1", "url2", "url3", "url4", "url5")
                    .parallelStream()
                    .map(url -> {
                        String thread = Thread.currentThread().getName();
                        // Simulate I/O
                        try { Thread.sleep(100); }
                        catch (InterruptedException e) { /* ignore */ }
                        return url + " fetched by " + thread;
                    })
                    .collect(Collectors.toList())
            ).get();

            results.forEach(System.out::println);
        } finally {
            customPool.shutdown();
        }
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 5: Parallel Reduction — Associativity       │
    // └──────────────────────────────────────────────────────┘

    /**
     * Parallel reduce splits into chunks, reduces each, then merges.
     *
     * For [1, 2, 3, 4] with addition:
     *
     *   Chunk 1: 1 + 2 = 3
     *   Chunk 2: 3 + 4 = 7
     *   Merge:   3 + 7 = 10  ✅ Same as sequential
     *
     * For [1, 2, 3, 4] with subtraction:
     *
     *   Sequential: ((1 - 2) - 3) - 4 = -8
     *   Parallel:
     *     Chunk 1: 1 - 2 = -1
     *     Chunk 2: 3 - 4 = -1
     *     Merge:   -1 - (-1) = 0  ❌ WRONG!
     *
     * Rule: reduce operator MUST be associative
     *       (a ⊕ b) ⊕ c == a ⊕ (b ⊕ c)
     */
    static void parallelReduction() {
        System.out.println("\n═══ Parallel Reduction ═══");

        List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8);

        // ✅ Associative: addition
        int seqSum = numbers.stream().reduce(0, Integer::sum);
        int parSum = numbers.parallelStream().reduce(0, Integer::sum);
        System.out.println("Sequential sum: " + seqSum + " | Parallel sum: " + parSum);

        // ❌ Non-associative: subtraction (may differ!)
        int seqSub = numbers.stream().reduce(0, (a, b) -> a - b);
        int parSub = numbers.parallelStream().reduce(0, (a, b) -> a - b);
        System.out.println("Sequential sub: " + seqSub + " | Parallel sub: " + parSub
            + (seqSub != parSub ? " ← DIFFERENT!" : ""));

        // ✅ String concatenation with Collectors (order preserved)
        String joined = numbers.parallelStream()
            .map(String::valueOf)
            .collect(Collectors.joining(", "));
        System.out.println("Joined (order preserved): " + joined);
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  MAIN — Run All Demos                                │
    // └──────────────────────────────────────────────────────┘

    public static void main(String[] args) throws Exception {
        performanceComparison();
        threadObservation();
        sharedStateTrap();
        customForkJoinPool();
        parallelReduction();
    }
}
