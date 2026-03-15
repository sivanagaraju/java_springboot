/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ControlFlowDemo.java                                   ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="ControlFlowDemo"
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates loops, conditions, and strict bool║
 * ║  WHY IT EXISTS  : Syntactical muscle memory for {} and () flow.  ║
 * ║  PYTHON COMPARE : Python 3.10 match vs Java 14 switch expression ║
 * ║  USE CASES      : Branching and iteration structures             ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                   ║
 * ║    [Switch Expr] --> MONDAY ->  "Weekday"                        ║
 * ║                  --> SUNDAY ->  "Weekend"                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run --args="ControlFlowDemo"
 * ║  EXPECTED OUTPUT: Examples of iteration and selection            ║
 * ║  RELATED FILES  : HowJavaWorks.java                              ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics;

import java.util.List;

/**
 * Examples of selection and iteration structures in Java.
 *
 * <p><b>Layer responsibility:</b> Algorithmic branching and logic.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   for x in range(5):
 *       print(x)
 * </pre>
 */
public class ControlFlowDemo {

    public static void main(String[] args) {

        // 1. STRICT BOOLS (NO TRUTHINESS)
        List<String> items = List.of();
        
        // This fails to compile: if (items)
        // Must be explicitly checked:
        if (!items.isEmpty()) {
            System.out.println("We have items!");
        } else {
            System.out.println("Empty inventory.");
        }

        // 2. FOR LOOPS
        System.out.println("\n--- Traditional For Loop ---");
        // Structure: init; condition; step
        for (int i = 0; i < 3; i++) {
            System.out.println("Index: " + i);
        }

        System.out.println("\n--- Enhanced For-Each Loop ---");
        List<String> names = List.of("Alice", "Bob", "Charlie");
        for (String name : names) { // Equivalent to: for name in names:
            System.out.println("Processing: " + name);
        }

        // 3. SWITCH EXPRESSIONS (Java 14+)
        // Replaces bulky old switch logic and prevents fall-through bugs.
        String day = "SATURDAY";
        String type = switch(day) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "Weekday";
            case "SATURDAY", "SUNDAY" -> "Weekend";
            default -> {
                // Multi-line block in a switch needs 'yield' to return the value
                System.out.println("Unknown day provided.");
                yield "Invalid";
            }
        };

        System.out.println("\n" + day + " is a " + type);
    }
}
