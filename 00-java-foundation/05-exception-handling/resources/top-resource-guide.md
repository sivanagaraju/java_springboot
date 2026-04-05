# Top Resource Guide — Exception Handling

## Official Documentation

- **[Java Tutorials: Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/)** — Oracle's complete guide: checked/unchecked, try-with-resources, chaining
- **[Throwable Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/Throwable.html)** — `getCause()`, `getSuppressed()`, `addSuppressed()` — the full API
- **[AutoCloseable Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/AutoCloseable.html)** — required for try-with-resources

## Books

1. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 69: Use exceptions only for exceptional conditions
   - Item 70: Use checked exceptions for recoverable conditions, runtime exceptions for programming errors
   - Item 71: Avoid unnecessary use of checked exceptions
   - Item 72: Favor standard exceptions
   - Item 73: Throw exceptions appropriate to the abstraction (exception translation)
   - Item 75: Include failure-capture information in detail messages

2. **Clean Code** — Robert C. Martin
   - Chapter 7: Error Handling — "Use unchecked exceptions", "Don't return null", "Don't pass null"

## Blog Posts

- **[Baeldung: Exception Handling in Java](https://www.baeldung.com/java-exceptions)** — Complete tutorial with all patterns
- **[Baeldung: @ControllerAdvice and @ExceptionHandler](https://www.baeldung.com/exception-handling-for-rest-with-spring)** — Spring REST error handling patterns
- **[Baeldung: Try-with-Resources](https://www.baeldung.com/java-try-with-resources)** — Suppressed exceptions, custom AutoCloseable
- **[Baeldung: Spring @Transactional Rollback](https://www.baeldung.com/spring-transactional-rollback-on-exception)** — Checked vs unchecked rollback behavior

## Key Concepts to Verify You Know

- [ ] Why `throw new RuntimeException(e.getMessage())` loses the stack trace — always pass `e` as cause
- [ ] Suppressed exceptions: try-with-resources stores `close()` exceptions via `addSuppressed()` — not lost
- [ ] Exception ordering in multi-catch: specific before general or compile error
- [ ] `@Transactional` default: rolls back on `RuntimeException`, does NOT roll back on checked exceptions
- [ ] Why `finally` with a `return` statement overrides `catch`'s `return` — and why it's a bug magnet
- [ ] Exception translation (Bloch Item 73): lower-layer exceptions should be translated to higher-abstraction exceptions at layer boundaries
