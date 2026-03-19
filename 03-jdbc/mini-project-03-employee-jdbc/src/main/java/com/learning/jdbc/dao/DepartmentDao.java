/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  DAO — Department Data Access Object                           ║
 * ║  Mini-Project: 03-employee-jdbc / dao                          ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Operations:                                                   ║
 * ║                                                                ║
 * ║  create(dept)      → INSERT + RETURN_GENERATED_KEYS            ║
 * ║  findById(id)      → SELECT WHERE id = ?                      ║
 * ║  findAll()         → SELECT * ORDER BY name                   ║
 * ║  findByName(name)  → SELECT WHERE name = ?                    ║
 * ║  delete(id)        → DELETE WHERE id = ?                      ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    class DepartmentRepository:                                 ║
 * ║        def create(self, dept): cursor.execute(...)             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.dao;

import com.learning.jdbc.config.DatabaseConfig;
import com.learning.jdbc.model.Department;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based Data Access Object for departments.
 *
 * <p>Follows the same DAO pattern as {@link EmployeeDao},
 * demonstrating multi-table JDBC operations.
 *
 * <p><b>Table schema:</b>
 * <pre>
 *   departments
 *   ┌──────────┬──────────────┬─────────────┐
 *   │ Column   │ Type         │ Constraint  │
 *   ├──────────┼──────────────┼─────────────┤
 *   │ id       │ BIGSERIAL    │ PRIMARY KEY │
 *   │ name     │ VARCHAR(50)  │ UNIQUE, NOT NULL │
 *   │ location │ VARCHAR(100) │ NOT NULL    │
 *   └──────────┴──────────────┴─────────────┘
 * </pre>
 */
public class DepartmentDao {

    private final DataSource dataSource;

    /**
     * Constructor — accepts DataSource for pool-managed connections.
     *
     * @param dataSource HikariCP DataSource
     */
    public DepartmentDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * INSERT — creates a new department and returns the generated ID.
     *
     * @param department the department to create
     * @return the auto-generated department ID
     * @throws SQLException if the insert fails
     */
    public long create(Department department) throws SQLException {
        String sql = "INSERT INTO departments (name, location) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, department.name());
            pstmt.setString(2, department.location());
            pstmt.executeUpdate();

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("No ID returned for inserted department");
            }
        }
    }

    /**
     * SELECT by ID — returns Optional.
     *
     * @param id department ID
     * @return Optional containing the department, or empty if not found
     * @throws SQLException if the query fails
     */
    public Optional<Department> findById(long id) throws SQLException {
        String sql = "SELECT id, name, location FROM departments WHERE id = ?";

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
     * SELECT all departments ordered by name.
     *
     * @return list of all departments
     * @throws SQLException if the query fails
     */
    public List<Department> findAll() throws SQLException {
        String sql = "SELECT id, name, location FROM departments ORDER BY name";
        List<Department> departments = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                departments.add(mapRow(rs));
            }
        }
        return departments;
    }

    /**
     * SELECT by name — useful for lookups since name is UNIQUE.
     *
     * @param name department name to search for
     * @return Optional containing the department, or empty
     * @throws SQLException if the query fails
     */
    public Optional<Department> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, location FROM departments WHERE name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * DELETE by ID.
     *
     * @param id department ID to delete
     * @return number of rows deleted (0 or 1)
     * @throws SQLException if the delete fails
     */
    public int delete(long id) throws SQLException {
        String sql = "DELETE FROM departments WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a Department record.
     *
     * @param rs ResultSet positioned at a valid row
     * @return Department record
     * @throws SQLException if column access fails
     */
    private Department mapRow(ResultSet rs) throws SQLException {
        return new Department(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("location")
        );
    }
}
