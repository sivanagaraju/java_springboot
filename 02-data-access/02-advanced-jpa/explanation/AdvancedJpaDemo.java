/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : AdvancedJpaDemo.java                                   ║
 * ║  MODULE : 02-data-access / 02-advanced-jpa                      ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates advanced JPA patterns —           ║
 * ║                   entity relationships, cascade operations,      ║
 * ║                   fetch strategies, and transaction behavior      ║
 * ║  WHY IT EXISTS  : Real applications have tables linked by        ║
 * ║                   foreign keys. Without proper relationship      ║
 * ║                   mapping, you get N+1 queries, cascade bugs,    ║
 * ║                   and LazyInitializationExceptions.              ║
 * ║  PYTHON COMPARE : SQLAlchemy relationship() with                 ║
 * ║                   back_populates + cascade="all, delete-orphan"  ║
 * ║  USE CASES      : 1) Department ←→ Employee hierarchy           ║
 * ║                   2) Cascade save (save parent = save children)  ║
 * ║                   3) JOIN FETCH to fix N+1 problem               ║
 * ║                   4) orphanRemoval for child cleanup             ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Bidirectional @OneToMany Relationship           ║
 * ║                                                                   ║
 * ║    ┌────────────────┐         ┌────────────────┐                 ║
 * ║    │   Department   │         │    Employee     │                 ║
 * ║    │────────────────│         │────────────────│                 ║
 * ║    │ id (PK)        │         │ id (PK)        │                 ║
 * ║    │ name           │◄───────┤ department_id   │                 ║
 * ║    │                │ 1    N  │ (FK)           │                 ║
 * ║    │ employees ─────┤mappedBy │ name           │                 ║
 * ║    │ (List)         │         │ email          │                 ║
 * ║    └────────────────┘         └────────────────┘                 ║
 * ║                                                                   ║
 * ║    INVERSE side               OWNING side                        ║
 * ║    (@OneToMany)               (@ManyToOne + @JoinColumn)         ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : Conceptual demo — patterns only               ║
 * ║  EXPECTED OUTPUT: N/A (not standalone-runnable)                  ║
 * ║  RELATED FILES  : 01-entity-relationships.md,                   ║
 * ║                   02-understanding-transactions.md               ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

// =======================
// PART 1: ENTITY CLASSES
// =======================

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Department entity — the PARENT in a @OneToMany relationship.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class Department(Base):
 *       __tablename__ = "departments"
 *       id = Column(Integer, primary_key=True)
 *       name = Column(String(100), nullable=False, unique=True)
 *       employees = relationship("Employee", back_populates="department",
 *                                cascade="all, delete-orphan")
 * </pre>
 *
 * <p><b>ASCII — Cascade Behavior:</b>
 * <pre>
 *   departmentRepository.save(dept)
 *       │
 *       ├── INSERT INTO departments (name) VALUES ('Engineering')
 *       │
 *       ├── cascade = ALL triggers:
 *       │       │
 *       │       ├── INSERT INTO employees (name, department_id) VALUES ('Alice', 1)
 *       │       └── INSERT INTO employees (name, department_id) VALUES ('Bob', 1)
 *       │
 *       └── All 3 rows committed in ONE transaction
 * </pre>
 */
@Entity
@Table(name = "departments")
class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * One department has many employees.
     * mappedBy = "department" → points to the 'department' field in Employee.
     * cascade = ALL → persist/delete employees when department is persisted/deleted.
     * orphanRemoval = true → delete employee if removed from this list.
     * fetch = LAZY → employees NOT loaded until first accessed.
     */
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();

    protected Department() {}

    public Department(String name) {
        this.name = name;
    }

    /**
     * Helper method to maintain BOTH sides of the bidirectional relationship.
     *
     * <p><b>Why this is needed:</b>
     * <pre>
     *   WITHOUT helper:                    WITH helper:
     *   dept.getEmployees().add(emp);      dept.addEmployee(emp);
     *   emp.setDepartment(dept);           // ← sets both sides automatically
     *   // ← easy to forget one side!
     * </pre>
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Employee> getEmployees() { return employees; }
}

/**
 * Employee entity — the CHILD (owning side) in a @ManyToOne relationship.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class Employee(Base):
 *       __tablename__ = "employees"
 *       id = Column(Integer, primary_key=True)
 *       name = Column(String(200), nullable=False)
 *       department_id = Column(Integer, ForeignKey("departments.id"))
 *       department = relationship("Department", back_populates="employees")
 * </pre>
 *
 * <p><b>ASCII — Owning Side Rule:</b>
 * <pre>
 *   @ManyToOne (Employee.department)
 *       │
 *       └── OWNS the relationship
 *           └── Has the FK column: department_id
 *           └── @JoinColumn(name = "department_id")
 *
 *   @OneToMany (Department.employees)
 *       │
 *       └── INVERSE side
 *           └── mappedBy = "department"
 *           └── No FK column here
 * </pre>
 */
@Entity
@Table(name = "employees")
class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private String email;

    /** Many employees belong to one department. OWNING SIDE. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "hired_at", updatable = false)
    private LocalDateTime hiredAt;

    @PrePersist
    protected void onCreate() {
        this.hiredAt = LocalDateTime.now();
    }

    protected Employee() {}

    public Employee(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }
}


// =======================
// PART 2: REPOSITORIES
// =======================

/**
 * Repository for Department entities with custom query methods.
 *
 * <p><b>ASCII — N+1 Problem vs JOIN FETCH:</b>
 * <pre>
 *   WITHOUT JOIN FETCH (N+1 problem):
 *   Query 1: SELECT * FROM departments                    ← 1 query
 *   Query 2: SELECT * FROM employees WHERE dept_id = 1    ← N queries
 *   Query 3: SELECT * FROM employees WHERE dept_id = 2    (one per dept)
 *   Query N: SELECT * FROM employees WHERE dept_id = N
 *
 *   WITH JOIN FETCH (solved):
 *   Query 1: SELECT d.*, e.* FROM departments d
 *            LEFT JOIN employees e ON d.id = e.dept_id    ← 1 query!
 * </pre>
 */
interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    /** JOIN FETCH solves N+1 by loading departments AND employees in 1 SQL. */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Optional<Department> findByIdWithEmployees(@Param("id") Long id);
}

interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartmentName(String departmentName);

    boolean existsByEmail(String email);
}


// =======================
// PART 3: SERVICE LAYER
// =======================

/**
 * Service demonstrating transaction management and cascade operations.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python uses "with db.begin():" for transactions.
 *   # Spring uses @Transactional annotation (proxy-based).
 *   async def create_department(db: AsyncSession, name: str, employees: list):
 *       async with db.begin():
 *           dept = Department(name=name)
 *           for emp_data in employees:
 *               dept.employees.append(Employee(**emp_data))
 *           db.add(dept)  # cascade saves employees too
 * </pre>
 *
 * <p><b>ASCII — @Transactional Proxy Mechanism:</b>
 * <pre>
 *   Caller
 *       │
 *       ▼
 *   [ Spring AOP Proxy ]     ← intercepts method call
 *       │
 *       ├── BEGIN TRANSACTION
 *       │
 *       ▼
 *   [ Actual Service Method ] ← your business logic runs here
 *       │
 *       ├── Success? → COMMIT
 *       └── RuntimeException? → ROLLBACK
 *
 *   ⚠ Self-invocation BYPASSES the proxy:
 *   this.otherMethod()  ← calls directly, NO transaction!
 * </pre>
 */
@Service
class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                              EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Creates a department with employees in a SINGLE transaction.
     * Thanks to CascadeType.ALL, saving the department auto-saves all employees.
     *
     * <p><b>Flow:</b>
     * <pre>
     *   createDepartmentWithEmployees("Engineering", [["Alice","a@x"],["Bob","b@x"]])
     *       │
     *       ▼
     *   new Department("Engineering")
     *       │
     *       ├── dept.addEmployee(new Employee("Alice", "a@x"))
     *       └── dept.addEmployee(new Employee("Bob", "b@x"))
     *       │
     *       ▼
     *   departmentRepository.save(dept)
     *       │
     *       ├── INSERT INTO departments → id=1
     *       ├── INSERT INTO employees (Alice, dept_id=1)
     *       └── INSERT INTO employees (Bob, dept_id=1)
     *       │
     *       ▼
     *   COMMIT (all 3 rows in 1 transaction)
     * </pre>
     */
    @Transactional
    public Department createDepartmentWithEmployees(String deptName,
                                                      List<String[]> employeeData) {
        Department dept = new Department(deptName);

        for (String[] data : employeeData) {
            Employee emp = new Employee(data[0], data[1]);
            dept.addEmployee(emp);  // cascade = ALL → emp auto-saved
        }

        return departmentRepository.save(dept);
    }

    /**
     * Read-only transaction for fetching data.
     * readOnly = true → Hibernate skips dirty checking → better performance.
     */
    @Transactional(readOnly = true)
    public Department getDepartmentWithEmployees(Long id) {
        return departmentRepository.findByIdWithEmployees(id)
            .orElseThrow(() -> new RuntimeException("Department not found: " + id));
    }

    /**
     * Demonstrates orphanRemoval.
     *
     * <p><b>ASCII — orphanRemoval behavior:</b>
     * <pre>
     *   dept.getEmployees().removeIf(e -> e.getId() == 42)
     *       │
     *       ▼
     *   JPA detects Employee(42) removed from list
     *       │
     *       ▼                              (orphanRemoval = true)
     *   DELETE FROM employees WHERE id = 42   ← auto-generated!
     * </pre>
     */
    @Transactional
    public void removeEmployee(Long deptId, Long employeeId) {
        Department dept = departmentRepository.findByIdWithEmployees(deptId)
            .orElseThrow(() -> new RuntimeException("Department not found"));

        dept.getEmployees().removeIf(e -> e.getId().equals(employeeId));
    }
}
