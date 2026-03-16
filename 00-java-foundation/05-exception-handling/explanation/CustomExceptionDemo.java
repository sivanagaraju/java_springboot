/**
 * ====================================================================
 * FILE    : CustomExceptionDemo.java
 * MODULE  : 05 — Exception Handling
 * PURPOSE : Building domain-specific custom exceptions
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: class InsufficientFundsError(Exception): ...
 *   Java:   class InsufficientFundsException extends RuntimeException { ... }
 *
 * CUSTOM EXCEPTION CLASS HIERARCHY FOR THIS DEMO:
 *
 *   RuntimeException
 *       │
 *       ├── BankException (our base)
 *       │       │
 *       │       ├── InsufficientFundsException
 *       │       │     fields: accountId, requested, available
 *       │       │
 *       │       └── AccountNotFoundException
 *       │             fields: accountId
 *       │
 *       └── (Spring later maps these to HTTP responses)
 *
 * WHEN TO CHOOSE CHECKED vs UNCHECKED:
 *
 *   ┌─────────────────────────────────────────────────────┐
 *   │  Caller can RECOVER?                                │
 *   │    YES → extends Exception (checked)                │
 *   │         e.g., FileNotFoundException → prompt user   │
 *   │                                                     │
 *   │    NO → extends RuntimeException (unchecked)       │
 *   │         e.g., IllegalArgumentException → fix code  │
 *   │         e.g., BusinessRuleViolation → invalid state │
 *   │                                                     │
 *   │  SPRING RULE: always unchecked (auto-rollback)     │
 *   └─────────────────────────────────────────────────────┘
 *
 * ====================================================================
 */
public class CustomExceptionDemo {

    // ═══════════════════════════════════════════════════════════════
    // STEP 1: Define the base exception for our banking domain
    // ═══════════════════════════════════════════════════════════════
    //
    // In a real project, this would be in: com.myapp.exception.BankException.java
    //
    // Chain: BankException → RuntimeException → Exception → Throwable
    //
    static class BankException extends RuntimeException {
        public BankException(String message) {
            super(message);
        }
        public BankException(String message, Throwable cause) {
            super(message, cause);  // ← preserves original stack trace
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // STEP 2: Create specific exceptions with structured data
    // ═══════════════════════════════════════════════════════════════
    //
    // MEMORY LAYOUT of InsufficientFundsException object:
    //
    //   ┌─────────────────────────────────────────────────────┐
    //   │  Object Header (12-16 bytes)                        │
    //   │  ┌────────────────── Throwable fields ────────────┐ │
    //   │  │  message: "Account ACC-001: ..."               │ │
    //   │  │  cause:   null (or wrapped exception)          │ │
    //   │  │  stackTrace: StackTraceElement[]               │ │
    //   │  ├────────────────── Our fields ──────────────────┤ │
    //   │  │  accountId:  "ACC-001"                         │ │
    //   │  │  requested:  500.0                             │ │
    //   │  │  available:  100.0                             │ │
    //   │  └────────────────────────────────────────────────┘ │
    //   └─────────────────────────────────────────────────────┘
    //
    static class InsufficientFundsException extends BankException {
        private final String accountId;
        private final double requested;
        private final double available;

        public InsufficientFundsException(String accountId, double requested, double available) {
            super(String.format("Account %s: requested $%.2f but only $%.2f available",
                    accountId, requested, available));
            this.accountId = accountId;
            this.requested = requested;
            this.available = available;
        }

        public String getAccountId() { return accountId; }
        public double getRequested() { return requested; }
        public double getAvailable() { return available; }
    }

    static class AccountNotFoundException extends BankException {
        private final String accountId;

        public AccountNotFoundException(String accountId) {
            super("Account not found: " + accountId);
            this.accountId = accountId;
        }

        public String getAccountId() { return accountId; }
    }

    // ═══════════════════════════════════════════════════════════════
    // STEP 3: Service that uses custom exceptions
    // ═══════════════════════════════════════════════════════════════
    static class BankService {
        private final java.util.Map<String, Double> accounts = new java.util.HashMap<>();

        public BankService() {
            accounts.put("ACC-001", 1000.0);
            accounts.put("ACC-002", 250.0);
        }

        public void withdraw(String accountId, double amount) {
            // Validate input — throw unchecked for programming errors
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive: " + amount);
            }

            // Check existence — throw domain exception
            Double balance = accounts.get(accountId);
            if (balance == null) {
                throw new AccountNotFoundException(accountId);
            }

            // Check business rule — throw domain exception with data
            if (amount > balance) {
                throw new InsufficientFundsException(accountId, amount, balance);
            }

            accounts.put(accountId, balance - amount);
            System.out.println("  Withdrew $" + amount + " from " + accountId
                    + " | New balance: $" + accounts.get(accountId));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // STEP 4: Handle exceptions at the appropriate level
    // ═══════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        BankService bank = new BankService();

        System.out.println("=== SUCCESSFUL WITHDRAWAL ===");
        bank.withdraw("ACC-001", 200.0);

        System.out.println("\n=== INSUFFICIENT FUNDS (structured exception) ===");
        try {
            bank.withdraw("ACC-002", 500.0);
        } catch (InsufficientFundsException e) {
            // We can access structured data for error handling!
            System.out.println("  Error: " + e.getMessage());
            System.out.println("  Account:   " + e.getAccountId());
            System.out.println("  Requested: $" + e.getRequested());
            System.out.println("  Available: $" + e.getAvailable());
            System.out.println("  Shortfall: $" + (e.getRequested() - e.getAvailable()));
        }

        System.out.println("\n=== ACCOUNT NOT FOUND ===");
        try {
            bank.withdraw("ACC-999", 100.0);
        } catch (AccountNotFoundException e) {
            System.out.println("  Error: " + e.getMessage());
            System.out.println("  Missing account: " + e.getAccountId());
        }

        System.out.println("\n=== INVALID INPUT (programming error) ===");
        try {
            bank.withdraw("ACC-001", -50.0);
        } catch (IllegalArgumentException e) {
            System.out.println("  Error: " + e.getMessage());
        }

        System.out.println("\n=== CATCHING BASE TYPE (polymorphic catch) ===");
        //
        // CATCH HIERARCHY:
        //   BankException catches BOTH InsufficientFundsException
        //   AND AccountNotFoundException
        //
        //   BankException
        //       ├── InsufficientFundsException  ← caught!
        //       └── AccountNotFoundException    ← caught!
        //
        try {
            bank.withdraw("ACC-002", 9999.0);
        } catch (BankException e) {
            System.out.println("  Caught BankException (parent): " + e.getClass().getSimpleName());
            System.out.println("  Message: " + e.getMessage());
        }
    }
}
