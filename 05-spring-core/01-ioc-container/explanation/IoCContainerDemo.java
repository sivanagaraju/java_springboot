/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : IoCContainerDemo.java                                  ║
 * ║  MODULE : 05-spring-core / 01-ioc-container                      ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates IoC container creating and         ║
 * ║                   managing bean instances automatically           ║
 * ║  WHY IT EXISTS  : Without IoC, you manually wire dependencies    ║
 * ║                   with new A(new B(new C())) — rigid coupling    ║
 * ║  PYTHON COMPARE : Python: obj = MyClass(dep1, dep2) — manual    ║
 * ║                   Spring: container creates and wires everything ║
 * ║  USE CASES      : 1) Service layer with repository dependency   ║
 * ║                   2) Multi-implementation strategy pattern       ║
 * ║                   3) Configuration-driven bean creation          ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — IoC Container Flow                               ║
 * ║                                                                    ║
 * ║    @SpringBootApplication                                          ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    [ ApplicationContext ]  ← IoC Container                        ║
 * ║        │                                                           ║
 * ║        ├── scan @Component classes                                 ║
 * ║        ├── create BeanDefinitions                                  ║
 * ║        ├── instantiate beans                                       ║
 * ║        ├── inject dependencies                                     ║
 * ║        ▼                                                           ║
 * ║    [ Fully Wired Application ]                                     ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Bean creation messages, greeting output          ║
 * ║  RELATED FILES  : ApplicationContextDemo.java, 01-what-is-ioc.md ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.ioc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * Demonstrates the IoC container creating and wiring beans automatically.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python — manual wiring
 *   repo = UserRepository()
 *   service = UserService(repo)
 *   greeting = service.greet("Sivan")
 *
 *   # Java/Spring — container wires automatically
 *   # Just annotate classes, Spring does the rest
 * </pre>
 *
 * <p><b>ASCII — Without vs With IoC:</b>
 * <pre>
 *   WITHOUT IoC:                    WITH IoC:
 *   ─────────────────              ──────────────────
 *   new ServiceA(                  @Service ServiceA
 *     new ServiceB(                @Service ServiceB
 *       new RepoC()                @Repository RepoC
 *     )                            → Container wires all
 *   )
 * </pre>
 */
@SpringBootApplication
public class IoCContainerDemo {

    public static void main(String[] args) {
        SpringApplication.run(IoCContainerDemo.class, args);
    }

    /**
     * CommandLineRunner executes after all beans are wired.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   if __name__ == "__main__":
     *       main()
     * </pre>
     *
     * @param greeter auto-injected by Spring (IoC in action!)
     * @return a CommandLineRunner that demonstrates IoC
     */
    @Bean
    CommandLineRunner demo(GreeterService greeter) {
        return args -> {
            System.out.println("=== IoC Container Demo ===");
            System.out.println();

            // Without IoC, you'd write: new GreeterService(new GreeterRepository())
            // With IoC, Spring does it for you — just declare the dependency
            String greeting = greeter.greet("Spring Learner");
            System.out.println(greeting);
            System.out.println();

            System.out.println("Key insight: We NEVER called 'new GreeterService()'");
            System.out.println("The IoC container created it, wired GreeterRepository, and injected here.");
        };
    }
}

// --- Supporting classes (in production, these would be separate files) ---

/**
 * Simple repository simulating data access.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class GreeterRepository:
 *       def find_greeting(self, name: str) -> str:
 *           return f"Hello, {name}!"
 * </pre>
 */
@Service
class GreeterRepository {

    /**
     * Returns a greeting message for the given name.
     *
     * @param name the person to greet
     * @return formatted greeting string
     */
    public String findGreeting(String name) {
        return "Hello, " + name + "! Welcome to Spring IoC.";
    }
}

/**
 * Service that uses GreeterRepository — injected by IoC container.
 *
 * <p><b>ASCII — Dependency flow:</b>
 * <pre>
 *   CommandLineRunner
 *       │
 *       ▼
 *   [ GreeterService ]  ← injected by container
 *       │
 *       ▼
 *   [ GreeterRepository ]  ← injected by container
 * </pre>
 */
@Service
class GreeterService {

    private final GreeterRepository repository;

    /**
     * Constructor injection — Spring auto-detects single constructor.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   def __init__(self, repo: GreeterRepository):
     *       self.repo = repo
     * </pre>
     *
     * @param repository auto-injected by Spring IoC
     */
    public GreeterService(GreeterRepository repository) {
        this.repository = repository;
        System.out.println("  → GreeterService created by IoC container");
    }

    /**
     * Greets a person using the repository.
     *
     * @param name the person to greet
     * @return the greeting message
     */
    public String greet(String name) {
        return repository.findGreeting(name);
    }
}
