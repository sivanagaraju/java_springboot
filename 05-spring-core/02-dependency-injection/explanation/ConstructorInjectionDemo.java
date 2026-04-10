/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ConstructorInjectionDemo.java                          ║
 * ║  MODULE : 05-spring-core / 02-dependency-injection                ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Shows constructor injection — the recommended  ║
 * ║                   DI approach with immutable, testable beans      ║
 * ║  WHY IT EXISTS  : Setter/field injection creates mutable beans   ║
 * ║                   that can be in invalid states                    ║
 * ║  PYTHON COMPARE : Python __init__ with type hints                ║
 * ║                   → Java constructor with final fields            ║
 * ║  USE CASES      : 1) Service with repository dependency          ║
 * ║                   2) Multiple required dependencies               ║
 * ║                   3) Testing with mock injection                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Constructor Injection Flow                       ║
 * ║                                                                    ║
 * ║    Spring Container                                                ║
 * ║        │                                                           ║
 * ║        ├── finds NotificationService (needs Notifier)             ║
 * ║        ├── finds EmailNotifier (implements Notifier)              ║
 * ║        ▼                                                           ║
 * ║    new NotificationService(emailNotifier)                          ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    [ final Notifier notifier = emailNotifier ]                    ║
 * ║    → IMMUTABLE — cannot be changed after construction             ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Notification sent via constructor-injected bean ║
 * ║  RELATED FILES  : SetterInjectionDemo.java, QualifierDemo.java   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.di;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Notifier interface — contract for sending notifications.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   from abc import ABC, abstractmethod
 *   class Notifier(ABC):
 *       @abstractmethod
 *       def send(self, message: str) -> None: ...
 * </pre>
 */
interface Notifier {
    void send(String message);
}

/**
 * Email implementation of Notifier.
 *
 * <p><b>ASCII — Strategy Pattern:</b>
 * <pre>
 *   Notifier (interface)
 *       ├── EmailNotifier   ← this class
 *       ├── SmsNotifier
 *       └── PushNotifier
 * </pre>
 */
@Service
class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("  📧 Email sent: " + message);
    }
}

/**
 * Service demonstrating constructor injection with final fields.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class NotificationService:
 *       def __init__(self, notifier: Notifier):
 *           self.notifier = notifier  # injected via Depends()
 * </pre>
 *
 * <p><b>ASCII — Why constructor injection is best:</b>
 * <pre>
 *   Constructor Injection:          Field Injection:
 *   ────────────────────           ────────────────
 *   ✅ final field                  ❌ mutable field
 *   ✅ fail-fast on missing         ❌ NPE at runtime
 *   ✅ new Service(mock) in test    ❌ need reflection
 *   ✅ visible in constructor       ❌ hidden in fields
 * </pre>
 */
@Service
class NotificationService {

    private final Notifier notifier;  // final = immutable

    /**
     * Single constructor — Spring auto-injects without @Autowired.
     *
     * <p><b>Flow:</b>
     * <pre>
     *   Spring scans → finds NotificationService
     *       │
     *       ▼
     *   Sees constructor parameter: Notifier
     *       │
     *       ▼
     *   Finds EmailNotifier bean (implements Notifier)
     *       │
     *       ▼
     *   Calls: new NotificationService(emailNotifier)
     * </pre>
     *
     * @param notifier injected by Spring IoC container
     */
    public NotificationService(Notifier notifier) {
        this.notifier = notifier;
        System.out.println("  → NotificationService created with " + notifier.getClass().getSimpleName());
    }

    /**
     * Sends a notification through the injected notifier.
     *
     * @param message the message to send
     */
    public void notify(String message) {
        notifier.send(message);
    }
}

/**
 * Runner to demonstrate constructor injection in action.
 */
@Component
class ConstructorInjectionRunner implements CommandLineRunner {

    private final NotificationService service;

    public ConstructorInjectionRunner(NotificationService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Constructor Injection Demo ===");
        System.out.println();
        service.notify("Order #1234 has been shipped!");
        System.out.println();
        System.out.println("Key insight: NotificationService has a FINAL Notifier field.");
        System.out.println("It cannot be changed after construction — immutable and thread-safe.");
    }
}
