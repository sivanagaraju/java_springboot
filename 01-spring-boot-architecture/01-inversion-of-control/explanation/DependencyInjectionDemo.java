package explanation;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: DependencyInjectionDemo.java                                                       ║
 * ║ PURPOSE & CONCEPT: Types of Dependency Injection                                         ║
 * ║ Demonstrates why Constructor Injection is vastly superior to Field Injection natively.   ║
 * ║ Shows how field injection can lead to NullPointerExceptions if not managed carefully.    ║
 * ║                                                                                          ║
 * ║ ┌──────────────────────────────────────────────────────────────────────────────────────┐ ║
 * ║ │ ASCII DIAGRAM: Constructor vs Field Injection                                        │ ║
 * ║ ├──────────────────────────────────────────────────────────────────────────────────────┤ ║
 * ║ │   FIELD INJECTION (Bad)           CONSTRUCTOR INJECTION (Good)                       │ ║
 * ║ │   ┌────────────────┐              ┌────────────────┐                                 │ ║
 * ║ │   │  Service       │              │  Service       │                                 │ ║
 * ║ │   │  @Autowired    │              │  db (final)    │                                 │ ║
 * ║ │   │  Database db;  │              └───────▲────────┘                                 │ ║
 * ║ │   └──────▲─────────┘                      │                                          │ ║
 * ║ │          │ (Magic)                        │ (JVM enforces at compilation)            │ ║
 * ║ │   ┌──────┴─────────┐              ┌───────┴────────┐                                 │ ║
 * ║ │   │ Spring Context │              │ JVM / Compiler │                                 │ ║
 * ║ │   └────────────────┘              └────────────────┘                                 │ ║
 * ║ └──────────────────────────────────────────────────────────────────────────────────────┘ ║
 * ║                                                                                          ║
 * ║ PYTHON COMPARE:                                                                          ║
 * ║ Field Injection is like randomly setting an attribute from outside without `__init__`.   ║
 * ║                                                                                          ║
 * ║ HOW TO RUN: ./gradlew :01-spring-boot-architecture:run --args="DependencyInjectionDemo"  ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 */
public class DependencyInjectionDemo {

    public static void main(String[] args) {
        // --- Field Injection Failure ---
        System.out.println("--- Field Injection Failure ---");
        var fieldService = new FieldInjectionService();
        try {
            fieldService.doWork();
        } catch (NullPointerException e) {
            System.out.println("CRASH! The database was never injected because the field is private!");
        }

        // --- Constructor Injection Success ---
        System.out.println("\n--- Constructor Injection Success ---");
        var realDb = new DatabaseConnection();
        
        // The Java Compiler physically FORCES us to provide the DatabaseConnection.
        // It is mathematically impossible to instantiate ConstructorInjectionService in a broken state.
        var constructorService = new ConstructorInjectionService(realDb);
        constructorService.doWork();
    }

    public static class DatabaseConnection {
        public void connect() {
            System.out.println("Database attached successfully.");
        }
    }

    // --- ANTI-PATTERN: Field Injection ---
    public static class FieldInjectionService {
        
        // In a real Spring App, this would have @Autowired
        // But outside Spring (like in a fast JUnit test), it's just a raw null object.
        private DatabaseConnection db;

        public void doWork() {
            db.connect(); // Throws NullPointerException if not injected.
        }
    }

    // --- BEST PRACTICE: Constructor Injection ---
    public static class ConstructorInjectionService {
        
        // Final ensures it cannot be nullified after creation.
        private final DatabaseConnection db;

        // In modern Spring, if a class has exactly one constructor, 
        // @Autowired is completely optional and implied.
        public ConstructorInjectionService(DatabaseConnection db) {
            this.db = db;
        }

        public void doWork() {
            db.connect(); // Guaranteed to work.
        }
    }
}
