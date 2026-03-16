/**
 * ====================================================================
 * FILE    : Ex01_BankTransactionException.java
 * MODULE  : 05 — Exception Handling
 * PURPOSE : Practice custom exceptions with a banking domain
 * ====================================================================
 *
 * EXERCISES:
 *
 *   1. Create a TransactionException hierarchy:
 *      TransactionException (base, extends RuntimeException)
 *        ├── InsufficientBalanceException (amount, balance)
 *        ├── InvalidAccountException (accountId)
 *        └── TransactionLimitExceededException (amount, dailyLimit)
 *
 *   2. Implement BankAccount with transfer() that:
 *      - Validates accounts exist
 *      - Checks sufficient balance
 *      - Enforces daily transaction limit ($10,000)
 *      - Throws appropriate custom exceptions
 *
 *   3. Write a main() that demonstrates catching each exception type
 *
 * DESIGN HINT:
 *   ┌─────────────────────────────────────────────┐
 *   │  TransactionException                        │
 *   │  ├── message (inherited from Throwable)     │
 *   │  ├── timestamp (when error occurred)        │
 *   │  └── transactionId (for audit trail)        │
 *   │                                             │
 *   │  InsufficientBalanceException               │
 *   │  ├── extends TransactionException           │
 *   │  ├── accountId                              │
 *   │  ├── requested                              │
 *   │  └── available                              │
 *   └─────────────────────────────────────────────┘
 *
 * ====================================================================
 */
import java.time.LocalDateTime;

public class Ex01_BankTransactionException {

    // ── Step 1: Create base TransactionException ────────────────────
    // TODO: Create TransactionException extends RuntimeException
    //       with fields: transactionId (String), timestamp (LocalDateTime)

    // ── Step 2: Create InsufficientBalanceException ─────────────────
    // TODO: extends TransactionException
    //       with fields: accountId, requested, available

    // ── Step 3: Create InvalidAccountException ──────────────────────
    // TODO: extends TransactionException
    //       with field: accountId

    // ── Step 4: Create TransactionLimitExceededException ────────────
    // TODO: extends TransactionException
    //       with fields: amount, dailyLimit

    // ── Step 5: BankAccount class ───────────────────────────────────
    //
    // Use a Map<String, Double> for account balances
    // Implement: transfer(fromId, toId, amount) throws custom exceptions
    //
    // VALIDATION ORDER:
    //   1. Check fromAccount exists → InvalidAccountException
    //   2. Check toAccount exists   → InvalidAccountException
    //   3. Check daily limit        → TransactionLimitExceededException
    //   4. Check balance            → InsufficientBalanceException
    //   5. Execute transfer

    public static void main(String[] args) {
        System.out.println("=== Bank Transaction Exception Exercise ===\n");

        // TODO: Create BankAccount with some test accounts
        // TODO: Test successful transfer
        // TODO: Test transfer from non-existent account
        // TODO: Test transfer with insufficient balance
        // TODO: Test transfer exceeding daily limit
        // TODO: Demonstrate catching base type (TransactionException)

        System.out.println("Implement the exercise above!");
    }
}
