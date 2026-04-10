/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : BeanScopeDemo.java                                     ║
 * ║  MODULE : 05-spring-core / 03-bean-lifecycle                      ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates singleton vs prototype bean scope ║
 * ║                   and the prototype-in-singleton trap             ║
 * ║  WHY IT EXISTS  : Wrong scope = shared mutable state (singleton) ║
 * ║                   or unexpected same-instance (prototype trap)   ║
 * ║  PYTHON COMPARE : Module-level obj = singleton                   ║
 * ║                   factory() call = prototype                      ║
 * ║  USE CASES      : 1) Stateless services (singleton)              ║
 * ║                   2) Stateful shopping carts (prototype)          ║
 * ║                   3) Per-request database sessions                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Singleton vs Prototype                           ║
 * ║                                                                    ║
 * ║    Singleton (default):          Prototype:                       ║
 * ║    ─────────────────────        ────────────                      ║
 * ║    Controller A ──┐              Controller A ──► Instance 1      ║
 * ║                    ├──► SAME     Controller B ──► Instance 2      ║
 * ║    Controller B ──┘  instance    Controller C ──► Instance 3      ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Singleton same hashCode, prototype different   ║
 * ║  RELATED FILES  : BeanLifecycleDemo.java, 04-bean-scopes.md     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.lifecycle;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Singleton-scoped bean — ONE instance for entire application.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Module-level singleton
 *   payment_service = PaymentService()  # created once, used everywhere
 * </pre>
 */
@Component
class SingletonService {
    private final String id = UUID.randomUUID().toString().substring(0, 8);

    public String getId() { return id; }
}

/**
 * Prototype-scoped bean — NEW instance every time requested.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   def create_cart() -> ShoppingCart:
 *       return ShoppingCart()  # new instance each call
 * </pre>
 */
@Component
@Scope("prototype")
class ShoppingCart {
    private final String cartId = UUID.randomUUID().toString().substring(0, 8);
    private final List<String> items = new ArrayList<>();

    public void addItem(String item) { items.add(item); }
    public String getCartId() { return cartId; }
    public List<String> getItems() { return items; }
}

/**
 * Demonstrates singleton vs prototype behavior and the prototype-in-singleton trap.
 *
 * <p><b>ASCII — The Prototype-in-Singleton Trap:</b>
 * <pre>
 *   WRONG: inject ShoppingCart directly into singleton
 *   → prototype injected ONCE at singleton creation
 *   → all requests share the SAME cart instance!
 *
 *   RIGHT: inject ObjectProvider&lt;ShoppingCart&gt;
 *   → call .getObject() each request
 *   → NEW cart instance every time
 * </pre>
 */
@Component
class BeanScopeRunner implements CommandLineRunner {

    private final SingletonService singleton1;
    private final SingletonService singleton2;
    private final ObjectProvider<ShoppingCart> cartProvider;

    /**
     * Note: We inject ObjectProvider for prototype, not the bean directly.
     * Direct injection would give us the SAME prototype instance always.
     */
    public BeanScopeRunner(
            SingletonService singleton1,
            SingletonService singleton2,
            ObjectProvider<ShoppingCart> cartProvider) {
        this.singleton1 = singleton1;
        this.singleton2 = singleton2;
        this.cartProvider = cartProvider;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Bean Scope Demo ===");
        System.out.println();

        // Singleton: same instance everywhere
        System.out.println("Singleton test:");
        System.out.println("  singleton1 ID: " + singleton1.getId());
        System.out.println("  singleton2 ID: " + singleton2.getId());
        System.out.println("  Same instance? " + (singleton1 == singleton2));
        System.out.println();

        // Prototype: new instance each time (via ObjectProvider)
        System.out.println("Prototype test (via ObjectProvider):");
        ShoppingCart cart1 = cartProvider.getObject();
        ShoppingCart cart2 = cartProvider.getObject();
        cart1.addItem("Java Book");
        cart2.addItem("Spring Guide");
        System.out.println("  cart1 ID: " + cart1.getCartId() + " items: " + cart1.getItems());
        System.out.println("  cart2 ID: " + cart2.getCartId() + " items: " + cart2.getItems());
        System.out.println("  Same instance? " + (cart1 == cart2));
    }
}
