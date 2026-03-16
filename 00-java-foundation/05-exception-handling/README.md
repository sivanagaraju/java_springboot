# 05 — Exception Handling

## WHY This Topic Matters

Every production Java application handles errors. Spring Boot's entire error-handling mechanism (controller advice, response entity, transaction rollback) is built on Java's exception hierarchy. If you don't understand checked vs unchecked, try-with-resources, and custom exceptions, Spring error handling will be opaque.

## Python Comparison

| Concept | Python | Java |
|---------|--------|------|
| Base class | `BaseException` | `Throwable` |
| Catch syntax | `except ValueError as e:` | `catch (ValueError e)` |
| User error | `Exception` | `Exception` (checked) or `RuntimeException` (unchecked) |
| System crash | `SystemExit`, `KeyboardInterrupt` | `Error` (OutOfMemoryError, StackOverflowError) |
| Resource cleanup | `with open(...) as f:` | `try (var f = ...) { }` (try-with-resources) |
| Raise | `raise ValueError("msg")` | `throw new IllegalArgumentException("msg")` |
| Declare | (not required) | `throws IOException` (checked only) |

## The Key Difference: Checked vs Unchecked

```
Python: ALL exceptions are unchecked. You CAN catch them, but you're never FORCED to.
Java:   CHECKED exceptions FORCE you to handle them at compile time.
        UNCHECKED exceptions (RuntimeException) behave like Python.
```

## Study Path

1. `01-exception-hierarchy.md` → `ExceptionHierarchyDemo.java`
2. `02-custom-exceptions.md` → `CustomExceptionDemo.java`
3. `03-try-with-resources.md` → `TryWithResourcesDemo.java`
4. `04-throws-vs-throw.md`
5. Complete exercises in `exercises/`

Review the [MINDMAP.md](MINDMAP.md) to visualize the full exception class hierarchy.
