# 00 — Java Foundation

> **Python bridge:** Java is Python with static types, explicit compilation, and OOP at its core.
> Every file has Python comparison comments so you always have an anchor.

## Why This Module Exists

Spring Boot is built on Java. If you don't deeply understand the JVM type system, OOP contracts, collection internals, functional pipelines, and thread mechanics, Spring will feel like magic that randomly breaks. This module eliminates that.

## Python → Java Mental Model

```
Python world you know:          Java world you're entering:
────────────────────────────    ────────────────────────────
x = 5 (dynamic, any type)      int x = 5; (static, fixed type)
list = [1, "hi", 3.14]         List<String> list (homogeneous)
def greet(): ...                public void greet() { ... }
class Dog: ...                  public class Dog { ... }
try: ... except: ...            try { } catch (Ex e) { }
lambda x: x * 2                (x) -> x * 2
[x for x in items if ...]      items.stream().filter().map()
threading.Thread(target=fn)     new Thread(runnable).start()
```

## Module Structure

| # | Sub-Topic | Key Insight for Spring |
|---|-----------|----------------------|
| 01 | [Java Basics](01-java-basics/) | `int` is not `Integer`; autoboxing trap |
| 02 | [OOP Fundamentals](02-oop-fundamentals/) | `extends` is single; `super()` must be first |
| 03 | [Advanced OOP](03-advanced-oop/) | Generics power `List<Product>` in Spring Data |
| 04 | [Strings & Arrays](04-strings-and-arrays/) | String immutability; `==` vs `.equals()` trap |
| 05 | [Exception Handling](05-exception-handling/) | Spring converts checked → RuntimeException |
| 06 | [Collections](06-collections/) | Wrong collection choice = silent perf bug |
| 07 | [Functional Java](07-functional-java/) | Streams & lambdas are everywhere in Spring |
| 08 | [Multithreading](08-multithreading/) | Spring Boot handles each request in a thread |

## The Learning Loop

```
┌──────────────────────────────────────────────────────────────────┐
│  THE LEARNING LOOP (repeat for every sub-topic)                   │
│                                                                    │
│  1. READ   README.md + MINDMAP.md  → big picture + visual map    │
│  2. READ   explanation/*.md        → concept + ASCII + Mermaid   │
│  3. ANSWER interview questions     → test understanding NOW       │
│  4. READ   explanation/*.java      → see it in annotated code     │
│  5. RUN    the demo file           → ./gradlew :module:run        │
│  6. WRITE  exercises               → build from scratch           │
└──────────────────────────────────────────────────────────────────┘
```

## How to Run

```bash
# Compile and run any demo
./gradlew :00-java-foundation:run --args="HowJavaWorks"
./gradlew :00-java-foundation:run --args="VariablesDemo"

# Run all tests
./gradlew :00-java-foundation:test
```

Review the [MINDMAP.md](MINDMAP.md) to visualize how all 8 sub-topics connect.
