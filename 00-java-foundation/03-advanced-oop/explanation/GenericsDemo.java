package explanation;

import java.util.ArrayList;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : GenericsDemo.java                                      ║
 * ║  MODULE : 00-java-foundation / 03-advanced-oop                   ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="GenericsDemo"║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates Java Generics (Classes & Methods) ║
 * ║  WHY IT EXISTS  : Type safety at compile time, reducing casts.   ║
 * ║  PYTHON COMPARE : Python type hints (List[str], TypeVar('T'))    ║
 * ║                   are ignored at runtime. Java also erases types ║
 * ║                   at runtime, but enforces them strictly at      ║
 * ║                   compile time!                                  ║
 * ║  USE CASES      : Collections, DAOs, Wrappers                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                    ║
 * ║    Box<T>                   Box<String> (Compile Time)            ║
 * ║    ┌─────────────┐          ┌──────────────────────┐             ║
 * ║    │ private T t │  ======> │ private String t     │             ║
 * ║    │ set(T t)    │          │ set(String t)        │             ║
 * ║    │ T get()     │          │ String get()         │             ║
 * ║    └─────────────┘          └──────────────────────┘             ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run --args="GenericsDemo" ║
 * ║  EXPECTED OUTPUT: Outputs demonstrating type safety              ║
 * ║  RELATED FILES  : 03-generics.md                                 ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class GenericsDemo {

    // 1. Generic Class
    // The `<T>` stands for "Type" and acts as a placeholder parameter.
    public static class Box<T> {
        private T content;

        public void set(T content) {
            this.content = content;
        }

        public T get() {
            return content;
        }
    }

    // 2. Generic Method
    // The `<E>` is declared BEFORE the return type.
    public static <E> void printArray(E[] inputArray) {
        System.out.print("[");
        for (int i = 0; i < inputArray.length; i++) {
            System.out.print(inputArray[i]);
            if (i < inputArray.length - 1) System.out.print(", ");
        }
        System.out.println("]");
    }

    // 3. Bounded Type Parameter
    // The type MUST extend Number (Integer, Double, Float, etc.)
    // `extends` here means "is a subclass of or implements the interface"
    public static <N extends Number> double calculateDoubleSum(N n1, N n2) {
        return n1.doubleValue() + n2.doubleValue();
    }

    // 4. Wildcards (?)
    // A List of "unknowns that extend Number". We cannot add to this list (except null)!
    public static void printNumbersAndSubclasses(List<? extends Number> list) {
        System.out.println("Printing wildcard list of numbers: " + list);
        // list.add(10); // COMPILE ERROR! Because the list might be a List<Double> in disguise.
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Generic Classes (Compile-time Type Safety) ---");
        // We configure the Box to hold exactly Strings
        Box<String> stringBox = new Box<>();
        stringBox.set("Secret Code 007");
        String val = stringBox.get(); // Notice NO cast needed!
        System.out.println("Box contents: " + val);

        // This would fail to compile:
        // stringBox.set(100); 
        
        System.out.println("\n--- 2. Generic Methods ---");
        Integer[] intArray = { 1, 2, 3 };
        String[] stringArray = { "Hello", "World" };
        
        System.out.print("Integer Array: ");
        printArray(intArray); // The compiler infers <Integer> 
        
        System.out.print("String Array: ");
        printArray(stringArray); // The compiler infers <String>

        System.out.println("\n--- 3. Bounded Type Parameters ---");
        // Compiles smoothly because Integer and Double extend Number
        System.out.println("Sum of 5 (Integer) and 3.24 (Double): " + calculateDoubleSum(5, 3.24));
        // calculateDoubleSum("A", "B"); // COMPILE ERROR! Strings aren't Numbers.

        System.out.println("\n--- 4. Wildcards ---");
        List<Integer> myInts = new ArrayList<>(List.of(10, 20, 30));
        List<Double> myDoubles = new ArrayList<>(List.of(1.5, 2.5, 3.5));
        
        printNumbersAndSubclasses(myInts);
        printNumbersAndSubclasses(myDoubles);
    }
}
