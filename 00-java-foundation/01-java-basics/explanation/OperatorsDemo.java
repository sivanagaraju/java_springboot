/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : OperatorsDemo.java                                     ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="OperatorsDemo"
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates Java operators and the == trap    ║
 * ║  WHY IT EXISTS  : Logical symbols and Object comparison differ.  ║
 * ║  PYTHON COMPARE : Python = 'and', 'or', 'not', '==', 'is'        ║
 * ║                   Java   = '&&', '||', '!', '.equals()', '=='    ║
 * ║  USE CASES      : Logical conditions, math, value comparison     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                   ║
 * ║    "Alice"   [ Heap Mem 0x01 ]   <--- a                          ║
 * ║    "Alice"   [ Heap Mem 0x02 ]   <--- b                          ║
 * ║    (a == b) is false!  a.equals(b) is true!                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run --args="OperatorsDemo"
 * ║  EXPECTED OUTPUT: Outputs showing operator results               ║
 * ║  RELATED FILES  : ControlFlowDemo.java                           ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics;

/**
 * Examples of arithmetic, relational, logical, and ternary operators.
 *
 * <p><b>Layer responsibility:</b> Basic language operations.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   x = 5
 *   y = x + 1
 *   is_valid = True and not False
 * </pre>
 */
public class OperatorsDemo {

    public static void main(String[] args) {

        // 1. ARITHMETIC & INTEGER TRUNCATION
        int a = 5;
        int b = 2;
        System.out.println("Integer division 5 / 2 = " + (a / b)); // Prints 2!
        System.out.println("Float division 5.0 / 2 = " + (5.0 / b)); // Prints 2.5
        
        // Post/Pre-increment (Python doesn't have these)
        int x = 10;
        int y = x++; // y gets 10, THEN x becomes 11
        System.out.println("x is " + x + ", y is " + y);

        // 2. LOGICAL OPERATORS
        // Python: and, or, not
        // Java: &&, ||, !
        boolean isWeekend = true;
        boolean isHoliday = false;
        if (isWeekend || isHoliday) {
            System.out.println("It's a day off!");
        }

        // 3. TERNARY OPERATOR
        // Python: result = "Even" if num % 2 == 0 else "Odd"
        // Java:   result = (num % 2 == 0) ? "Even" : "Odd";
        int age = 15;
        String status = (age >= 18) ? "Adult" : "Minor";
        System.out.println("Status: " + status);

        // 4. THE EQUALITY TRAP (Crucial for Python developers!)
        // Python   '==' compares VALUES. 'is' compares IDENTITY.
        // Java     '==' compares IDENTITY. '.equals()' compares VALUES.
        String str1 = new String("Alice");
        String str2 = new String("Alice");

        System.out.println("str1 == str2: " + (str1 == str2)); // false! Different memory addresses
        System.out.println("str1.equals(str2): " + str1.equals(str2)); // true! Same value
    }
}
