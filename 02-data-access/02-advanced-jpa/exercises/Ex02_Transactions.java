/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex02_Transactions.java                                 ║
 * ║  MODULE : 02-data-access / 02-advanced-jpa / exercises          ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — Implement service-layer transaction║
 * ║                   management with @Transactional                 ║
 * ║  DIFFICULTY     : Intermediate                                   ║
 * ║  PYTHON COMPARE : "async with db.begin():" for transactions     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Transaction Flow                                ║
 * ║                                                                   ║
 * ║    transfer(from=1, to=2, amount=100)                            ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    ── BEGIN TRANSACTION ──                                        ║
 * ║        │                                                          ║
 * ║        ├── Load Account 1 (balance=500)                           ║
 * ║        ├── Load Account 2 (balance=200)                           ║
 * ║        │                                                          ║
 * ║        ├── Check: 500 >= 100? ──Yes──→ Proceed                    ║
 * ║        │              └──No──→ ROLLBACK + throw                   ║
 * ║        │                                                          ║
 * ║        ├── Debit: Account 1 → 400                                 ║
 * ║        ├── Credit: Account 2 → 300                                ║
 * ║        │                                                          ║
 * ║        ├── logAuditEvent() ← REQUIRES_NEW (separate TX)          ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    ── COMMIT ──                                                   ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : Not standalone — add to a Spring Boot app     ║
 * ║  EXPECTED OUTPUT: Transfer success/fail + audit log persists    ║
 * ║  RELATED FILES  : 02-understanding-transactions.md              ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * VERIFICATION:
 * 1. Test the transfer with sufficient funds → should succeed.
 * 2. Test with insufficient funds → should rollback (no money lost).
 * 3. Test audit log on failed transfer → log should still persist
 *    (REQUIRES_NEW ensures independent transaction).
 */

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BankAccountService — exercises transaction management patterns.
 *
 * <p><b>ASCII — @Transactional Proxy Mechanism:</b>
 * <pre>
 *   Caller
 *       │
 *       ▼
 *   [ Spring AOP Proxy ]     ← intercepts calls to @Transactional methods
 *       │
 *       ├── BEGIN TRANSACTION
 *       ▼
 *   [ Actual Method ]         ← your code runs here
 *       │
 *       ├── Success? → COMMIT
 *       └── Exception? → ROLLBACK
 *
 *   ⚠ GOTCHA: this.logAuditEvent() bypasses the proxy!
 *     Solution: Inject self or use a separate bean.
 * </pre>
 */
@Service
public class BankAccountService {

    private final BankAccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;

    public BankAccountService(BankAccountRepository accountRepository,
                                AuditLogRepository auditLogRepository) {
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
    }

    // ========================================================================
    // TODO 1: Add @Transactional annotation.
    // This method modifies data → needs a read-write transaction.
    // It should rollback on ANY exception (including checked exceptions).
    // Hint: @Transactional(rollbackFor = Exception.class)
    // ========================================================================
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount)
            throws InsufficientFundsException {

        BankAccount from = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new RuntimeException("Account not found: " + fromAccountId));
        BankAccount to = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new RuntimeException("Account not found: " + toAccountId));

        if (from.getBalance().compareTo(amount) < 0) {
            // Log the failed attempt BEFORE throwing the exception
            logAuditEvent("TRANSFER_FAILED",
                "Insufficient funds: " + fromAccountId + " → " + toAccountId);
            throw new InsufficientFundsException(
                "Account " + fromAccountId + " has insufficient funds");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        // JPA dirty checking generates UPDATE SQL automatically at commit
        logAuditEvent("TRANSFER_SUCCESS",
            amount + " from " + fromAccountId + " to " + toAccountId);
    }

    // ========================================================================
    // TODO 2: Add @Transactional with Propagation.REQUIRES_NEW.
    // This ensures the audit log is saved in a SEPARATE transaction.
    // Even if the main transfer rolls back, the audit entry persists.
    //
    // Python equivalent: Creating a new Session() for the audit log,
    //   independent of the main transaction.
    // ========================================================================
    public void logAuditEvent(String eventType, String details) {
        AuditLog log = new AuditLog();
        log.setEventType(eventType);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    // ========================================================================
    // TODO 3: Add @Transactional(readOnly = true).
    // This is a read-only query — readOnly hint optimizes Hibernate
    // by disabling dirty checking (no automatic UPDATE detection).
    // ========================================================================
    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
            .map(BankAccount::getBalance)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }

    // ========================================================================
    // TODO 4: Add @Transactional(readOnly = true).
    // ========================================================================
    public List<AuditLog> getAuditHistory() {
        return auditLogRepository.findAll();
    }

    // ========================================================================
    // TODO 5 (BONUS): Implement a method to transfer money between accounts
    // that belong to DIFFERENT banks. Use Propagation.REQUIRES_NEW for
    // the external bank's operation so it can be independently committed.
    //
    // Scenario: If the local debit succeeds but the external credit fails,
    // the REQUIRES_NEW transaction on the external side rolls back independently.
    //
    // Hint: This is why distributed transactions are hard!
    // ========================================================================
}

/**
 * Custom checked exception for insufficient funds.
 * Remember: Spring does NOT rollback on checked exceptions by default!
 * That's why TODO 1 needs rollbackFor = Exception.class.
 */
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
