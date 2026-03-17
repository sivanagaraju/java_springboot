import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  TEXT BLOCKS, VAR, & COLLECTION FACTORIES — Demo            ║
 * ║  Convenience features from Java 9-15                        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  Text Blocks:  Multi-line strings with """..."""          │
 * │  var:          Local type inference (still static typing) │
 * │  Factories:    List.of(), Set.of(), Map.of() → immutable │
 * │  String API:   isBlank(), strip(), lines(), repeat()      │
 * └──────────────────────────────────────────────────────────┘
 */
public class ModernSyntaxDemo {

    public static void main(String[] args) {
        System.out.println("═══ MODERN SYNTAX DEMO ═══\n");

        // 1. Text Blocks
        System.out.println("1. Text Blocks:");
        String sql = """
                SELECT u.name, u.email
                FROM users u
                JOIN orders o ON u.id = o.user_id
                WHERE o.total > 100.0
                ORDER BY u.name
                """;
        System.out.println("  SQL:\n" + sql);

        String json = """
                {
                  "name": "John",
                  "age": 25,
                  "roles": ["admin", "user"]
                }
                """;
        System.out.println("  JSON:\n" + json);

        // Text block with string interpolation (formatted)
        String name = "Alice";
        int age = 30;
        String template = """
                Hello, %s!
                You are %d years old.
                """.formatted(name, age);
        System.out.println("  Formatted:\n" + template);

        // 2. var — local type inference
        System.out.println("2. var Keyword:");
        var list = new ArrayList<String>();       // ArrayList<String>
        var count = list.size();                  // int
        var message = "Hello, var!";              // String
        System.out.println("  list type: " + list.getClass().getSimpleName());
        System.out.println("  count type: " + ((Object) count).getClass().getSimpleName());
        System.out.println("  message: " + message);

        // var in for loops
        var numbers = List.of(1, 2, 3, 4, 5);
        System.out.print("  for-each: ");
        for (var n : numbers) {
            System.out.print(n + " ");
        }
        System.out.println();

        // 3. Collection Factories (Java 9)
        System.out.println("\n3. Collection Factories:");
        var immutableList = List.of("a", "b", "c");
        var immutableSet  = Set.of(1, 2, 3);
        var immutableMap  = Map.of("key1", "val1", "key2", "val2");
        System.out.println("  List: " + immutableList);
        System.out.println("  Set:  " + immutableSet);
        System.out.println("  Map:  " + immutableMap);

        try {
            immutableList.add("d");
        } catch (UnsupportedOperationException e) {
            System.out.println("  ❌ List.of() is immutable — add() throws!");
        }

        // 4. String Enhancements (Java 11)
        System.out.println("\n4. String Enhancements:");
        System.out.println("  isBlank(\"  \"): " + "  ".isBlank());
        System.out.println("  strip(\"  hi  \"): '" + "  hi  ".strip() + "'");
        System.out.println("  repeat(\"ha\", 3): " + "ha".repeat(3));
        System.out.println("  lines count: " + "a\nb\nc".lines().count());

        "first\nsecond\nthird".lines().forEach(line ->
            System.out.println("    line: " + line));

        // 5. Sequenced Collections (Java 21)
        System.out.println("\n5. Sequenced Collections (Java 21):");
        var seqList = List.of("A", "B", "C", "D");
        System.out.println("  getFirst: " + seqList.getFirst());
        System.out.println("  getLast:  " + seqList.getLast());
        System.out.println("  reversed: " + seqList.reversed());

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Text blocks clean up SQL/JSON/HTML in code.");
        System.out.println("var reduces verbosity, keeps type safety.");
        System.out.println("List.of()/Set.of()/Map.of() create immutable collections.");
    }
}
