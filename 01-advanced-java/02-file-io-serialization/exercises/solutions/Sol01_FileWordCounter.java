import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 1: File Word Counter                                      ║
 * ║  Count word frequencies using NIO.2 Files.lines() + Streams         ║
 * ║                                                                      ║
 * ║  ASCII PIPELINE:                                                     ║
 * ║                                                                      ║
 * ║   Files.lines(path)          → Stream<String> (lazy, line by line)  ║
 * ║     .flatMap(split by words) → Stream<String> (all words)           ║
 * ║     .map(String::toLowerCase)→ Stream<String> (normalized)          ║
 * ║     .filter(non-empty)       → Stream<String> (clean tokens)        ║
 * ║     .collect(groupingBy +    → Map<String, Long> (word → count)     ║
 * ║              counting())                                             ║
 * ║     .entrySet().stream()                                             ║
 * ║     .sorted(by value desc)   → top 10 entries                       ║
 * ║     .forEach(print)                                                  ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */

/**
 * Demonstrates NIO.2 lazy file reading, Stream pipeline, and Collectors.
 * Key insight: Files.lines() reads ONE LINE AT A TIME — constant memory
 * regardless of file size. Never use Files.readAllLines() for large files.
 */
public class Sol01_FileWordCounter {

    /**
     * Counts word frequencies from the given file path.
     *
     * @param inputPath path to the text file to analyze
     * @return map of word to frequency, sorted by frequency descending
     * @throws IOException if file cannot be read
     */
    public static Map<String, Long> countWords(Path inputPath) throws IOException {
        // WHY Files.lines(): lazy Stream — reads line-by-line from disk,
        // never loads the entire file into memory. Safe for multi-GB files.
        try (Stream<String> lines = Files.lines(inputPath)) {
            return lines
                .flatMap(line -> Arrays.stream(
                    line.replaceAll("[^a-zA-Z\\s]", "").split("\\s+")
                    // WHY replaceAll: strips punctuation (.,!?) before splitting
                    // \\s+ splits on any whitespace including multiple spaces/tabs
                ))
                .map(String::toLowerCase)         // WHY: "The" and "the" are the same word
                .filter(word -> !word.isEmpty())  // WHY: replaceAll can produce empty tokens
                .collect(Collectors.groupingBy(
                    word -> word,                 // group by the word itself
                    Collectors.counting()         // count occurrences per group
                ));
        }
        // WHY try-with-resources: Stream<String> from Files.lines() wraps the
        // underlying file reader — must be closed to release file descriptor
    }

    /**
     * Returns the top N words by frequency from the given word map.
     *
     * @param wordCounts map of word to count
     * @param topN       maximum number of entries to return
     * @return list of entries sorted by count descending
     */
    public static List<Map.Entry<String, Long>> topN(Map<String, Long> wordCounts, int topN) {
        return wordCounts.entrySet().stream()
            // WHY comparingByValue(reversed): sort by count, highest first
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(topN)
            .collect(Collectors.toList());
    }

    /**
     * Writes word frequency results to an output file.
     *
     * @param results list of word-count pairs to write
     * @param outputPath path where results will be written
     * @throws IOException if file cannot be written
     */
    public static void writeResults(List<Map.Entry<String, Long>> results,
                                    Path outputPath) throws IOException {
        // WHY Files.createDirectories: output file's parent dir may not exist
        Files.createDirectories(outputPath.getParent() != null
            ? outputPath.getParent() : Path.of("."));

        // WHY BufferedWriter: batches writes into OS calls — significantly faster
        // for many small writes than calling Files.write() in a loop
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
            writer.write("Word Frequencies (Top " + results.size() + "):\n");
            writer.write("─".repeat(40) + "\n");
            for (Map.Entry<String, Long> entry : results) {
                writer.write(String.format("%-20s → %d%n", entry.getKey(), entry.getValue()));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // Create a sample input file for demonstration
        Path inputPath = Path.of(System.getProperty("java.io.tmpdir"), "sample_text.txt");
        Files.writeString(inputPath,
            """
            To be or not to be that is the question
            Whether tis nobler in the mind to suffer
            The slings and arrows of outrageous fortune
            Or to take arms against a sea of troubles
            To be or not to be
            """);

        System.out.println("Analyzing: " + inputPath);

        // WHY separate methods: single responsibility — count, rank, write are independent
        Map<String, Long> wordCounts = countWords(inputPath);
        List<Map.Entry<String, Long>> top10 = topN(wordCounts, 10);

        System.out.println("\nWord Frequencies (Top 10):");
        top10.forEach(e -> System.out.printf("  %-15s → %d%n", e.getKey(), e.getValue()));

        // Write results to file
        Path outputPath = Path.of(System.getProperty("java.io.tmpdir"), "word_counts.txt");
        writeResults(top10, outputPath);
        System.out.println("\nResults written to: " + outputPath);
    }
}

/* ─────────────────────────────────────────────────────────────────────────────
   COMMON MISTAKES

   MISTAKE 1: Files.readAllLines() for large files
     List<String> lines = Files.readAllLines(path);  // loads ALL lines into RAM
     For a 5GB log file: OutOfMemoryError.
     Fix: Files.lines(path) returns a lazy Stream — O(1) memory.

   MISTAKE 2: Not closing Files.lines() stream
     Stream<String> lines = Files.lines(path);  // no try-with-resources
     File descriptor leak — eventually "Too many open files".
     Fix: always use try (Stream<String> lines = Files.lines(path)) { }

   MISTAKE 3: Splitting on single space instead of \\s+
     line.split(" ")  // "hello  world" → ["hello", "", "world"]
     Empty string passes filter and corrupts counts.
     Fix: line.split("\\s+") handles multiple consecutive whitespace.

   MISTAKE 4: Not removing punctuation before splitting
     "hello," and "hello" counted separately.
     Fix: replaceAll("[^a-zA-Z\\s]", "") before split.

   MISTAKE 5: Sorting the wrong direction
     .sorted(Map.Entry.comparingByValue())  // ascending — lowest count first!
     Fix: .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
   ───────────────────────────────────────────────────────────────────────────── */
