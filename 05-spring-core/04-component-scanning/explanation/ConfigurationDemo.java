/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ConfigurationDemo.java                                 ║
 * ║  MODULE : 05-spring-core / 04-component-scanning                  ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Shows @Configuration + @Bean for creating      ║
 * ║                   beans from third-party libraries               ║
 * ║  WHY IT EXISTS  : You can't put @Component on classes you don't ║
 * ║                   own (RestTemplate, ObjectMapper, DataSource)   ║
 * ║  PYTHON COMPARE : Python: factory function returning configured  ║
 * ║                   object → Java: @Bean method in @Configuration ║
 * ║  USE CASES      : 1) RestTemplate bean with timeouts            ║
 * ║                   2) ObjectMapper with custom modules             ║
 * ║                   3) DataSource with connection pool settings    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — @Bean Registration Flow                         ║
 * ║                                                                    ║
 * ║    @Configuration class                                            ║
 * ║        │                                                           ║
 * ║        ├── @Bean objectMapper()  → ObjectMapper bean              ║
 * ║        ├── @Bean restTemplate()  → RestTemplate bean              ║
 * ║        └── @Bean dataSource()    → DataSource bean                ║
 * ║                                                                    ║
 * ║    Each @Bean method:                                              ║
 * ║      1. Spring calls the method                                   ║
 * ║      2. Return value registered as bean                            ║
 * ║      3. Method name = bean name                                   ║
 * ║      4. Parameters auto-injected from other beans                 ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Custom-configured beans in action              ║
 * ║  RELATED FILES  : ComponentScanDemo.java, 03-configuration.md   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.scanning;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @Configuration class for beans that can't be auto-scanned.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python factory functions
 *   def create_json_serializer():
 *       return JsonSerializer(indent=2, sort_keys=True)
 *
 *   def create_clock():
 *       return Clock()   # from datetime module
 * </pre>
 *
 * <p><b>ASCII — Full mode vs Lite mode:</b>
 * <pre>
 *   @Configuration (Full mode):    @Component (Lite mode):
 *   ──────────────────────────    ─────────────────────────
 *   CGLIB proxy created            No proxy
 *   Inter-bean refs → SAME bean    Inter-bean refs → NEW object!
 *   Always use @Configuration      Avoid @Bean in @Component
 * </pre>
 */
@Configuration
class AppConfig {

    /**
     * Creates a Clock bean — useful for testable time-dependent code.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   clock = Clock()  # or mock_clock for testing
     * </pre>
     *
     * @return system UTC clock
     */
    @Bean
    public Clock clock() {
        System.out.println("  → @Bean: Clock created (UTC)");
        return Clock.systemUTC();
    }

    /**
     * Creates a configured DateTimeFormatter bean.
     *
     * @return formatter for ISO date-time with locale
     */
    @Bean
    public DateTimeFormatter dateFormatter() {
        System.out.println("  → @Bean: DateTimeFormatter created");
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
    }

    /**
     * Creates a bean that depends on another @Bean method.
     * In @Configuration (Full mode), calling clock() returns the SAME managed bean.
     *
     * @return a time service using the Clock bean
     */
    @Bean
    public TimeService timeService() {
        // clock() returns the MANAGED bean — NOT a new Clock instance
        // This works because @Configuration creates a CGLIB proxy
        return new TimeService(clock(), dateFormatter());
    }
}

/**
 * A service that uses injected Clock and Formatter — demonstrates inter-bean refs.
 */
class TimeService {
    private final Clock clock;
    private final DateTimeFormatter formatter;

    public TimeService(Clock clock, DateTimeFormatter formatter) {
        this.clock = clock;
        this.formatter = formatter;
        System.out.println("  → TimeService created with Clock and Formatter");
    }

    public String currentTime() {
        return formatter.format(java.time.LocalDateTime.now(clock));
    }
}
