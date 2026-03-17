/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              SINGLETON PATTERN — Demo                       ║
 * ║  One instance, thread-safe, and why Spring does it better   ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * WHAT: Ensures only ONE instance of a class exists.
 * WHY:  Shared resources (connection pools, configs, caches).
 * SPRING: @Component is singleton-scoped by default — no GoF needed!
 *
 * ┌─────────────────────────────────────────────────────────┐
 * │  Singleton Approaches (Best → Worst)                    │
 * │                                                         │
 * │  1. Enum              ✅ Best. Thread+serialization safe│
 * │  2. Eager static      ✅ Simple. JVM loading guarantee  │
 * │  3. Double-checked    ⚠️ Complex. Needs volatile       │
 * │  4. Lazy (no sync)    ❌ Broken under concurrency!     │
 * │                                                         │
 * │  In Spring → @Component (let the container manage it)   │
 * └─────────────────────────────────────────────────────────┘
 */
public class SingletonDemo {

    // ── APPROACH 1: Enum Singleton (RECOMMENDED) ──────────────
    /**
     * ┌────────────────────────────────────────┐
     * │ enum AppConfig                         │
     * │  ┌──────────┐                          │
     * │  │ INSTANCE │── only ONE exists        │
     * │  │ dbUrl    │   (JVM guarantee)        │
     * │  │ poolSize │                          │
     * │  └──────────┘                          │
     * │  ✅ Thread-safe ✅ Serialization-safe  │
     * │  ✅ Reflection-safe                    │
     * └────────────────────────────────────────┘
     */
    enum AppConfig {
        INSTANCE;

        private final String dbUrl;
        private final int maxPoolSize;

        AppConfig() {
            this.dbUrl = "jdbc:mysql://localhost:3306/mydb";
            this.maxPoolSize = 10;
        }

        public String getDbUrl()        { return dbUrl; }
        public int    getMaxPoolSize()  { return maxPoolSize; }
    }

    // ── APPROACH 2: Eager Static Initialization ──────────────
    /**
     * ┌──────────────────────────────────────┐
     * │ Class Loading                        │
     * │  ┌──────────┐                        │
     * │  │ INSTANCE │ created ONCE at load   │
     * │  └──────────┘                        │
     * │  static final = immutable reference  │
     * │  JVM class loading = thread-safe     │
     * └──────────────────────────────────────┘
     */
    static class EagerSingleton {
        private static final EagerSingleton INSTANCE = new EagerSingleton();
        private final long createdAt;

        private EagerSingleton() {
            this.createdAt = System.nanoTime();
            System.out.println("  [Eager] Created at " + createdAt);
        }

        public static EagerSingleton getInstance() { return INSTANCE; }
        public long getCreatedAt() { return createdAt; }
    }

    // ── APPROACH 3: Double-Checked Locking ───────────────────
    /**
     * ┌──────────────────────────────────────────────┐
     * │ Thread-1              Thread-2               │
     * │  check null? YES       check null? YES       │
     * │  acquire lock          wait...               │
     * │  check null? YES       ...                   │
     * │  create instance       ...                   │
     * │  release lock          acquire lock          │
     * │                        check null? NO        │
     * │                        return existing       │
     * │                        release lock          │
     * └──────────────────────────────────────────────┘
     * volatile prevents instruction reordering!
     */
    static class DCLSingleton {
        private static volatile DCLSingleton instance;

        private DCLSingleton() {
            System.out.println("  [DCL] Created by " + Thread.currentThread().getName());
        }

        public static DCLSingleton getInstance() {
            if (instance == null) {                          // 1st check (no lock)
                synchronized (DCLSingleton.class) {
                    if (instance == null) {                  // 2nd check (with lock)
                        instance = new DCLSingleton();
                    }
                }
            }
            return instance;
        }
    }

    // ── MAIN: Demonstrate all approaches ─────────────────────
    public static void main(String[] args) throws InterruptedException {
        System.out.println("═══ SINGLETON PATTERN DEMO ═══\n");

        // 1. Enum singleton
        System.out.println("1. Enum Singleton:");
        AppConfig c1 = AppConfig.INSTANCE;
        AppConfig c2 = AppConfig.INSTANCE;
        System.out.println("  Same instance? " + (c1 == c2));  // true
        System.out.println("  DB URL: " + c1.getDbUrl());
        System.out.println("  Pool: " + c1.getMaxPoolSize());

        // 2. Eager singleton
        System.out.println("\n2. Eager Singleton:");
        EagerSingleton e1 = EagerSingleton.getInstance();
        EagerSingleton e2 = EagerSingleton.getInstance();
        System.out.println("  Same instance? " + (e1 == e2));  // true

        // 3. DCL singleton — thread safety test
        System.out.println("\n3. DCL Singleton (multi-threaded test):");
        Thread t1 = new Thread(() -> DCLSingleton.getInstance(), "Thread-A");
        Thread t2 = new Thread(() -> DCLSingleton.getInstance(), "Thread-B");
        t1.start(); t2.start();
        t1.join();  t2.join();
        System.out.println("  Only ONE 'Created' message should appear above.");

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("In Spring: use @Component (singleton scope by default).");
        System.out.println("In pure Java: use enum singleton.");
        System.out.println("Never use lazy singleton without synchronization.");
    }
}
