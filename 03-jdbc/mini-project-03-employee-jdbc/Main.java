/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  MAIN — Employee JDBC Application Entry Point                  ║
 * ║  Mini-Project: 03-employee-jdbc                                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Application Flow:                                             ║
 * ║                                                                ║
 * ║  main()                                                        ║
 * ║   ├──► DatabaseConfig.getDataSource() (HikariCP pool)         ║
 * ║   ├──► EmployeeDao dao = new EmployeeDao(ds)                  ║
 * ║   │                                                            ║
 * ║   ├──► CREATE: dao.create("Alice", "Eng", 95000)              ║
 * ║   ├──► READ:   dao.findById(id)                               ║
 * ║   ├──► UPDATE: dao.updateSalary(id, 105000)                   ║
 * ║   ├──► LIST:   dao.findAll()                                  ║
 * ║   ├──► BATCH:  dao.batchCreate(employees)                     ║
 * ║   ├──► SEARCH: dao.findByDepartment("Engineering")            ║
 * ║   ├──► DELETE: dao.delete(id)                                 ║
 * ║   │                                                            ║
 * ║   └──► DatabaseConfig.shutdown()                              ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    engine = create_engine(url)                                 ║
 * ║    repo = EmployeeRepository(engine)                           ║
 * ║    repo.create(...)                                            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.miniproject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * CLI application demonstrating all DAO operations.
 *
 * <pre>
 *   Dependency chain:
 *   Main → EmployeeDao → DataSource → HikariCP → PostgreSQL
 * </pre>
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Employee JDBC Mini-Project          ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        var dao = new EmployeeDao(DatabaseConfig.getDataSource());

        try {
            // CREATE
            System.out.println("--- CREATE ---");
            long id1 = dao.create("Alice Smith", "Engineering", new BigDecimal("95000"));
            long id2 = dao.create("Bob Jones", "Marketing", new BigDecimal("82000"));
            System.out.println("Created employees: " + id1 + ", " + id2);
            System.out.println();

            // READ
            System.out.println("--- READ BY ID ---");
            dao.findById(id1);
            System.out.println();

            // UPDATE
            System.out.println("--- UPDATE SALARY ---");
            int updated = dao.updateSalary(id1, new BigDecimal("105000"));
            System.out.println("Updated " + updated + " row(s)");
            dao.findById(id1);
            System.out.println();

            // BATCH CREATE
            System.out.println("--- BATCH CREATE ---");
            List<String[]> batch = List.of(
                    new String[]{"Carol White", "Engineering", "88000"},
                    new String[]{"Dave Brown", "Engineering", "92000"},
                    new String[]{"Eve Davis", "Marketing", "78000"}
            );
            int[] results = dao.batchCreate(batch);
            System.out.println("Batch inserted " + results.length + " employees");
            System.out.println();

            // LIST ALL
            System.out.println("--- LIST ALL ---");
            dao.findAll();
            System.out.println();

            // SEARCH BY DEPARTMENT
            System.out.println("--- SEARCH: Engineering ---");
            dao.findByDepartment("Engineering");
            System.out.println();

            // DELETE
            System.out.println("--- DELETE ---");
            int deleted = dao.delete(id2);
            System.out.println("Deleted " + deleted + " row(s)");
            System.out.println();

            // FINAL STATE
            System.out.println("--- FINAL STATE ---");
            dao.findAll();

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.shutdown();
            System.out.println("\nPool shut down. Application complete.");
        }
    }
}
