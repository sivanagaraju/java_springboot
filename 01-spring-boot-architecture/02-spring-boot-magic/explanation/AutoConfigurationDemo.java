package explanation;

/**
 * Demonstrates how Spring Boot Auto-Configuration acts mechanically.
 * While Spring Boot scans your classpath and application.properties inside its massive engine, 
 * this simulates exactly what its internal '@Conditional' classes look like natively.
 */
public class AutoConfigurationDemo {

    public static void main(String[] args) {
        System.out.println("--- Spring Boot Auto-Config Simulator ---");
        
        // Imagine: spring.datasource.url=jdbc:mysql://...
        boolean hasDatabaseUrlProperty = true; 
        
        // Imagine: Dependencies include spring-boot-starter-web
        boolean hasTomcatLibrary = true;

        System.out.println("Scanning Classpath and Properties...\n");

        if (hasTomcatLibrary) {
            System.out.println("[Auto-Configured]: Tomcat Web Server started on port 8080.");
        }

        if (hasDatabaseUrlProperty) {
            System.out.println("[Auto-Configured]: HikariCP Connection Pool linked to database url.");
        } else {
            System.out.println("[Auto-Configured]: No DB URL found. Standing up in-memory H2 database.");
        }
        
        System.out.println("\nApplication is ready to accept requests!");
    }
}
