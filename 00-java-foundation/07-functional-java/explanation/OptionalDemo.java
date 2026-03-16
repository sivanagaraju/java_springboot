/**
 * ====================================================================
 * FILE    : OptionalDemo.java
 * MODULE  : 07 — Functional Java
 * PURPOSE : Optional patterns, safe null handling, chaining
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: x = value if value is not None else "default"
 *   Java:   String x = Optional.ofNullable(value).orElse("default")
 *
 * OPTIONAL STATES:
 *
 *   ┌─────────────────────────┐     ┌─────────────────────────┐
 *   │ Optional.of("Alice")    │     │ Optional.empty()         │
 *   │ ┌─────────────────────┐│     │ ┌─────────────────────┐ │
 *   │ │ value = "Alice"     ││     │ │ value = null         │ │
 *   │ │ isPresent = true    ││     │ │ isPresent = false    │ │
 *   │ └─────────────────────┘│     │ └─────────────────────┘ │
 *   └─────────────────────────┘     └─────────────────────────┘
 *
 * ANTI-PATTERNS (NEVER DO):
 *
 *   ✗ Optional<String> field;        → use @Nullable
 *   ✗ void method(Optional<T> param) → use overloads
 *   ✗ optional.get() w/o check       → use orElse/orElseThrow
 *   ✗ if (opt.isPresent()) opt.get() → use map/flatMap/orElse
 *
 * ====================================================================
 */
import java.util.*;

public class OptionalDemo {

    // ── Simulate a database/service layer ───────────────────────────
    static Map<String, String> users = Map.of(
            "U001", "Alice",
            "U002", "Bob",
            "U003", "Charlie"
    );

    static Map<String, String> addresses = Map.of(
            "Alice", "123 Main St",
            "Charlie", "456 Oak Ave"
    );

    static Optional<String> findUser(String id) {
        return Optional.ofNullable(users.get(id));
    }

    static Optional<String> findAddress(String name) {
        return Optional.ofNullable(addresses.get(name));
    }

    public static void main(String[] args) {

        System.out.println("=== CREATING OPTIONALS ===");

        // ── Three creation methods ──────────────────────────────────
        Optional<String> full = Optional.of("Hello");        // MUST be non-null
        Optional<String> nullable = Optional.ofNullable(null); // safe with null
        Optional<String> empty = Optional.empty();             // explicitly empty

        System.out.println("full:     " + full);
        System.out.println("nullable: " + nullable);
        System.out.println("empty:    " + empty);

        // of(null) would throw NullPointerException!
        // Optional.of(null); // ← DON'T DO THIS

        System.out.println("\n=== SAFE ACCESS PATTERNS ===");

        Optional<String> name = findUser("U001");

        // ── orElse: provide default (always evaluated) ──────────────
        String result1 = name.orElse("Unknown");
        System.out.println("orElse: " + result1);

        // ── orElseGet: lazy default (evaluated only if empty) ───────
        //
        // DIFFERENCE:
        //   orElse("default")     → "default" is ALWAYS created
        //   orElseGet(() -> ...)   → supplier called ONLY if Optional is empty
        //
        //   Use orElseGet when default is expensive (DB call, object creation)
        //
        String result2 = name.orElseGet(() -> "Computed-" + System.currentTimeMillis());
        System.out.println("orElseGet: " + result2);

        // ── orElseThrow: throw if absent ────────────────────────────
        //
        // SPRING PATTERN:
        //   User user = repo.findById(id)
        //       .orElseThrow(() -> new ResourceNotFoundException(id));
        //
        try {
            String missing = findUser("U999")
                    .orElseThrow(() -> new RuntimeException("User not found: U999"));
        } catch (RuntimeException e) {
            System.out.println("orElseThrow: " + e.getMessage());
        }

        System.out.println("\n=== MAP — Transform Value If Present ===");

        // ── map: transform Optional's value ─────────────────────────
        //
        // FLOW:
        //   Optional("Alice") → map(toUpperCase) → Optional("ALICE")
        //   Optional.empty()  → map(toUpperCase) → Optional.empty()
        //
        Optional<Integer> nameLength = findUser("U001")
                .map(String::length);
        System.out.println("User U001 name length: " + nameLength.orElse(0));

        Optional<Integer> missingLength = findUser("U999")
                .map(String::length);
        System.out.println("User U999 name length: " + missingLength.orElse(0));

        System.out.println("\n=== FLATMAP — Chain Optionals ===");

        // ── flatMap: when the mapping function returns Optional ─────
        //
        // PROBLEM with map:
        //   findUser("U001")             → Optional("Alice")
        //   .map(name -> findAddress(name)) → Optional(Optional("123 Main St"))
        //   ← nested Optional! Bad!
        //
        // SOLUTION with flatMap:
        //   findUser("U001")                → Optional("Alice")
        //   .flatMap(n -> findAddress(n))    → Optional("123 Main St")
        //   ← flat! Good!
        //
        //   ┌──────────────────────────────────────────────────┐
        //   │  map:     fn returns T     → Optional<T>          │
        //   │  flatMap: fn returns Opt<T> → Optional<T> (FLAT!) │
        //   └──────────────────────────────────────────────────┘
        //
        String address = findUser("U001")
                .flatMap(OptionalDemo::findAddress)
                .orElse("No address");
        System.out.println("U001 address: " + address);

        String noAddress = findUser("U002")  // Bob exists but has no address
                .flatMap(OptionalDemo::findAddress)
                .orElse("No address");
        System.out.println("U002 address: " + noAddress);

        String noUser = findUser("U999")     // User doesn't exist
                .flatMap(OptionalDemo::findAddress)
                .orElse("No address");
        System.out.println("U999 address: " + noUser);

        System.out.println("\n=== FILTER — Conditional Keep ===");

        // ── filter: keep value only if predicate matches ────────────
        Optional<String> longName = findUser("U001")
                .filter(n -> n.length() > 3);
        System.out.println("Long name (>3): " + longName);

        Optional<String> shortName = findUser("U002")
                .filter(n -> n.length() > 5);
        System.out.println("Short name (>5): " + shortName);  // Bob is only 3 chars

        System.out.println("\n=== ifPresent / ifPresentOrElse ===");

        // ── ifPresent: execute action only if value exists ──────────
        findUser("U001").ifPresent(n -> System.out.println("Found: " + n));

        // ── ifPresentOrElse (Java 9+) ───────────────────────────────
        findUser("U999").ifPresentOrElse(
                n -> System.out.println("Found: " + n),
                () -> System.out.println("User not found!")
        );

        System.out.println("\n=== STREAM INTEGRATION ===");

        // ── Optional.stream() (Java 9+): integrate with streams ────
        List<String> ids = List.of("U001", "U999", "U002", "U003");

        List<String> foundUsers = ids.stream()
                .map(OptionalDemo::findUser)     // Stream<Optional<String>>
                .flatMap(Optional::stream)        // Stream<String> (empties removed)
                .toList();
        System.out.println("Found users: " + foundUsers);
    }
}
