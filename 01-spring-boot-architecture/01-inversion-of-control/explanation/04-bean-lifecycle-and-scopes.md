# Bean Scopes and Lifecycles

Whenever Spring Boot registers a class into the IoC Container, it defines exactly how long this object should live. This is known as the Bean **Scope**.

## 1. The Singleton Scope (Default Context)

By default, every annotated component (`@Service`, `@Controller`, `@Repository`) in Spring Boot operates under the **Singleton Scope**.

**Architectural Mechanics:**
- Spring instantiates exactly one instance of your class on the Heap during application startup.
- Every subsequent injection request receives the exact same memory pointer.
- **Critical Danger:** Singletons must be entirely **STATELESS**. 
- If you declare a class-level variable `private String currentUser` inside a Singleton, Thread A and Thread B will overwrite each other, causing catastrophic data corruption. You must only store autowired dependencies at the class level.

## 2. The Prototype Scope

**Mechanics:**
- If you annotate a class with `@Scope("prototype")`, Spring changes the behavior.
- Every single time another class demands this bean, Spring physically calls `new` and hands over a brand new instance.
- Spring does not track Prototype beans after giving them away. It is up to the Garbage Collector.

**Use Case:**
Rare. Usually used when you need a complex object to hold state for a specific short-lived algorithm and you want Spring to build its inner dependencies before handing it to you.

## 3. Web-Aware Scopes

If working inside a web application (Spring MVC), Spring offers specialized scopes:
- `@RequestScope`: A brand new instance is generated for every single HTTP Request. Very safe for holding state for one user's click.
- `@SessionScope`: A single instance is generated and maintained for a user's entire HTTP Session (e.g., a Shopping Cart object).

## The Bean Lifecycle

What happens during the exact moment Spring builds your Singletons?

1. **Instantiation:** Spring calls the constructor.
2. **Dependency Injection:** Spring pushes the required beans into the constructor arguments.
3. **Initialization Callback:** Spring calls any method annotated with `@PostConstruct`. You use this to run setup logic (e.g., priming a cache after the DB connection is injected).
4. **Use:** The application runs.
5. **Destruction Callback:** Before shutting down, Spring calls any method annotated with `@PreDestroy`. You use this to close network sockets cleanly.
