package exercises;

import java.util.HashMap;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Ex02_CustomCondition.java                                                          ║
 * ║ PURPOSE & CONCEPT: Simulating @Conditional Auto-Configuration                            ║
 * ║ Demonstrates building manual condition rules matching property requirements to see       ║
 * ║ exactly how `@Conditional` annotations evaluate before loading a Bean.                   ║
 * ║                                                                                          ║
 * ║ ┌──────────────────────────────────────────────────────────────────────────────────────┐ ║
 * ║ │ ASCII DIAGRAM: @Conditional Evaluation                                               │ ║
 * ║ ├──────────────────────────────────────────────────────────────────────────────────────┤ ║
 * ║ │ [Environment: database.type=mysql]                                                   │ ║
 * ║ │        │                                                                             │ ║
 * ║ │ [Condition Evaluator]                                                                │ ║
 * ║ │        ├──► Match? YES ──► Return MySQLDatabase                                      │ ║
 * ║ │        └──► Match? NO  ──► Return InMemH2Database                                    │ ║
 * ║ └──────────────────────────────────────────────────────────────────────────────────────┘ ║
 * ║                                                                                          ║
 * ║ PYTHON COMPARE:                                                                          ║
 * ║ `if os.getenv('database.type') == 'mysql': db = MySQL() else: db = H2()`                 ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
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
        
        // Simulated environment variables
        
        var applicationProperties = new HashMap<String, String>();
        // User explicitly defines the property:
        applicationProperties.put("database.type", "mysql");

        System.out.println("--- Booting Application (Simulating Spring Run) ---");
        
        var activeDatabase = autoConfigureDatabase(applicationProperties);
        activeDatabase.connect();
    }

    public static Database autoConfigureDatabase(Map<String, String> env) {
        
        // Read the property using var
        var dbType = env.get("database.type");

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
