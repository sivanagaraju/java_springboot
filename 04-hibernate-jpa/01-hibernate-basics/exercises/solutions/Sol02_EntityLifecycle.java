/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_EntityLifecycle.java                            ║
 * ║  MODULE : 04-hibernate-jpa / 01-hibernate-basics                ║
 * ║  SOLUTION FOR : Ex02_EntityLifecycle.java                       ║
 * ║  NOTE   : SOLUTION — read after attempting the exercise         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  KEY LEARNINGS FROM THIS EXERCISE                               ║
 * ║  1. persist() = TRANSIENT → PERSISTENT (INSERT at commit)       ║
 * ║  2. Dirty checking fires UPDATE automatically — no save() needed ║
 * ║  3. Closing a session detaches all its entities                  ║
 * ║  4. merge() re-attaches detached entities (SELECT + UPDATE)      ║
 * ║  5. remove() requires a PERSISTENT entity — load first          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.hibernate.basics.exercises.solutions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Solution for Ex02 — All 4 entity lifecycle methods implemented with WHY comments.
 *
 * <p>State machine summary:
 * <pre>
 *   new Item()          → TRANSIENT
 *   persist(item)       → PERSISTENT   (INSERT queued)
 *   tx.commit()         → PERSISTENT   (INSERT fires, id populated)
 *   session.close()     → DETACHED
 *   session2.merge(item)→ PERSISTENT   (SELECT + UPDATE queued)
 *   session.remove(item)→ REMOVED      (DELETE queued)
 *   tx.commit()         → (gone from DB)
 * </pre>
 * </p>
 *
 * <p><b>Python/SQLAlchemy state equivalents:</b>
 * <pre>
 *   # TRANSIENT  → object not added to session
 *   # PERSISTENT → session.add(obj) or loaded via query
 *   # DETACHED   → session.expunge(obj), or session closed
 *   # REMOVED    → session.delete(obj), before session.commit()
 * </pre>
 * </p>
 */
public class Sol02_EntityLifecycle {

    // =========================================================================
    // Pre-built Item entity — identical to the exercise file
    // =========================================================================

    /**
     * A simple inventory item entity.
     */
    @Entity
    @Table(name = "items")
    public static class Item {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 100)
        private String name;

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
        public Long getId()      { return id; }

        /** @return item name */
        public String getName()  { return name; }

        /** @return retail price */
        public double getPrice() { return price; }

        /**
         * Mutates the name. When the entity is PERSISTENT, Hibernate records
         * this change and generates an UPDATE at tx.commit().
         *
         * @param name new name value
         */
        public void setName(String name)   { this.name  = name; }

        /**
         * Mutates the price. Same dirty-checking behaviour as setName.
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
    // LIFECYCLE METHOD IMPLEMENTATIONS
    // =========================================================================

    /**
     * Persist a new Item and return its DB-generated ID.
     *
     * <p>State transitions:
     * <pre>
     *   new Item(name, price)  → TRANSIENT  (no id, Hibernate unaware)
     *   session.persist(item)  → PERSISTENT (id still null — INSERT queued)
     *   tx.commit()            → PERSISTENT (INSERT fires, id now set by DB)
     *   return item.getId()    → Long (auto-increment value from H2)
     * </pre>
     * </p>
     *
     * @param sf    SessionFactory to open sessions from
     * @param name  item name
     * @param price item price
     * @return the database-generated primary key
     */
    static Long createItem(SessionFactory sf, String name, double price) {
        // try-with-resources ensures session.close() even if an exception occurs.
        // In Spring Boot, @Transactional handles open/close automatically.
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();

            // TRANSIENT — new object, id is null, Hibernate has no record of it
            Item item = new Item(name, price);

            // persist() transitions item to PERSISTENT state.
            // The INSERT SQL is NOT fired here — it is queued in the "action queue".
            // WHY defer: Hibernate batches multiple INSERTs into one round-trip when
            // possible, reducing network overhead.
            session.persist(item);

            // commit() flushes the action queue → INSERT fires here.
            // The database generates the id (AUTO_INCREMENT / SERIAL), and Hibernate
            // reads it back via JDBC's getGeneratedKeys() and sets item.id.
            tx.commit();

            // item.getId() is now populated — safe to return
            return item.getId();
        }
    }

    /**
     * Find an item and update its price using Hibernate dirty checking.
     *
     * <p>Key insight: no explicit save call is needed. Hibernate takes a snapshot
     * of the entity's field values when it is loaded. At flush/commit time it
     * compares the current field values to the snapshot. Any differences generate
     * UPDATE statements for those columns only.</p>
     *
     * @param sf       SessionFactory to open sessions from
     * @param id       ID of the item to update
     * @param newPrice the new price to assign
     */
    static void updatePrice(SessionFactory sf, Long id, double newPrice) {
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();

            // find() issues a SELECT and returns a PERSISTENT entity.
            // Hibernate immediately takes a "snapshot" of all field values.
            Item item = session.find(Item.class, id);

            // Mutate the field directly on the PERSISTENT entity.
            // Hibernate is watching this object — it detects the price change.
            // WHY no session.update() call: dirty checking compares the current
            // field values to the snapshot at tx.commit(). Only changed columns
            // appear in the UPDATE clause — this is more efficient than a
            // full-row UPDATE.
            item.setPrice(newPrice);

            // commit() flushes: Hibernate detects price changed → generates
            //   UPDATE items SET price=? WHERE id=?
            // name is NOT in the UPDATE because it did not change.
            tx.commit();
        }
        // session.close() → item transitions to DETACHED
    }

    /**
     * Load an item (PERSISTENT), close the session (DETACHED), modify the
     * detached entity, then re-attach it to a new session via merge().
     *
     * <p>This pattern mirrors real web applications: load in request 1,
     * send to client (now detached), receive modifications in request 2,
     * merge back into a new session.</p>
     *
     * @param sf      SessionFactory to open sessions from
     * @param id      ID of the item to modify
     * @param newName the new name to apply
     * @return the re-attached managed entity returned by merge()
     */
    static Item detachAndReattach(SessionFactory sf, Long id, String newName) {
        Item detached;

        // --- Session 1: load and immediately detach ---
        try (Session session1 = sf.openSession()) {
            Transaction tx = session1.beginTransaction();

            // find() → PERSISTENT in session1
            detached = session1.find(Item.class, id);
            System.out.println("Session 1 closed → item DETACHED");

            tx.commit();
        }
        // session1.close() → detached transitions to DETACHED state.
        // Hibernate can no longer track changes to this object.

        // Modify the detached entity.
        // WHY this is invisible to Hibernate: the entity is no longer associated
        // with any Session. There is no snapshot to compare against.
        // The change exists only in the Java object's heap memory.
        detached.setName(newName);
        System.out.println("item.setName(\"" + newName + "\") → Hibernate cannot see this change");

        // --- Session 2: merge detached entity back ---
        try (Session session2 = sf.openSession()) {
            Transaction tx = session2.beginTransaction();

            // merge() workflow:
            //   1. Hibernate issues SELECT items WHERE id=? to get current DB state
            //   2. Copies detached entity's field values onto the loaded persistent copy
            //   3. Schedules UPDATE for any fields that differ from the DB state
            //   4. Returns the PERSISTENT copy (NOT the detached argument)
            //
            // WHY merge returns a new reference: the original detached object is
            // NOT re-attached. The return value is the managed persistent entity.
            // Always use the return value — do not keep using the detached reference.
            Item managed = session2.merge(detached);
            System.out.println("session2.merge(item) → SELECT then UPDATE");

            tx.commit(); // UPDATE items SET name=? WHERE id=?

            return managed;
        }
    }

    /**
     * Delete an item by ID.
     *
     * <p>remove() requires a PERSISTENT entity — you must load it first.
     * If the item does not exist, handle gracefully rather than throwing.</p>
     *
     * @param sf the SessionFactory to open sessions from
     * @param id ID of the item to delete
     */
    static void deleteItem(SessionFactory sf, Long id) {
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();

            // find() returns null if no row with that id exists — does NOT throw.
            // WHY use find() and not get(): in Hibernate 6, get() is deprecated;
            // find() is the JPA-standard method and returns null (not a proxy).
            Item item = session.find(Item.class, id);

            // Guard: handle gracefully if the item was already deleted or never existed.
            if (item == null) {
                System.out.println("deleteItem: item with id=" + id
                        + " not found — nothing to delete");
                tx.rollback();
                return;
            }

            // remove() transitions item to REMOVED state.
            // The DELETE SQL is queued, not fired yet.
            // WHY not fired immediately: Hibernate may batch this with other
            // operations for efficiency. The DELETE fires at tx.commit().
            session.remove(item);

            tx.commit(); // DELETE items WHERE id=?
            System.out.println("Deleted item id=" + id);
        }
    }

    // =========================================================================
    // Pre-built SessionFactory builder and main() — identical to exercise
    // =========================================================================

    /**
     * Builds an H2 in-memory SessionFactory for the Item entity.
     *
     * @return configured SessionFactory
     */
    static SessionFactory buildSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.url",
                        "jdbc:h2:mem:lifecycle;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.dialect",
                        "org.hibernate.dialect.H2Dialect")
                // create-drop generates DDL from @Entity/@Table/@Column on startup
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                // show_sql lets you observe each INSERT, SELECT, UPDATE, DELETE
                .applySetting("hibernate.show_sql", "true")
                .build();

        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Item.class);
        return sources.buildMetadata().buildSessionFactory();
    }

    /**
     * Entry point — calls all four lifecycle methods and prints results.
     *
     * <p>Expected output:
     * <pre>
     * === Entity Lifecycle Solution ===
     * --- createItem: "Widget" at $9.99 ---
     * Created item with id: 1
     *
     * --- updatePrice: set price to $14.99 (dirty checking) ---
     * No explicit save() needed — Hibernate detects field change at commit
     * Verified in DB: Item{id=1, name='Widget', price=14.99}
     *
     * --- detachAndReattach: rename to "Super Widget" ---
     * Session 1 closed → item DETACHED
     * item.setName("Super Widget") → Hibernate cannot see this change
     * session2.merge(item) → SELECT then UPDATE
     * Re-attached item name: Super Widget
     *
     * --- deleteItem: remove id=1 ---
     * Deleted item id=1
     * After delete, find() returns: null (null = successfully deleted)
     * </pre>
     * </p>
     *
     * @param args unused
     */
    public static void main(String[] args) {
        try (SessionFactory sf = buildSessionFactory()) {

            System.out.println("=== Entity Lifecycle Solution ===\n");

            // --- createItem ---
            System.out.println("--- createItem: \"Widget\" at $9.99 ---");
            Long id = createItem(sf, "Widget", 9.99);
            System.out.println("Created item with id: " + id);

            // --- updatePrice (dirty checking) ---
            System.out.println("\n--- updatePrice: set price to $14.99 (dirty checking) ---");
            updatePrice(sf, id, 14.99);
            System.out.println(
                    "No explicit save() needed — Hibernate detects field change at commit");

            // Reload to verify the UPDATE was persisted
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

            // Confirm the row is gone
            try (var session = sf.openSession()) {
                Item gone = session.find(Item.class, id);
                System.out.println("After delete, find() returns: " + gone
                        + " (null = successfully deleted)");
            }

        } catch (Exception e) {
            System.err.println("Solution failed unexpectedly: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Calling session.update() (deprecated, removed in Hibernate 6)
 *   WRONG: session.update(item);
 *   PROBLEM: session.update() was a Hibernate-proprietary method that does not
 *            exist in JPA. It was removed in Hibernate 6. Code that calls it
 *            will fail at runtime with NoSuchMethodError.
 *   RIGHT: Use dirty checking (no explicit call needed for PERSISTENT entities)
 *          or session.merge() for DETACHED entities.
 *
 * MISTAKE 2: Calling merge() on a never-persisted entity
 *   WRONG: Item brand_new = new Item("X", 1.0);
 *          session.merge(brand_new); // entity has no id
 *   PROBLEM: merge() issues a SELECT first. With a null id, it finds nothing,
 *            then INSERTs — creating a duplicate instead of updating.
 *            merge() is for DETACHED entities (those that were previously persisted).
 *   RIGHT: Use persist() for brand-new entities; merge() only for detached ones.
 *
 * MISTAKE 3: Not calling tx.rollback() in a catch block
 *   WRONG: } catch (Exception e) { throw e; } // transaction left open
 *   PROBLEM: An uncommitted transaction holds database row locks. If you do not
 *            roll back, other transactions block waiting for those locks until
 *            the connection times out — usually 30 seconds or more.
 *   RIGHT:
 *     Transaction tx = session.beginTransaction();
 *     try {
 *         // ... work ...
 *         tx.commit();
 *     } catch (Exception e) {
 *         tx.rollback(); // always release locks on failure
 *         throw e;
 *     }
 *
 * MISTAKE 4: Ignoring the return value of merge()
 *   WRONG: session.merge(detachedItem);        // return value discarded
 *          System.out.println(detachedItem.getName()); // still reading detached
 *   PROBLEM: merge() returns a NEW persistent entity. The argument is NOT
 *            re-attached. Any further reads or writes must use the return value.
 *   RIGHT: Item managed = session.merge(detachedItem);
 *          System.out.println(managed.getName());
 *
 * MISTAKE 5: Accessing a removed entity after session.remove() but before commit
 *   WRONG: session.remove(item);
 *          System.out.println(item.getName()); // ok in memory
 *          Item same = session.find(Item.class, item.getId()); // null — already removed
 *   PROBLEM: After remove(), the entity is in REMOVED state. find() within the
 *            same session returns null because Hibernate knows it is pending
 *            deletion. Attempting to re-persist it causes
 *            EntityExistsException or inconsistent state.
 *   RIGHT: Do not use an entity after calling remove() on it.
 * ═══════════════════════════════════════════════════════════════════ */
