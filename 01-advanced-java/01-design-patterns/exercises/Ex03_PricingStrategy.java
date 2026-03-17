/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 3: Pricing Strategy                               ║
 * ║  Implement dynamic pricing using the Strategy pattern       ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * TASK: Create a pricing calculator with swappable strategies.
 *
 * Requirements:
 * 1. PricingStrategy interface: double apply(double basePrice, int quantity)
 * 2. Implement 3 strategies:
 *    - RegularPricing: price * qty
 *    - BulkPricing: 10% off if qty >= 10, 20% off if qty >= 50
 *    - SeasonalPricing: takes a discount percentage constructor param
 * 3. PricingCalculator context: setStrategy() + calculate()
 * 4. Use lambda for a one-off "flash sale" strategy
 *
 * BONUS: Create a CompositePricing that applies multiple strategies
 *        (e.g., bulk discount + seasonal discount).
 *
 * Expected Output:
 *   Regular (5 × $100): $500.00
 *   Bulk    (15 × $100): $1,350.00  (10% off)
 *   Seasonal(5 × $100, 25% off): $375.00
 *   Flash   (5 × $100): $300.00
 *
 * HINTS:
 * - @FunctionalInterface allows lambda usage
 * - Strategy is identical to Comparator pattern
 * - Think about how Spring's AuthenticationProviders work
 */
public class Ex03_PricingStrategy {

    // TODO: Define PricingStrategy functional interface

    // TODO: Implement RegularPricing, BulkPricing, SeasonalPricing

    // TODO: Create PricingCalculator context

    // TODO: Implement CompositePricing (bonus)

    public static void main(String[] args) {
        // TODO: Use class-based strategies
        // TODO: Use lambda strategy for flash sale
        // TODO: Swap strategies at runtime
        System.out.println("Exercise 3: Implement the Pricing Strategy!");
    }
}
