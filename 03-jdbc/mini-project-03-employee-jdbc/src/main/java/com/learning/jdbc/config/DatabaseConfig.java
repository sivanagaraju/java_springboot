/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  CONFIG — HikariCP Connection Pool Configuration               ║
 * ║  Mini-Project: 03-employee-jdbc / config                       ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Pool Lifecycle:                                               ║
 * ║                                                                ║
 * ║  getDataSource() [first call]                                  ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  HikariConfig → setJdbcUrl, setUsername, setMaxPoolSize        ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  new HikariDataSource(config) → pool initialized              ║
 * ║       │                                                        ║
 * ║       ▼   (subsequent calls)                                   ║
 * ║  return cached DataSource (singleton)                          ║
 * ║                                                                ║
 * ║  shutdown()                                                    ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  ds.close() → all idle connections released                    ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    engine = create_engine(url, pool_size=10)                   ║
 * ║    engine.dispose()                                            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * HikariCP connection pool — singleton DataSource provider.
 *
 * <pre>
 *   Without pooling:
 *   ┌──────────────────────────────────────────┐
 *   │  Each request → new TCP connection → DB  │
 *   │  Cost: ~50ms per connection              │
 *   │  1000 requests = 1000 connections!       │
 *   └──────────────────────────────────────────┘
 *
 *   With HikariCP:
 *   ┌──────────────────────────────────────────┐
 *   │  App → borrow from pool → return to pool │
 *   │  Cost: ~0.5ms per borrow                 │
 *   │  1000 requests = 10 connections reused   │
 *   └──────────────────────────────────────────┘
 * </pre>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   from sqlalchemy import create_engine
 *   engine = create_engine("postgresql://...", pool_size=10)
 * </pre>
 */
public final class DatabaseConfig {

    private static HikariDataSource dataSource;

    // Database credentials — in production, use env vars or config files
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "postgres";
    private static final int MAX_POOL_SIZE = 10;

    // Private constructor — static utility class
    private DatabaseConfig() {}

    /**
     * Returns the singleton DataSource, creating the pool on first call.
     *
     * <pre>
     *   Thread safety:
     *   synchronized ensures only one thread creates the pool.
     *   After creation, concurrent access is safe (HikariCP is thread-safe).
     * </pre>
     *
     * @return configured HikariCP DataSource
     */
    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(JDBC_URL);
            config.setUsername(USERNAME);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(MAX_POOL_SIZE);

            // Connection validation — detect stale connections
            config.setConnectionTestQuery("SELECT 1");

            // Pool naming — useful for monitoring in production
            config.setPoolName("EmployeeJdbcPool");

            dataSource = new HikariDataSource(config);
            System.out.println("[DatabaseConfig] Pool initialized: " + config.getPoolName()
                    + " (max " + MAX_POOL_SIZE + " connections)");
        }
        return dataSource;
    }

    /**
     * Shuts down the connection pool — releases all connections.
     *
     * <p>Must be called at application shutdown to avoid connection leaks.
     *
     * <p><b>Python equivalent:</b> {@code engine.dispose()}
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DatabaseConfig] Pool shut down.");
        }
    }
}
