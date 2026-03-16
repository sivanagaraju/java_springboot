package exercises;

import java.util.HashMap;
import java.util.Map;

/**
 * EXERCISE 2: Simulating @Conditional Auto-Configuration
 * 
 * TASK:
 * 1. Simulating Spring Boot, read the conceptual "application.properties" map.
 * 2. Look exactly for the key "database.type".
 * 3. Write an exact IF statement simulating "@ConditionalOnProperty(name='database.type', havingValue='mysql')".
 * 4. IF the property is exactly "mysql", return the `MySQLDatabase`.
 * 5. ELSE (simulating @ConditionalOnMissingBean or fallback), return the `InMemH2Database`.
 */
public class Ex02_CustomCondition {

    public static void main(String[] args) {
        
        Map<String, String> applicationProperties = new HashMap<>();
        // User explicitly defines the property:
        applicationProperties.put("database.type", "mysql");

        System.out.println("--- Booting Application (Simulating Spring Run) ---");
        
        Database activeDatabase = autoConfigureDatabase(applicationProperties);
        activeDatabase.connect();
    }

    // This method perfectly physically mimics the massive internal engine of Spring Boot Auto-Configuration
    public static Database autoConfigureDatabase(Map<String, String> env) {
        
        // TODO: 3. Read the property
        String dbType = env.get("database.type");

        // TODO: 4 & 5. Write the condition rules
        if ("mysql".equalsIgnoreCase(dbType)) {
            System.out.println("[Auto-Config]: Condition Match for MySQL!");
            return new MySQLDatabase();
        } else {
            System.out.println("[Auto-Config]: Fallback Condition. Defaulting to H2 In-Memory DB...");
            return new InMemH2Database();
        }
    }

    // --- Structural Interfaces ---
    public interface Database {
        void connect();
    }

    // --- Concrete Beans ---
    public static class MySQLDatabase implements Database {
        public void connect() {
            System.out.println("Connecting to Remote MySQL via TCP/IP 3306...");
        }
    }

    public static class InMemH2Database implements Database {
        public void connect() {
            System.out.println("Building Fast In-Memory Database for Development...");
        }
    }
}
