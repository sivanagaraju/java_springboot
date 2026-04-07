/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  FILE   : CrudDemo.java                                             ║
 * ║  MODULE : 04-hibernate-jpa / 01-hibernate-basics                   ║
 * ║  GRADLE : ./gradlew :04-hibernate-jpa:run                           ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrate all four CRUD operations (Create,     ║
 * ║                   Read, Update, Delete) and a JPQL query against    ║
 * ║                   a Product entity using Hibernate's Session API    ║
 * ║  WHY IT EXISTS  : Every application needs CRUD. Hibernate makes     ║
 * ║                   CREATE/UPDATE/DELETE declarative (no SQL written  ║
 * ║                   by hand) while JPQL lets you query by object      ║
 * ║                   fields rather than raw column names.              ║
 * ║  PYTHON COMPARE : SQLAlchemy session.add(), session.get(),          ║
 * ║                   field mutation + session.commit(), and            ║
 * ║                   session.delete() — same four operations           ║
 * ║  USE CASES      : Inventory management, product catalog, any        ║
 * ║                   domain model that needs persistent CRUD with      ║
 * ║                   filtering queries                                 ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — CRUD Operation → Hibernate → SQL mapping           ║
 * ║                                                                      ║
 * ║   Operation          Hibernate API          SQL Generated           ║
 * ║   ──────────────────────────────────────────────────────────        ║
 * ║   CREATE         session.persist(p)      INSERT INTO products       ║
 * ║   READ           session.find(cls, id)   SELECT * WHERE id=?        ║
 * ║   UPDATE (dirty) p.setPrice(949.99)      UPDATE products SET price  ║
 * ║   DELETE         session.remove(p)       DELETE FROM products       ║
 * ║   QUERY (JPQL)   createQuery(jpql)       SELECT * WHERE price > ?   ║
 * ║                                                                      ║
 * ╠══════════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :04-hibernate-jpa:run                   ║
 * ║  EXPECTED OUTPUT: See EXPECTED OUTPUT block at bottom of main()     ║
 * ║  RELATED FILES  : 05-crud-operations.md, SessionFactoryDemo.java    ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics;

// 1. Java standard library
import java.util.List;

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
 * CrudDemo — demonstrates Create, Read, Update, Delete, and JPQL filtering
 * on a {@code Product} entity backed by an H2 in-memory database.
 *
 * <p>Each section of {@code main()} targets exactly one operation and prints
 * both the Hibernate API call and the SQL it generates (via
 * {@code show_sql=true}). This makes it easy to trace the mapping from
 * Java object operations to their relational equivalents without reading
 * the Hibernate source code.</p>
 *
 * <p>The Update section deliberately uses dirty checking rather than
 * {@code session.merge()} to reinforce that in JPA you mutate fields —
 * you do not call a "save" method. The JPQL query section shows how
 * object-field names ({@code p.price}) map to column names ({@code price})
 * transparently.</p>
 *
 * <p><b>Python SQLAlchemy equivalent:</b>
 * <pre>
 *   # CREATE
 *   session.add(Product(name="Laptop", price=999.99, quantity_in_stock=50))
 *   session.commit()
 *
 *   # READ
 *   laptop = session.get(Product, 1)
 *
 *   # UPDATE (dirty tracking)
 *   laptop.price = 949.99
 *   session.commit()       # UPDATE emitted automatically
 *
 *   # DELETE
 *   session.delete(mouse)
 *   session.commit()
 *
 *   # QUERY
 *   results = session.execute(
 *       select(Product).where(Product.price > 100)
 *   ).scalars().all()
 * </pre>
 *
 * <p><b>ASCII — Architecture position:</b>
 * <pre>
 *   main() — orchestrates 5 CRUD sections
 *       │
 *       ▼
 *   CrudDemo   ← YOU ARE HERE
 *       │  each section opens a fresh Session for isolation
 *       ▼
 *   Session (Hibernate Unit of Work)
 *       │  translates entity mutations → INSERT/SELECT/UPDATE/DELETE
 *       ▼
 *   H2 in-memory database (jdbc:h2:mem:demo)
 * </pre>
 */
public class CrudDemo {

    // ──────────────────────────────────────────────────────────────────
    //  INNER ENTITY — Product
    // ──────────────────────────────────────────────────────────────────

    /**
     * Product — JPA entity representing a warehouse inventory item.
     *
     * <p>Contains only the fields needed to demonstrate all four CRUD
     * operations and a price-filter JPQL query. The {@code price} field
     * uses a primitive {@code double} for simplicity; production code
     * should use {@code BigDecimal} to avoid floating-point rounding
     * errors in financial calculations.</p>
     *
     * <p><b>Python SQLAlchemy equivalent:</b>
     * <pre>
     *   class Product(Base):
     *       __tablename__ = "products"
     *       id                = mapped_column(Integer, primary_key=True)
     *       name              = mapped_column(String(150), nullable=False)
     *       price             = mapped_column(Float, nullable=False)
     *       quantity_in_stock = mapped_column(Integer, nullable=False)
     * </pre>
     *
     * <p><b>ASCII — Database mapping:</b>
     * <pre>
     *   Product.java              products table
     *   ──────────────────────────────────────────────
     *   id (Long)             →   id BIGINT PK AUTO_INC
     *   name (String)         →   name VARCHAR(150) NOT NULL
     *   price (double)        →   price DOUBLE NOT NULL
     *   quantityInStock (int) →   quantity_in_stock INT NOT NULL
     * </pre>
     */
    @Entity
    @Table(name = "products")
    public static class Product {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 150)
        private String name;

        // WHY double not BigDecimal: demo simplicity. In production always use
        // BigDecimal for monetary values — double has IEEE 754 precision issues
        // (e.g. 0.1 + 0.2 != 0.3) that cause incorrect financial rounding.
        @Column(nullable = false)
        private double price;

        @Column(name = "quantity_in_stock", nullable = false)
        private int quantityInStock;

        /** Required no-arg constructor — JPA spec mandates this for proxy creation. */
        public Product() {}

        /**
         * Constructs a transient Product ready to be persisted.
         *
         * @param name             product display name, stored in {@code name} column
         * @param price            unit price in dollars (use BigDecimal in production)
         * @param quantityInStock  initial stock count; must be &gt;= 0
         */
        public Product(String name, double price, int quantityInStock) {
            this.name = name;
            this.price = price;
            this.quantityInStock = quantityInStock;
        }

        /** Returns the surrogate primary key assigned by the database. */
        public Long getId() { return id; }

        /** Returns the product display name. */
        public String getName() { return name; }

        /** Returns the current unit price. */
        public double getPrice() { return price; }

        /**
         * Updates the unit price.
         *
         * <p>Calling this inside an active session triggers dirty checking —
         * Hibernate will auto-generate UPDATE SQL at commit time with no
         * explicit {@code session.merge()} call needed.</p>
         *
         * @param price  new unit price; must be &gt;= 0
         */
        public void setPrice(double price) { this.price = price; }

        /** Returns the current quantity in stock. */
        public int getQuantityInStock() { return quantityInStock; }

        @Override
        public String toString() {
            return "Product{id=" + id
                + ", name='" + name + "'"
                + ", price=$" + price
                + ", qty=" + quantityInStock
                + "}";
        }
    }

    // ──────────────────────────────────────────────────────────────────
    //  DEMO ENTRY POINT
    // ──────────────────────────────────────────────────────────────────

    /**
     * Runs the complete CRUD demo across five clearly labelled sections,
     * each using a fresh session to prevent first-level cache interference.
     *
     * <p><b>Flow:</b>
     * <pre>
     *   main()
     *       │
     *       ├─ buildSessionFactory()  — H2 + Product entity registered
     *       │
     *       ├─ CREATE: persist 3 Products in one transaction
     *       │       └─ 3x INSERT INTO products ...
     *       │
     *       ├─ READ: session.find(Product.class, 1L)
     *       │       └─ SELECT * FROM products WHERE id=1
     *       │
     *       ├─ UPDATE: laptop.setPrice(949.99) → tx.commit()
     *       │       └─ UPDATE products SET price=949.99 WHERE id=1
     *       │           (no explicit save — dirty checking)
     *       │
     *       ├─ DELETE: session.remove(mouse)
     *       │       └─ DELETE FROM products WHERE id=2
     *       │
     *       ├─ QUERY: JPQL FROM Product WHERE price > :minPrice
     *       │       └─ SELECT * FROM products WHERE price > 100.0
     *       │
     *       └─ close SessionFactory
     * </pre>
     *
     * @param args  unused command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("=== CRUD Operations Demo ===");

        // WHY manual registry: mirrors exactly what Spring Boot auto-configures
        // behind @SpringBootApplication + spring.datasource.* properties.
        // Seeing it manually demystifies the "magic" in Spring Boot.
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", "jdbc:h2:mem:demo;DB_CLOSE_DELAY=-1")
            .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
            .applySetting("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            // WHY create-drop: guarantees a clean schema on every run —
            // never leave data from a previous run affecting assertions
            .applySetting("hibernate.hbm2ddl.auto", "create-drop")
            .applySetting("hibernate.show_sql", "true")
            .build();

        SessionFactory sessionFactory = new MetadataSources(registry)
            .addAnnotatedClass(Product.class)
            .buildMetadata()
            .buildSessionFactory();

        // ── CREATE ────────────────────────────────────────────────────
        System.out.println("\n=== CREATE: persist 3 products ===");

        // WHY capture IDs outside try-with-resources: the session closes at
        // the end of the block, making the entities DETACHED. IDs are primitive-
        // boxed Long values (eagerly loaded) so safe to use after session close.
        Long laptopId;
        Long mouseId;

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Product laptop  = new Product("Laptop",  999.99, 50);
            Product mouse   = new Product("Mouse",    29.99, 200);
            Product monitor = new Product("Monitor", 399.99, 75);

            System.out.println("Persisting: Laptop ($999.99, qty=50)");
            System.out.println("Persisting: Mouse ($29.99, qty=200)");
            System.out.println("Persisting: Monitor ($399.99, qty=75)");

            session.persist(laptop);
            session.persist(mouse);
            session.persist(monitor);

            tx.commit();
            // WHY flush before reading IDs: commit flushes, so IDs are
            // assigned by the DB auto-increment at this point
            System.out.println("SQL: 3x INSERT INTO products ... (see show_sql above)");
            System.out.println("IDs assigned: "
                + laptop.getId() + ", " + mouse.getId() + ", " + monitor.getId());

            laptopId = laptop.getId();
            mouseId  = mouse.getId();
        }

        // ── READ ──────────────────────────────────────────────────────
        System.out.println("\n=== READ: find by ID ===");

        try (Session session = sessionFactory.openSession()) {
            // WHY session.find(): the JPA-standard equivalent of EntityManager.find().
            // Hibernate checks the first-level cache first; if not found it emits
            // SELECT. session.get() is the Hibernate-legacy equivalent — prefer find().
            Product laptop = session.find(Product.class, laptopId);
            System.out.println("session.find(Product.class, " + laptopId + ") → " + laptop.getName());
            System.out.println("SQL: SELECT * FROM products WHERE id=" + laptopId
                + " (see show_sql above)");
        }

        // ── UPDATE (dirty checking) ───────────────────────────────────
        System.out.println("\n=== UPDATE: dirty checking (no explicit save) ===");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            Product laptop = session.find(Product.class, laptopId);
            System.out.println("laptop.setPrice(949.99)  ← just mutate the field");

            // WHY this works without merge(): session.find() returns a PERSISTENT
            // entity. Hibernate snapshots all field values when the entity enters
            // the persistence context. At tx.commit(), Hibernate diffs current
            // state vs. snapshot — price changed from 999.99 → 949.99, so an
            // UPDATE is generated automatically. No merge() or update() needed.
            laptop.setPrice(949.99);

            tx.commit();
            System.out.println("tx.commit()");
            System.out.println("SQL: UPDATE products SET price=949.99 WHERE id=" + laptopId);
            System.out.println("(No merge() or save() needed!)");
        }

        // ── DELETE ────────────────────────────────────────────────────
        System.out.println("\n=== DELETE: remove product ===");

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            // WHY re-fetch before remove: session.remove() requires a PERSISTENT
            // entity — you cannot pass a detached object directly. Always load
            // within the same session that will call remove().
            Product mouse = session.find(Product.class, mouseId);
            session.remove(mouse);

            tx.commit();
            System.out.println("session.remove(mouse)");
            System.out.println("SQL: DELETE FROM products WHERE id=" + mouseId
                + " (see show_sql above)");
        }

        // ── QUERY: JPQL with parameter binding ───────────────────────
        System.out.println("\n=== QUERY: find all products with price > 100 ===");

        try (Session session = sessionFactory.openSession()) {
            // WHY JPQL not SQL: JPQL uses entity and field names (Product, price)
            // not table and column names (products, price). This means renaming
            // a @Column does not break the query — only renaming the Java field does.
            // It is also database-agnostic: the same JPQL runs on H2, PostgreSQL,
            // MySQL, etc. without change.
            String jpql = """
                FROM Product p
                WHERE p.price > :minPrice
                ORDER BY p.price DESC
                """;

            System.out.println("JPQL: FROM Product p WHERE p.price > :minPrice");

            List<Product> expensive = session.createQuery(jpql, Product.class)
                // WHY setParameter: named parameters prevent SQL injection and
                // allow Hibernate to cache the prepared statement for reuse
                .setParameter("minPrice", 100.0)
                .list();

            expensive.forEach(p ->
                System.out.println("  " + p.getName() + " $" + p.getPrice())
            );

            System.out.println("SQL: SELECT * FROM products WHERE price > 100.0"
                + " ORDER BY price DESC (see show_sql above)");
        }

        sessionFactory.close();
        StandardServiceRegistryBuilder.destroy(registry);

        /*
         * EXPECTED OUTPUT:
         * ================
         * === CRUD Operations Demo ===
         *
         * === CREATE: persist 3 products ===
         * Persisting: Laptop ($999.99, qty=50)
         * Persisting: Mouse ($29.99, qty=200)
         * Persisting: Monitor ($399.99, qty=75)
         * Hibernate: insert into products (name,price,quantity_in_stock,id) values (?,?,?,default)
         * Hibernate: insert into products (name,price,quantity_in_stock,id) values (?,?,?,default)
         * Hibernate: insert into products (name,price,quantity_in_stock,id) values (?,?,?,default)
         * SQL: 3x INSERT INTO products ... (see show_sql above)
         * IDs assigned: 1, 2, 3
         *
         * === READ: find by ID ===
         * Hibernate: select p1_0.id,p1_0.name,p1_0.price,p1_0.quantity_in_stock
         *     from products p1_0 where p1_0.id=?
         * session.find(Product.class, 1) → Laptop
         * SQL: SELECT * FROM products WHERE id=1 (see show_sql above)
         *
         * === UPDATE: dirty checking (no explicit save) ===
         * Hibernate: select p1_0.id,p1_0.name,p1_0.price,p1_0.quantity_in_stock
         *     from products p1_0 where p1_0.id=?
         * laptop.setPrice(949.99)  ← just mutate the field
         * Hibernate: update products set name=?,price=?,quantity_in_stock=? where id=?
         * tx.commit()
         * SQL: UPDATE products SET price=949.99 WHERE id=1
         * (No merge() or save() needed!)
         *
         * === DELETE: remove product ===
         * Hibernate: select p1_0.id,p1_0.name,p1_0.price,p1_0.quantity_in_stock
         *     from products p1_0 where p1_0.id=?
         * Hibernate: delete from products where id=?
         * session.remove(mouse)
         * SQL: DELETE FROM products WHERE id=2 (see show_sql above)
         *
         * === QUERY: find all products with price > 100 ===
         * JPQL: FROM Product p WHERE p.price > :minPrice
         * Hibernate: select p1_0.id,p1_0.name,p1_0.price,p1_0.quantity_in_stock
         *     from products p1_0 where p1_0.price>? order by p1_0.price desc
         *   Laptop $949.99
         *   Monitor $399.99
         * SQL: SELECT * FROM products WHERE price > 100.0 ORDER BY price DESC (see show_sql above)
         */
    }
}
