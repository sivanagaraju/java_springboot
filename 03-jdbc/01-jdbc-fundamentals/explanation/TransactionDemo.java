/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  TRANSACTION DEMO — Manual JDBC Transaction Management         ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Transaction Flow (Bank Transfer):                             ║
 * ║                                                                ║
 * ║  conn.setAutoCommit(false)  ← Start manual transaction        ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  ┌─────────────────┐    ┌─────────────────┐                   ║
 * ║  │ DEBIT Account A │───►│ CREDIT Account B │                   ║
 * ║  │ balance -= $100 │    │ balance += $100  │                   ║
 * ║  └─────────────────┘    └────────┬────────┘                   ║
 * ║                                  │                             ║
 * ║                           ┌──────┴──────┐                     ║
 * ║                           ▼             ▼                      ║
 * ║                     ┌─────────┐   ┌──────────┐                ║
 * ║                     │ COMMIT  │   │ ROLLBACK │                ║
 * ║                     │ (both   │   │ (restore │                ║
 * ║                     │ applied)│   │ both)    │                ║
 * ║                     └─────────┘   └──────────┘                ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    conn = psycopg2.connect(dsn)  # autocommit=False by default║
 * ║    try:                                                        ║
 * ║        cursor.execute(debit_sql)                               ║
 * ║        cursor.execute(credit_sql)                              ║
 * ║        conn.commit()                                           ║
 * ║    except:                                                     ║
 * ║        conn.rollback()                                         ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.demo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * Demonstrates manual JDBC transaction management patterns.
 *
 * <p>Core principle: Multiple operations as one atomic unit.
 * <pre>
 *   autoCommit=true (default):   autoCommit=false (manual):
 *   ────────────────────────     ─────────────────────────
 *   INSERT → AUTO COMMIT        INSERT ─┐
 *   INSERT → AUTO COMMIT        INSERT  ├─► COMMIT or ROLLBACK
 *   INSERT → AUTO COMMIT        INSERT ─┘
 *   (each independent)          (all-or-nothing)
 * </pre>
 *
 * <p>Python difference: psycopg2 defaults to autocommit=False (safe!).
 * JDBC defaults to autocommit=True (dangerous for multi-statement ops!).
 */
public class TransactionDemo {

    private static final String URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    /**
     * Entry point — demonstrates transaction patterns.
     *
     * <pre>
     *   main()
     *    ├──► basicTransaction()     — commit/rollback pattern
     *    ├──► savepointDemo()        — partial rollback
     *    └──► isolationLevelDemo()   — set isolation level
     * </pre>
     */
    public static void main(String[] args) {
        System.out.println("=== Transaction Demo ===\n");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            basicTransaction(conn);
            savepointDemo(conn);
            isolationLevelDemo(conn);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * Pattern 1: Basic commit/rollback — bank transfer example.
     *
     * <pre>
     *   Account A: $500          Account B: $300
     *        │                        │
     *        ▼                        ▼
     *   DEBIT $100              CREDIT $100
     *   balance = $400          balance = $400
     *        │                        │
     *        └────────┬───────────────┘
     *                 ▼
     *            COMMIT (both)
     *         or ROLLBACK (neither)
     * </pre>
     */
    private static void basicTransaction(Connection conn) throws SQLException {
        System.out.println("--- Pattern 1: Basic Transaction (Bank Transfer) ---");

        conn.setAutoCommit(false);  // Start manual transaction control

        try {
            // Debit from account A
            try (PreparedStatement debit = conn.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?")) {
                debit.setBigDecimal(1, new BigDecimal("100.00"));
                debit.setLong(2, 1L);
                debit.setBigDecimal(3, new BigDecimal("100.00")); // Prevent negative balance

                int debitRows = debit.executeUpdate();
                if (debitRows == 0) {
                    conn.rollback();
                    System.out.println("Transfer FAILED: Insufficient funds (rolled back)");
                    return;
                }
            }

            // Credit to account B
            try (PreparedStatement credit = conn.prepareStatement(
                    "UPDATE accounts SET balance = balance + ? WHERE id = ?")) {
                credit.setBigDecimal(1, new BigDecimal("100.00"));
                credit.setLong(2, 2L);
                credit.executeUpdate();
            }

            conn.commit();  // Both operations succeed → make permanent
            System.out.println("Transfer COMMITTED: $100 from Account 1 to Account 2");

        } catch (SQLException e) {
            conn.rollback();  // Any failure → undo everything
            System.err.println("Transfer ROLLED BACK: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);  // Reset for connection pool reuse
        }
        System.out.println();
    }

    /**
     * Pattern 2: Savepoints — partial rollback within a transaction.
     *
     * <pre>
     *   BEGIN TRANSACTION
     *        │
     *        ▼
     *   INSERT Order (must succeed)
     *        │
     *        ▼
     *   SAVEPOINT ─────────────────────────┐
     *        │                             │
     *        ▼                             │
     *   INSERT Notification (optional)     │
     *        │                             │
     *   ┌────┴────┐                        │
     *   │ Success │    │ Failure │          │
     *   └────┬────┘    └────┬───┘          │
     *        │              │              │
     *        │         ROLLBACK TO ◄───────┘
     *        │         SAVEPOINT
     *        │              │
     *        └──────┬───────┘
     *               ▼
     *           COMMIT (Order saved, notification skipped if failed)
     * </pre>
     */
    private static void savepointDemo(Connection conn) throws SQLException {
        System.out.println("--- Pattern 2: Savepoint (Partial Rollback) ---");

        conn.setAutoCommit(false);
        Savepoint beforeNotification = null;

        try {
            // Critical operation — insert order
            try (PreparedStatement orderStmt = conn.prepareStatement(
                    "INSERT INTO orders (customer_id, total) VALUES (?, ?)")) {
                orderStmt.setLong(1, 42L);
                orderStmt.setBigDecimal(2, new BigDecimal("199.99"));
                orderStmt.executeUpdate();
                System.out.println("  Order inserted (critical operation)");
            }

            // Set savepoint before optional operation
            beforeNotification = conn.setSavepoint("before_notification");

            // Optional operation — send notification (may fail)
            try (PreparedStatement notifStmt = conn.prepareStatement(
                    "INSERT INTO notifications (user_id, message) VALUES (?, ?)")) {
                notifStmt.setLong(1, 42L);
                notifStmt.setString(2, "Your order has been placed!");
                notifStmt.executeUpdate();
                System.out.println("  Notification inserted (optional operation)");
            } catch (SQLException e) {
                // Notification failed — rollback ONLY the notification
                conn.rollback(beforeNotification);
                System.out.println("  Notification FAILED — rolled back to savepoint (order preserved)");
            }

            conn.commit();  // Commit order (and notification if it succeeded)
            System.out.println("  Transaction COMMITTED");

        } catch (SQLException e) {
            conn.rollback();  // Catastrophic failure — rollback everything
            System.err.println("  Transaction ROLLED BACK: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
        System.out.println();
    }

    /**
     * Pattern 3: Setting transaction isolation level.
     *
     * <pre>
     *   Isolation Levels (least → most strict):
     *   ─────────────────────────────────────────
     *   READ_UNCOMMITTED → dirty reads OK
     *   READ_COMMITTED   → PostgreSQL default ✓
     *   REPEATABLE_READ  → snapshot isolation
     *   SERIALIZABLE     → full isolation (slowest)
     * </pre>
     */
    private static void isolationLevelDemo(Connection conn) throws SQLException {
        System.out.println("--- Pattern 3: Isolation Levels ---");

        // Read current level
        int currentLevel = conn.getTransactionIsolation();
        System.out.println("Current isolation: " + isolationLevelName(currentLevel));

        // Set to REPEATABLE_READ for consistent reporting queries
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        System.out.println("Changed to: " + isolationLevelName(conn.getTransactionIsolation()));

        // Reset to default
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        System.out.println("Reset to: " + isolationLevelName(conn.getTransactionIsolation()));

        System.out.println();
    }

    /**
     * Converts numeric isolation level to readable name.
     */
    private static String isolationLevelName(int level) {
        return switch (level) {
            case Connection.TRANSACTION_NONE -> "NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED -> "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED -> "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ -> "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE -> "SERIALIZABLE";
            default -> "UNKNOWN (" + level + ")";
        };
    }
}
