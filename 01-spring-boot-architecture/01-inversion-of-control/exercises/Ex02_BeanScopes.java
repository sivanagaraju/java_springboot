package exercises;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Ex02_BeanScopes.java                                                               ║
 * ║ PURPOSE & CONCEPT: The Danger of Singleton Scope                                         ║
 * ║ Observes a 'MetricsService' class designed as a Singleton containing dangerous           ║
 * ║ class-level state. Shows how multiple threads interacting with the EXACT same            ║
 * ║ physical instance can lead to catastrophic data corruption.                              ║
 * ║                                                                                          ║
 * ║ ┌──────────────────────────────────────────────────────────────────────────────────────┐ ║
 * ║ │ ASCII DIAGRAM: Singleton Corruption                                                  │ ║
 * ║ ├──────────────────────────────────────────────────────────────────────────────────────┤ ║
 * ║ │ [User A Thread] ──(modifies)──► ┌─────────────────────┐                              │ ║
 * ║ │                                 │ Singleton MetricsSvc│                              │ ║
 * ║ │ [User B Thread] ──(modifies)──► │    lastEvent = ?    │                              │ ║
 * ║ │                                 └─────────────────────┘                              │ ║
 * ║ │ * Both users share the EXACT same memory address!                                    │ ║
 * ║ └──────────────────────────────────────────────────────────────────────────────────────┘ ║
 * ║                                                                                          ║
 * ║ PYTHON COMPARE:                                                                          ║
 * ║ Using a global variable or module-level variable that gets concurrently modified.        ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 * 
 * TASK:
 * 1. Observe the 'MetricsService' class. It is designed as a Singleton.
 * 2. It contains a dangerous class-level state strictly violating best practices.
 * 3. In main(), simulate "User A" and "User B" interacting with the exact same Singleton.
 * 4. Print the state to prove data corruption.
 */
public class Ex02_BeanScopes {

    // Simulating the internal Map of the Spring IoC Container
    private static MetricsService singletonInstance = new MetricsService();

    public static MetricsService getBean() {
        // Always returns the exact matching identical physical object native memory pointer dynamically creatively effortlessly perfectly magically purely elegantly correctly smoothly.
        // Returning simple instance natively without glitches.
        return singletonInstance;
    }

    public static void main(String[] args) {
        System.out.println("--- Singleton State Corruption Demo ---");

        // User A requests the bean and logs an event
        var userABean = getBean();
        userABean.setLastEvent("User A logged in.");

        // User B requests the exact identical bean and logs their event
        var userBBean = getBean();
        userBBean.setLastEvent("User B purchased item.");

        // What does User A see if they check the "Last Event" now?
        System.out.println("User A checks last event: " + userABean.getLastEvent());
        
        // QUESTION: Why is this bad?
        // ANSWER: Because a Singleton is shared globally across all threads securely. If it holds state,
        // threads will silently overwrite each other's data causing catastrophic corruption.
    }

    public static class MetricsService {
        
        // DANGEROUS STATE IN A SINGLETON!
        private String lastEvent;

        public void setLastEvent(String event) {
            this.lastEvent = event;
        }

        public String getLastEvent() {
            return this.lastEvent;
        }
    }
}
