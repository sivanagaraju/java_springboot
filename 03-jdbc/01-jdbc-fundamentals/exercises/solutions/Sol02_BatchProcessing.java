/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 02 — Batch Processing with Transactions              ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals / exercises       ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Batch Insert Architecture:                                    ║
 * ║                                                                ║
 * ║  Simple batch:                                                 ║
 * ║  ┌───────────────────────────────────────────────────┐         ║
 * ║  │ setAutoCommit(false)                               │         ║
 * ║  │   ▼                                                │         ║
 * ║  │ Loop N records:                                    │         ║
 * ║  │   setString(1, name) + addBatch()                  │         ║
 * ║  │   ▼                                                │         ║
 * ║  │ executeBatch()  ← single network round-trip        │         ║
 * ║  │   ▼                                                │         ║
 * ║  │ commit()                                           │         ║
 * ║  └───────────────────────────────────────────────────┘         ║
 * ║                                                                ║
 * ║  Chunked batch (memory-safe):                                 ║
 * ║  ┌───────────────────────────────────────────────────┐         ║
 * ║  │ Loop N records:                                    │         ║
 * ║  │   addBatch()                                       │         ║
 * ║  │   if (i % 500 == 0) {                              │         ║
 * ║  │       executeBatch() ← flush to DB                 │         ║
 * ║  │       clearBatch()   ← free memory                 │         ║
 * ║  │   }                                                │         ║
 * ║  │ executeBatch()  ← remaining rows                   │         ║
 * ║  │ commit()                                           │         ║
 * ║  └───────────────────────────────────────────────────┘         ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    cursor.executemany(sql, list_of_tuples)                     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.exercises.solutions;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Complete solution for Exercise 02 — Batch Processing.
 *
 * <pre>
 *   Performance comparison:
 *   ───────────────────────
 *   Individual INSERTs:  ~30ms × 1000 = 30 seconds  (1000 network round-trips)
 *   Batch (all at once): ~200ms for all 1000         (1 network round-trip)
 *   Batch (chunked 500): ~250ms but uses less memory (2 round-trips)
 * </pre>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python psycopg2 — executemany
 *   data = [("Alice", "Eng", 95000), ("Bob", "Mktg", 82000)]
 *   cursor.executemany(
 *       "INSERT INTO employees (name, department, salary) VALUES (%s, %s, %s)",
 *       data
 *   )
 *   conn.commit()
 * </pre>
 *
 * <p><b>ASCII — Why batch is faster:</b>
 * <pre>
 *   Individual INSERTs:              Batch INSERT:
 *   App → DB  (INSERT #1)           App → DB  (INSERT #1..#1000)
 *   App → DB  (INSERT #2)                ↑
 *   App → DB  (INSERT #3)           Single network round-trip!
 *   ...  × 1000                     ~200ms total vs ~30 seconds
 * </pre>
 */
public class Sol02_BatchProcessing {

    private static final String URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final int BATCH_SIZE = 500;

    /**
     * Entry point — runs both batch insert approaches and compares timing.
     *
     * <pre>
     *   main()
     *    ├──► batchInsertEmployees(conn, 1000)    — simple batch
     *    └──► batchInsertWithChunking(conn, 5000)  — chunked batch
     * </pre>
     */
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("=== Batch Processing Solution ===\n");

            // Simple batch
            long start1 = System.currentTimeMillis();
            batchInsertEmployees(conn, 1000);
            long elapsed1 = System.currentTimeMillis() - start1;
            System.out.println("Simple batch (1000 rows): " + elapsed1 + "ms\n");

            // Chunked batch
            long start2 = System.currentTimeMillis();
            batchInsertWithChunking(conn, 5000);
            long elapsed2 = System.currentTimeMillis() - start2;
            System.out.println("Chunked batch (5000 rows): " + elapsed2 + "ms\n");

            // Cleanup
            try (Statement stmt = conn.createStatement()) {
                int deleted = stmt.executeUpdate("DELETE FROM employees WHERE name LIKE 'BatchEmp_%'");
                System.out.println("Cleanup: deleted " + deleted + " test rows");
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
        }
    }

    /**
     * Simple batch — accumulate all rows, execute once.
     *
     * <pre>
     *   Steps:
     *   ──────
     *   1. setAutoCommit(false)     ← manual transaction
     *   2. Loop count times:
     *      │  setString(1, name)
     *      │  setString(2, dept)
     *      │  setBigDecimal(3, salary)
     *      └► addBatch()            ← accumulates in memory
     *   3. executeBatch()           ← single DB round-trip
     *   4. commit()                 ← make changes permanent
     *   5. catch → rollback()       ← undo ALL on error
     *   6. finally → setAutoCommit(true) ← restore default
     * </pre>
     *
     * <p><b>Warning:</b> For very large batches (100K+ rows), this accumulates
     * all SQL in memory. Use {@link #batchInsertWithChunking} instead.
     *
     * @param conn  active database connection
     * @param count number of employees to insert
     * @throws SQLException if batch execution fails
     */
    private static void batchInsertEmployees(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
        String[] departments = {"Engineering", "Marketing", "Sales", "HR", "Finance"};

        conn.setAutoCommit(false);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setString(1, "BatchEmp_" + i);
                pstmt.setString(2, departments[i % departments.length]);
                pstmt.setBigDecimal(3, new BigDecimal(50000 + (i * 100)));
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            conn.commit();
            System.out.println("  Inserted " + results.length + " rows (simple batch)");

        } catch (SQLException e) {
            conn.rollback();   // Undo everything if ANY insert fails
            throw e;
        } finally {
            conn.setAutoCommit(true);  // Always restore default!
        }
    }

    /**
     * Chunked batch — execute every BATCH_SIZE rows to control memory.
     *
     * <pre>
     *   Why chunking?
     *   ─────────────
     *   addBatch() stores SQL in JVM heap memory.
     *   100,000 rows × ~500 bytes/row = ~50 MB in memory!
     *
     *   Solution: executeBatch() + clearBatch() every 500 rows.
     *
     *   Memory usage:
     *   ┌──────────────────────────────────────────────┐
     *   │  Simple batch:   ████████████████████ 50 MB  │
     *   │  Chunked (500):  ██ 0.25 MB (max at a time) │
     *   └──────────────────────────────────────────────┘
     *
     *   Loop:
     *     addBatch()
     *     if (i % BATCH_SIZE == 0) {
     *         executeBatch();   ← flush to DB
     *         clearBatch();     ← free memory!
     *     }
     *   executeBatch();         ← remaining rows (count % BATCH_SIZE)
     * </pre>
     *
     * @param conn  active database connection
     * @param count number of employees to insert
     * @throws SQLException if batch execution fails
     */
    private static void batchInsertWithChunking(Connection conn, int count) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
        String[] departments = {"Engineering", "Marketing", "Sales", "HR", "Finance"};
        int totalInserted = 0;

        conn.setAutoCommit(false);

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= count; i++) {
                pstmt.setString(1, "BatchEmp_" + i);
                pstmt.setString(2, departments[i % departments.length]);
                pstmt.setBigDecimal(3, new BigDecimal(50000 + (i * 100)));
                pstmt.addBatch();

                // Flush every BATCH_SIZE rows
                if (i % BATCH_SIZE == 0) {
                    int[] results = pstmt.executeBatch();
                    totalInserted += results.length;
                    pstmt.clearBatch();  // Free memory!
                }
            }

            // Execute remaining rows (if count is not evenly divisible by BATCH_SIZE)
            int[] remaining = pstmt.executeBatch();
            totalInserted += remaining.length;

            conn.commit();
            System.out.println("  Inserted " + totalInserted + " rows (chunked, batch size = " + BATCH_SIZE + ")");

        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
