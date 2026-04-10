/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ApplicationContextDemo.java                            ║
 * ║  MODULE : 05-spring-core / 01-ioc-container                      ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Explores ApplicationContext features beyond     ║
 * ║                   basic bean creation — events, environment,      ║
 * ║                   resource loading                                ║
 * ║  WHY IT EXISTS  : ApplicationContext is the full-featured IoC    ║
 * ║                   container — understanding its capabilities      ║
 * ║                   is essential for Spring mastery                 ║
 * ║  PYTHON COMPARE : No Python equivalent — ApplicationContext      ║
 * ║                   = DI container + event bus + config loader     ║
 * ║  USE CASES      : 1) Programmatic bean lookup                    ║
 * ║                   2) Environment and profile inspection          ║
 * ║                   3) Event publishing                             ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — ApplicationContext Features                      ║
 * ║                                                                    ║
 * ║    ApplicationContext                                              ║
 * ║        │                                                           ║
 * ║        ├── BeanFactory      → create/get beans                    ║
 * ║        ├── EventPublisher   → publish application events          ║
 * ║        ├── ResourceLoader   → load files/URLs                     ║
 * ║        ├── MessageSource    → i18n messages                       ║
 * ║        └── Environment      → profiles + properties               ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Context info, bean count, profiles, events      ║
 * ║  RELATED FILES  : IoCContainerDemo.java, 02-beanfactory.md       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.ioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Demonstrates ApplicationContext capabilities beyond basic DI.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python has no single equivalent — you'd need:
 *   # - A DI container (e.g., dependency-injector)
 *   # - An event system (e.g., blinker)
 *   # - A config loader (e.g., python-decouple)
 *   # Spring combines ALL of these in ApplicationContext
 * </pre>
 *
 * <p><b>ASCII — ApplicationContext is the central hub:</b>
 * <pre>
 *   Your Code ──────► ApplicationContext ──────► Beans
 *                         │                        │
 *                         ├── getBean()            ├── @Service
 *                         ├── publishEvent()       ├── @Repository
 *                         ├── getEnvironment()     ├── @Controller
 *                         └── getResource()        └── @Configuration
 * </pre>
 */
@Component
public class ApplicationContextDemo implements CommandLineRunner {

    private final ApplicationContext context;
    private final Environment environment;

    /**
     * Constructor injection of ApplicationContext itself.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   def __init__(self, app: FastAPI):
     *       self.app = app  # no equivalent — FastAPI doesn't expose container
     * </pre>
     *
     * @param context the Spring ApplicationContext
     * @param environment the Spring Environment (profiles + properties)
     */
    public ApplicationContextDemo(ApplicationContext context, Environment environment) {
        this.context = context;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== ApplicationContext Demo ===");
        System.out.println();

        // 1. Bean count — how many beans Spring created
        int beanCount = context.getBeanDefinitionCount();
        System.out.println("Total beans in context: " + beanCount);

        // 2. Active profiles
        String[] profiles = environment.getActiveProfiles();
        System.out.println("Active profiles: " +
                (profiles.length == 0 ? "default" : String.join(", ", profiles)));

        // 3. Programmatic bean lookup (avoid in production — use DI instead)
        String[] beanNames = context.getBeanNamesForType(GreeterService.class);
        System.out.println("GreeterService beans: " + String.join(", ", beanNames));

        // 4. Environment properties
        String javaVersion = environment.getProperty("java.version");
        System.out.println("Java version: " + javaVersion);

        System.out.println();
        System.out.println("Key insight: ApplicationContext = BeanFactory + EventPublisher");
        System.out.println("  + ResourceLoader + MessageSource + Environment");
    }
}
