# Exception Handling — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting.

## Exercises

### Ex01 — Bank Transaction Exceptions (`Ex01_BankTransactionException.java`)
Build a custom exception hierarchy for banking: `TransactionException` → `InsufficientBalanceException`, `InvalidAccountException`, `TransactionLimitExceededException`. Implement `BankAccount.transfer()`.

**Skills:** Custom exception classes, exception hierarchy, checked vs unchecked, catch order
**Time estimate:** 30 minutes

### Ex02 — File Reader with Resources (`Ex02_FileReaderWithResources.java`)
Implement `readFileContents()`, `copyFile()`, and a custom `AutoCloseable` `LogWriter`. Demonstrates proper resource management.

**Skills:** `try-with-resources`, `AutoCloseable`, `FileNotFoundException` vs `IOException`, multi-resource try
**Time estimate:** 25 minutes

## Solutions
See `solutions/` directory after attempting.

## Connection to Spring Boot
- Ex01 → Spring's `@ResponseStatus(HttpStatus.BAD_REQUEST)` on custom exceptions
- Ex01 → `@ControllerAdvice` + `@ExceptionHandler` — handle domain exceptions at REST layer
- Ex02 → Spring's `ResourceUtils`, `ClassPathResource` — file reading patterns
