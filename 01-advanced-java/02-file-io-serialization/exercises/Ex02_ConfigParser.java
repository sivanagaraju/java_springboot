import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 2: Config Parser                                  ║
 * ║  Parse a key=value config file into a Map                   ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * TASK: Parse a .properties-style config file.
 *
 * Requirements:
 * 1. Read file with BufferedReader or Files.readAllLines()
 * 2. Ignore blank lines and lines starting with #
 * 3. Split on first '=' to get key and value
 * 4. Trim whitespace from keys and values
 * 5. Store in Map<String, String>
 * 6. Support get(key), getOrDefault(key, default), getInt(key)
 *
 * Sample input file:
 *   # Database config
 *   db.host = localhost
 *   db.port = 5432
 *   db.name = myapp
 *
 *   # Server config
 *   server.port = 8080
 *
 * Expected Output:
 *   db.host → localhost
 *   db.port → 5432 (int: 5432)
 *   server.port → 8080
 *
 * HINTS:
 * - line.split("=", 2) limits split to first = only
 * - Integer.parseInt() for getInt()
 * - Think about how Spring loads application.properties!
 */
public class Ex02_ConfigParser {
    public static void main(String[] args) {
        // TODO: Create sample config file in temp directory
        // TODO: Parse with line filtering + splitting
        // TODO: Implement ConfigMap with get/getOrDefault/getInt
        // TODO: Print all config entries
        System.out.println("Exercise 2: Implement the Config Parser!");
    }
}
