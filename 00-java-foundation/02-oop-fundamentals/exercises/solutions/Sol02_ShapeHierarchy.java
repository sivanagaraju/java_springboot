/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_ShapeHierarchy.java                              ║
 * ║  MODULE : 00-java-foundation / 02-oop-fundamentals               ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — abstract class + polymorphism       ║
 * ║  DEMONSTRATES   : abstract class, method overriding, dynamic     ║
 * ║                   dispatch, List<Shape> polymorphic collection   ║
 * ║  PYTHON COMPARE : Python ABC + @abstractmethod; Java abstract    ║
 * ║                                                                  ║
 * ║  HIERARCHY:                                                      ║
 * ║   Shape (abstract)                                               ║
 * ║    ├── Circle      → area = π * r²                               ║
 * ║    └── Rectangle   → area = w * h                                ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for geometric shapes.
 *
 * <p>WHY abstract class (not interface): shapes share state ({@code color})
 * and behavior ({@code getColor()}). An interface could declare {@code getColor()}
 * but cannot hold the {@code color} field. Abstract classes are correct when
 * subclasses share both fields and partial implementations.
 *
 * <p>Python equivalent:
 * <pre>{@code
 *   from abc import ABC, abstractmethod
 *   class Shape(ABC):
 *       def __init__(self, color): self.color = color
 *       @abstractmethod
 *       def calculate_area(self): pass
 * }</pre>
 */
abstract class Shape {

    // WHY protected (not private): subclasses need direct access to color
    // without going through a getter in their constructors. Private would
    // force super.getColor() in every subclass — verbose for no safety benefit.
    protected final String color;

    /**
     * Initializes the shape's color.
     *
     * @param color the display color of the shape
     */
    public Shape(String color) {
        this.color = color;
    }

    /** Returns the color of this shape. */
    public String getColor() {
        return color;
    }

    /**
     * Calculates the area of this shape.
     *
     * <p>WHY abstract: every concrete shape has a different area formula.
     * There is no meaningful default — forcing subclasses to implement it
     * is exactly what abstract methods exist for. If we provided a default
     * (e.g., return 0), subclasses could forget to override and silently
     * return wrong answers.
     *
     * @return area in square units
     */
    public abstract double calculateArea();
}

/**
 * Circle — area = π * r².
 */
class Circle extends Shape {

    // WHY private: radius is Circle-specific implementation detail.
    // Shape doesn't need to know about it.
    private final double radius;

    /**
     * Creates a circle with the given color and radius.
     *
     * @param color  display color
     * @param radius radius; must be positive
     */
    public Circle(String color, double radius) {
        super(color); // WHY super(): must initialize parent's color field
        this.radius = radius;
    }

    /**
     * Returns π * radius².
     *
     * @return area of this circle
     */
    @Override
    public double calculateArea() {
        // WHY Math.PI: predefined constant with maximum double precision.
        // Never use 3.14 or 3.14159 — those introduce unnecessary error.
        return Math.PI * radius * radius;
    }
}

/**
 * Rectangle — area = width * height.
 */
class Rectangle extends Shape {

    private final double width;
    private final double height;

    /**
     * Creates a rectangle with the given color and dimensions.
     *
     * @param color  display color
     * @param width  horizontal dimension; must be positive
     * @param height vertical dimension; must be positive
     */
    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    /**
     * Returns width * height.
     *
     * @return area of this rectangle
     */
    @Override
    public double calculateArea() {
        return width * height;
    }
}

/**
 * Test harness demonstrating polymorphism via {@code List<Shape>}.
 */
public class Sol02_ShapeHierarchy {

    /**
     * Runs the shape hierarchy demonstration.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("--- Shape Hierarchy Solution ---\n");

        // WHY List<Shape>: we store different concrete types under one interface.
        // This is runtime polymorphism — the JVM decides at runtime which
        // calculateArea() to call based on the actual object type, not the
        // declared reference type. This is "dynamic dispatch".
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Circle("Red", 5.0));
        shapes.add(new Rectangle("Blue", 4.0, 6.0));
        shapes.add(new Circle("Green", 2.0));

        double totalArea = 0;
        for (Shape shape : shapes) {
            // WHY shape.calculateArea() dispatches correctly:
            // Even though 'shape' is declared as Shape, the JVM calls the
            // overridden method on the actual Circle or Rectangle instance.
            double area = shape.calculateArea();
            System.out.printf("  %s %s → area = %.2f%n",
                    shape.getColor(), shape.getClass().getSimpleName(), area);
            totalArea += area;
        }

        System.out.printf("%nTotal combined area: %.2f%n", totalArea);
        // Expected: π*25 + 24 + π*4 ≈ 78.54 + 24 + 12.57 ≈ 115.11
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using an interface instead of abstract class
 *   WRONG: interface Shape { String getColor(); double calculateArea(); }
 *   PROBLEM: Interfaces cannot hold the 'color' field. Each subclass
 *            would need its own 'color' field and getter — repeated code.
 *   RIGHT: Abstract class when subclasses share STATE (fields).
 *          Interface when they only share BEHAVIOR (method signatures).
 *
 * MISTAKE 2: Making calculateArea() non-abstract with a dummy return
 *   WRONG: public double calculateArea() { return 0; }
 *   PROBLEM: Subclasses can compile without overriding it, silently
 *            returning 0 for all areas — a silent bug.
 *   RIGHT: abstract — forces override at compile time.
 *
 * MISTAKE 3: Forgetting super() in subclass constructor
 *   WRONG: public Circle(String color, double radius) { this.radius = radius; }
 *   PROBLEM: Java inserts implicit super() which requires a no-arg constructor.
 *            Shape has no no-arg constructor → compile error.
 *   RIGHT: Always call super(args) as the FIRST statement.
 *
 * MISTAKE 4: Accessing parent field directly across package without protected
 *   If 'color' were private in Shape, Circle and Rectangle couldn't access it.
 *   Use protected for fields subclasses legitimately need.
 *
 * MISTAKE 5: instanceof checks instead of polymorphism
 *   WRONG: if (shape instanceof Circle) { ((Circle)shape).getRadius(); }
 *   RIGHT: add getArea() to the abstract class and let polymorphism dispatch.
 *          instanceof + cast is an anti-pattern in OOP design.
 * ═══════════════════════════════════════════════════════════════════ */
