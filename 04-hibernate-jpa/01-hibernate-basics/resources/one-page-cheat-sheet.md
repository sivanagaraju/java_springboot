# 01-Hibernate-Basics — One-Page Cheat Sheet

## Entity Annotation Quick Reference

| Annotation | Purpose | Key Attributes |
|---|---|---|
| `@Entity` | Marks class as JPA-managed | — |
| `@Table` | Sets table name | `name`, `schema`, `indexes` |
| `@Id` | Primary key field | — |
| `@GeneratedValue` | Auto ID generation | `strategy` (IDENTITY/SEQUENCE/TABLE/AUTO) |
| `@Column` | Field-to-column mapping | `name`, `nullable`, `unique`, `length` |
| `@Enumerated` | Enum persistence | `EnumType.STRING` (use this!) or `ORDINAL` (avoid!) |
| `@Transient` | Field NOT persisted | — |
| `@CreationTimestamp` | Auto-set on INSERT | Hibernate-specific annotation |
| `@UpdateTimestamp` | Auto-set on INSERT+UPDATE | Hibernate-specific annotation |
| `@Version` | Optimistic lock version | Use `Integer` type |

## GenerationType Comparison

| Strategy | What it does | When to use |
|---|---|---|
| `IDENTITY` | DB auto-increment (SERIAL/IDENTITY column) | PostgreSQL, MySQL, H2 — simplest choice |
| `SEQUENCE` | DB sequence object, batch allocation | PostgreSQL, Oracle — best for high throughput |
| `TABLE` | Separate lock table | Avoid — bottleneck, use only for portability |
| `AUTO` | Hibernate picks | Avoid — unpredictable across DB vendors |

## Entity Lifecycle States (ASCII)

```
                                    session.persist(entity)
  new Entity()  ─────────────────────────────────────────────►  PERSISTENT
   TRANSIENT                                                      (tracked, ID assigned)
                                                                      │
  session.close() or  ◄────────────────────────────────────────────  │ session.detach(entity)
  session.evict(entity)                                               │
       │                                                              ▼
   DETACHED ────────────────────────────────────────────────► PERSISTENT (again)
   (not tracked)          session.merge(detached)               via session.merge()
       │
       │ session.find() returns null after remove + commit
       ▼
   REMOVED ──────────────────────────────────────────────────► (gone from DB after commit)
       (marked for delete)
```

## CRUD Cheat Sheet

```java
// CREATE
session.persist(entity);  // entity: Transient → Persistent

// READ (by PK)
Product p = session.find(Product.class, 1L);  // L1 cache first, then DB

// UPDATE (dirty checking)
p.setPrice(99.99);  // just mutate
tx.commit();        // Hibernate auto-generates UPDATE SQL

// DELETE
session.remove(p);  // entity: Persistent → Removed
tx.commit();        // DELETE SQL fires

// QUERY (JPQL)
List<Product> results = session.createQuery(
    "FROM Product p WHERE p.price > :min", Product.class)
    .setParameter("min", 50.0)
    .getResultList();
```

## Python → Java Quick Map

| SQLAlchemy | Hibernate/JPA |
|---|---|
| `class Product(Base):` | `@Entity public class Product` |
| `id = Column(Integer, primary_key=True)` | `@Id @GeneratedValue private Long id` |
| `name = Column(String(100), nullable=False)` | `@Column(length=100, nullable=false) private String name` |
| `session.add(product)` | `session.persist(product)` |
| `session.commit()` | `tx.commit()` |
| `session.query(Product).get(1)` | `session.find(Product.class, 1L)` |
| `session.delete(product)` | `session.remove(product)` |

## 5 Traps to Avoid

1. **`hbm2ddl.auto=create` in production** → drops all tables on startup, data destroyed
2. **`@Enumerated(EnumType.ORDINAL)`** → silent data corruption if enum order changes
3. **No no-arg constructor** → JPA cannot instantiate entity, `InstantiationException`
4. **Accessing lazy collection after session close** → `LazyInitializationException`
5. **Forgetting `tx.rollback()` in catch block** → uncommitted transaction holds DB locks
