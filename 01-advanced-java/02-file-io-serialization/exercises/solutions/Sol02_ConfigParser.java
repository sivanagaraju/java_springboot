import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 2: Config Parser                                          ║
 * ║  Parse .properties-style config file into typed ConfigMap           ║
 * ║                                                                      ║
 * ║  ASCII FLOW:                                                         ║
 * ║                                                                      ║
 * ║   config.properties file:                                           ║
 * ║   # Comment (skip)                                                   ║
 * ║   db.host = localhost                                                ║
 * ║   db.port = 5432                                                     ║
 * ║                                                                      ║
 * ║   Parse flow:                                                        ║
 * ║   line → skip blank/comment                                          ║
 * ║         → split on first '=' (limit=2)                              ║
 * ║         → trim key + value                                           ║
 * ║         → put in Map                                                 ║
 * ║                                                                      ║
 * ║   Access: config.get("db.host") → "localhost"                       ║
 * ║            config.getInt("db.port") → 5432                          ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */

/**
 * Demonstrates line-by-line file parsing with BufferedReader.
 * This is exactly how Spring Boot loads application.properties at startup.
 */
public class Sol02_ConfigParser {

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 1: ConfigMap — typed accessor wrapping Map<String, String>
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Thread-safe, typed configuration map.
     * Mirrors Spring's {@code Environment} API: get(), getOrDefault(), typed getters.
     */
    static class ConfigMap {

        // WHY LinkedHashMap: preserves insertion order for deterministic iteration/printing
        private final Map<String, String> properties = new LinkedHashMap<>();

        /**
         * Loads configuration from a .properties-style file.
         * Handles: blank lines, comment lines (# prefix), key=value pairs.
         *
         * @param path path to config file
         * @return populated ConfigMap
         * @throws IOException if file cannot be read
         */
        public static ConfigMap load(Path path) throws IOException {
            ConfigMap config = new ConfigMap();

            // WHY BufferedReader: reads line-by-line with buffer, not char-by-char
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();

                    // WHY skip blank lines and comments: standard .properties format
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                        continue;
                    }

                    // WHY split limit=2: "key=value=with=equals" → ["key", "value=with=equals"]
                    // Without limit, "url=jdbc:mysql://host:3306/db" splits into 4 parts!
                    String[] parts = trimmed.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        config.properties.put(key, value);
                    }
                    // WHY ignore malformed lines: tolerant parsing for robustness
                }
            }

            return config;
        }

        /**
         * Returns the value for the given key.
         *
         * @param key configuration key
         * @return value, or null if key not present
         */
        public String get(String key) {
            return properties.get(key);
        }

        /**
         * Returns the value for the given key, or a default if missing.
         *
         * @param key          configuration key
         * @param defaultValue value to return if key is absent
         * @return value or defaultValue
         */
        public String getOrDefault(String key, String defaultValue) {
            return properties.getOrDefault(key, defaultValue);
        }

        /**
         * Returns the value for the given key parsed as an integer.
         *
         * @param key configuration key whose value is an integer
         * @return parsed integer value
         * @throws NumberFormatException if value is not a valid integer
         * @throws NoSuchElementException if key is not present
         */
        public int getInt(String key) {
            String value = properties.get(key);
            if (value == null) {
                throw new NoSuchElementException("Config key not found: " + key);
            }
            // WHY parseInt here, not at load time: properties are strings by nature;
            // type conversion happens at the access boundary (caller knows the type)
            return Integer.parseInt(value);
        }

        /**
         * Returns all configuration entries for inspection/printing.
         *
         * @return unmodifiable view of all entries
         */
        public Set<Map.Entry<String, String>> entries() {
            return Collections.unmodifiableSet(properties.entrySet());
        }

        @Override
        public String toString() {
            return properties.toString();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 2: Main — creates sample config file, parses, accesses values
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        // Create sample config file in temp directory
        Path configPath = Path.of(System.getProperty("java.io.tmpdir"), "app.properties");

        // WHY text block: multi-line string without concatenation — clean and readable
        Files.writeString(configPath, """
            # Database config
            db.host = localhost
            db.port = 5432
            db.name = myapp
            db.url = jdbc:postgresql://localhost:5432/myapp

            # Server config
            server.port = 8080
            server.timeout = 30
            """);

        System.out.println("Loading config from: " + configPath);

        ConfigMap config = ConfigMap.load(configPath);

        // Basic access
        System.out.println("\n--- String values ---");
        System.out.println("db.host  → " + config.get("db.host"));
        System.out.println("db.url   → " + config.get("db.url"));

        // getOrDefault for optional properties
        System.out.println("db.pass  → " + config.getOrDefault("db.pass", "(not set)"));

        // Typed getInt access
        System.out.println("\n--- Integer values ---");
        System.out.println("db.port      → " + config.getInt("db.port"));
        System.out.println("server.port  → " + config.getInt("server.port"));

        // Print all entries
        System.out.println("\n--- All config entries ---");
        config.entries().forEach(e ->
            System.out.printf("  %-25s = %s%n", e.getKey(), e.getValue())
        );

        // Demonstrate NumberFormatException
        try {
            config.getInt("db.host");  // "localhost" is not an int
        } catch (NumberFormatException e) {
            System.out.println("\nExpected: " + e.getMessage());
        }
    }
}

/* ─────────────────────────────────────────────────────────────────────────────
   COMMON MISTAKES

   MISTAKE 1: split("=") without limit
     "url=jdbc:postgresql://host:5432/db".split("=") → 4 parts!
     Fix: always split("=", 2) — splits on first '=' only.

   MISTAKE 2: Not trimming whitespace
     "  db.host  =  localhost  " → key " db.host " with spaces
     Fix: parts[0].trim() and parts[1].trim()

   MISTAKE 3: Using FileReader without BufferedReader
     new FileReader(path.toFile())  // unbuffered — slow
     Fix: Files.newBufferedReader(path) — buffered + handles encoding correctly

   MISTAKE 4: Failing silently on malformed lines
     if (parts.length != 2) throw exception  // crashes on empty or comment lines
     Fix: skip malformed lines; already handled by blank/comment checks above

   MISTAKE 5: parseInt at load time
     properties.put(key, Integer.parseInt(value))  // assumes all values are ints!
     Config files are text — convert at access time when you know the type.
   ───────────────────────────────────────────────────────────────────────────── */
