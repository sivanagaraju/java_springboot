/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 02 — Batch Processing with Transactions              ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Batch Insert Flow:                                            ║
 * ║                                                                ║
 * ║  setAutoCommit(false)                                          ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  ┌─────────────────────────┐                                   ║
 * ║  │ Loop 1000 records:      │                                   ║
 * ║  │   setString(1, name)    │                                   ║
 * ║  │   addBatch()            │──► executeBatch() every 500       ║
 * ║  └─────────────────────────┘                                   ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  commit() ── or ── rollback() on error                         ║
 * ║                                                                ║
 * ║  Python: cursor.executemany(sql, list_of_tuples)               ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.exercises;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Exercise: Implement batch inserts with chunking and transactions.
 *
 * <pre>
 *   Performance comparison:
 *   ───────────────────────
 *   Individual INSERTs:  ~30ms × 1000 = 30 seconds
 *   Batch (all at once): ~200ms for all 1000
 *   Batch (chunked 500): ~250ms but uses less memory
 * </pre>
 */
public class Ex02_BatchProcessing {

    private static final String URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final int BATCH_SIZE = 500;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // TODO 1: Call batchInsertEmployees(conn, 1000)
            // TODO 2: Call batchInsertWithChunking(conn, 5000)
            // TODO 3: Compare timing between the two approaches
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * TODO 4: Simple batch — add all, execute once.
     *
     * <pre>
     *   Steps:
     *   1. setAutoCommit(false)
     *   2. Loop: setString/setBigDecimal + addBatch()
     *   3. executeBatch()
     *   4. commit()
     *   5. In catch: rollback()
     *   6. In finally: setAutoCommit(true)
     * </pre>
     */
    private static void batchInsertEmployees(Connection conn, int count) throws SQLException {
        // TODO: Implement using addBatch() + executeBatch()
        throw new UnsupportedOperationException("TODO: Implement");
    }

    /**
     * TODO 5: Chunked batch — execute every BATCH_SIZE rows.
     *
     * <pre>
     *   Why chunking?
     *   ─────────────
     *   addBatch() stores SQL in memory.
     *   100,000 rows = potential OutOfMemoryError.
     *   Solution: executeBatch() + clearBatch() every 500 rows.
     *
     *   Loop:
     *     if (i % BATCH_SIZE == 0) {
     *         pstmt.executeBatch();
     *         pstmt.clearBatch();    ← Free memory!
     *     }
     * </pre>
     */
    private static void batchInsertWithChunking(Connection conn, int count) throws SQLException {
        // TODO: Implement with executeBatch() every BATCH_SIZE rows
        // Don't forget to execute remaining rows after the loop!
        throw new UnsupportedOperationException("TODO: Implement");
    }
}
