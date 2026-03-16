/**
 * ====================================================================
 * FILE    : Ex02_ParallelFileProcessor.java
 * MODULE  : 08 — Multithreading
 * PURPOSE : Real-world multithreaded I/O using ExecutorService
 * ====================================================================
 *
 * EXERCISES:
 *
 *   Build a parallel file processor using CompletableFuture:
 *
 *   1. simulateReadFile(filename) → String
 *      - Simulate reading a file (Thread.sleep + return content)
 *      - Each "file" takes a different amount of time
 *
 *   2. processFile(content) → String
 *      - Simulate processing (e.g., word count, uppercase)
 *      - Returns a result string
 *
 *   3. processAllFiles(List<String> files) → List<String>
 *      - Submit ALL file reads as CompletableFuture tasks
 *      - Chain processFile after EACH read completes
 *      - Collect ALL results
 *      - HINT: CompletableFuture.supplyAsync + thenApply + allOf
 *
 *   4. BONUS: Add error handling
 *      - If a file "fails" to read, use exceptionally() to provide
 *        a fallback result instead of crashing the entire batch
 *
 * ARCHITECTURE:
 *
 *   ┌─────────────────────────────────────────────────────────┐
 *   │  Main Thread                                            │
 *   │    │                                                    │
 *   │    ├── CF: read("file1.txt") → process → result1       │
 *   │    ├── CF: read("file2.txt") → process → result2       │
 *   │    ├── CF: read("file3.txt") → process → result3       │
 *   │    ├── CF: read("file4.txt") → ERROR  → fallback4      │
 *   │    │                                                    │
 *   │    └── allOf().join() → collect all results             │
 *   │                                                         │
 *   │  All reads happen IN PARALLEL on ForkJoinPool           │
 *   │  Total time ≈ max(readTimes), not sum(readTimes)       │
 *   └─────────────────────────────────────────────────────────┘
 *
 * ====================================================================
 */
import java.util.*;
import java.util.concurrent.*;

public class Ex02_ParallelFileProcessor {

    // TODO: Simulate file reading (different durations per file)
    static String simulateReadFile(String filename) {
        // Simulate: throw RuntimeException for "error.txt"
        // Otherwise return "Content of <filename>"
        return "";
    }

    // TODO: Simulate file processing (e.g., word count)
    static String processFile(String content) {
        return "";
    }

    // TODO: Process all files in parallel, return all results
    static List<String> processAllFiles(List<String> filenames) {
        // HINT:
        // 1. Create List<CompletableFuture<String>>
        // 2. For each file: supplyAsync(() -> readFile)
        //    .thenApply(content -> processFile)
        //    .exceptionally(ex -> "Error: " + ex.getMessage())
        // 3. CompletableFuture.allOf(...).join()
        // 4. Collect results from each future via .join()
        return List.of();
    }

    public static void main(String[] args) {
        List<String> files = List.of(
                "data.csv", "report.txt", "config.xml",
                "error.txt", "summary.pdf"
        );

        System.out.println("=== Parallel File Processor Exercise ===\n");
        System.out.println("Files to process: " + files);

        long start = System.currentTimeMillis();
        List<String> results = processAllFiles(files);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("\nResults:");
        results.forEach(r -> System.out.println("  " + r));
        System.out.println("\nTotal time: " + elapsed + "ms");
        System.out.println("(Should be ~max(readTimes), not sum(readTimes))");
    }
}
