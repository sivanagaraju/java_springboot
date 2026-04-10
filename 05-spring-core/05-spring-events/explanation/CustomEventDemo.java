/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : CustomEventDemo.java                                   ║
 * ║  MODULE : 05-spring-core / 05-spring-events                       ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Shows custom event creation, publishing, and   ║
 * ║                   listening using POJO events (Spring 4.2+)      ║
 * ║  WHY IT EXISTS  : Custom events decouple business logic — the   ║
 * ║                   publisher doesn't know who listens              ║
 * ║  PYTHON COMPARE : Python blinker: signal('order-created')       ║
 * ║                   Spring: publishEvent(OrderCreatedEvent)        ║
 * ║  USE CASES      : 1) Order → notification + inventory + audit   ║
 * ║                   2) User registration → welcome email + stats   ║
 * ║                   3) Payment → receipt + accounting + analytics   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Custom Event Flow                                ║
 * ║                                                                    ║
 * ║    OrderService.createOrder()                                      ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    publisher.publishEvent(new OrderCreatedEvent(...))              ║
 * ║        │                                                           ║
 * ║        ├──► EmailListener           → sends email                 ║
 * ║        ├──► InventoryListener       → updates stock               ║
 * ║        └──► AuditListener           → writes audit log            ║
 * ║                                                                    ║
 * ║    OrderService knows NOTHING about listeners! (loose coupling)   ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Order created + 3 listener reactions            ║
 * ║  RELATED FILES  : AsyncEventDemo.java, 02-custom-events.md      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.events;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Custom event — any POJO works since Spring 4.2 (no ApplicationEvent needed).
 * Using Java 21 record for immutability.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   @dataclass(frozen=True)
 *   class OrderCreatedEvent:
 *       order_id: int
 *       customer: str
 *       total: Decimal
 * </pre>
 */
record OrderCreatedEvent(Long orderId, String customer, BigDecimal total) {}

/**
 * Service that publishes events — doesn't know about listeners.
 *
 * <p><b>ASCII — Publisher doesn't know listeners:</b>
 * <pre>
 *   OrderEventService ──► ApplicationEventPublisher ──► ???
 *                         (Spring finds all listeners)
 * </pre>
 */
@Service
class OrderEventService {

    private final ApplicationEventPublisher publisher;

    public OrderEventService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Creates an order and publishes an event.
     * The service has NO idea who will react to this event.
     *
     * @param customer customer name
     * @param total order total
     */
    public void createOrder(String customer, BigDecimal total) {
        Long orderId = System.currentTimeMillis() % 10000;
        System.out.println("  📦 Order #" + orderId + " created for " + customer);

        // Fire and forget — publisher doesn't care who listens
        publisher.publishEvent(new OrderCreatedEvent(orderId, customer, total));
    }
}

// --- Listeners (each independent, loosely coupled) ---

@Component
class OrderEmailListener {
    @EventListener
    public void handle(OrderCreatedEvent event) {
        System.out.println("  📧 Email sent to " + event.customer() +
                ": Order #" + event.orderId() + " confirmed ($" + event.total() + ")");
    }
}

@Component
class OrderInventoryListener {
    @EventListener
    public void handle(OrderCreatedEvent event) {
        System.out.println("  📦 Inventory updated for order #" + event.orderId());
    }
}

@Component
class OrderAuditListener {
    @EventListener
    public void handle(OrderCreatedEvent event) {
        System.out.println("  📝 Audit log: " + event.customer() +
                " placed order #" + event.orderId() + " ($" + event.total() + ")");
    }
}

/**
 * Runner to demonstrate the custom event flow.
 */
@Component
class CustomEventRunner implements CommandLineRunner {

    private final OrderEventService orderService;

    public CustomEventRunner(OrderEventService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Custom Event Demo ===");
        System.out.println();
        orderService.createOrder("Sivan", new BigDecimal("149.99"));
        System.out.println();
        System.out.println("Key insight: OrderEventService has NO imports of any listener class.");
        System.out.println("You can add/remove listeners without changing the service!");
    }
}
