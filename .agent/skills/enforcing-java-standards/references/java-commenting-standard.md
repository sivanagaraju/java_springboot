# Java Commenting Standard — 4 Layers

Every `.java` file in spring-mastery uses this exact 4-layer commenting standard.
ASCII diagrams are MANDATORY in Layers 1, 2, and 3 — Mermaid cannot render in Java comments.

## Contents

- Layer 1: File Header (every .java file)
- Layer 2: Class-Level Javadoc (every class)
- Layer 3: Method-Level Javadoc (every public method)
- Layer 4: Inline Comments (complex logic only)
- ASCII Diagram Type Reference
- Complete Example File

---

## Layer 1 — File Header (every .java file)

Use the `╔══╗` box format. The ASCII diagram in the header shows the concept's
architecture or data flow — it is not optional.

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ClassName.java                                        ║
 * ║  MODULE : XX-module-name / YY-subtopic                          ║
 * ║  GRADLE : ./gradlew :XX-module-name:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : One sentence — what this file demonstrates    ║
 * ║  WHY IT EXISTS  : What problem existed before this feature      ║
 * ║  PYTHON COMPARE : Python/FastAPI equivalent pattern             ║
 * ║  USE CASES      : 3–4 real-world scenarios                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM  (replace with concept-appropriate diagram)       ║
 * ║                                                                   ║
 * ║    HTTP Request                                                   ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    [ @RestController ]  ← validates input, maps HTTP ↔ DTO      ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    [ @Service ]         ← business logic, @Transactional        ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    [ Repository ]       ← data access                           ║
 * ║        │                                                          ║
 * ║        ▼                                                          ║
 * ║    [ PostgreSQL ]                                                 ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :module-name:bootRun                ║
 * ║  EXPECTED OUTPUT: describe what the console/API shows           ║
 * ║  RELATED FILES  : OtherClass.java, 01-concept.md                ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
```

---

## Layer 2 — Class-Level Javadoc (every class)

Must include: responsibility description, Python equivalent, ASCII showing
where this class fits in the architecture.

```java
/**
 * ProductService — business logic layer for the Product domain.
 *
 * <p>Handles all product-related operations: creation, inventory management,
 * pricing updates, and soft deletion. All database writes are transactional.</p>
 *
 * <p><b>Python FastAPI equivalent:</b>
 * <pre>
 *   class ProductService:
 *       def __init__(self, repo: ProductRepo = Depends(get_db)):
 *           self.repo = repo
 *
 *       def create(self, req: CreateProductRequest) -> ProductResponse:
 *           ...
 * </pre>
 * In Spring, dependency injection is handled by the container — no
 * explicit Depends() call needed in method signatures.
 *
 * <p><b>ASCII — Where this class sits in the layered architecture:</b>
 * <pre>
 *   HTTP Request
 *       │
 *       ▼
 *   [ ProductController ]   ← validates input, calls this service
 *       │
 *       ▼
 *   [ ProductService ]      ← business rules  ← YOU ARE HERE
 *       │
 *       ▼
 *   [ ProductRepository ]   ← JPA data access
 *       │
 *       ▼
 *   [ PostgreSQL ]
 * </pre>
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
```

---

## Layer 3 — Method-Level Javadoc (every public method)

Must include: contract description, Python equivalent, ASCII flow for non-trivial
methods, `@param`, `@return`, `@throws` tags.

```java
/**
 * Transfer funds between two accounts atomically.
 *
 * <p>Both accounts must exist and the source must have sufficient balance.
 * The entire operation runs in a single database transaction — if any step
 * fails, all changes are rolled back.</p>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   async def transfer(from_id: int, to_id: int, amount: Decimal,
 *                      db: AsyncSession = Depends(get_db)):
 *       async with db.begin():
 *           from_acct = await db.get(Account, from_id)
 *           to_acct   = await db.get(Account, to_id)
 *           from_acct.balance -= amount
 *           to_acct.balance   += amount
 * </pre>
 *
 * <p><b>Flow:</b>
 * <pre>
 *   transfer(fromId, toId, amount)
 *       │
 *       ▼
 *   Load both accounts ──→ Not found? → throw AccountNotFoundException
 *       │
 *       ▼
 *   Check balance ──→ Insufficient? → throw InsufficientFundsException
 *       │
 *       ▼
 *   Debit fromAccount.balance
 *   Credit toAccount.balance
 *       │
 *       ▼
 *   JPA dirty-check → generates UPDATE SQL at transaction commit
 * </pre>
 *
 * @param fromId  ID of the source account (must exist and have sufficient balance)
 * @param toId    ID of the destination account (must exist)
 * @param amount  Amount to transfer (must be positive)
 * @return        TransferResult with new balances for both accounts
 * @throws AccountNotFoundException   if either account ID does not exist
 * @throws InsufficientFundsException if source account balance < amount
 */
@Transactional
public TransferResult transfer(Long fromId, Long toId, BigDecimal amount) {
```

---

## Layer 4 — Inline Comments (complex logic only)

Only for: non-obvious business rules, security gotchas, performance decisions,
"why not the obvious approach" explanations.

NEVER for obvious code — do not comment `i++; // increment i`.

```java
// Hibernate generates: WHERE id = ? AND version = ? (optimistic lock check)
// If another thread updated this row, version won't match → OptimisticLockException
@Version
private Long version;

// BCrypt work factor 12 = ~250ms hash time on modern hardware
// High enough to slow brute-force, low enough for login UX
// Never use factor < 10 in production
private static final int BCRYPT_STRENGTH = 12;

// ServiceLoader reads META-INF/services/java.sql.Driver — no Class.forName() needed since Java 6
Connection conn = DriverManager.getConnection(url, user, pass);
```

---

## ASCII Diagram Type Reference

Choose the ASCII type that best represents the concept being documented:

| ASCII Type | When to Use | Characters |
|-----------|------------|-----------|
| Vertical flow | Request pipelines, algorithm steps | `│ ▼ ──→` |
| Layered stack | Architecture layers, Spring layers | `[ Layer ]` |
| Decision tree | Conditional logic, error paths | `──→ condition? → result` |
| State machine | Entity lifecycle, bean lifecycle | `State1 ──→ State2` |
| Comparison table | Type mappings, option tradeoffs | Aligned columns |
| Box diagram | Component boundaries, module structure | `┌─────┐ │ └─────┘` |

---

## Complete Example File

See [examples/ProductServiceImpl.java](../examples/ProductServiceImpl.java) for a
complete demonstration of all 4 layers applied to a realistic Spring Boot service class.
