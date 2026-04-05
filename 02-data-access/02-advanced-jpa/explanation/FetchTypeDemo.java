/**
 * ╔════════════════════════════════════════════════════════════════════════════╗
 * ║  FILE   : FetchTypeDemo.java                                             ║
 * ║  MODULE : 02-data-access / 02-advanced-jpa                               ║
 * ║  GRADLE : ./gradlew :02-data-access:run --args="FetchTypeDemo"           ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates how fetch strategy choices change SQL      ║
 * ║                   count and why JOIN FETCH / EntityGraph fix N+1          ║
 * ║  WHY IT EXISTS  : Correct JPA mappings can still perform badly if each    ║
 * ║                   relationship is loaded one row at a time                ║
 * ║  PYTHON COMPARE : SQLAlchemy joinedload/selectinload with an explicit     ║
 * ║                   session boundary                                        ║
 * ║  USE CASES      : dashboards, order history, reporting endpoints,         ║
 * ║                   graph-heavy admin screens                               ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                            ║
 * ║                                                                          ║
 * ║   Controller -> Service -> Repository -> EntityManager -> Database       ║
 * ║                        |                     |                            ║
 * ║                        |                     +-- LAZY loop = 1 + N        ║
 * ║                        +-- JOIN FETCH / EntityGraph = planned graph       ║
 * ║                                                                          ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :02-data-access:run --args="FetchTypeDemo"   ║
 * ║  EXPECTED OUTPUT: A printed comparison of N+1, JOIN FETCH, EntityGraph,  ║
 * ║                   and the transaction boundary reminder                   ║
 * ║  RELATED FILES  : 03-fetch-strategies-and-n-plus-one.md,                 ║
 * ║                   01-entity-relationships.md, 02-understanding-          ║
 * ║                   transactions.md                                         ║
 * ╚════════════════════════════════════════════════════════════════════════════╝
 */
import java.util.List;

/**
 * Prints the difference between a naive lazy-loading loop and an explicit
 * fetch plan.
 *
 * <p>Python equivalent:
 * <pre>
 *   with Session(engine) as session:
 *       departments = session.execute(select(Department)).scalars().all()
 *       # touching department.employees in a loop may trigger N+1
 *       # use joinedload() or selectinload() for an explicit fetch plan
 * </pre>
 *
 * <p>Architecture placement:
 * <pre>
 *   [Controller]
 *       |
 *       v
 *   [Service] -- transaction boundary stays open
 *       |
 *       v
 *   [Repository] -- query shape is chosen here
 *       |
 *       v
 *   [EntityManager / Hibernate] -- emits SQL
 * </pre>
 */
public final class FetchTypeDemo {

    private static final List<String> DEPARTMENTS = List.of(
        "Engineering",
        "Finance",
        "Human Resources",
        "Sales",
        "Support",
        "Security",
        "Data",
        "Product",
        "Operations",
        "Marketing"
    );

    private FetchTypeDemo() {
        // Utility demo class.
    }

    /**
     * Runs the fetch-strategy comparison from naive lazy loading to planned fetch graphs.
     *
     * @param args command line arguments, ignored by this demo
     */
    public static void main(String[] args) {
        printBanner();
        printLayerReminder();
        printNPlusOne();
        printJoinFetch();
        printEntityGraph();
        printTransactionalReminder();

        /*
         * EXPECTED OUTPUT:
         * ================
         * === Fetch Strategy Demo ===
         * [Scenario 1] Naive lazy loading
         * SQL #1: SELECT id, name FROM departments
         * SQL #2...#11: employee lookups for each department
         * Result: classic N+1 pattern
         *
         * [Scenario 2] JOIN FETCH
         * Total queries: 1
         *
         * [Scenario 3] EntityGraph
         * Result: declared fetch policy instead of ad-hoc lazy loops
         */
    }

    /**
     * Prints the high-level lesson before the scenario breakdown begins.
     */
    private static void printBanner() {
        System.out.println("=== Fetch Strategy Demo ===");
        System.out.println("Goal: show how fetch planning changes SQL count and endpoint stability.");
        System.out.println();
    }

    /**
     * Prints the repository-to-database layer reminder for where the problem lives.
     */
    private static void printLayerReminder() {
        System.out.println("[Layer Context]");
        System.out.println("Controller -> Service -> Repository -> EntityManager -> Database");
        System.out.println("The N+1 problem is not a controller bug; it is usually a fetch-plan bug.");
        System.out.println();
    }

    /**
     * Simulates the classic N+1 problem caused by touching a lazy relationship in a loop.
     */
    private static void printNPlusOne() {
        System.out.println("[Scenario 1] Naive lazy loading");
        System.out.println("Repository call: findAllDepartments()");
        System.out.println("SQL #1: SELECT id, name FROM departments");

        int queryCount = 1;

        for (String department : DEPARTMENTS) {
            queryCount++;

            // Each loop access simulates touching a lazy collection after the root query.
            System.out.println(
                "SQL #" + queryCount + ": SELECT * FROM employees WHERE department = '" + department + "'"
            );
        }

        System.out.println("Total queries: " + queryCount);
        System.out.println("Result: classic N+1 pattern.");
        System.out.println();
    }

    /**
     * Simulates a repository method that uses JOIN FETCH to pre-plan the graph.
     */
    private static void printJoinFetch() {
        System.out.println("[Scenario 2] JOIN FETCH");
        System.out.println("Repository call: findAllDepartmentsWithEmployees()");
        System.out.println(
            "SQL #1: SELECT d.*, e.* FROM departments d LEFT JOIN employees e ON e.department_id = d.id"
        );
        System.out.println("Total queries: 1");
        System.out.println("Result: query shape is explicit and stable.");
        System.out.println();
    }

    /**
     * Simulates an {@code @EntityGraph} repository method.
     */
    private static void printEntityGraph() {
        System.out.println("[Scenario 3] EntityGraph");
        System.out.println(
            "Repository call: findAllByNameContaining(...) with @EntityGraph(attributePaths = \"employees\")"
        );
        System.out.println("SQL shape: provider applies the declared fetch plan without ad-hoc lazy loops.");
        System.out.println("Result: repository-level fetch policy instead of scattered loading logic.");
        System.out.println();
    }

    /**
     * Prints the transaction and proxy reminders that still matter after the fetch plan is fixed.
     */
    private static void printTransactionalReminder() {
        System.out.println("[Reminder] Fetch strategy still needs a live transaction.");
        System.out.println("If a lazy collection is touched after the service transaction closes,");
        System.out.println("Hibernate cannot initialize the proxy and you can get LazyInitializationException.");
        System.out.println("Also remember the self-invocation trap: one method calling another method in the same");
        System.out.println("service class bypasses the Spring proxy, so the transaction may never open.");
    }
}
