/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 1: Notification Factory                           ║
 * ║  Build a factory system that creates notification channels  ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * TASK: Implement a notification system using Factory Method.
 *
 * Requirements:
 * 1. Create a Notification interface with send(String message)
 * 2. Implement: EmailNotification, SmsNotification, SlackNotification
 * 3. Create NotificationFactory with create(String type) method
 * 4. Support runtime registration of new notification types
 *
 * BONUS: Make the factory thread-safe using ConcurrentHashMap
 *
 * Expected Output:
 *   📧 [EMAIL] Welcome, John!
 *   📱 [SMS] Your OTP is 123456
 *   💬 [SLACK] Build #42 passed
 *   🔔 [WEBHOOK] Event triggered
 *
 * HINTS:
 * - Use a Map<String, Supplier<Notification>> for the registry
 * - Consider using enum for type safety instead of String keys
 * - Think about how Spring's BeanFactory does the same thing
 */
public class Ex01_NotificationFactory {

    // TODO: Define Notification interface

    // TODO: Implement concrete notifications (Email, SMS, Slack)

    // TODO: Create NotificationFactory with Map-based registry

    // TODO: Add a register() method for runtime extensibility

    public static void main(String[] args) {
        // TODO: Create notifications via factory
        // TODO: Register a new "webhook" type at runtime
        // TODO: Test all notification types
        System.out.println("Exercise 1: Implement the Notification Factory!");
    }
}
