package explanation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : AnnotationDemo.java                                    ║
 * ║  MODULE : 00-java-foundation / 03-advanced-oop                   ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="AnnotationDemo" ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates defining and processing metadata  ║
 * ║  WHY IT EXISTS  : Frameworks (Spring/Hibernate) rely on this     ║
 * ║                   heavily to inject behavior without boilerplate.║
 * ║  PYTHON COMPARE : Like Decorators, but completely passive.       ║
 * ║                   Annotations do nothing until code *reads* them.║
 * ║  USE CASES      : @Override, @Entity, @RestController, @Test     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                    ║
 * ║    @RequiresRole("ADMIN")                                         ║
 * ║    public void deleteDB() { ... }                                 ║
 * ║                                                                  ║
 * ║         [Reflection Engine]                                      ║
 * ║         Reads Method metadata ---> Finds @RequiresRole           ║
 * ║         Validates current User ---> Executes or Throws           ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run --args="AnnotationDemo" ║
 * ║  EXPECTED OUTPUT: Output showing Annotation Processor logic      ║
 * ║  RELATED FILES  : 04-annotations.md                              ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class AnnotationDemo {

    // 1. Defining a Custom Annotation
    // Target: Where can this be applied? (Methods only)
    @Target(ElementType.METHOD)
    // Retention: How long does this annotation survive? (Runtime, so we can read it!)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequiresRole {
        // Annotation attributes look like interface methods but act like fields with defaults.
        String value() default "GUEST"; 
    }

    // 2. Applying the Annotation
    static class SecurityService {

        @RequiresRole("ADMIN")
        public void wipeDatabase() {
            System.out.println("💥 DATABASE WIPED. I HOPE YOU KNEW WHAT YOU WERE DOING.");
        }

        // Just using the default "GUEST" value
        @RequiresRole
        public void viewPublicHomepage() {
            System.out.println("👀 Viewing the public homepage.");
        }

        public void unannotatedMethod() {
            System.out.println("This method has no security metadata.");
        }
    }

    // 3. Processing the Annotation (Simulating Spring Security)
    public static void processAnnotationsAndExecute(Object obj, String currentUserRole) throws Exception {
        Class<?> clazz = obj.getClass();
        
        System.out.println("--- Processing Methods for: " + clazz.getSimpleName() + " ---");

        // Use Reflection to look at every single method inside the class
        for (Method method : clazz.getDeclaredMethods()) {
            
            // Check if our sticker (@RequiresRole) is on this method
            if (method.isAnnotationPresent(RequiresRole.class)) {
                
                // Read the actual sticky note value
                RequiresRole annotation = method.getAnnotation(RequiresRole.class);
                String requiredRole = annotation.value();
                
                System.out.printf("Attempting to execute '%s()' as '%s'. Requires: '%s'%n", 
                                  method.getName(), currentUserRole, requiredRole);
                
                // Security Enforcement Logic
                if (requiredRole.equals(currentUserRole)) {
                    System.out.println("✅ Access Granted.");
                    method.invoke(obj); // Actually call the method dynamically!
                } else {
                    System.out.println("❌ Access Denied. Security Exception thrown.\n");
                }
            } else {
                System.out.printf("Method '%s()' has no @RequiresRole, skipping execution.%n", method.getName());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        SecurityService service = new SecurityService();

        // Let's pretend a GUEST is using the app
        System.out.println(">>> USER ROLE: GUEST ");
        processAnnotationsAndExecute(service, "GUEST");

        // Let's pretend an ADMIN is using the app
        System.out.println("\n>>> USER ROLE: ADMIN ");
        processAnnotationsAndExecute(service, "ADMIN");
    }
}
