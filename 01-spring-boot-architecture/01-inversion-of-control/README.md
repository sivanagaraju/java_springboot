# 01 - Inversion of Control (IoC) & Dependency Injection (DI)

Welcome to the foundation of the Spring Framework. Before you can write a functional Spring Boot application, you must master the core architectural pattern that Spring is built upon: **Inversion of Control**.

> **Python Bridge:** In Python, global variables, singletons, or explicit structural wiring (`my_service = Service(db_conn)`) dictate how objects find each other. Spring automates this completely by taking control over object creation and injecting the required pieces at runtime, much like an advanced, automated factory.

## 🎯 Why This Module Matters
Traditional Java applications tightly couple their classes together using the `new` keyword. This makes unit testing impossible and adapting to business changes incredibly painful. Spring's **IoC Container** solves this by physically separating the creation of objects from their usage.

## 📚 What You Will Learn
1. **Tight Coupling:** Why the `new` keyword is dangerous in enterprise architecture.
2. **IoC Container:** How the `ApplicationContext` manages the lifecycle of your objects.
3. **Dependency Injection:** Why Constructor Injection is the only acceptable method for wiring dependencies.
4. **Bean Scopes:** The difference between Singletons and Prototypes, and why Singletons must remain stateless.
5. **Component Scanning:** How Spring uses stereotypes (`@Component`, `@Service`, `@Repository`) to automatically discover your code.

## 📂 Directory Structure
- `/explanation`: Start here. Contains detailed theory, Mermaid diagrams, Python comparisons, and Interview Q&A.
- `/exercises`: Hands-on coding exercises. Read the instructions in the `README.md` inside, modify the `.java` files, and run them to prove you understand the concepts.

## 🚀 How to Proceed
Start by deeply reading the files in the `explanation` folder in numbered order (01 → 05). Then, run the `.java` demos inside that folder to view the console output. Finally, tackle the `exercises`.
