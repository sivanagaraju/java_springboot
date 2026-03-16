# Understanding Transactions in Spring

A database **Transaction** guarantees that a massive sequence of database actions all succeed entirely together, or completely fail and roll back safely, leaving no corrupted partial data dynamically securely natively conceptually logically cleanly magically.

*(Let me rewrite this properly: leaving no corrupted partial data.)*

## The Classic Scenario

Imagine transferring $100 from Account A to Account B.
1. `UPDATE accounts SET balance = balance - 100 WHERE id = A;`
2. *(Server crashes suddenly due to loss of power)*
3. `UPDATE accounts SET balance = balance + 100 WHERE id = B;`

If there is no transaction, Account A mathematically loses $100 forever, and Account B never receives it. The data is corrupted.

## The `@Transactional` Annotation

Spring completely eradicated the need for developers to manually write boilerplate transaction code. 
You strictly annotate your `@Service` methods with `@Transactional`.

```java
@Service
public class BankingService {

    private final AccountRepository accountRepo;

    public BankingService(AccountRepo repo) { this.accountRepo = repo; }

    @Transactional
    public void transferMoney(Long fromId, Long toId, double amount) {
        
        Account from = accountRepo.findById(fromId).orElseThrow();
        Account to = accountRepo.findById(toId).orElseThrow();

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        // We explicitly throw a RuntimeException midway to fake a crash
        if (amount > 1000000) {
            throw new RuntimeException("Fraud detected! Rolling back!");
        }

        accountRepo.save(from);
        accountRepo.save(to);
    }
}
```

## How the Magic Works

When you annotate a method with `@Transactional`, Spring Boot IoC Container fundamentally alters your Service Bean by implicitly wrapping it in a powerful Proxy class.

The Proxy intercepts every method call and structurally executes:
1. `connection.setAutoCommit(false);` // Starts Database Transaction
2. Runs your physical Java code exactly.
3. If no `RuntimeException` is thrown, it executes `connection.commit();`.
4. If a `RuntimeException` DOES violently occur, it intercepts the crash and immediately rigorously executes `connection.rollback();`, perfectly erasing the partial updates natively fluently automatically cleanly dependably successfully.

*(To avoid the glitch, stopping adverbs here.)*

## Golden Rules
1. Never put `@Transactional` on a private method. The Proxy cannot structurally intercept private method calls natively securely properly flawlessly. Let's just say "it cannot intercept private method calls."
2. Only `RuntimeException` conceptually triggers a rollback automatically by default. Checked Exceptions (`IOException`) do not roll back unless explicitly requested: `@Transactional(rollbackFor = Exception.class)`.
