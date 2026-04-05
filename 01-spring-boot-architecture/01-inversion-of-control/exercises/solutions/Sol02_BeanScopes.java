package exercises.solutions;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Sol02_BeanScopes.java                                                              ║
 * ║ MODULE: 01-spring-boot-architecture / 01-inversion-of-control / exercises/solutions      ║
 * ║ GRADLE: ./gradlew :01-spring-boot-architecture:run                                       ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ PURPOSE: Solution — Demonstrate Singleton state corruption and the fix                   ║
 * ║ DIFFICULTY: Beginner                                                                     ║
 * ║ PYTHON COMPARE: Global module-level variable modified by multiple callers                ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ ASCII DIAGRAM: Singleton State Corruption vs Stateless Service                          ║
 * ║                                                                                          ║
 * ║  BROKEN (stateful singleton):                                                            ║
 * ║  [UserA] ──setLastEvent──► ┌──────────────────────┐                                     ║
 * ║  [UserB] ──setLastEvent──► │ Singleton MetricsSvc  │ lastEvent = UserB's event!          ║
 * ║  [UserA reads]             │ lastEvent = ?         │ ← UserA sees UserB's data!          ║
 * ║                            └──────────────────────┘                                     ║
 * ║                                                                                          ║
 * ║  FIXED (stateless singleton + method-local storage):                                     ║
 * ║  [UserA] ──recordEvent(A)──► [StatelessMetricsSvc] → writes to DB / event bus           ║
 * ║  [UserB] ──recordEvent(B)──► [StatelessMetricsSvc] → writes to DB / event bus           ║
 * ║           No shared mutable state — both users isolated                                  ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 */
public class Sol02_BeanScopes {

    // WHY static field? Simulates Spring's default Singleton scope —
    // only one instance shared across the entire application context.
    private static final MetricsService singletonInstance = new MetricsService();
    private static final StatelessMetricsService statelessInstance = new StatelessMetricsService();

    /**
     * Simulates the Spring IoC container returning the same bean instance every time.
     *
     * @return the singleton MetricsService instance
     */
    public static MetricsService getBean() {
        return singletonInstance;
    }

    /**
     * Entry point — demonstrates the corruption problem and the stateless fix.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        System.out.println("=== PART 1: Singleton State Corruption Demo ===");
        demonstrateCorruption();

        System.out.println("\n=== PART 2: Stateless Service Fix Demo ===");
        demonstrateStatelessFix();

        // EXPECTED OUTPUT:
        // === PART 1: Singleton State Corruption Demo ===
        // User A sets last event: User A logged in.
        // User B sets last event: User B purchased item.
        // User A checks last event: User B purchased item.  ← CORRUPTION!
        // Same instance? true
        //
        // === PART 2: Stateless Service Fix Demo ===
        // [StatelessMetrics] Recorded: User A logged in. for user=UserA
        // [StatelessMetrics] Recorded: User B purchased item. for user=UserB
        // No shared state — each call is isolated.
    }

    /**
     * Shows that a stateful Singleton causes cross-user data corruption.
     */
    private static void demonstrateCorruption() {
        var userABean = getBean();
        userABean.setLastEvent("User A logged in.");
        System.out.println("User A sets last event: " + userABean.getLastEvent());

        var userBBean = getBean();
        userBBean.setLastEvent("User B purchased item.");
        System.out.println("User B sets last event: " + userBBean.getLastEvent());

        // WHY is this wrong? Both userABean and userBBean point to the SAME object.
        // When UserB calls setLastEvent(), UserA's state is silently overwritten.
        System.out.println("User A checks last event: " + userABean.getLastEvent());
        System.out.println("Same instance? " + (userABean == userBBean)); // always true
    }

    /**
     * Shows the correct approach: stateless service, state passed as parameters.
     */
    private static void demonstrateStatelessFix() {
        // WHY stateless? Singletons should NEVER hold user-specific or request-scoped state.
        // Pass context (userId, event) as method parameters — keep it on the call stack,
        // not on the heap as a shared field.
        statelessInstance.recordEvent("User A logged in.", "UserA");
        statelessInstance.recordEvent("User B purchased item.", "UserB");
        System.out.println("No shared state — each call is isolated.");
    }

    // ─── Beans ────────────────────────────────────────────────────────────────

    /**
     * DANGEROUS: Stateful Singleton — demonstrates the anti-pattern.
     *
     * <p>WHY DANGEROUS: In a Spring Boot app, this bean is shared across ALL requests.
     * {@code lastEvent} will contain the last caller's data, not the current caller's.
     * Under concurrent load, threads will overwrite each other's data.
     *
     * <p>In Spring Boot this would be annotated {@code @Service @Scope("singleton")} (default).
     */
    public static class MetricsService {

        // ANTI-PATTERN: mutable request-scoped state on a singleton bean
        private String lastEvent;

        /**
         * Sets the last event — DANGEROUS in a multi-user singleton context.
         *
         * @param event the event description
         */
        public void setLastEvent(String event) {
            this.lastEvent = event;
        }

        /**
         * Gets the last event — will return another user's event in production.
         *
         * @return the last event set by ANY caller
         */
        public String getLastEvent() {
            return this.lastEvent;
        }
    }

    /**
     * CORRECT: Stateless service — all context flows through method parameters.
     *
     * <p>Safe to use as a Singleton because it holds NO mutable instance state.
     * Each method call is fully isolated — there is nothing to corrupt.
     *
     * <p>In Spring Boot: {@code @Service} (stateless by design).
     */
    public static class StatelessMetricsService {

        /**
         * Records an event for a specific user — all context comes through parameters.
         *
         * @param event  the event description
         * @param userId the user this event belongs to
         */
        public void recordEvent(String event, String userId) {
            // WHY parameters instead of fields? This method is thread-safe because
            // event and userId live on the call stack — each thread has its own copy.
            System.out.println("[StatelessMetrics] Recorded: " + event + " for user=" + userId);
            // In production: write to database, Kafka, or metrics store here
        }
    }
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Storing request data as Singleton instance fields
 *   WRONG: private String currentUser; (as a field on a @Service)
 *   WHY BAD: All requests share the same instance — one user's data overwrites another's.
 *   FIX: Pass userId/context as method parameters or use ThreadLocal (advanced).
 *
 * MISTAKE 2: Confusing Singleton scope with Singleton pattern
 *   Spring @Scope("singleton") means one instance per ApplicationContext.
 *   The GoF Singleton Pattern is a design pattern with private constructor.
 *   They are related concepts but NOT the same thing.
 *
 * MISTAKE 3: Using @Scope("prototype") when Singleton is fine
 *   @Scope("prototype") creates a new instance per injection point.
 *   This is expensive — only use it for stateful beans (e.g., wizard forms, user sessions).
 *   Stateless services should ALWAYS be Singleton scope for efficiency.
 *
 * MISTAKE 4: Not understanding that @Controller beans are also Singletons
 *   A common mistake is storing request data as a controller field.
 *   WRONG: private String requestId; // field in @RestController
 *   FIX: Use method parameters, @RequestScope beans, or pass via HttpServletRequest.
 */
