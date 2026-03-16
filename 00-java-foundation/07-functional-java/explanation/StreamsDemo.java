/**
 * ====================================================================
 * FILE    : StreamsDemo.java
 * MODULE  : 07 — Functional Java
 * PURPOSE : Stream pipeline: source → intermediate → terminal
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: [x*2 for x in nums if x > 3]
 *   Java:   nums.stream().filter(x -> x > 3).map(x -> x*2).toList()
 *
 * STREAM PIPELINE MODEL:
 *
 *   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
 *   │ SOURCE  │────▶│ filter  │────▶│  map    │────▶│ collect │
 *   │.stream()│     │ (lazy)  │     │ (lazy)  │     │(triggers│
 *   └─────────┘     └─────────┘     └─────────┘     │  ALL!)  │
 *                                                    └─────────┘
 *
 * LAZY EXECUTION:
 *
 *   ┌────────────────────────────────────────────────────┐
 *   │  Element-by-element pipeline (NOT stage-by-stage): │
 *   │                                                    │
 *   │  elem 1 → filter ✓ → map → collect(add)          │
 *   │  elem 2 → filter ✗ → SKIP                        │
 *   │  elem 3 → filter ✓ → map → collect(add)          │
 *   │  ...                                               │
 *   │                                                    │
 *   │  ✦ Short-circuits possible (limit, findFirst)     │
 *   └────────────────────────────────────────────────────┘
 *
 * ====================================================================
 */
import java.util.*;
import java.util.stream.*;

public class StreamsDemo {

    // ── Simple data class for demos ─────────────────────────────────
    record Employee(String name, String dept, int salary) {}

    public static void main(String[] args) {

        System.out.println("=== BASIC STREAM OPERATIONS ===");

        List<Integer> numbers = List.of(5, 3, 8, 1, 9, 2, 7, 4, 6);

        // Filter + Map + Collect
        List<Integer> doubled = numbers.stream()
                .filter(n -> n > 4)           // keep: 5, 8, 9, 7, 6
                .map(n -> n * 2)              // transform: 10, 16, 18, 14, 12
                .sorted()                     // sort: 10, 12, 14, 16, 18
                .toList();                    // collect to immutable List
        System.out.println("Doubled (>4): " + doubled);

        // Reduce (fold everything into one value)
        int sum = numbers.stream()
                .reduce(0, Integer::sum);     // 0 + 5 + 3 + 8 + ... = 45
        System.out.println("Sum: " + sum);

        // Statistics
        IntSummaryStatistics stats = numbers.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        System.out.println("Stats — min:" + stats.getMin()
                + " max:" + stats.getMax()
                + " avg:" + stats.getAverage()
                + " count:" + stats.getCount());

        System.out.println("\n=== REAL-WORLD: Employee Processing ===");

        List<Employee> employees = List.of(
                new Employee("Alice",   "Engineering", 95000),
                new Employee("Bob",     "Marketing",   72000),
                new Employee("Charlie", "Engineering", 88000),
                new Employee("Diana",   "Marketing",   65000),
                new Employee("Eve",     "Engineering", 110000),
                new Employee("Frank",   "Sales",       78000)
        );

        // ── Get names of engineers earning > 90k ────────────────────
        List<String> highPaidEngineers = employees.stream()
                .filter(e -> e.dept().equals("Engineering"))
                .filter(e -> e.salary() > 90000)
                .map(Employee::name)
                .toList();
        System.out.println("High-paid engineers: " + highPaidEngineers);

        // ── Group by department ─────────────────────────────────────
        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::dept));
        byDept.forEach((dept, emps) ->
                System.out.println("  " + dept + ": "
                        + emps.stream().map(Employee::name).toList()));

        // ── Average salary per department ───────────────────────────
        Map<String, Double> avgSalary = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::dept,
                        Collectors.averagingInt(Employee::salary)
                ));
        System.out.println("Avg salary: " + avgSalary);

        // ── Highest paid employee ───────────────────────────────────
        employees.stream()
                .max(Comparator.comparingInt(Employee::salary))
                .ifPresent(e -> System.out.println("Highest paid: " + e.name()
                        + " ($" + e.salary() + ")"));

        System.out.println("\n=== STRING OPERATIONS ===");

        List<String> words = List.of("the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog");

        // Join with delimiter
        String sentence = words.stream()
                .collect(Collectors.joining(" "));
        System.out.println("Joined: " + sentence);

        // Distinct + sorted
        List<String> unique = words.stream()
                .distinct()
                .sorted()
                .toList();
        System.out.println("Unique sorted: " + unique);

        // Word frequency (Collectors.groupby + counting)
        Map<String, Long> freq = words.stream()
                .collect(Collectors.groupingBy(
                        w -> w,
                        Collectors.counting()
                ));
        System.out.println("Frequency: " + freq);

        System.out.println("\n=== flatMap: FLATTENING NESTED STRUCTURES ===");

        // ── flatMap unwraps one level of nesting ────────────────────
        //
        // map:     Stream<List<String>> → still nested!
        // flatMap: Stream<String>       → flattened!
        //
        //  [ ["a","b"], ["c","d"] ]
        //     map    → Stream of [ ["a","b"], ["c","d"] ]  (nested)
        //     flatMap→ Stream of [ "a", "b", "c", "d" ]   (flat)
        //
        List<List<String>> nested = List.of(
                List.of("Java", "Kotlin"),
                List.of("Python", "Ruby"),
                List.of("Go", "Rust")
        );

        List<String> flat = nested.stream()
                .flatMap(Collection::stream)  // flatten each inner list
                .sorted()
                .toList();
        System.out.println("Flattened: " + flat);

        System.out.println("\n=== SHORT-CIRCUIT OPERATIONS ===");

        // ── findFirst + limit: stop processing early ────────────────
        Optional<Integer> firstBig = numbers.stream()
                .filter(n -> n > 7)
                .findFirst();               // stops after finding first match!
        System.out.println("First > 7: " + firstBig.orElse(-1));

        // anyMatch / allMatch / noneMatch
        boolean hasNegative = numbers.stream().anyMatch(n -> n < 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        System.out.println("Has negative: " + hasNegative);
        System.out.println("All positive: " + allPositive);

        System.out.println("\n=== STREAM CREATION METHODS ===");

        // From values
        Stream.of("A", "B", "C").forEach(System.out::print);
        System.out.println();

        // From range (like Python's range())
        IntStream.range(1, 6).forEach(n -> System.out.print(n + " "));
        System.out.println(" (exclusive end)");

        // Generate infinite stream + limit
        Stream.generate(Math::random).limit(3)
                .forEach(d -> System.out.printf("  Random: %.4f%n", d));

        // Iterate (like Python's itertools.count)
        Stream.iterate(1, n -> n * 2).limit(8)
                .forEach(n -> System.out.print(n + " "));
        System.out.println(" (powers of 2)");
    }
}
