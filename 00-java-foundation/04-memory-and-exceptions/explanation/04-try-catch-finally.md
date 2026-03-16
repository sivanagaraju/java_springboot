# Try, Catch, and Finally

Handling exceptions is often taught as a way to prevent crashes. Architecturally, it is about controlling the flow of execution when the system enters an invalid state, ensuring external resources are not leaked.

## The Basic Structure

```java
try {
    // Risky code that might interact with the database or file system.
    System.out.println("Processing...");
    throw new RuntimeException("Database offline");
} catch (RuntimeException e) {
    // Fallback logic, logging, or alerting.
    System.out.println("Caught: " + e.getMessage());
} finally {
    // Guaranteed to run, even if an exception is thrown or a return statement is hit.
    System.out.println("Cleaning up resources...");
}
```

## The Return Value Trap

The `finally` block executes *after* the `try` or `catch` block resolves, but *before* control is returned to the caller. This leads to a notorious interview question and architectural trap.

```java
public int calculate() {
    try {
        return 10;
    } finally {
        return 20; // This overrides the 10!
    }
}
```
**Architect Rule:** Never place a `return` statement inside a `finally` block. It silently swallows exceptions and overrides intentional return values from the `try` block.

## Rethrowing and Wrapping Exceptions

In modern distributed systems, low-level exceptions should be wrapped in high-level business exceptions.

```java
public void loadUserData(int userId) {
    try {
        database.query("SELECT * FROM users WHERE id = " + userId);
    } catch (SQLException e) {
        // Bad: Exposing database internals to the caller.
        // throw e; 
        
        // Good: Wrapping in a standard RuntimeException.
        throw new UserDataLoadException("Failed to load user: " + userId, e);
    }
}
```
Here, we pass `e` as the second parameter. This preserves the original Stack Trace entirely, allowing developers to see exactly where the failure originated in the database driver.
