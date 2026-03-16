/**
 * ====================================================================
 * FILE    : LambdaDemo.java
 * MODULE  : 07 — Functional Java
 * PURPOSE : Lambda syntax, functional interfaces, method references
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: lambda x: x * 2
 *   Java:   x -> x * 2
 *   Python: sorted(list, key=len)
 *   Java:   list.sort(Comparator.comparing(String::length))
 *
 * LAMBDA = ANONYMOUS FUNCTION:
 *
 *   ┌──────────────────────────────────────────────────┐
 *   │  BEFORE (anonymous class):                       │
 *   │  Comparator<String> c = new Comparator<String>() │
 *   │    @Override                                      │
 *   │    public int compare(String a, String b) {      │
 *   │      return a.length() - b.length();              │
 *   │    }                                              │
 *   │  };                                               │
 *   │                                                    │
 *   │  AFTER (lambda):                                   │
 *   │  Comparator<String> c = (a, b) ->                │
 *   │      a.length() - b.length();                     │
 *   └──────────────────────────────────────────────────┘
 *
 * FUNCTIONAL INTERFACE MAP:
 *
 *   ┌──────────────────┬──────────────┬──────────────────┐
 *   │  Interface       │  Method      │  Signature       │
 *   ├──────────────────┼──────────────┼──────────────────┤
 *   │  Predicate<T>    │  test(T)     │  T → boolean     │
 *   │  Function<T,R>   │  apply(T)    │  T → R           │
 *   │  Consumer<T>     │  accept(T)   │  T → void        │
 *   │  Supplier<T>     │  get()       │  () → T          │
 *   │  Comparator<T>   │  compare(T,T)│  (T,T) → int    │
 *   └──────────────────┴──────────────┴──────────────────┘
 *
 * ====================================================================
 */
import java.util.*;
import java.util.function.*;

public class LambdaDemo {

    public static void main(String[] args) {

        System.out.println("=== LAMBDA SYNTAX EVOLUTION ===");

        // ── Anonymous class → Lambda progression ────────────────────
        List<String> names = new ArrayList<>(List.of("Charlie", "Alice", "Bob", "Diana"));

        // Step 1: Anonymous inner class (pre-Java 8)
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
        System.out.println("Anon class sort: " + names);

        // Step 2: Lambda with explicit types
        names.sort((String a, String b) -> a.compareTo(b));
        System.out.println("Lambda (typed):  " + names);

        // Step 3: Lambda with type inference
        names.sort((a, b) -> a.compareTo(b));
        System.out.println("Lambda (inferred): " + names);

        // Step 4: Method reference (shortest form!)
        names.sort(String::compareTo);
        System.out.println("Method reference:  " + names);

        System.out.println("\n=== PREDICATE — Test/Filter ===");

        // ── Predicate<T>: T → boolean ──────────────────────────────
        //
        // FLOW: element → predicate.test(element) → true/false
        //       "Alice" → isLong.test("Alice") → true (length 5 > 4)
        //       "Bob"   → isLong.test("Bob")   → false (length 3 ≤ 4)
        //
        Predicate<String> isLong = s -> s.length() > 4;
        Predicate<String> startsWithA = s -> s.startsWith("A");

        System.out.println("isLong(Alice): " + isLong.test("Alice"));
        System.out.println("startsWithA(Bob): " + startsWithA.test("Bob"));

        // Combining predicates
        Predicate<String> longAndA = isLong.and(startsWithA);
        Predicate<String> longOrA = isLong.or(startsWithA);
        Predicate<String> notLong = isLong.negate();

        names.stream().filter(longAndA).forEach(n -> System.out.println("  longAndA: " + n));

        System.out.println("\n=== FUNCTION — Transform ===");

        // ── Function<T,R>: T → R ───────────────────────────────────
        Function<String, Integer> toLength = String::length;
        Function<String, String> toUpper = String::toUpperCase;

        System.out.println("toLength(Alice): " + toLength.apply("Alice"));
        System.out.println("toUpper(hello):  " + toUpper.apply("hello"));

        // Chaining: andThen (f.andThen(g) = g(f(x)))
        Function<String, Integer> upperLength = toUpper.andThen(toLength);
        System.out.println("upperLength(hello): " + upperLength.apply("hello"));

        System.out.println("\n=== CONSUMER — Side Effects ===");

        // ── Consumer<T>: T → void ──────────────────────────────────
        Consumer<String> printer = s -> System.out.println("  >> " + s);
        Consumer<String> logger = s -> System.out.println("  [LOG] " + s);

        // Chain consumers
        Consumer<String> printAndLog = printer.andThen(logger);
        printAndLog.accept("Hello");

        System.out.println("\n=== SUPPLIER — Factory ===");

        // ── Supplier<T>: () → T ────────────────────────────────────
        Supplier<List<String>> listFactory = ArrayList::new;
        Supplier<String> greeting = () -> "Hello at " + System.currentTimeMillis();

        List<String> freshList = listFactory.get();  // new ArrayList!
        System.out.println("Fresh list: " + freshList);
        System.out.println("Greeting: " + greeting.get());

        System.out.println("\n=== METHOD REFERENCES (::) ===");

        // ── 4 types of method references ────────────────────────────
        //
        // TYPE 1: Static method     (ClassName::staticMethod)
        Function<String, Integer> parser = Integer::parseInt;
        System.out.println("Static ref: " + parser.apply("42"));

        // TYPE 2: Instance method on object  (instance::method)
        String prefix = "Hello, ";
        Function<String, String> greeter = prefix::concat;
        System.out.println("Instance ref: " + greeter.apply("World"));

        // TYPE 3: Instance method on type  (ClassName::instanceMethod)
        Function<String, String> upper = String::toUpperCase;
        System.out.println("Type ref: " + upper.apply("java"));

        // TYPE 4: Constructor reference  (ClassName::new)
        Supplier<ArrayList<String>> constructor = ArrayList::new;
        System.out.println("Constructor ref: " + constructor.get());

        System.out.println("\n=== EFFECTIVELY FINAL ===");

        // ── Lambdas capture variables that are effectively final ────
        //
        // EFFECTIVELY FINAL = assigned once, never modified
        //
        String tag = "[INFO]";  // never reassigned → effectively final
        Consumer<String> taggedPrint = msg -> System.out.println(tag + " " + msg);
        taggedPrint.accept("Lambda captures 'tag' from enclosing scope");

        // This would NOT compile:
        // tag = "[ERROR]";  // reassignment makes it NOT effectively final
        // → "local variables referenced from lambda must be effectively final"

        System.out.println("\n=== CUSTOM FUNCTIONAL INTERFACE ===");

        // ── Define your own functional interface ────────────────────
        //
        // @FunctionalInterface ensures exactly ONE abstract method
        //
        @FunctionalInterface
        interface MathOperation {
            double execute(double a, double b);
        }

        MathOperation add = (a, b) -> a + b;
        MathOperation multiply = (a, b) -> a * b;
        MathOperation power = Math::pow;  // method reference!

        System.out.println("add(3, 4):      " + add.execute(3, 4));
        System.out.println("multiply(3, 4): " + multiply.execute(3, 4));
        System.out.println("power(2, 10):   " + power.execute(2, 10));
    }
}
