/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  MAIN — Employee JDBC Application Entry Point                  ║
 * ║  Mini-Project: 03-employee-jdbc                                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Application Flow:                                             ║
 * ║                                                                ║
 * ║  main()                                                        ║
 * ║   ├──► DatabaseConfig.getDataSource()  (HikariCP pool)        ║
 * ║   ├──► new EmployeeDao(ds)                                    ║
 * ║   ├──► new DepartmentDao(ds)                                  ║
 * ║   │                                                            ║
 * ║   ├──► DEPARTMENTS: create → findAll                          ║
 * ║   ├──► EMPLOYEES: create → findById → update → findAll        ║
 * ║   ├──► BATCH: batchCreate(list) → findByDepartment            ║
 * ║   ├──► CLEANUP: delete employees                              ║
 * ║   │                                                            ║
 * ║   └──► DatabaseConfig.shutdown()                              ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    engine = create_engine(url)                                 ║
 * ║    repo = EmployeeRepository(engine)                           ║
 * ║    repo.create(...); repo.find_all()                           ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc;

import com.learning.jdbc.config.DatabaseConfig;
import com.learning.jdbc.dao.DepartmentDao;
import com.learning.jdbc.dao.EmployeeDao;
import com.learning.jdbc.model.Department;
import com.learning.jdbc.model.Employee;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * CLI entry point — demonstrates all JDBC CRUD operations.
 *
 * <pre>
 *   Execution order:
 *   ─────────────────
 *   1. Initialize pool  → DatabaseConfig.getDataSource()
 *   2. Department ops    → create Engineering, Marketing
 *   3. Employee CRUD     → create, read, update, verify
 *   4. Batch insert      → bulk create 5 employees
 *   5. Query             → findByDepartment("Engineering")
 *   6. Cleanup           → delete test data
 *   7. Shutdown pool     → DatabaseConfig.shutdown()
 * </pre>
 */
public class EmployeeJdbcApp {

    /**
     * Entry point — runs the full CRUD demonstration.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        var ds = DatabaseConfig.getDataSource();
        var employeeDao = new EmployeeDao(ds);
        var departmentDao = new DepartmentDao(ds);

        try {
            // ──── Department Operations ────
            System.out.println("═══ Department Operations ═══");
            long engId = departmentDao.create(Department.createNew("Engineering", "Building A, Floor 3"));
            long mktId = departmentDao.create(Department.createNew("Marketing", "Building B, Floor 1"));
            System.out.println("Created departments: Engineering (ID=" + engId + "), Marketing (ID=" + mktId + ")");

            departmentDao.findAll().forEach(d ->
                    System.out.printf("  [%d] %s — %s%n", d.id(), d.name(), d.location()));

            // ──── Employee CRUD ────
            System.out.println("\n═══ Employee CRUD ═══");
            long aliceId = employeeDao.create(Employee.createNew("Alice", "Engineering", new BigDecimal("95000")));
            System.out.println("Created: Alice (ID=" + aliceId + ")");

            employeeDao.findById(aliceId).ifPresent(e ->
                    System.out.printf("  Found: [%d] %s | %s | $%s%n", e.id(), e.name(), e.department(), e.salary()));

            int updated = employeeDao.updateSalary(aliceId, new BigDecimal("105000"));
            System.out.println("Updated salary: " + updated + " row(s)");

            employeeDao.findById(aliceId).ifPresent(e ->
                    System.out.printf("  After update: [%d] %s | $%s%n", e.id(), e.name(), e.salary()));

            // ──── Batch Insert ────
            System.out.println("\n═══ Batch Insert ═══");
            List<Employee> batch = List.of(
                    Employee.createNew("Bob", "Engineering", new BigDecimal("88000")),
                    Employee.createNew("Carol", "Marketing", new BigDecimal("82000")),
                    Employee.createNew("Dave", "Engineering", new BigDecimal("92000")),
                    Employee.createNew("Eve", "Marketing", new BigDecimal("79000")),
                    Employee.createNew("Frank", "Engineering", new BigDecimal("97000"))
            );
            int batchCount = employeeDao.batchCreate(batch);
            System.out.println("Batch inserted: " + batchCount + " employees");

            // ──── Query by Department ────
            System.out.println("\n═══ Engineering Team ═══");
            employeeDao.findByDepartment("Engineering").forEach(e ->
                    System.out.printf("  [%d] %s | $%s%n", e.id(), e.name(), e.salary()));

            // ──── All Employees ────
            System.out.println("\n═══ All Employees ═══");
            employeeDao.findAll().forEach(e ->
                    System.out.printf("  [%d] %s | %s | $%s | %s%n",
                            e.id(), e.name(), e.department(), e.salary(), e.createdAt()));

            // ──── Cleanup ────
            System.out.println("\n═══ Cleanup ═══");
            int totalDeleted = 0;
            for (Employee e : employeeDao.findAll()) {
                totalDeleted += employeeDao.delete(e.id());
            }
            System.out.println("Deleted " + totalDeleted + " employees");

            for (Department d : departmentDao.findAll()) {
                departmentDao.delete(d.id());
            }
            System.out.println("Deleted departments");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        } finally {
            DatabaseConfig.shutdown();
        }
    }
}
