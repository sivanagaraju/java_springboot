/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  DAO — Employee Data Access Object                             ║
 * ║  Mini-Project: 03-employee-jdbc / dao                          ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Operations:                                                   ║
 * ║                                                                ║
 * ║  create(employee)       → INSERT + RETURN_GENERATED_KEYS       ║
 * ║  findById(id)           → SELECT WHERE id = ?                  ║
 * ║  findAll()              → SELECT * (all rows)                  ║
 * ║  findByDepartment(dept) → SELECT WHERE department = ?          ║
 * ║  updateSalary(id, sal)  → UPDATE WHERE id = ?                  ║
 * ║  delete(id)             → DELETE WHERE id = ?                  ║
 * ║  batchCreate(list)      → addBatch() + executeBatch()          ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    class EmployeeRepository:                                   ║
 * ║        def create(self, emp): cursor.execute(...)              ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.dao;

import com.learning.jdbc.config.DatabaseConfig;
import com.learning.jdbc.model.Employee;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based Data Access Object for employees.
 *
 * <pre>
 *   Design pattern: DAO (Data Access Object)
 *   ┌──────────────────────────────────────────────┐
 *   │  Service Layer (business logic)              │
 *   │        │                                     │
 *   │        ▼                                     │
 *   │  EmployeeDao  ← all SQL lives here          │
 *   │        │                                     │
 *   │        ▼                                     │
 *   │  DataSource (HikariCP pool)                  │
 *   │        │                                     │
 *   │        ▼                                     │
 *   │  PostgreSQL                                   │
 *   └──────────────────────────────────────────────┘
 * </pre>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class EmployeeRepository:
 *       def __init__(self, engine: Engine):
 *           self.engine = engine
 *       def create(self, emp):
 *           with self.engine.connect() as conn:
 *               conn.execute(text("INSERT ..."), {"name": emp.name})
 * </pre>
 */
public class EmployeeDao {

    private final DataSource dataSource;

    /**
     * Constructor — accepts DataSource for pool-managed connections.
     *
     * @param dataSource HikariCP DataSource (injected, not created here)
     */
    public EmployeeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * INSERT — creates a new employee and returns the generated ID.
     *
     * <pre>
     *   PreparedStatement + RETURN_GENERATED_KEYS
     *       │
     *       ▼
     *   executeUpdate() → getGeneratedKeys() → id
     * </pre>
     *
     * @param employee the employee to create (id is ignored)
     * @return the auto-generated employee ID
     * @throws SQLException if the insert fails
     */
    public long create(Employee employee) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, employee.name());
            pstmt.setString(2, employee.department());
            pstmt.setBigDecimal(3, employee.salary());
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
     * SELECT by ID — returns Optional to handle "not found" without nulls.
     *
     * <pre>
     *   pstmt.setLong(1, id) → executeQuery()
     *       │
     *       ├── rs.next() = true  → Optional.of(employee)
     *       └── rs.next() = false → Optional.empty()
     * </pre>
     *
     * @param id employee ID to search for
     * @return Optional containing the employee, or empty if not found
     * @throws SQLException if the query fails
     */
    public Optional<Employee> findById(long id) throws SQLException {
        String sql = "SELECT id, name, department, salary, created_at FROM employees WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * SELECT all — returns list of all employees.
     *
     * @return list of all employees (may be empty, never null)
     * @throws SQLException if the query fails
     */
    public List<Employee> findAll() throws SQLException {
        String sql = "SELECT id, name, department, salary, created_at FROM employees ORDER BY id";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                employees.add(mapRow(rs));
            }
        }
        return employees;
    }

    /**
     * SELECT by department — filtered query.
     *
     * @param department the department name to filter by
     * @return list of employees in the given department
     * @throws SQLException if the query fails
     */
    public List<Employee> findByDepartment(String department) throws SQLException {
        String sql = "SELECT id, name, department, salary, created_at FROM employees WHERE department = ?";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, department);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(mapRow(rs));
                }
            }
        }
        return employees;
    }

    /**
     * UPDATE salary — returns number of rows affected.
     *
     * @param id        employee ID
     * @param newSalary the new salary value
     * @return number of rows updated (0 or 1)
     * @throws SQLException if the update fails
     */
    public int updateSalary(long id, BigDecimal newSalary) throws SQLException {
        String sql = "UPDATE employees SET salary = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, newSalary);
            pstmt.setLong(2, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * DELETE by ID — returns number of rows deleted.
     *
     * @param id employee ID to delete
     * @return number of rows deleted (0 or 1)
     * @throws SQLException if the delete fails
     */
    public int delete(long id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * BATCH INSERT with manual transaction management.
     *
     * <pre>
     *   setAutoCommit(false)
     *       │
     *       ▼
     *   Loop employees:
     *       addBatch()
     *       │
     *       ▼  (every 500 rows)
     *       executeBatch() + clearBatch()
     *       │
     *       ▼
     *   commit() or rollback()
     * </pre>
     *
     * @param employees list of employees to insert
     * @return total number of rows inserted
     * @throws SQLException if the batch fails (all changes rolled back)
     */
    public int batchCreate(List<Employee> employees) throws SQLException {
        String sql = "INSERT INTO employees (name, department, salary) VALUES (?, ?, ?)";
        int totalInserted = 0;
        int batchSize = 500;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < employees.size(); i++) {
                    Employee emp = employees.get(i);
                    pstmt.setString(1, emp.name());
                    pstmt.setString(2, emp.department());
                    pstmt.setBigDecimal(3, emp.salary());
                    pstmt.addBatch();

                    // Flush every batchSize rows to control memory
                    if ((i + 1) % batchSize == 0) {
                        int[] results = pstmt.executeBatch();
                        totalInserted += results.length;
                        pstmt.clearBatch();
                    }
                }

                // Execute remaining rows
                int[] remaining = pstmt.executeBatch();
                totalInserted += remaining.length;

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return totalInserted;
    }

    /**
     * Maps a ResultSet row to an Employee record.
     *
     * <pre>
     *   ResultSet row → Employee record
     *   ┌───────────┬───────────────────────┐
     *   │ Column    │ Method                │
     *   ├───────────┼───────────────────────┤
     *   │ id        │ rs.getLong("id")       │
     *   │ name      │ rs.getString("name")  │
     *   │ salary    │ rs.getBigDecimal(...)  │
     *   │ created_at│ rs.getTimestamp(...)   │
     *   └───────────┴───────────────────────┘
     * </pre>
     *
     * @param rs ResultSet positioned at a valid row
     * @return Employee record
     * @throws SQLException if column access fails
     */
    private Employee mapRow(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("department"),
                rs.getBigDecimal("salary"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
