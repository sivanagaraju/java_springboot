/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_Transactions.java                                ║
 * ║  MODULE : 02-data-access / 02-advanced-jpa / exercises/solutions ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : SOLUTION — @Transactional with propagation    ║
 * ║                   and rollback behavior                          ║
 * ║  DIFFICULTY     : Intermediate                                   ║
 * ║  PYTHON COMPARE : async with db.begin() / separate session      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Transaction Propagation                        ║
 * ║                                                                  ║
 * ║  transfer() ── @Transactional(rollbackFor=Exception.class)      ║
 * ║      │                                                           ║
 * ║      │── BEGIN TX1                                               ║
 * ║      │── debit account 1                                         ║
 * ║      │── credit account 2                                        ║
 * ║      │                                                           ║
 * ║      └── logAuditEvent() ── @Transactional(REQUIRES_NEW)        ║
 * ║              │                                                   ║
 * ║              │── SUSPEND TX1                                     ║
 * ║              │── BEGIN TX2                                       ║
 * ║              │── INSERT audit_log                                ║
 * ║              └── COMMIT TX2 (independent of TX1!)               ║
 * ║                                                                  ║
 * ║  If TX1 rolls back, TX2 (audit) is already committed.           ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

package solutions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Bank account service demonstrating @Transactional best practices.
 *
 * <p>Key lessons:
 * <ol>
 *   <li>Rollback on checked exceptions requires explicit {@code rollbackFor}</li>
 *   <li>Audit logging needs {@code REQUIRES_NEW} to survive main transaction rollback</li>
 *   <li>Read-only queries should declare {@code readOnly=true} for Hibernate optimization</li>
 *   <li>Self-invocation bypasses the AOP proxy — {@code logAuditEvent} must be on a separate bean</li>
 * </ol>
 *
 * <p><b>Python SQLAlchemy equivalent:</b>
 * <pre>
 *   async def transfer(from_id, to_id, amount, db):
 *       async with db.begin() as tx:
 *           from_acc = await db.get(Account, from_id)
 *           to_acc = await db.get(Account, to_id)
 *           from_acc.balance -= amount
 *           to_acc.balance += amount
 *           # audit log in separate session
 *           async with AsyncSession(engine) as audit_session:
 *               audit_session.add(AuditLog(...))
 *               await audit_session.commit()
 * </pre>
 */
@Service
public class Sol02_BankAccountService {

    private final BankAccountRepository accountRepository;
    private final AuditLogRepository auditLogRepository;

    // WHY inject AuditService separately? To fix the self-invocation AOP bypass problem.
    // If logAuditEvent() was on THIS class, calling it via this.logAuditEvent() would
    // bypass the Spring proxy and REQUIRES_NEW would have no effect.
    private final Sol02_AuditService auditService;

    public Sol02_BankAccountService(BankAccountRepository accountRepository,
                                    AuditLogRepository auditLogRepository,
                                    Sol02_AuditService auditService) {
        this.accountRepository = accountRepository;
        this.auditLogRepository = auditLogRepository;
        this.auditService = auditService;
    }

    /**
     * Transfers money between two bank accounts atomically.
     *
     * <p>WHY rollbackFor = Exception.class? By default @Transactional only rolls back
     * on unchecked exceptions (RuntimeException). {@code InsufficientFundsException} is a
     * checked exception — without rollbackFor, a thrown checked exception would COMMIT
     * the partial changes to the database!
     *
     * <p>WHY call auditService.logAuditEvent() instead of this.logAuditEvent()?
     * Self-invocation (this.method()) bypasses the Spring AOP proxy. The audit log
     * uses REQUIRES_NEW which needs to run in a separate transaction. Injecting the
     * audit service as a separate bean ensures the proxy wraps the call correctly.
     *
     * @param fromAccountId the source account ID
     * @param toAccountId   the destination account ID
     * @param amount        the amount to transfer (positive)
     * @throws InsufficientFundsException if the source account lacks sufficient balance
     */
    @Transactional(rollbackFor = Exception.class)
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount)
            throws InsufficientFundsException {

        // Load both accounts — throws RuntimeException (auto-rollback) if not found
        BankAccount from = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new RuntimeException("Account not found: " + fromAccountId));
        BankAccount to = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new RuntimeException("Account not found: " + toAccountId));

        // Business validation — throws checked exception
        if (from.getBalance().compareTo(amount) < 0) {
            // WHY audit BEFORE throwing? The exception will rollback the main transaction.
            // The audit log (in REQUIRES_NEW) must be written BEFORE the rollback.
            auditService.logAuditEvent("TRANSFER_FAILED",
                "Insufficient funds in account " + fromAccountId);
            throw new InsufficientFundsException(
                "Account " + fromAccountId + " has insufficient funds. " +
                "Available: " + from.getBalance() + ", Required: " + amount);
        }

        // Modify entities — Hibernate's dirty checking generates UPDATE SQL at commit
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        // WHY no explicit save()? JPA dirty checking detects the field changes
        // and generates UPDATE SQL when the transaction commits.

        // Audit the successful transfer
        auditService.logAuditEvent("TRANSFER_SUCCESS",
            amount + " transferred from account " + fromAccountId + " to " + toAccountId);
    }

    /**
     * Gets the balance of an account.
     *
     * <p>WHY readOnly=true? Two optimizations:
     * 1. Hibernate disables dirty checking (no need to snapshot all loaded entities).
     * 2. Some databases/JDBC drivers optimize read-only connections for performance.
     * Use readOnly=true on ALL query-only methods.
     *
     * @param accountId the account ID
     * @return the current balance
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long accountId) {
        return accountRepository.findById(accountId)
            .map(BankAccount::getBalance)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
    }

    /**
     * Returns the full audit history.
     *
     * <p>readOnly=true — this is a pure query with no side effects.
     *
     * @return all audit log entries ordered by timestamp
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditHistory() {
        return auditLogRepository.findAll();
    }
}

// ===========================================================================
// Separate AuditService bean — required to avoid self-invocation AOP bypass
// ===========================================================================

/**
 * Audit log service — always writes in its OWN independent transaction.
 *
 * <p>Why a separate class? The AOP proxy only intercepts calls from OUTSIDE the class.
 * If logAuditEvent() were a method on BankAccountService and called via {@code this},
 * the proxy would be bypassed and REQUIRES_NEW would have no effect.
 *
 * <p><b>REQUIRES_NEW behavior:</b> When called from an existing transaction (TX1),
 * Spring suspends TX1, starts a NEW independent transaction (TX2), executes the method,
 * commits TX2, then resumes TX1. TX2's commit is permanent even if TX1 later rolls back.
 */
@Service
class Sol02_AuditService {

    private final AuditLogRepository auditLogRepository;

    public Sol02_AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Persists an audit event in an independent transaction.
     *
     * <p>WHY Propagation.REQUIRES_NEW?
     * When the main transfer() rolls back (e.g., insufficient funds), the audit log
     * must still be committed. REQUIRES_NEW creates a completely independent transaction
     * for the audit entry — it commits or rolls back independently of the caller's transaction.
     *
     * <p>WHY NOT just REQUIRED? REQUIRED joins the existing transaction — if the caller
     * rolls back, the audit log rolls back with it. That defeats the purpose.
     *
     * @param eventType a category label for the event (e.g., "TRANSFER_SUCCESS")
     * @param details   human-readable description of what happened
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAuditEvent(String eventType, String details) {
        AuditLog log = new AuditLog();
        log.setEventType(eventType);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
        // This INSERT is committed immediately — independent of caller's transaction
    }
}

/** Checked exception for business rule violations. */
class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Forgetting rollbackFor on checked exceptions
 *   WRONG: @Transactional (no rollbackFor)
 *   If throw InsufficientFundsException (checked) → Spring COMMITS the partial changes!
 *   FIX: @Transactional(rollbackFor = Exception.class) to rollback on ALL exceptions.
 *
 * MISTAKE 2: Self-invocation bypassing REQUIRES_NEW
 *   WRONG: this.logAuditEvent(...)  // inside BankAccountService
 *   This calls the real object, NOT the proxy. REQUIRES_NEW has no effect.
 *   FIX: Extract audit logging to a separate @Service bean and inject it.
 *
 * MISTAKE 3: Using REQUIRES_NEW everywhere instead of only where needed
 *   REQUIRES_NEW suspends the current transaction and starts a new one.
 *   This uses an extra DB connection from the pool — wasteful if overused.
 *   Use REQUIRES_NEW only when the method MUST commit independently (audit, email).
 *
 * MISTAKE 4: Not using readOnly=true for query methods
 *   Without readOnly=true, Hibernate takes a write lock and performs dirty checking
 *   on all loaded entities — unnecessary overhead for pure reads.
 *   Add @Transactional(readOnly=true) to every service method that only reads data.
 *
 * MISTAKE 5: Calling @Transactional methods from non-Spring objects
 *   If you call a @Transactional method from a plain 'new' object (not a Spring bean),
 *   there is no proxy — the transaction never starts.
 *   FIX: Always inject Spring beans via constructor injection; never use 'new' on @Service.
 */
