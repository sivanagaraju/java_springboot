/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  PREPARED STATEMENT DEMO — Parameterized Queries & Batching    ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  PreparedStatement Flow:                                       ║
 * ║                                                                ║
 * ║  conn.prepareStatement("INSERT INTO t (a,b) VALUES (?,?)")     ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  ┌──────────────────────────────────────────┐                  ║
 * ║  │ DB compiles SQL plan (parse + optimize)  │  ONE time        ║
 * ║  └──────────┬───────────────────────────────┘                  ║
 * ║             ▼                                                  ║
 * ║  ┌──────────────────┐     ┌──────────────────┐                ║
 * ║  │ setString(1, val)│ ──► │ executeUpdate()   │  REUSE plan   ║
 * ║  │ setInt(2, val)   │     │ or executeQuery() │               ║
 * ║  └──────────────────┘     └──────────────────┘                ║
 * ║             │                      │                           ║
 * ║             └──── Loop with new ───┘                           ║
 * ║                   parameters                                   ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    cursor.execute("INSERT INTO t VALUES (%s, %s)", (a, b))     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.demo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Demonstrates PreparedStatement for safe, efficient SQL execution.
 *
 * <p>Key concepts:
 * <pre>
 *   Statement            PreparedStatement
 *   ─────────            ─────────────────
 *   SQL as string        SQL with ? placeholders
 *   Compile every time   Compile once, run many
 *   SQL injection risk!  SQL injection safe ✓
 *   No type checking     Type-safe setters
 * </pre>
 *
 * <p>Python equivalent:
 * <pre>
 *   # SAFE — parameterized
 *   cursor.execute("SELECT * FROM products WHERE name = %s", (name,))
 *
 *   # UNSAFE — string formatting
 *   cursor.execute(f"SELECT * FROM products WHERE name = '{name}'")
 * </pre>
 */
public class PreparedStatementDemo {

    private static final String URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    /**
     * Entry point — demonstrates CRUD + batching with PreparedStatement.
     *
     * <pre>
     *   main()
     *    ├──► insertProduct()           — single INSERT
     *    ├──► queryProducts()           — SELECT with parameters
     *    ├──► updateProduct()           — UPDATE with parameters
     *    └──► batchInsertProducts()     — Batch INSERT (fast!)
     * </pre>
     */
    public static void main(String[] args) {
        System.out.println("=== PreparedStatement Demo ===\n");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            insertProduct(conn);
            queryProducts(conn);
            updateProduct(conn);
            batchInsertProducts(conn);
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    /**
     * INSERT with PreparedStatement — type-safe parameter binding.
     *
     * <pre>
     *   "INSERT INTO products (name, price, category, created_at) VALUES (?, ?, ?, ?)"
     *                                                                     1   2   3   4
     *   setString(1, "Keyboard")     ──► name = 'Keyboard'
     *   setBigDecimal(2, 49.99)      ──► price = 49.99
     *   setString(3, "ELECTRONICS")  ──► category = 'ELECTRONICS'
     *   setTimestamp(4, now())        ──► created_at = '2024-01-15 10:30:00'
     * </pre>
     */
    private static void insertProduct(Connection conn) throws SQLException {
        System.out.println("--- INSERT Demo ---");

        String sql = "INSERT INTO products (name, price, category, created_at) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "Mechanical Keyboard");
            pstmt.setBigDecimal(2, new BigDecimal("89.99"));
            pstmt.setString(3, "ELECTRONICS");
            pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s)");
        }
        System.out.println();
    }

    /**
     * SELECT with parameters — demonstrating ResultSet iteration.
     *
     * <pre>
     *   "SELECT id, name, price FROM products WHERE category = ? AND price < ?"
     *                                                            1            2
     *   ┌─────────────────────────────────────────┐
     *   │ ResultSet (cursor BEFORE first row)     │
     *   │                                         │
     *   │  rs.next() → true  ──► Read row 1       │
     *   │  rs.next() → true  ──► Read row 2       │
     *   │  rs.next() → false ──► Exit loop        │
     *   └─────────────────────────────────────────┘
     * </pre>
     */
    private static void queryProducts(Connection conn) throws SQLException {
        System.out.println("--- SELECT Demo ---");

        String sql = "SELECT id, name, price FROM products WHERE category = ? AND price < ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "ELECTRONICS");
            pstmt.setBigDecimal(2, new BigDecimal("200.00"));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String name = rs.getString("name");
                    BigDecimal price = rs.getBigDecimal("price");
                    System.out.printf("  Product #%d: %s — $%s%n", id, name, price);
                }
            }
        }
        System.out.println();
    }

    /**
     * UPDATE with PreparedStatement — returns affected row count.
     *
     * <pre>
     *   "UPDATE products SET price = ? WHERE id = ?"
     *                         1              2
     *   Returns: int rowsAffected (0 = no matching row)
     * </pre>
     */
    private static void updateProduct(Connection conn) throws SQLException {
        System.out.println("--- UPDATE Demo ---");

        String sql = "UPDATE products SET price = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, new BigDecimal("79.99"));
            pstmt.setLong(2, 1L);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated " + rowsAffected + " row(s)");
        }
        System.out.println();
    }

    /**
     * BATCH INSERT — 100x faster than individual inserts.
     *
     * <pre>
     *   Individual:  INSERT → commit → INSERT → commit → ... (N round trips)
     *
     *   Batch:       addBatch() × N → executeBatch() → commit (1 round trip)
     *                ┌────────────────────────────────────────┐
     *                │  1000 rows → 1 network round-trip!     │
     *                │  vs 1000 individual round-trips        │
     *                │  Result: ~50-100x faster               │
     *                └────────────────────────────────────────┘
     * </pre>
     *
     * Python equivalent:
     * {@code cursor.executemany(sql, [(name, price) for p in products])}
     */
    private static void batchInsertProducts(Connection conn) throws SQLException {
        System.out.println("--- BATCH INSERT Demo ---");

        String sql = "INSERT INTO products (name, price, category, created_at) VALUES (?, ?, ?, ?)";

        conn.setAutoCommit(false);  // CRITICAL for batch performance

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] names = {"USB Hub", "Mouse Pad", "Monitor Stand", "Cable Clips", "Webcam"};
            BigDecimal[] prices = {
                    new BigDecimal("19.99"), new BigDecimal("12.99"),
                    new BigDecimal("49.99"), new BigDecimal("7.99"),
                    new BigDecimal("59.99")
            };

            long startTime = System.nanoTime();

            for (int i = 0; i < names.length; i++) {
                pstmt.setString(1, names[i]);
                pstmt.setBigDecimal(2, prices[i]);
                pstmt.setString(3, "ACCESSORIES");
                pstmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.addBatch();  // Add to batch, don't execute yet
            }

            int[] results = pstmt.executeBatch();  // Execute ALL at once
            conn.commit();                          // Commit the transaction

            long elapsed = (System.nanoTime() - startTime) / 1_000_000;

            System.out.println("Batch inserted " + results.length + " rows in " + elapsed + "ms");
        } catch (SQLException e) {
            conn.rollback();  // Rollback ALL on any failure
            throw e;
        } finally {
            conn.setAutoCommit(true);  // Reset for next operation
        }
        System.out.println();
    }
}
