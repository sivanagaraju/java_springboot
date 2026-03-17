import java.util.Map;
import java.util.HashMap;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║          FACTORY METHOD PATTERN — Demo                      ║
 * ║  Let subclasses decide which object to create               ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * WHAT: Define an interface for creating objects; subclass decides which.
 * WHY:  Decouple object creation from client code.
 * SPRING: Every @Bean method IS a factory method.
 *
 * ┌──────────────────────────────────────────────────────────────┐
 * │  Client  ──→  NotificationFactory  ──→  Notification        │
 * │                      △                       △              │
 * │           ┌──────────┼──────────┐    ┌──────┼──────┐        │
 * │       EmailFactory SmsFactory  PushF  Email  Sms  Push      │
 * │          create()   create()         send() send() send()   │
 * └──────────────────────────────────────────────────────────────┘
 */
public class FactoryDemo {

    // ── Product interface ──────────────────────────────────────
    interface Notification {
        void send(String message);
        String getChannel();
    }

    // ── Concrete products ──────────────────────────────────────
    static class EmailNotification implements Notification {
        public void send(String msg) {
            System.out.println("  📧 Email sent: " + msg);
        }
        public String getChannel() { return "EMAIL"; }
    }

    static class SmsNotification implements Notification {
        public void send(String msg) {
            System.out.println("  📱 SMS sent: " + msg);
        }
        public String getChannel() { return "SMS"; }
    }

    static class PushNotification implements Notification {
        public void send(String msg) {
            System.out.println("  🔔 Push sent: " + msg);
        }
        public String getChannel() { return "PUSH"; }
    }

    // ── Factory interface ──────────────────────────────────────
    /**
     * ┌────────────────────────────────────────────┐
     * │ NotificationFactory (Creator)               │
     * │  + create(): Notification                   │
     * │                                             │
     * │  Subclasses decide WHICH Notification       │
     * │  to instantiate. Client never uses 'new'.   │
     * └────────────────────────────────────────────┘
     */
    interface NotificationFactory {
        Notification create();
    }

    static class EmailFactory implements NotificationFactory {
        public Notification create() { return new EmailNotification(); }
    }
    static class SmsFactory implements NotificationFactory {
        public Notification create() { return new SmsNotification(); }
    }
    static class PushFactory implements NotificationFactory {
        public Notification create() { return new PushNotification(); }
    }

    // ── Registry Pattern (Simple Factory + Map) ────────────────
    /**
     * ┌─────────────────────────────────────────────┐
     * │  Registry Pattern                            │
     * │  ┌────────────┬───────────────────────┐     │
     * │  │ "email"    │ → EmailFactory         │     │
     * │  │ "sms"      │ → SmsFactory           │     │
     * │  │ "push"     │ → PushFactory          │     │
     * │  └────────────┴───────────────────────┘     │
     * │  No if/else! Just map.get(type).create()    │
     * └─────────────────────────────────────────────┘
     */
    static class NotificationRegistry {
        private static final Map<String, NotificationFactory> FACTORIES = new HashMap<>();

        static {
            FACTORIES.put("email", new EmailFactory());
            FACTORIES.put("sms",   new SmsFactory());
            FACTORIES.put("push",  new PushFactory());
        }

        public static Notification create(String type) {
            NotificationFactory factory = FACTORIES.get(type.toLowerCase());
            if (factory == null) {
                throw new IllegalArgumentException("Unknown type: " + type);
            }
            return factory.create();
        }

        // Extensible — add new factories at runtime!
        public static void register(String type, NotificationFactory factory) {
            FACTORIES.put(type.toLowerCase(), factory);
        }
    }

    // ── MAIN ───────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("═══ FACTORY METHOD PATTERN DEMO ═══\n");

        // 1. Direct factory usage
        System.out.println("1. Direct Factory:");
        NotificationFactory emailFactory = new EmailFactory();
        Notification email = emailFactory.create();
        email.send("Welcome to the platform!");

        // 2. Registry-based creation (no if/else!)
        System.out.println("\n2. Registry Pattern:");
        String[] types = {"email", "sms", "push"};
        for (String type : types) {
            Notification n = NotificationRegistry.create(type);
            n.send("Order #12345 confirmed");
        }

        // 3. Runtime extensibility
        System.out.println("\n3. Runtime Extension:");
        NotificationRegistry.register("slack",
            () -> new Notification() {
                public void send(String msg) { System.out.println("  💬 Slack: " + msg); }
                public String getChannel() { return "SLACK"; }
            });
        NotificationRegistry.create("slack").send("Deploy successful!");

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Factory Method decouples creation from usage.");
        System.out.println("Registry pattern eliminates if/else chains.");
        System.out.println("Spring's @Bean methods ARE factory methods.");
    }
}
