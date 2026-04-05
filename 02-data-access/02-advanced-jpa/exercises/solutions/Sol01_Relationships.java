/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_Relationships.java                               ║
 * ║  MODULE : 02-data-access / 02-advanced-jpa / exercises/solutions ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : SOLUTION — JPA entity relationships with      ║
 * ║                   promoted join entity pattern                   ║
 * ║  DIFFICULTY     : Intermediate                                   ║
 * ║  PYTHON COMPARE : SQLAlchemy relationship() + ForeignKey()      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Student / Course / Enrollment Relationship     ║
 * ║                                                                  ║
 * ║  Student ──(1:N)──► Enrollment ◄──(N:1)── Course               ║
 * ║                          │                                       ║
 * ║                    enrolledAt + grade  ← extra columns           ║
 * ║                                                                  ║
 * ║  WHY NOT @ManyToMany directly?                                   ║
 * ║  @ManyToMany creates a JOIN table with only FKs.                 ║
 * ║  We need enrolledAt and grade → need a real entity in between.  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

package solutions;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// ===========================================================================
// ENTITY 1: Student
// ===========================================================================

/**
 * Student entity — the "one" side of the 1:N relationship with Enrollment.
 *
 * <p>Uses CascadeType.ALL with orphanRemoval=true so that when a Student is deleted,
 * all their Enrollment records are also deleted (logical ownership: student owns enrollments).
 *
 * <p><b>ASCII — cascade and orphanRemoval behavior:</b>
 * <pre>
 *   studentRepo.delete(alice);
 *       │
 *       ├── DELETE FROM enrollments WHERE student_id = alice.id  ← cascade ALL
 *       └── DELETE FROM students WHERE id = alice.id
 *
 *   alice.getEnrollments().remove(enrollment);
 *       └── DELETE FROM enrollments WHERE id = enrollment.id  ← orphanRemoval
 * </pre>
 */
@Entity
@Table(name = "students")
public class Sol01_Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Email — unique constraint prevents duplicate student registrations.
     *
     * <p>WHY @Column(unique=true) and not @UniqueConstraint? Both work.
     * @Column(unique=true) is shorthand for a single-column unique constraint.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Student's course enrollments.
     *
     * <p>WHY CascadeType.ALL? A student owns their enrollments — lifecycle operations
     * (persist, merge, remove, refresh) should cascade from Student to Enrollment.
     * <p>WHY orphanRemoval=true? If an enrollment is removed from this collection,
     * delete it from the database automatically (avoids dangling records).
     * <p>WHY FetchType.LAZY? Default for @OneToMany. Never use EAGER on collections
     * — loads all enrollments on every Student load, regardless of whether they're needed.
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true,
               fetch = FetchType.LAZY)
    private List<Sol01_Enrollment> enrollments = new ArrayList<>();

    /** Required by JPA for proxy generation. Protected prevents misuse. */
    protected Sol01_Student() {}

    /**
     * Business constructor.
     *
     * @param name  the student's full name (non-null)
     * @param email the student's unique email (non-null)
     */
    public Sol01_Student(String name, String email) {
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.email = Objects.requireNonNull(email, "Email must not be null");
    }

    /**
     * Helper method to enroll the student in a course.
     *
     * <p>WHY a helper method instead of direct list manipulation? The bidirectional
     * relationship must be kept in sync from BOTH sides.
     * Without this helper: {@code student.enrollments} has the enrollment but
     * {@code course.enrollments} doesn't — the in-memory graph is inconsistent.
     *
     * @param course the course to enroll in
     * @return the created enrollment
     */
    public Sol01_Enrollment enrollInCourse(Sol01_Course course) {
        Sol01_Enrollment enrollment = new Sol01_Enrollment();
        enrollment.setStudent(this);
        enrollment.setCourse(course);

        // Sync both sides of the bidirectional relationship
        this.enrollments.add(enrollment);
        course.getEnrollments().add(enrollment);

        return enrollment;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Sol01_Enrollment> getEnrollments() { return enrollments; }
}

// ===========================================================================
// ENTITY 2: Course
// ===========================================================================

/**
 * Course entity — the "one" side of the 1:N relationship with Enrollment.
 *
 * <p>WHY cascade = {PERSIST, MERGE} and NOT CascadeType.ALL here?
 * Deleting a Course should NOT delete Students. If we used CascadeType.ALL,
 * deleting a course would cascade REMOVE to enrollments, and from enrollments
 * to students — catastrophically deleting all enrolled students!
 */
@Entity
@Table(name = "courses")
public class Sol01_Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    /** Longer text content stored as TEXT column (no length limit). */
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer credits;

    /**
     * Course enrollments — the "one" side.
     *
     * <p>WHY NO CascadeType.REMOVE? Deleting a course removes enrollments, NOT students.
     * Using ALL here would cascade REMOVE to Enrollment → then to Student. Dangerous!
     * Only cascade PERSIST (save course → save enrollments) and MERGE (update propagation).
     */
    @OneToMany(mappedBy = "course", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
               fetch = FetchType.LAZY)
    private List<Sol01_Enrollment> enrollments = new ArrayList<>();

    protected Sol01_Course() {}

    public Sol01_Course(String title, String description, Integer credits) {
        this.title = Objects.requireNonNull(title);
        this.description = description;
        this.credits = Objects.requireNonNull(credits);
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getCredits() { return credits; }
    public List<Sol01_Enrollment> getEnrollments() { return enrollments; }
}

// ===========================================================================
// ENTITY 3: Enrollment (the promoted join entity)
// ===========================================================================

/**
 * Enrollment — the join entity between Student and Course.
 *
 * <p>This is the "promoted @ManyToMany" pattern: instead of letting JPA manage
 * a hidden join table via {@code @ManyToMany}, we create a real entity so we can
 * add business data ({@code enrolledAt}, {@code grade}) to the relationship.
 *
 * <p>A unique constraint on (student_id, course_id) prevents duplicate enrollments.
 */
@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_enrollment_student_course",
           columnNames = {"student_id", "course_id"}
       ))
public class Sol01_Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The owning side of the Student relationship.
     *
     * <p>WHY FetchType.LAZY? Default for @ManyToOne is EAGER, which would load the full
     * Student object every time an Enrollment is loaded — often unnecessary.
     * Explicit LAZY prevents N+1 query problems when loading lists of enrollments.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Sol01_Student student;

    /**
     * The owning side of the Course relationship.
     *
     * <p>@JoinColumn specifies the foreign key column name in the enrollments table.
     * Without this, JPA uses a default name that may not match your DB schema.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Sol01_Course course;

    /**
     * Timestamp when the student enrolled.
     *
     * <p>WHY updatable=false? Enrollment date should never change after creation.
     */
    @Column(updatable = false)
    private LocalDateTime enrolledAt;

    /** Grade assigned after course completion — null until graded. */
    @Column(length = 2)
    private String grade;

    /**
     * Auto-sets enrolledAt on first INSERT via JPA lifecycle callback.
     *
     * <p>WHY @PrePersist and not a constructor default? Ensures the timestamp is set
     * by the JVM clock at the actual database persist moment, not at object creation.
     */
    @PrePersist
    protected void onCreate() {
        this.enrolledAt = LocalDateTime.now();
    }

    protected Sol01_Enrollment() {}

    public Long getId() { return id; }
    public Sol01_Student getStudent() { return student; }
    public void setStudent(Sol01_Student student) { this.student = student; }
    public Sol01_Course getCourse() { return course; }
    public void setCourse(Sol01_Course course) { this.course = course; }
    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Using @ManyToMany directly when extra columns are needed
 *   WRONG: @ManyToMany on Student and Course
 *   WHY BAD: @ManyToMany generates a hidden join table with only FK columns.
 *            You cannot add enrolledAt or grade to a hidden table.
 *   FIX: Create a real Enrollment entity and map @ManyToOne from both sides.
 *
 * MISTAKE 2: Using CascadeType.ALL on the Course side
 *   WRONG: @OneToMany(cascade = ALL) on Course.enrollments
 *   WHY BAD: CascadeType.REMOVE on Course would delete the enrollment record,
 *            which could cascade further to delete students in some mappings.
 *   FIX: Use cascade = {PERSIST, MERGE} on Course — never REMOVE.
 *
 * MISTAKE 3: Forgetting to sync both sides of a bidirectional relationship
 *   WRONG: student.getEnrollments().add(enrollment) without setting enrollment.setStudent(student)
 *   WHY BAD: The in-memory object graph is inconsistent. Hibernate uses the OWNING SIDE
 *            (@ManyToOne) to generate the FK — the collection side is read-only for SQL.
 *            But the in-memory inconsistency causes bugs in the same session.
 *   FIX: Always use the helper method (enrollInCourse) to sync both sides.
 *
 * MISTAKE 4: Using FetchType.EAGER on collection associations
 *   WRONG: @OneToMany(fetch = EAGER) on Student.enrollments
 *   WHY BAD: Every Student query loads ALL their enrollments immediately.
 *            Loading 100 students triggers 100 extra queries = N+1 problem.
 *   FIX: Use FetchType.LAZY (default for @OneToMany) and use JOIN FETCH when needed.
 */
