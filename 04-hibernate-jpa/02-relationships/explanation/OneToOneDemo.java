// ╔══════════════════════════════════════════════════════════════════════════╗
// ║  FILE    : OneToOneDemo.java                                             ║
// ║  MODULE  : 04-hibernate-jpa / 02-relationships                           ║
// ║  GRADLE  : ./gradlew :04-hibernate-jpa:run \                             ║
// ║              -PmainClass=com.learning.hibernate.relationships.OneToOneDemo║
// ║                                                                          ║
// ║  PURPOSE : Demonstrate @OneToOne bidirectional mapping with cascade,     ║
// ║            LAZY fetch, and dirty-checking auto-update.                   ║
// ║                                                                          ║
// ║  WHY IT EXISTS : @OneToOne is the most misunderstood JPA relationship    ║
// ║    because it looks simple but hides critical FK-ownership and lazy-load ║
// ║    subtleties. LAZY @OneToOne requires bytecode enhancement in Hibernate  ║
// ║    6 unless the FK-owning side is queried — this demo shows why Employee  ║
// ║    (not EmployeeDetail) owns the FK column.                              ║
// ║                                                                          ║
// ║  PYTHON COMPARE :                                                        ║
// ║    # SQLAlchemy equivalent                                               ║
// ║    class Employee(Base):                                                 ║
// ║        detail = relationship("EmployeeDetail",                           ║
// ║                              uselist=False,    # OneToOne                ║
// ║                              cascade="all",                              ║
// ║                              lazy="select")    # = LAZY                  ║
// ║    class EmployeeDetail(Base):                                           ║
// ║        employee = relationship("Employee",                               ║
// ║                                back_populates="detail")                  ║
// ║                                                                          ║
// ║  ENTITY RELATIONSHIP                                                     ║
// ║                                                                          ║
// ║    employees table          employee_details table                       ║
// ║   ┌──────────────────┐      ┌───────────────────────┐                   ║
// ║   │ id (PK)          │      │ id (PK)               │                   ║
// ║   │ name             │      │ employee_id (FK) ◄──┐ │                   ║
// ║   │ detail_id (FK) ──┼─────►│ department            │ │                   ║
// ║   └──────────────────┘      │ salary                │ │                   ║
// ║                             └───────────────────────┘ │                  ║
// ║    Employee owns the FK (detail_id), so Employee is   │                  ║
// ║    the "owning side" — mappedBy goes on EmployeeDetail │                  ║
// ╚══════════════════════════════════════════════════════════════════════════╝

package com.learning.hibernate.relationships;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.math.BigDecimal;

/**
 * OneToOneDemo — demonstrates bidirectional @OneToOne mapping between
 * Employee and EmployeeDetail.
 *
 * <p><b>Core Concepts Shown:</b>
 * <ul>
 *   <li>FK ownership: Employee holds {@code detail_id} so it is the owning side</li>
 *   <li>{@code mappedBy} on EmployeeDetail tells Hibernate "you don't own the FK"</li>
 *   <li>LAZY fetch: salary data is not loaded when displaying an employee name</li>
 *   <li>Dirty checking: modifying a managed entity auto-persists on commit</li>
 * </ul>
 *
 * <p><b>Python equivalent (SQLAlchemy):</b>
 * <pre>
 *   employee.detail = EmployeeDetail(department="Eng", salary=100000)
 *   session.add(employee)   # cascade="all" persists detail automatically
 *   session.commit()
 * </pre>
 *
 * <p><b>Architecture:</b>
 * <pre>
 *   Employee ──(FK: detail_id)──► EmployeeDetail
 *       ▲                              │
 *       └──────(mappedBy="detail")─────┘
 *       "inverse side reads through owner"
 * </pre>
 */
public class OneToOneDemo {

    // ──────────────────────────────────────────────────────────────────────────
    // INNER ENTITIES — defined here so the demo is self-contained in one file.
    // In a real project these would be in separate files under src/main/java.
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Employee — the OWNING side of the @OneToOne relationship.
     *
     * <p>Owns the FK column {@code detail_id} in the {@code employees} table.
     * Because Employee holds the FK, it controls the join. When you save an
     * Employee, cascade=ALL automatically saves the associated EmployeeDetail.
     *
     * <p>Python compare: the model that defines {@code ForeignKey("employee_details.id")}
     */
    @Entity
    @Table(name = "employees")
    static class Employee {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        /**
         * The associated detail record.
         *
         * <p>WHY cascade=ALL: persisting/deleting an Employee should automatically
         * persist/delete its detail — they are one logical unit. The detail has no
         * meaning without the employee.
         *
         * <p>WHY fetch=LAZY: when displaying an employee's name in a list, we should
         * NOT load the salary and department from a separate table. The detail is
         * only fetched when explicitly accessed via {@code employee.getDetail()}.
         */
        @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JoinColumn(name = "detail_id")
        private EmployeeDetail detail;

        public Employee() {}

        public Employee(String name) {
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public EmployeeDetail getDetail() { return detail; }
        public void setDetail(EmployeeDetail detail) { this.detail = detail; }
    }

    /**
     * EmployeeDetail — the INVERSE (non-owning) side of the @OneToOne.
     *
     * <p>Does NOT hold a FK column. The {@code mappedBy="detail"} attribute
     * tells Hibernate: "the 'detail' field in Employee owns the join column —
     * look there for the FK, not here."
     *
     * <p>Python compare: the model decorated with {@code back_populates="detail"}
     * on the relationship that does NOT define a ForeignKey.
     */
    @Entity
    @Table(name = "employee_details")
    static class EmployeeDetail {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String department;
        private BigDecimal salary;

        /**
         * WHY mappedBy="detail": EmployeeDetail does NOT own the FK column.
         * Employee.detail is the owning field. mappedBy prevents Hibernate from
         * creating a duplicate FK column on the employee_details table.
         * Without mappedBy, you would get TWO FK columns — one on each side.
         */
        @OneToOne(mappedBy = "detail")
        private Employee employee;

        public EmployeeDetail() {}

        public EmployeeDetail(String department, BigDecimal salary) {
            this.department = department;
            this.salary = salary;
        }

        public Long getId() { return id; }
        public String getDepartment() { return department; }
        public BigDecimal getSalary() { return salary; }
        public void setDepartment(String department) { this.department = department; }
        public void setSalary(BigDecimal salary) { this.salary = salary; }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MAIN — demo runner
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Entry point — runs all @OneToOne demo sections in sequence.
     *
     * <p>WHY structure: each section has a labeled header so output matches
     * the learning goal. Sections are independent transactions to demonstrate
     * the lifecycle clearly (create → reload → lazy access → dirty check).
     *
     * @param args unused
     * @throws Exception if Hibernate configuration fails
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== OneToOne Demo ===\n");

        // WHY StandardServiceRegistry instead of persistence.xml:
        // For standalone demos (no Spring, no app server) we build the registry
        // programmatically. In Spring Boot this is handled by auto-configuration.
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .applySetting("hibernate.show_sql", "true")
                .applySetting("hibernate.format_sql", "true")
                .build();

        SessionFactory sf = new MetadataSources(registry)
                .addAnnotatedClass(Employee.class)
                .addAnnotatedClass(EmployeeDetail.class)
                .buildMetadata()
                .buildSessionFactory();

        Long employeeId = createEmployeeWithDetail(sf);
        demonstrateLazyLoading(sf, employeeId);
        demonstrateDirtyChecking(sf, employeeId);

        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    /**
     * Section 1 — persist an Employee and EmployeeDetail via cascade.
     *
     * <p>WHY only persist Employee: cascade=ALL means Hibernate will
     * automatically issue an INSERT for EmployeeDetail when Employee is saved.
     * This mirrors SQLAlchemy's {@code session.add(employee)} with cascade="all".
     *
     * @param sf the active SessionFactory
     * @return the generated Employee ID for use in subsequent sections
     */
    private static Long createEmployeeWithDetail(SessionFactory sf) {
        System.out.println("--- Creating Employee with EmployeeDetail ---");

        Long id;
        try (Session session = sf.openSession()) {
            session.beginTransaction();

            Employee emp = new Employee("Alice Chen");
            EmployeeDetail detail = new EmployeeDetail("Engineering", new BigDecimal("120000.00"));

            // WHY set BOTH sides of the bidirectional relationship:
            // Hibernate only reads the owning side (Employee.detail) to write the FK.
            // But if we don't set detail.employee, the in-memory object graph is
            // inconsistent — detail.getEmployee() would return null until reload.
            emp.setDetail(detail);
            detail = detail; // detail.employee intentionally not set in this demo
            // (Only needed if we read detail.employee in same session)

            session.persist(emp); // WHY: cascade=ALL persists detail automatically
            session.getTransaction().commit();

            id = emp.getId();
            System.out.println("  Saved Employee id=" + id + " -> detail id=" + detail.getId());
        }
        return id;
    }

    /**
     * Section 2 — reload Employee and demonstrate LAZY detail.
     *
     * <p>The EmployeeDetail SQL is NOT executed when Employee is loaded.
     * It fires only when {@code emp.getDetail()} is called — at that point
     * Hibernate issues a second SELECT to fetch the employee_details row.
     *
     * @param sf         the active SessionFactory
     * @param employeeId the Employee to reload
     */
    private static void demonstrateLazyLoading(SessionFactory sf, Long employeeId) {
        System.out.println("\n--- Loading Employee (EmployeeDetail is LAZY) ---");
        System.out.println("  (Watch for 1 SQL now, 1 more SQL when detail is accessed)");

        try (Session session = sf.openSession()) {
            // WHY new session: forces a fresh load — no first-level cache carryover
            Employee emp = session.get(Employee.class, employeeId);
            System.out.println("  Employee loaded: " + emp.getName() + "  <- only 1 query so far");

            System.out.println("\n--- Accessing detail triggers SQL ---");
            // WHY this triggers a query: FetchType.LAZY means the proxy is not
            // initialized until a field is actually read. This line causes
            // Hibernate to fire: SELECT * FROM employee_details WHERE id = ?
            String dept = emp.getDetail().getDepartment();
            System.out.println("  Department: " + dept + "  <- detail SQL fired just now");
            System.out.println("  Salary: " + emp.getDetail().getSalary() + "  <- no extra SQL (proxy already initialized)");
        }
    }

    /**
     * Section 3 — demonstrate dirty checking (automatic UPDATE on commit).
     *
     * <p>In a managed (open) session, any field change on a loaded entity is
     * automatically detected at flush time. No explicit {@code session.update()}
     * or {@code session.merge()} is needed.
     *
     * <p>Python compare: SQLAlchemy works the same way —
     * {@code session.commit()} auto-flushes dirty objects.
     *
     * @param sf         the active SessionFactory
     * @param employeeId the Employee whose detail salary we will raise
     */
    private static void demonstrateDirtyChecking(SessionFactory sf, Long employeeId) {
        System.out.println("\n--- Updating detail (dirty checking) ---");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            Employee emp = session.get(Employee.class, employeeId);

            BigDecimal oldSalary = emp.getDetail().getSalary();
            BigDecimal newSalary = oldSalary.multiply(new BigDecimal("1.10")); // 10% raise

            // WHY no session.update() call: Hibernate snapshots entity state at
            // load time. At flush/commit it compares current state to snapshot.
            // Any difference generates an UPDATE SQL automatically.
            emp.getDetail().setSalary(newSalary);

            session.getTransaction().commit(); // WHY: UPDATE fires here (flush before commit)
            System.out.println("  Salary updated from " + oldSalary + " to " + newSalary + " (dirty check auto-UPDATE)");
        }

        // Verify the update persisted
        try (Session session = sf.openSession()) {
            Employee emp = session.get(Employee.class, employeeId);
            System.out.println("  Verified persisted salary: " + emp.getDetail().getSalary());
        }
    }
}

// EXPECTED OUTPUT:
// =================================================
// === OneToOne Demo ===
//
// --- Creating Employee with EmployeeDetail ---
//   Saved Employee id=1 -> detail id=1
//
// --- Loading Employee (EmployeeDetail is LAZY) ---
//   (Watch for 1 SQL now, 1 more SQL when detail is accessed)
//   Employee loaded: Alice Chen  <- only 1 query so far
//
// --- Accessing detail triggers SQL ---
//   Department: Engineering  <- detail SQL fired just now
//   Salary: 120000.00  <- no extra SQL (proxy already initialized)
//
// --- Updating detail (dirty checking) ---
//   Salary updated from 120000.00 to 132000.000 (dirty check auto-UPDATE)
//   Verified persisted salary: 132000.000
// =================================================
