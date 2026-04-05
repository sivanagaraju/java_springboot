/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_BankAccount.java                                 ║
 * ║  MODULE : 00-java-foundation / 02-oop-fundamentals               ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — encapsulation with BankAccount      ║
 * ║  DEMONSTRATES   : private fields, getters, no raw setter         ║
 * ║  PYTHON COMPARE : Python uses @property; Java uses explicit get()║
 * ║                                                                  ║
 * ║  ENCAPSULATION PRINCIPLE:                                        ║
 * ║   External code ──▶ public methods ──▶ private fields            ║
 * ║   Direct field access is blocked at compile time                 ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

/**
 * Demonstrates encapsulation: data hidden behind controlled access methods.
 *
 * <p>Python equivalent uses {@code @property} decorators and name-mangling
 * ({@code __balance}) for pseudo-privacy. Java enforces it at compile time
 * via the {@code private} modifier — the compiler rejects direct field access.
 */
class BankAccount {

    // WHY private final: accountNumber must never change after creation.
    // final prevents reassignment; private prevents external access.
    private final String accountNumber;

    // WHY private (not final): balance changes via deposit/withdraw,
    // but only through controlled methods — not direct assignment.
    private double balance;

    /**
     * Creates a new BankAccount.
     *
     * <p>Defensive initialization: negative starting balance is silently
     * corrected to 0.0 rather than throwing — accounts always start non-negative.
     *
     * @param accountNumber the unique account identifier
     * @param initialBalance starting balance; negative values treated as 0.0
     */
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        // WHY Math.max: silently correct negative initial balance.
        // Throwing here is also valid — depends on whether invalid input
        // is a programming error (throw) or expected user mistake (correct).
        this.balance = Math.max(0.0, initialBalance);
    }

    /**
     * Returns the account number. Read-only — no setter provided.
     *
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Returns the current balance.
     *
     * @return current balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Deposits the given amount into this account.
     *
     * <p>Negative or zero deposits are silently ignored — they are
     * not errors but no-ops. In a production system you'd log them.
     *
     * @param amount the amount to deposit; must be positive
     */
    public void deposit(double amount) {
        // WHY guard clause: negative deposits are no-ops, not exceptions.
        // This keeps callers from needing try-catch for routine validation.
        if (amount <= 0) {
            return;
        }
        balance += amount;
    }

    /**
     * Withdraws the given amount from this account.
     *
     * @param amount the amount to withdraw; must be positive and <= balance
     * @return {@code true} if the withdrawal succeeded; {@code false} otherwise
     */
    public boolean withdraw(double amount) {
        // WHY two conditions: amount must be positive AND balance must cover it.
        // A negative withdrawal would otherwise increase the balance — a bug.
        if (amount <= 0 || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    /** Returns a human-readable representation for debugging. */
    @Override
    public String toString() {
        // WHY format: currency values should show 2 decimal places
        return String.format("BankAccount{id='%s', balance=%.2f}", accountNumber, balance);
    }
}

/**
 * Test harness for BankAccount encapsulation exercise.
 */
public class Sol01_BankAccount {

    /**
     * Runs all test scenarios.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("--- Encapsulation Solution ---\n");

        BankAccount account = new BankAccount("AC123456", 500.00);
        System.out.println("Account Number: " + account.getAccountNumber()); // AC123456
        System.out.println("Initial Balance: " + account.getBalance());      // 500.0

        account.deposit(150.00);
        System.out.println("After deposit 150: " + account.getBalance());    // 650.0

        account.deposit(-50.00); // WHY: should be ignored silently
        System.out.println("After bad deposit: " + account.getBalance());    // 650.0

        boolean ok1 = account.withdraw(100.00);
        System.out.println("Withdraw 100: " + ok1 + " | Balance: " + account.getBalance()); // true | 550.0

        boolean ok2 = account.withdraw(9999.00);
        System.out.println("Withdraw 9999: " + ok2 + " | Balance: " + account.getBalance()); // false | 550.0

        // WHY negative initial balance test:
        BankAccount negStart = new BankAccount("AC999", -100);
        System.out.println("\nNegative start corrected: " + negStart.getBalance()); // 0.0

        System.out.println("\n" + account);

        // Security: the following lines would be COMPILE ERRORS (not shown at runtime):
        // account.balance = 5000000;       // Error: balance has private access
        // account.accountNumber = "HACK";  // Error: accountNumber has private access
        System.out.println("\n✓ Direct field access blocked at compile time");
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Providing a raw setter for balance
 *   WRONG: public void setBalance(double balance) { this.balance = balance; }
 *   PROBLEM: Breaks encapsulation — any code can set balance to any value.
 *   RIGHT: Only expose deposit() and withdraw() which enforce business rules.
 *
 * MISTAKE 2: Making accountNumber mutable
 *   WRONG: private String accountNumber; (without final)
 *   RIGHT: private final String accountNumber;
 *          final at the field level prevents reassignment anywhere in the class.
 *
 * MISTAKE 3: Forgetting 'this.' in constructor with same-name params
 *   WRONG: accountNumber = accountNumber;  // assigns param to itself, field unchanged
 *   RIGHT: this.accountNumber = accountNumber;
 *
 * MISTAKE 4: Using == for String comparison in business logic
 *   WRONG: if (accountNumber == "AC123")   // reference comparison, likely false
 *   RIGHT: if (accountNumber.equals("AC123"))
 *
 * MISTAKE 5: Not checking negative in withdraw
 *   WRONG: if (amount > balance) return false;
 *   PROBLEM: withdraw(-100) would increase balance. Always check amount > 0.
 * ═══════════════════════════════════════════════════════════════════ */
