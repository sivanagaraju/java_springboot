import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 1: File Word Counter                              ║
 * ║  Count word frequencies in a text file using NIO.2 + Streams║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * TASK: Read a text file and count word frequencies.
 *
 * Requirements:
 * 1. Read file using Files.lines() (NIO.2)
 * 2. Split each line into words (ignore punctuation)
 * 3. Count frequency of each word (case-insensitive)
 * 4. Print top 10 most frequent words
 * 5. Write results to an output file
 *
 * BONUS: Accept filename as command-line argument
 *
 * Expected Output:
 *   Word Frequencies (Top 10):
 *   the    → 42
 *   and    → 28
 *   to     → 25
 *   ...
 *
 * HINTS:
 * - Use Stream's flatMap to split lines into words
 * - Use Collectors.groupingBy + Collectors.counting()
 * - Use entry.stream().sorted() for top-10
 */
public class Ex01_FileWordCounter {
    public static void main(String[] args) {
        // TODO: Read file using Files.lines()
        // TODO: Split into words, normalize case, remove punctuation
        // TODO: Count frequencies with Stream + Collectors
        // TODO: Sort and display top 10
        // TODO: Write results to output file
        System.out.println("Exercise 1: Implement the Word Counter!");
    }
}
