# 01 — Advanced Java

> **Prerequisite:** Complete `00-java-foundation` first (OOP, collections, multithreading).

## What You'll Master

This module covers three pillars that separate junior from mid-level Java developers:

| Sub-Topic | Why It Matters |
|-----------|---------------|
| **Design Patterns** | Reusable solutions to common OOP problems — the vocabulary every Java/Spring developer speaks |
| **File I/O & Serialization** | Reading/writing files, streams, NIO.2 API, and JSON processing with Jackson |
| **Java New Features (9–21)** | Records, sealed classes, pattern matching, virtual threads — modern Java |

## Python → Java Mental Model

| Python Concept | Java Equivalent |
|---------------|-----------------|
| `@abstractmethod` + duck typing | Strategy/Template Method pattern |
| Context managers (`with`) | try-with-resources (already learned) |
| `json.dumps/loads` | Jackson `ObjectMapper` |
| `pathlib.Path` | `java.nio.file.Path` |
| `@dataclass` | Records (Java 16+) |
| `match/case` (3.10+) | Pattern matching (Java 21) |
| `asyncio` | Virtual threads (Java 21) |

## Study Path

```
01-design-patterns ──→ Learn the "why" behind Spring's architecture
        │
02-file-io-serialization ──→ Handle data persistence & JSON
        │
03-java-new-features ──→ Modern Java that you'll use daily
        │
mini-project-02-library-system ──→ Apply everything together
```

## 🔗 Spring Connection

```
Design Patterns → Spring IS patterns:
  Singleton     → @Component (default scope)
  Factory       → BeanFactory
  Observer      → ApplicationEvent
  Template      → JdbcTemplate, RestTemplate
  Strategy      → authentication providers
  Decorator     → filter chains, HandlerInterceptor

File I/O → Spring Resource abstraction
  classpath: → ClassPathResource
  file:      → FileSystemResource
  Jackson    → @RequestBody / @ResponseBody auto-conversion
```
