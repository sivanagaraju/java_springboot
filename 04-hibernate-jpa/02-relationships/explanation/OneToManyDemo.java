// ╔══════════════════════════════════════════════════════════════════════════╗
// ║  FILE    : OneToManyDemo.java                                            ║
// ║  MODULE  : 04-hibernate-jpa / 02-relationships                           ║
// ║  GRADLE  : ./gradlew :04-hibernate-jpa:run \                             ║
// ║              -PmainClass=com.learning.hibernate.relationships.OneToManyDemo║
// ║                                                                          ║
// ║  PURPOSE : Demonstrate bidirectional @OneToMany / @ManyToOne with        ║
// ║            cascade persist, orphanRemoval, and the critical              ║
// ║            "synchronize both sides" pattern.                             ║
// ║                                                                          ║
// ║  WHY IT EXISTS : @OneToMany is the most common JPA relationship and the  ║
// ║    #1 source of bugs. Forgetting to set both sides of the bidirectional  ║
// ║    link causes silent data inconsistency in the same session.            ║
// ║    orphanRemoval is critical for collections that "own" their children.  ║
// ║                                                                          ║
// ║  PYTHON COMPARE :                                                        ║
// ║    class Department(Base):                                               ║
// ║        employees = relationship("Employee",                              ║
// ║                                 back_populates="department",             ║
// ║                                 cascade="all, delete-orphan",            ║
// ║                                 lazy="select")                           ║
// ║    class Employee(Base):                                                 ║
// ║        dept_id = Column(ForeignKey("departments.id"))                    ║
// ║        department = relationship("Department",                           ║
// ║                                  back_populates="employees")             ║
// ║                                                                          ║
// ║  ENTITY RELATIONSHIP                                                     ║
// ║                                                                          ║
// ║    departments              employees                                    ║
// ║   ┌──────────────┐         ┌────────────────────────┐                   ║
// ║   │ id (PK)      │ 1     N │ id (PK)                │                   ║
// ║   │ name         │◄────────┤ dept_id (FK)           │                   ║
// ║   └──────────────┘         │ name                   │                   ║
// ║                             └────────────────────────┘                   ║
// ║    FK lives on the "many" side (employees.dept_id)                       ║
// ║    @ManyToOne on Employee is the owning side                             ║
// ╚══════════════════════════════════════════════════════════════════════════╝

package com.learning.hibernate.relationships;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * OneToManyDemo — bidirectional @OneToMany / @ManyToOne relationship.
 *
 * <p><b>Core Concepts Shown:</b>
 * <ul>
 *   <li>The FK always lives on the "many" side (employees.dept_id)</li>
 *   <li>{@code mappedBy} on @OneToMany prevents a spurious join table</li>
 *   <li>Convenience method {@code addEmployee()} keeps both sides in sync</li>
 *   <li>{@code orphanRemoval=true}: remove from collection → DELETE from DB</li>
 *   <li>Cascade PERSIST flows from Department → Employee on save</li>
 * </ul>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   dept = Department(name="Engineering")
 *   emp = Employee(name="Bob")
 *   dept.employees.append(emp)   # SQLAlchemy sets emp.department via backref
 *   session.add(dept)            # cascade saves employees
 *   session.commit()
 *
 *   # In JPA you MUST set both sides manually (no automatic backref)
 *   dept.getEmployees().add(emp);   // not enough!
 *   emp.setDepartment(dept);        // REQUIRED in JPA
 * </pre>
 *
 * <p><b>Architecture:</b>
 * <pre>
 *   Department (1) ──── addEmployee() ────► Employee (N)
 *                       sets BOTH sides:
 *                       dept.employees.add(emp)
 *                       emp.setDepartment(dept)
 * </pre>
 */
public class OneToManyDemo {

    // ──────────────────────────────────────────────────────────────────────────
    // INNER ENTITIES
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Department — the "one" side of the @OneToMany relationship.
     *
     * <p>Does NOT own the FK column. {@code mappedBy} references the owning
     * field on Employee. Without {@code mappedBy}, Hibernate would create a
     * useless join table (department_employees) alongside the FK column.
     */
    @Entity
    @Table(name = "departments")
    static class Department {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        /**
         * WHY mappedBy="department": Employee.department owns the FK column dept_id.
         * mappedBy prevents Hibernate from creating a redundant join table.
         *
         * WHY cascade=PERSIST (not ALL): auto-persist new employees added to this
         * department. We do NOT use cascade=REMOVE — instead orphanRemoval=true is
         * the cleaner approach for collection-managed deletion.
         *
         * WHY orphanRemoval=true: removing an Employee from this list should delete
         * the DB row. Without this, the row remains with dept_id=NULL (orphan).
         */
        @OneToMany(mappedBy = "department", cascade = CascadeType.PERSIST, orphanRemoval = true)
        private List<Employee> employees = new ArrayList<>();

        public Department() {}

        public Department(String name) { this.name = name; }

        /**
         * Adds an Employee and synchronizes BOTH sides of the bidirectional link.
         *
         * <p>WHY both sides: JPA has no automatic backref. The owning side
         * (Employee.department) controls the FK written to the database. Adding to
         * the collection without calling {@code emp.setDepartment(this)} results in
         * a NULL dept_id written to the employees table — silent data bug.
         *
         * @param emp the Employee to associate; must not be null
         */
        public void addEmployee(Employee emp) {
            this.employees.add(emp);         // WHY: keeps in-memory graph consistent
            emp.setDepartment(this);          // WHY: sets owning-side FK
        }

        /**
         * Removes an Employee from this department.
         * orphanRemoval=true issues a DELETE for the removed row on commit.
         *
         * @param emp the Employee to remove
         */
        public void removeEmployee(Employee emp) {
            this.employees.remove(emp);
            emp.setDepartment(null);
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public List<Employee> getEmployees() { return employees; }
    }

    /**
     * Employee — the "many" side and OWNING side of the relationship.
     *
     * <p>Owns FK column {@code dept_id}. @ManyToOne default fetch is EAGER —
     * overridden to LAZY because loading employee lists should not JOIN to
     * departments for every single row.
     */
    @Entity
    @Table(name = "emp_onetomany")
    static class Employee {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        /**
         * WHY fetch=LAZY: @ManyToOne defaults to EAGER. For employee lists
         * with thousands of rows, each eager load adds a JOIN to departments.
         * Always override @ManyToOne to LAZY unless proven otherwise by profiling.
         */
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "dept_id")
        private Department department;

        public Employee() {}

        public Employee(String name) { this.name = name; }

        public Long getId() { return id; }
        public String getName() { return name; }
        public Department getDepartment() { return department; }
        public void setDepartment(Department department) { this.department = department; }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // MAIN
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Entry point — runs all @OneToMany demo sections in sequence.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== OneToMany Demo ===\n");

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .applySetting("hibernate.show_sql", "true")
                .build();

        SessionFactory sf = new MetadataSources(registry)
                .addAnnotatedClass(Department.class)
                .addAnnotatedClass(Employee.class)
                .buildMetadata()
                .buildSessionFactory();

        Long deptId = createDepartmentWithEmployees(sf);
        addEmployeeToExisting(sf, deptId);
        demonstrateOrphanRemoval(sf, deptId);
        queryEmployeesInDepartment(sf, deptId);

        sf.close();
        StandardServiceRegistryBuilder.destroy(registry);
    }

    /**
     * Section 1 — create Department with 3 Employees via cascade PERSIST.
     *
     * <p>Only {@code session.persist(dept)} is called — cascade saves all
     * employees linked via {@code addEmployee()}.
     *
     * @param sf the SessionFactory
     * @return the ID of the persisted Department
     */
    private static Long createDepartmentWithEmployees(SessionFactory sf) {
        System.out.println("--- Create Department with 3 Employees (cascade persist) ---");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            Department eng = new Department("Engineering");
            // WHY addEmployee (not direct list.add): sets BOTH sides of the link
            eng.addEmployee(new Employee("Alice"));
            eng.addEmployee(new Employee("Bob"));
            eng.addEmployee(new Employee("Carol"));

            session.persist(eng); // cascade=PERSIST saves all 3 employees too
            session.getTransaction().commit();

            System.out.println("  Saved dept id=" + eng.getId()
                    + " with " + eng.getEmployees().size() + " employees");
            return eng.getId();
        }
    }

    /**
     * Section 2 — add a new Employee to an existing Department.
     *
     * @param sf     the SessionFactory
     * @param deptId the existing Department ID
     */
    private static void addEmployeeToExisting(SessionFactory sf, Long deptId) {
        System.out.println("\n--- Add Employee to existing Department ---");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            Department dept = session.get(Department.class, deptId);
            Employee dave = new Employee("Dave");
            dept.addEmployee(dave);

            // WHY explicit persist: cascade=PERSIST only runs at the time the parent
            // is first saved. New children added to an already-persistent parent must
            // be persisted explicitly (or use cascade=ALL which also handles this).
            session.persist(dave);
            session.getTransaction().commit();

            System.out.println("  Added Dave. Dept now has "
                    + dept.getEmployees().size() + " employees");
        }
    }

    /**
     * Section 3 — orphanRemoval: remove from list → DELETE DB row.
     *
     * <p>WHY orphanRemoval: children that cannot exist without their parent
     * should be automatically deleted when removed from the collection.
     * Without it, orphaned rows with NULL FK values accumulate silently.
     *
     * @param sf     the SessionFactory
     * @param deptId the Department to modify
     */
    private static void demonstrateOrphanRemoval(SessionFactory sf, Long deptId) {
        System.out.println("\n--- orphanRemoval: remove employee from list -> deletes from DB ---");

        try (Session session = sf.openSession()) {
            session.beginTransaction();

            Department dept = session.get(Department.class, deptId);
            System.out.println("  Before removal: " + dept.getEmployees().size() + " employees");

            // WHY helper method: sets emp.department=null so in-memory state is
            // consistent. orphanRemoval detects null parent → issues DELETE on flush.
            Employee toRemove = dept.getEmployees().get(0);
            dept.removeEmployee(toRemove);

            session.getTransaction().commit();
            System.out.println("  After removal: "
                    + dept.getEmployees().size() + " employees (DB row deleted)");
        }
    }

    /**
     * Section 4 — JPQL query to find employees in a named department.
     *
     * @param sf     the SessionFactory
     * @param deptId the Department ID to filter by
     */
    private static void queryEmployeesInDepartment(SessionFactory sf, Long deptId) {
        System.out.println("\n--- Query: find all employees in Engineering department ---");

        try (Session session = sf.openSession()) {
            // WHY JPQL path navigation (e.department.id): Hibernate translates the
            // dot-path to a JOIN automatically — single SQL, no lazy collection access
            List<Employee> engineers = session.createQuery(
                    "SELECT e FROM OneToManyDemo$Employee e "
                            + "WHERE e.department.id = :deptId",
                    Employee.class)
                    .setParameter("deptId", deptId)
                    .list();

            System.out.println("  Found " + engineers.size() + " engineers:");
            engineers.forEach(e -> System.out.println("    - " + e.getName()));
        }
    }
}

// EXPECTED OUTPUT:
// =================================================
// === OneToMany Demo ===
//
// --- Create Department with 3 Employees (cascade persist) ---
//   Saved dept id=1 with 3 employees
//
// --- Add Employee to existing Department ---
//   Added Dave. Dept now has 4 employees
//
// --- orphanRemoval: remove employee from list -> deletes from DB ---
//   Before removal: 4 employees
//   After removal: 3 employees (DB row deleted)
//
// --- Query: find all employees in Engineering department ---
//   Found 3 engineers:
//     - Bob
//     - Carol
//     - Dave
// =================================================
