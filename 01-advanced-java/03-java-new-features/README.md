# 03 — Java New Features (9–21)

> **Modern Java: Records, sealed classes, pattern matching, text blocks, and more.**

## Why This Matters

Java releases every 6 months. Features since Java 9 define modern Java — the Java you'll write in Spring Boot 3.x projects. Understanding these features separates a "Java 8 developer" from a "modern Java developer."

## Feature Timeline

| Version | Year | Key Features | Spring Impact |
|---------|------|--------------|---------------|
| **Java 9** | 2017 | Modules, `List.of()`, `Optional.stream()` | Modular Spring 6 |
| **Java 10** | 2018 | `var` local variable | Cleaner Spring code |
| **Java 11** ★ LTS | 2018 | `String` methods, `HttpClient`, `Files.readString()` | Spring Boot 2.x baseline |
| **Java 14** | 2020 | Records (preview), `switch` expressions | DTOs, config props |
| **Java 15** | 2020 | Text blocks, sealed classes (preview) | SQL/JSON templates |
| **Java 16** | 2021 | Records (final), `instanceof` pattern | Spring 6 DTOs |
| **Java 17** ★ LTS | 2021 | Sealed classes (final), pattern matching | Spring Boot 3.0 baseline |
| **Java 21** ★ LTS | 2023 | Virtual threads, sequenced collections | Spring Boot 3.2+ |

## Python → Java

| Python | Java (Modern) |
|--------|---------------|
| `@dataclass` | `record User(String name, int age) {}` |
| `match/case` | `switch` with pattern matching |
| f-strings `f"Hi {name}"` | Text blocks `"""..."""` + `STR.` (Java 21+) |
| `type: int \| str` | `sealed interface` permits specific subtypes |
| `asyncio` | Virtual threads (`Thread.ofVirtual()`) |
