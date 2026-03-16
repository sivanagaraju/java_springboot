/**
 * ====================================================================
 * FILE    : ExceptionHierarchyDemo.java
 * MODULE  : 05 — Exception Handling
 * PURPOSE : Demonstrate the full exception hierarchy and handling flow
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: try: ... except ValueError as e: ...
 *   Java:   try { ... } catch (IllegalArgumentException e) { ... }
 *   Python: ALL exceptions are unchecked
 *   Java:   Checked must be caught or declared with throws
 *
 * EXCEPTION HIERARCHY (key types):
 *
 *                      ┌──────────┐
 *                      │Throwable │
 *                      └────┬─────┘
 *               ┌───────────┴───────────┐
 *          ┌────┴────┐            ┌─────┴─────┐
 *          │  Error  │            │ Exception  │
 *          └────┬────┘            └─────┬─────┘
 *               │                       │
 *     ┌─────────┴──────┐     ┌──────────┴──────────┐
 *     │                │     │                      │
 *  OutOfMemory    StackOver  │              ┌───────┴────────┐
 *  Error          flowError  │              │RuntimeException│
 *                            │              └───────┬────────┘
 *                       Checked                     │
 *                  (IOException,           Unchecked (like Python)
 *                   SQLException)      (NullPointerException,
 *                                       IllegalArgumentException)
 *
 * CATCH ORDER RULE:
 *
 *   ┌──────────────────────────────────────────────────┐
 *   │  catch blocks are checked TOP to BOTTOM.          │
 *   │  Put MOST SPECIFIC exception first.               │
 *   │                                                    │
 *   │  ✅ catch (FileNotFoundException e)  ← specific   │
 *   │     catch (IOException e)            ← broader    │
 *   │     catch (Exception e)              ← broadest   │
 *   │                                                    │
 *   │  ❌ catch (Exception e)              ← catches ALL │
 *   │     catch (IOException e)            ← UNREACHABLE│
 *   └──────────────────────────────────────────────────┘
 *
 * ====================================================================
 */
public class ExceptionHierarchyDemo {

    public static void main(String[] args) {

        System.out.println("=== UNCHECKED EXCEPTIONS (RuntimeException) ===");

        // ── ArithmeticException (division by zero) ──────────────────
        //
        // FLOW:
        //   10 / 0 → JVM throws ArithmeticException
        //         → catch (ArithmeticException e) matches
        //         → finally ALWAYS runs
        //
        try {
            int result = 10 / 0;
            System.out.println("This line never executes");
        } catch (ArithmeticException e) {
            System.out.println("Caught ArithmeticException: " + e.getMessage());
        } finally {
            System.out.println("finally: always runs (cleanup here)");
        }

        System.out.println();

        // ── NullPointerException ────────────────────────────────────
        //
        // FLOW:
        //   str = null → str.length() → JVM throws NullPointerException
        //
        //   Stack trace reading:
        //   java.lang.NullPointerException
        //       at ExceptionHierarchyDemo.main(ExceptionHierarchyDemo.java:XX)
        //       ^^^^^^^^^^^^^^^^^^^^^^^^ ^^^^ ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        //       class name               method    file:line
        //
        try {
            String str = null;
            str.length();  // NPE!
        } catch (NullPointerException e) {
            System.out.println("Caught NullPointerException: " + e.getMessage());
            // Java 14+ gives helpful NPE messages:
            // "Cannot invoke String.length() because str is null"
        }

        System.out.println();

        // ── ArrayIndexOutOfBoundsException ──────────────────────────
        //
        // DIAGRAM:
        //   int[3] → valid indices: [0] [1] [2]
        //   accessing [5] → OUT OF BOUNDS!
        //
        try {
            int[] arr = {1, 2, 3};
            System.out.println(arr[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught ArrayIndexOutOfBounds: " + e.getMessage());
        }

        System.out.println("\n=== CATCH ORDER MATTERS ===");

        // ── Specific → Broad ordering ───────────────────────────────
        //
        // CHECK ORDER:
        //   Step 1: Is it NumberFormatException? → YES → execute this catch
        //   Step 2: Is it IllegalArgumentException? → not reached
        //   Step 3: Is it Exception? → not reached
        //
        try {
            int num = Integer.parseInt("not_a_number");
        } catch (NumberFormatException e) {
            // NumberFormatException extends IllegalArgumentException
            System.out.println("Caught NumberFormatException (most specific): " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Caught IllegalArgumentException (broader)");
        } catch (Exception e) {
            System.out.println("Caught Exception (broadest)");
        }

        System.out.println("\n=== MULTI-CATCH (Java 7+) ===");

        // ── Catch multiple exception types with same handler ────────
        //
        // FLOW: if either type is thrown, same handler runs.
        // The variable 'e' is effectively final in multi-catch.
        //
        try {
            String input = "abc";
            int parsed = Integer.parseInt(input);
        } catch (NumberFormatException | ArithmeticException e) {
            System.out.println("Multi-catch: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        System.out.println("\n=== FINALLY ALWAYS RUNS ===");

        // ── finally runs even with return ───────────────────────────
        //
        // EXECUTION ORDER:
        //   1. try block → prints "try"
        //   2. return reached BUT → finally must run first
        //   3. finally block → prints "finally"
        //   4. NOW the return executes
        //
        System.out.println("testFinally() returns: " + testFinally());

        System.out.println("\n=== STACK TRACE ANATOMY ===");

        // ── Reading a stack trace ───────────────────────────────────
        //
        // STACK TRACE FORMAT:
        //   ExceptionType: message
        //       at package.ClassName.methodName(FileName.java:lineNumber)
        //       at package.ClassName.methodName(FileName.java:lineNumber)
        //       ...
        //
        //   Read BOTTOM-UP: bottom = entry point, top = where error occurred
        //
        try {
            methodA();
        } catch (RuntimeException e) {
            System.out.println("Exception: " + e.getMessage());
            System.out.println("Stack trace (read bottom-up):");
            for (StackTraceElement frame : e.getStackTrace()) {
                System.out.println("  at " + frame);
            }
        }
    }

    // ── Helper: demonstrates finally with return ────────────────────
    static String testFinally() {
        try {
            return "from try";
        } finally {
            System.out.println("finally: runs BEFORE return!");
            // NEVER put return here — it overrides try's return!
        }
    }

    // ── Helpers: create a multi-level stack trace ────────────────────
    static void methodA() { methodB(); }
    static void methodB() { methodC(); }
    static void methodC() {
        throw new RuntimeException("Error in methodC — trace shows full call chain");
    }
}
