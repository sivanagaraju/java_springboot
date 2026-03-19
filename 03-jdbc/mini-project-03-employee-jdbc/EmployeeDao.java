/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  EMPLOYEE DAO — Data Access Object for CRUD + Batch + Search   ║
 * ║  Mini-Project: 03-employee-jdbc                                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  DAO Pattern:                                                  ║
 * ║                                                                ║
 * ║  ┌─────────┐     ┌──────────────┐     ┌──────────┐            ║
 * ║  │  Main   │────►│ EmployeeDao  │────►│ Database │            ║
 * ║  │ (client)│     │ (all SQL     │     │(Postgres)│            ║
 * ║  │         │     │  lives here) │     │          │            ║
 * ║  └─────────┘     └──────────────┘     └──────────┘            ║
 * ║                                                                ║
 * ║  Methods:                                                      ║
 * ║    create(name, dept, salary) → long id                       ║
 * ║    findById(id)  → prints employee                            ║
 * ║    findAll()     → prints all employees                       ║
 * ║    update(id, newSalary) → int rowsAffected                   ║
 * ║    delete(id)    → int rowsDeleted                            ║
 * ║    batchCreate(list) → int[] results                          ║
 * ║    findByDepartment(dept) → prints matches                    ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    class EmployeeRepository:                                   ║
 * ║        def create(self, name, dept, salary): ...               ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.miniproject;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

/**
 * Data Access Object encapsulating all employee SQL operations.
 *
 * <pre>
 *   Key design decisions:
 *   ─────────────────────
 *   ✓ All methods use PreparedStatement (SQL injection safe)
 *   ✓ All resources use try-with-resources (no leaks)
 *   ✓ DataSource injected (pool-managed connections)
 *   ✓ Batch operations use manual transactions
 * </pre>
 */
public class EmployeeDao {

    private final DataSource dataSource;

    public EmployeeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Creates a new employee and returns the generated ID.
     *
     * <pre>
     *   INSERT → RETURN_GENERATED_KEYS → getGeneratedKeys()
     *                                      └──► rs.getLong(1) = new id
     * </pre>
     */
    public long create(String name, String department, BigDecimal salary) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setString(2, department);
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
     * Finds an employee by ID.
     *
     * <pre>
     *   SELECT → rs.next() → read columns by name
     * </pre>
     */
    public void findById(long id) throws SQLException {
        String sql = "SELECT id, name, department, salary, created_at FROM employees WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
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

    /** Lists all employees. */
    public void findAll() throws SQLException {
        String sql = "SELECT id, name, department, salary FROM employees ORDER BY id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.printf("  [%d] %s | %s | $%s%n",
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getBigDecimal("salary"));
            }
        }
    }

    /** Updates salary, returns rows affected. */
    public int updateSalary(long id, BigDecimal newSalary) throws SQLException {
        String sql = "UPDATE employees SET salary = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, newSalary);
            pstmt.setLong(2, id);
            return pstmt.executeUpdate();
        }
    }

    /** Deletes an employee, returns rows deleted. */
    public int delete(long id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * Batch inserts employees with transaction control.
     *
     * <pre>
     *   setAutoCommit(false)
     *    │
     *    ▼
     *   Loop: addBatch() × N → executeBatch() → commit()
     *   On error: rollback()
     * </pre>
     */
    public int[] batchCreate(List<String[]> employees) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (String[] emp : employees) {
                    pstmt.setString(1, emp[0]);
                    pstmt.setString(2, emp[1]);
                    pstmt.setBigDecimal(3, new BigDecimal(emp[2]));
                    pstmt.addBatch();
                }

                int[] results = pstmt.executeBatch();
                conn.commit();
                return results;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    /** Finds employees by department. */
    public void findByDepartment(String department) throws SQLException {
        String sql = "SELECT id, name, salary FROM employees WHERE department = ? ORDER BY salary DESC";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, department);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  [%d] %s — $%s%n",
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("salary"));
                }
            }
        }
    }
}
