/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_Relationships.java                                ║
 * ║  MODULE : 02-data-access / 02-advanced-jpa / exercises          ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — Implement entity relationships     ║
 * ║                   with proper JPA annotations                    ║
 * ║  DIFFICULTY     : Intermediate                                   ║
 * ║  PYTHON COMPARE : SQLAlchemy relationship() + ForeignKey()      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Entity Relationship Design                      ║
 * ║                                                                   ║
 * ║    ┌──────────┐         ┌──────────────┐        ┌─────────┐     ║
 * ║    │ Student  │         │  Enrollment  │        │ Course  │     ║
 * ║    │──────────│         │──────────────│        │─────────│     ║
 * ║    │ id  (PK) │◄───┐   │ id (PK)      │   ┌──►│ id (PK) │     ║
 * ║    │ name     │    └───┤ student_id FK│   │   │ title   │     ║
 * ║    │ email    │    1:N │ course_id  FK├───┘   │ credits │     ║
 * ║    │          │        │ enrolled_at  │  N:1  │         │     ║
 * ║    │enrollments│        │ grade        │        │enrollments│    ║
 * ║    └──────────┘         └──────────────┘        └─────────┘     ║
 * ║                                                                   ║
 * ║    WHY promoted join entity: pure @ManyToMany can't have         ║
 * ║    enrolled_at and grade columns — need intermediate entity      ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : Not standalone — add to a Spring Boot app     ║
 * ║  EXPECTED OUTPUT: 3 tables: students, courses, enrollments      ║
 * ║  RELATED FILES  : 01-entity-relationships.md                    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * PYTHON COMPARISON:
 * In SQLAlchemy, the same relationship would be:
 *
 *   class Enrollment(Base):
 *       __tablename__ = "enrollments"
 *       id = Column(Integer, primary_key=True)
 *       student_id = Column(ForeignKey("students.id"), nullable=False)
 *       course_id = Column(ForeignKey("courses.id"), nullable=False)
 *       enrolled_at = Column(DateTime, default=func.now())
 *       grade = Column(String(2))
 *       student = relationship("Student", back_populates="enrollments")
 *       course = relationship("Course", back_populates="enrollments")
 */

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

// ===========================================================================
// ENTITY 1: Student
// ===========================================================================

// TODO 1: Add @Entity and @Table(name = "students") annotations.
public class Student {

    // TODO 2: Add @Id and @GeneratedValue(strategy = GenerationType.IDENTITY).
    private Long id;

    // TODO 3: Add @Column(nullable = false, length = 200).
    private String name;

    // TODO 4: Add @Column(nullable = false, unique = true).
    private String email;

    // TODO 5: Add @OneToMany relationship to Enrollment.
    // Use mappedBy = "student", cascade = ALL, orphanRemoval = true.
    private List<Enrollment> enrollments = new ArrayList<>();

    // TODO 6: Add a helper method to enroll in a course.
    // public void enrollInCourse(Course course) {
    //     Enrollment enrollment = new Enrollment();
    //     enrollment.setStudent(this);
    //     enrollment.setCourse(course);
    //     this.enrollments.add(enrollment);
    //     course.getEnrollments().add(enrollment);
    // }

    protected Student() {}

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // TODO 7: Add getters and setters.
}

// ===========================================================================
// ENTITY 2: Course
// ===========================================================================

// TODO 8: Add @Entity and @Table(name = "courses") annotations.
public class Course {

    // TODO 9: Add @Id and @GeneratedValue.
    private Long id;

    // TODO 10: Add @Column(nullable = false, length = 300).
    private String title;

    // TODO 11: Add @Column(columnDefinition = "TEXT").
    private String description;

    // TODO 12: Add @Column(nullable = false).
    private Integer credits;

    // TODO 13: Add @OneToMany relationship to Enrollment.
    // Use mappedBy = "course" — do NOT use cascade REMOVE here!
    // (Deleting a course should not delete students.)
    // Use cascade = {PERSIST, MERGE} instead.
    private List<Enrollment> enrollments = new ArrayList<>();

    protected Course() {}

    public Course(String title, String description, Integer credits) {
        this.title = title;
        this.description = description;
        this.credits = credits;
    }

    // TODO 14: Add getters.
}

// ===========================================================================
// ENTITY 3: Enrollment (the promoted @ManyToMany join entity)
// ===========================================================================

// TODO 15: Add @Entity and @Table(name = "enrollments").
// TODO 16: Add a unique constraint: @Table(uniqueConstraints =
//   @UniqueConstraint(columnNames = {"student_id", "course_id"}))
// This prevents a student from enrolling in the same course twice.
public class Enrollment {

    // TODO 17: Add @Id and @GeneratedValue.
    private Long id;

    // TODO 18: Add @ManyToOne(fetch = FetchType.LAZY).
    // Add @JoinColumn(name = "student_id", nullable = false).
    private Student student;

    // TODO 19: Add @ManyToOne(fetch = FetchType.LAZY).
    // Add @JoinColumn(name = "course_id", nullable = false).
    private Course course;

    // TODO 20: Add @Column(updatable = false) and @PrePersist to auto-set.
    private LocalDateTime enrolledAt;

    // TODO 21: This is nullable — grade is assigned after course completion.
    private String grade;

    @PrePersist
    protected void onCreate() {
        this.enrolledAt = LocalDateTime.now();
    }

    protected Enrollment() {}

    // TODO 22: Add getters and setters.
}
