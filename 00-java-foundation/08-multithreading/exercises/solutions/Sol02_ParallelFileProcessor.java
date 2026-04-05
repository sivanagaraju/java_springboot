/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_ParallelFileProcessor.java                       ║
 * ║  MODULE : 00-java-foundation / 08-multithreading                 ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — parallel I/O with CompletableFuture ║
 * ║  DEMONSTRATES   : supplyAsync, thenApply, allOf, exceptionally   ║
 * ║  PYTHON COMPARE : asyncio.gather vs CompletableFuture.allOf      ║
 * ║                                                                  ║
 * ║  CONCURRENCY DIAGRAM:                                            ║
 * ║   Main ─┬─ CF: read(file1) → process → result1                  ║
 * ║          ├─ CF: read(file2) → process → result2                  ║
 * ║          ├─ CF: read(file3) → process → result3                  ║
 * ║          └─ CF: read(error) → FAIL  → fallback                  ║
 * ║         allOf().join() ← waits for ALL to complete               ║
 * ║   Total time ≈ max(readTimes), not sum(readTimes)               ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Demonstrates parallel file processing using {@code CompletableFuture}.
 *
 * <p>Key insight: {@code CompletableFuture.supplyAsync()} submits a task to
 * {@code ForkJoinPool.commonPool()} by default. All file reads start simultaneously,
 * and {@code allOf().join()} waits for the slowest one — total time equals the
 * MAX read time, not the SUM.
 *
 * <p>Python equivalent: {@code asyncio.gather(read(f1), read(f2), read(f3))}
 * uses coroutines for cooperative multitasking. CompletableFuture uses threads
 * from a pool — true parallelism (not just concurrency) on multi-core machines.
 */
public class Sol02_ParallelFileProcessor {

    // WHY static map: simulates file contents + read times without real I/O
    private static final Map<String, Integer> FILE_READ_TIMES_MS = Map.of(
            "data.csv",    300,
            "report.txt",  200,
            "config.xml",  100,
            "error.txt",     0, // WHY 0: will throw before sleep
            "summary.pdf", 250
    );

    /**
     * Simulates reading a file with a delay proportional to file size.
     *
     * <p>WHY Thread.sleep: simulates network/disk I/O latency.
     * In production this would be {@code Files.readString(path)} or similar.
     *
     * @param filename the simulated file to read
     * @return simulated file content
     * @throws RuntimeException for "error.txt" to demonstrate error handling
     */
    static String simulateReadFile(String filename) {
        // WHY fail fast before sleep: error.txt fails immediately
        if ("error.txt".equals(filename)) {
            throw new RuntimeException("Simulated read failure for: " + filename);
        }
        try {
            int delay = FILE_READ_TIMES_MS.getOrDefault(filename, 150);
            Thread.sleep(delay); // WHY: simulate I/O latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Content of " + filename + " (" + FILE_READ_TIMES_MS.getOrDefault(filename, 150) + "ms)";
    }

    /**
     * Processes (transforms) file content.
     *
     * <p>Real-world equivalent: parse CSV, validate XML, extract text from PDF.
     *
     * @param content raw file content
     * @return processed result string
     */
    static String processFile(String content) {
        // WHY simulate processing: demonstrates chaining with thenApply
        int wordCount = content.split("\\s+").length;
        return String.format("PROCESSED[words=%d]: %s", wordCount, content);
    }

    /**
     * Processes all files in parallel using CompletableFuture.
     *
     * <p>Pipeline per file:
     * <ol>
     *   <li>{@code supplyAsync}: submit file read to thread pool</li>
     *   <li>{@code thenApply}: when read completes, process content (same thread)</li>
     *   <li>{@code exceptionally}: if read fails, return a fallback result</li>
     * </ol>
     *
     * <p>WHY {@code allOf().join()}: waits for ALL futures to complete before
     * collecting results. Without allOf, calling {@code future.join()} in a
     * loop would serialize the waits — no parallelism benefit.
     *
     * @param filenames list of file names to process
     * @return list of results in the same order as filenames
     */
    static List<String> processAllFiles(List<String> filenames) {
        // Step 1: Create one CompletableFuture per file (all start simultaneously)
        List<CompletableFuture<String>> futures = filenames.stream()
                .map(filename -> CompletableFuture
                        // WHY supplyAsync: runs on ForkJoinPool, non-blocking for caller
                        .supplyAsync(() -> simulateReadFile(filename))
                        // WHY thenApply: chains processing AFTER read, same thread pool
                        .thenApply(content -> processFile(content))
                        // WHY exceptionally: per-file error recovery — one failure
                        // doesn't cancel all other futures. Returns a fallback string.
                        .exceptionally(ex -> "ERROR[" + filename + "]: " + ex.getMessage())
                )
                .collect(Collectors.toList());

        // Step 2: Wait for ALL futures to complete (blocks until the slowest finishes)
        // WHY allOf + join: allOf returns a void CF that completes when all input CFs complete.
        // join() blocks synchronously (like Python's asyncio.run).
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Step 3: Collect results — all futures are done at this point, join() won't block
        return futures.stream()
                .map(CompletableFuture::join) // WHY safe here: all futures completed in step 2
                .collect(Collectors.toList());
    }

    /**
     * Demonstrates using a custom ExecutorService instead of ForkJoinPool.
     *
     * <p>WHY custom executor: ForkJoinPool is designed for CPU-bound work-stealing.
     * For I/O-bound tasks (file reads, HTTP calls), a fixed thread pool with
     * more threads than cores can achieve better throughput.
     *
     * @param filenames list of file names to process
     * @return list of results
     */
    static List<String> processAllFilesWithCustomExecutor(List<String> filenames) {
        // WHY 4 threads for I/O: while one thread waits for I/O, others can run.
        // For CPU-bound: use Runtime.getRuntime().availableProcessors() threads.
        ExecutorService ioPool = Executors.newFixedThreadPool(4,
                r -> new Thread(r, "io-worker")); // WHY named threads: easier debugging

        try {
            List<CompletableFuture<String>> futures = filenames.stream()
                    .map(filename -> CompletableFuture
                            .supplyAsync(() -> simulateReadFile(filename), ioPool) // WHY explicit executor
                            .thenApply(Sol02_ParallelFileProcessor::processFile)
                            .exceptionally(ex -> "ERROR[" + filename + "]: " + ex.getMessage())
                    )
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        } finally {
            // WHY shutdown: always shut down ExecutorService to release thread resources.
            // Forgetting this is a resource leak — threads keep running in the background.
            ioPool.shutdown();
        }
    }

    /**
     * Runs both processing approaches and compares timing.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        List<String> files = List.of(
                "data.csv", "report.txt", "config.xml", "error.txt", "summary.pdf");

        System.out.println("=== Parallel File Processor Solutions ===\n");
        System.out.println("Files to process: " + files);

        // ── Approach 1: ForkJoinPool (default) ──────────────────────────
        System.out.println("\n--- ForkJoinPool (default) ---");
        long start1 = System.currentTimeMillis();
        List<String> results1 = processAllFiles(files);
        long elapsed1 = System.currentTimeMillis() - start1;

        System.out.println("Results:");
        results1.forEach(r -> System.out.println("  " + r));
        System.out.println("Total time: " + elapsed1 + "ms");
        System.out.println("(Should be ~300ms = max read time, not ~850ms = sum)");

        // ── Approach 2: Custom I/O thread pool ──────────────────────────
        System.out.println("\n--- Custom I/O Thread Pool ---");
        long start2 = System.currentTimeMillis();
        List<String> results2 = processAllFilesWithCustomExecutor(files);
        long elapsed2 = System.currentTimeMillis() - start2;

        System.out.println("Results (same as above):");
        results2.forEach(r -> System.out.println("  " + r));
        System.out.println("Total time: " + elapsed2 + "ms");
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Calling join() in the map() — serializes the parallelism
 *   WRONG: filenames.stream().map(f -> CF.supplyAsync(...).join())
 *   PROBLEM: Each join() blocks until THAT file completes before starting the next.
 *            Total time = SUM of all times — no parallelism benefit.
 *   RIGHT: Collect all CFs first, then allOf().join(), then collect results.
 *
 * MISTAKE 2: Not calling exceptionally() — one failure cancels all results
 *   WRONG: Without exceptionally(), if error.txt throws, the entire allOf() fails.
 *   RIGHT: .exceptionally(ex -> fallback) — isolates failure to that one CF.
 *
 * MISTAKE 3: Not shutting down ExecutorService
 *   WRONG: ExecutorService pool = Executors.newFixedThreadPool(4);  /* never closed *\/
 *   PROBLEM: Thread leak — the 4 threads keep running even after main() exits,
 *            preventing JVM from shutting down (or causing OOM in long-running apps).
 *   RIGHT: try { ... } finally { pool.shutdown(); }
 *          or try-with-resources (Java 19+: ExecutorService implements AutoCloseable)
 *
 * MISTAKE 4: Using ForkJoinPool for I/O-bound work
 *   ForkJoinPool has N threads where N = availableProcessors. Each I/O thread
 *   that blocks starves CPU-bound tasks waiting in the pool.
 *   For I/O-bound: use Executors.newFixedThreadPool(N) where N >> cores.
 *   Java 21 virtual threads: Executors.newVirtualThreadPerTaskExecutor() handles
 *   I/O blocking efficiently without a fixed thread pool.
 *
 * MISTAKE 5: Checking future.isDone() instead of using allOf()
 *   WRONG: while (!future.isDone()) Thread.sleep(10);  // busy-waiting
 *   RIGHT: CompletableFuture.allOf(futures).join()  // blocks efficiently
 * ═══════════════════════════════════════════════════════════════════ */
