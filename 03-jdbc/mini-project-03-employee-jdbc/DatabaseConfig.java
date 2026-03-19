/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  DATABASE CONFIG — HikariCP Connection Pool Setup              ║
 * ║  Mini-Project: 03-employee-jdbc                                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Pool Architecture:                                            ║
 * ║                                                                ║
 * ║  ┌──────────────────────────────────────────┐                  ║
 * ║  │         HikariCP Connection Pool         │                  ║
 * ║  │                                          │                  ║
 * ║  │  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐    │                  ║
 * ║  │  │ C1 │ │ C2 │ │ C3 │ │ C4 │ │ C5 │    │                  ║
 * ║  │  └────┘ └────┘ └────┘ └────┘ └────┘    │                  ║
 * ║  │  min=5               max=10             │                  ║
 * ║  └──────────────────────────────────────────┘                  ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  jdbc:postgresql://localhost:5432/springmastery                 ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    engine = create_engine(url, pool_size=5, max_overflow=5)    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.miniproject;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Centralized database configuration using HikariCP.
 *
 * <pre>
 *   Singleton Pattern:
 *   ──────────────────
 *   DatabaseConfig.getDataSource() → same HikariDataSource instance
 *   │
 *   └──► All DAO classes share this single pool
 * </pre>
 *
 * <p>Python equivalent: SQLAlchemy {@code create_engine()} at module level.
 */
public class DatabaseConfig {

    private static final HikariDataSource DATA_SOURCE;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/springmastery");
        config.setUsername("postgres");
        config.setPassword("postgres");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30_000);
        config.setIdleTimeout(600_000);
        config.setMaxLifetime(1_800_000);
        config.setLeakDetectionThreshold(60_000);
        config.setPoolName("EmployeeAppPool");

        DATA_SOURCE = new HikariDataSource(config);
    }

    /** Returns the shared DataSource (HikariCP pool). */
    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    /** Shuts down the pool — call on application exit. */
    public static void shutdown() {
        if (!DATA_SOURCE.isClosed()) {
            DATA_SOURCE.close();
        }
    }

    // Prevent instantiation
    private DatabaseConfig() {}
}
