/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 01 — CRUD Operations with PreparedStatement          ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals / exercises       ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  CRUD Flow:                                                    ║
 * ║                                                                ║
 * ║  createEmployee(conn, name, dept, salary)                      ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  PreparedStatement + RETURN_GENERATED_KEYS                     ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  executeUpdate() → getGeneratedKeys() → id                    ║
 * ║                                                                ║
 * ║  findEmployeeById(conn, id)                                    ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  pstmt.setLong(1, id) → executeQuery() → rs.next()            ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  rs.getString("name"), rs.getBigDecimal("salary")              ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    cursor.execute("INSERT ... RETURNING id", (name, dept, s))  ║
 * ║    id = cursor.fetchone()[0]                                   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.exercises.solutions;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Complete solution for Exercise 01 — CRUD Operations.
 *
 * <pre>
 *   employees table:
 *   ┌────────────┬──────────────┬─────────────────────┐
 *   │ Column     │ Type         │ Constraints          │
 *   ├────────────┼──────────────┼─────────────────────┤
 *   │ id         │ BIGSERIAL    │ PRIMARY KEY          │
 *   │ name       │ VARCHAR(100) │ NOT NULL             │
 *   │ department │ VARCHAR(50)  │ NOT NULL             │
 *   │ salary     │ DECIMAL(10,2)│ NOT NULL, CHECK > 0  │
 *   │ created_at │ TIMESTAMP    │ DEFAULT now()        │
 *   └────────────┴──────────────┴─────────────────────┘
 * </pre>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   import psycopg2
 *   conn = psycopg2.connect(dsn)
 *   cursor = conn.cursor()
 *   cursor.execute("INSERT INTO employees ...", (name, dept, salary))
 *   conn.commit()
 * </pre>
 */
public class Sol01_CrudOperations {

    private static final String URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    /**
     * Entry point — demonstrates full CRUD lifecycle.
     *
     * <pre>
     *   main()
     *    ├──► CREATE employee → get ID
     *    ├──► READ   employee by ID
     *    ├──► UPDATE salary
     *    ├──► READ   again (verify update)
     *    └──► DELETE employee
     * </pre>
     */
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("=== CRUD Operations Solution ===\n");

            // CREATE
            long id = createEmployee(conn, "Alice", "Engineering", new BigDecimal("95000"));
            System.out.println("Created employee with ID: " + id);

            // READ
            System.out.println("\n--- Read by ID ---");
            findEmployeeById(conn, id);

            // UPDATE
            int updated = updateSalary(conn, id, new BigDecimal("105000"));
            System.out.println("\nUpdated " + updated + " row(s)");

            // READ again to verify
            System.out.println("\n--- After update ---");
            findEmployeeById(conn, id);

            // DELETE
            int deleted = deleteEmployee(conn, id);
            System.out.println("\nDeleted " + deleted + " row(s)");

            // Verify deletion
            System.out.println("\n--- After delete ---");
            findEmployeeById(conn, id);

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
        }
    }

    /**
     * INSERT with RETURN_GENERATED_KEYS.
     *
     * <pre>
     *   PreparedStatement with RETURN_GENERATED_KEYS
     *       │
     *       ▼
     *   setString(1, name)     ← parameter binding (SQL injection safe)
     *   setString(2, dept)
     *   setBigDecimal(3, salary)
     *       │
     *       ▼
     *   executeUpdate()        ← executes INSERT
     *       │
     *       ▼
     *   getGeneratedKeys()     ← reads auto-generated ID
     *       └──► rs.getLong(1) = new employee ID
     * </pre>
     *
     * <p>Python equivalent:
     * {@code cursor.execute("INSERT ... RETURNING id", (name, dept, salary))}
     *
     * @param conn   active database connection
     * @param name   employee name
     * @param dept   department name
     * @param salary employee salary
     * @return the auto-generated employee ID
     * @throws SQLException if the insert fails
     */
    private static long createEmployee(Connection conn, String name,
                                        String dept, BigDecimal salary) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";

        // RETURN_GENERATED_KEYS tells the driver to fetch the auto-generated ID
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, dept);
            pstmt.setBigDecimal(3, salary);
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("No ID returned for inserted employee");
            }
        }
    }

    /**
     * SELECT by ID — reads one row using column-name getters.
     *
     * <pre>
     *   pstmt.setLong(1, id)
     *       │
     *       ▼
     *   executeQuery() → ResultSet
     *       │
     *       ├── rs.next() == true  → print employee
     *       └── rs.next() == false → "not found"
     * </pre>
     *
     * <p>Python equivalent:
     * {@code cursor.execute("SELECT * WHERE id=%s", (id,)); row = cursor.fetchone()}
     *
     * @param conn active database connection
     * @param id   employee ID to find
     * @throws SQLException if the query fails
     */
    private static void findEmployeeById(Connection conn, long id) throws SQLException {
        String sql = "SELECT id, name, department, salary, created_at FROM employees WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Column-name access is safer than index-based (resilient to SELECT reordering)
                    System.out.printf("  [%d] %s | %s | $%s | %s%n",
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("department"),
                            rs.getBigDecimal("salary"),
                            rs.getTimestamp("created_at"));
                } else {
                    System.out.println("  Employee not found: " + id);
                }
            }
        }
    }

    /**
     * UPDATE salary — returns number of rows affected.
     *
     * <pre>
     *   UPDATE employees SET salary = ? WHERE id = ?
     *       │
     *       ▼
     *   executeUpdate() → int rowsAffected
     *       ├── 1 = success
     *       └── 0 = no employee with that ID
     * </pre>
     *
     * @param conn      active database connection
     * @param id        employee ID to update
     * @param newSalary the new salary value
     * @return number of rows affected (0 or 1)
     * @throws SQLException if the update fails
     */
    private static int updateSalary(Connection conn, long id, BigDecimal newSalary) throws SQLException {
        String sql = "UPDATE employees SET salary = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, newSalary);
            pstmt.setLong(2, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * DELETE by ID — returns number of rows deleted.
     *
     * <pre>
     *   DELETE FROM employees WHERE id = ?
     *       │
     *       ▼
     *   executeUpdate() → int rowsDeleted
     * </pre>
     *
     * @param conn active database connection
     * @param id   employee ID to delete
     * @return number of rows deleted (0 or 1)
     * @throws SQLException if the delete fails
     */
    private static int deleteEmployee(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        }
    }
}
