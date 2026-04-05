import java.util.*;
import java.util.stream.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 1: Shape Calculator with Sealed + Records                 ║
 * ║  Type-safe geometric shapes with exhaustive pattern matching        ║
 * ║                                                                      ║
 * ║  HIERARCHY:                                                          ║
 * ║                                                                      ║
 * ║   sealed interface Shape                                            ║
 * ║     ├── record Circle(double radius)                                ║
 * ║     ├── record Rectangle(double width, double height)               ║
 * ║     └── record Triangle(double a, double b, double c)               ║
 * ║                                                                      ║
 * ║  EXHAUSTIVE SWITCH: no default needed — compiler proves coverage    ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */

/**
 * Demonstrates sealed interfaces, records, and pattern matching switch.
 * Key insight: exhaustive switch on sealed types catches missing cases at
 * compile time — adding a new Shape subtype causes every uncovered switch
 * to fail compilation immediately.
 */
public class Sol01_ShapeCalculator {

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 1: Sealed type hierarchy — controlled polymorphism
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sealed interface — only Circle, Rectangle, Triangle can implement Shape.
     * This allows exhaustive switch expressions without a {@code default} branch.
     */
    sealed interface Shape permits Circle, Rectangle, Triangle {}

    /**
     * Circle defined by radius. Record auto-generates: constructor, radius(),
     * equals(), hashCode(), toString().
     */
    record Circle(double radius) implements Shape {
        // Compact constructor for validation
        public Circle {
            // WHY: fail fast at construction — prevents invalid circles in the system
            if (radius <= 0) throw new IllegalArgumentException("radius must be > 0, was: " + radius);
        }
    }

    /**
     * Rectangle defined by width and height.
     */
    record Rectangle(double width, double height) implements Shape {
        public Rectangle {
            if (width <= 0 || height <= 0)
                throw new IllegalArgumentException("dimensions must be > 0");
        }
    }

    /**
     * Triangle defined by three side lengths. Validates triangle inequality.
     */
    record Triangle(double a, double b, double c) implements Shape {
        public Triangle {
            if (a <= 0 || b <= 0 || c <= 0)
                throw new IllegalArgumentException("sides must be > 0");
            // WHY triangle inequality: a degenerate triangle (3+4>7.001 but 3+4<8)
            // has no area and is mathematically invalid
            if (a + b <= c || a + c <= b || b + c <= a)
                throw new IllegalArgumentException(
                    "Invalid triangle: sides " + a + ", " + b + ", " + c
                );
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 2: Shape operations — exhaustive switch, no default
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calculates the area of any Shape using exhaustive pattern matching.
     * No {@code default} branch — if a new Shape is added to the sealed
     * interface without updating this switch, the code WILL NOT COMPILE.
     *
     * @param shape the shape to measure
     * @return area in square units
     */
    static double area(Shape shape) {
        return switch (shape) {
            // WHY no default: sealed + exhaustive switch = compile-time safety
            case Circle c    -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t  -> {
                // Heron's formula: area = √(s(s-a)(s-b)(s-c)) where s = (a+b+c)/2
                double s = (t.a() + t.b() + t.c()) / 2;
                // WHY yield: multi-statement case arm requires explicit yield
                yield Math.sqrt(s * (s - t.a()) * (s - t.b()) * (s - t.c()));
            }
        };
    }

    /**
     * Calculates the perimeter of any Shape.
     *
     * @param shape the shape to measure
     * @return perimeter in linear units
     */
    static double perimeter(Shape shape) {
        return switch (shape) {
            case Circle c    -> 2 * Math.PI * c.radius();
            case Rectangle r -> 2 * (r.width() + r.height());
            case Triangle t  -> t.a() + t.b() + t.c();
        };
    }

    /**
     * Returns a human-readable description of the shape using a text block.
     *
     * @param shape the shape to describe
     * @return multi-line description string
     */
    static String describe(Shape shape) {
        return switch (shape) {
            case Circle c -> """
                Circle
                  radius    = %.2f
                  area      = %.4f
                  perimeter = %.4f
                """.formatted(c.radius(), area(c), perimeter(c));
            case Rectangle r -> """
                Rectangle
                  width×height = %.2f × %.2f
                  area         = %.4f
                  perimeter    = %.4f
                """.formatted(r.width(), r.height(), area(r), perimeter(r));
            case Triangle t -> """
                Triangle
                  sides     = %.2f, %.2f, %.2f
                  area      = %.4f
                  perimeter = %.4f
                """.formatted(t.a(), t.b(), t.c(), area(t), perimeter(t));
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 3: Main — demonstrates shapes, sorting, and text block output
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        List<Shape> shapes = List.of(
            new Circle(5),
            new Rectangle(4, 6),
            new Triangle(3, 4, 5)
        );

        // Print each shape's metrics
        for (Shape s : shapes) {
            System.out.printf("%-25s area=%7.2f, perimeter=%7.2f%n",
                s.toString(), area(s), perimeter(s));
        }

        // Sort by area ascending
        List<Shape> sorted = shapes.stream()
            // WHY comparingDouble: avoids boxing overhead of Double.compare
            .sorted(Comparator.comparingDouble(Sol01_ShapeCalculator::area))
            .collect(Collectors.toList());

        System.out.print("\nSorted by area: ");
        sorted.forEach(s -> System.out.printf("%s(%.1f) ",
            s.getClass().getSimpleName(), area(s)));
        System.out.println();

        // Show detailed description for one shape
        System.out.println("\n" + describe(new Circle(5)));

        // Pattern matching with guard condition
        for (Shape s : shapes) {
            String label = switch (s) {
                case Circle c when c.radius() > 3 -> "Large circle";
                case Circle c                     -> "Small circle";
                case Rectangle r when r.width() == r.height() -> "Square";
                case Rectangle r                  -> "Rectangle";
                case Triangle t when t.a() == t.b() && t.b() == t.c() -> "Equilateral";
                case Triangle t                   -> "Triangle";
            };
            System.out.println(s.getClass().getSimpleName() + " → " + label);
        }
    }
}

/* ─────────────────────────────────────────────────────────────────────────────
   COMMON MISTAKES

   MISTAKE 1: Adding 'default' to sealed switch
     default -> throw new IllegalStateException(...);
     This silences the compiler — adding a new Shape WON'T cause compile error.
     Remove default and let the compiler enforce exhaustiveness.

   MISTAKE 2: Using instanceof instead of pattern switch
     if (s instanceof Circle) { Circle c = (Circle) s; ... }
     Pattern matching switch is cleaner and compiler-checked.
     Use switch (s) { case Circle c -> ... }

   MISTAKE 3: Validation in wrong place
     double area = Math.PI * radius * radius;  if (area < 0) throw ...
     Validate at construction time (compact constructor), not in area().
     Once a Circle is constructed, you know radius > 0.

   MISTAKE 4: record accessor syntax confusion
     c.getRadius()  ← Lombok style — WRONG for records
     c.radius()     ← correct record accessor (no 'get' prefix)

   MISTAKE 5: Triangle Heron's formula without computing 's' first
     return Math.sqrt(t.a()*t.b()*t.c());  ← incorrect formula
     Correct: s = (a+b+c)/2; area = sqrt(s*(s-a)*(s-b)*(s-c))
   ───────────────────────────────────────────────────────────────────────────── */
