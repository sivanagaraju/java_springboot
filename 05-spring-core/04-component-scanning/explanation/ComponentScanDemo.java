/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ComponentScanDemo.java                                 ║
 * ║  MODULE : 05-spring-core / 04-component-scanning                  ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Shows how @ComponentScan auto-discovers beans  ║
 * ║                   and how stereotype annotations classify them   ║
 * ║  WHY IT EXISTS  : Without scanning, you'd register every bean   ║
 * ║                   manually — hundreds of lines of XML config     ║
 * ║  PYTHON COMPARE : Python: import + instantiate manually         ║
 * ║                   Spring: annotate + auto-discovered             ║
 * ║  USE CASES      : 1) Auto-detecting services and repos          ║
 * ║                   2) Custom package scanning                      ║
 * ║                   3) Excluding test configurations                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Scanning Process                                 ║
 * ║                                                                    ║
 * ║    @SpringBootApplication (com.learning.springcore)               ║
 * ║        │                                                           ║
 * ║        ▼ @ComponentScan                                           ║
 * ║    ┌─────────────────────────────┐                                 ║
 * ║    │ Scan sub-packages:          │                                 ║
 * ║    │  ├── .service/  → @Service  │                                 ║
 * ║    │  ├── .repo/     → @Repos    │                                 ║
 * ║    │  └── .config/   → @Config   │                                 ║
 * ║    └─────────────────────────────┘                                 ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: List of all discovered beans by stereotype     ║
 * ║  RELATED FILES  : ConfigurationDemo.java, ConditionalBeanDemo    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.scanning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Demonstrates automatic component scanning and stereotype classification.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python — no scanning, must import everything manually
 *   from services.product_service import ProductService
 *   from repos.product_repo import ProductRepo
 *   service = ProductService(ProductRepo())
 *
 *   # Spring — just annotate with @Service/@Repository
 *   # ComponentScan finds them all automatically
 * </pre>
 */
@Component
class ComponentScanRunner implements CommandLineRunner {

    private final ApplicationContext context;

    public ComponentScanRunner(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Component Scan Demo ===");
        System.out.println();

        // Show total beans
        System.out.println("Total beans: " + context.getBeanDefinitionCount());

        // Show our custom beans
        System.out.println("\nCustom beans discovered by @ComponentScan:");
        for (String name : context.getBeanDefinitionNames()) {
            if (name.startsWith("com.learning") || name.contains("Scanner") ||
                    name.contains("Product") || name.contains("Order")) {
                Object bean = context.getBean(name);
                System.out.println("  " + name + " → " + bean.getClass().getSimpleName());
            }
        }
    }
}

// --- Beans that will be auto-discovered by @ComponentScan ---

@Service
class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public String findProduct(Long id) {
        return repo.findById(id);
    }
}

@Repository
class ProductRepository {
    public String findById(Long id) {
        return "Product #" + id;
    }
}

@Service
class OrderService {
    public String createOrder(String product) {
        return "Order created for: " + product;
    }
}
