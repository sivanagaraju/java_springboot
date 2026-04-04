/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_ConnectionPool.java                             ║
 * ║  SOLUTION FOR : Ex01_ConnectionPool.java                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  KEY LEARNINGS FROM THIS EXERCISE                               ║
 * ║  1. setJdbcUrl() sets the connection target (host/port/db)      ║
 * ║  2. setMaximumPoolSize() caps concurrent connections             ║
 * ║  3. HikariDataSource implements AutoCloseable (try-with-res.)   ║
 * ║  4. Connection returned to pool when try-with-resources exits   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.exercises.solutions;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * Solution for Ex01 — HikariCP Connection Pool Configuration.
 *
 * <p>The three missing pieces were:</p>
 * <ul>
 *   <li>setJdbcUrl("jdbc:postgresql://localhost:5432/springdb")</li>
 *   <li>setUsername("spring") + setPassword("spring")</li>
 *   <li>setMaximumPoolSize(10)</li>
 * </ul>
 */
public class Sol01_ConnectionPool {

    public static void main(String[] args) {
        HikariConfig config = new HikariConfig();

        // TODO 1 SOLUTION: JDBC URL format — jdbc:<driver>://<host>:<port>/<database>
        // jdbc:        → JDBC protocol prefix (always present)
        // postgresql:  → identifies which JDBC driver to use (Type 4 pure Java)
        // localhost:5432 → host and port of the PostgreSQL server
        // /springdb    → the database name to connect to
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/springdb");

        // TODO 2 SOLUTION: Credentials from our Docker container setup
        // In production, never hardcode credentials — use environment variables
        // or a secrets manager (AWS Secrets Manager, HashiCorp Vault, etc.)
        config.setUsername("spring");
        config.setPassword("spring");

        // TODO 3 SOLUTION: Pool size of 10
        // HikariCP will open up to 10 simultaneous connections to PostgreSQL.
        // Why 10? For a demo app with no concurrent load, even 2 would work.
        // Production rule: (2 × CPU cores) + effective_spindle_count is a common
        // starting point. Too high → PostgreSQL's max_connections exhausted.
        // Too low → connection wait time under load.
        config.setMaximumPoolSize(10);

        // HikariDataSource implements Closeable — use try-with-resources to
        // ensure the pool shuts down cleanly (closes all connections) on exit.
        // In Spring Boot, the container manages this lifecycle automatically.
        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            System.out.println("Pool initialized with " + config.getMaximumPoolSize() + " connections");

            // Connection is borrowed from the pool here, used, then automatically
            // returned when the try block exits — this is the key benefit of pooling.
            // Without pooling: open new TCP connection (~100ms) for every request.
            // With pooling: reuse existing connection (~1ms) for every request.
            try (Connection conn = dataSource.getConnection()) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Connected to: " + meta.getDatabaseProductName()
                        + " " + meta.getDatabaseProductVersion());
            } catch (SQLException e) {
                System.err.println("Failed to get connection: " + e.getMessage());
            }

            System.out.println("Pool closed.");
        }
    }
}

/*
 * COMMON MISTAKES
 * ===============
 *
 * 1. USING DriverManager INSTEAD OF DataSource
 *    Wrong:  Connection conn = DriverManager.getConnection(url, user, pass);
 *    Why it fails: DriverManager opens a brand new TCP connection every call
 *    (~50–200ms per connection). Under load this becomes a bottleneck and
 *    eventually exhausts OS file descriptors.
 *    Right: Always use HikariDataSource (or Spring's auto-configured DataSource).
 *
 * 2. FORGETTING TO CLOSE THE DATASOURCE
 *    Wrong:  HikariDataSource ds = new HikariDataSource(config);
 *            // ... use it ... never close
 *    Why it fails: HikariCP starts background health-check threads. If the
 *    DataSource is not closed, these threads keep running after main() exits,
 *    causing the JVM to hang.
 *    Right: Use try-with-resources, or call ds.close() in a finally block.
 *    In Spring Boot, the container handles this automatically via @Bean lifecycle.
 *
 * 3. HARDCODING CREDENTIALS
 *    Wrong:  config.setPassword("spring");  // committed to git
 *    Why it fails: Credentials in source code get committed, accidentally shared,
 *    and are impossible to rotate without a code change.
 *    Right: Read from environment variables or a secrets manager:
 *      config.setPassword(System.getenv("DB_PASSWORD"));
 *    In Spring Boot: spring.datasource.password=${DB_PASSWORD} in application.properties
 *
 * 4. SETTING maximumPoolSize TOO HIGH
 *    Wrong:  config.setMaximumPoolSize(100);
 *    Why it fails: PostgreSQL defaults to max_connections=100. If you run
 *    3 application pods × 100 pool size = 300 connections → PostgreSQL rejects them.
 *    Right: Start with 10, tune based on monitoring. Check PostgreSQL's
 *    pg_stat_activity view to see actual connection usage under load.
 */
