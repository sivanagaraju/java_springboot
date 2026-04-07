/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_UniversityRelationships.java                     ║
 * ║  MODULE : 04-hibernate-jpa / 02-relationships                   ║
 * ║  GRADLE : ./gradlew :04-hibernate-jpa:run                       ║
 * ║           -PmainClass=com.learning.hibernate.relationships       ║
 * ║                      .exercises.Ex01_UniversityRelationships     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE LEVEL : 1 (Guided — target: under 20 minutes)         ║
 * ║  PURPOSE        : Add relationship annotations to University     ║
 * ║                   entities so Hibernate can manage the FK/join   ║
 * ║                   tables automatically.                          ║
 * ║  COVERS         : @OneToMany, @ManyToOne, @ManyToMany, mappedBy  ║
 * ║                   cascade, orphanRemoval, FetchType              ║
 * ║                                                                  ║
 * ║  INSTRUCTIONS   : Complete the 5 TODO items in order.           ║
 * ║  READ FIRST     : explanation/02-one-to-many.md                 ║
 * ║                   explanation/03-many-to-many.md                ║
 * ║                                                                  ║
 * ║  SUCCESS SIGNAL : Console shows university structure with        ║
 * ║                   professors and students enrolled in courses.   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.relationships.exercises;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exercise 1: University Relationships.
 *
 * <p>Four entities model a university domain:
 * <ul>
 *   <li>{@link Department} owns a list of {@link Professor}s — one-to-many.</li>
 *   <li>{@link Student} enrolls in many {@link Course}s, and a Course has many Students
 *       — many-to-many, with Student as the owning side.</li>
 * </ul>
 *
 * <p>The entities are static inner classes so the entire exercise compiles from one file.
 * In production code every entity would live in its own file under a dedicated package.
 *
 * <p>Work through the TODO comments top-to-bottom. Each one tells you exactly which
 * annotations to add and explains WHY those settings matter.
 */
public class Ex01_UniversityRelationships {

    // =========================================================================
    // ENTITY 1 of 4: Department
    // =========================================================================

    /**
     * Academic department that owns one or more professors.
     *
     * <p>Database table: {@code departments}
     * <p>Relationship role: "one" side of the one-to-many with {@link Professor}.
     * The departments table has NO foreign key; instead the professors table stores
     * a {@code dept_id} column that points back here.
     */
    @Entity
    @Table(name = "departments")
    public static class Department {

        /** Surrogate primary key — H2 IDENTITY column strategy. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /** Human-readable department label, e.g. "Computer Science". */
        private String name;

        // -------------------------------------------------------------------------
        // TODO 1: Annotate the professors collection with @OneToMany.
        //
        //   Add these annotations on the line immediately above the field:
        //
        //     @OneToMany(
        //         mappedBy      = "department",        // Professor.department field owns the FK
        //         cascade       = CascadeType.PERSIST, // persisting Dept auto-persists Professors
        //         orphanRemoval = true,                // removing from list => DELETE professor row
        //         fetch         = FetchType.LAZY       // defer loading until the collection is accessed
        //     )
        //
        //   WHY cascade=PERSIST and NOT cascade=ALL?
        //     ALL includes REMOVE. Deleting a Department with ALL would also delete every
        //     professor in it. In a real university a professor can be reassigned, so you
        //     only want the save to cascade — not the delete.
        //
        //   WHY orphanRemoval=true?
        //     Makes this collection the single source of truth for membership.
        //     dept.getProfessors().remove(prof); session.flush(); → issues DELETE SQL.
        //
        //   WHY FetchType.LAZY?
        //     A query like "SELECT d FROM Department d WHERE d.name = :name" should NOT
        //     immediately load all professors; you may only need the department name.
        //     LAZY defers the extra SELECT until you actually iterate getProfessors().
        //
        //   >>> ADD THE ANNOTATIONS ABOVE THE NEXT LINE, THEN DELETE THIS BLOCK <<<
        // -------------------------------------------------------------------------
        private List<Professor> professors = new ArrayList<>();

        /**
         * Convenience helper — always prefer this over direct list manipulation.
         *
         * <p>Sets BOTH sides of the bidirectional link: adds to this department's list
         * AND sets {@code professor.department = this}. Omitting the second step is the
         * single most common bidirectional JPA bug — the FK column stays NULL.
         *
         * @param p professor to attach; must not be {@code null}
         */
        public void addProfessor(Professor p) {
            professors.add(p);
            p.setDepartment(this); // both sides must be set in bidirectional relationships
        }

        // --- accessors ---

        public Long getId() { return id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<Professor> getProfessors() { return professors; }
        public void setProfessors(List<Professor> professors) { this.professors = professors; }

        @Override
        public String toString() {
            return "Department{id=" + id + ", name='" + name + "'}";
        }
    }

    // =========================================================================
    // ENTITY 2 of 4: Professor
    // =========================================================================

    /**
     * Professor who belongs to exactly one department.
     *
     * <p>Database table: {@code professors}
     * <p>Relationship role: "many" side AND owning side of the Department↔Professor link.
     * The professors table holds the {@code dept_id} FK column because this entity owns
     * the association. The FK column name is declared via {@code @JoinColumn}.
     */
    @Entity
    @Table(name = "professors")
    public static class Professor {

        /** Surrogate primary key. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /** Professor's full name. */
        private String name;

        /** Research or teaching specialization, e.g. "Algorithms". */
        private String specialization;

        // -------------------------------------------------------------------------
        // TODO 2: Annotate the department field with @ManyToOne and @JoinColumn.
        //
        //   Add these two annotations on the lines immediately above the field:
        //
        //     @ManyToOne(fetch = FetchType.LAZY)
        //     @JoinColumn(name = "dept_id")
        //
        //   WHY is @ManyToOne the owning side?
        //     In SQL the FK lives in the child table (professors.dept_id → departments.id).
        //     JPA mirrors SQL: the entity whose table holds the FK column is the owner.
        //     The @OneToMany on Department declares mappedBy="department" to acknowledge
        //     it is a read-only mirror — it does not write the FK column itself.
        //
        //   WHY @JoinColumn(name = "dept_id")?
        //     Without it Hibernate generates a column name from convention, which might be
        //     "department_id". Being explicit eliminates ambiguity when DBAs review the schema.
        //
        //   WHY FetchType.LAZY?
        //     When you load a list of professors (e.g. "show all profs in dept X"), you
        //     already have the department. Loading it again via eager fetch is wasted SQL.
        //
        //   >>> ADD THE ANNOTATIONS ABOVE THE NEXT LINE, THEN DELETE THIS BLOCK <<<
        // -------------------------------------------------------------------------
        private Department department;

        // --- accessors ---

        public Long getId() { return id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }

        public Department getDepartment() { return department; }
        public void setDepartment(Department department) { this.department = department; }

        @Override
        public String toString() {
            return "Professor{id=" + id + ", name='" + name
                    + "', specialization='" + specialization + "'}";
        }
    }

    // =========================================================================
    // ENTITY 3 of 4: Student
    // =========================================================================

    /**
     * Student who can enroll in many courses.
     *
     * <p>Database table: {@code students}
     * <p>Relationship role: OWNING side of the Student↔Course many-to-many.
     * The {@code student_course} join table is declared here. {@link Course} mirrors
     * the relationship using {@code mappedBy="courses"}.
     */
    @Entity
    @Table(name = "students")
    public static class Student {

        /** Surrogate primary key. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /** Student's full name. */
        private String name;

        // -------------------------------------------------------------------------
        // TODO 3: Annotate the courses field with @ManyToMany and @JoinTable.
        //
        //   Add these annotations on the lines immediately above the field:
        //
        //     @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        //     @JoinTable(
        //         name               = "student_course",
        //         joinColumns        = @JoinColumn(name = "student_id"),
        //         inverseJoinColumns = @JoinColumn(name = "course_id")
        //     )
        //
        //   WHY Set<Course> and not List<Course>?
        //     Set prevents duplicate enrollments. With List, calling enroll(cs101) twice
        //     inserts two rows in the student_course join table. Set.add() is idempotent —
        //     adding the same Course object twice is silently ignored. This is purely a
        //     Java collection property; the database also has a composite PK on the join
        //     table, but the constraint error is much worse UX than the silent no-op.
        //
        //   WHY only CascadeType.PERSIST and CascadeType.MERGE?
        //     Courses are shared entities — many students enroll in the same course.
        //     CascadeType.REMOVE would mean "delete this student → also delete the course",
        //     wiping it for ALL enrolled students. Never cascade REMOVE across a shared
        //     many-to-many.
        //
        //   WHY @JoinTable here and NOT on Course?
        //     One side must declare the join table. The other side uses mappedBy to defer
        //     to this declaration. If you put @JoinTable on both sides, Hibernate creates
        //     two separate join tables — a subtle bug that only shows up at runtime.
        //
        //   >>> ADD THE ANNOTATIONS ABOVE THE NEXT LINE, THEN DELETE THIS BLOCK <<<
        // -------------------------------------------------------------------------
        private Set<Course> courses = new HashSet<>();

        /**
         * Enrolls this student in a course, keeping both sides of the link in sync.
         *
         * @param c the course to enroll in; safe to call again even if already enrolled
         *          because the backing Set ignores duplicate adds
         */
        public void enroll(Course c) {
            courses.add(c);
            c.getStudents().add(this); // inverse side must stay in sync in-memory
        }

        // --- accessors ---

        public Long getId() { return id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Set<Course> getCourses() { return courses; }
        public void setCourses(Set<Course> courses) { this.courses = courses; }

        @Override
        public String toString() {
            return "Student{id=" + id + ", name='" + name + "'}";
        }
    }

    // =========================================================================
    // ENTITY 4 of 4: Course
    // =========================================================================

    /**
     * Academic course that many students can enroll in.
     *
     * <p>Database table: {@code courses}
     * <p>Relationship role: INVERSE side of the Student↔Course many-to-many.
     * Course uses {@code mappedBy="courses"} to delegate join-table ownership to
     * {@link Student}. Writes to this set are ignored by Hibernate's dirty-checking.
     */
    @Entity
    @Table(name = "courses")
    public static class Course {

        /** Surrogate primary key. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /** Full course title, e.g. "Data Structures". */
        private String title;

        /** Short catalogue code, e.g. "CS201". */
        private String code;

        // -------------------------------------------------------------------------
        // TODO 4: Annotate the students field with @ManyToMany(mappedBy = "courses").
        //
        //   Add this annotation on the line immediately above the field:
        //
        //     @ManyToMany(mappedBy = "courses")
        //
        //   WHY mappedBy?
        //     mappedBy declares this side as the INVERSE (non-owning) side.
        //     The join table is created and managed by the annotation on Student.courses.
        //     Hibernate ignores any adds/removes you make to Course.students for the
        //     purposes of writing to the join table — changes MUST go through Student.courses.
        //
        //   WHY no cascade here?
        //     Cascade would propagate saves/deletes from Course to Student. A course being
        //     archived should never delete the students who enrolled in it.
        //
        //   WHY no @JoinTable here?
        //     Declaring @JoinTable on both sides creates two join tables. Only the owning
        //     side (Student) should declare it.
        //
        //   >>> ADD THE ANNOTATION ABOVE THE NEXT LINE, THEN DELETE THIS BLOCK <<<
        // -------------------------------------------------------------------------
        private Set<Student> students = new HashSet<>();

        // --- accessors ---

        public Long getId() { return id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public Set<Student> getStudents() { return students; }
        public void setStudents(Set<Student> students) { this.students = students; }

        @Override
        public String toString() {
            return "Course{id=" + id + ", title='" + title + "', code='" + code + "'}";
        }
    }

    // =========================================================================
    // MAIN — Complete TODO 5
    // =========================================================================

    /**
     * Entry point. Bootstraps an H2 in-memory database, persists the university
     * structure, and prints a summary to verify the relationships work correctly.
     *
     * <p>The SessionFactory setup is provided — do not modify it. Your work starts at
     * the TODO 5 comment block below.
     *
     * @param args unused
     */
    public static void main(String[] args) {

        // SessionFactory setup — provided, do not modify -------------------------
        SessionFactory sessionFactory = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url",
                        "jdbc:h2:mem:university;DB_CLOSE_DELAY=-1")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "true")
                .setProperty("hibernate.format_sql", "true")
                .addAnnotatedClass(Department.class)
                .addAnnotatedClass(Professor.class)
                .addAnnotatedClass(Student.class)
                .addAnnotatedClass(Course.class)
                .buildSessionFactory();
        // End provided setup -----------------------------------------------------

        // -------------------------------------------------------------------------
        // TODO 5: Implement the following 8 steps inside a Hibernate session.
        //
        //   Open a session with: Session session = sessionFactory.openSession();
        //   Start a transaction: Transaction tx = session.beginTransaction();
        //
        //   5a. Create a Department named "Computer Science".
        //         Department dept = new Department();
        //         dept.setName("Computer Science");
        //
        //   5b. Create 2 professors and add them using dept.addProfessor():
        //         Prof 1 — name="Dr. Ada Lovelace",  specialization="Programming Languages"
        //         Prof 2 — name="Dr. Alan Turing",   specialization="Algorithms"
        //       HINT: addProfessor() sets both sides; do NOT also call prof.setDepartment().
        //
        //   5c. Create 2 courses:
        //         Course 1 — title="Java Fundamentals",  code="CS101"
        //         Course 2 — title="Data Structures",    code="CS201"
        //
        //   5d. Create 1 student:
        //         Student alice = new Student();
        //         alice.setName("Alice");
        //
        //   5e. Enroll Alice in both courses using alice.enroll(course).
        //
        //   5f. Persist with cascade — only TWO calls needed:
        //         session.persist(dept);    // cascade=PERSIST saves professors automatically
        //         session.persist(alice);   // cascade=PERSIST saves courses + join rows
        //       Do NOT call session.persist() separately on each Professor or Course.
        //
        //   5g. Commit: tx.commit();
        //
        //   5h. Print results (you may re-open a session or use the same one):
        //         "Department: Computer Science | Professors: [Dr. Ada Lovelace, Dr. Alan Turing]"
        //         "Student Alice enrolled in: [Java Fundamentals, Data Structures]"
        //
        //   5i. Close: session.close();
        //
        //   >>> REPLACE THIS COMMENT BLOCK WITH YOUR IMPLEMENTATION <<<
        // -------------------------------------------------------------------------

        System.out.println("\n=== TODO 5: Replace this placeholder with your implementation ===\n");

        sessionFactory.close();
    }
}
