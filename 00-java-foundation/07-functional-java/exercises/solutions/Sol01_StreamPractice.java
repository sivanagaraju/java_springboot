/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_StreamPractice.java                              ║
 * ║  MODULE : 00-java-foundation / 07-functional-java                ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — Stream pipeline operations          ║
 * ║  DEMONSTRATES   : groupingBy, averagingDouble, flatMap, sorted   ║
 * ║  PYTHON COMPARE : sorted(key=) vs Comparator; groupby vs groupBy ║
 * ║                                                                  ║
 * ║  STREAM OPERATIONS:                                              ║
 * ║   source → filter → sorted → map → flatMap → collect            ║
 * ║   All intermediate ops are LAZY — only execute at terminal op    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.*;
import java.util.stream.*;

/**
 * Solutions for Stream API exercises using student data.
 *
 * <p>Key reminder: streams are lazy — intermediate operations ({@code filter},
 * {@code map}, {@code sorted}) only execute when a terminal operation
 * ({@code collect}, {@code count}, {@code forEach}) is called.
 * This enables the JVM to optimize the pipeline as a single pass.
 *
 * <p>Python bridge: Python's comprehensions ({@code [x for x in lst if ...]})
 * are evaluated eagerly. Java streams are lazy — like Python generators.
 */
public class Sol01_StreamPractice {

    /** Immutable student record — Java 16+ record syntax. */
    record Student(String name, String major, double gpa, List<String> courses) {}

    /** Returns sample test data. */
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

    /**
     * Returns names of top N students sorted by GPA (highest first).
     *
     * <p>Python: {@code [s.name for s in sorted(students, key=lambda x: -x.gpa)[:n]]}
     *
     * @param students list of students
     * @param n        number of top students to return
     * @return list of student names in descending GPA order
     */
    public static List<String> getTopStudents(List<Student> students, int n) {
        return students.stream()
                // WHY comparingDouble: avoids boxing to Double for comparison.
                // reversed() changes ascending to descending.
                .sorted(Comparator.comparingDouble(Student::gpa).reversed())
                .limit(n)
                .map(Student::name) // WHY map after limit: only extract name for the top N
                .collect(Collectors.toList());
    }

    /**
     * Computes average GPA per major.
     *
     * <p>Python: {@code {major: mean(s.gpa for s in group) for major, group in groupby(students, key=lambda s: s.major)}}
     *
     * @param students list of students
     * @return map from major → average GPA
     */
    public static Map<String, Double> averageGpaByMajor(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(
                        Student::major,                        // WHY groupBy major: partition into major buckets
                        Collectors.averagingDouble(Student::gpa) // WHY averagingDouble: avoids boxing, computes mean
                ));
    }

    /**
     * Classifies students and counts per grade category.
     *
     * <p>Categories: Dean's List (≥3.5), Good (≥3.0), Average (≥2.0), Probation (<2.0).
     *
     * @param students list of students
     * @return map from grade category → count
     */
    public static Map<String, Long> countByGrade(List<Student> students) {
        return students.stream()
                .collect(Collectors.groupingBy(
                        // WHY classify function in groupingBy: transforms each element
                        // to a group key before counting
                        s -> classify(s.gpa()),
                        Collectors.counting() // WHY counting(): downstream collector counts per group
                ));
    }

    /**
     * Helper: classifies a GPA into a grade category string.
     *
     * <p>Extracted to a separate method to keep the stream pipeline readable.
     *
     * @param gpa the GPA to classify
     * @return grade category string
     */
    private static String classify(double gpa) {
        if (gpa >= 3.5) return "Dean's List";
        if (gpa >= 3.0) return "Good";
        if (gpa >= 2.0) return "Average";
        return "Probation";
    }

    /**
     * Returns all unique course names sorted alphabetically across all students.
     *
     * <p>WHY flatMap: each student has a {@code List<String>} of courses.
     * Without flatMap, we'd get {@code Stream<List<String>>}.
     * flatMap flattens to {@code Stream<String>} — all courses in one stream.
     *
     * <p>Python: {@code sorted(set(c for s in students for c in s.courses))}
     *
     * @param students list of students
     * @return sorted list of unique course names
     */
    public static List<String> getFlattenedCourses(List<Student> students) {
        return students.stream()
                .flatMap(s -> s.courses().stream()) // WHY flatMap: unwrap List<String> to String
                .distinct()                         // WHY distinct: remove duplicates (e.g., "Calculus" appears 3 times)
                .sorted()                           // WHY sorted: natural String order (alphabetical)
                .collect(Collectors.toList());
    }

    /**
     * Runs all test cases with expected output.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        List<Student> students = getSampleStudents();
        System.out.println("=== Stream Practice Solutions ===\n");

        System.out.println("Top 3 students: " + getTopStudents(students, 3));
        // Expected: [Charlie(3.9), Alice(3.8), Frank(3.7)]

        System.out.println("\nAvg GPA by major: " + averageGpaByMajor(students));
        // Expected: CS≈3.5, Math≈3.45, Physics≈2.7

        System.out.println("\nCount by grade: " + countByGrade(students));
        // Expected: Dean's List=4, Good=1, Average=1, Probation=1

        System.out.println("\nAll unique courses (sorted):");
        getFlattenedCourses(students).forEach(c -> System.out.println("  " + c));
        // Expected: AI, Algorithms, Calculus, Databases, ... (alphabetical, no duplicates)
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Reusing a stream after terminal operation
 *   WRONG: Stream<Student> s = students.stream(); s.sorted(); s.collect(...);
 *   PROBLEM: IllegalStateException — stream already consumed by sorted().
 *   RIGHT: Chain all ops: students.stream().sorted().collect(...)
 *
 * MISTAKE 2: map() before flatMap() loses the flattening
 *   WRONG: students.stream().map(s -> s.courses().stream())
 *   RESULT: Stream<Stream<String>> — nested streams, not flat
 *   RIGHT: students.stream().flatMap(s -> s.courses().stream())
 *
 * MISTAKE 3: Collectors.toMap() with duplicate keys throws
 *   Collectors.toMap(Student::major, Student::gpa) throws IllegalStateException
 *   when multiple students share a major. Use groupingBy for multi-value groups.
 *
 * MISTAKE 4: sorted() on a very large stream with a bad comparator
 *   sorted() is a STATEFUL intermediate op — it must collect ALL elements
 *   before producing the first output. Don't use sorted() on infinite streams
 *   or streams of millions of records unless you limit() first.
 *
 * MISTAKE 5: comparingDouble vs comparing for GPA
 *   Comparator.comparing(Student::gpa) would box double→Double for comparison.
 *   comparingDouble(Student::gpa) avoids boxing — prefer for primitive types.
 * ═══════════════════════════════════════════════════════════════════ */
