# Top Resource Guide — Design Patterns

## Official Documentation

- **[Spring Framework Reference — Application Events](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html)** — Official guide to Spring's Observer/Event system (`ApplicationEventPublisher`, `@EventListener`, `@TransactionalEventListener`)
- **[Spring Security Architecture](https://docs.spring.io/spring-security/reference/servlet/architecture.html)** — Real-world Strategy, Decorator, and Chain of Responsibility patterns in a production framework
- **[Spring Data REST Reference](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)** — Factory and Builder patterns in Spring Data configuration

## Books (Ordered by Priority)

1. **Design Patterns: Elements of Reusable Object-Oriented Software** — Gang of Four (Gamma, Helm, Johnson, Vlissides)
   - The original. Read the introduction and the 8 patterns covered here. Skip the C++ code — the diagrams and intent sections are what matter.

2. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 3: Enforce singleton with private constructor or enum
   - Item 2: Consider a builder when faced with many constructor parameters
   - Items 47-48: Streams and parallel streams (Strategy pattern applied)
   - The most important Java book after GoF.

3. **Head First Design Patterns, 2nd Edition** — Freeman & Robson
   - Best introductory explanation with visual diagrams. Read before GoF if patterns are new to you.

## Blog Posts

- **[Baeldung: Design Patterns in Spring](https://www.baeldung.com/spring-framework-design-patterns)** — Singleton, Factory, Proxy, Template in Spring context
- **[Baeldung: Builder Pattern in Java](https://www.baeldung.com/creational-design-patterns)** — Manual and Lombok `@Builder` implementations
- **[Reflectoring.io: Strategy Pattern](https://reflectoring.io/strategy-pattern-spring/)** — Spring DI-based Strategy injection
- **[Vlad Mihalcea: Template Method in Spring](https://vladmihalcea.com/)** — JdbcTemplate deep dive

## Videos

- **[Java Brains: Design Patterns Series](https://www.youtube.com/@JavaBrains)** — Clear, focused explanations of all GoF patterns with Java code
- **[Amigoscode: Spring Boot + Design Patterns](https://www.youtube.com/@amigoscode)** — Practical patterns in Spring Boot context

## Interactive Practice

- **[Refactoring.Guru](https://refactoring.guru/design-patterns/java)** — Best visual explanations + Java code examples + real-world analogies for every pattern. Bookmark this.
- **[LeetCode Design category](https://leetcode.com/problemset/?topicSlugs=design)** — Design interview problems that directly test Singleton, Factory, Observer, Strategy

## Key Concepts to Verify You Know

- [ ] Why enum singleton defeats both Reflection and Serialization attacks
- [ ] Difference between `@EventListener` and `@TransactionalEventListener(phase=AFTER_COMMIT)`
- [ ] How `JdbcTemplate` implements Template Method (what you provide vs what it handles)
- [ ] Why `@FunctionalInterface` makes Strategy pattern trivially a lambda
- [ ] How Spring's `HandlerAdapter` implements Adapter for `DispatcherServlet`
- [ ] `Decorator vs Proxy` — structural similarity, intent difference
