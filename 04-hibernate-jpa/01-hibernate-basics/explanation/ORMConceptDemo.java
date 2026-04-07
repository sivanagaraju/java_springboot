/*
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║  FILE     : ORMConceptDemo.java                                              ║
 * ║  MODULE   : 04-hibernate-jpa / 01-hibernate-basics                           ║
 * ║  GRADLE   : ./gradlew :04-hibernate-jpa:run                                  ║
 * ║             -PmainClass=com.learning.hibernate.basics.ORMConceptDemo          ║
 * ║  PURPOSE  : Contrast raw JDBC INSERT (~12 lines) with Hibernate              ║
 * ║             persist() (3 lines) for the same Product entity.                 ║
 * ║  WHY      : Before ORM, every CRUD op was 10-15 lines of JDBC plumbing:      ║
 * ║             getConnection → prepareStatement → bind params by index →        ║
 * ║             executeUpdate → close everything. One typo in a parameter index  ║
 * ║             silently stores wrong data. Hibernate eliminates that plumbing.  ║
 * ║  PYTHON   : Equivalent to showing raw psycopg2 cursor vs SQLAlchemy session. ║
 * ║             session.add(product) vs cursor.execute("INSERT ...", (name,price))║
 * ║                                                                              ║
 * ║  ASCII Diagram — What this demo shows:                                       ║
 * ║                                                                              ║
 * ║   JDBC:                        Hibernate:                                    ║
 * ║   ┌────────────────────┐       ┌─────────────────────┐                       ║
 * ║   │ Connection         │       │ SessionFactory       │                       ║
 * ║   │ PreparedStatement  │  vs   │ Session.persist()    │                       ║
 * ║   │ bind param 1       │       │ tx.commit()          │                       ║
 * ║   │ bind param 2       │       └─────────────────────┘                       ║
 * ║   │ executeUpdate()    │         ~3 lines total                               ║
 * ║   │ close stmt         │                                                      ║
 * ║   │ close conn         │                                                      ║
 * ║   └────────────────────┘                                                      ║
 * ║     ~12 lines per INSERT                                                      ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.*;
import java.util.List;

/**
 * ORMConceptDemo — side-by-side comparison of raw JDBC vs Hibernate ORM.
 *
 * <p><b>Responsibility:</b> Demonstrate in one runnable file exactly how much
 * boilerplate ORM eliminates, and how Hibernate generates SQL transparently.</p>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # JDBC equivalent — raw psycopg2
 *   conn = psycopg2.connect(dsn)
 *   cur = conn.cursor()
 *   cur.execute("INSERT INTO products (name, price) VALUES (%s, %s)", (name, price))
 *   conn.commit()
 *   cur.close(); conn.close()
 *
 *   # Hibernate equivalent — SQLAlchemy
 *   session.add(Product(name=name, price=price))
 *   session.commit()
 * </pre>
 * </p>
 *
 * <p><b>ASCII Position in module:</b>
 * <pre>
 *   01-orm-concept.md        ← concepts
 *   03-entity-annotations.md ← annotations
 *   ORMConceptDemo.java      ← THIS FILE — runnable contrast
 * </pre>
 * </p>
 */
public class ORMConceptDemo {

    // ─────────────────────────────────────────────────────────────────────────
    // INNER ENTITY CLASS
    // WHY inner static class: keeps this demo self-contained in one file.
    //     In real Spring Boot projects, entities live in their own files.
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Product — a minimal JPA entity demonstrating the four essential annotations.
     *
     * <p>Every JPA entity needs:
     * <ul>
     *   <li>{@code @Entity} — marks the class for Hibernate's reflection scan</li>
     *   <li>{@code @Table} — specifies the database table name</li>
     *   <li>{@code @Id} — marks the primary key field</li>
     *   <li>{@code @GeneratedValue} — delegates ID assignment to the database</li>
     * </ul>
     * </p>
     */
    @Entity
    @Table(name = "products")
    public static class Product {

        // WHY @Id: every entity must have exactly one primary key field.
        //     Hibernate uses this to identity entities in its L1 cache.
        @Id
        // WHY IDENTITY: tells the DB to auto-increment (H2: IDENTITY column).
        //     Hibernate sends the INSERT first and reads back the generated key.
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // WHY nullable=false: maps to a NOT NULL constraint in the DB schema.
        //     Hibernate validates this at persist() time before sending SQL.
        @Column(nullable = false)
        private String name;

        // WHY no @Column: Hibernate uses default column name = field name ("price").
        //     Explicit @Column is only needed when you need to customize name/constraints.
        private double price;

        /** Required by JPA spec — Hibernate uses reflection to instantiate entities. */
        protected Product() {}

        public Product(String name, double price) {
            this.name  = name;
            this.price = price;
        }

        public Long   getId()    { return id; }
        public String getName()  { return name; }
        public double getPrice() { return price; }

        @Override
        public String toString() {
            return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAIN
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Runs the three-section demo: JDBC contrast, Hibernate persist, Hibernate query.
     *
     * @param args not used
     * @throws Exception if JDBC or Hibernate setup fails
     */
    public static void main(String[] args) throws Exception {

        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║        ORM Concept Demo — JDBC vs Hibernate      ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        // ─────────────────────────────────────────────────────────────────────
        // SECTION 1: JDBC APPROACH — raw boilerplate for one INSERT
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n══ SECTION 1: Raw JDBC INSERT (~12 lines) ══\n");

        // WHY: Load H2 driver explicitly — JDBC does not auto-discover drivers
        //      in standalone (non-Spring) environments before Java 9's ServiceLoader.
        Class.forName("org.h2.Driver");

        // WHY DB_CLOSE_DELAY=-1: H2 in-memory databases close when the last connection
        //     closes by default. -1 keeps the DB alive for the duration of the JVM.
        try (Connection conn = DriverManager.getConnection(
                "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1", "sa", "")) {

            // WHY: Create the table manually — JDBC has no schema management.
            //      Hibernate with hbm2ddl.auto=create-drop does this automatically.
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS products (" +
                "  id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY," +
                "  name  VARCHAR(255) NOT NULL," +
                "  price DOUBLE" +
                ")"
            );

            // WHY PreparedStatement: prevents SQL injection by binding parameters
            //     as typed values rather than concatenating strings.
            String sql = "INSERT INTO products (name, price) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "JDBC Laptop");  // WHY: bind by index (1-based, error-prone)
                ps.setDouble(2, 1299.99);
                int rowsAffected = ps.executeUpdate();
                System.out.println("  JDBC inserted rows: " + rowsAffected);
                System.out.println("  SQL executed: INSERT INTO products (name, price) VALUES ('JDBC Laptop', 1299.99)");
                System.out.println("  Lines of code for one INSERT: ~12 (plus error handling)");
            }
        }

        // ─────────────────────────────────────────────────────────────────────
        // SECTION 2: HIBERNATE APPROACH — same INSERT via session.persist()
        // ─────────────────────────────────────────────────────────────────────
        System.out.println("\n══ SECTION 2: Hibernate INSERT — 3 lines of business code ══\n");

        // WHY StandardServiceRegistryBuilder: programmatic alternative to hibernate.cfg.xml.
        //     Spring Boot does this for you via LocalContainerEntityManagerFactoryBean.
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url",          "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
            .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
            .applySetting("hibernate.connection.username",     "sa")
            .applySetting("hibernate.connection.password",     "")
            // WHY H2Dialect: tells Hibernate to generate H2-compatible SQL.
            //     In production, use org.hibernate.dialect.PostgreSQLDialect.
            .applySetting("hibernate.dialect",                 "org.hibernate.dialect.H2Dialect")
            // WHY create-drop: drops and recreates all tables on startup/shutdown.
            //     Correct ONLY for tests. NEVER use in production.
            .applySetting("hibernate.hbm2ddl.auto",            "create-drop")
            // WHY show_sql=true: prints every SQL statement Hibernate generates.
            //     Disabled in production to reduce log volume.
            .applySetting("hibernate.show_sql",                "true")
            .applySetting("hibernate.format_sql",              "true")
            .build();

        // WHY addAnnotatedClass: tells Hibernate which classes to scan for @Entity.
        //     Spring Boot uses @EntityScan to discover all entity classes automatically.
        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Product.class);

        // WHY: buildSessionFactory() is the expensive one-time setup.
        //     At this point Hibernate creates the "products" table (hbm2ddl.auto=create-drop).
        try (SessionFactory sessionFactory = sources.buildMetadata().buildSessionFactory()) {

            System.out.println("  → SessionFactory created. Hibernate generated CREATE TABLE automatically.");
            System.out.println("  → Now persisting a Product...\n");

            // WHY try-with-resources on Session: Session implements AutoCloseable.
            //     Forgetting to close leaks a JDBC connection from the pool permanently.
            try (Session session = sessionFactory.openSession()) {
                Transaction tx = session.beginTransaction();

                // ── BUSINESS CODE: just 3 lines ──
                Product hibernateProduct = new Product("Hibernate Laptop", 1299.99);
                session.persist(hibernateProduct); // WHY: object is now PERSISTENT — Hibernate tracks it
                tx.commit();                       // WHY: triggers flush → INSERT SQL sent to DB
                // ── end of business code ──

                System.out.println("  Hibernate persisted: " + hibernateProduct);
                System.out.println("  Lines of business code for one INSERT: 3");
                System.out.println("  (Hibernate handled: connection, PreparedStatement,");
                System.out.println("   parameter binding, execute, close, transaction)");
            }

            // ─────────────────────────────────────────────────────────────────
            // SECTION 3: QUERY BACK — using JPQL
            // ─────────────────────────────────────────────────────────────────
            System.out.println("\n══ SECTION 3: Query All Products via JPQL ══\n");

            try (Session session = sessionFactory.openSession()) {

                // WHY JPQL "FROM Product": refers to the Product CLASS, not the "products" TABLE.
                //     Hibernate translates this to dialect-specific SQL automatically.
                List<Product> products = session
                    .createQuery("FROM Product ORDER BY name", Product.class)
                    .getResultList();

                System.out.println("  Products retrieved from DB:");
                for (Product p : products) {
                    System.out.println("    " + p);
                }

                System.out.println("\n  JPQL query:  FROM Product ORDER BY name");
                System.out.println("  SQL generated: SELECT p.id, p.name, p.price");
                System.out.println("                 FROM products p ORDER BY p.name");
            }

        } // WHY: sessionFactory.close() here — triggers DROP TABLE (hbm2ddl.auto=create-drop)

        System.out.println("\n✓ Demo complete. SessionFactory closed, H2 tables dropped.");

        /*
         * EXPECTED OUTPUT:
         * ═══════════════════════════════════════════════════════
         * ╔══════════════════════════════════════════════════╗
         * ║        ORM Concept Demo — JDBC vs Hibernate      ║
         * ╚══════════════════════════════════════════════════╝
         *
         * ══ SECTION 1: Raw JDBC INSERT (~12 lines) ══
         *   JDBC inserted rows: 1
         *   SQL executed: INSERT INTO products (name, price) VALUES ('JDBC Laptop', 1299.99)
         *   Lines of code for one INSERT: ~12 (plus error handling)
         *
         * ══ SECTION 2: Hibernate INSERT — 3 lines of business code ══
         *   → SessionFactory created. Hibernate generated CREATE TABLE automatically.
         *   → Now persisting a Product...
         *
         *   Hibernate SQL (show_sql=true):
         *       insert
         *       into
         *           products
         *           (name, price)
         *       values
         *           (?, ?)
         *
         *   Hibernate persisted: Product{id=1, name='Hibernate Laptop', price=1299.99}
         *   Lines of business code for one INSERT: 3
         *   (Hibernate handled: connection, PreparedStatement,
         *    parameter binding, execute, close, transaction)
         *
         * ══ SECTION 3: Query All Products via JPQL ══
         *   Hibernate SQL:
         *       select
         *           p1_0.id,
         *           p1_0.name,
         *           p1_0.price
         *       from
         *           products p1_0
         *       order by
         *           p1_0.name
         *
         *   Products retrieved from DB:
         *     Product{id=1, name='Hibernate Laptop', price=1299.99}
         *
         *   JPQL query:  FROM Product ORDER BY name
         *   SQL generated: SELECT p.id, p.name, p.price
         *                  FROM products p ORDER BY p.name
         *
         * ✓ Demo complete. SessionFactory closed, H2 tables dropped.
         * ═══════════════════════════════════════════════════════
         */
    }
}
