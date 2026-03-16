# Custom Exceptions

Junior developers often use generic `RuntimeException` or `IllegalArgumentException` for every failure.
Java Architects design domain-specific custom exceptions to clearly define the failure conditions of the business logic.

## Why Create Custom Exceptions?

1. **Granular Catching:** You can catch `UserNotFoundException` differently than `DatabaseTimeoutException`.
2. **Clearer Logs:** Seeing `InsufficientFundsException` in DataDog or Splunk is instantly actionable. Seeing `RuntimeException: balance too low` requires reading the stack trace.
3. **HTTP Status Mapping:** In Spring Boot, custom exceptions can automatically map to specific HTTP Status codes (e.g., `UserNotFoundException` -> 404 Not Found).

## Creating a Business Exception

Modern custom exceptions ALWAYS extend `RuntimeException`. They should provide constructors that accept custom error messages and root cause throwables.

```java
public class InsufficientFundsException extends RuntimeException {
    
    // Store relevant business data for the error handler
    private final double requestedAmount;
    private final double currentBalance;

    public InsufficientFundsException(String message, double requestedAmount, double currentBalance) {
        super(message);
        this.requestedAmount = requestedAmount;
        this.currentBalance = currentBalance;
    }

    public InsufficientFundsException(String message, Throwable cause, double requestedAmount, double currentBalance) {
        super(message, cause);
        this.requestedAmount = requestedAmount;
        this.currentBalance = currentBalance;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }
}
```

## Throwing the Custom Exception

```java
public void withdraw(double amount) {
    if (amount > this.balance) {
        throw new InsufficientFundsException(
            String.format("Withdrawal failed. Requested: %.2f, Balance: %.2f", amount, balance),
            amount, 
            this.balance
        );
    }
    this.balance -= amount;
}
```

By embedding the exact `requestedAmount` and `currentBalance` into the Exception object itself, the global error handler can extract those values and generate a highly detailed error response for the API consumer without parsing String messages.
