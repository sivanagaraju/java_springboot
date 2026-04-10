/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex02_AnnotationConfig.java                             ║
 * ║  MODULE : 05-spring-core / 01-ioc-container / exercises           ║
 * ║  GRADLE : ./gradlew :05-spring-core:test                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — convert manual wiring to Spring     ║
 * ║                   annotation-based configuration                  ║
 * ║  WHY IT EXISTS  : Builds muscle memory for @Service, @Component,║
 * ║                   @Autowired annotation placement                 ║
 * ║  PYTHON COMPARE : Python: no annotations needed for DI          ║
 * ║                   Java: each class needs a stereotype annotation ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE INSTRUCTIONS:                                           ║
 * ║  1. Read the manually-wired code below                            ║
 * ║  2. Add appropriate annotations to make Spring manage the beans  ║
 * ║  3. Remove the manual wiring code                                 ║
 * ║  4. Verify Spring auto-discovers and wires all dependencies      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.exercises;

/*
 * === EXERCISE: Add Spring annotations to these classes ===
 *
 * Currently: manually wired with 'new' keywords
 * Goal: Spring auto-discovers and wires via annotations
 *
 * Steps:
 * 1. Add @Repository to ProductRepository
 * 2. Add @Service to ProductService
 * 3. Add @Component to ProductController (or @RestController for web)
 * 4. Remove all 'new' calls — Spring handles object creation
 * 5. Ensure constructor injection (single constructor, no @Autowired needed)
 */

// TODO: Add @Repository
class ProductRepository {
    public String findById(Long id) {
        return "Product #" + id + " — Spring Core Learning Guide";
    }

    public java.util.List<String> findAll() {
        return java.util.List.of("Product #1", "Product #2", "Product #3");
    }
}

// TODO: Add @Service
class ProductService {
    private final ProductRepository repo;

    // TODO: This constructor should be auto-detected by Spring
    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public String getProduct(Long id) {
        return repo.findById(id);
    }

    public java.util.List<String> getAllProducts() {
        return repo.findAll();
    }
}

// TODO: Add @Component (or @RestController if making it a web endpoint)
class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    public void displayProduct(Long id) {
        System.out.println(service.getProduct(id));
    }

    public void displayAll() {
        service.getAllProducts().forEach(System.out::println);
    }
}

/**
 * Main class — currently manually wires everything.
 * After adding annotations, Spring Boot would handle all wiring.
 */
public class Ex02_AnnotationConfig {

    public static void main(String[] args) {
        // ❌ Manual wiring (remove this after adding annotations)
        ProductRepository repo = new ProductRepository();
        ProductService service = new ProductService(repo);
        ProductController controller = new ProductController(service);

        controller.displayProduct(1L);
        controller.displayAll();

        System.out.println();
        System.out.println("TODO: Replace manual wiring with Spring annotations");
        System.out.println("  1. @Repository on ProductRepository");
        System.out.println("  2. @Service on ProductService");
        System.out.println("  3. @Component on ProductController");
        System.out.println("  4. Delete all 'new' calls above");
        System.out.println("  5. Run with: ./gradlew :05-spring-core:bootRun");
    }
}
