# 01-Hibernate-Basics — Progressive Quiz Drill

## Round 1 — Core Recall (8 questions)
Answer from memory, then check the key.

**Q1:** A `Customer` class has `@Entity` but no `@Table`. What table name does Hibernate use?
**Q2:** You have `@GeneratedValue(strategy = GenerationType.IDENTITY)`. After `session.persist(customer)` but BEFORE `tx.commit()`, does `customer.getId()` return a value?
**Q3:** What are the 4 entity lifecycle states in order?
**Q4:** You add `@Enumerated(EnumType.ORDINAL)` and your enum is `{ACTIVE, INACTIVE, SUSPENDED}`. A year later, a teammate adds `PENDING` between `ACTIVE` and `INACTIVE`. What breaks in the database?
**Q5:** What is the difference between `session.persist()` and `session.merge()`?
**Q6:** You set `hbm2ddl.auto=create` in your production `application.properties`. What happens on the next deployment?
**Q7:** A `Product` entity has `@Transient String displayName`. After persisting and reloading, `displayName` is null. Is this expected? Why?
**Q8:** What is the difference between `session.flush()` and `session.clear()`?

### Answer Key — Round 1
A1: `Customer` (class name lowercase = table name by default convention)
A2: Yes — with IDENTITY strategy, Hibernate executes the INSERT immediately to get the DB-assigned ID, even before tx.commit()
A3: Transient → Persistent → Detached → Removed
A4: All existing rows with INACTIVE (stored as ordinal 1) now map to PENDING. SUSPENDED (stored as 2) now maps to INACTIVE. Silent data corruption — no error thrown.
A5: `persist()` makes a NEW (transient) entity persistent. `merge()` re-attaches a DETACHED entity and returns a new managed instance (the original remains detached).
A6: Hibernate drops all tables and recreates them on startup. All production data is destroyed.
A7: Yes, expected. `@Transient` explicitly tells Hibernate NOT to persist this field. It must be recomputed after reload.
A8: `flush()` synchronizes in-memory entity state to the DB (executes pending SQL) but keeps entities in the session. `clear()` evicts ALL entities from the L1 cache — next access reloads from DB.

---

## Round 2 — Apply & Compare (3 questions)
Translate between SQLAlchemy and Hibernate.

**Q1:** Translate this SQLAlchemy model to a Hibernate entity:
```python
class Product(Base):
    __tablename__ = "products"
    id = Column(Integer, primary_key=True)
    name = Column(String(100), nullable=False)
    status = Column(Enum(ProductStatus), nullable=False)
    display_label = None  # computed, not stored
```

**Q2:** In SQLAlchemy you update with: `product.name = "New Name"` and then `session.commit()`. Write the equivalent Hibernate dirty-checking code.

**Q3:** In SQLAlchemy you query with: `session.query(Product).filter(Product.price > 100).order_by(Product.price).all()`. Write the equivalent JPQL.

### Answer Key — Round 2
A1:
```java
@Entity
@Table(name = "products")
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;
    @Transient
    private String displayLabel;
}
```

A2: Exactly the same pattern! In Hibernate dirty checking:
```java
try (Session session = sf.openSession()) {
    Transaction tx = session.beginTransaction();
    Product product = session.find(Product.class, id);
    product.setName("New Name");  // Hibernate detects this
    tx.commit();  // UPDATE SQL auto-generated
}
```
Key difference: SQLAlchemy requires `session.add(product)` if the object was detached; Hibernate's dirty checking works automatically for PERSISTENT entities.

A3: `SELECT p FROM Product p WHERE p.price > 100 ORDER BY p.price ASC`

---

## Round 3 — Debug the Bug (3 scenarios)

**Scenario 1:** Your app starts fine but when you call `session.persist(product)` you get `org.hibernate.MappingException: Unknown entity: com.example.Product`. The `Product` class has all the field annotations. What's missing?

**Scenario 2:** After `session.persist(customer)` and `tx.commit()`, you close the session. Later you call `customer.setEmail("new@example.com")` and nothing happens in the DB. Your teammate says "use `session.merge()`". Why is `merge()` needed here?

**Scenario 3:** Your production database gets wiped every Monday morning when the app restarts. Check the logs — Hibernate prints `HHH000476: Executing import script 'import.sql'`. What property is causing this?

### Answer Key — Round 3
S1: `@Entity` annotation is missing from the class. Without `@Entity`, Hibernate doesn't register the class in its metadata. Fix: add `@Entity` above the class declaration.

S2: After closing the session, `customer` is DETACHED — Hibernate is no longer tracking it. Modifications to detached entities don't generate SQL. `session.merge(detachedCustomer)` re-attaches it to a new session (performs SELECT + UPDATE). Without merge, the change is lost.

S3: `hbm2ddl.auto=create` or `hbm2ddl.auto=create-drop`. The `create` setting drops and recreates all tables on every startup. Fix for production: use `hbm2ddl.auto=validate` (fails fast if schema doesn't match) or `hbm2ddl.auto=none` (manage schema with Flyway/Liquibase).

---

## Round 4 — Staff-Level Scenarios (2 questions)

**Q1 — Multi-Environment Strategy:**
Your team has 3 environments: local dev, staging, production. What `hbm2ddl.auto` value do you use in each, and why? What tool do you use to manage schema changes in staging and production?

**Q2 — Design Question:**
A new engineer proposes adding `@GeneratedValue(strategy = GenerationType.TABLE)` across all entities "for portability". You're running PostgreSQL. What problems does this cause, and what strategy would you recommend instead?

### Answer Key — Round 4

A1:
- **Local dev**: `create-drop` — fresh schema on every run, great for iterating on entity design
- **Staging**: `validate` — schema must match entities exactly; migrations run separately via Flyway/Liquibase before app start
- **Production**: `validate` or `none` — NEVER `create`, `update`, or `create-drop` in prod
- Schema management tool: **Flyway** (SQL migration files) or **Liquibase** (XML/YAML changelogs). Both track which migrations ran. Spring Boot integrates both via starter dependencies.

A2: Problems with `GenerationType.TABLE`:
1. Uses a DB table (`hibernate_sequence`) with pessimistic locking for ID generation → bottleneck at high concurrency
2. All entities share one sequence table by default → IDs become globally unique but per-entity non-sequential
3. Network round-trip to DB for every new ID, even for bulk inserts

Recommendation for PostgreSQL:
- Use `GenerationType.SEQUENCE` with `@SequenceGenerator` per entity-type:
```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
@SequenceGenerator(name = "product_seq", sequenceName = "product_id_seq", allocationSize = 50)
private Long id;
```
`allocationSize=50` means Hibernate allocates 50 IDs at a time → far fewer DB calls.
