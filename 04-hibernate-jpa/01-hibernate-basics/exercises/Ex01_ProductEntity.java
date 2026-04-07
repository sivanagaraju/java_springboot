/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_ProductEntity.java                               ║
 * ║  MODULE : 04-hibernate-jpa / 01-hibernate-basics                ║
 * ║  GRADLE : ./gradlew :04-hibernate-jpa:run                       ║
 * ║           -PmainClass=com.learning.hibernate.basics.exercises.Ex01_ProductEntity
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE LEVEL : 1 (Guided — target: under 20 minutes)         ║
 * ║  PURPOSE        : Annotate a Product entity with JPA annotations ║
 * ║  COVERS         : @Entity, @Table, @Id, @GeneratedValue,         ║
 * ║                   @Column, @Enumerated, @Transient               ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  INSTRUCTIONS                                                    ║
 * ║  Complete the 6 TODO items to annotate the Product class.       ║
 * ║  Read explanation/03-entity-annotations.md first.               ║
 * ║                                                                  ║
 * ║  SUCCESS CRITERIA:                                               ║
 * ║  Console shows: "Product saved! id=1, name=Laptop, status=AVAILABLE"
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics.exercises;

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
 * Exercise 01 — Annotate a Product entity with core JPA annotations.
 *
 * <p>JPA annotations are metadata that tell Hibernate how to map a plain Java
 * class to a relational database table. Without these annotations, Hibernate
 * ignores the class entirely.</p>
 *
 * <p><b>Python/SQLAlchemy equivalent:</b>
 * <pre>
 *   class Product(Base):
 *       __tablename__ = "products"
 *       id     = Column(Integer, primary_key=True, autoincrement=True)
 *       name   = Column(String(100), nullable=False)
 *       price  = Column(Float)
 *       status = Column(Enum(ProductStatus))
 *       # display_label is NOT a column — it is computed in Python, never stored
 * </pre>
 * </p>
 */
public class Ex01_ProductEntity {

    // =========================================================================
    // STEP 1 — Annotate the Product class (add annotations above the class and
    //           above each field as instructed by the TODOs below)
    // =========================================================================

    // TODO 1: Add @Entity annotation directly above "public static class Product {"
    //         This marks the class for Hibernate's classpath scanning.
    //         Without it, Hibernate raises MappingException at startup.

    // TODO 2: Add @Table(name = "products") directly above "public static class Product {"
    //         This binds the class to the "products" table.
    //         Without it, Hibernate defaults the table name to the class name ("Product").
    //         Explicit names prevent silent breakage if the class is ever renamed.
    public static class Product {

        // TODO 3: Add @Id AND @GeneratedValue(strategy = GenerationType.IDENTITY)
        //         @Id         → marks this field as the table's primary key
        //         @GeneratedValue → tells Hibernate NOT to assign the ID itself;
        //                           let the database auto-increment column do it.
        //         IDENTITY strategy works with H2, PostgreSQL, MySQL auto-increment.
        private Long id;

        // TODO 4: Add @Column(nullable = false, length = 100)
        //         nullable = false → DDL: NOT NULL constraint
        //         length = 100     → DDL: VARCHAR(100)
        //         Both constraints are enforced at the DB level when hbm2ddl creates
        //         the schema.
        private String name;

        // price has no special column constraints — Hibernate maps Java double to
        // DOUBLE PRECISION automatically. Leave this field unannotated.
        private double price;

        // TODO 5: Add @Enumerated(EnumType.STRING)
        //         WHY STRING and not ORDINAL?
        //         ORDINAL stores integer positions (0, 1, 2).
        //         If you insert a new enum value between existing ones (e.g., between
        //         AVAILABLE and OUT_OF_STOCK) every existing DB row silently maps to
        //         the wrong enum constant — undetectable data corruption.
        //         STRING stores the name ("AVAILABLE"), safe to reorder at any time.
        //         Full comparison in explanation/03-entity-annotations.md.
        private ProductStatus status;

        // TODO 6: Add @Transient
        //         This field must NOT be stored in the database.
        //         It is derived from name + price and should be recomputed as needed.
        //         After reading an entity back from DB, this field will be null.
        //         That is the expected behaviour — prove it in main() below.
        private String displayLabel; // computed as: name + " ($" + price + ")"

        // --- Provided code below — do not modify ---

        /**
         * All-args constructor — used when creating a new Product to persist.
         * Sets displayLabel in memory (it will be null after reloading from DB).
         *
         * @param name   product display name (max 100 chars)
         * @param price  retail price
         * @param status current availability status
         */
        public Product(String name, double price, ProductStatus status) {
            this.name = name;
            this.price = price;
            this.status = status;
            // displayLabel is populated here but @Transient means Hibernate never
            // stores it; after session.find() it comes back as null.
            this.displayLabel = name + " ($" + price + ")";
        }

        /**
         * No-arg constructor required by JPA specification.
         * Hibernate uses reflection to instantiate entities — it calls the no-arg
         * constructor first, then injects field values via reflection.
         * Without this, Hibernate throws InstantiationException at startup.
         */
        public Product() {}

        /** @return the DB-generated primary key, or null before first persist */
        public Long getId()              { return id; }

        /** @return the product name */
        public String getName()          { return name; }

        /** @return the retail price */
        public double getPrice()         { return price; }

        /** @return current availability status */
        public ProductStatus getStatus() { return status; }

        /**
         * Returns the computed display label.
         * NOTE: This is a @Transient field. After loading from the database,
         * this method returns null — Hibernate never stores or restores it.
         *
         * @return display label string, or null if the entity was loaded from DB
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

    // =========================================================================
    // Enum for Product.status — referenced by TODO 5 above
    // =========================================================================

    /**
     * Availability status of a product.
     * Always annotate the field that uses this enum with @Enumerated(EnumType.STRING).
     */
    public enum ProductStatus {
        AVAILABLE,
        OUT_OF_STOCK,
        DISCONTINUED
    }

    // =========================================================================
    // main() — provided, do not modify
    // =========================================================================

    /**
     * Builds an H2 in-memory SessionFactory, persists one Product, reads it back,
     * and proves that the @Transient field is null after reload.
     *
     * <p>No Docker required — H2 runs entirely in-process.</p>
     *
     * <p>Run with:</p>
     * <pre>
     *   ./gradlew :04-hibernate-jpa:run
     *     -PmainClass=com.learning.hibernate.basics.exercises.Ex01_ProductEntity
     * </pre>
     *
     * @param args unused
     */
    public static void main(String[] args) {
        // Build H2 in-memory registry — no external database needed
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url",
                        "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.dialect",
                        "org.hibernate.dialect.H2Dialect")
                // create-drop: generates schema from annotations on startup,
                // drops all tables on close — perfect for self-contained demos
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                // show_sql: prints every SQL statement — essential while learning
                .applySetting("hibernate.show_sql", "true")
                .build();

        // Tell Hibernate which classes are entities — reads the annotations you added
        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Product.class);

        // buildSessionFactory() processes annotations and creates the schema.
        // If TODO 1 (@Entity) is missing, this line throws MappingException.
        try (SessionFactory sf = sources.buildMetadata().buildSessionFactory()) {

            // --- PHASE 1: Persist ---
            Long savedId;
            try (var session = sf.openSession()) {
                var tx = session.beginTransaction();

                // Product is TRANSIENT here — Hibernate does not know it exists yet
                Product laptop = new Product("Laptop", 999.99, ProductStatus.AVAILABLE);
                System.out.println("Before persist (displayLabel in memory): "
                        + laptop.getDisplayLabel());

                // persist() → PERSISTENT state; Hibernate assigns the auto-generated id
                session.persist(laptop);

                // commit() fires the INSERT SQL
                tx.commit();
                savedId = laptop.getId();
                System.out.println("INSERT fired — assigned id: " + savedId);
            } // session closes → entity becomes DETACHED

            // --- PHASE 2: Reload and verify ---
            try (var session = sf.openSession()) {
                // find() fires a SELECT and returns a fresh PERSISTENT entity
                // This entity was built by Hibernate via the no-arg constructor +
                // reflection — displayLabel is never populated (it is @Transient)
                Product loaded = session.find(Product.class, savedId);

                System.out.println("\nProduct saved! id=" + loaded.getId()
                        + ", name=" + loaded.getName()
                        + ", status=" + loaded.getStatus());

                // The key demonstration: @Transient field is null after reload
                System.out.println("\n@Transient demonstration:");
                System.out.println("  displayLabel after DB reload = '"
                        + loaded.getDisplayLabel() + "'");
                System.out.println(
                        "  (null is correct — @Transient fields are never stored)");
            }

        } catch (Exception e) {
            System.err.println("\nExercise failed. Did you complete all 6 TODOs?");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
