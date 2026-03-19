/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  HIKARICP DEMO — Connection Pool Configuration & Monitoring    ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  HikariCP Pool Lifecycle:                                      ║
 * ║                                                                ║
 * ║  ┌───────────────────────────────────────────────┐             ║
 * ║  │            HikariCP Connection Pool           │             ║
 * ║  │                                               │             ║
 * ║  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐        │             ║
 * ║  │  │ Conn │ │ Conn │ │ Conn │ │ Conn │  ...    │             ║
 * ║  │  │  #1  │ │  #2  │ │  #3  │ │  #4  │        │             ║
 * ║  │  │ IDLE │ │ BUSY │ │ IDLE │ │ BUSY │        │             ║
 * ║  │  └──────┘ └──────┘ └──────┘ └──────┘        │             ║
 * ║  │                                               │             ║
 * ║  │  Thread A ──borrow──► Conn #1                 │             ║
 * ║  │  Thread B ◄──return── Conn #2                 │             ║
 * ║  └───────────────────────────────────────────────┘             ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    engine = create_engine(url, pool_size=10, max_overflow=5)   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.demo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Demonstrates HikariCP connection pool setup and usage.
 *
 * <p>Architecture with HikariCP:
 * <pre>
 *   ┌─────────────┐    ┌──────────────┐    ┌──────────┐
 *   │ Application  │───►│  HikariCP    │───►│ Database │
 *   │ (this class) │    │  Pool (10)   │    │(Postgres)│
 *   │              │◄───│              │◄───│          │
 *   └─────────────┘    └──────────────┘    └──────────┘
 *                       │ borrow ~0.2ms │
 *                       │ vs new ~30ms  │
 * </pre>
 *
 * <p>Python equivalent:
 * <pre>
 *   from sqlalchemy import create_engine
 *   engine = create_engine("postgresql://...", pool_size=10,
 *                          max_overflow=5, pool_timeout=30)
 *   with engine.connect() as conn:
 *       result = conn.execute("SELECT 1")
 * </pre>
 */
public class HikariCPDemo {

    /**
     * Entry point — demonstrates HikariCP setup and pool metrics.
     *
     * <pre>
     *   main()
     *    ├──► createPool()             — configure HikariCP
     *    ├──► usePooledConnection()    — borrow and return
     *    └──► printPoolMetrics()       — monitor pool health
     * </pre>
     */
    public static void main(String[] args) {
        System.out.println("=== HikariCP Demo ===\n");

        // Create a configured pool
        HikariDataSource dataSource = createPool();

        try {
            usePooledConnection(dataSource);
            simulateConcurrentAccess(dataSource);
            printPoolMetrics(dataSource);
        } finally {
            dataSource.close();  // Shutdown pool — close all connections
            System.out.println("Pool shut down.");
        }
    }

    /**
     * Creates and configures a HikariCP connection pool.
     *
     * <pre>
     *   HikariConfig settings:
     *   ─────────────────────
     *   maximumPoolSize = 10     ← Max concurrent connections
     *   minimumIdle = 5          ← Keep 5 idle (pre-warmed)
     *   connectionTimeout = 30s  ← Wait time for a connection
     *   idleTimeout = 10min      ← Close idle connections after this
     *   maxLifetime = 30min      ← Recreate connections after this
     *   leakDetection = 60s      ← Log warning if held >60s
     *   poolName = "DemoPool"    ← Name for metrics/logging
     * </pre>
     *
     * Python equivalent:
     * {@code create_engine(url, pool_size=10, max_overflow=5, pool_timeout=30)}
     */
    private static HikariDataSource createPool() {
        System.out.println("--- Creating HikariCP Pool ---");

        HikariConfig config = new HikariConfig();

        // Connection config
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/springmastery");
        config.setUsername("postgres");
        config.setPassword("postgres");

        // Pool sizing
        config.setMaximumPoolSize(10);    // Max connections in pool
        config.setMinimumIdle(5);         // Keep 5 connections idle and ready

        // Timeouts
        config.setConnectionTimeout(30_000);  // Wait 30s for a connection (ms)
        config.setIdleTimeout(600_000);       // Close idle connections after 10min
        config.setMaxLifetime(1_800_000);     // Recreate connections after 30min

        // Leak detection (identifies code that borrows but never returns connections)
        config.setLeakDetectionThreshold(60_000);  // Warn if held >60s

        // Identification
        config.setPoolName("DemoPool");

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");  // PostgreSQL validation query

        // PostgreSQL-specific optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HikariDataSource ds = new HikariDataSource(config);
        System.out.println("Pool created: " + ds.getPoolName());
        System.out.println();
        return ds;
    }

    /**
     * Uses a pooled connection — borrow, execute, return.
     *
     * <pre>
     *   getConnection()  ──► Execute SQL ──► close()
     *      (borrow)                          (return to pool)
     *      ~0.2ms                            ~0.05ms
     *
     *   Note: close() does NOT destroy the connection —
     *   it RETURNS it to the pool for reuse!
     * </pre>
     */
    private static void usePooledConnection(HikariDataSource ds) {
        System.out.println("--- Using Pooled Connection ---");

        long start = System.nanoTime();

        // try-with-resources: close() returns the connection to the pool
        try (Connection conn = ds.getConnection()) {
            long borrowTime = (System.nanoTime() - start) / 1_000; // microseconds

            try (PreparedStatement pstmt = conn.prepareStatement("SELECT 1 AS result")) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Query result: " + rs.getInt("result"));
                    }
                }
            }

            System.out.println("Connection borrow time: " + borrowTime + " μs");
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        // Connection returned to pool here (NOT closed!)
        System.out.println();
    }

    /**
     * Simulates concurrent access to demonstrate pool sharing.
     *
     * <pre>
     *   Thread-1 ──borrow──► Conn #1 ──return──►
     *   Thread-2 ──borrow──► Conn #2 ──return──►
     *   Thread-3 ──borrow──► Conn #3 ──return──►
     *   Thread-4 ──borrow──► Conn #1 (reused!) ──return──►
     *   Thread-5 ──borrow──► Conn #2 (reused!) ──return──►
     * </pre>
     */
    private static void simulateConcurrentAccess(HikariDataSource ds) {
        System.out.println("--- Simulating Concurrent Access ---");

        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            final int threadId = i + 1;
            threads[i] = new Thread(() -> {
                try (Connection conn = ds.getConnection()) {
                    try (PreparedStatement pstmt = conn.prepareStatement("SELECT pg_sleep(0.1)")) {
                        pstmt.execute();
                    }
                    System.out.println("  Thread-" + threadId + " completed (conn: "
                            + conn.toString().substring(conn.toString().lastIndexOf('@')) + ")");
                } catch (SQLException e) {
                    System.err.println("  Thread-" + threadId + " error: " + e.getMessage());
                }
            });
            threads[i].start();
        }

        // Wait for all threads
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
    }

    /**
     * Prints current pool health metrics.
     *
     * <pre>
     *   Pool Metrics:
     *   ─────────────
     *   Total:   10  ← Total connections managed
     *   Active:   0  ← Currently borrowed by threads
     *   Idle:    10  ← Available for borrowing
     *   Waiting:  0  ← Threads waiting for a connection
     * </pre>
     */
    private static void printPoolMetrics(HikariDataSource ds) {
        System.out.println("--- Pool Metrics ---");

        var pool = ds.getHikariPoolMXBean();
        if (pool != null) {
            System.out.println("Total connections:   " + pool.getTotalConnections());
            System.out.println("Active connections:  " + pool.getActiveConnections());
            System.out.println("Idle connections:    " + pool.getIdleConnections());
            System.out.println("Waiting threads:     " + pool.getThreadsAwaitingConnection());
        }
        System.out.println();
    }
}
