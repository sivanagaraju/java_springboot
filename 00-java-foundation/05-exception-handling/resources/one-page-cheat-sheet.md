# Exception Handling — One-Page Cheat Sheet

## Exception Hierarchy

```
Throwable
├── Error                          (JVM errors — never catch)
│   ├── OutOfMemoryError
│   └── StackOverflowError
└── Exception
    ├── IOException                (checked — must handle)
    ├── SQLException               (checked — must handle)
    └── RuntimeException           (unchecked — optional)
        ├── NullPointerException
        ├── IllegalArgumentException
        ├── IllegalStateException
        └── UnsupportedOperationException
```

## Key Syntax

```java
// Basic try-catch-finally
try {
    riskyOperation();
} catch (IOException e) {            // most specific first
    log.error("IO error", e);
} catch (Exception e) {              // most general last
    log.error("Unexpected", e);
} finally {
    cleanup();                        // ALWAYS runs (except System.exit)
}

// Multi-catch (Java 7+)
catch (IOException | SQLException e) {
    // e is effectively final here
}

// Try-with-resources (Java 7+) — implements AutoCloseable
try (BufferedReader reader = Files.newBufferedReader(path);
     Connection conn = dataSource.getConnection()) {
    // both closed automatically, even on exception
}
// close() exceptions → getSuppressed()[0], NOT swallowed

// Re-throw with cause preservation
try {
    externalApi.call();
} catch (IOException e) {
    throw new ServiceException("API call failed", e);  // pass e as cause!
}

// Checked exception to unchecked
throw new RuntimeException(checkedException);       // wrap
throw new UncheckedIOException(ioException);        // specific wrapper
```

## Custom Exception Best Practices

```java
// Always extend RuntimeException for Spring apps
public class PaymentException extends RuntimeException {
    private final String paymentId;

    public PaymentException(String message, String paymentId) {
        super(message);
        this.paymentId = paymentId;
    }

    public PaymentException(String message, String paymentId, Throwable cause) {
        super(message, cause);  // always provide cause constructor!
        this.paymentId = paymentId;
    }
}
```

## Spring Exception Handling

```java
// @ControllerAdvice — global handler
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handle(PaymentException ex) {
        return ResponseEntity.status(402).body(
            new ErrorResponse(ex.getMessage(), ex.getPaymentId())
        );
    }

    @ExceptionHandler(Exception.class)  // catch-all
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(500).body(new ErrorResponse("Internal error"));
    }
}

// @ResponseStatus on exception class
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException { ... }

// @Transactional rollback — default: RuntimeException only
@Transactional(rollbackFor = Exception.class)  // include checked
@Transactional(noRollbackFor = OptimisticLockException.class)
```

## Python Bridge

| Java | Python |
|------|--------|
| `try { } catch (IOException e) { }` | `try: except IOError as e:` |
| `finally { }` | `finally:` |
| `try-with-resources` | `with open(f) as file:` |
| Checked exceptions | No equivalent (type hints only) |
| `throws IOException` in signature | No equivalent |
| `e.getCause()` | `e.__cause__` |
| `e.getSuppressed()` | No equivalent |
| `throw new X(msg, cause)` | `raise X(msg) from cause` |
| `RuntimeException` (unchecked) | All exceptions are "unchecked" |

## Common Traps

```
TRAP 1: Swallowing exceptions
  catch (Exception e) { log.error("failed"); }  // stack trace lost!
  Fix: always pass e to the logger: log.error("failed", e)

TRAP 2: Catching and re-throwing without cause
  throw new ServiceException(e.getMessage());   // stack trace lost!
  Fix: throw new ServiceException(msg, e)       // pass original as cause

TRAP 3: Exception in finally swallows original
  try { return result; }
  finally { closeFile(); }  // if this throws, result is lost!
  Fix: wrap the finally body in its own try-catch

TRAP 4: Wrong catch order (compile error)
  catch (Exception e) { }
  catch (IOException e) { }  // UNREACHABLE — won't compile
  Fix: specific first, general last

TRAP 5: @Transactional won't rollback on checked exceptions
  @Transactional void save() throws IOException  // no rollback!
  Fix: @Transactional(rollbackFor = Exception.class)

TRAP 6: Multi-catch variable is effectively final
  catch (IOException | SQLException e) {
    e = new IOException();  // compile error!
  }
```
