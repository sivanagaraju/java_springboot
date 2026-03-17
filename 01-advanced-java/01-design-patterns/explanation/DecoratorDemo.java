import java.io.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║     DECORATOR PATTERN — Demo                                ║
 * ║  Wrap objects to add behavior without modifying originals   ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * WHAT: Add responsibilities to objects dynamically by wrapping them.
 * WHY:  Avoids class explosion from inheritance. Single Responsibility.
 * SPRING: I/O streams, HandlerInterceptor chains, Filter chains.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  Onion Architecture of Decorators:                          │
 * │                                                             │
 * │  ┌── LoggingDecorator ──────────────────────────────────┐  │
 * │  │  log("before")                                        │  │
 * │  │  ┌── CachingDecorator ───────────────────────────┐   │  │
 * │  │  │  if (cached) return cached                     │   │  │
 * │  │  │  ┌── UpperCaseDecorator ──────────────────┐   │   │  │
 * │  │  │  │  ┌── BasicDataService ─────────────┐   │   │   │  │
 * │  │  │  │  │  return db.query(key)            │   │   │   │  │
 * │  │  │  │  └─────────────────────────────────┘   │   │   │  │
 * │  │  │  │  result.toUpperCase()                   │   │   │  │
 * │  │  │  └────────────────────────────────────────┘   │   │  │
 * │  │  │  cache.put(key, result)                        │   │  │
 * │  │  └────────────────────────────────────────────────┘   │  │
 * │  │  log("after")                                         │  │
 * │  └──────────────────────────────────────────────────────┘  │
 * └─────────────────────────────────────────────────────────────┘
 */
public class DecoratorDemo {

    // ── Component interface ────────────────────────────────────
    interface DataService {
        String getData(String key);
    }

    // ── Concrete Component ─────────────────────────────────────
    static class BasicDataService implements DataService {
        public String getData(String key) {
            // Simulate DB query
            return "value_for_" + key;
        }
    }

    // ── Base Decorator ─────────────────────────────────────────
    static abstract class DataServiceDecorator implements DataService {
        protected final DataService wrapped;
        DataServiceDecorator(DataService wrapped) { this.wrapped = wrapped; }
    }

    // ── Concrete Decorators ────────────────────────────────────
    static class LoggingDecorator extends DataServiceDecorator {
        LoggingDecorator(DataService wrapped) { super(wrapped); }

        public String getData(String key) {
            System.out.println("    [LOG] Getting data for key: " + key);
            String result = wrapped.getData(key);
            System.out.println("    [LOG] Result: " + result);
            return result;
        }
    }

    static class CachingDecorator extends DataServiceDecorator {
        private final java.util.Map<String, String> cache = new java.util.HashMap<>();

        CachingDecorator(DataService wrapped) { super(wrapped); }

        public String getData(String key) {
            if (cache.containsKey(key)) {
                System.out.println("    [CACHE] Hit for: " + key);
                return cache.get(key);
            }
            System.out.println("    [CACHE] Miss for: " + key);
            String result = wrapped.getData(key);
            cache.put(key, result);
            return result;
        }
    }

    static class UpperCaseDecorator extends DataServiceDecorator {
        UpperCaseDecorator(DataService wrapped) { super(wrapped); }

        public String getData(String key) {
            return wrapped.getData(key).toUpperCase();
        }
    }

    // ── MAIN ───────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("═══ DECORATOR PATTERN DEMO ═══\n");

        // 1. Plain service
        System.out.println("1. Basic Service (no decoration):");
        DataService basic = new BasicDataService();
        System.out.println("  Result: " + basic.getData("user.name"));

        // 2. With logging
        System.out.println("\n2. + Logging Decorator:");
        DataService logged = new LoggingDecorator(new BasicDataService());
        logged.getData("user.name");

        // 3. Full stack: logging + caching + uppercase
        System.out.println("\n3. Full Stack (Logging → Caching → UpperCase → Basic):");
        DataService fullStack = new LoggingDecorator(
                                  new CachingDecorator(
                                    new UpperCaseDecorator(
                                      new BasicDataService())));
        fullStack.getData("user.name");  // cache miss
        System.out.println();
        fullStack.getData("user.name");  // cache hit!

        // 4. Java I/O — real-world Decorator
        System.out.println("\n4. Java I/O Decorators:");
        System.out.println("  new DataInputStream(");
        System.out.println("    new BufferedInputStream(");
        System.out.println("      new FileInputStream(\"data.bin\")))");
        System.out.println("  Each layer adds: typed reads → buffering → file access");

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Decorator adds behavior DYNAMICALLY via wrapping.");
        System.out.println("Each decorator follows Single Responsibility.");
        System.out.println("Java I/O streams are the classic Decorator example.");
    }
}
