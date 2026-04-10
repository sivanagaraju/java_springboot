/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_OrderEventSystem.java                             ║
 * ║  MODULE : 05-spring-core / 05-spring-events / exercises           ║
 * ║  GRADLE : ./gradlew :05-spring-core:test                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — build a complete event-driven       ║
 * ║                   order processing pipeline                       ║
 * ║  WHY IT EXISTS  : Event-driven design is the foundation for     ║
 * ║                   microservices and scalable architectures        ║
 * ║  PYTHON COMPARE : Python: pubsub pattern or Django signals      ║
 * ║                   Java: Spring Events with @EventListener         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE INSTRUCTIONS:                                           ║
 * ║  1. Create 3 events: OrderPlaced, OrderPaid, OrderShipped        ║
 * ║  2. Create OrderService that publishes OrderPlaced               ║
 * ║  3. Create PaymentListener: OrderPlaced → process → OrderPaid   ║
 * ║  4. Create ShippingListener: OrderPaid → ship → OrderShipped    ║
 * ║  5. Create NotificationListener: listens to ALL three events    ║
 * ║  6. Run and verify the event chain:                               ║
 * ║     OrderPlaced → PaymentListener → OrderPaid → ShippingListener║
 * ║     → OrderShipped → all logged by NotificationListener          ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Event Pipeline                                   ║
 * ║                                                                    ║
 * ║    OrderService.placeOrder()                                       ║
 * ║        │                                                           ║
 * ║        ▼ publishes                                                ║
 * ║    OrderPlacedEvent ──┬──► PaymentListener                        ║
 * ║                       │       │ publishes                          ║
 * ║                       │       ▼                                    ║
 * ║                       │   OrderPaidEvent ──► ShippingListener     ║
 * ║                       │                          │ publishes       ║
 * ║                       │                          ▼                 ║
 * ║                       │                      OrderShippedEvent     ║
 * ║                       │                                            ║
 * ║                       └──► NotificationListener (all 3 events)    ║
 * ║                                                                    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.exercises;

/**
 * Exercise: Build a complete event-driven order processing pipeline.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Django signals approach
 *   order_placed = Signal()
 *   order_paid = Signal()
 *   order_shipped = Signal()
 *
 *   @receiver(order_placed)
 *   def handle_payment(sender, order, **kwargs):
 *       process_payment(order)
 *       order_paid.send(sender=None, order=order)
 *
 *   @receiver(order_paid)
 *   def handle_shipping(sender, order, **kwargs):
 *       ship_order(order)
 *       order_shipped.send(sender=None, order=order)
 * </pre>
 */
public class Ex01_OrderEventSystem {

    // TODO: Create event records
    // record OrderPlacedEvent(Long orderId, String customer, double total) {}
    // record OrderPaidEvent(Long orderId, String paymentRef) {}
    // record OrderShippedEvent(Long orderId, String trackingNumber) {}

    // TODO: Create OrderService with ApplicationEventPublisher
    //   placeOrder(customer, total) → publishes OrderPlacedEvent

    // TODO: Create PaymentListener
    //   @EventListener OrderPlacedEvent → process → publish OrderPaidEvent

    // TODO: Create ShippingListener
    //   @EventListener OrderPaidEvent → ship → publish OrderShippedEvent

    // TODO: Create NotificationListener
    //   @EventListener for ALL 3 events → log notifications

    public static void main(String[] args) {
        System.out.println("=== Order Event System Exercise ===");
        System.out.println();
        System.out.println("Build an event pipeline:");
        System.out.println("  1. record OrderPlacedEvent(orderId, customer, total)");
        System.out.println("  2. record OrderPaidEvent(orderId, paymentRef)");
        System.out.println("  3. record OrderShippedEvent(orderId, trackingNumber)");
        System.out.println();
        System.out.println("Event chain:");
        System.out.println("  placeOrder() → OrderPlacedEvent");
        System.out.println("    → PaymentListener → OrderPaidEvent");
        System.out.println("      → ShippingListener → OrderShippedEvent");
        System.out.println("        → NotificationListener logs everything");
        System.out.println();
        System.out.println("Key technique: Each listener can publish NEW events,");
        System.out.println("creating a chain without any listener knowing about others.");
    }
}
