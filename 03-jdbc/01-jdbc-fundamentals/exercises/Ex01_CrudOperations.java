/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 01 — CRUD Operations with PreparedStatement          ║
 * ║  Module: 03-jdbc | Sub: 01-jdbc-fundamentals                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  CRUD Flow:                                                    ║
 * ║  CREATE ──► INSERT INTO employees VALUES (?, ?, ?)             ║
 * ║  READ   ──► SELECT * FROM employees WHERE id = ?               ║
 * ║  UPDATE ──► UPDATE employees SET salary = ? WHERE id = ?       ║
 * ║  DELETE ──► DELETE FROM employees WHERE id = ?                 ║
 * ║                                                                ║
 * ║  Python: cursor.execute(sql, (name, dept, salary))             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.exercises;

import java.math.BigDecimal;
import java.sql.*;

/**
 * Exercise: Implement CRUD for an Employee table.
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
 *   └────────────┴──────────────┴─────────────────────┘
 * </pre>
 */
public class Ex01_CrudOperations {

    private static final String URL = "jdbc:postgresql://localhost:5432/springmastery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // TODO 1: createEmployee(conn, "Alice", "Engineering", new BigDecimal("95000"))
            // TODO 2: findEmployeeById(conn, id)
            // TODO 3: updateSalary(conn, id, new BigDecimal("105000"))
            // TODO 4: deleteEmployee(conn, id)
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // TODO 5: INSERT with RETURN_GENERATED_KEYS
    // Python: cursor.execute("INSERT ... RETURNING id", params)
    private static long createEmployee(Connection conn, String name,
                                        String dept, BigDecimal salary) throws SQLException {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    // TODO 6: SELECT by ID, read with rs.next() + getters
    // Python: cursor.execute("SELECT * WHERE id=%s", (id,)); cursor.fetchone()
    private static void findEmployeeById(Connection conn, long id) throws SQLException {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    // TODO 7: UPDATE salary, return rowsAffected
    // Python: cursor.execute("UPDATE ... SET salary=%s WHERE id=%s", (sal, id))
    private static int updateSalary(Connection conn, long id, BigDecimal newSalary) throws SQLException {
        throw new UnsupportedOperationException("TODO: Implement");
    }

    // TODO 8: DELETE by ID, return rowsDeleted
    // Python: cursor.execute("DELETE FROM employees WHERE id=%s", (id,))
    private static int deleteEmployee(Connection conn, long id) throws SQLException {
        throw new UnsupportedOperationException("TODO: Implement");
    }
}
