/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  FILE   : SessionFactoryDemo.java                                   ║
 * ║  MODULE : 04-hibernate-jpa / 01-hibernate-basics                   ║
 * ║  GRADLE : ./gradlew :04-hibernate-jpa:run                           ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Walk through all 5 JPA entity lifecycle states:   ║
 * ║                   TRANSIENT → PERSISTENT → (DIRTY CHECK) →          ║
 * ║                   DETACHED → RE-ATTACHED via merge()                ║
 * ║  WHY IT EXISTS  : Most bugs in Hibernate code come from not         ║
 * ║                   knowing which lifecycle state an entity is in.    ║
 * ║                   Understanding the state machine eliminates an     ║
 * ║                   entire class of LazyInitializationException and   ║
 * ║                   "unsaved transient instance" errors.              ║
 * ║  PYTHON COMPARE : SQLAlchemy Session.add() (transient→pending),    ║
 * ║                   session.flush() (pending→persistent), and         ║
 * ║                   session.expunge() (detach from session)           ║
 * ║  USE CASES      : Any service that spans multiple requests, caches  ║
 * ║                   entities between transactions, or needs to pass   ║
 * ║                   detached entities across application layers       ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Entity Lifecycle State Machine                     ║
 * ║                                                                      ║
 * ║   new Customer()          ←─────────────────────────────────┐       ║
 * ║        │                                                     │       ║
 * ║        ▼                                                     │       ║
 * ║   [TRANSIENT]  ── session.persist() ──▶  [PERSISTENT]       │       ║
 * ║                                               │              │       ║
 * ║                                    mutate field → dirty check│       ║
 * ║                                          tx.commit()         │       ║
 * ║                                               │ UPDATE SQL   │       ║
 * ║                                               ▼              │       ║
 * ║                                        session.close()       │       ║
 * ║                                               │              │       ║
 * ║                                               ▼              │       ║
 * ║                                         [DETACHED] ──────────┘       ║
 * ║                                               │  session2.merge()    ║
 * ║                                               ▼                      ║
 * ║                                        [PERSISTENT again]            ║
 * ║                                         in Session 2                 ║
 * ║                                                                      ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :04-hibernate-jpa:run                   ║
 * ║  EXPECTED OUTPUT: See EXPECTED OUTPUT block at bottom of main()     ║
 * ║  RELATED FILES  : 04-session-factory.md, EntityAnnotationsDemo.java ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics;

// 1. Java standard library
import java.util.Objects;

// 2. Third-party — Hibernate core bootstrap API
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

// 3. Jakarta EE — JPA annotations (NEVER javax.persistence)
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * SessionFactoryDemo — demonstrates the complete JPA entity lifecycle through
 * all five states using a {@code Customer} entity and two separate sessions.
 *
 * <p>This class proves each lifecycle transition by printing the entity's
 * ID and name at every state boundary. Critically, the DETACHED section
 * shows that mutating an entity after the session closes produces no SQL —
 * the change is invisible to Hibernate until the entity is re-attached via
 * {@code session.merge()}.</p>
 *
 * <p>Understanding this state machine is prerequisite knowledge for debugging
 * any non-trivial Hibernate application. Spring Boot wraps the session in
 * {@code @Transactional} but the same states apply — the boundary is just
 * the method rather than explicit {@code openSession()} calls.</p>
 *
 * <p><b>Python SQLAlchemy session equivalent:</b>
 * <pre>
 *   # TRANSIENT
 *   customer = Customer(name="Alice", email="alice@example.com")
 *
 *   # PERSISTENT (pending in SQLAlchemy terminology)
 *   session.add(customer)
 *   session.flush()          # INSERT emitted, id assigned
 *
 *   # DIRTY CHECK
 *   customer.name = "Alicia"
 *   session.commit()         # UPDATE emitted automatically
 *
 *   # DETACHED equivalent
 *   session.expunge(customer)
 *   customer.name = "Alice Again"  # invisible — no SQL
 *
 *   # RE-ATTACH equivalent
 *   session.add(customer)   # SQLAlchemy re-merges on add after expunge
 *   session.commit()        # UPDATE emitted
 * </pre>
 *
 * <p><b>ASCII — Architecture position:</b>
 * <pre>
 *   main() — test driver
 *       │
 *       ▼
 *   SessionFactoryDemo   ← YOU ARE HERE
 *       │  manually manages Session lifecycle
 *       ▼
 *   SessionFactory (thread-safe, application-scoped)
 *       │  creates Sessions (not thread-safe, request-scoped)
 *       ▼
 *   Session (Unit of Work) — tracks entity state
 *       │
 *       ▼
 *   H2 in-memory database
 * </pre>
 */
public class SessionFactoryDemo {

    // ──────────────────────────────────────────────────────────────────
    //  INNER ENTITY — Customer
    // ──────────────────────────────────────────────────────────────────

    /**
     * Customer — minimal JPA entity used to demonstrate the lifecycle states.
     *
     * <p>Only {@code id}, {@code name}, and {@code email} are needed to
     * illustrate every state transition. The entity is intentionally simple
     * so the demo can focus on the lifecycle rather than field complexity.</p>
     *
     * <p><b>Python SQLAlchemy equivalent:</b>
     * <pre>
     *   class Customer(Base):
     *       __tablename__ = "customers"
     *       id    = mapped_column(Integer, primary_key=True)
     *       name  = mapped_column(String(100), nullable=False)
     *       email = mapped_column(String(150), nullable=False, unique=True)
     * </pre>
     *
     * <p><b>ASCII — Database mapping:</b>
     * <pre>
     *   Customer.java       customers table
     *   ─────────────────────────────────────
     *   id (Long)       →   id BIGINT PK
     *   name (String)   →   name VARCHAR(100) NOT NULL
     *   email (String)  →   email VARCHAR(150) NOT NULL UNIQUE
     * </pre>
     */
    @Entity
    @Table(name = "customers")
    public static class Customer {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 100)
        private String name;

        @Column(nullable = false, unique = true, length = 150)
        private String email;

        /** Required no-arg constructor — JPA spec mandates this for proxying. */
        public Customer() {}

        /**
         * Constructs a transient Customer not yet associated with any session.
         *
         * @param name   customer display name — stored in {@code name} column
         * @param email  customer email address — must be unique across all rows
         */
        public Customer(String name, String email) {
            this.name = name;
            this.email = email;
        }

        /** Returns the surrogate primary key, or {@code null} if transient. */
        public Long getId() { return id; }

        /** Returns the customer's display name. */
        public String getName() { return name; }

        /**
         * Updates the customer's display name.
         *
         * <p>Calling this while the entity is in the PERSISTENT state (inside
         * an open session) will trigger dirty checking — Hibernate compares
         * this new value against its snapshot and auto-generates UPDATE SQL
         * at flush/commit time without any explicit {@code save()} call.</p>
         *
         * @param name  new display name; must not be null
         */
        public void setName(String name) { this.name = name; }

        /** Returns the customer's email address. */
        public String getEmail() { return email; }

        @Override
        public String toString() {
            return "Customer{id=" + id + ", name='" + name + "', email='" + email + "'}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Customer c)) return false;
            // WHY id-based equality: two Customer objects representing the same
            // DB row should be equal regardless of which session loaded them
            return Objects.equals(id, c.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    // ──────────────────────────────────────────────────────────────────
    //  DEMO ENTRY POINT
    // ──────────────────────────────────────────────────────────────────

    /**
     * Runs the five-state lifecycle demo, printing the entity state at
     * every transition boundary to show exactly when SQL is generated.
     *
     * <p><b>Flow:</b>
     * <pre>
     *   main()
     *       │
     *       ├─ buildSessionFactory()  — H2 + Customer entity registered
     *       │
     *       ├─ State 1: TRANSIENT     — new Customer(), id=null
     *       │
     *       ├─ State 2: PERSISTENT    — session.persist(), id assigned, INSERT
     *       │
     *       ├─ State 3: DIRTY CHECK   — setName() → tx.commit() → UPDATE auto-generated
     *       │
     *       ├─ State 4: DETACHED      — session closed, setName() → NO SQL
     *       │
     *       ├─ State 5: RE-ATTACHED   — session2.merge() → SELECT + UPDATE
     *       │
     *       └─ close SessionFactory
     * </pre>
     *
     * @param args  unused command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("=== Session & Entity Lifecycle Demo ===");

        // WHY manual registry: Spring Boot hides this behind auto-configuration.
        // Seeing it manually reveals what @EnableJpaRepositories and
        // spring.datasource.* properties actually configure under the hood.
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
            .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
            .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            .applySetting("hibernate.hbm2ddl.auto", "create-drop")
            .applySetting("hibernate.show_sql", "true")
            .build();

        SessionFactory sessionFactory = new MetadataSources(registry)
            .addAnnotatedClass(Customer.class)
            .buildMetadata()
            .buildSessionFactory();

        // ── State 1: TRANSIENT ────────────────────────────────────────
        System.out.println("\n--- State 1: TRANSIENT ---");
        Customer customer = new Customer("Alice", "alice@example.com");
        System.out.println("customer = new Customer(\"Alice\", \"alice@example.com\")");
        // id is null because no session has been involved yet — the object
        // exists only in JVM heap, Hibernate has no awareness of it
        System.out.println("id = " + customer.getId()
            + " (not in DB, Hibernate doesn't know about it)");

        // ── State 2: PERSISTENT ───────────────────────────────────────
        System.out.println("\n--- State 2: PERSISTENT (after persist) ---");

        Long customerId;

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // WHY persist() not save(): persist() is the JPA-standard method.
            // Hibernate's legacy save() returns the ID immediately and can
            // behave differently with detached entities — prefer persist().
            session.persist(customer);

            // WHY flush before commit: flushing forces Hibernate to synchronize
            // the in-memory state to the DB immediately so we can show the
            // assigned ID before the transaction closes
            session.flush();

            System.out.println("session.persist(customer)");
            System.out.println("id = " + customer.getId()
                + " (Hibernate assigned ID, tracking changes)");
            System.out.println("SQL: INSERT INTO customers ... (see show_sql output above)");

            // ── State 3: DIRTY CHECKING ───────────────────────────────
            System.out.println("\n--- State 3: DIRTY CHECKING ---");
            System.out.println("customer.setName(\"Alicia\")  ← Hibernate sees this change");

            // WHY this produces UPDATE without explicit save():
            // When session.persist() was called, Hibernate took a snapshot of the
            // entity's field values. At tx.commit() / flush(), Hibernate diffs
            // the current state against that snapshot. Any difference becomes
            // an UPDATE statement — no developer action required.
            customer.setName("Alicia");

            tx.commit();
            System.out.println("tx.commit()");
            System.out.println("SQL: UPDATE customers SET name='Alicia' WHERE id="
                + customer.getId() + "  ← auto-generated!");
            System.out.println("No explicit save() needed — this is JPA dirty checking");

            customerId = customer.getId();
        }
        // ── session is now CLOSED — customer is DETACHED ──────────────

        // ── State 4: DETACHED ─────────────────────────────────────────
        System.out.println("\n--- State 4: DETACHED (after session close) ---");
        System.out.println("session closed — customer is no longer tracked");

        // WHY no SQL: the entity's session is gone. Hibernate's persistence
        // context is cleared. Mutations to the object are invisible to any
        // Hibernate infrastructure — they're just plain Java object changes.
        customer.setName("Alice Again");
        System.out.println("customer.setName(\"Alice Again\")  ← Hibernate doesn't see this change");
        System.out.println("No SQL generated");

        // ── State 5: RE-ATTACHED via merge() ─────────────────────────
        System.out.println("\n--- State 5: RE-ATTACHED (merge) ---");

        try (Session session2 = sessionFactory.openSession()) {
            Transaction tx2 = session2.beginTransaction();

            // WHY merge() not persist(): merge() handles detached entities.
            // Hibernate SELECTs the current DB row for the given ID, applies
            // the detached entity's changes on top of it, and returns a NEW
            // persistent instance. The detached `customer` reference is NOT
            // made persistent — only the returned `merged` object is.
            Customer merged = session2.merge(customer);

            tx2.commit();

            System.out.println("Session 2: merged = session2.merge(customer)");
            System.out.println("SQL: SELECT id=" + customerId
                + " then UPDATE  ← Hibernate fetches current state, applies changes");
            System.out.println("merged.getName() = " + merged.getName());
            System.out.println("(detached customer.getName() still = " + customer.getName()
                + " but merged copy is now the tracked one)");
        }

        sessionFactory.close();
        StandardServiceRegistryBuilder.destroy(registry);

        /*
         * EXPECTED OUTPUT:
         * ================
         * === Session & Entity Lifecycle Demo ===
         *
         * --- State 1: TRANSIENT ---
         * customer = new Customer("Alice", "alice@example.com")
         * id = null (not in DB, Hibernate doesn't know about it)
         *
         * --- State 2: PERSISTENT (after persist) ---
         * Hibernate: insert into customers (email,name,id) values (?,?,default)
         * session.persist(customer)
         * id = 1 (Hibernate assigned ID, tracking changes)
         * SQL: INSERT INTO customers ... (see show_sql output above)
         *
         * --- State 3: DIRTY CHECKING ---
         * customer.setName("Alicia")  ← Hibernate sees this change
         * Hibernate: update customers set email=?,name=? where id=?
         * tx.commit()
         * SQL: UPDATE customers SET name='Alicia' WHERE id=1  ← auto-generated!
         * No explicit save() needed — this is JPA dirty checking
         *
         * --- State 4: DETACHED (after session close) ---
         * session closed — customer is no longer tracked
         * customer.setName("Alice Again")  ← Hibernate doesn't see this change
         * No SQL generated
         *
         * --- State 5: RE-ATTACHED (merge) ---
         * Hibernate: select c1_0.id,c1_0.email,c1_0.name from customers c1_0 where c1_0.id=?
         * Hibernate: update customers set email=?,name=? where id=?
         * Session 2: merged = session2.merge(customer)
         * SQL: SELECT id=1 then UPDATE  ← Hibernate fetches current state, applies changes
         * merged.getName() = Alice Again
         * (detached customer.getName() still = Alice Again but merged copy is now the tracked one)
         */
    }
}
