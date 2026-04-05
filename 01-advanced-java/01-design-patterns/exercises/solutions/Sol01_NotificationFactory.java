/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 1: Notification Factory                                   ║
 * ║  Factory Method with Map-based registry + runtime extensibility     ║
 * ║                                                                      ║
 * ║  ASCII FLOW:                                                         ║
 * ║                                                                      ║
 * ║   factory.create("email")                                            ║
 * ║        │                                                             ║
 * ║        ▼                                                             ║
 * ║   registry.get("email")   ← Map<String, Supplier<Notification>>     ║
 * ║        │                                                             ║
 * ║        ▼                                                             ║
 * ║   Supplier.get() → new EmailNotification()                          ║
 * ║        │                                                             ║
 * ║        ▼                                                             ║
 * ║   notification.send("Welcome, John!")                                ║
 * ║        │                                                             ║
 * ║        ▼                                                             ║
 * ║   📧 [EMAIL] Welcome, John!                                         ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Demonstrates Factory Method with a Supplier-based registry.
 * Key insight: Map<String, Supplier<Notification>> is the pattern Spring's
 * ApplicationContext uses internally for bean creation.
 */
public class Sol01_NotificationFactory {

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 1: Product interface — all notifications implement this contract
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * The Product interface. All concrete notification types implement this.
     * In Spring: this is the bean interface (e.g., MessageSender).
     */
    interface Notification {
        /**
         * Sends a message through this notification channel.
         *
         * @param message the content to deliver
         */
        void send(String message);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 2: Concrete products — each channel has a single responsibility
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Email notification — delivers messages via email channel.
     */
    static class EmailNotification implements Notification {
        @Override
        public void send(String message) {
            // WHY: prefix shows channel origin for debugging/logging
            System.out.println("📧 [EMAIL] " + message);
        }
    }

    /**
     * SMS notification — delivers messages via SMS channel.
     */
    static class SmsNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("📱 [SMS] " + message);
        }
    }

    /**
     * Slack notification — delivers messages to a Slack workspace.
     */
    static class SlackNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("💬 [SLACK] " + message);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 3: Factory — thread-safe registry with runtime registration
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Factory with a Supplier-based registry.
     *
     * <p>Design decisions:
     * <ul>
     *   <li>ConcurrentHashMap — thread-safe for concurrent web request handling
     *   <li>Supplier&lt;Notification&gt; — creates a NEW instance per call (prototype)
     *   <li>Pre-registration in static initializer — matches Spring's component scanning
     * </ul>
     */
    static class NotificationFactory {

        // WHY ConcurrentHashMap: factory is a singleton, accessed from multiple threads
        private final Map<String, Supplier<Notification>> registry = new ConcurrentHashMap<>();

        /** Initializes built-in notification types. */
        public NotificationFactory() {
            // WHY register in constructor: deterministic setup, no lazy initialization race
            registry.put("email", EmailNotification::new);
            registry.put("sms", SmsNotification::new);
            registry.put("slack", SlackNotification::new);
        }

        /**
         * Registers a new notification type at runtime.
         * Enables plugin-style extensibility without modifying factory code.
         *
         * @param type    the key clients use to request this type
         * @param creator factory function for the new type
         */
        public void register(String type, Supplier<Notification> creator) {
            registry.put(type.toLowerCase(), creator);
        }

        /**
         * Creates a notification of the requested type.
         *
         * @param type the notification channel key (case-insensitive)
         * @return new Notification instance
         * @throws IllegalArgumentException if type is not registered
         */
        public Notification create(String type) {
            Supplier<Notification> creator = registry.get(type.toLowerCase());

            // WHY explicit check: fail fast with a clear message rather than NPE
            if (creator == null) {
                throw new IllegalArgumentException(
                    "Unknown notification type: '" + type + "'. Registered: " + registry.keySet()
                );
            }

            return creator.get();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 4: Main — demonstrates factory usage and runtime extensibility
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        NotificationFactory factory = new NotificationFactory();

        // WHY: test each built-in type
        factory.create("email").send("Welcome, John!");
        factory.create("sms").send("Your OTP is 123456");
        factory.create("slack").send("Build #42 passed");

        // WHY: runtime registration shows Open/Closed Principle —
        // new types added without modifying existing factory code
        factory.register("webhook", () -> message ->
            System.out.println("🔔 [WEBHOOK] " + message)
        );
        factory.create("webhook").send("Event triggered");

        // WHY: test error handling
        try {
            factory.create("fax");
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}

/* ─────────────────────────────────────────────────────────────────────────────
   COMMON MISTAKES

   MISTAKE 1: Using new directly in create() instead of registry
     return new EmailNotification();  ← Open/Closed violation
     Every new type requires modifying create(). Use registry.

   MISTAKE 2: Using HashMap instead of ConcurrentHashMap
     HashMap.put() is NOT thread-safe. Concurrent register() calls corrupt state.
     ConcurrentHashMap uses segment locking for safe concurrent access.

   MISTAKE 3: Storing instances (not Suppliers) in the registry
     registry.put("email", new EmailNotification());  ← all calls share ONE instance
     Use Supplier to create a fresh instance per call (unless singleton is intended).

   MISTAKE 4: Not lowercasing the type key
     registry.get("Email") vs registry.get("email") → null if not normalized.
     Always normalize: type.toLowerCase() on both put() and get().

   MISTAKE 5: Returning null instead of throwing IllegalArgumentException
     if (creator == null) return null;  ← caller gets NPE elsewhere, no context
     Always throw with a clear message including valid options.
   ───────────────────────────────────────────────────────────────────────────── */
