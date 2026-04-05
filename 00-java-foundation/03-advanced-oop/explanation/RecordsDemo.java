/**
 * ╔════════════════════════════════════════════════════════════════════════════╗
 * ║  FILE   : RecordsDemo.java                                               ║
 * ║  MODULE : 00-java-foundation / 03-advanced-oop                           ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run -PmainClass=RecordsDemo      ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates Java records as immutable value carriers  ║
 * ║  WHY IT EXISTS  : Replace mutable DTO boilerplate with compiler-backed   ║
 * ║                   value semantics, generated methods, and validation      ║
 * ║  PYTHON COMPARE : @dataclass(frozen=True) / Pydantic model               ║
 * ║  USE CASES      : DTOs, event payloads, API responses, config snapshots  ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                            ║
 * ║                                                                          ║
 * ║   Caller / Controller                                                     ║
 * ║          │                                                               ║
 * ║          ▼                                                               ║
 * ║   [RecordsDemo] ----creates----> [StudentProfile record]                 ║
 * ║          │                                │                              ║
 * ║          │                                ├-- compact constructor         ║
 * ║          │                                ├-- accessors / equals()       ║
 * ║          │                                └-- toString() / hashCode()    ║
 * ║          ▼                                                               ║
 * ║   immutable payload ready for logging / transport / comparison           ║
 * ║                                                                          ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run -PmainClass=RecordsDemo ║
 * ║  EXPECTED OUTPUT: Value equality, defensive copying, and pattern         ║
 * ║                   matching against an immutable payload                  ║
 * ║  RELATED FILES  : 07-records.md, 06-object-class.md, GenericsDemo.java  ║
 * ╚════════════════════════════════════════════════════════════════════════════╝
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Shows why records are a better fit than mutable boilerplate classes when the
 * job is "carry data safely across a boundary."
 *
 * <p>Python equivalent:
 * <pre>
 *   @dataclass(frozen=True)
 *   class StudentProfile:
 *       student_id: str
 *       name: str
 *       skills: list[str]
 * </pre>
 * Python dataclasses reduce boilerplate, while Java records go one step further
 * by making the data-carrier contract explicit in the type definition itself.
 *
 * <p>Architecture view:
 * <pre>
 *   [Service / Controller]
 *            |
 *            v
 *   [RecordsDemo] ----> [StudentProfile record]
 *            |                  |
 *            |                  +-- compiler generates accessors
 *            +-- compares / logs / pattern-matches the value
 * </pre>
 */
public final class RecordsDemo {

    /**
     * Immutable student snapshot used to demonstrate record semantics.
     *
     * <p>Python equivalent:
     * <pre>
     *   @dataclass(frozen=True)
     *   class StudentProfile:
     *       student_id: str
     *       name: str
     *       skills: list[str]
     * </pre>
     *
     * <p>Construction flow:
     * <pre>
     *   incoming values
     *       |
     *       v
     *   validate null / blank input
     *       |
     *       v
     *   copy mutable list defensively
     *       |
     *       v
     *   immutable value object leaves constructor
     * </pre>
     */
    public record StudentProfile(String studentId, String name, List<String> skills) {

        /**
         * Validates and normalizes the record components before the value exists.
         */
        StudentProfile {
            Objects.requireNonNull(studentId, "studentId must not be null");
            Objects.requireNonNull(name, "name must not be null");
            Objects.requireNonNull(skills, "skills must not be null");

            studentId = studentId.strip();
            name = name.strip();

            if (studentId.isBlank()) {
                throw new IllegalArgumentException("studentId must not be blank");
            }
            if (name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }

            // Records should behave like frozen value objects at the boundary.
            // List.copyOf() prevents outside mutation from leaking into the record.
            skills = List.copyOf(skills);
        }

        /**
         * Returns a compact console-friendly summary of the record.
         *
         * @return a single-line summary string for demo output
         */
        String summary() {
            return studentId + " | " + name + " | skills=" + skills.size();
        }
    }

    private RecordsDemo() {
        // Utility demo class.
    }

    /**
     * Runs the record walkthrough from construction to pattern matching.
     *
     * <p>Flow:
     * <pre>
     *   main()
     *     -> printRecordBasics()
     *     -> printDefensiveCopyDemo()
     *     -> printPatternMatchingDemo()
     * </pre>
     *
     * @param args command-line arguments; unused in this demo
     */
    public static void main(String[] args) {
        StudentProfile primary = new StudentProfile(
            "S-101",
            "Asha",
            List.of("Java 21", "Spring Boot", "JDBC")
        );
        StudentProfile duplicate = new StudentProfile(
            "S-101",
            "Asha",
            List.of("Java 21", "Spring Boot", "JDBC")
        );

        printRecordBasics(primary, duplicate);
        printDefensiveCopyDemo();
        printPatternMatchingDemo(primary);

        System.out.println("\n=== WHY RECORDS HELP ===");
        System.out.println("They remove boilerplate and make immutable intent obvious at the type level.");

        /*
         * EXPECTED OUTPUT:
         * ================
         * === RECORD BASICS ===
         * Record value: StudentProfile[...]
         * Equal values: true
         * Same hash code: true
         *
         * === DEFENSIVE COPY CHECK ===
         * Source list after mutation: [Git, SQL, Docker]
         * Record still frozen: [Git, SQL]
         *
         * === PATTERN MATCHING ===
         * Matched StudentProfile for Asha
         */
    }

    /**
     * Prints the compiler-generated behavior that records give you for free.
     *
     * @param primary the first record instance
     * @param duplicate a second record with the same component values
     */
    private static void printRecordBasics(StudentProfile primary, StudentProfile duplicate) {
        System.out.println("=== RECORD BASICS ===");
        System.out.println("Record value:   " + primary);
        System.out.println("Summary:        " + primary.summary());
        System.out.println("Equal values:   " + primary.equals(duplicate));
        System.out.println("Same hash code: " + (primary.hashCode() == duplicate.hashCode()));
    }

    /**
     * Demonstrates why defensive copying still matters inside records.
     *
     * <p>Python equivalent: converting a mutable list to a tuple or frozen model
     * before returning it from an API boundary.</p>
     */
    private static void printDefensiveCopyDemo() {
        System.out.println("\n=== DEFENSIVE COPY CHECK ===");

        List<String> externalSkills = new ArrayList<>(List.of("Git", "SQL"));
        StudentProfile defensiveCopy = new StudentProfile("S-202", "Mina", externalSkills);

        externalSkills.add("Docker");

        System.out.println("Source list after mutation: " + externalSkills);
        System.out.println("Record still frozen:        " + defensiveCopy.skills());
    }

    /**
     * Shows Java's pattern matching syntax reading a record without a manual cast.
     *
     * @param payload the record to inspect through an Object reference
     */
    private static void printPatternMatchingDemo(Object payload) {
        System.out.println("\n=== PATTERN MATCHING ===");

        if (payload instanceof StudentProfile profile) {
            System.out.println("Matched StudentProfile for " + profile.name());
            System.out.println("Accessors stay readable: " + profile.studentId() + ", " + profile.name());
        }
    }
}
