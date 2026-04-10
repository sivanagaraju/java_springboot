/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : QualifierDemo.java                                     ║
 * ║  MODULE : 05-spring-core / 02-dependency-injection                ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates @Qualifier and @Primary to        ║
 * ║                   resolve ambiguous bean injection                 ║
 * ║  WHY IT EXISTS  : When multiple beans implement the same          ║
 * ║                   interface, Spring needs help choosing            ║
 * ║  PYTHON COMPARE : Python: factory(type="sms") → named bean      ║
 * ║                   Java: @Qualifier("smsNotifier") → named bean   ║
 * ║  USE CASES      : 1) Email vs SMS notification strategy          ║
 * ║                   2) Dev vs Prod data source selection            ║
 * ║                   3) Injecting all implementations as List        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Multiple Implementations                         ║
 * ║                                                                    ║
 * ║    MessageSender (interface)                                       ║
 * ║        │                                                           ║
 * ║        ├── EmailSender   @Primary (default)                       ║
 * ║        ├── SmsSender     @Qualifier("sms")                        ║
 * ║        └── PushSender    @Qualifier("push")                       ║
 * ║                                                                    ║
 * ║    AlertService:                                                   ║
 * ║        primary sender → EmailSender (no qualifier needed)         ║
 * ║        sms sender     → SmsSender (@Qualifier("sms"))             ║
 * ║        all senders    → List<MessageSender> (all 3!)              ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Messages sent via email, SMS, and all channels ║
 * ║  RELATED FILES  : 04-qualifier.md, 05-primary.md                 ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.di;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Message sender interface — multiple implementations exist.
 */
interface MessageSender {
    void send(String message);
    String getChannel();
}

@Service
@Primary  // default when no @Qualifier specified
class EmailSender implements MessageSender {
    @Override public void send(String message) { System.out.println("  📧 Email: " + message); }
    @Override public String getChannel() { return "email"; }
}

@Service("sms")
class SmsSender implements MessageSender {
    @Override public void send(String message) { System.out.println("  📱 SMS: " + message); }
    @Override public String getChannel() { return "sms"; }
}

@Service("push")
class PushSender implements MessageSender {
    @Override public void send(String message) { System.out.println("  🔔 Push: " + message); }
    @Override public String getChannel() { return "push"; }
}

/**
 * Demonstrates @Primary, @Qualifier, and List injection.
 *
 * <p><b>ASCII — Resolution priority:</b>
 * <pre>
 *   @Qualifier specified? ──► YES: use qualified bean
 *       │
 *       ▼ NO
 *   @Primary exists? ──► YES: use primary bean
 *       │
 *       ▼ NO
 *   Only one bean? ──► YES: use it
 *       │
 *       ▼ NO
 *   → NoUniqueBeanDefinitionException!
 * </pre>
 */
@Component
class QualifierRunner implements CommandLineRunner {

    private final MessageSender primarySender;      // gets @Primary (EmailSender)
    private final MessageSender smsSender;           // gets @Qualifier("sms")
    private final List<MessageSender> allSenders;    // gets ALL implementations

    /**
     * Demonstrates three injection patterns:
     * 1. No qualifier → gets @Primary
     * 2. @Qualifier("sms") → gets specific bean
     * 3. List → gets ALL beans of type
     */
    public QualifierRunner(
            MessageSender primarySender,
            @Qualifier("sms") MessageSender smsSender,
            List<MessageSender> allSenders) {
        this.primarySender = primarySender;
        this.smsSender = smsSender;
        this.allSenders = allSenders;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== @Qualifier + @Primary Demo ===");
        System.out.println();

        System.out.println("1. Primary sender (no qualifier):");
        primarySender.send("Default channel message");
        System.out.println();

        System.out.println("2. Qualified sender (@Qualifier(\"sms\")):");
        smsSender.send("Urgent: your code is deployed!");
        System.out.println();

        System.out.println("3. All senders (List<MessageSender>):");
        allSenders.forEach(s -> s.send("Broadcast to " + s.getChannel()));
    }
}
