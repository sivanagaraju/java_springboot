/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_DIRefactor.java                                   ║
 * ║  MODULE : 05-spring-core / 02-dependency-injection / exercises    ║
 * ║  GRADLE : ./gradlew :05-spring-core:test                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Refactor field injection to constructor         ║
 * ║                   injection — the most important DI habit        ║
 * ║  WHY IT EXISTS  : Field injection is the #1 anti-pattern in     ║
 * ║                   Spring — this trains the correct approach       ║
 * ║  PYTHON COMPARE : Python always uses __init__ (constructor).    ║
 * ║                   Java devs often wrongly use @Autowired fields. ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE INSTRUCTIONS:                                           ║
 * ║  1. Identify the field injection anti-pattern below               ║
 * ║  2. Refactor to constructor injection with final fields          ║
 * ║  3. Remove all @Autowired annotations                             ║
 * ║  4. Write a unit test that creates the service with mocks        ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.exercises;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

/**
 * ❌ BEFORE: Field injection — the anti-pattern.
 *
 * <p><b>Problems:</b>
 * <pre>
 *   1. Cannot make fields final → mutable
 *   2. Cannot test without Spring context
 *   3. Hidden dependencies — not visible in constructor
 *   4. Can create object in invalid state
 * </pre>
 *
 * TODO: Refactor to constructor injection
 */
// @Service
class UserService_BEFORE {
    // @Autowired
    // private UserRepository userRepo;        // ❌ not final, hidden

    // @Autowired
    // private NotificationService notifier;   // ❌ not final, hidden

    // @Autowired
    // private AuditLogger auditLogger;        // ❌ not final, hidden

    public String createUser(String name) {
        // All 3 dependencies used — but caller can't see them!
        return "User created: " + name;
    }
}

/**
 * ✅ AFTER: Constructor injection — the correct pattern.
 *
 * <p><b>Benefits:</b>
 * <pre>
 *   1. final fields → immutable
 *   2. Test: new UserService(mockRepo, mockNotifier, mockLogger)
 *   3. All dependencies visible in constructor
 *   4. Can't create without ALL dependencies
 * </pre>
 */
class UserService_AFTER {
    // TODO: Make these final fields
    // private final UserRepository userRepo;
    // private final NotificationService notifier;
    // private final AuditLogger auditLogger;

    // TODO: Single constructor — Spring auto-detects
    // public UserService_AFTER(UserRepository userRepo,
    //                          NotificationService notifier,
    //                          AuditLogger auditLogger) {
    //     this.userRepo = userRepo;
    //     this.notifier = notifier;
    //     this.auditLogger = auditLogger;
    // }
}

/**
 * Placeholder interfaces for the exercise.
 */
interface UserRepository { String save(String name); }
interface AuditLogger { void log(String action); }

public class Ex01_DIRefactor {
    public static void main(String[] args) {
        System.out.println("=== DI Refactor Exercise ===");
        System.out.println();
        System.out.println("Task: Refactor UserService from field injection to constructor injection");
        System.out.println();
        System.out.println("Steps:");
        System.out.println("  1. Make all dependency fields 'final'");
        System.out.println("  2. Create a constructor that accepts all dependencies");
        System.out.println("  3. Remove all @Autowired annotations");
        System.out.println("  4. Verify: can you create the service with 'new' in a test?");
        System.out.println("     new UserService(mockRepo, mockNotifier, mockLogger)");
    }
}
