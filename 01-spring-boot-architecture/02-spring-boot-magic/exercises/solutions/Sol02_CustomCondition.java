package exercises.solutions;

import java.util.HashMap;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Sol02_CustomCondition.java                                                         ║
 * ║ MODULE: 01-spring-boot-architecture / 02-spring-boot-magic / exercises/solutions         ║
 * ║ GRADLE: ./gradlew :01-spring-boot-architecture:run                                       ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ PURPOSE: Solution — Simulating @Conditional and @ConditionalOnProperty                   ║
 * ║ DIFFICULTY: Intermediate                                                                  ║
 * ║ PYTHON COMPARE: if os.getenv('database.type') == 'mysql': db = MySQLDB() else: db = H2() ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ ASCII DIAGRAM: @Conditional Evaluation at Application Startup                            ║
 * ║                                                                                          ║
 * ║   [application.properties]                                                               ║
 * ║          │ database.type = mysql                                                         ║
 * ║          ▼                                                                               ║
 * ║   [Spring Condition Evaluator]                                                           ║
 * ║          │                                                                               ║
 * ║          ├── @ConditionalOnProperty(name="database.type", havingValue="mysql")           ║
 * ║          │              │                                                                ║
 * ║          │         MATCH? YES ──► Register MySQLDatabase bean                            ║
 * ║          │         MATCH? NO  ──► Skip bean                                              ║
 * ║          │                                                                               ║
 * ║          └── @ConditionalOnMissingBean(Database.class)                                   ║
 * ║                         └──► Register InMemH2Database as fallback                        ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 */
public class Sol02_CustomCondition {

    /**
     * Entry point — demonstrates conditional bean selection based on environment config.
     *
     * <p>This pattern is identical to what Spring Boot auto-configuration does:
     * check properties → conditionally register beans → inject whichever qualified.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("=== Scenario 1: MySQL configured ===");
        var mysqlProps = new HashMap<String, String>();
        mysqlProps.put("database.type", "mysql");
        autoConfigureDatabase(mysqlProps).connect();

        System.out.println("\n=== Scenario 2: No property set (fallback to H2) ===");
        var emptyProps = new HashMap<String, String>();
        autoConfigureDatabase(emptyProps).connect();

        System.out.println("\n=== Scenario 3: Unknown value (fallback to H2) ===");
        var unknownProps = new HashMap<String, String>();
        unknownProps.put("database.type", "oracle");
        autoConfigureDatabase(unknownProps).connect();

        // EXPECTED OUTPUT:
        // === Scenario 1: MySQL configured ===
        // [Auto-Config]: Condition Match for MySQL!
        // Connecting to Remote MySQL via TCP/IP 3306...
        //
        // === Scenario 2: No property set (fallback to H2) ===
        // [Auto-Config]: Fallback Condition. Defaulting to H2 In-Memory DB...
        // Building Fast In-Memory Database for Development...
        //
        // === Scenario 3: Unknown value (fallback to H2) ===
        // [Auto-Config]: Fallback Condition. Defaulting to H2 In-Memory DB...
        // Building Fast In-Memory Database for Development...
    }

    /**
     * Simulates Spring Boot's @Conditional bean registration logic.
     *
     * <p>In real Spring Boot this pattern looks like:
     * <pre>
     *   {@code @Bean}
     *   {@code @ConditionalOnProperty(name = "database.type", havingValue = "mysql")}
     *   public Database mysqlDatabase() { return new MySQLDatabase(); }
     *
     *   {@code @Bean}
     *   {@code @ConditionalOnMissingBean(Database.class)}
     *   public Database h2Database() { return new InMemH2Database(); }
     * </pre>
     *
     * @param env the simulated application properties (non-null)
     * @return the appropriate Database implementation
     */
    public static Database autoConfigureDatabase(Map<String, String> env) {
        // WHY equalsIgnoreCase? Properties are case-insensitive by convention.
        // "MySQL", "mysql", "MYSQL" should all match.
        var dbType = env.get("database.type");

        if ("mysql".equalsIgnoreCase(dbType)) {
            System.out.println("[Auto-Config]: Condition Match for MySQL!");
            return new MySQLDatabase();
        } else {
            // WHY default to H2? Same reason Spring Boot auto-configures H2 in-memory
            // when no datasource is configured — safe default for development/testing.
            System.out.println("[Auto-Config]: Fallback Condition. Defaulting to H2 In-Memory DB...");
            return new InMemH2Database();
        }
    }

    // ─── Database Interface (Bean Contract) ───────────────────────────────────

    /**
     * Contract for database connections.
     *
     * <p>WHY interface? {@code @Conditional} beans are always injected by type.
     * Having multiple beans implement the same interface lets Spring pick the right
     * one at startup based on conditions, without the caller knowing which impl is active.
     */
    public interface Database {
        /**
         * Establishes a database connection.
         */
        void connect();
    }

    // ─── Conditional Bean Implementations ─────────────────────────────────────

    /**
     * Production MySQL database connection.
     *
     * <p>In Spring Boot: {@code @Bean @ConditionalOnProperty(name="database.type", havingValue="mysql")}
     */
    public static class MySQLDatabase implements Database {
        @Override
        public void connect() {
            System.out.println("Connecting to Remote MySQL via TCP/IP 3306...");
        }
    }

    /**
     * Development / test H2 in-memory database.
     *
     * <p>In Spring Boot: {@code @Bean @ConditionalOnMissingBean(Database.class)}
     * — registered only when no other Database bean was registered first.
     */
    public static class InMemH2Database implements Database {
        @Override
        public void connect() {
            System.out.println("Building Fast In-Memory Database for Development...");
        }
    }
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Using == for String comparison in conditions
 *   WRONG: if (dbType == "mysql")
 *   WHY BAD: String == compares references, not values. Will always be false
 *            for property values loaded from files (different String objects).
 *   FIX: Use "mysql".equals(dbType) or "mysql".equalsIgnoreCase(dbType).
 *
 * MISTAKE 2: @ConditionalOnProperty without matchIfMissing for optional properties
 *   If the property is absent, @ConditionalOnProperty defaults to NOT matching.
 *   Add matchIfMissing=true to register the bean when the property is not set at all:
 *   @ConditionalOnProperty(name="feature.flag", havingValue="true", matchIfMissing=false)
 *
 * MISTAKE 3: Forgetting that condition evaluation order matters in auto-configuration
 *   @ConditionalOnMissingBean checks if a bean of that type ALREADY exists in the context.
 *   If your primary bean is defined after the fallback in the same config class,
 *   the fallback registers first. Order matters — use @Order or @AutoConfigureAfter.
 *
 * MISTAKE 4: Using @Profile instead of @ConditionalOnProperty for feature flags
 *   @Profile("mysql") switches the ENTIRE profile.
 *   @ConditionalOnProperty switches individual beans — more fine-grained.
 *   Use @Profile for environment-level switches (dev/test/prod).
 *   Use @ConditionalOnProperty for feature-level switches within an environment.
 */
