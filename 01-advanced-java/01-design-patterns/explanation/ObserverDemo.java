import java.util.ArrayList;
import java.util.List;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║          OBSERVER PATTERN — Demo                            ║
 * ║  Event-driven communication + Spring ApplicationEvent       ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * WHAT: When something happens, notify all interested parties.
 * WHY:  Decouples publisher from subscribers — add listeners without modifying source.
 * SPRING: ApplicationEvent + @EventListener.
 *
 * ┌────────────────────────────────────────────────────────────┐
 * │  OrderService (Subject)                                    │
 * │    │                                                       │
 * │    │ publish(OrderEvent)                                   │
 * │    ├──────→ EmailListener.onEvent()     📧 Send email     │
 * │    ├──────→ InventoryListener.onEvent() 📦 Update stock   │
 * │    └──────→ AnalyticsListener.onEvent() 📊 Track metrics  │
 * │                                                            │
 * │  Adding loyalty? Just add LoyaltyListener. Zero changes!  │
 * └────────────────────────────────────────────────────────────┘
 */
public class ObserverDemo {

    // ── Event (the data being published) ───────────────────────
    record OrderEvent(String orderId, double amount, String customerEmail) {}

    // ── Observer interface ─────────────────────────────────────
    interface OrderObserver {
        void onOrderPlaced(OrderEvent event);
    }

    // ── Concrete Observers ─────────────────────────────────────
    static class EmailNotifier implements OrderObserver {
        public void onOrderPlaced(OrderEvent e) {
            System.out.println("  📧 Email → " + e.customerEmail()
                + ": Order " + e.orderId() + " confirmed ($" + e.amount() + ")");
        }
    }

    static class InventoryUpdater implements OrderObserver {
        public void onOrderPlaced(OrderEvent e) {
            System.out.println("  📦 Inventory → Reducing stock for order " + e.orderId());
        }
    }

    static class AnalyticsTracker implements OrderObserver {
        public void onOrderPlaced(OrderEvent e) {
            System.out.println("  📊 Analytics → Revenue +$" + e.amount());
        }
    }

    // ── Subject (Event Source) ─────────────────────────────────
    /**
     * ┌────────────────────────────────────────┐
     * │ OrderService (Subject)                  │
     * │  ┌─────────────────────────────┐       │
     * │  │ List<OrderObserver> observers│       │
     * │  ├──── EmailNotifier           │       │
     * │  ├──── InventoryUpdater        │       │
     * │  └──── AnalyticsTracker        │       │
     * │                                        │
     * │  placeOrder(id, amount, email)         │
     * │    → save order                        │
     * │    → notify ALL observers              │
     * └────────────────────────────────────────┘
     */
    static class OrderService {
        private final List<OrderObserver> observers = new ArrayList<>();

        public void subscribe(OrderObserver observer) {
            observers.add(observer);
            System.out.println("  ✓ Subscribed: " + observer.getClass().getSimpleName());
        }

        public void unsubscribe(OrderObserver observer) {
            observers.remove(observer);
        }

        public void placeOrder(String orderId, double amount, String email) {
            System.out.println("\n  → Placing order " + orderId + " ($" + amount + ")");
            // ... business logic: save to DB ...
            OrderEvent event = new OrderEvent(orderId, amount, email);
            // Notify all observers
            observers.forEach(obs -> obs.onOrderPlaced(event));
        }
    }

    // ── MAIN ───────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("═══ OBSERVER PATTERN DEMO ═══\n");

        OrderService orderService = new OrderService();

        // 1. Subscribe observers
        System.out.println("1. Subscribing Observers:");
        orderService.subscribe(new EmailNotifier());
        orderService.subscribe(new InventoryUpdater());
        orderService.subscribe(new AnalyticsTracker());

        // 2. Place an order — all observers notified!
        System.out.println("\n2. Placing Orders:");
        orderService.placeOrder("ORD-001", 99.99, "john@example.com");
        orderService.placeOrder("ORD-002", 249.50, "jane@example.com");

        // 3. Dynamic: add loyalty observer at runtime
        System.out.println("\n3. Adding Observer at Runtime:");
        orderService.subscribe(event ->
            System.out.println("  🏆 Loyalty → +" + (int)(event.amount() * 10) + " points"));
        orderService.placeOrder("ORD-003", 150.00, "bob@example.com");

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Observer decouples event source from event handlers.");
        System.out.println("In Spring: use @EventListener instead of manual subscribe.");
        System.out.println("Use @Async @EventListener for non-critical side effects.");
    }
}
