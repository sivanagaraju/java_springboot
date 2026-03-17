import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  PATTERN MATCHING & SWITCH EXPRESSIONS — Demo               ║
 * ║  instanceof patterns, switch patterns, record patterns     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  OLD: if (obj instanceof String) { String s = (String)obj; } │
 * │  NEW: if (obj instanceof String s) { /* use s */ }      │
 * │                                                          │
 * │  OLD: switch + break + default                           │
 * │  NEW: switch expression → with patterns and guards       │
 * └──────────────────────────────────────────────────────────┘
 */
public class PatternMatchingDemo {

    // Sealed hierarchy for exhaustive matching
    sealed interface Animal permits Dog, Cat, Fish {}
    record Dog(String name, String breed) implements Animal {}
    record Cat(String name, boolean indoor) implements Animal {}
    record Fish(String species, double tankSize) implements Animal {}

    // Pattern matching in switch (exhaustive)
    static String describe(Animal animal) {
        return switch (animal) {
            case Dog d when d.breed().equals("Labrador")
                -> "🐕 " + d.name() + " the friendly Lab!";
            case Dog d
                -> "🐕 " + d.name() + " (" + d.breed() + ")";
            case Cat c when c.indoor()
                -> "🐱 " + c.name() + " (indoor kitty)";
            case Cat c
                -> "🐱 " + c.name() + " (outdoor adventurer)";
            case Fish f
                -> "🐟 " + f.species() + " in " + f.tankSize() + "L tank";
        };
    }

    // Heterogeneous object matching
    static String format(Object obj) {
        return switch (obj) {
            case Integer i when i < 0   -> "Negative: " + i;
            case Integer i              -> "Integer: " + i;
            case String s when s.isBlank() -> "Blank string";
            case String s               -> "String: \"" + s + "\"";
            case double[] arr           -> "double[] length " + arr.length;
            case List<?> list when list.isEmpty() -> "Empty list";
            case List<?> list           -> "List with " + list.size() + " items";
            case null                   -> "null!";
            default                     -> obj.getClass().getSimpleName();
        };
    }

    // instanceof pattern matching
    static void oldVsNew(Object obj) {
        // Old way
        if (obj instanceof String) {
            String s = (String) obj;
            System.out.println("  OLD: length = " + s.length());
        }

        // New way — pattern variable binding
        if (obj instanceof String s && s.length() > 3) {
            System.out.println("  NEW: \"" + s + "\" has " + s.length() + " chars");
        }
    }

    // Switch expression (enhanced)
    static String dayCategory(String day) {
        return switch (day.toUpperCase()) {
            case "MON", "TUE", "WED", "THU", "FRI" -> "Workday";
            case "SAT", "SUN" -> "Weekend";
            default -> throw new IllegalArgumentException("Not a day: " + day);
        };
    }

    public static void main(String[] args) {
        System.out.println("═══ PATTERN MATCHING DEMO ═══\n");

        // 1. Sealed class pattern matching
        System.out.println("1. Sealed Class Matching:");
        Animal[] animals = {
            new Dog("Buddy", "Labrador"),
            new Dog("Rex", "Shepherd"),
            new Cat("Whiskers", true),
            new Cat("Storm", false),
            new Fish("Nemo", 50.0)
        };
        for (Animal a : animals) {
            System.out.println("  " + describe(a));
        }

        // 2. Heterogeneous matching
        System.out.println("\n2. Object Matching:");
        Object[] objects = {42, -7, "Hello", "  ", new double[]{1,2,3}, List.of(), List.of("a","b"), null};
        for (Object obj : objects) {
            System.out.println("  " + format(obj));
        }

        // 3. instanceof pattern
        System.out.println("\n3. instanceof Pattern:");
        oldVsNew("Hello World");

        // 4. Switch expression
        System.out.println("\n4. Switch Expression:");
        for (String day : List.of("Mon", "Sat", "Wed", "Sun")) {
            System.out.println("  " + day + " → " + dayCategory(day));
        }

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Pattern matching eliminates manual casting.");
        System.out.println("Guarded patterns (when) replace if-else chains.");
        System.out.println("Sealed + patterns = compile-time exhaustiveness.");
    }
}
