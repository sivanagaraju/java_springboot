/**
 * ====================================================================
 * FILE    : Ex01_StreamPractice.java
 * MODULE  : 07 — Functional Java
 * PURPOSE : Practice Stream operations with real data processing
 * ====================================================================
 *
 * EXERCISES:
 *
 *   Use the provided list of students to solve each problem
 *   using a SINGLE stream pipeline per problem.
 *
 *   1. getTopStudents(students, n) → List<String>
 *      - Return names of top N students by GPA (highest first)
 *
 *   2. averageGpaByMajor(students) → Map<String, Double>
 *      - Group by major, compute average GPA per major
 *
 *   3. countByGrade(students) → Map<String, Long>
 *      - Classify: GPA >= 3.5 → "Dean's List", >= 3.0 → "Good",
 *        >= 2.0 → "Average", else → "Probation"
 *      - Count students in each category
 *
 *   4. getFlattenedCourses(students) → List<String>
 *      - Get ALL unique course names sorted alphabetically
 *      - HINT: Use flatMap to unwrap nested lists
 *
 * STREAM PIPELINE REMINDER:
 *
 *   source.stream()
 *       .filter(...)    ← keep elements matching condition
 *       .map(...)       ← transform each element
 *       .flatMap(...)   ← flatten nested structures
 *       .sorted(...)    ← order results
 *       .collect(...)   ← gather into collection
 *
 * ====================================================================
 */
import java.util.*;
import java.util.stream.*;

public class Ex01_StreamPractice {

    record Student(String name, String major, double gpa, List<String> courses) {}

    static List<Student> getSampleStudents() {
        return List.of(
            new Student("Alice",   "CS",      3.8, List.of("Algorithms", "Databases", "AI")),
            new Student("Bob",     "Math",    3.2, List.of("Calculus", "Linear Algebra", "Statistics")),
            new Student("Charlie", "CS",      3.9, List.of("Algorithms", "Networks", "OS")),
            new Student("Diana",   "Physics", 3.5, List.of("Quantum", "Calculus", "Statistics")),
            new Student("Eve",     "CS",      2.8, List.of("Databases", "Web Dev")),
            new Student("Frank",   "Math",    3.7, List.of("Calculus", "Number Theory", "Algorithms")),
            new Student("Grace",   "Physics", 1.9, List.of("Mechanics", "Thermodynamics"))
        );
    }

    // TODO: Return names of top N students by GPA
    public static List<String> getTopStudents(List<Student> students, int n) {
        // HINT: sorted(Comparator.comparingDouble(Student::gpa).reversed())
        return List.of();
    }

    // TODO: Average GPA grouped by major
    public static Map<String, Double> averageGpaByMajor(List<Student> students) {
        // HINT: Collectors.groupingBy + Collectors.averagingDouble
        return Map.of();
    }

    // TODO: Classify and count by grade category
    public static Map<String, Long> countByGrade(List<Student> students) {
        // HINT: Collectors.groupingBy(classifyFn, Collectors.counting())
        return Map.of();
    }

    // TODO: Get all unique courses sorted
    public static List<String> getFlattenedCourses(List<Student> students) {
        // HINT: flatMap(s -> s.courses().stream())
        return List.of();
    }

    public static void main(String[] args) {
        List<Student> students = getSampleStudents();
        System.out.println("=== Stream Practice ===\n");
        System.out.println("Top 3: " + getTopStudents(students, 3));
        System.out.println("Avg GPA by major: " + averageGpaByMajor(students));
        System.out.println("Count by grade: " + countByGrade(students));
        System.out.println("All courses: " + getFlattenedCourses(students));
    }
}
