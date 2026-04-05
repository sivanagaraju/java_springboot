package exercises.solutions;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Sol01_ConstructorInjection.java                                                    ║
 * ║ MODULE: 01-spring-boot-architecture / 01-inversion-of-control / exercises/solutions      ║
 * ║ GRADLE: ./gradlew :01-spring-boot-architecture:run                                       ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ PURPOSE: Solution — Constructor Injection refactor                                       ║
 * ║ DIFFICULTY: Beginner                                                                     ║
 * ║ PYTHON COMPARE: Passing dependencies into __init__(self, repo) for testability           ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ ASCII DIAGRAM: Dependency Inversion after refactor                                       ║
 * ║                                                                                          ║
 * ║   BEFORE (tight coupling):                                                               ║
 * ║   [UserService] ──new──► [RealUserRepository]  ← Cannot be swapped!                     ║
 * ║                                                                                          ║
 * ║   AFTER (constructor injection):                                                         ║
 * ║   [Main] ──creates──► [MockUserRepository]                                               ║
 * ║      │                       │                                                           ║
 * ║      └───injects into──► [UserService]  ← Depends on interface, not impl                 ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 */
public class Sol01_ConstructorInjection {

    /**
     * Entry point demonstrating manual (non-Spring) constructor injection.
     *
     * <p>In a real Spring Boot app, the IoC container wires these automatically.
     * Here we show the same principle manually so the mechanics are clear.
     *
     * <p><b>ASCII — Spring Boot does this automatically:</b>
     * <pre>
     *   ApplicationContext ──@Autowired──► UserRepository impl
     *                      └──injects──► UserService constructor
     * </pre>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("--- Booting Application ---");

        // WHY MockUserRepository? Because we OWN this wiring decision at the call site.
        // In tests we pass Mock; in production Spring would pass RealUserRepository.
        var mockRepo = new MockUserRepository();
        var service = new UserService(mockRepo);

        service.saveUser("Alice");

        System.out.println("\n--- Swapping to Real Repo (simulating prod profile) ---");
        // Demonstrate that swapping the implementation requires zero changes to UserService.
        var prodService = new UserService(new RealUserRepository());
        prodService.saveUser("Bob");

        // EXPECTED OUTPUT:
        // --- Booting Application ---
        // Processing business logic... [MOCK DB] Skipped Network. Safely intercepted Alice
        //
        // --- Swapping to Real Repo (simulating prod profile) ---
        // Processing business logic... [REAL DB] Establishing TCP Connection... Saving Bob
    }

    // ─── Interface (Dependency Abstraction) ───────────────────────────────────

    /**
     * Contract for persisting user data.
     *
     * <p>Depending on this interface (not a concrete class) is the key to testability
     * and the Open-Closed Principle — new implementations don't require editing UserService.
     */
    public interface UserRepository {
        /**
         * Persist a user by username.
         *
         * @param username the username to save (non-null)
         */
        void save(String username);
    }

    // ─── Implementations ──────────────────────────────────────────────────────

    /**
     * Production implementation that writes to a real database over TCP.
     *
     * <p><b>In Spring Boot:</b> annotated {@code @Repository @Profile("prod")}
     */
    public static class RealUserRepository implements UserRepository {
        @Override
        public void save(String username) {
            System.out.println("[REAL DB] Establishing TCP Connection... Saving " + username);
        }
    }

    /**
     * Test-double implementation that avoids all I/O.
     *
     * <p>WHY: Unit tests must be fast, deterministic, and isolated.
     * A mock avoids flaky network calls and lets you test business logic alone.
     *
     * <p><b>In Spring Boot:</b> annotated {@code @Repository @Profile("test")}
     */
    public static class MockUserRepository implements UserRepository {
        @Override
        public void save(String username) {
            System.out.println("[MOCK DB] Skipped Network. Safely intercepted " + username);
        }
    }

    // ─── Service (Depends on Abstraction) ─────────────────────────────────────

    /**
     * Business logic layer for user operations.
     *
     * <p>Depends only on {@link UserRepository} interface — never on a concrete class.
     * This is the D in SOLID: Dependency Inversion Principle.
     *
     * <p><b>In Spring Boot:</b>
     * <pre>
     *   {@code @Service}
     *   public class UserService {
     *       private final UserRepository repository;
     *
     *       public UserService(UserRepository repository) { // Spring autowires here
     *           this.repository = repository;
     *       }
     *   }
     * </pre>
     */
    public static class UserService {

        // WHY final? Guarantees the dependency is set once at construction time.
        // A mutable field could be accidentally overwritten or left null.
        private final UserRepository repository;

        /**
         * Constructs UserService with its required dependency.
         *
         * <p>Constructor injection is preferred over field injection (@Autowired on field)
         * because: (1) final field guarantees immutability, (2) testable without Spring,
         * (3) explicit about required dependencies (fails fast at startup if missing).
         *
         * @param repository the persistence strategy to use (non-null)
         */
        public UserService(UserRepository repository) {
            this.repository = repository;
        }

        /**
         * Validates and persists a new user.
         *
         * @param username the username to register (non-null)
         */
        public void saveUser(String username) {
            System.out.print("Processing business logic... ");
            repository.save(username);
        }
    }
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Field injection instead of constructor injection
 *   WRONG: @Autowired private UserRepository repository;
 *   WHY BAD: The field is not final; the class is untestable without Spring;
 *            the dependency can be null if Spring misconfigures it.
 *   FIX: Use constructor injection — Spring 4.3+ auto-wires single-constructor classes.
 *
 * MISTAKE 2: Depending on concrete class instead of interface
 *   WRONG: private final RealUserRepository repository;
 *   WHY BAD: Cannot swap to MockUserRepository in tests; violates Open-Closed Principle.
 *   FIX: Depend on the UserRepository interface.
 *
 * MISTAKE 3: Forgetting 'final' on the injected field
 *   WRONG: private UserRepository repository;
 *   WHY BAD: The field can be accidentally reassigned after construction.
 *   FIX: Always make injected fields 'final'.
 *
 * MISTAKE 4: Using 'new' inside a service to create its dependency
 *   WRONG: this.repository = new RealUserRepository();
 *   WHY BAD: Hard-codes the implementation — back to tight coupling.
 *   FIX: Accept the dependency via constructor parameter.
 */
