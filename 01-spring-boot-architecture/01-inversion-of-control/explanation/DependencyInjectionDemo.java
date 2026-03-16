package explanation;

/**
 * While Spring Boot automates this, this class demonstrates
 * the actual manual mechanical difference between Field and Constructor Injection.
 */
public class DependencyInjectionDemo {

    public static void main(String[] args) {
        System.out.println("--- Field Injection Failure ---");
        FieldInjectionService fieldService = new FieldInjectionService();
        try {
            fieldService.doWork();
        } catch (NullPointerException e) {
            System.out.println("CRASH! The database was never injected because the field is private!");
        }

        System.out.println("\n--- Constructor Injection Success ---");
        DatabaseConnection realDb = new DatabaseConnection();
        
        // The Java Compiler physically FORCES us to provide the DatabaseConnection.
        // It is mathematically impossible to instantiate ConstructorInjectionService in a broken state.
        ConstructorInjectionService constructorService = new ConstructorInjectionService(realDb);
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
