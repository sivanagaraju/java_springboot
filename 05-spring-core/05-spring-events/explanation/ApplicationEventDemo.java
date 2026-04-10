/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ApplicationEventDemo.java                              ║
 * ║  MODULE : 05-spring-core / 05-spring-events                       ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Listens to built-in Spring lifecycle events    ║
 * ║                   to understand context startup/shutdown flow     ║
 * ║  WHY IT EXISTS  : Knowing WHEN the context is ready vs just      ║
 * ║                   refreshed is critical for production apps       ║
 * ║  PYTHON COMPARE : FastAPI @app.on_event("startup") = limited    ║
 * ║                   Spring has 6+ lifecycle events                  ║
 * ║  USE CASES      : 1) Healthcheck enablement after full startup   ║
 * ║                   2) Cache warmup at the right moment             ║
 * ║                   3) Graceful shutdown logging                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Spring Boot Event Timeline                       ║
 * ║                                                                    ║
 * ║    ApplicationStartingEvent                                        ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    ContextRefreshedEvent (beans created)                          ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    ApplicationStartedEvent                                         ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    ApplicationReadyEvent ← APP FULLY READY                        ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    ContextClosedEvent ← SHUTDOWN                                  ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Event listener log messages during startup     ║
 * ║  RELATED FILES  : CustomEventDemo.java, AsyncEventDemo.java     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.events;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listens to built-in Spring lifecycle events.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   @app.on_event("startup")
 *   async def startup():
 *       print("App started")
 *
 *   @app.on_event("shutdown")
 *   async def shutdown():
 *       print("App shutting down")
 * </pre>
 *
 * <p><b>ASCII — Event listener registration:</b>
 * <pre>
 *   Spring Container
 *       │
 *       ├── fires ContextRefreshedEvent
 *       │       └── all @EventListener(ContextRefreshedEvent) called
 *       │
 *       ├── fires ApplicationReadyEvent
 *       │       └── all @EventListener(ApplicationReadyEvent) called
 *       │
 *       └── fires ContextClosedEvent (shutdown)
 *               └── all @EventListener(ContextClosedEvent) called
 * </pre>
 */
@Component
public class ApplicationEventDemo {

    /**
     * Fired when ApplicationContext is refreshed (beans created).
     */
    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        System.out.println("  🔄 ContextRefreshedEvent — all beans created and wired");
    }

    /**
     * Fired after context refresh, before runners.
     */
    @EventListener
    public void onStarted(ApplicationStartedEvent event) {
        System.out.println("  🚀 ApplicationStartedEvent — context ready, runners pending");
    }

    /**
     * Fired when app is FULLY READY — runners completed.
     * Best place for: enabling healthcheck, starting queue consumers.
     */
    @EventListener
    public void onReady(ApplicationReadyEvent event) {
        long uptime = event.getTimeTaken().toMillis();
        System.out.println("  ✅ ApplicationReadyEvent — app fully ready (startup: " + uptime + "ms)");
    }

    /**
     * Fired during graceful shutdown.
     * Best place for: flushing buffers, deregistering from service discovery.
     */
    @EventListener
    public void onShutdown(ContextClosedEvent event) {
        System.out.println("  🛑 ContextClosedEvent — shutting down gracefully");
    }
}
