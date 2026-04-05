/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 3: Pricing Strategy                                       ║
 * ║  Swappable pricing algorithms using @FunctionalInterface + lambdas  ║
 * ║                                                                      ║
 * ║  ASCII FLOW:                                                         ║
 * ║                                                                      ║
 * ║   calculator.setStrategy(new BulkPricing())                         ║
 * ║        │                                                             ║
 * ║   calculator.calculate(100.0, 15)                                   ║
 * ║        │                                                             ║
 * ║        ▼                                                             ║
 * ║   strategy.apply(100.0, 15)  ← BulkPricing: 15 >= 10 → 10% off    ║
 * ║        │                                                             ║
 * ║        ▼                                                             ║
 * ║   100.0 * 15 * 0.90 = $1,350.00                                     ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */

import java.util.List;

/**
 * Demonstrates Strategy with @FunctionalInterface enabling lambda strategies.
 * Key insight: Spring's AuthenticationProvider uses the same pattern —
 * multiple implementations, context (AuthenticationManager) delegates to them.
 */
public class Sol03_PricingStrategy {

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 1: Strategy interface — @FunctionalInterface enables lambda usage
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * The Strategy interface. {@code @FunctionalInterface} is optional but
     * provides compile-time guarantee of single abstract method (SAM).
     * Enables lambda syntax: {@code (price, qty) -> price * qty * 0.5}
     */
    @FunctionalInterface
    interface PricingStrategy {
        /**
         * Calculates total price for the given unit price and quantity.
         *
         * @param basePrice price per unit
         * @param quantity  number of units
         * @return total price after applying this strategy's rules
         */
        double apply(double basePrice, int quantity);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 2: Concrete strategies — each encapsulates one pricing algorithm
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Regular pricing: simply price × quantity, no discount.
     */
    static class RegularPricing implements PricingStrategy {
        @Override
        public double apply(double basePrice, int quantity) {
            return basePrice * quantity;
        }
    }

    /**
     * Bulk pricing: tiered discounts based on quantity thresholds.
     * 10% off for qty ≥ 10, 20% off for qty ≥ 50.
     */
    static class BulkPricing implements PricingStrategy {
        @Override
        public double apply(double basePrice, int quantity) {
            // WHY tiered: most pricing tables are threshold-based — this mirrors real pricing sheets
            double discount = quantity >= 50 ? 0.20
                            : quantity >= 10 ? 0.10
                            : 0.0;
            return basePrice * quantity * (1 - discount);
        }
    }

    /**
     * Seasonal pricing: configurable discount percentage.
     * Constructor injection makes the discount a first-class parameter.
     */
    static class SeasonalPricing implements PricingStrategy {

        private final double discountPercent;  // WHY final: strategy is immutable after construction

        /**
         * @param discountPercent discount fraction (0.0–1.0), e.g., 0.25 for 25% off
         */
        public SeasonalPricing(double discountPercent) {
            // WHY validate: prevent negative "discounts" that inflate price
            if (discountPercent < 0 || discountPercent > 1) {
                throw new IllegalArgumentException(
                    "discountPercent must be 0.0–1.0, was: " + discountPercent
                );
            }
            this.discountPercent = discountPercent;
        }

        @Override
        public double apply(double basePrice, int quantity) {
            return basePrice * quantity * (1 - discountPercent);
        }
    }

    /**
     * Composite pricing: applies multiple strategies sequentially.
     * Each strategy's output becomes the next strategy's base price.
     * Bonus: demonstrates how strategies can be combined.
     */
    static class CompositePricing implements PricingStrategy {

        private final List<PricingStrategy> strategies;

        /**
         * @param strategies ordered list of strategies to apply in sequence
         */
        public CompositePricing(List<PricingStrategy> strategies) {
            this.strategies = List.copyOf(strategies);  // WHY copyOf: defensive copy
        }

        @Override
        public double apply(double basePrice, int quantity) {
            // WHY base=1.0: accumulate discount multiplier, then apply to basePrice*qty once
            double multiplier = 1.0;
            for (PricingStrategy strategy : strategies) {
                // Extract the per-unit multiplier from each strategy
                double fullPrice = basePrice * quantity;
                double discountedPrice = strategy.apply(basePrice, quantity);
                multiplier *= (fullPrice > 0 ? discountedPrice / fullPrice : 1.0);
            }
            return basePrice * quantity * multiplier;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 3: Context — calculator delegates to the injected strategy
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * PricingCalculator — the Context in the Strategy pattern.
     * Does not know about specific pricing logic — delegates entirely to strategy.
     */
    static class PricingCalculator {

        private PricingStrategy strategy;

        /**
         * @param strategy the initial pricing strategy
         */
        public PricingCalculator(PricingStrategy strategy) {
            this.strategy = strategy;
        }

        /**
         * Swaps the pricing strategy at runtime.
         * Enables runtime algorithm changes without reconstructing the calculator.
         *
         * @param strategy new pricing strategy
         */
        public void setStrategy(PricingStrategy strategy) {
            this.strategy = strategy;
        }

        /**
         * Calculates the total price using the current strategy.
         *
         * @param basePrice price per unit
         * @param quantity  number of units
         * @return total price
         */
        public double calculate(double basePrice, int quantity) {
            // WHY delegate: context has NO pricing logic — pure delegation
            return strategy.apply(basePrice, quantity);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 4: Main — demonstrates strategy swap and lambda usage
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        PricingCalculator calculator = new PricingCalculator(new RegularPricing());

        // Regular pricing
        double result = calculator.calculate(100.0, 5);
        System.out.printf("Regular  (5 × $100):            $%.2f%n", result);

        // Bulk pricing — swap strategy at runtime
        calculator.setStrategy(new BulkPricing());
        result = calculator.calculate(100.0, 15);
        System.out.printf("Bulk     (15 × $100, 10%% off): $%.2f%n", result);

        // Seasonal pricing
        calculator.setStrategy(new SeasonalPricing(0.25));
        result = calculator.calculate(100.0, 5);
        System.out.printf("Seasonal (5 × $100, 25%% off):  $%.2f%n", result);

        // Lambda strategy — no class needed!
        // WHY lambda: @FunctionalInterface enables this; identical to instantiating a class
        calculator.setStrategy((price, qty) -> price * qty * 0.60); // flash sale 40% off
        result = calculator.calculate(100.0, 5);
        System.out.printf("Flash    (5 × $100, 40%% off):  $%.2f%n", result);

        // Composite: bulk + seasonal
        calculator.setStrategy(new CompositePricing(List.of(
            new BulkPricing(),           // 10% off for qty=15
            new SeasonalPricing(0.05)    // additional 5% seasonal
        )));
        result = calculator.calculate(100.0, 15);
        System.out.printf("Composite(15 × $100, bulk+seasonal): $%.2f%n", result);
    }
}

/* ─────────────────────────────────────────────────────────────────────────────
   COMMON MISTAKES

   MISTAKE 1: if/else inside calculate() instead of delegation
     if (type.equals("bulk")) return price * qty * 0.9;  ← violates Open/Closed
     Adding a new strategy requires modifying calculate(). Use delegation.

   MISTAKE 2: Not using @FunctionalInterface
     Without it, the interface still works with lambdas IF it has one abstract method.
     But @FunctionalInterface gives a compile-time error if you accidentally add a second
     abstract method — acts as documentation and safety net.

   MISTAKE 3: State in strategy
     class SeasonalPricing { private double lastResult; }
     Strategy instances are often shared (like Spring beans) — state causes race conditions.
     All pricing logic should be pure functions: same inputs → same output.

   MISTAKE 4: Capturing mutable variables in lambda
     double discount = 0.25;
     calculator.setStrategy((p, q) -> p * q * (1 - discount));  ← requires effectively final
     discount = 0.30;  // COMPILE ERROR — can't mutate after lambda capture

   MISTAKE 5: Mixing strategy selection with strategy execution
     class Calculator { double calc(String type, double price, int qty) { ... } }
     This is Simple Factory + inline strategy combined — neither pattern properly.
     Separate the concern: factory selects the strategy, strategy does the math.
   ───────────────────────────────────────────────────────────────────────────── */
