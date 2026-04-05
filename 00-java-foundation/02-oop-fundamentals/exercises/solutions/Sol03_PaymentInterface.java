/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol03_PaymentInterface.java                            ║
 * ║  MODULE : 00-java-foundation / 02-oop-fundamentals               ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — interfaces + polymorphic methods    ║
 * ║  DEMONSTRATES   : interface, implements, dependency on interface ║
 * ║  PYTHON COMPARE : Python Protocol (typing); Java interface       ║
 * ║                                                                  ║
 * ║  DESIGN:                                                         ║
 * ║   Payable (interface)                                            ║
 * ║    ├── CreditCard  implements Payable                            ║
 * ║    └── UPI         implements Payable                            ║
 * ║   checkout(Payable, amount) → accepts EITHER concrete type       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

/**
 * Defines the capability contract for any payment method.
 *
 * <p>WHY interface (not abstract class): {@code CreditCard} and {@code UPI}
 * share NO fields and NO implementation — only the capability contract.
 * Interfaces are correct for "can-do" relationships.
 *
 * <p>Python equivalent (structural subtyping via Protocol):
 * <pre>{@code
 *   from typing import Protocol
 *   class Payable(Protocol):
 *       def authorize(self, amount: float) -> bool: ...
 *       def capture(self) -> bool: ...
 * }</pre>
 */
interface Payable {

    /**
     * Validates and authorizes the payment for the given amount.
     *
     * @param amount the payment amount
     * @return {@code true} if authorization succeeded
     */
    boolean authorize(double amount);

    /**
     * Captures (settles) the previously authorized payment.
     *
     * @return {@code true} if capture succeeded
     */
    boolean capture();
}

/**
 * Credit card payment method.
 *
 * <p>In production this would call the card network's authorization API.
 */
class CreditCard implements Payable {

    // WHY private: PCI-DSS requirement — card number and CVV must never
    // be accessible outside the class that holds them.
    private final String cardNumber;
    private final int cvv;

    /**
     * Creates a credit card payment method.
     *
     * @param cardNumber the 16-digit card number (masked in logs)
     * @param cvv        the 3-digit security code
     */
    public CreditCard(String cardNumber, int cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }

    /**
     * Authorizes payment via card network.
     *
     * @param amount the payment amount
     * @return {@code true} always (simulation)
     */
    @Override
    public boolean authorize(double amount) {
        // WHY mask cardNumber: never log full card numbers — last 4 digits only
        String masked = "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        System.out.printf("  [CreditCard] Authorizing %.2f for card %s%n", amount, masked);
        return true;
    }

    /**
     * Captures funds via credit network.
     *
     * @return {@code true} always (simulation)
     */
    @Override
    public boolean capture() {
        System.out.println("  [CreditCard] Capturing funds from credit network...");
        return true;
    }
}

/**
 * UPI (Unified Payments Interface) payment method.
 */
class UPI implements Payable {

    // WHY private final: VPA is an immutable identifier for this payment method
    private final String vpa;

    /**
     * Creates a UPI payment method.
     *
     * @param vpa the Virtual Payment Address (e.g., {@code user@bank})
     */
    public UPI(String vpa) {
        this.vpa = vpa;
    }

    /**
     * Sends authorization request to the user's bank.
     *
     * @param amount the payment amount
     * @return {@code true} always (simulation)
     */
    @Override
    public boolean authorize(double amount) {
        System.out.printf("  [UPI] Sending PIN request to %s for %.2f%n", vpa, amount);
        return true;
    }

    /**
     * Waits for bank settlement confirmation.
     *
     * @return {@code true} always (simulation)
     */
    @Override
    public boolean capture() {
        System.out.println("  [UPI] Waiting for bank confirmation...");
        return true;
    }
}

/**
 * Test harness demonstrating polymorphic {@code checkout()} usage.
 */
public class Sol03_PaymentInterface {

    /**
     * Processes a payment using any {@code Payable} implementation.
     *
     * <p>WHY accept Payable (not CreditCard/UPI): this method works for ALL
     * current and future payment methods. Adding {@code Crypto} later requires
     * zero changes to {@code checkout()} — Open/Closed Principle.
     *
     * <p>Python equivalent: any object with authorize() and capture() methods
     * works due to duck typing. Java requires explicit {@code implements Payable}.
     *
     * @param paymentMethod any Payable implementation
     * @param amount        the amount to charge
     * @return {@code true} if both authorize and capture succeeded
     */
    public static boolean checkout(Payable paymentMethod, double amount) {
        System.out.println("  [Gateway] Commencing checkout...");
        // WHY short-circuit: don't capture if authorization failed
        if (paymentMethod.authorize(amount)) {
            return paymentMethod.capture();
        }
        return false;
    }

    /**
     * Runs checkout scenarios for both payment methods.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("--- Payment Interface Solution ---\n");

        CreditCard card = new CreditCard("1111-2222-3333-4444", 123);
        UPI upi = new UPI("sivan@icici");

        System.out.println("=== Credit Card Checkout ===");
        boolean cardOk = checkout(card, 99.99);
        System.out.println("Result: " + cardOk);

        System.out.println("\n=== UPI Checkout ===");
        boolean upiOk = checkout(upi, 50.00);
        System.out.println("Result: " + upiOk);

        // WHY Payable reference: demonstrates that the variable type
        // doesn't matter — both concrete types work as Payable.
        System.out.println("\n=== Polymorphic reference ===");
        Payable method = new CreditCard("9999-8888-7777-6666", 321);
        checkout(method, 200.00);
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: checkout() accepting concrete types
 *   WRONG: public static boolean checkout(CreditCard card, double amount)
 *   PROBLEM: Now you need a separate checkout() for UPI, Crypto, etc.
 *            Every new payment method requires a new method overload.
 *   RIGHT: Accept the interface type — works for all implementations.
 *
 * MISTAKE 2: Using abstract class when there's no shared state
 *   If CreditCard and UPI have no shared fields, an abstract class
 *   adds no value over an interface. Use interfaces for pure contracts.
 *
 * MISTAKE 3: Making interface methods public explicitly
 *   WRONG: public boolean authorize(double amount);
 *   RIGHT: boolean authorize(double amount);
 *   Interface methods are implicitly public — adding 'public' is redundant
 *   (though not wrong). The @Override annotation in implementations IS needed.
 *
 * MISTAKE 4: Forgetting @Override on implementations
 *   Without @Override, a typo in the method name creates a NEW method
 *   instead of overriding — the interface method remains unimplemented,
 *   but only caught at runtime (or not at all if the wrong method is called).
 *
 * MISTAKE 5: Spring anti-pattern — injecting concrete type
 *   WRONG: @Autowired CreditCard paymentService;
 *   RIGHT: @Autowired Payable paymentService;
 *          Spring injects the right bean; code stays testable with mocks.
 * ═══════════════════════════════════════════════════════════════════ */
