/**
 * ====================================================================
 * FILE    : Ex02_FileReaderWithResources.java
 * MODULE  : 05 — Exception Handling
 * PURPOSE : Practice try-with-resources with file I/O
 * ====================================================================
 *
 * EXERCISES:
 *
 *   1. Implement readFileContents(path) using try-with-resources
 *      - Read a file line by line using BufferedReader
 *      - Return all lines as a List<String>
 *      - Handle FileNotFoundException vs IOException separately
 *
 *   2. Implement copyFile(source, destination) using try-with-resources
 *      - Open BOTH source (reader) and destination (writer) in same try
 *      - Copy line by line
 *      - Both resources auto-close even on error
 *
 *   3. Implement a custom LogWriter that implements AutoCloseable
 *      - write(String message) → appends to internal StringBuilder
 *      - close() → prints all accumulated messages with timestamps
 *      - Use it in try-with-resources
 *
 * RESOURCE FLOW for copyFile:
 *
 *   try (reader = ...; writer = ...) {
 *       // copy data
 *   }
 *
 *   OPEN:  reader → writer
 *   USE:   reader.readLine() → writer.write()
 *   CLOSE: writer → reader  (LIFO)
 *
 * ====================================================================
 */
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Ex02_FileReaderWithResources {

    /**
     * Read all lines from a file using try-with-resources.
     * 
     * HINT:
     *   try (BufferedReader br = new BufferedReader(new FileReader(path))) {
     *       String line;
     *       while ((line = br.readLine()) != null) { ... }
     *   }
     *
     * Python equivalent:
     *   with open(path) as f:
     *       return f.readlines()
     */
    public static List<String> readFileContents(String path) {
        // TODO: Implement with try-with-resources
        return new ArrayList<>();
    }

    /**
     * Copy contents from source file to destination file.
     * Both resources must be in the same try-with-resources.
     * 
     * Python equivalent:
     *   with open(source) as fin, open(dest, 'w') as fout:
     *       fout.write(fin.read())
     */
    public static void copyFile(String source, String destination) {
        // TODO: Implement with multiple resources in try-with-resources
    }

    /**
     * Create a custom AutoCloseable LogWriter.
     * 
     * Requirements:
     *   - Implements AutoCloseable
     *   - write(msg) appends to internal buffer with timestamp
     *   - close() flushes all messages to System.out
     *   - Use in try-with-resources to guarantee flush
     */
    // TODO: Implement LogWriter class here

    public static void main(String[] args) {
        System.out.println("=== File Reader With Resources Exercise ===\n");

        // TODO: Test readFileContents with a real file
        // TODO: Test copyFile
        // TODO: Test LogWriter in try-with-resources

        System.out.println("Implement the exercises above!");
    }
}
