# 07 — Functional Java (Lambdas, Streams, Optional)

## Why This Matters

Java 8 introduced lambdas and streams — the biggest change to Java since generics. In Spring Boot, you'll use these constantly: filtering query results, mapping DTOs, Optional return types from repositories, and reactive programming builds directly on these concepts.

If you know Python's `map()`, `filter()`, `lambda`, and list comprehensions, you already understand the concepts. The syntax is just different.

## Python → Java Quick Map

| Python | Java | Notes |
|--------|------|-------|
| `lambda x: x * 2` | `x -> x * 2` | Lambda expression |
| `map(fn, list)` | `list.stream().map(fn)` | Transform each element |
| `filter(fn, list)` | `list.stream().filter(fn)` | Keep elements matching condition |
| `[x*2 for x in list]` | `list.stream().map(x -> x*2).toList()` | Stream pipeline |
| `sum(list)` | `list.stream().reduce(0, Integer::sum)` | Reduce to single value |
| `sorted(list, key=fn)` | `list.stream().sorted(Comparator.comparing(fn))` | Sort with key |
| `None` handling | `Optional<T>` | Explicit nullable wrapper |

## Sub-topics

| # | File | Concept |
|---|------|---------|
| 1 | `01-lambda-expressions.md` | Lambda syntax, functional interfaces, method references |
| 2 | `02-streams-api.md` | Stream pipeline: source → intermediate → terminal |
| 3 | `03-optional.md` | Optional as null-safe return type |
| 4 | `LambdaDemo.java` | Lambda expressions and method references |
| 5 | `StreamsDemo.java` | Complete stream pipeline examples |
| 6 | `OptionalDemo.java` | Optional patterns and anti-patterns |

## Exercises

- `Ex01_StreamPractice.java` — Data processing with streams
- `Ex02_OptionalChaining.java` — Null-safe navigation with Optional

## Study Path

1. Read `01-lambda-expressions.md` → understand the syntax
2. Run `LambdaDemo.java` → experiment with lambdas
3. Read `02-streams-api.md` → understand the pipeline model
4. Run `StreamsDemo.java` → process real data
5. Read `03-optional.md` → understand null-safety
6. Run `OptionalDemo.java` → see Optional in practice
7. Complete exercises
