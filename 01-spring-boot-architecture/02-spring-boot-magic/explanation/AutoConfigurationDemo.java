package explanation;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: AutoConfigurationDemo.java                                                         ║
 * ║ PURPOSE & CONCEPT: Spring Boot Auto-Configuration mechanism                              ║
 * ║ Demonstrates how Spring Boot Auto-Configuration acts mechanically.                       ║
 * ║ While Spring Boot scans your classpath and application.properties inside its massive     ║
 * ║ engine, this simulates exactly what its internal '@Conditional' classes look like.       ║
 * ║                                                                                          ║
 * ║ ┌──────────────────────────────────────────────────────────────────────────────────────┐ ║
 * ║ │ ASCII DIAGRAM: Auto-Configuration Engine                                             │ ║
 * ║ ├──────────────────────────────────────────────────────────────────────────────────────┤ ║
 * ║ │ [Classpath/Properties] ──(Scanned by)──► [@EnableAuto..]                             │ ║
 * ║ │        │                                                                             │ ║
 * ║ │        ├──► [Tomcat AutoConfig] (Condition: Web = YES)                               │ ║
 * ║ │        └──► [Hikari AutoConfig] (Condition: URL = YES)                               │ ║
 * ║ └──────────────────────────────────────────────────────────────────────────────────────┘ ║
 * ║                                                                                          ║
 * ║ PYTHON COMPARE:                                                                          ║
 * ║ Like a setup script that checks `import sys` modules and environment variables to        ║
 * ║ decide what to wire up automatically (e.g., binding to Flask if installed).              ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 */
public class AutoConfigurationDemo {

    public static void main(String[] args) {
        System.out.println("--- Spring Boot Auto-Config Simulator ---");
        
        // Imagine: spring.datasource.url=jdbc:mysql://... using var
        var hasDatabaseUrlProperty = true; 
        
        // Imagine: Dependencies include spring-boot-starter-web
        var hasTomcatLibrary = true;

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
