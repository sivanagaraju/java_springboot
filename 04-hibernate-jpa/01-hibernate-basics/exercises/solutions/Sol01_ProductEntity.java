/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_ProductEntity.java                              ║
 * ║  MODULE : 04-hibernate-jpa / 01-hibernate-basics                ║
 * ║  SOLUTION FOR : Ex01_ProductEntity.java                         ║
 * ║  NOTE   : SOLUTION — read after attempting the exercise         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  KEY LEARNINGS FROM THIS EXERCISE                               ║
 * ║  1. @Entity is required — Hibernate ignores unannotated classes  ║
 * ║  2. @Table(name=...) decouples the Java name from the SQL name   ║
 * ║  3. GenerationType.IDENTITY delegates ID generation to the DB    ║
 * ║  4. EnumType.STRING is safe; EnumType.ORDINAL causes corruption  ║
 * ║     if enum values are ever reordered                           ║
 * ║  5. @Transient fields are never stored — confirmed null on load  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics.exercises.solutions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Solution for Ex01 — All 6 JPA annotation TODOs implemented.
 *
 * <p>Each annotation choice is explained with a WHY comment so you understand
 * the reasoning, not just the syntax.</p>
 *
 * <p><b>Python/SQLAlchemy equivalent:</b>
 * <pre>
 *   class Product(Base):
 *       __tablename__ = "products"
 *       id     = Column(Integer, primary_key=True, autoincrement=True)
 *       name   = Column(String(100), nullable=False)
 *       price  = Column(Float)
 *       status = Column(Enum(ProductStatus))
 *       # display_label is NOT a column — transient in Python too
 * </pre>
 * </p>
 */
public class Sol01_ProductEntity {

    // TODO 1 SOLUTION: @Entity
    // WHY: Marks this class for Hibernate's classpath scanning. Without this
    //      annotation, Hibernate ignores the class entirely — you will get
    //      MappingException("Unknown entity: Product") at startup.

    // TODO 2 SOLUTION: @Table(name = "products")
    // WHY: Binds the entity to the "products" table explicitly. Without this,
    //      Hibernate defaults the table name to the class name ("Product").
    //      Explicit names prevent silent breakage if the class is ever renamed
    //      during a refactor — the column mapping stays correct.
    @Entity
    @Table(name = "products")
    public static class Product {

        // TODO 3 SOLUTION: @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
        // WHY @Id: Designates this field as the table's primary key. Every JPA
        //          entity must have exactly one @Id field.
        // WHY IDENTITY: Delegates ID generation to the database's auto-increment
        //               column (SERIAL in PostgreSQL, AUTO_INCREMENT in MySQL,
        //               IDENTITY in H2). Hibernate does NOT generate the value —
        //               it reads it back after the INSERT fires.
        //               IDENTITY is the simplest strategy for single-app setups.
        //               For multi-entity apps on PostgreSQL, prefer SEQUENCE with
        //               a named @SequenceGenerator to avoid the global sequence table.
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // TODO 4 SOLUTION: @Column(nullable = false, length = 100)
        // WHY nullable = false: Adds a NOT NULL DDL constraint. Hibernate enforces
        //                       this at the database level when hbm2ddl creates the
        //                       schema. A null name INSERT will fail with
        //                       ConstraintViolationException.
        // WHY length = 100: Maps to VARCHAR(100) in the DDL. Without this,
        //                   Hibernate defaults String to VARCHAR(255).
        //                   Explicit lengths prevent over-allocating storage and
        //                   make the schema intention clear.
        @Column(nullable = false, length = 100)
        private String name;

        // price: no annotation needed — Hibernate maps Java double to DOUBLE
        // PRECISION automatically. Default column name = field name ("price").
        private double price;

        // TODO 5 SOLUTION: @Enumerated(EnumType.STRING)
        // WHY STRING and not ORDINAL:
        //   ORDINAL stores 0, 1, 2 (enum position). If you ever insert a new
        //   constant between AVAILABLE and OUT_OF_STOCK, all existing DB rows
        //   now point to the wrong constant. This is SILENT data corruption —
        //   no exception, no warning, just wrong data.
        //   STRING stores "AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED".
        //   New constants can be inserted anywhere in the enum without affecting
        //   existing DB rows. The DB column is also human-readable without a code
        //   lookup table.
        @Enumerated(EnumType.STRING)
        private ProductStatus status;

        // TODO 6 SOLUTION: @Transient
        // WHY: This field is computed from name + price and is not useful to store
        //      in the database. @Transient tells Hibernate to skip this field
        //      entirely — no DDL column, no INSERT value, no SELECT read.
        //      After reloading from DB, this field is null (confirmed in main()).
        //      Recompute it in a getter when needed rather than storing it.
        @Transient
        private String displayLabel; // computed as: name + " ($" + price + ")"

        // --- Provided code — unchanged from exercise ---

        /**
         * All-args constructor used when creating a new Product.
         *
         * @param name   product name
         * @param price  retail price
         * @param status availability status
         */
        public Product(String name, double price, ProductStatus status) {
            this.name = name;
            this.price = price;
            this.status = status;
            // Set in memory here — but @Transient means Hibernate never stores it.
            // This value is null after any session.find() call.
            this.displayLabel = name + " ($" + price + ")";
        }

        /**
         * No-arg constructor required by JPA.
         * Hibernate instantiates entities via reflection using this constructor.
         */
        public Product() {}

        /** @return DB-generated primary key */
        public Long getId()              { return id; }

        /** @return product name */
        public String getName()          { return name; }

        /** @return retail price */
        public double getPrice()         { return price; }

        /** @return availability status */
        public ProductStatus getStatus() { return status; }

        /**
         * Returns the in-memory computed display label.
         * This returns null when the entity was loaded from DB because
         * @Transient fields are never populated by Hibernate.
         *
         * @return display label, or null if loaded from database
         */
        public String getDisplayLabel()  { return displayLabel; }

        @Override
        public String toString() {
            return "Product{id=" + id
                    + ", name='" + name + "'"
                    + ", price=" + price
                    + ", status=" + status
                    + ", displayLabel='" + displayLabel + "'"
                    + "}";
        }
    }

    /**
     * Availability status of a product.
     * Always use @Enumerated(EnumType.STRING) — never ORDINAL.
     */
    public enum ProductStatus {
        AVAILABLE,
        OUT_OF_STOCK,
        DISCONTINUED
    }

    /**
     * Entry point — persists a Product, reads it back, and demonstrates
     * that the @Transient field is null after reload.
     *
     * <p>Run with:
     * ./gradlew :04-hibernate-jpa:run
     *   -PmainClass=com.learning.hibernate.basics.exercises.solutions.Sol01_ProductEntity
     * </p>
     *
     * @param args unused
     */
    public static void main(String[] args) {
        // H2 in-memory database — no Docker required, destroyed on JVM exit
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url",
                        "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.dialect",
                        "org.hibernate.dialect.H2Dialect")
                // create-drop: generates schema from annotations, drops on close
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                // show_sql: observe every INSERT and SELECT statement
                .applySetting("hibernate.show_sql", "true")
                .build();

        MetadataSources sources = new MetadataSources(registry);
        // Register Product — Hibernate reads all annotations we added above
        sources.addAnnotatedClass(Product.class);

        // buildSessionFactory() validates annotations and creates the schema DDL.
        // If @Entity is missing, this throws MappingException immediately.
        try (SessionFactory sf = sources.buildMetadata().buildSessionFactory()) {

            // --- PHASE 1: Persist ---
            Long savedId;
            try (var session = sf.openSession()) {
                var tx = session.beginTransaction();

                // TRANSIENT state — new object, Hibernate unaware of it
                Product laptop = new Product("Laptop", 999.99, ProductStatus.AVAILABLE);

                // displayLabel is populated in the constructor (in-memory only)
                System.out.println("Before persist, displayLabel in memory: '"
                        + laptop.getDisplayLabel() + "'");

                // persist() → PERSISTENT; DB id will be assigned after commit
                session.persist(laptop);

                // commit() fires the INSERT SQL (observe in show_sql output)
                tx.commit();
                savedId = laptop.getId();
                System.out.println("INSERT fired. DB assigned id: " + savedId);
            } // session.close() → entity transitions to DETACHED

            // --- PHASE 2: Reload and verify @Transient behaviour ---
            try (var session = sf.openSession()) {
                // find() fires SELECT, constructs entity via no-arg constructor
                // + reflection — @Transient fields are skipped entirely
                Product loaded = session.find(Product.class, savedId);

                // SUCCESS output
                System.out.println("\nProduct saved! id=" + loaded.getId()
                        + ", name=" + loaded.getName()
                        + ", status=" + loaded.getStatus());

                // The @Transient demonstration
                System.out.println("\n@Transient field 'displayLabel' after reload: '"
                        + loaded.getDisplayLabel() + "'");
                System.out.println(
                        "Result: null — Hibernate never stored or restored this field");
            }

        } catch (Exception e) {
            System.err.println("Solution failed unexpectedly: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/* ═══════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Forgetting @Entity
 *   WRONG: public class Product { ... }
 *   PROBLEM: Hibernate ignores this class entirely. MappingException at startup:
 *            "Unknown entity: com.learning.hibernate.basics.exercises.Product"
 *   RIGHT: @Entity public class Product { ... }
 *
 * MISTAKE 2: Using EnumType.ORDINAL
 *   WRONG: @Enumerated(EnumType.ORDINAL)
 *   PROBLEM: Stores 0, 1, 2 for the enum position. Add a new enum value in the
 *            middle (e.g., BACKORDERED between AVAILABLE and OUT_OF_STOCK) and
 *            all existing DB records now map to wrong values. Silent data corruption
 *            — no exception, no warning at runtime. Catastrophic in production.
 *   RIGHT: @Enumerated(EnumType.STRING) — stores "AVAILABLE". Safe to reorder.
 *
 * MISTAKE 3: No no-arg constructor
 *   WRONG: only public Product(String name, double price) { ... }
 *   PROBLEM: JPA requires a no-arg constructor (protected or public) for reflection.
 *            Hibernate calls it to create an empty instance, then sets fields one
 *            by one. Without it, InstantiationException is thrown at runtime.
 *   RIGHT: Add public Product() {} or protected Product() {}
 *
 * MISTAKE 4: GenerationType.AUTO without explicit sequence on PostgreSQL
 *   WRONG: @GeneratedValue(strategy = GenerationType.AUTO) on PostgreSQL
 *   PROBLEM: Hibernate creates a single global "hibernate_sequence" table shared
 *            across ALL entities. IDs become non-sequential per entity and gaps
 *            appear that are confusing in production monitoring.
 *   RIGHT: Use GenerationType.IDENTITY for simple single-entity apps, or
 *          GenerationType.SEQUENCE with a named @SequenceGenerator for
 *          multi-entity apps on PostgreSQL.
 *
 * MISTAKE 5: Making the @Id field final
 *   WRONG: private final Long id;
 *   PROBLEM: JPA reflection sets the id field after the INSERT fires. A final
 *            field cannot be mutated by reflection outside the constructor.
 *            Throws IllegalAccessException or leaves id as null permanently.
 *   RIGHT: private Long id; — mutable, Hibernate sets it after INSERT.
 * ═══════════════════════════════════════════════════════════ */
