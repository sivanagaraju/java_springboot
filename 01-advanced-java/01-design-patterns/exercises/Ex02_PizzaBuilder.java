/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 2: Pizza Builder                                  ║
 * ║  Build an immutable Pizza using the Builder pattern         ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * TASK: Create an immutable Pizza class using Builder pattern.
 *
 * Requirements:
 * 1. Pizza has: size (S/M/L), crust (thin/thick), toppings List, extra cheese (bool)
 * 2. Builder validates: size is required, at least 1 topping required
 * 3. Pizza is IMMUTABLE — all fields final, unmodifiable toppings list
 * 4. Use fluent API — each method returns Builder for chaining
 *
 * BONUS: Add a preset() method: .preset("margherita") sets toppings + crust
 *
 * Expected Output:
 *   Pizza[L, thick crust, [Pepperoni, Mushrooms, Olives], extra cheese]
 *   Pizza[M, thin crust, [Mozzarella, Tomato, Basil], no extra cheese]
 *
 * HINTS:
 * - Private constructor that takes Builder
 * - Collections.unmodifiableList() for immutable toppings
 * - build() throws IllegalStateException if validation fails
 */
public class Ex02_PizzaBuilder {

    // TODO: Create immutable Pizza class with private constructor

    // TODO: Create static inner Builder class with fluent setters

    // TODO: Add validation in build()

    // TODO: Add preset() method for common pizzas

    public static void main(String[] args) {
        // TODO: Build a custom pizza
        // TODO: Build a preset margherita
        // TODO: Test validation (no size → exception)
        System.out.println("Exercise 2: Implement the Pizza Builder!");
    }
}
