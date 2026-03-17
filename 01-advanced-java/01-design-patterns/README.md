# 01 — Design Patterns

> **Gang of Four patterns that Spring is built on.**

## Why Patterns Matter

Design patterns are not academic exercises — they are the **architecture of Spring Boot**. Every `@Component`, `@Bean`, and `@EventListener` is a pattern in action.

## Pattern Catalog

| Pattern | Category | Spring Usage | Python Equivalent |
|---------|----------|-------------|-------------------|
| **Singleton** | Creational | `@Component` (default scope) | Module-level instance |
| **Factory Method** | Creational | `BeanFactory`, `FactoryBean` | `classmethod` factory |
| **Builder** | Creational | `ResponseEntity.ok().body()` | Fluent APIs |
| **Observer** | Behavioral | `ApplicationEvent` + `@EventListener` | `signal/slot`, callbacks |
| **Strategy** | Behavioral | Auth providers, `ResourceLoader` | First-class functions |
| **Template Method** | Behavioral | `JdbcTemplate`, `RestTemplate` | Abstract base class |
| **Decorator** | Structural | `BufferedReader`, filter chains | `@functools.wraps` |
| **Adapter** | Structural | `HandlerAdapter`, `MessageConverter` | Duck typing |

## Study Path

```
01-singleton → 02-factory → 03-builder     (Creational: how to create)
      ↓
04-observer → 05-strategy → 06-template    (Behavioral: how to communicate)
      ↓
07-decorator → 08-adapter                   (Structural: how to compose)
```
