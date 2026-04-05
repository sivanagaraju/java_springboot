/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_FileReaderWithResources.java                     ║
 * ║  MODULE : 00-java-foundation / 05-exception-handling             ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — try-with-resources + AutoCloseable  ║
 * ║  DEMONSTRATES   : multi-resource TWR, suppressed exceptions,     ║
 * ║                   custom AutoCloseable, LIFO close order         ║
 * ║  PYTHON COMPARE : Python 'with' statement; contextmanager        ║
 * ║                                                                  ║
 * ║  RESOURCE LIFECYCLE:                                             ║
 * ║   OPEN:  reader → writer   (declaration order)                  ║
 * ║   USE:   read/write                                              ║
 * ║   CLOSE: writer → reader   (LIFO — reverse of open order)       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates try-with-resources for file I/O and custom AutoCloseable.
 *
 * <p>WHY try-with-resources over manual try-finally:
 * <ul>
 *   <li>Guarantees close() even if the body throws</li>
 *   <li>If both body AND close() throw, suppresses the close() exception
 *       (accessible via getSuppressed()) — not lost like manual finally</li>
 *   <li>Multiple resources closed in LIFO order — declared last, closed first</li>
 * </ul>
 *
 * <p>Python equivalent: Python's {@code with} statement uses the context
 * manager protocol ({@code __enter__} / {@code __exit__}). Java uses
 * the {@code AutoCloseable} interface with its single {@code close()} method.
 */
public class Sol02_FileReaderWithResources {

    /**
     * Reads all lines from a file using try-with-resources.
     *
     * <p>Uses {@code Files.newBufferedReader()} (NIO.2, Java 7+) instead of
     * {@code new BufferedReader(new FileReader(path))} — NIO.2 version accepts
     * a charset parameter (defaults to UTF-8) and avoids platform encoding issues.
     *
     * <p>Python: {@code with open(path, encoding='utf-8') as f: return f.readlines()}
     *
     * @param path the file to read
     * @return list of lines, empty if file not found
     */
    public static List<String> readFileContents(String path) {
        List<String> lines = new ArrayList<>();

        // WHY try-with-resources: BufferedReader implements AutoCloseable.
        // The reader is guaranteed to close even if readLine() throws.
        // The compiler generates a finally block that calls close().
        try (BufferedReader reader = Files.newBufferedReader(
                Path.of(path), StandardCharsets.UTF_8)) {
            String line;
            // WHY while != null: readLine() returns null at end of file
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (java.nio.file.NoSuchFileException e) {
            // WHY specific FileNotFoundException first: gives caller meaningful signal
            System.err.println("File not found: " + path);
        } catch (IOException e) {
            // WHY catch IOException separately: handles read errors, permission denied, etc.
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }

    /**
     * Copies a source file to a destination file.
     *
     * <p>Both reader and writer are opened in the SAME try-with-resources.
     * Closing order is LIFO: writer closes first, then reader.
     * This is safe because the writer flushes before close().
     *
     * <p>Python: {@code with open(src) as fin, open(dst, 'w') as fout: fout.write(fin.read())}
     *
     * @param source      path of the file to copy from
     * @param destination path of the file to copy to (created/overwritten)
     * @throws IOException if reading or writing fails
     */
    public static void copyFile(String source, String destination) throws IOException {
        // WHY both resources in same try header: both are auto-closed.
        // If source open fails, destination is never opened (no cleanup needed).
        // If destination open fails, source is already open and will be closed.
        try (BufferedReader reader = Files.newBufferedReader(Path.of(source), StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(Path.of(destination), StandardCharsets.UTF_8)) {

            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) {
                    writer.newLine(); // WHY: preserve line separators between lines
                }
                writer.write(line);
                firstLine = false;
            }
        }
        // WHY not catch here: let caller decide how to handle copy failures.
        // This method has a specific purpose — copying — not error recovery.
        System.out.println("Copied " + source + " → " + destination);
    }

    // ── Custom AutoCloseable ─────────────────────────────────────────

    /**
     * A buffered log writer that flushes all entries when closed.
     *
     * <p>Demonstrates the AutoCloseable contract: any class that manages
     * a resource (file, connection, buffer) should implement AutoCloseable
     * so callers can use try-with-resources for guaranteed cleanup.
     *
     * <p>WHY buffer until close(): batching writes reduces I/O operations.
     * The downside: if the process crashes before close(), buffered entries
     * are lost. For critical logging, flush immediately instead.
     *
     * <p>Python equivalent: context manager with {@code __enter__}/{@code __exit__},
     * or the {@code @contextmanager} decorator.
     */
    static class LogWriter implements AutoCloseable {

        private final String loggerName;
        // WHY StringBuilder: accumulate log entries in memory; flush on close()
        private final StringBuilder buffer = new StringBuilder();
        private boolean closed = false;

        /**
         * Creates a LogWriter with the given logger name.
         *
         * @param loggerName prefix for each log entry
         */
        public LogWriter(String loggerName) {
            this.loggerName = loggerName;
        }

        /**
         * Buffers a log message with a timestamp.
         *
         * @param message the message to log
         * @throws IllegalStateException if the writer has already been closed
         */
        public void write(String message) {
            // WHY guard: writing to a closed resource is a programming error
            if (closed) {
                throw new IllegalStateException("LogWriter already closed");
            }
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
            buffer.append(String.format("[%s][%s] %s%n", timestamp, loggerName, message));
        }

        /**
         * Flushes all buffered log entries to stdout and marks this writer as closed.
         *
         * <p>Called automatically when used in try-with-resources. Safe to call
         * multiple times — subsequent calls after the first are no-ops.
         */
        @Override
        public void close() {
            if (!closed) {
                // WHY check buffer.length: don't print header if nothing was logged
                if (buffer.length() > 0) {
                    System.out.println("=== LogWriter[" + loggerName + "] flush ===");
                    System.out.print(buffer);
                    System.out.println("=== End ===");
                }
                closed = true; // WHY idempotent: close() must be safe to call twice
            }
        }
    }

    /**
     * Demonstrates all three exercises.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== File Reader With Resources Solution ===\n");

        // ── Exercise 3: Custom LogWriter in try-with-resources ───────────
        // WHY demonstrate LogWriter first: doesn't need real files on disk
        System.out.println("--- LogWriter Demo ---");
        try (LogWriter log = new LogWriter("RequestProcessor")) {
            log.write("Processing order ID=12345");
            log.write("Payment validated");
            log.write("Inventory updated");
            log.write("Email notification queued");
            // WHY: close() is called automatically here, which flushes all entries
        }
        // After try block: log.close() has been called, all entries flushed

        System.out.println("\n--- LogWriter: auto-close on exception ---");
        try (LogWriter log = new LogWriter("FailingService")) {
            log.write("Starting operation");
            log.write("About to fail...");
            throw new RuntimeException("Simulated failure");
        } catch (RuntimeException e) {
            System.out.println("Caught: " + e.getMessage());
            // WHY: even though an exception was thrown, close() was called
            // before the catch block executed — all log entries are flushed
        }

        // ── Exercise 1: readFileContents (requires a real file) ──────────
        System.out.println("\n--- readFileContents: non-existent file ---");
        List<String> lines = readFileContents("nonexistent.txt");
        System.out.println("Lines from missing file: " + lines.size()); // 0

        // ── Exercise 2: copyFile (requires real files) ───────────────────
        // To test for real: create a temp file, copy it, verify destination
        System.out.println("\n--- copyFile: would copy if files existed ---");
        System.out.println("(Pass real paths to copyFile(source, dest) to test)");
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Manual try-finally losing the original exception
 *   try { return process(); }
 *   finally { close(); }  // if close() throws, process()'s result/exception is LOST
 *   Fix: try-with-resources stores the close() exception as suppressed —
 *        access via e.getSuppressed()[0]. The original exception propagates.
 *
 * MISTAKE 2: Declaring resources INSIDE the try block
 *   WRONG: try { BufferedReader r = new BufferedReader(...); ... }
 *   PROBLEM: r is not in scope for auto-close. Won't compile as TWR.
 *   RIGHT: try (BufferedReader r = new BufferedReader(...)) { ... }
 *
 * MISTAKE 3: Wrong close order assumption
 *   try (A a = ...; B b = ...) { }
 *   Close order: B closes FIRST, then A (LIFO — last opened, first closed).
 *   Matters when B depends on A (e.g., writer depends on underlying stream).
 *
 * MISTAKE 4: Not implementing AutoCloseable for custom resources
 *   If you write a class that wraps a connection, stream, or lock,
 *   always implement AutoCloseable to enable try-with-resources.
 *   Users will forget to close() — make it easy with TWR.
 *
 * MISTAKE 5: Platform-specific line endings in copyFile
 *   writer.write(line) + writer.newLine() uses platform line separator.
 *   For cross-platform files (e.g., Unix files on Windows), use
 *   Files.copy(source, dest) from NIO.2 instead — preserves bytes exactly.
 * ═══════════════════════════════════════════════════════════════════ */
