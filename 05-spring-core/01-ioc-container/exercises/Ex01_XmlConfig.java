/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_XmlConfig.java                                    ║
 * ║  MODULE : 05-spring-core / 01-ioc-container / exercises           ║
 * ║  GRADLE : ./gradlew :05-spring-core:test                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — understand XML configuration so you ║
 * ║                   can read legacy Spring apps                     ║
 * ║  WHY IT EXISTS  : Many enterprise apps still use XML config.     ║
 * ║                   Reading XML is essential; writing it is not.   ║
 * ║  PYTHON COMPARE : Python has no XML config — always code-based  ║
 * ║                   This exercise shows why Java moved to annots   ║
 * ║  USE CASES      : 1) Reading legacy Spring 2.x/3.x codebases   ║
 * ║                   2) Understanding Spring's evolution             ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — XML Bean Registration                            ║
 * ║                                                                    ║
 * ║    applicationContext.xml                                          ║
 * ║        │                                                           ║
 * ║        ├── <bean id="userRepo" class="...UserRepo"/>              ║
 * ║        ├── <bean id="userService" class="...UserService">         ║
 * ║        │       <constructor-arg ref="userRepo"/>                  ║
 * ║        │   </bean>                                                 ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    ClassPathXmlApplicationContext("applicationContext.xml")        ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    context.getBean("userService") → fully wired UserService       ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE INSTRUCTIONS:                                           ║
 * ║  1. Read the XML config below (as a String — no file needed)     ║
 * ║  2. Answer: Which beans are registered? What are their deps?      ║
 * ║  3. Convert the XML config to @Configuration + @Bean              ║
 * ║  4. Run the test to verify your understanding                     ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.exercises;

/**
 * Exercise: Understand XML configuration and convert to Java Config.
 *
 * <p><b>Python equivalent of this exercise:</b>
 * <pre>
 *   # Python doesn't have this problem — config is always code!
 *   # But imagine reading a YAML config and converting to Python code.
 * </pre>
 *
 * <p><b>ASCII — Conversion task:</b>
 * <pre>
 *   XML (legacy):                     Java Config (modern):
 *   ─────────────                     ───────────────────
 *   &lt;bean id="..." class="..."&gt;       @Bean
 *     &lt;constructor-arg ref="..."/&gt;   public Type name() {
 *   &lt;/bean&gt;                              return new Type(dep);
 *                                     }
 * </pre>
 */
public class Ex01_XmlConfig {

    /*
     * === XML Configuration to Read (Legacy Spring 2.x format) ===
     *
     * <beans>
     *   <bean id="messageRepository" class="com.learning.MessageRepository"/>
     *
     *   <bean id="messageService" class="com.learning.MessageService">
     *     <constructor-arg ref="messageRepository"/>
     *     <property name="defaultLanguage" value="en"/>
     *   </bean>
     *
     *   <bean id="notificationService" class="com.learning.NotificationService">
     *     <constructor-arg ref="messageService"/>
     *   </bean>
     * </beans>
     */

    // ─── YOUR TASK: Answer these questions ───

    // Q1: How many beans are registered in the XML above?
    // Answer: 3 beans — messageRepository, messageService, notificationService

    // Q2: What is the dependency chain?
    // Answer: notificationService → messageService → messageRepository

    // Q3: Which injection types are used?
    // Answer: constructor-arg = constructor injection, property = setter injection

    // ─── YOUR TASK: Convert to @Configuration ───

    /*
     * TODO: Create a @Configuration class that produces the same beans as the XML above.
     *
     * @Configuration
     * public class AppConfig {
     *     @Bean
     *     public MessageRepository messageRepository() {
     *         return new MessageRepository();
     *     }
     *
     *     @Bean
     *     public MessageService messageService() {
     *         MessageService service = new MessageService(messageRepository());
     *         service.setDefaultLanguage("en");
     *         return service;
     *     }
     *
     *     @Bean
     *     public NotificationService notificationService() {
     *         return new NotificationService(messageService());
     *     }
     * }
     */

    // Placeholder classes for the exercise
    static class MessageRepository {
        public String findMessage(String key) { return "Message: " + key; }
    }

    static class MessageService {
        private final MessageRepository repo;
        private String defaultLanguage;

        public MessageService(MessageRepository repo) { this.repo = repo; }
        public void setDefaultLanguage(String lang) { this.defaultLanguage = lang; }
        public String getMessage(String key) { return repo.findMessage(key) + " [" + defaultLanguage + "]"; }
    }

    static class NotificationService {
        private final MessageService messageService;

        public NotificationService(MessageService messageService) { this.messageService = messageService; }
        public String notify(String key) { return "NOTIFICATION: " + messageService.getMessage(key); }
    }

    public static void main(String[] args) {
        // Manual wiring (simulating what XML config does)
        MessageRepository repo = new MessageRepository();
        MessageService service = new MessageService(repo);
        service.setDefaultLanguage("en");
        NotificationService notification = new NotificationService(service);

        System.out.println(notification.notify("welcome"));
        System.out.println("✅ Exercise complete — you understand XML → Java Config conversion!");
    }
}
