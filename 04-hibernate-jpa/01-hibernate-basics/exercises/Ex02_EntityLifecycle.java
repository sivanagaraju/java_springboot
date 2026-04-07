/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex02_EntityLifecycle.java                             ║
 * ║  MODULE : 04-hibernate-jpa / 01-hibernate-basics                ║
 * ║  GRADLE : ./gradlew :04-hibernate-jpa:run                       ║
 * ║           -PmainClass=com.learning.hibernate.basics.exercises.Ex02_EntityLifecycle
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE LEVEL : 2 (Practitioner — target: 20–45 minutes)      ║
 * ║  PURPOSE        : Implement the 4 entity lifecycle operations    ║
 * ║  COVERS         : persist, find, dirty checking, detach,        ║
 * ║                   merge, remove                                  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  INSTRUCTIONS                                                    ║
 * ║  Implement the 4 method stubs below.                            ║
 * ║  The pre-built main() calls each method and prints results.     ║
 * ║  Read explanation/02-entity-lifecycle.md before starting.       ║
 * ║                                                                  ║
 * ║  SUCCESS CRITERIA:                                               ║
 * ║  Console prints 4 sections each showing the operation succeeded ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics.exercises;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Exercise 02 — Implement CRUD using the Hibernate entity lifecycle.
 *
 * <p>The four states of a JPA entity:</p>
 * <ul>
 *   <li><b>TRANSIENT</b>  — created with {@code new}, unknown to Hibernate, no ID</li>
 *   <li><b>PERSISTENT</b> — associated with an open Session; changes are tracked
 *       automatically (dirty checking)</li>
 *   <li><b>DETACHED</b>   — was persistent, but session is now closed; changes are
 *       NOT tracked</li>
 *   <li><b>REMOVED</b>    — scheduled for DELETE; still persistent until commit</li>
 * </ul>
 *
 * <p><b>Python/SQLAlchemy state equivalents:</b>
 * <pre>
 *   # TRANSIENT → "transient" (obj not added to session)
 *   # PERSISTENT → "persistent" (session.add(obj) or loaded from DB)
 *   # DETACHED → "detached" (session.expunge(obj) or session closed)
 *   # REMOVED → "deleted" (session.delete(obj), before commit)
 * </pre>
 * </p>
 */
public class Ex02_EntityLifecycle {

    // =========================================================================
    // Pre-built Item entity — do not modify
    // =========================================================================

    /**
     * A simple inventory item entity.
     * All JPA annotations are already applied — this is provided code.
     * Your task is to implement the four lifecycle methods below.
     */
    @Entity
    @Table(name = "items")
    public static class Item {

        /** Primary key — assigned by H2 auto-increment on persist. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /** Item display name — required, max 100 characters. */
        @Column(nullable = false, length = 100)
        private String name;

        /** Retail price — no special DB constraint. */
        @Column
        private double price;

        /**
         * All-args constructor for creating new items.
         *
         * @param name  item name
         * @param price retail price
         */
        public Item(String name, double price) {
            this.name  = name;
            this.price = price;
        }

        /** No-arg constructor required by JPA specification. */
        public Item() {}

        /** @return DB-generated primary key */
        public Long getId()     { return id; }

        /** @return item name */
        public String getName() { return name; }

        /** @return retail price */
        public double getPrice() { return price; }

        /**
         * Sets a new name.
         * When the entity is PERSISTENT, Hibernate detects this call at flush/commit
         * and generates an UPDATE statement automatically (dirty checking).
         *
         * @param name new name value
         */
        public void setName(String name)   { this.name  = name; }

        /**
         * Sets a new price.
         * Same dirty-checking behaviour as setName — no explicit save() needed.
         *
         * @param price new price value
         */
        public void setPrice(double price) { this.price = price; }

        @Override
        public String toString() {
            return "Item{id=" + id + ", name='" + name + "', price=" + price + "}";
        }
    }

    // =========================================================================
    // LIFECYCLE METHOD STUBS — implement these four methods
    // =========================================================================

    /**
     * Persist a new Item and return its database-generated ID.
     *
     * <p>State transitions to implement:
     * <pre>
     *   new Item(name, price)   → TRANSIENT  (no ID, Hibernate unaware)
     *   session.persist(item)   → PERSISTENT (ID not yet assigned)
     *   tx.commit()             → INSERT SQL fires; ID now populated
     * </pre>
     * </p>
     *
     * <p>Steps:
     * <ol>
     *   <li>Open a session (try-with-resources to ensure it closes)</li>
     *   <li>Begin a transaction</li>
     *   <li>Create: {@code Item item = new Item(name, price)} — TRANSIENT</li>
     *   <li>{@code session.persist(item)} — becomes PERSISTENT, ID will be set</li>
     *   <li>{@code tx.commit()} — INSERT fires, {@code item.getId()} is now set</li>
     *   <li>Return {@code item.getId()}</li>
     * </ol>
     * </p>
     *
     * @param sf    the SessionFactory to open sessions from
     * @param name  item name to store
     * @param price item price to store
     * @return the database-generated ID for the new item
     */
    static Long createItem(SessionFactory sf, String name, double price) {
        // TODO: implement
        // Hint: session.persist(item) does not immediately fire SQL.
        //       The INSERT happens at tx.commit() (or at flush time).
        throw new UnsupportedOperationException("createItem not implemented yet");
    }

    /**
     * Find an item by ID and update its price using Hibernate dirty checking.
     *
     * <p>DO NOT call {@code session.merge()}, {@code session.update()}, or any
     * explicit save method — dirty checking handles the UPDATE automatically.</p>
     *
     * <p>Steps:
     * <ol>
     *   <li>Open session, begin transaction</li>
     *   <li>{@code session.find(Item.class, id)} — loads a PERSISTENT entity</li>
     *   <li>{@code item.setPrice(newPrice)} — Hibernate watches this assignment</li>
     *   <li>{@code tx.commit()} — Hibernate compares entity state to snapshot taken
     *       at load time; detects price changed; generates UPDATE for that column only</li>
     * </ol>
     * </p>
     *
     * @param sf       the SessionFactory to open sessions from
     * @param id       ID of the item to update
     * @param newPrice the new price to set
     */
    static void updatePrice(SessionFactory sf, Long id, double newPrice) {
        // TODO: implement
        // Hint: you do NOT need to call session.merge(), session.update(), or
        //       session.save(). Simply mutating the PERSISTENT entity is enough.
        //       This is the core of Hibernate's dirty-checking mechanism.
        throw new UnsupportedOperationException("updatePrice not implemented yet");
    }

    /**
     * Load an item, detach it, modify it offline, then re-attach via merge().
     *
     * <p>This method demonstrates the DETACHED state — common in web applications
     * where entities are loaded in one HTTP request, modified in a form, then
     * sent back in a second HTTP request with a different session.</p>
     *
     * <p>Steps:
     * <ol>
     *   <li>Open session 1, begin tx1</li>
     *   <li>{@code session1.find(Item.class, id)} — PERSISTENT in session 1</li>
     *   <li>Close session 1 → item becomes DETACHED (Hibernate can no longer track it)</li>
     *   <li>{@code item.setName(newName)} — this change is invisible to Hibernate</li>
     *   <li>Open session 2, begin tx2</li>
     *   <li>{@code Item managed = session2.merge(item)} — Hibernate issues a SELECT
     *       to check current DB state, then schedules an UPDATE with the new name;
     *       returns a new PERSISTENT proxy ({@code managed})</li>
     *   <li>tx2.commit() — UPDATE fires</li>
     *   <li>Return {@code managed}</li>
     * </ol>
     * </p>
     *
     * @param sf      the SessionFactory to open sessions from
     * @param id      ID of the item to load and modify
     * @param newName the new name to set on the detached entity
     * @return the re-attached managed entity after merge
     */
    static Item detachAndReattach(SessionFactory sf, Long id, String newName) {
        // TODO: implement
        // Hint: merge() returns a NEW managed entity — the original detached
        //       reference is not re-attached. Use the return value of merge().
        throw new UnsupportedOperationException("detachAndReattach not implemented yet");
    }

    /**
     * Delete an item by ID.
     *
     * <p>Steps:
     * <ol>
     *   <li>Open session, begin transaction</li>
     *   <li>{@code session.find(Item.class, id)} — returns null if not found</li>
     *   <li>If null, print a warning and return — handle gracefully</li>
     *   <li>{@code session.remove(item)} — transitions entity to REMOVED state</li>
     *   <li>{@code tx.commit()} — DELETE SQL fires</li>
     * </ol>
     * </p>
     *
     * @param sf the SessionFactory to open sessions from
     * @param id ID of the item to delete
     */
    static void deleteItem(SessionFactory sf, Long id) {
        // TODO: implement
        // Hint: session.remove() requires a PERSISTENT entity.
        //       You must load it with find() first — you cannot pass just an ID.
        //       If find() returns null, the item does not exist — handle that case.
        throw new UnsupportedOperationException("deleteItem not implemented yet");
    }

    // =========================================================================
    // Pre-built SessionFactory builder and main() — do not modify
    // =========================================================================

    /**
     * Builds an H2 in-memory SessionFactory for the Item entity.
     * No Docker required — H2 runs entirely in-process.
     *
     * @return configured SessionFactory
     */
    static SessionFactory buildSessionFactory() {
        // H2 in-memory database — destroyed when the JVM exits
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url",
                        "jdbc:h2:mem:lifecycle;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.dialect",
                        "org.hibernate.dialect.H2Dialect")
                // create-drop: Hibernate creates "items" table from @Entity, drops on close
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                // show_sql: every SQL printed to stdout — observe each lifecycle operation
                .applySetting("hibernate.show_sql", "true")
                .build();

        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Item.class);
        return sources.buildMetadata().buildSessionFactory();
    }

    /**
     * Entry point — calls all four lifecycle methods and prints results.
     * When your implementations are correct, this prints the success output
     * described in the exercise header.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        try (SessionFactory sf = buildSessionFactory()) {

            System.out.println("=== Entity Lifecycle Exercise ===\n");

            // --- createItem ---
            System.out.println("--- createItem: \"Widget\" at $9.99 ---");
            Long id = createItem(sf, "Widget", 9.99);
            System.out.println("Created item with id: " + id);

            // --- updatePrice (dirty checking) ---
            System.out.println("\n--- updatePrice: set price to $14.99 (dirty checking) ---");
            updatePrice(sf, id, 14.99);
            System.out.println("No explicit save() needed — Hibernate detects field change at commit");

            // Reload to confirm the update was persisted
            try (var session = sf.openSession()) {
                Item loaded = session.find(Item.class, id);
                System.out.println("Verified in DB: " + loaded);
            }

            // --- detachAndReattach ---
            System.out.println("\n--- detachAndReattach: rename to \"Super Widget\" ---");
            Item reattached = detachAndReattach(sf, id, "Super Widget");
            System.out.println("Re-attached item name: " + reattached.getName());

            // --- deleteItem ---
            System.out.println("\n--- deleteItem: remove id=" + id + " ---");
            deleteItem(sf, id);

            // Confirm deletion
            try (var session = sf.openSession()) {
                Item gone = session.find(Item.class, id);
                System.out.println("After delete, find() returns: " + gone
                        + " (null = successfully deleted)");
            }

        } catch (UnsupportedOperationException e) {
            System.err.println("Not implemented yet: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exercise failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
