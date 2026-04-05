/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_GenericPair.java                                 ║
 * ║  MODULE : 00-java-foundation / 03-advanced-oop                   ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — generic class with two type params  ║
 * ║  DEMONSTRATES   : <K,V> type parameters, type erasure, bounds   ║
 * ║  PYTHON COMPARE : Python typing.Generic[K, V]; Java Pair<K,V>   ║
 * ║                                                                  ║
 * ║  TYPE PARAMETER FLOW:                                            ║
 * ║   Pair<String, Integer> → K=String, V=Integer at compile time   ║
 * ║   At runtime: type erased to Pair<Object, Object> (erasure)     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

/**
 * A generic container holding exactly two related values of potentially
 * different types.
 *
 * <p>WHY generics: Without generics, Pair would hold {@code Object} and
 * require unsafe casts at every call site. Generics push type-checking
 * to compile time — the compiler guarantees {@code getKey()} always
 * returns K, never an incompatible type.
 *
 * <p>Python equivalent:
 * <pre>{@code
 *   from typing import Generic, TypeVar
 *   K = TypeVar('K'); V = TypeVar('V')
 *   class Pair(Generic[K, V]):
 *       def __init__(self, key: K, value: V): ...
 * }</pre>
 *
 * @param <K> the type of the first (key) element
 * @param <V> the type of the second (value) element
 */
class Pair<K, V> {

    // WHY private final: Pair is immutable by design — once created,
    // neither element changes. Immutability makes Pairs safe as Map keys.
    private final K key;
    private final V value;

    /**
     * Creates a Pair with the given key and value.
     *
     * @param key   the first element
     * @param value the second element
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the first element.
     *
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns the second element.
     *
     * @return the value
     */
    public V getValue() {
        return value;
    }

    /**
     * Creates a new Pair with key and value swapped.
     *
     * <p>Demonstrates using the generic type in a method return type.
     *
     * @return a new Pair with V as key and K as value
     */
    public Pair<V, K> swap() {
        return new Pair<>(value, key);
    }

    /**
     * Returns a debug-friendly string representation.
     *
     * @return string like {@code Pair(Alice, 95)}
     */
    @Override
    public String toString() {
        return "Pair(" + key + ", " + value + ")";
    }
}

/**
 * Test harness for the generic Pair class.
 */
public class Sol01_GenericPair {

    /**
     * Demonstrates Pair with several type combinations.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("--- Generic Pair Solution ---\n");

        // WHY diamond operator <>: compiler infers K=String, V=Integer
        // from the constructor argument types. No need to repeat.
        Pair<String, Integer> studentGrade = new Pair<>("Alice", 95);
        System.out.println("Student Grade: " + studentGrade.getKey()
                + " scored " + studentGrade.getValue());
        // Expected: Student Grade: Alice scored 95

        // WHY Integer (not int): generics require reference types.
        // Java auto-boxes int → Integer automatically here.
        Pair<Integer, String> response = new Pair<>(404, "Not Found");
        System.out.println("HTTP Response: " + response.getKey()
                + " " + response.getValue());
        // Expected: HTTP Response: 404 Not Found

        // Bonus: swap demonstrates flexible generic return types
        Pair<String, Integer> swapped = response.swap()
                // Pair<String, Integer> because response was <Integer, String>
                .swap(); // WHY: swap twice → back to original types
        System.out.println("\nSwap demo: " + response + " → swapped → " + swapped);

        // Bonus: Pair as function return value — avoids creating a full class
        // for a method that needs to return two related values
        Pair<Boolean, String> result = validateAge(25);
        System.out.println("\nvalidateAge(25): valid=" + result.getKey()
                + ", message='" + result.getValue() + "'");

        Pair<Boolean, String> result2 = validateAge(15);
        System.out.println("validateAge(15): valid=" + result2.getKey()
                + ", message='" + result2.getValue() + "'");
    }

    /**
     * Example method returning two values without creating a dedicated class.
     *
     * <p>In production prefer a record: {@code record ValidationResult(boolean valid, String message) {}}.
     *
     * @param age the age to validate
     * @return Pair of (isValid, message)
     */
    private static Pair<Boolean, String> validateAge(int age) {
        if (age >= 18) {
            return new Pair<>(true, "Age " + age + " is valid");
        }
        return new Pair<>(false, "Age " + age + " is below minimum (18)");
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using primitive types as type parameters
 *   WRONG: Pair<int, double> p = new Pair<>(1, 2.0);   // compile error
 *   RIGHT: Pair<Integer, Double> p = new Pair<>(1, 2.0); // auto-boxed
 *          Generics work with reference types only (type erasure limitation).
 *
 * MISTAKE 2: Raw types — omitting the type parameters
 *   WRONG: Pair p = new Pair("Alice", 95);   // compiles but unchecked
 *   PROBLEM: p.getKey() returns Object; caller must cast unsafely.
 *            Compiler issues unchecked warnings that suppress real bugs.
 *   RIGHT: Always specify type parameters.
 *
 * MISTAKE 3: Confusing Pair<String, Integer> with Pair<Integer, String>
 *   These are DIFFERENT types. The compiler catches misuse:
 *   Pair<String, Integer> p = new Pair<>(42, "hello"); // compile error
 *
 * MISTAKE 4: Type erasure surprise at runtime
 *   At runtime, Pair<String, Integer> and Pair<Integer, String> are
 *   the same class (Pair). instanceof cannot check type parameters:
 *   WRONG: if (p instanceof Pair<String, Integer>) { }  // compile error
 *   RIGHT: if (p instanceof Pair<?,?>) { }
 *
 * MISTAKE 5: Overusing Pair instead of named records
 *   Pair<String, Integer> is fine for quick utility. For domain objects,
 *   prefer a named record: record StudentGrade(String name, int score) {}
 *   — self-documenting, no ambiguity about which element means what.
 * ═══════════════════════════════════════════════════════════════════ */
