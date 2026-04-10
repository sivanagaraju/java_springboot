/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_CustomStereotype.java                             ║
 * ║  MODULE : 05-spring-core / 04-component-scanning / exercises      ║
 * ║  GRADLE : ./gradlew :05-spring-core:test                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — create a custom stereotype          ║
 * ║                   annotation that combines @Component + logging  ║
 * ║  WHY IT EXISTS  : Custom stereotypes reduce boilerplate and      ║
 * ║                   enforce consistent patterns across the team    ║
 * ║  PYTHON COMPARE : Python: class decorator @my_service            ║
 * ║                   Java: Meta-annotation combining stereotypes    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE INSTRUCTIONS:                                           ║
 * ║  1. Create a @TrackedService annotation                           ║
 * ║  2. Meta-annotate it with @Service (so Spring auto-detects)      ║
 * ║  3. Create a BeanPostProcessor that logs beans with @Tracked     ║
 * ║  4. Apply @TrackedService to a test class                         ║
 * ║  5. Verify the bean is discovered AND logged                      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.exercises;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Exercise: Create a custom stereotype annotation.
 *
 * <p><b>ASCII — Custom stereotype is a meta-annotation:</b>
 * <pre>
 *   @TrackedService          (custom)
 *       │
 *       └── @Service         (Spring stereotype)
 *               │
 *               └── @Component   (base Spring bean marker)
 * </pre>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   def tracked_service(cls):
 *       """Custom decorator that registers + logs the class"""
 *       print(f"Tracking: {cls.__name__}")
 *       return cls
 *
 *   @tracked_service
 *   class MyService: ...
 * </pre>
 */
public class Ex01_CustomStereotype {

    // TODO: Step 1 — Create @TrackedService annotation
    // @Target(ElementType.TYPE)
    // @Retention(RetentionPolicy.RUNTIME)
    // @Service  // ← meta-annotated: Spring auto-detects @TrackedService classes
    // public @interface TrackedService {
    //     String value() default "";
    // }

    // TODO: Step 2 — Apply it to a service
    // @TrackedService
    // class InventoryService {
    //     public int checkStock(String productId) {
    //         return 42;
    //     }
    // }

    // TODO: Step 3 — Create a BeanPostProcessor that logs @TrackedService beans
    // @Component
    // class TrackingPostProcessor implements BeanPostProcessor {
    //     @Override
    //     public Object postProcessAfterInitialization(Object bean, String name) {
    //         if (bean.getClass().isAnnotationPresent(TrackedService.class)) {
    //             System.out.println("📍 Tracked: " + name + " (" + bean.getClass().getSimpleName() + ")");
    //         }
    //         return bean;
    //     }
    // }

    public static void main(String[] args) {
        System.out.println("=== Custom Stereotype Exercise ===");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Create @TrackedService annotation (meta-annotated with @Service)");
        System.out.println("  2. Apply to InventoryService");
        System.out.println("  3. Create BPP that logs all @TrackedService beans");
        System.out.println("  4. Run and verify: \"📍 Tracked: inventoryService\"");
        System.out.println();
        System.out.println("Key insight: Your custom annotation COMBINES @Service + tracking.");
        System.out.println("Any class with @TrackedService is auto-detected AND logged.");
    }
}
