/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  FILE   : EntityAnnotationsDemo.java                                ║
 * ║  MODULE : 04-hibernate-jpa / 01-hibernate-basics                   ║
 * ║  GRADLE : ./gradlew :04-hibernate-jpa:run                           ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrate every core JPA annotation on a Book   ║
 * ║                   entity — @Entity, @Table, @Column, @Enumerated,   ║
 * ║                   @Transient, @CreationTimestamp, and dirty checking ║
 * ║  WHY IT EXISTS  : Before JPA, developers hand-wrote every INSERT,   ║
 * ║                   UPDATE, and CREATE TABLE. Annotations replaced     ║
 * ║                   XML descriptors and SQL boilerplate entirely.      ║
 * ║  PYTHON COMPARE : SQLAlchemy Column(...), Mapped[...], and          ║
 * ║                   @declared_attr on a declarative Base class        ║
 * ║  USE CASES      : Library catalog, product catalog, any entity      ║
 * ║                   that needs column constraints without raw SQL      ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Annotation → DB Column mapping                     ║
 * ║                                                                      ║
 * ║   Book.java (Java field)     →   books table (DB column)            ║
 * ║   ───────────────────────────────────────────────────               ║
 * ║   @Id @GeneratedValue        →   id BIGINT PRIMARY KEY AUTO_INC     ║
 * ║   @Column(name="book_title") →   book_title VARCHAR(200) NOT NULL   ║
 * ║   @Enumerated(STRING)        →   status VARCHAR (AVAILABLE/etc)     ║
 * ║   @Column(unique=true)       →   isbn VARCHAR(13) UNIQUE            ║
 * ║   @Transient                 →   [NOT STORED — no column created]   ║
 * ║   @CreationTimestamp         →   created_at TIMESTAMP NOT NULL      ║
 * ║                                                                      ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :04-hibernate-jpa:run                   ║
 * ║  EXPECTED OUTPUT: See EXPECTED OUTPUT block at bottom of main()     ║
 * ║  RELATED FILES  : 03-entity-annotations.md, SessionFactoryDemo.java ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics;

// 1. Java standard library
import java.time.LocalDateTime;
import java.util.List;

// 2. Third-party — Hibernate core (no Spring dependency — raw Hibernate)
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

// 3. Jakarta EE — JPA annotations (NEVER javax.persistence)
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * EntityAnnotationsDemo — demonstrates every core JPA entity annotation
 * on a {@code Book} entity using an H2 in-memory database.
 *
 * <p>This class is a standalone Hibernate program that creates the schema,
 * persists three {@code Book} instances, and then proves each annotation
 * works as advertised: {@code @Column} constraints are applied to DDL,
 * {@code @Enumerated(STRING)} stores human-readable text instead of a
 * fragile ordinal, {@code @Transient} fields survive in-memory but are
 * never written to the database, and dirty checking auto-generates UPDATE
 * SQL without an explicit {@code session.merge()} call.</p>
 *
 * <p><b>Python SQLAlchemy equivalent:</b>
 * <pre>
 *   class Book(Base):
 *       __tablename__ = "books"
 *       id     = Column(Integer, primary_key=True, autoincrement=True)
 *       title  = Column("book_title", String(200), nullable=False)
 *       status = Column(Enum(BookStatus), nullable=False)
 *       isbn   = Column(String(13), unique=True)
 *       # @Transient equivalent: a plain Python @property (not mapped)
 *       created_at = Column(DateTime, default=func.now(), nullable=False)
 * </pre>
 *
 * <p><b>ASCII — Architecture position:</b>
 * <pre>
 *   main() — test driver
 *       │
 *       ▼
 *   EntityAnnotationsDemo   ← YOU ARE HERE
 *       │  builds SessionFactory, runs demo sequence
 *       ▼
 *   Hibernate ORM (SessionFactory / Session)
 *       │  translates @Entity annotations → SQL DDL + DML
 *       ▼
 *   H2 in-memory database (jdbc:h2:mem:demo)
 * </pre>
 */
public class EntityAnnotationsDemo {

    // ──────────────────────────────────────────────────────────────────
    //  INNER ENTITY — BookStatus enum
    // ──────────────────────────────────────────────────────────────────

    /**
     * BookStatus — valid lifecycle states for a library book.
     *
     * <p>Stored as STRING in the database (see {@code @Enumerated(STRING)}
     * on the {@code Book.status} field). This means adding a new enum
     * constant later (e.g. {@code RESERVED}) never corrupts existing rows,
     * unlike ORDINAL which shifts integer values when the enum is reordered.</p>
     */
    enum BookStatus {
        AVAILABLE,
        BORROWED,
        LOST
    }

    // ──────────────────────────────────────────────────────────────────
    //  INNER ENTITY — Book
    // ──────────────────────────────────────────────────────────────────

    /**
     * Book — JPA entity mapped to the {@code books} table.
     *
     * <p>Demonstrates six key annotations in one class: {@code @Entity},
     * {@code @Table}, {@code @Id} + {@code @GeneratedValue},
     * {@code @Column} with constraints, {@code @Enumerated(STRING)},
     * {@code @Transient}, and {@code @CreationTimestamp}.</p>
     *
     * <p><b>Python SQLAlchemy equivalent:</b>
     * <pre>
     *   class Book(Base):
     *       __tablename__ = "books"
     *       id     = mapped_column(Integer, primary_key=True)
     *       title  = mapped_column("book_title", String(200), nullable=False)
     *       status = mapped_column(Enum(BookStatus), nullable=False)
     * </pre>
     *
     * <p><b>ASCII — Field → Column mapping:</b>
     * <pre>
     *   Java field         DB column           Constraint
     *   ─────────────────────────────────────────────────────
     *   id (Long)       →  id BIGINT           PK, AUTO_INC
     *   title (String)  →  book_title VARCHAR  NOT NULL, 200
     *   status (enum)   →  status VARCHAR      NOT NULL
     *   isbn (String)   →  isbn VARCHAR        UNIQUE, 13
     *   displayLabel    →  [none]              @Transient
     *   createdAt       →  created_at TIMESTAMP NOT NULL
     * </pre>
     */
    @Entity
    @Table(name = "books")
    public static class Book {

        @Id
        // WHY IDENTITY: H2 supports IDENTITY strategy; Hibernate delegates ID
        // generation to the DB — no sequence table needed for this demo
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "book_title", nullable = false, length = 200)
        private String title;

        @Column(nullable = false)
        // WHY STRING not ORDINAL: ordinal stores 0/1/2 — if enum is reordered
        // (e.g. BORROWED moves before AVAILABLE) every existing row silently
        // maps to the wrong status. STRING stores "AVAILABLE" literally.
        @Enumerated(EnumType.STRING)
        private BookStatus status;

        @Column(name = "isbn", unique = true, length = 13)
        private String isbn;

        // WHY @Transient: displayLabel is computed at runtime for UI display.
        // Storing it would create redundant data that could drift out of sync.
        @Transient
        private String displayLabel;

        // WHY updatable=false: once created_at is written on INSERT it should
        // never change — marking it non-updatable enforces that at the ORM level
        @CreationTimestamp
        @Column(name = "created_at", nullable = false, updatable = false)
        private LocalDateTime createdAt;

        /** Required no-arg constructor — JPA spec mandates it for proxy creation. */
        public Book() {}

        /**
         * Convenience constructor for demo use.
         *
         * @param title  book title — stored in {@code book_title} column
         * @param status initial lifecycle status — stored as VARCHAR string
         * @param isbn   13-character ISBN — must be unique across all books
         */
        public Book(String title, BookStatus status, String isbn) {
            this.title = title;
            this.status = status;
            this.isbn = isbn;
            // WHY compute here: displayLabel is always title + status bracket.
            // Hibernate will NOT persist it, but it's useful while the object
            // is in the persistent state within the same session.
            this.displayLabel = title + " [" + status + "]";
        }

        /** Returns the surrogate primary key assigned by the database. */
        public Long getId() { return id; }

        /** Returns the book title stored in the {@code book_title} column. */
        public String getTitle() { return title; }

        /** Returns the current lifecycle status of the book. */
        public BookStatus getStatus() { return status; }

        /**
         * Updates the book's lifecycle status.
         *
         * <p>Calling this inside an open Hibernate session triggers dirty
         * checking — Hibernate will auto-generate an UPDATE at flush time
         * without any explicit {@code session.merge()} or {@code session.save()}.</p>
         *
         * @param status  new status; must not be null
         */
        public void setStatus(BookStatus status) { this.status = status; }

        /** Returns the ISBN stored in the unique {@code isbn} column. */
        public String getIsbn() { return isbn; }

        /**
         * Returns the transient display label — only populated in memory,
         * never persisted to the database.
         *
         * @return display label string, or {@code null} if loaded from DB
         */
        public String getDisplayLabel() { return displayLabel; }

        /**
         * Sets a transient display label for in-memory use only.
         *
         * <p>This value will NOT survive a reload from the database because
         * the field is annotated {@code @Transient}. This method exists
         * solely to demonstrate that behavior in the demo.</p>
         *
         * @param displayLabel  any string label for UI display
         */
        public void setDisplayLabel(String displayLabel) {
            this.displayLabel = displayLabel;
        }

        /** Returns the timestamp when this record was created. */
        public LocalDateTime getCreatedAt() { return createdAt; }

        @Override
        public String toString() {
            return "Book{id=" + id
                + ", title='" + title + "'"
                + ", status=" + status
                + ", isbn='" + isbn + "'"
                + ", displayLabel='" + displayLabel + "'"
                + ", createdAt=" + createdAt
                + "}";
        }
    }

    // ──────────────────────────────────────────────────────────────────
    //  DEMO ENTRY POINT
    // ──────────────────────────────────────────────────────────────────

    /**
     * Runs the full entity-annotations demo in five clearly labeled sections.
     *
     * <p>Each section targets a different annotation or Hibernate behaviour:
     * DDL generation, enum string storage, {@code @Transient} loss on reload,
     * and dirty checking triggering UPDATE without an explicit save.</p>
     *
     * <p><b>Flow:</b>
     * <pre>
     *   main()
     *       │
     *       ├─ buildSessionFactory()  — wires H2, registers Book entity
     *       │
     *       ├─ Section 1: persist 3 Books (one per BookStatus)
     *       │       └─ Hibernate emits CREATE TABLE + 3x INSERT (show_sql=true)
     *       │
     *       ├─ Section 2: load all books — confirm round-trip
     *       │
     *       ├─ Section 3: @Transient proof — set displayLabel, persist,
     *       │             reload — label is null in reloaded object
     *       │
     *       ├─ Section 4: dirty checking — mutate status, commit —
     *       │             Hibernate emits UPDATE without explicit save()
     *       │
     *       └─ close SessionFactory
     * </pre>
     *
     * @param args  unused command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("=== EntityAnnotations Demo ===");

        // ── Build SessionFactory ──────────────────────────────────────
        // WHY StandardServiceRegistry: this is Hibernate's bootstrap API
        // when running without Spring. Spring Boot auto-configures this
        // from application.properties — here we do it manually to see
        // what Spring hides.
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
            .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
            .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            // WHY create-drop: schema is recreated fresh every run —
            // perfect for demos; never use in production
            .applySetting("hibernate.hbm2ddl.auto", "create-drop")
            // WHY show_sql: lets us see every SQL Hibernate generates —
            // proves annotations produce correct DDL and DML
            .applySetting("hibernate.show_sql", "true")
            .build();

        SessionFactory sessionFactory = new MetadataSources(registry)
            // WHY addAnnotatedClass: Hibernate must know which classes are
            // entities; Spring Boot scans the classpath automatically, but
            // standalone Hibernate requires explicit registration
            .addAnnotatedClass(Book.class)
            .buildMetadata()
            .buildSessionFactory();

        // ── Section 1: Create 3 books ─────────────────────────────────
        System.out.println("\n--- Creating 3 Books ---");
        System.out.println("(watch show_sql output above for CREATE TABLE and INSERT statements)");

        Long availableId;
        Long borrowedId;

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Book available = new Book("Clean Code", BookStatus.AVAILABLE, "9780132350884");
            Book borrowed  = new Book("Effective Java", BookStatus.BORROWED, "9780134685991");
            Book lost      = new Book("The Pragmatic Programmer", BookStatus.LOST, "9780135957059");

            session.persist(available);
            session.persist(borrowed);
            session.persist(lost);

            tx.commit();

            // WHY capture IDs now: once session closes, entity is DETACHED —
            // accessing a lazy-loaded field would throw LazyInitializationException.
            // IDs are eagerly loaded so safe to read post-commit.
            availableId = available.getId();
            borrowedId  = borrowed.getId();

            System.out.println("Persisted: " + available);
            System.out.println("Persisted: " + borrowed);
            System.out.println("Persisted: " + lost);
        }

        System.out.println("\n--- Hibernate generates CREATE TABLE (see show_sql output above) ---");

        // ── Section 2: Query all books ────────────────────────────────
        System.out.println("\n--- Loading all books ---");

        try (Session session = sessionFactory.openSession()) {
            // WHY text block for JPQL: multi-line queries are readable
            // and the closing """ aligns with the indentation
            List<Book> books = session.createQuery(
                """
                FROM Book b
                ORDER BY b.id
                """,
                Book.class
            ).list();

            books.forEach(b -> System.out.println("Loaded: " + b));
        }

        // ── Section 3: @Transient proof ───────────────────────────────
        System.out.println("\n--- @Transient field lost after reload ---");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Book book = session.find(Book.class, availableId);
            book.setDisplayLabel("LIBRARY COPY");

            System.out.println("displayLabel before commit: " + book.getDisplayLabel());

            tx.commit();
            // WHY evict: forces Hibernate to release the entity from its
            // first-level cache so the next find() hits the DB, not the cache
            session.evict(book);

            Book reloaded = session.find(Book.class, availableId);
            // displayLabel will be null — @Transient means no column was created
            System.out.println("displayLabel after reload : " + reloaded.getDisplayLabel()
                + "  ← null because @Transient was never stored");
        }

        // ── Section 4: Dirty checking ─────────────────────────────────
        System.out.println("\n--- Dirty checking: status updated without explicit save ---");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Book book = session.find(Book.class, borrowedId);
            System.out.println("Status before: " + book.getStatus());

            // WHY no session.merge() or session.update(): Hibernate snapshots
            // the entity state when it enters persistent state. At flush/commit
            // time it diffs current state vs. snapshot and auto-generates UPDATE.
            book.setStatus(BookStatus.AVAILABLE);
            System.out.println("Called setStatus(AVAILABLE) — no explicit save()");

            tx.commit();
            // SQL: UPDATE books SET status='AVAILABLE' WHERE id=? — auto-generated
            System.out.println("Status after  : " + book.getStatus()
                + "  ← UPDATE was auto-generated by dirty checking");
        }

        sessionFactory.close();
        StandardServiceRegistryBuilder.destroy(registry);

        /*
         * EXPECTED OUTPUT:
         * ================
         * === EntityAnnotations Demo ===
         *
         * --- Creating 3 Books ---
         * (watch show_sql output above for CREATE TABLE and INSERT statements)
         * Hibernate: create table books (id bigint generated by default as identity,
         *     isbn varchar(13) unique, book_title varchar(200) not null,
         *     created_at timestamp(6) not null, status varchar(255) not null,
         *     primary key (id))
         * Hibernate: insert into books (isbn,book_title,created_at,status,id)
         *     values (?,?,?,?,default)
         * Hibernate: insert into books (isbn,book_title,created_at,status,id)
         *     values (?,?,?,?,default)
         * Hibernate: insert into books (isbn,book_title,created_at,status,id)
         *     values (?,?,?,?,default)
         * Persisted: Book{id=1, title='Clean Code', status=AVAILABLE, ...}
         * Persisted: Book{id=2, title='Effective Java', status=BORROWED, ...}
         * Persisted: Book{id=3, title='The Pragmatic Programmer', status=LOST, ...}
         *
         * --- Hibernate generates CREATE TABLE (see show_sql output above) ---
         *
         * --- Loading all books ---
         * Hibernate: select b1_0.id, b1_0.isbn, b1_0.book_title, b1_0.created_at,
         *     b1_0.status from books b1_0 order by b1_0.id
         * Loaded: Book{id=1, title='Clean Code', status=AVAILABLE, ...}
         * Loaded: Book{id=2, title='Effective Java', status=BORROWED, ...}
         * Loaded: Book{id=3, title='The Pragmatic Programmer', status=LOST, ...}
         *
         * --- @Transient field lost after reload ---
         * displayLabel before commit: LIBRARY COPY
         * displayLabel after reload : null  ← null because @Transient was never stored
         *
         * --- Dirty checking: status updated without explicit save ---
         * Status before: BORROWED
         * Called setStatus(AVAILABLE) — no explicit save()
         * Hibernate: update books set isbn=?,book_title=?,status=? where id=?
         * Status after  : AVAILABLE  ← UPDATE was auto-generated by dirty checking
         */
    }
}
