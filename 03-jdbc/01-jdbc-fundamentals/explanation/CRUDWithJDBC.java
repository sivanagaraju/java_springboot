/*
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║  FILE   : CRUDWithJDBC.java                                                 ║
 * ║  MODULE : 03-jdbc / 01-jdbc-fundamentals                                    ║
 * ║  GRADLE : ./gradlew :03-jdbc:run -PmainClass=com.springmastery.jdbc.demo.CRUDWithJDBC ║
 * ╠══════════════════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Teach raw JDBC CRUD with visible SQL, parameter binding,  ║
 * ║                   transaction boundaries, and batch behavior                 ║
 * ║  WHY IT EXISTS  : JPA hides these mechanics; JDBC lets you see what Spring  ║
 * ║                   Data and Hibernate eventually automate                     ║
 * ║  PYTHON COMPARE : psycopg2 cursor.execute() / SQLAlchemy Core               ║
 * ║  USE CASES      : DAO onboarding, migration scripts, debugging data bugs,   ║
 * ║                   maintenance jobs, batch imports                            ║
 * ╠══════════════════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                               ║
 * ║                                                                             ║
 * ║   CLI / Service                                                              ║
 * ║        │                                                                    ║
 * ║        ▼                                                                    ║
 * ║   [CRUDWithJDBC] ----prints----> [SQL templates] ----binds---->             ║
 * ║        │                                            [PreparedStatement]      ║
 * ║        │                                                      │              ║
 * ║        └---------------- transaction / commit / rollback -----┴--> PostgreSQL║
 * ║                                                                             ║
 * ╠══════════════════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :03-jdbc:run -PmainClass=com.springmastery.jdbc.demo.CRUDWithJDBC ║
 * ║  EXPECTED OUTPUT: CRUD SQL templates, Python comparisons, and transaction   ║
 * ║                   lifecycle reminders for batch writes                       ║
 * ║  RELATED FILES  : JdbcConnectionDemo.java, PreparedStatementDemo.java,       ║
 * ║                   TransactionDemo.java, 09-crud-with-jdbc.md                 ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
package com.springmastery.jdbc.demo;

import java.util.List;

/**
 * Demonstrates the raw JDBC CRUD lifecycle without hiding the SQL behind an ORM.
 *
 * <p>Python equivalent:
 * <pre>
 *   # psycopg2 / SQLAlchemy Core mental model
 *   cursor.execute(sql, params)
 *   conn.commit()
 * </pre>
 * Java is more explicit, but that is exactly what makes JDBC such a strong
 * learning layer before Spring Data JPA and Hibernate.
 *
 * <p>Architecture placement:
 * <pre>
 *   [Service / CLI]
 *         |
 *         v
 *   [CRUDWithJDBC] ---> [PreparedStatement] ---> [Connection] ---> [PostgreSQL]
 *         |
 *         +-- prints the SQL and transaction steps learners must internalize
 * </pre>
 */
public final class CRUDWithJDBC {

    private static final String TABLE_SCHEMA = """
        CREATE TABLE employees (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            department VARCHAR(50) NOT NULL,
            salary DECIMAL(10,2) NOT NULL CHECK (salary > 0),
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
        """;

    private CRUDWithJDBC() {
        // Utility demo class.
    }

    /**
     * Entry point for the CRUD walkthrough.
     *
     * <p>Flow:
     * <pre>
     *   main()
     *     -> printCrudLifecycle()
     *     -> printSchema()
     *     -> printCrudSteps()
     *     -> printTransactionBoundary()
     *     -> printBatchWriteNotes()
     * </pre>
     *
     * @param args standard Java entry-point arguments; not used in this demo
     */
    public static void main(String[] args) {
        System.out.println("=== CRUD With JDBC ===\n");
        System.out.println("Why raw JDBC before JPA?");
        System.out.println("- JDBC shows the exact SQL, transaction, and connection lifecycle.");
        System.out.println("- JPA will later automate the same work, but only after you understand the metal layer.\n");

        printCrudLifecycle();
        printSchema();
        printCrudSteps();
        printTransactionBoundary();
        printBatchWriteNotes();

        /*
         * EXPECTED OUTPUT:
         * ================
         * === CRUD With JDBC ===
         * Why raw JDBC before JPA?
         * --- CRUD Lifecycle ---
         * CREATE -> READ -> UPDATE -> DELETE -> COMMIT / ROLLBACK
         *
         * --- Target Table ---
         * CREATE TABLE employees (...)
         *
         * --- CRUD Steps ---
         * CREATE
         * SQL:
         * INSERT INTO employees ...
         *
         * --- Transaction Boundary ---
         * setAutoCommit(false) -> executeUpdate()/executeBatch() -> commit() or rollback()
         */
    }

    /**
     * Prints the visual CRUD lifecycle before the SQL details.
     */
    private static void printCrudLifecycle() {
        System.out.println("--- CRUD Lifecycle ---");
        System.out.println("CREATE -> READ -> UPDATE -> DELETE -> COMMIT / ROLLBACK");
        System.out.println("Connection -> PreparedStatement -> executeUpdate()/executeQuery() -> commit()");
        System.out.println();
    }

    /**
     * Prints the table schema that every CRUD method targets.
     */
    private static void printSchema() {
        System.out.println("--- Target Table ---");
        System.out.println(TABLE_SCHEMA);
    }

    /**
     * Prints the canonical CRUD operations and their Python equivalents.
     */
    private static void printCrudSteps() {
        System.out.println("--- CRUD Steps ---");

        for (CrudStep step : steps()) {
            System.out.println(step.operation());
            System.out.println("SQL:");
            System.out.println(step.sql());
            System.out.println("Python:");
            System.out.println(step.python());
            System.out.println("Why this matters:");
            System.out.println(step.note());
            System.out.println();
        }
    }

    /**
     * Explains the transaction boundary around a unit of work.
     */
    private static void printTransactionBoundary() {
        System.out.println("--- Transaction Boundary ---");
        System.out.println("setAutoCommit(false) -> executeUpdate()/executeBatch() -> commit()");
        System.out.println("If anything fails, catch SQLException and call rollback().");
        System.out.println("This is the same discipline as Python DB-API code around a unit of work.");
        System.out.println();
    }

    /**
     * Explains why batch writes belong inside one explicit transaction.
     */
    private static void printBatchWriteNotes() {
        System.out.println("--- Batch Writes ---");
        System.out.println("Use setAutoCommit(false) before a batch insert so all rows succeed or all rows roll back.");
        System.out.println("This is the same discipline you would use with psycopg2: explicit commit/rollback around a unit of work.");
        System.out.println("If a batch insert fails halfway through, JDBC should roll back the entire group, not leave partial data behind.");
    }

    /**
     * Returns the CRUD scenarios in one place so the walkthrough stays readable.
     *
     * @return ordered CRUD examples from CREATE to DELETE
     */
    private static List<CrudStep> steps() {
        return List.of(
            new CrudStep(
                "CREATE",
                """
                INSERT INTO employees (name, department, salary)
                VALUES (?, ?, ?)
                RETURNING id;
                """,
                """
                cursor.execute(
                    "INSERT INTO employees (name, department, salary) VALUES (%s, %s, %s) RETURNING id",
                    (name, department, salary)
                )
                row_id = cursor.fetchone()[0]
                conn.commit()
                """,
                "PreparedStatement protects against SQL injection and lets the driver reuse the compiled query plan."
            ),
            new CrudStep(
                "READ",
                """
                SELECT id, name, department, salary, created_at
                FROM employees
                WHERE id = ?;
                """,
                """
                cursor.execute(
                    "SELECT id, name, department, salary, created_at FROM employees WHERE id = %s",
                    (employee_id,)
                )
                row = cursor.fetchone()
                """,
                "Read queries are the easiest place to learn ResultSet iteration and column-by-name access."
            ),
            new CrudStep(
                "UPDATE",
                """
                UPDATE employees
                SET salary = ?
                WHERE id = ?;
                """,
                """
                cursor.execute(
                    "UPDATE employees SET salary = %s WHERE id = %s",
                    (new_salary, employee_id)
                )
                conn.commit()
                """,
                "Updates show why explicit transactions matter: the row count tells you whether the change actually happened."
            ),
            new CrudStep(
                "DELETE",
                """
                DELETE FROM employees
                WHERE id = ?;
                """,
                """
                cursor.execute("DELETE FROM employees WHERE id = %s", (employee_id,))
                conn.commit()
                """,
                "Deletes are intentionally loud in raw JDBC so you always know when the database changes."
            ),
            new CrudStep(
                "BATCH INSERT",
                """
                INSERT INTO employees (name, department, salary)
                VALUES (?, ?, ?)
                """,
                """
                for employee in employees:
                    cursor.execute(
                        "INSERT INTO employees (name, department, salary) VALUES (%s, %s, %s)",
                        employee
                    )
                conn.commit()
                """,
                "Batching is where JDBC earns its keep: one transaction can write many rows faster than one statement at a time."
            )
        );
    }

    private record CrudStep(String operation, String sql, String python, String note) {
    }
}
