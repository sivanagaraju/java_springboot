package explanation;

public class IoCVsTraditionalDemo {

    public static void main(String[] args) {
        System.out.println("--- Traditional Tight Coupling ---");
        BadOrderService badService = new BadOrderService();
        badService.processOrder();
        // PROBLEM: We cannot test BadOrderService without hitting the real HardcodedPaymentGateway.

        System.out.println("\n--- Inversion of Control (IoC) ---");
        // We create the dependency externally
        PaymentGateway mockGateway = new MockPaymentGateway(); 
        
        // We INJECT the dependency through the constructor.
        // We inverted the control of dependency creation!
        GoodOrderService goodService = new GoodOrderService(mockGateway);
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
