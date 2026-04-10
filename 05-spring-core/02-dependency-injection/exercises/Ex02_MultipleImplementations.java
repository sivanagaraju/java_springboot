/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex02_MultipleImplementations.java                      ║
 * ║  MODULE : 05-spring-core / 02-dependency-injection / exercises    ║
 * ║  GRADLE : ./gradlew :05-spring-core:test                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — wire multiple implementations      ║
 * ║                   using @Qualifier and @Primary                   ║
 * ║  WHY IT EXISTS  : Real apps often have multiple implementations ║
 * ║                   — knowing how to select is essential            ║
 * ║  PYTHON COMPARE : Python: factory function with type parameter  ║
 * ║                   Java: @Qualifier("name") on injection point   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE INSTRUCTIONS:                                           ║
 * ║  1. Create a PaymentProcessor interface                           ║
 * ║  2. Implement: StripeProcessor, PayPalProcessor, MockProcessor   ║
 * ║  3. Mark StripeProcessor as @Primary                              ║
 * ║  4. Create a CheckoutService that injects:                        ║
 * ║     a) The primary processor (no qualifier)                       ║
 * ║     b) PayPal processor (with @Qualifier)                         ║
 * ║     c) ALL processors (as List<PaymentProcessor>)                 ║
 * ║  5. Test that the right processor is used in each case           ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.exercises;

/**
 * Exercise: Multiple implementations with @Primary and @Qualifier.
 *
 * <p><b>ASCII — What you should build:</b>
 * <pre>
 *   PaymentProcessor (interface)
 *       │
 *       ├── StripeProcessor   ← @Primary (default)
 *       ├── PayPalProcessor   ← @Qualifier("paypal")
 *       └── MockProcessor     ← @Qualifier("mock") + @Profile("test")
 *
 *   CheckoutService:
 *       primary  → StripeProcessor (no qualifier needed)
 *       paypal   → PayPalProcessor (@Qualifier("paypal"))
 *       all      → List<PaymentProcessor> (all 3)
 * </pre>
 */
public class Ex02_MultipleImplementations {

    // TODO: Define PaymentProcessor interface
    interface PaymentProcessor {
        String process(double amount);
        String getName();
    }

    // TODO: Implement StripeProcessor with @Service + @Primary

    // TODO: Implement PayPalProcessor with @Service("paypal")

    // TODO: Implement MockProcessor with @Service("mock") + @Profile("test")

    // TODO: Create CheckoutService with:
    //   1) PaymentProcessor primary (no qualifier → gets @Primary)
    //   2) @Qualifier("paypal") PaymentProcessor paypal
    //   3) List<PaymentProcessor> all (gets ALL implementations)

    public static void main(String[] args) {
        System.out.println("=== Multiple Implementations Exercise ===");
        System.out.println();
        System.out.println("Build plan:");
        System.out.println("  1. PaymentProcessor interface with process() and getName()");
        System.out.println("  2. StripeProcessor — @Service @Primary");
        System.out.println("  3. PayPalProcessor — @Service(\"paypal\")");
        System.out.println("  4. MockProcessor — @Service(\"mock\") @Profile(\"test\")");
        System.out.println("  5. CheckoutService — inject primary, qualified, and list");
        System.out.println();
        System.out.println("Expected behavior:");
        System.out.println("  primary.process(100)  → \"Stripe: charged $100\"");
        System.out.println("  paypal.process(50)    → \"PayPal: charged $50\"");
        System.out.println("  all.size()            → 2 or 3 (depends on active profile)");
    }
}
