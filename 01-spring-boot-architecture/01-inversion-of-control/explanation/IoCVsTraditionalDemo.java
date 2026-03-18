package explanation;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: IoCVsTraditionalDemo.java                                                          ║
 * ║ PURPOSE & CONCEPT: Inversion of Control vs Tight Coupling                                ║
 * ║ Demonstrates the core mechanical difference between hardcoding dependencies (`new`)      ║
 * ║ and injecting them via the constructor (IoC). Shows why IoC leads to testable code.      ║
 * ║                                                                                          ║
 * ║ ┌──────────────────────────────────────────────────────────────────────────────────────┐ ║
 * ║ │ ASCII DIAGRAM: Inversion of Control vs Tight Coupling                                │ ║
 * ║ ├──────────────────────────────────────────────────────────────────────────────────────┤ ║
 * ║ │   TIGHT COUPLING (Bad)                 INVERSION OF CONTROL (Good)                   │ ║
 * ║ │   ┌─────────────────┐                  ┌─────────────┐                               │ ║
 * ║ │   │ BadOrderService │                  │ Spring      │ ────(injects)───┐           │ ║
 * ║ │   │                 │                  │ Container   │                 ▼           │ ║
 * ║ │   │  new Gateway()  │──(hardcoded)──►  └─────────────┘         ┌─────────┴────────┐  │ ║
 * ║ │   └─────────────────┘                                          │ GoodOrderService │  │ ║
 * ║ │                                                                └──────────────────┘  │ ║
 * ║ └──────────────────────────────────────────────────────────────────────────────────────┘ ║
 * ║                                                                                          ║
 * ║ PYTHON COMPARE:                                                                          ║
 * ║ Tight: `self.gateway = HardcodedGateway()` vs IoC: `def __init__(self, gateway)`         ║
 * ║                                                                                          ║
 * ║ HOW TO RUN: ./gradlew :01-spring-boot-architecture:run --args="IoCVsTraditionalDemo"     ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 */
public class IoCVsTraditionalDemo {

    public static void main(String[] args) {
        // --- Traditional Tight Coupling ---
        System.out.println("--- Traditional Tight Coupling ---");
        var badService = new BadOrderService();
        badService.processOrder();
        // PROBLEM: We cannot test BadOrderService without hitting the real HardcodedPaymentGateway.

        // --- Inversion of Control (IoC) ---
        System.out.println("\n--- Inversion of Control (IoC) ---");
        // We create the dependency externally using modern 'var'
        var mockGateway = new MockPaymentGateway(); 
        
        // We INJECT the dependency through the constructor.
        // We inverted the control of dependency creation!
        var goodService = new GoodOrderService(mockGateway);
        goodService.processOrder();
        // BENEFIT: GoodOrderService is completely decoupled and easily unit testable.
    }

    // --- The Interface (Abstraction) ---
    public interface PaymentGateway {
        void pay();
    }

    // --- Concrete Implementations ---
    public static class HardcodedPaymentGateway implements PaymentGateway {
        public void pay() {
            System.out.println("[PRODUCTION] Charging real credit card...");
        }
    }

    public static class MockPaymentGateway implements PaymentGateway {
        public void pay() {
            System.out.println("[TEST] Simulating successful payment without charging.");
        }
    }

    // --- TIGHTLY COUPLED SERVICE ---
    public static class BadOrderService {
        // TIGHT COUPLING: Hardcoded dependency instantiation.
        private HardcodedPaymentGateway gateway = new HardcodedPaymentGateway();

        public void processOrder() {
            System.out.print("Processing order... ");
            gateway.pay();
        }
    }

    // --- LOOSELY COUPLED SERVICE (IoC) ---
    public static class GoodOrderService {
        // LOOSE COUPLING: Depends on abstraction. Final guarantees immutability.
        private final PaymentGateway gateway;

        // The dependency is handed to us from the outside logic.
        public GoodOrderService(PaymentGateway gateway) {
            this.gateway = gateway;
        }

        public void processOrder() {
            System.out.print("Processing order logically... ");
            gateway.pay();
        }
    }
}
