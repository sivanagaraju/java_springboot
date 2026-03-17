import java.util.List;
import java.util.function.Function;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║          STRATEGY PATTERN — Demo                            ║
 * ║  Interchangeable algorithms via composition/lambdas         ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * WHAT: Define a family of algorithms, encapsulate each, make interchangeable.
 * WHY:  Swap behavior at runtime without modifying context.
 * SPRING: AuthenticationProvider, ResourceLoader, MessageConverter.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │  PricingContext                                              │
 * │    │                                                         │
 * │    │ uses PricingStrategy                                    │
 * │    │                                                         │
 * │    ├── RegularPricing      (base price)                     │
 * │    ├── PremiumPricing      (20% discount)                   │
 * │    ├── BlackFridayPricing  (50% off + free shipping)        │
 * │    └── (any lambda)        (custom logic at runtime)        │
 * └──────────────────────────────────────────────────────────────┘
 */
public class StrategyDemo {

    // ── Strategy interface (functional!) ───────────────────────
    @FunctionalInterface
    interface PricingStrategy {
        double calculate(double basePrice, int quantity);
    }

    // ── Concrete strategies ────────────────────────────────────
    static class RegularPricing implements PricingStrategy {
        public double calculate(double price, int qty) {
            return price * qty;
        }
    }

    static class PremiumPricing implements PricingStrategy {
        public double calculate(double price, int qty) {
            return price * qty * 0.80;  // 20% member discount
        }
    }

    static class BlackFridayPricing implements PricingStrategy {
        public double calculate(double price, int qty) {
            double total = price * qty * 0.50;  // 50% off
            return total > 100 ? total : total + 9.99;  // free shipping over $100
        }
    }

    // ── Context ────────────────────────────────────────────────
    /**
     * ┌────────────────────────────────────────────┐
     * │  ShoppingCart (Context)                      │
     * │  ┌──────────────────────────────────────┐  │
     * │  │ PricingStrategy strategy             │  │
     * │  │ (injected, swappable at runtime)     │  │
     * │  └──────────────────────────────────────┘  │
     * │  checkout() → strategy.calculate(price,qty)│
     * └────────────────────────────────────────────┘
     */
    static class ShoppingCart {
        private PricingStrategy strategy;

        public ShoppingCart(PricingStrategy strategy) {
            this.strategy = strategy;
        }

        public void setStrategy(PricingStrategy strategy) {
            this.strategy = strategy;
        }

        public double checkout(double price, int quantity) {
            return strategy.calculate(price, quantity);
        }
    }

    // ── MAIN ───────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("═══ STRATEGY PATTERN DEMO ═══\n");

        double price = 100.00;
        int qty = 3;

        // 1. Class-based strategies
        System.out.println("1. Class-based Strategies (price=$100, qty=3):");
        ShoppingCart cart = new ShoppingCart(new RegularPricing());
        System.out.printf("  Regular:      $%.2f%n", cart.checkout(price, qty));

        cart.setStrategy(new PremiumPricing());
        System.out.printf("  Premium:      $%.2f%n", cart.checkout(price, qty));

        cart.setStrategy(new BlackFridayPricing());
        System.out.printf("  Black Friday: $%.2f%n", cart.checkout(price, qty));

        // 2. Lambda strategies (no class needed!)
        System.out.println("\n2. Lambda Strategies:");
        cart.setStrategy((p, q) -> p * q * 0.70);  // 30% off
        System.out.printf("  Flash sale:   $%.2f%n", cart.checkout(price, qty));

        cart.setStrategy((p, q) -> q >= 5 ? p * q * 0.60 : p * q);
        System.out.printf("  Bulk (qty<5): $%.2f%n", cart.checkout(price, 3));
        System.out.printf("  Bulk (qty≥5): $%.2f%n", cart.checkout(price, 5));

        // 3. JDK Strategy examples
        System.out.println("\n3. JDK Built-in Strategies:");
        List<String> names = List.of("Charlie", "Alice", "Bob");

        System.out.println("  Comparator (sorting strategy):");
        List<String> sorted = names.stream().sorted().toList();
        System.out.println("    Natural: " + sorted);

        sorted = names.stream().sorted((a, b) -> b.compareTo(a)).toList();
        System.out.println("    Reverse: " + sorted);

        // Function<T,R> is also a strategy!
        System.out.println("\n  Function (transformation strategy):");
        Function<String, String> upper = String::toUpperCase;
        Function<String, String> first3 = s -> s.substring(0, Math.min(3, s.length()));
        Function<String, String> combined = upper.andThen(first3);
        names.stream().map(combined).forEach(s -> System.out.println("    " + s));

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Strategy = swappable algorithm via composition.");
        System.out.println("With Java 8 lambdas, simple strategies don't need classes.");
        System.out.println("Comparator, Predicate, Function are all Strategy pattern!");
    }
}
