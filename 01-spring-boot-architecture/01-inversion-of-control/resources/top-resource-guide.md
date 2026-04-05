# IoC & Dependency Injection — Top Resource Guide

Curated external resources to deepen your understanding beyond this module.

---

## Official Documentation

| Resource | URL | Best For |
|---|---|---|
| Spring Framework Core — IoC Container | https://docs.spring.io/spring-framework/docs/current/reference/html/core.html | Definitive reference on beans, DI, scopes |
| Spring Boot Beans & Dependency Injection | https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.spring-beans-and-dependency-injection | Spring Boot specific DI guidance |
| Spring Framework Annotation Reference | https://docs.spring.io/spring-framework/docs/current/javadoc-api/ | JavaDoc for @Component, @Autowired, @Qualifier |

---

## Books

| Title | Author | Why Read It |
|---|---|---|
| *Spring in Action, 6th ed.* | Craig Walls | Best beginner-to-intermediate Spring book; chapters 1–3 cover IoC deeply |
| *Spring Boot: Up & Running* | Mark Heckler | Fast-paced, practical; good chapter on DI and auto-configuration |
| *Dependency Injection Principles, Practices, and Patterns* | Seemann & van Deursen | Language-agnostic deep dive into DI patterns, anti-patterns, and trade-offs |

---

## Blog Posts & Articles

| Title | Source | Why Read It |
|---|---|---|
| "Constructor Injection vs Field Injection" | Baeldung | Concrete comparison with code; covers testability argument |
| "Spring Bean Scopes" | Baeldung | Covers singleton, prototype, request, session, application with examples |
| "Self-Invocation & AOP Proxies" | Baeldung | Explains why `this.method()` bypasses `@Transactional` |
| "Understanding Spring @Autowired" | Reflectoring.io | Explains autowiring modes with modern constructor injection focus |

Search: "site:baeldung.com spring constructor injection" or "site:reflectoring.io spring dependency injection"

---

## Videos

| Title | Channel | Why Watch |
|---|---|---|
| *Spring Framework Tutorial* (playlist) | Java Brains (YouTube) | Long-form, concept-first; excellent IoC and DI episodes |
| *Spring Boot Tutorial for Beginners* | Amigoscode (YouTube) | Project-based; shows DI in a real REST API context |
| *SOLID Principles in Spring* | Coding with John (YouTube) | Connects DI to SOLID, specifically DIP |

---

## Interactive

| Resource | URL | Why Use It |
|---|---|---|
| Spring Initializr | https://start.spring.io | Scaffold a project to experiment with DI live |
| Baeldung Spring IoC guides | https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring | Step-by-step hands-on with runnable code |
