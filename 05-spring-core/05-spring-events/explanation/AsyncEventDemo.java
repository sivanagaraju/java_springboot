/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : AsyncEventDemo.java                                    ║
 * ║  MODULE : 05-spring-core / 05-spring-events                       ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates @Async @EventListener for         ║
 * ║                   non-blocking event processing                   ║
 * ║  WHY IT EXISTS  : Synchronous events block the publisher —       ║
 * ║                   @Async fires event in separate thread pool     ║
 * ║  PYTHON COMPARE : Python asyncio.create_task(handler(event))    ║
 * ║                   → Java @Async @EventListener                    ║
 * ║  USE CASES      : 1) Sending emails without blocking order flow  ║
 * ║                   2) Analytics tracking in background             ║
 * ║                   3) Notification push to external services       ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Async vs Sync Events                             ║
 * ║                                                                    ║
 * ║    SYNC:  Publisher ──► Listener1 (wait) ──► Listener2 (wait)     ║
 * ║           Total time = L1 + L2                                    ║
 * ║                                                                    ║
 * ║    ASYNC: Publisher ──┬──► Listener1 (own thread)                 ║
 * ║                       └──► Listener2 (own thread)                 ║
 * ║           Publisher returns immediately!                           ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Async listeners log thread names                ║
 * ║  RELATED FILES  : CustomEventDemo.java, 03-async-events.md      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.events;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * Async configuration — enables @Async annotation processing.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python asyncio equivalent
 *   import asyncio
 *   loop = asyncio.get_event_loop()
 *   executor = ThreadPoolExecutor(max_workers=5)
 * </pre>
 */
@Configuration
@EnableAsync
class AsyncConfig {

    /**
     * Custom thread pool for async event processing.
     *
     * @return configured thread pool executor
     */
    @Bean("eventExecutor")
    public Executor eventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("async-event-");
        executor.initialize();
        return executor;
    }
}

/**
 * Payment event for async processing demo.
 */
record PaymentProcessedEvent(Long paymentId, String customer, double amount) {}

/**
 * Async listener — runs in separate thread, doesn't block publisher.
 *
 * <p><b>ASCII — Async execution:</b>
 * <pre>
 *   Main Thread:  publishEvent() → returns immediately → next line
 *   Async Pool:   async-event-1 → handlePayment() → slow email send
 *                 async-event-2 → handleReceipt() → slow PDF generation
 * </pre>
 */
@Component
class AsyncPaymentListeners {

    /**
     * Async listener — processes in background thread.
     */
    @Async("eventExecutor")
    @EventListener
    public void handlePaymentNotification(PaymentProcessedEvent event) {
        System.out.println("  📧 [" + Thread.currentThread().getName() +
                "] Sending payment notification for $" + event.amount());
        // Simulate slow email sending
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        System.out.println("  ✅ [" + Thread.currentThread().getName() +
                "] Notification sent to " + event.customer());
    }

    /**
     * Another async listener — runs in parallel with the first.
     */
    @Async("eventExecutor")
    @EventListener
    public void handleReceiptGeneration(PaymentProcessedEvent event) {
        System.out.println("  🧾 [" + Thread.currentThread().getName() +
                "] Generating receipt for payment #" + event.paymentId());
        try { Thread.sleep(150); } catch (InterruptedException ignored) {}
        System.out.println("  ✅ [" + Thread.currentThread().getName() +
                "] Receipt generated");
    }
}
