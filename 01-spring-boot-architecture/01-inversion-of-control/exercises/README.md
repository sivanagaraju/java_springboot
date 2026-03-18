# Module 1 Exercises: Inversion of Control

These exercises test your theoretical understanding of IoC without requiring a full Spring Boot runtime environment physically booting up. Spring Boot simplifies things, but you must conceptually understand what the framework is mapping natively in the background.

## 🛠️ Exercise 1: Refactoring to Constructor Injection
**Goal:** Prove you can break Tight Coupling.
**Tasks in `Ex01_ConstructorInjection.java`:**
1. You are given a tightly coupled `UserService` that physically instantiates a `UserRepository`.
2. Refactor `UserService` to utilize pure Constructor Injection securely.
3. Validate that you can write a simulated "Unit Test" inside `main()` by providing a mock repository natively.

## 🧪 Exercise 2: Understanding Bean Scopes
**Goal:** Prove you comprehend exactly how Singletons behave in memory.
**Tasks in `Ex02_BeanScopes.java`:**
1. A fake "Spring Container" returns an instance of a Singleton `MetricsService`.
2. Two separate simulated threads will request the `MetricsService` and mutate its internal state.
3. Observe and document why storing state in a Singleton is a catastrophic architectural disaster.
