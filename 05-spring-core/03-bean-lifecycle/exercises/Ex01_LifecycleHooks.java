/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_LifecycleHooks.java                               ║
 * ║  MODULE : 05-spring-core / 03-bean-lifecycle / exercises          ║
 * ║  GRADLE : ./gradlew :05-spring-core:test                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — implement ALL lifecycle callbacks   ║
 * ║                   and verify their execution order                ║
 * ║  WHY IT EXISTS  : Lifecycle ordering is a common interview       ║
 * ║                   question and essential for debugging            ║
 * ║  PYTHON COMPARE : Python: __init__ + __del__ only                ║
 * ║                   Java: 12 phases with multiple hook points      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE INSTRUCTIONS:                                           ║
 * ║  1. Create a ConnectionPool class with all lifecycle callbacks   ║
 * ║  2. Implement: constructor, @PostConstruct, afterPropertiesSet,  ║
 * ║     @PreDestroy, DisposableBean.destroy()                        ║
 * ║  3. Log a numbered message in each callback                       ║
 * ║  4. Run and verify the correct order                              ║
 * ║  5. Add @Scope("prototype") and observe what changes             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.exercises;

/**
 * Exercise: Implement all lifecycle hooks and verify order.
 *
 * <p><b>Expected output order:</b>
 * <pre>
 *   [1] Constructor called
 *   [2] @PostConstruct — pool warming up
 *   [3] InitializingBean.afterPropertiesSet()
 *   ─── BEAN READY ───
 *   (application running)
 *   ─── SHUTDOWN ───
 *   [4] @PreDestroy — draining connections
 *   [5] DisposableBean.destroy() — pool closed
 * </pre>
 *
 * <p><b>Bonus question:</b>
 * What happens to order [4] and [5] if you add @Scope("prototype")?
 * Answer: They never execute! Spring doesn't manage prototype lifecycle.
 */
public class Ex01_LifecycleHooks {

    // TODO: Create ConnectionPool that implements:
    //   1. Constructor → log "[1] Constructor called"
    //   2. @PostConstruct → log "[2] @PostConstruct"
    //   3. InitializingBean.afterPropertiesSet() → log "[3] afterPropertiesSet"
    //   4. @PreDestroy → log "[4] @PreDestroy"
    //   5. DisposableBean.destroy() → log "[5] destroy"

    // TODO: Make it @Service and run with ./gradlew :05-spring-core:bootRun

    // TODO BONUS: Change to @Scope("prototype") and observe what changes

    public static void main(String[] args) {
        System.out.println("=== Lifecycle Hooks Exercise ===");
        System.out.println();
        System.out.println("Expected lifecycle order:");
        System.out.println("  [1] Constructor");
        System.out.println("  [2] @PostConstruct");
        System.out.println("  [3] InitializingBean.afterPropertiesSet()");
        System.out.println("  --- BEAN READY ---");
        System.out.println("  [4] @PreDestroy (on shutdown)");
        System.out.println("  [5] DisposableBean.destroy() (on shutdown)");
        System.out.println();
        System.out.println("BONUS: What happens with @Scope(\"prototype\")?");
        System.out.println("  → @PreDestroy and destroy() are NEVER called!");
        System.out.println("  → Spring doesn't manage prototype beans after creation");
    }
}
