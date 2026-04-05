# 01 - Inversion of Control (IoC) and Dependency Injection (DI)

This is the foundation of the Spring Framework. If we do not understand how Spring takes over object creation, wiring, and lifecycle management, the rest of Spring Boot will look like unexplained magic.

> Python bridge: in Python, you often wire dependencies explicitly with constructors or factory functions. Spring does the same work, but the container owns the object graph and injects dependencies at startup.

## Why This Module Matters
Traditional Java code tends to couple classes together with `new`. That makes testing harder, swapping implementations harder, and production debugging much more fragile. Spring's IoC container solves that by moving object creation out of the application code.

## Container Mental Model

```mermaid
flowchart LR
    A[Controller] --> B[Service interface]
    B --> C[ApplicationContext]
    C --> D[Concrete service bean]
    D --> E[Repository bean]
```

## What You Will Learn
- Tight coupling versus IoC
- `ApplicationContext` as the bean registry and lifecycle manager
- Constructor injection as the preferred enterprise style
- Bean scopes and lifecycle callbacks
- Component scanning and stereotype annotations

## Directory Structure
- `/explanation`: Deep theory, Mermaid diagrams, Python comparisons, and interview questions
- `/exercises`: Hands-on coding tasks to prove the concepts

## How to Proceed
1. Read the explanation files in order.
2. Run the Java demos in `explanation/` to see the container model in action.
3. Finish the exercises once the IoC story is clear.
