package explanation;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : InnerClassDemo.java                                    ║
 * ║  MODULE : 00-java-foundation / 03-advanced-oop                   ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="InnerClassDemo"║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates the 4 types of nested classes     ║
 * ║  WHY IT EXISTS  : Logical grouping and extreme encapsulation     ║
 * ║  PYTHON COMPARE : Python nested classes = Java static nested.    ║
 * ║                   Python has no "member" inner class equivalent  ║
 * ║                   without explicitly passing `self` to the inner.║
 * ║  USE CASES      : Node in LinkedList, Event Listeners, Builders  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                    ║
 * ║    Outer Instance                                                 ║
 * ║    ┌───────────────────────────┐                                 ║
 * ║    │ private int x = 10;       │                                 ║
 * ║    │                           │                                 ║
 * ║    │   Member Inner Instance   │                                 ║
 * ║    │   ┌──────────────────┐    │                                 ║
 * ║    │   │ access to x!     │    │                                 ║
 * ║    │   └──────────────────┘    │                                 ║
 * ║    └───────────────────────────┘                                 ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run --args="InnerClassDemo" ║
 * ║  EXPECTED OUTPUT: Output from all 4 types of inner classes       ║
 * ║  RELATED FILES  : 01-inner-classes.md                            ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class InnerClassDemo {

    // 1. Static Nested Class
    // Used when the nested class does NOT need access to instance variables of the outer class.
    // Extremely common for Builder patterns (e.g. User.Builder) or Helper structures (e.g. LinkedList.Node)
    static class StaticNested {
        void display() {
            System.out.println("[Static Nested] Hello! I don't need an Outer instance to exist.");
        }
    }

    // Instance variable of the outer class
    private String secretData = "Top Secret Engine Code";

    // 2. Member Inner Class (Non-static)
    // Used when the nested class MUST interact with instance variables of the outer class.
    // E.g., An Iterator that traverses a specific ArrayList.
    class MemberInner {
        void revealSecret() {
            // Implicitly accessing InnerClassDemo.this.secretData
            System.out.println("[Member Inner] I can see the outer secret: " + secretData);
        }
    }

    public void demonstrateLocalAndAnonymous() {

        // 3. Local Inner Class
        // Defined inside a method block. Extremely rare in modern Java.
        // Can access 'effectively final' local method variables.
        final String localVariable = "Local scope string";

        class LocalInner {
            void print() {
                System.out.println("[Local Inner] I am inside a method. Variable: " + localVariable);
                System.out.println("[Local Inner] I can also see the static outer secret: " + secretData);
            }
        }

        LocalInner local = new LocalInner();
        local.print();

        // 4. Anonymous Inner Class
        // Creating an instance of a class that implements an interface or extends a class ON THE FLY
        // without giving it a formal name.
        // Replaced 95% of the time by Lambdas since Java 8, but still needed if the interface
        // has MORE than one method to implement.
        Runnable anonymousRunnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("[Anonymous Inner] I implement Runnable gracefully inline!");
            }
        };

        anonymousRunnable.run();
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Static Nested Class ---");
        // We do NOT need an instance of InnerClassDemo to create StaticNested
        InnerClassDemo.StaticNested nested = new InnerClassDemo.StaticNested();
        nested.display();

        System.out.println("\n--- 2. Member Inner Class ---");
        // We MUST create an instance of InnerClassDemo first!
        InnerClassDemo demo = new InnerClassDemo();
        // Notice the syntax: existingInstance.new Inner()
        InnerClassDemo.MemberInner inner = demo.new MemberInner();
        inner.revealSecret();

        System.out.println("\n--- 3 & 4. Local and Anonymous Inner Classes ---");
        demo.demonstrateLocalAndAnonymous();
    }
}
