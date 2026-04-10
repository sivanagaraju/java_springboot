/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : SpringCoreApplication.java                              ║
 * ║  MODULE : 05-spring-core                                          ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Single entry point for the Spring Core module  ║
 * ║  WHY IT EXISTS  : Spring Boot needs exactly ONE                   ║
 * ║                   @SpringBootApplication class per runnable module║
 * ║  PYTHON COMPARE : if __name__ == "__main__": uvicorn.run(app)    ║
 * ║                   → Spring: SpringApplication.run(this.class)     ║
 * ║  USE CASES      : Running all demos via bootRun                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Spring Boot Startup                              ║
 * ║                                                                    ║
 * ║    ./gradlew :05-spring-core:bootRun                              ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    JVM starts → SpringApplication.run()                           ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    ApplicationContext created                                      ║
 * ║        │                                                           ║
 * ║        ├── @ComponentScan finds @Service, @Component, @Repository ║
 * ║        ├── @Configuration @Bean methods called                    ║
 * ║        ├── Dependencies injected (constructor first)              ║
 * ║        ├── @PostConstruct on each bean                            ║
 * ║        ▼                                                           ║
 * ║    ApplicationReadyEvent → CommandLineRunners execute             ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: All demo runners execute sequentially           ║
 * ║  RELATED FILES  : application.yml, build.gradle                   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Core learning module.
 *
 * <p>This class bootstraps the entire Spring IoC container and executes
 * all registered {@code CommandLineRunner} beans in demo classes.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python FastAPI
 *   if __name__ == "__main__":
 *       uvicorn.run("main:app", reload=True)
 *
 *   # Spring Boot
 *   SpringApplication.run(SpringCoreApplication.class, args)
 *   // One line bootstraps: IoC container + DI + lifecycle + embedded server
 * </pre>
 *
 * <p><b>ASCII — What this class triggers:</b>
 * <pre>
 *   SpringCoreApplication.main()
 *       │
 *       ▼
 *   SpringApplication.run()
 *       │
 *       ├── Create ApplicationContext
 *       ├── Scan com.learning.springcore.** for @Component
 *       ├── Wire all dependencies
 *       ├── Run @PostConstruct methods
 *       ├── Fire ApplicationReadyEvent
 *       └── Execute all CommandLineRunner beans
 * </pre>
 */
@SpringBootApplication
public class SpringCoreApplication {

    /**
     * JVM entry point — delegates to Spring Boot.
     *
     * @param args command-line arguments (e.g., --spring.profiles.active=dev)
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringCoreApplication.class, args);
    }
}
