# Progressive Quiz Drill — Exception Handling

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** What is the difference between checked and unchecked exceptions? Give two examples of each. Why does Spring favor unchecked exceptions?

**Q2.** A method declares `throws IOException`. The caller doesn't handle it. What happens at compile time? What if you change it to `throws RuntimeException`?

**Q3.** In a `try-catch-finally` block, the `catch` calls `return 42`. Does `finally` still execute?

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** Design a custom exception hierarchy for a payment service. You need: base `PaymentException`, subclasses `InsufficientFundsException` and `PaymentGatewayException`. Show which are checked vs unchecked and why. Include proper constructor chaining.

**Q5.** Write a `readConfig(Path path)` method that opens a file, reads it, and closes it properly. Show three versions: (1) manual try-finally, (2) try-with-resources, (3) what happens if both the `read()` and `close()` throw — which exception wins?

**Q6.** A Spring `@Service` method calls an external API and wraps the checked `IOException` into an unchecked `ServiceException`. The exception propagates to a `@ControllerAdvice` handler. Draw the flow and explain what `getCause()` returns.

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code:
```java
try {
    riskyOperation();
} catch (Exception e) {
    log.error("Failed");
} catch (IOException e) {
    log.error("IO Failed");
}
```
This doesn't compile. Why? Fix it.

**Q8.** Code:
```java
try {
    return processFile();
} finally {
    closeFile();  // closeFile() throws IOException
}
```
`processFile()` returns successfully. What happens? What exception does the caller see?

**Q9.** Code:
```java
public void transfer(double amount) {
    try {
        debit(amount);
        credit(amount);
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage());
    }
}
```
What critical information is lost here? How do you fix it?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're designing exception handling for a microservice that calls 3 external APIs in sequence. Requirements: (1) if API-1 fails, abort and return 503; (2) if API-2 fails, retry once then abort; (3) if API-3 fails, log and continue with partial data; (4) all errors must include correlation IDs; (5) no checked exceptions should leak to the controller layer. Design the complete exception hierarchy, the retry mechanism, and the `@ControllerAdvice` error response structure.

---

## Answer Key

**A1.** Checked: `IOException`, `SQLException` — must be declared or caught, compiler-enforced, for recoverable external conditions. Unchecked: `NullPointerException`, `IllegalArgumentException` — extend `RuntimeException`, no compiler requirement. Spring favors unchecked because: frameworks can't predict which exceptions callers want to handle; wrapping every Spring operation in try-catch would drown application code in ceremony; transaction rollback triggers automatically on `RuntimeException` but NOT on checked exceptions by default.

**A2.** Compile error if caller neither catches `IOException` nor declares `throws IOException`. Changing to `throws RuntimeException`: no compile error — unchecked exceptions don't need to be declared. Callers can still catch `RuntimeException` but aren't forced to. Note: `throws RuntimeException` in the signature is legal but redundant/misleading — omit it.

**A3.** Yes — `finally` ALWAYS executes, even after `return`. JVM saves the return value, runs `finally`, then returns the saved value. Exception: `System.exit()` terminates the JVM before `finally` runs. Also: if `finally` itself contains a `return`, it overrides the `catch`'s return (a notorious bug).

**A4.** Both unchecked (extend `RuntimeException`) — Spring convention. `PaymentException(String message, Throwable cause)` base. `InsufficientFundsException(double requested, double available)` with custom field + `super(String.format("Requested %.2f but only %.2f available", requested, available))`. `PaymentGatewayException(String gatewayCode, Throwable cause)` with `super("Gateway error: " + gatewayCode, cause)`. Pattern: domain exceptions are unchecked, carry rich context, chain cause. `@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)` on the class for automatic HTTP mapping.

**A5.** Manual: `BufferedReader reader = null; try { reader = ...; return reader.readLine(); } finally { if (reader != null) reader.close(); }` — verbose, null check needed. Try-with-resources: `try (BufferedReader reader = Files.newBufferedReader(path)) { return reader.readLine(); }` — clean, `close()` called even on exception. If both throw: without try-with-resources, the `finally` exception propagates and SUPPRESSES the original. With try-with-resources: original exception propagates; `close()` exception is attached via `Throwable.addSuppressed()` — accessible via `e.getSuppressed()[0]`. This is the key reason to prefer try-with-resources.

**A6.** Service catches `IOException`, wraps: `throw new ServiceException("External API failed", ioException)`. `@ControllerAdvice` catches `ServiceException`, calls `ex.getCause()` → returns the original `IOException`. Correct wrapping: always pass cause as constructor argument (`new ServiceException(msg, cause)`) — never `new ServiceException(e.getMessage())` which loses stack trace and cause type.

**A7.** Unreachable catch block — `IOException` is a subclass of `Exception`. The `catch (Exception e)` block already handles all exceptions including `IOException`. Java requires more-specific types first. Fix: swap the order — `catch (IOException e)` first, then `catch (Exception e)`. Rule: always order catches from most-specific to most-general.

**A8.** The caller sees `IOException` from `closeFile()` — it completely replaces the successful return value. The original success is silently discarded. This is the classic "exception in finally swallows result" bug. Fix: `try { return processFile(); } finally { try { closeFile(); } catch (IOException e) { log.warn("Close failed", e); } }` — swallow or log the close exception explicitly when you don't want it to override the result.

**A9.** `e.getMessage()` loses: the exception type, the original stack trace, and any cause chain. `new RuntimeException(e.getMessage())` creates a brand new exception with a new stack trace pointing to the re-throw line — the original stack where the failure happened is gone. Fix: `throw new RuntimeException("Transfer failed", e)` — pass `e` as cause. Even better: define `TransferException(String msg, Throwable cause)` for typed handling.

**A10.** Exception hierarchy: `BaseServiceException(String message, String correlationId, Throwable cause)`. `Api1Exception`, `Api2Exception extends RetryableException`, `Api3Exception extends PartialFailureException`. Retry mechanism: `RetryTemplate` (Spring Retry) with `@Retryable(value = Api2Exception.class, maxAttempts = 2)`. Correlation ID: `MDC.put("correlationId", id)` in a filter, propagate via exception constructor. ControllerAdvice: `@ExceptionHandler(Api1Exception.class)` → 503; `@ExceptionHandler(PartialFailureException.class)` → 200 with `partial: true` in response body; `@ExceptionHandler(BaseServiceException.class)` → generic error response with `correlationId` field. No checked exceptions escape service layer: all wrapped in domain unchecked exceptions at the boundary.
