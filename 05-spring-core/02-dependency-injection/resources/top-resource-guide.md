# 02-Dependency-Injection — Top Resource Guide

External learning resources only. No internal repo links.

## Official Documentation

- **Spring Framework Reference — Dependency Injection** — Core DI mechanics
  https://docs.spring.io/spring-framework/reference/core/beans/dependencies.html

- **Spring Framework Reference — @Autowired, @Qualifier, @Primary**
  https://docs.spring.io/spring-framework/reference/core/beans/annotation-config.html

## Books

- **"Spring in Action, 7th Edition"** by Craig Walls (Manning, 2024)
  — Chapter 1 Sections 1.2-1.3 cover DI patterns thoroughly.
  — Best first read for understanding constructor vs setter injection.

- **"Pro Spring 6"** by Iuliana Cosmina et al. (Apress, 2023)
  — Chapter 3 covers all injection styles with detailed comparison tables.
  — Includes @Qualifier, @Primary, and collection injection.

## Blogs — Highest Signal

- **Baeldung** (baeldung.com)
  - "Constructor Dependency Injection in Spring" — definitive tutorial
  - "Guide to Spring @Autowired" — covers all injection types
  - "Spring @Qualifier Annotation" — disambiguation patterns

- **Reflectoring** (reflectoring.io)
  - "Why Field Injection is Evil" — the strongest case against field injection

## Videos

- **Java Brains** YouTube — "Spring Framework" playlist
  — Videos 5-10 cover DI patterns with excellent diagrams

- **Dan Vega** YouTube — "Spring Boot for Beginners"
  — Modern, concise DI tutorials (2024)

## Concepts Checklist — Before Moving to 03-Bean-Lifecycle

- [ ] Can explain why constructor injection is preferred over field injection
- [ ] Know that single-constructor classes don't need `@Autowired`
- [ ] Understand `@Primary` vs `@Qualifier` and when to use each
- [ ] Can inject `List<T>` to get all implementations of an interface
- [ ] Know the prototype-in-singleton trap and how to fix it
- [ ] Can write a unit test with mock dependencies (no Spring context)
- [ ] Understand the circular dependency problem and how constructor injection exposes it
