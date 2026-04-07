# Hibernate Basics — Mind Map

## ORM Concept
- **Definition**: Maps Java objects to relational DB rows automatically
- **Problem It Solves**
  - Object-Relational Impedance Mismatch
    - Objects have identity (reference equality) — tables have primary keys
    - Objects have inheritance — tables are flat
    - Objects have associations (object references) — tables have foreign keys
    - Objects have a rich type system — SQL has primitive types
  - Raw JDBC maintenance disaster
    - Every entity needs INSERT / SELECT / UPDATE / DELETE SQL by hand
    - `ResultSet` → object mapping is mechanical and duplicated
    - Connection lifecycle must be manually managed in every method
    - Schema changes require updates in dozens of SQL strings
- **What Hibernate Provides**
  - Auto-generates SQL from annotated Java classes
  - Dirty checking — tracks changed fields, generates minimal UPDATE
  - First-level cache — same Session never hits DB twice for same entity
  - Transaction management integration
  - Schema generation / validation via `hbm2ddl.auto`
- **Python Parallel**: SQLAlchemy ORM (`declarative_base`, `session.add`, `session.query`)
- **JPA vs Hibernate**
  - JPA — Jakarta Persistence API — the standard specification (`jakarta.persistence.*`)
  - Hibernate — the most popular JPA implementation (also has its own extensions)
  - Spring Data JPA — Spring wrapper around JPA/Hibernate (adds repositories, query methods)
  - Use JPA annotations wherever possible — portable across EclipseLink, OpenJPA

---

## Hibernate Architecture
- **Core Components**
  - `SessionFactory`
    - Heavyweight — created once per application (at startup)
    - Compiles HQL, validates schema, initialises connection pool
    - Thread-safe — share across the entire application
    - Python parallel: `create_engine()` from SQLAlchemy
    - JPA equivalent: `EntityManagerFactory`
  - `Session`
    - Lightweight — created per request / unit of work
    - Wraps a JDBC `Connection`
    - Maintains the **first-level cache** (identity map)
    - NOT thread-safe — never share a Session between threads
    - Python parallel: SQLAlchemy `Session`
    - JPA equivalent: `EntityManager`
  - `Transaction`
    - Wraps the underlying JDBC/JTA transaction
    - `beginTransaction()`, `commit()`, `rollback()`
    - Always use explicit transactions — never rely on auto-commit
  - `ConnectionProvider`
    - Abstraction over JDBC `DataSource` / `DriverManager`
    - Hibernate delegates actual connection pooling here (HikariCP in production)
  - `Query` / `TypedQuery`
    - HQL (Hibernate Query Language) — object-oriented SQL
    - Criteria API — type-safe programmatic queries
    - Native SQL — escape hatch to raw SQL when needed
- **Configuration Entry Points**
  - `hibernate.cfg.xml` — XML-based (legacy)
  - `StandardServiceRegistryBuilder` — programmatic (modern)
  - `persistence.xml` — JPA standard config file
  - Spring Boot `application.properties` — auto-configuration (builds EMF for you)

---

## Entity Lifecycle States
- **Transient**
  - Object created with `new` — no ID, not known to Hibernate
  - Not in DB, not in first-level cache
  - Transition to Persistent: call `session.persist(entity)` or `session.save(entity)`
  - Python parallel: SQLAlchemy `transient` state (before `session.add`)
- **Persistent**
  - Associated with an open `Session`
  - Has a DB row (or will have one at flush)
  - Hibernate tracks all field changes (dirty checking)
  - Transition to Detached: `session.detach(entity)` / `session.evict(entity)` / session close
  - Transition to Removed: `session.remove(entity)` / `session.delete(entity)`
- **Detached**
  - Row exists in DB, but entity is not associated with any Session
  - Changes to a detached entity are NOT tracked
  - Transition to Persistent: `session.merge(entity)` (copies state to new managed instance)
  - Python parallel: SQLAlchemy `detached` state (after `session.expunge`)
- **Removed**
  - Marked for deletion — DELETE will execute at flush
  - Still associated with Session until flush/commit
  - Can be "un-removed" by calling `session.persist(entity)` again before flush
- **State Transitions Summary**
  - `new` creates Transient state
  - `persist()` moves to Persistent
  - `detach()` or session close moves to Detached
  - `merge()` returns new Persistent instance from Detached
  - `remove()` moves to Removed
  - `commit()` or `flush()` executes SQL and reflects changes in DB

---

## Entity Annotations
- **Class-Level**
  - `@Entity` — marks class as JPA entity; must have no-arg constructor
  - `@Table(name, schema, indexes, uniqueConstraints)` — maps to specific table
    - `name` — table name (defaults to class name)
    - `schema` — database schema
    - `indexes = @Index(columnList = "email")` — creates DB index
- **Identity**
  - `@Id` — marks primary key field
  - `@GeneratedValue(strategy = GenerationType.X)`
    - `IDENTITY` — auto-increment column (MySQL, PostgreSQL SERIAL) — no pre-allocation
    - `SEQUENCE` — DB sequence object — allows batch inserts (preferred for PostgreSQL)
    - `TABLE` — dedicated table for ID generation — portable but slowest
    - `AUTO` — Hibernate picks based on dialect — avoid in production (unpredictable)
  - `@SequenceGenerator(name, sequenceName, allocationSize)` — configures SEQUENCE strategy
- **Column Mapping**
  - `@Column(name, nullable, unique, length, precision, scale, columnDefinition)`
    - `nullable = false` — adds `NOT NULL` constraint on DDL generation
    - `unique = true` — adds unique constraint
    - `length` — applies to `VARCHAR` columns
    - `insertable/updatable = false` — computed or audit columns
  - `@Transient` — field is NOT persisted to DB (computed fields, display values)
- **Enum Handling**
  - `@Enumerated(EnumType.STRING)` — stores enum name as VARCHAR — safe
  - `@Enumerated(EnumType.ORDINAL)` — stores enum ordinal as INT — DANGEROUS (breaks if order changes)
  - Always use `STRING` in production
- **Audit Timestamps**
  - `@CreationTimestamp` — Hibernate sets on first INSERT (non-JPA, Hibernate-specific)
  - `@UpdateTimestamp` — Hibernate updates on every UPDATE
  - JPA alternative: `@PrePersist` / `@PreUpdate` lifecycle callbacks
- **Optimistic Locking**
  - `@Version` — integer or timestamp field; Hibernate increments on each UPDATE
  - Prevents lost-update concurrency bugs without DB-level locking
- **Python Parallel**
  - `Column(String(100), nullable=False)` maps to `@Column(length=100, nullable=false)`
  - `Column(Enum(MyEnum))` maps to `@Enumerated(EnumType.STRING)`
  - `created_at = Column(DateTime, default=func.now())` maps to `@CreationTimestamp`

---

## CRUD Operations
- **CREATE**
  - `session.persist(entity)` — JPA standard; entity must be in Transient state
  - `session.save(entity)` — Hibernate-specific (legacy); returns the generated ID immediately
  - SQL executes at flush time (not at `persist()` call)
- **READ**
  - `session.find(Class, id)` — JPA; returns null if not found
  - `session.get(Class, id)` — Hibernate; same as find()
  - `session.load(Class, id)` — returns proxy; throws if not found (lazy)
  - HQL: `session.createQuery("FROM Product WHERE price > :min", Product.class)`
  - Criteria API: type-safe alternative to HQL strings
- **UPDATE**
  - Dirty checking — modify a Persistent entity, changes auto-detected at flush
  - No explicit `update()` call needed for managed entities
  - `session.merge(detachedEntity)` — re-attaches and returns new managed copy
- **DELETE**
  - `session.remove(entity)` — JPA standard; entity must be Persistent
  - `session.delete(entity)` — Hibernate-specific (legacy)
  - Bulk delete: HQL `DELETE FROM Product WHERE price < :min` (bypasses dirty checking)
- **Flush and Clear**
  - `session.flush()` — writes pending changes to DB without committing
  - `session.clear()` — detaches all entities from Session (resets first-level cache)
  - `session.evict(entity)` — detaches a single entity
  - Auto-flush before every query and before commit (default `FlushMode.AUTO`)
- **Transaction Pattern**
  - `Transaction tx = session.beginTransaction()`
  - do work inside try block
  - `tx.commit()` on success
  - `tx.rollback()` in catch block
  - `session.close()` in finally block
- **Python Parallel**
  - `session.add(obj)` maps to `session.persist(entity)`
  - `session.query(Product).get(1)` maps to `session.find(Product.class, 1L)`
  - `session.delete(obj)` maps to `session.remove(entity)`
  - `session.commit()` maps to `transaction.commit()`

---

## Session vs EntityManager
- **Session (Hibernate-native API)**
  - `org.hibernate.Session`
  - Additional methods: `save()`, `update()`, `saveOrUpdate()`, `get()`, `load()`
  - More flexible but ties code to Hibernate
- **EntityManager (JPA standard API)**
  - `jakarta.persistence.EntityManager`
  - Methods: `persist()`, `find()`, `merge()`, `remove()`, `createQuery()`
  - Portable — works with EclipseLink, OpenJPA, etc.
  - Prefer EntityManager in new code; use Session only for Hibernate-specific features
- **Obtaining EntityManager in Spring**
  - `@PersistenceContext EntityManager em` — Spring injects a proxy; thread-safe
  - Underlying Session accessible via `em.unwrap(Session.class)` when needed

---

## Common Production Traps
- Creating `SessionFactory` per request causes startup cost multiplied by every request leading to system crash
- Not closing `Session` causes connection leak leading to pool exhaustion
- `hbm2ddl.auto=create` in production drops and recreates tables on every restart
- `EnumType.ORDINAL` breaks silently when enum values are reordered
- No `@Column(nullable=false)` constraints allows null data to sneak in despite Java `@NotNull`
- Not using transactions means each persist auto-commits with no atomicity
- Calling `session.merge()` on a never-persisted entity inserts a duplicate row