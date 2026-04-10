/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : BeanLifecycleDemo.java                                 ║
 * ║  MODULE : 05-spring-core / 03-bean-lifecycle                      ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Shows the complete bean lifecycle with all      ║
 * ║                   callback hooks logging their execution order    ║
 * ║  WHY IT EXISTS  : Understanding lifecycle is essential for        ║
 * ║                   debugging Spring timing issues and resource     ║
 * ║                   management                                      ║
 * ║  PYTHON COMPARE : Python: __init__ + __del__ (2 hooks)           ║
 * ║                   Java: 12 lifecycle phases with hooks            ║
 * ║  USE CASES      : 1) Database connection pool init/cleanup       ║
 * ║                   2) Cache warmup at startup                      ║
 * ║                   3) Graceful shutdown with resource release       ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Bean Lifecycle Phases                            ║
 * ║                                                                    ║
 * ║    1. Constructor           ← object created                      ║
 * ║    2. Dependencies injected ← setters called                      ║
 * ║    3. @PostConstruct        ← init resources                      ║
 * ║    4. afterPropertiesSet()  ← Spring callback                     ║
 * ║    5. Custom init           ← @Bean(initMethod)                   ║
 * ║    ──────── BEAN READY ────────                                   ║
 * ║    6. @PreDestroy           ← cleanup resources                   ║
 * ║    7. destroy()             ← Spring callback                     ║
 * ║    8. Custom destroy        ← @Bean(destroyMethod)                ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Numbered lifecycle phase messages in order      ║
 * ║  RELATED FILES  : BeanScopeDemo.java, BeanPostProcessorDemo.java║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

/**
 * A bean that implements ALL lifecycle callbacks to demonstrate execution order.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class LifecycleBean:
 *       def __init__(self):          # constructor
 *           print("constructed")
 *       def __post_init__(self):     # ~ @PostConstruct (dataclass only)
 *           print("initialized")
 *       def __del__(self):           # ~ @PreDestroy (unreliable!)
 *           print("destroyed")
 * </pre>
 *
 * <p><b>ASCII — Callback execution order:</b>
 * <pre>
 *   Constructor
 *       │
 *       ▼
 *   @PostConstruct
 *       │
 *       ▼
 *   InitializingBean.afterPropertiesSet()
 *       │
 *       ▼
 *   ─── BEAN READY ───
 *       │
 *       ▼
 *   @PreDestroy
 *       │
 *       ▼
 *   DisposableBean.destroy()
 * </pre>
 */
@Service
class LifecycleBean implements InitializingBean, DisposableBean {

    private String resourceName;

    /**
     * Phase 1: Constructor — object instantiation.
     */
    public LifecycleBean() {
        System.out.println("  [1] Constructor called — bean instantiated");
        this.resourceName = "DatabaseConnection";
    }

    /**
     * Phase 2: @PostConstruct — runs after injection, before use.
     * Use for: validation, cache warmup, resource initialization.
     */
    @PostConstruct
    public void postConstruct() {
        System.out.println("  [2] @PostConstruct — initializing " + resourceName);
    }

    /**
     * Phase 3: InitializingBean — Spring-specific callback.
     * Prefer @PostConstruct unless you need Spring-specific behavior.
     */
    @Override
    public void afterPropertiesSet() {
        System.out.println("  [3] InitializingBean.afterPropertiesSet()");
    }

    // ─── BEAN IS NOW READY FOR USE ───

    /**
     * Phase 4: @PreDestroy — runs during graceful shutdown.
     * Use for: closing connections, flushing caches.
     */
    @PreDestroy
    public void preDestroy() {
        System.out.println("  [4] @PreDestroy — releasing " + resourceName);
    }

    /**
     * Phase 5: DisposableBean — Spring-specific destroy callback.
     */
    @Override
    public void destroy() {
        System.out.println("  [5] DisposableBean.destroy()");
    }

    public String getResourceName() {
        return resourceName;
    }
}
