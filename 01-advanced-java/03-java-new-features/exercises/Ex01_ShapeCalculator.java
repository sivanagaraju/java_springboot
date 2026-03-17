import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 1: Shape Calculator with Sealed + Records         ║
 * ║  Build an algebraic data type for geometric shapes         ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * TASK: Create a type-safe shape system using sealed + records.
 *
 * Requirements:
 * 1. sealed interface Shape permits: Circle, Rectangle, Triangle, Polygon
 * 2. Each shape is a record with appropriate fields
 * 3. Implement area() using exhaustive switch (no default!)
 * 4. Implement perimeter() using exhaustive switch
 * 5. Implement describe() returning a text block description
 * 6. Sort shapes by area using Comparable
 *
 * BONUS: Add a Polygon record that takes List<Point> and
 *        calculates area using the Shoelace formula.
 *
 * Expected Output:
 *   Circle(r=5):    area=78.54, perimeter=31.42
 *   Rectangle(4×6): area=24.00, perimeter=20.00
 *   Triangle(3,4,5): area=6.00, perimeter=12.00
 *   Sorted: [Triangle, Rectangle, Circle]
 *
 * HINTS:
 * - record Circle(double radius) implements Shape {}
 * - Triangle area: Heron's formula √(s(s-a)(s-b)(s-c))
 * - Use switch expression for all calculations
 */
public class Ex01_ShapeCalculator {
    // TODO: Define sealed interface Shape
    // TODO: Define record types for Circle, Rectangle, Triangle
    // TODO: Implement area(), perimeter(), describe()
    // TODO: Sort by area

    public static void main(String[] args) {
        System.out.println("Exercise 1: Build the Shape Calculator!");
    }
}
