/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_Calculator.java                                  ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — console calculator with Scanner     ║
 * ║  DEMONSTRATES   : Scanner I/O, switch expression, loops          ║
 * ║  PYTHON COMPARE : input() vs Scanner; match vs switch expression ║
 * ║                                                                  ║
 * ║  FLOW:                                                           ║
 * ║   Scanner.next() → parse double → switch expression → result    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.Scanner;

/**
 * Console calculator demonstrating Scanner input, switch expressions,
 * and proper resource management.
 *
 * <p>Python equivalent uses {@code input()} and {@code match/case}.
 * Key Java difference: Scanner must be closed to release the underlying
 * {@code System.in} stream. Use try-with-resources or explicit {@code close()}.
 */
public class Sol02_Calculator {

    /**
     * Runs the interactive calculator loop.
     *
     * <p>Accepts: {@code quit} to exit, any double for numbers,
     * one of {@code +}, {@code -}, {@code *}, {@code /} for operators.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        // WHY try-with-resources: Scanner wraps System.in and must be closed
        // to release the underlying OS file descriptor.
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Java Console Calculator ===");
            System.out.println("Type 'quit' to exit.\n");

            // WHY boolean flag: cleaner than break inside while(true)
            boolean running = true;
            while (running) {
                System.out.print("Enter first number (or 'quit'): ");
                String input = scanner.next();

                // WHY equalsIgnoreCase: user might type 'Quit' or 'QUIT'
                if (input.equalsIgnoreCase("quit")) {
                    running = false;
                    continue;
                }

                // WHY try-catch here (not outside loop): recover from bad input
                // and continue the loop instead of crashing the program
                double num1;
                try {
                    num1 = Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    System.out.println("  ✗ Invalid number: '" + input + "'. Try again.\n");
                    continue;
                }

                System.out.print("Enter operator (+, -, *, /): ");
                String op = scanner.next();

                System.out.print("Enter second number: ");
                double num2;
                try {
                    num2 = Double.parseDouble(scanner.next());
                } catch (NumberFormatException e) {
                    System.out.println("  ✗ Invalid second number. Try again.\n");
                    continue;
                }

                // WHY switch expression (Java 14+): eliminates fall-through bugs,
                // each arm is an expression not a statement — more like Python match.
                // yield is required inside {} blocks to return a value.
                double result = switch (op) {
                    case "+" -> num1 + num2;
                    case "-" -> num1 - num2;
                    case "*" -> num1 * num2;
                    case "/" -> {
                        // WHY check for zero: dividing by zero with doubles produces
                        // Infinity, not an exception — but we want a user-friendly error
                        if (num2 == 0.0) {
                            System.out.println("  ✗ Division by zero is undefined.");
                            yield Double.NaN;
                        }
                        yield num1 / num2;
                    }
                    default -> {
                        System.out.println("  ✗ Unknown operator: '" + op + "'");
                        yield Double.NaN;
                    }
                };

                // WHY isNaN check: NaN means we already printed an error above
                if (!Double.isNaN(result)) {
                    System.out.printf("  = %.6f%n%n", result);
                }
            }

            System.out.println("Calculator exited. Goodbye!");
        } // WHY: scanner auto-closed here via try-with-resources
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using scanner.nextDouble() directly
 *   WRONG: double num = scanner.nextDouble();
 *   PROBLEM: If user types "abc", InputMismatchException is thrown AND
 *            the invalid token remains in the buffer. Next call reads "abc" again.
 *   RIGHT: String input = scanner.next(); Double.parseDouble(input);
 *          This way you can handle bad input and move past it cleanly.
 *
 * MISTAKE 2: Not closing Scanner
 *   WRONG: Scanner scanner = new Scanner(System.in);
 *   RIGHT: try (Scanner scanner = new Scanner(System.in)) { ... }
 *          Scanner wraps System.in; forgetting to close leaks the OS fd.
 *
 * MISTAKE 3: Using if/else chain instead of switch expression
 *   Old way: if (op.equals("+")) result = ...; else if ...
 *   Java 14+: switch expression is cleaner, exhaustiveness checked by compiler,
 *   and eliminates accidental fall-through.
 *
 * MISTAKE 4: Comparing doubles with ==
 *   99.99 == 99.99  may be false due to floating-point representation.
 *   Use Math.abs(a - b) < 1e-9 for equality checks.
 *
 * MISTAKE 5: Scanner.next() vs Scanner.nextLine()
 *   next() reads one token (up to whitespace).
 *   nextLine() reads the whole line including spaces.
 *   Mixing them creates bugs: nextLine() after nextInt() reads the leftover newline.
 * ═══════════════════════════════════════════════════════════════════ */
