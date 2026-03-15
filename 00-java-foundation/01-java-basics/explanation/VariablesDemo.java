/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : VariablesDemo.java                                     ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="VariablesDemo"
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Showcase primitives, wrappers, and autoboxing  ║
 * ║  WHY IT EXISTS  : Type system differences are the #1 source of   ║
 * ║                   bugs for Python developers moving to Java.     ║
 * ║  PYTHON COMPARE : Python dynamically infers type. Java requires  ║
 * ║                   strict declarations and explicit bounds.       ║
 * ║  USE CASES      : Choosing memory-efficient data types           ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                   ║
 * ║     STACK                   HEAP                                 ║
 * ║   ---------               --------                               ║
 * ║   int x = 5               [     ]                                ║
 * ║   Integer y ──(ref)─────▶ [  5  ]                                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run --args="VariablesDemo"
 * ║  EXPECTED OUTPUT: Output of primitive bounds and NPE demo        ║
 * ║  RELATED FILES  : OperatorsDemo.java                             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics;

/**
 * Demonstrates Java's static typing, primitives, and object wrappers.
 *
 * <p><b>Layer responsibility:</b> Basic language types layer.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   age = 30           # dynamically typed int
 *   salary = 95000.50  # dynamically typed float
 *   is_active = True   # dynamically typed bool
 * </pre>
 */
public class VariablesDemo {

    public static void main(String[] args) {
        
        // 1. PRIMITIVE TYPES (Stored on Stack)
        // Integer types
        byte tinyNum = 127;           // 8-bit (-128 to 127)
        short smallNum = 32767;       // 16-bit
        int count = 2147483647;       // 32-bit (Default choice for integers)
        long bigNum = 9000000000000L; // 64-bit (Note 'L' suffix)
        
        // Floating point
        float price = 19.99f;         // 32-bit (Note 'f' suffix)
        double precisePrice = 19.99;  // 64-bit (Default choice for decimals)
        
        // Other primitives
        boolean isValid = true;       // 1-bit
        char letter = 'A';            // 16-bit Unicode character (Single quotes!)

        System.out.println("Primitives initialized successfully.");

        // 2. WRAPPER CLASSES (Stored on Heap)
        // Usually used when working with Collections (Lists, Maps) which require Objects
        Integer wrappedCount = 100;   // Autoboxing: primitive 100 -> Integer Object
        int rawCount = wrappedCount;  // Unboxing: Integer Object -> primitive

        System.out.println("Wrapped count: " + wrappedCount);

        // 3. THE AUTOBOXING TRAP
        // Primitives cannot be null. Objects can be.
        Integer nullWrapper = null;
        
        try {
            // This attempts implicit unboxing: nullWrapper.intValue()
            int trap = nullWrapper; 
        } catch (NullPointerException e) {
            System.out.println("Caught the Autoboxing Trap! Cannot unbox a null wrapper.");
        }
    }
}
