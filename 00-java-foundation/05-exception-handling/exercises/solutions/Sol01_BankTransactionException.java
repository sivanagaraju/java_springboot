/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_BankTransactionException.java                    ║
 * ║  MODULE : 00-java-foundation / 05-exception-handling             ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — custom exception hierarchy          ║
 * ║  DEMONSTRATES   : exception inheritance, cause chaining, context ║
 * ║  PYTHON COMPARE : Python class X(Exception): Java extends        ║
 * ║                                                                  ║
 * ║  HIERARCHY:                                                      ║
 * ║   RuntimeException                                               ║
 * ║    └── TransactionException (base with transactionId + timestamp)║
 * ║         ├── InsufficientBalanceException                         ║
 * ║         ├── InvalidAccountException                              ║
 * ║         └── TransactionLimitExceededException                    ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

// ── Exception Hierarchy ──────────────────────────────────────────────

/**
 * Base exception for all banking transaction failures.
 *
 * <p>WHY extends RuntimeException: Spring convention — unchecked exceptions
 * don't pollute method signatures with checked throws declarations.
 * Callers choose to handle them; they're not forced to.
 *
 * <p>WHY carry transactionId + timestamp: every exception must be traceable
 * in audit logs. Without these, a "transaction failed" log entry is useless
 * — you can't correlate it to the offending transaction record.
 */
class TransactionException extends RuntimeException {

    private final String transactionId;
    private final LocalDateTime timestamp;

    /**
     * Creates a TransactionException with audit context.
     *
     * @param message       human-readable error description
     * @param transactionId unique identifier for the failed transaction
     */
    public TransactionException(String message, String transactionId) {
        super(message);
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now(); // WHY capture at construction: failure time
    }

    /**
     * Creates a TransactionException wrapping a lower-level cause.
     *
     * @param message       human-readable error description
     * @param transactionId unique identifier for the failed transaction
     * @param cause         the underlying exception that triggered this one
     */
    public TransactionException(String message, String transactionId, Throwable cause) {
        super(message, cause); // WHY pass cause: preserves original stack trace
        this.transactionId = transactionId;
        this.timestamp = LocalDateTime.now();
    }

    /** Returns the transaction ID for audit lookup. */
    public String getTransactionId() { return transactionId; }

    /** Returns the timestamp when this exception was created. */
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("%s[txId=%s, at=%s]: %s",
                getClass().getSimpleName(), transactionId,
                timestamp.format(DateTimeFormatter.ISO_LOCAL_TIME), getMessage());
    }
}

/**
 * Thrown when an account has insufficient funds for a transfer.
 *
 * <p>Carries both the requested and available amounts so the error handler
 * can construct a meaningful message for the user without re-querying the DB.
 */
class InsufficientBalanceException extends TransactionException {

    private final String accountId;
    private final double requested;
    private final double available;

    /**
     * Creates an InsufficientBalanceException.
     *
     * @param accountId the account that lacks funds
     * @param requested the amount attempted
     * @param available the actual balance
     * @param txId      the transaction ID for audit
     */
    public InsufficientBalanceException(String accountId, double requested,
                                        double available, String txId) {
        // WHY format message in super: the message is set once, immutably
        super(String.format("Account %s: requested %.2f but only %.2f available",
                accountId, requested, available), txId);
        this.accountId = accountId;
        this.requested = requested;
        this.available = available;
    }

    public String getAccountId() { return accountId; }
    public double getRequested()  { return requested; }
    public double getAvailable()  { return available; }
}

/**
 * Thrown when an account ID does not exist in the system.
 */
class InvalidAccountException extends TransactionException {

    private final String accountId;

    /**
     * Creates an InvalidAccountException.
     *
     * @param accountId the unrecognized account ID
     * @param txId      the transaction ID for audit
     */
    public InvalidAccountException(String accountId, String txId) {
        super("Account not found: " + accountId, txId);
        this.accountId = accountId;
    }

    public String getAccountId() { return accountId; }
}

/**
 * Thrown when a transfer would exceed the daily transaction limit.
 */
class TransactionLimitExceededException extends TransactionException {

    private final double amount;
    private final double dailyLimit;

    /**
     * Creates a TransactionLimitExceededException.
     *
     * @param amount     the attempted transfer amount
     * @param dailyLimit the configured daily limit
     * @param txId       the transaction ID for audit
     */
    public TransactionLimitExceededException(double amount, double dailyLimit, String txId) {
        super(String.format("Amount %.2f exceeds daily limit of %.2f", amount, dailyLimit), txId);
        this.amount = amount;
        this.dailyLimit = dailyLimit;
    }

    public double getAmount()     { return amount; }
    public double getDailyLimit() { return dailyLimit; }
}

// ── BankAccount Service ──────────────────────────────────────────────

/**
 * Simulated bank account service demonstrating exception-driven validation.
 *
 * <p>Validation order matters: check existence first (no point checking
 * balance on a non-existent account), then limits, then balance.
 */
class BankService {

    private static final double DAILY_LIMIT = 10_000.00;
    // WHY Map: simulates an in-memory account store
    private final Map<String, Double> accounts = new HashMap<>();

    /** Adds a test account with the given balance. */
    public void addAccount(String id, double balance) {
        accounts.put(id, balance);
    }

    /**
     * Transfers money between two accounts.
     *
     * <p>Validation order:
     * <ol>
     *   <li>From account exists</li>
     *   <li>To account exists</li>
     *   <li>Amount within daily limit</li>
     *   <li>Sufficient balance</li>
     * </ol>
     *
     * @param fromId source account ID
     * @param toId   destination account ID
     * @param amount transfer amount (must be positive)
     * @param txId   unique transaction identifier for audit
     * @throws InvalidAccountException             if either account is unknown
     * @throws TransactionLimitExceededException   if amount exceeds daily limit
     * @throws InsufficientBalanceException        if source lacks funds
     */
    public void transfer(String fromId, String toId, double amount, String txId) {
        // WHY check fromId first: most common failure, fail fast
        if (!accounts.containsKey(fromId)) {
            throw new InvalidAccountException(fromId, txId);
        }
        if (!accounts.containsKey(toId)) {
            throw new InvalidAccountException(toId, txId);
        }
        if (amount > DAILY_LIMIT) {
            throw new TransactionLimitExceededException(amount, DAILY_LIMIT, txId);
        }

        double available = accounts.get(fromId);
        if (amount > available) {
            throw new InsufficientBalanceException(fromId, amount, available, txId);
        }

        // WHY execute only after all validations pass: partial transfers
        // (debit without credit) would leave accounts in inconsistent state
        accounts.put(fromId, available - amount);
        accounts.put(toId, accounts.get(toId) + amount);

        System.out.printf("  ✓ Transferred %.2f from %s to %s [txId=%s]%n",
                amount, fromId, toId, txId);
    }

    /** Returns the current balance for an account (for verification). */
    public double getBalance(String id) {
        return accounts.getOrDefault(id, 0.0);
    }
}

// ── Test Harness ────────────────────────────────────────────────────

/**
 * Demonstrates all exception paths in the banking service.
 */
public class Sol01_BankTransactionException {

    /**
     * Runs all transfer scenarios, catching specific exception types.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== Bank Transaction Exception Solution ===\n");

        BankService bank = new BankService();
        bank.addAccount("ACC001", 5_000.00);
        bank.addAccount("ACC002", 1_000.00);

        // ── Scenario 1: Successful transfer ─────────────────────────────
        System.out.println("--- Scenario 1: Successful Transfer ---");
        try {
            bank.transfer("ACC001", "ACC002", 500.00, "TX-001");
            System.out.printf("  ACC001 balance: %.2f%n", bank.getBalance("ACC001"));
        } catch (TransactionException e) {
            System.out.println("  ✗ " + e);
        }

        // ── Scenario 2: Invalid source account ──────────────────────────
        System.out.println("\n--- Scenario 2: Invalid Account ---");
        try {
            bank.transfer("UNKNOWN", "ACC002", 100.00, "TX-002");
        } catch (InvalidAccountException e) {
            System.out.println("  ✗ InvalidAccount: " + e.getMessage());
            System.out.println("    txId=" + e.getTransactionId());
        }

        // ── Scenario 3: Insufficient balance ────────────────────────────
        System.out.println("\n--- Scenario 3: Insufficient Balance ---");
        try {
            bank.transfer("ACC002", "ACC001", 9_000.00, "TX-003");
        } catch (InsufficientBalanceException e) {
            System.out.printf("  ✗ Insufficient: requested=%.2f available=%.2f%n",
                    e.getRequested(), e.getAvailable());
        }

        // ── Scenario 4: Daily limit exceeded ────────────────────────────
        System.out.println("\n--- Scenario 4: Daily Limit Exceeded ---");
        try {
            bank.transfer("ACC001", "ACC002", 15_000.00, "TX-004");
        } catch (TransactionLimitExceededException e) {
            System.out.printf("  ✗ LimitExceeded: amount=%.2f limit=%.2f%n",
                    e.getAmount(), e.getDailyLimit());
        }

        // ── Scenario 5: Catch base type (handles all transaction errors) ─
        System.out.println("\n--- Scenario 5: Catching Base Type ---");
        try {
            bank.transfer("ACC001", "GHOST", 100.00, "TX-005");
        } catch (TransactionException e) {
            // WHY catch base type: handler doesn't need to know the specific subtype
            // — it just logs and returns an error response
            System.out.println("  ✗ Transaction failed [" + e.getTransactionId() + "]: "
                    + e.getMessage());
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Swallowing cause information
 *   WRONG: throw new TransactionException(e.getMessage(), txId);
 *   PROBLEM: If e is an IOException, you lose the original stack trace
 *            and the IOException type. getCause() returns null.
 *   RIGHT: throw new TransactionException("DB error", txId, e);
 *
 * MISTAKE 2: Not setting timestamp at construction time
 *   WRONG: setting timestamp in a getter (lazy evaluation)
 *   PROBLEM: The timestamp reflects when you READ the exception, not
 *            when it was THROWN — confusing in multi-thread scenarios.
 *   RIGHT: Capture LocalDateTime.now() in the constructor.
 *
 * MISTAKE 3: Extending Exception (checked) for domain exceptions
 *   WRONG: class TransactionException extends Exception
 *   PROBLEM: Every calling method must declare throws TransactionException.
 *            Spring's @Transactional rollback won't trigger automatically.
 *   RIGHT: extends RuntimeException for Spring-style domain exceptions.
 *
 * MISTAKE 4: Executing the transfer before all validations pass
 *   Debit fromId, then fail while crediting toId → money disappears.
 *   Always validate everything before mutating any state.
 *
 * MISTAKE 5: Catching Exception instead of specific subclass
 *   catch (Exception e) in production code silently swallows unexpected
 *   exceptions (NullPointerException, etc.) that should propagate.
 *   Be specific in your catch blocks.
 * ═══════════════════════════════════════════════════════════════════ */
