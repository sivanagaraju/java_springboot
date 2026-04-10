// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║  FILE:    PostDao.java                                                       ║
// ║  MODULE:  04-hibernate-jpa / mini-project-04-blog-hibernate                 ║
// ║  GRADLE:  com.learning.blog (subproject of java_springboot)                 ║
// ║                                                                              ║
// ║  PURPOSE: Data-access layer for the Post aggregate.                         ║
// ║           All Hibernate Session interaction is isolated here so that        ║
// ║           BlogService stays free of persistence boilerplate.                ║
// ║           Demonstrates:                                                      ║
// ║             • JPQL with JOIN FETCH (N+1 prevention)                         ║
// ║             • Simple CRUD with explicit transaction control                  ║
// ║             • Returning detached entities by closing the session early       ║
// ║                                                                              ║
// ║  WHY IT EXISTS: Separating DAO from service is the Repository pattern       ║
// ║                 (Repository in DDD terms). Spring Data JPA replaces this    ║
// ║                 boilerplate in production, but writing it by hand reveals   ║
// ║                 exactly what Spring generates under the hood.               ║
// ║                                                                              ║
// ║  PYTHON COMPARE:                                                             ║
// ║    SQLAlchemy equivalent — a plain function or class operating on Session:  ║
// ║      def find_by_id(session, post_id):                                      ║
// ║          return session.get(Post, post_id)                                  ║
// ║      def find_with_comments(session, post_id):                              ║
// ║          return session.query(Post)                                         ║
// ║                         .options(joinedload(Post.comments))                 ║
// ║                         .filter(Post.id == post_id).one()                  ║
// ║                                                                              ║
// ║  ASCII ARCHITECTURE:                                                         ║
// ║    BlogApp                                                                   ║
// ║       │                                                                      ║
// ║       ▼                                                                      ║
// ║    BlogService                                                               ║
// ║       │                                                                      ║
// ║       ▼                                                                      ║
// ║    [PostDao]  ← YOU ARE HERE                                                ║
// ║       │  uses SessionFactory (thread-safe, app-scoped singleton)            ║
// ║       ▼                                                                      ║
// ║    Session (one per operation — unit of work)                               ║
// ║       │                                                                      ║
// ║       ▼                                                                      ║
// ║    H2 in-memory database                                                    ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

package com.learning.blog.dao;

import com.learning.blog.entity.Post;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Data-access object for {@link Post} — all Hibernate Session management lives here.
 *
 * <p>Each public method opens its own {@link Session}, performs exactly one logical
 * operation, commits (or rolls back), and closes the session. This is the
 * <em>session-per-operation</em> pattern — suitable for demos and scripts. In a
 * Spring Boot application, Spring's {@code @Transactional} manages the session
 * boundary automatically so the DAO only calls {@code entityManager.persist()} etc.
 *
 * <p><b>JPQL note:</b> all queries use JPQL (Java Persistence Query Language), not
 * native SQL. JPQL operates on entity class names and field names, not table and
 * column names. Hibernate translates JPQL to the target DB dialect at runtime.
 *
 * <p><b>Python equivalent:</b> a module of functions that accept a SQLAlchemy
 * {@code Session} as their first argument and perform ORM queries on it.
 *
 * <p><b>Position in stack:</b>
 * <pre>
 *   BlogApp → BlogService → [PostDao] → Session → H2
 * </pre>
 */
public class PostDao {

    // WHY field not local: SessionFactory is thread-safe and expensive to create.
    // One instance is created at startup (in BlogApp) and shared across all DAOs.
    private final SessionFactory sessionFactory;

    // ──────────────────────────── CONSTRUCTOR ──────────────────────────────── //

    /**
     * Constructs a PostDao backed by the given Hibernate SessionFactory.
     *
     * <p>WHY constructor injection: makes the dependency explicit and testable.
     * In Spring Boot, this would be replaced by {@code @Autowired} or constructor
     * injection via Spring's IoC container.
     *
     * @param sessionFactory the application-scoped Hibernate session factory;
     *                       must not be null and must already be fully configured
     */
    public PostDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // ─────────────────────────────── WRITE ─────────────────────────────────── //

    /**
     * Persists a new (transient) Post and returns its database-assigned ID.
     *
     * <p>WHY session.persist(): JPA-standard method for transitioning an entity
     * from TRANSIENT to PERSISTENT. Hibernate's legacy {@code save()} is equivalent
     * but non-standard — always prefer {@code persist()} in new code.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   session.add(post)
     *   session.commit()
     *   return post.id
     * </pre>
     *
     * @param post a transient Post instance; tags should already be attached via Post.addTag()
     * @return the database-generated primary key assigned after INSERT
     */
    public Long save(Post post) {
        // WHY try-with-resources: Session is AutoCloseable; this guarantees close() on exception
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(post);
                // WHY flush before commit: ensures the INSERT fires and id is assigned
                // before we return — without flush, id could still be null
                session.flush();
                tx.commit();
                return post.getId();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Merges a detached Post back into a new session and commits.
     *
     * <p>WHY merge() not persist(): persist() on a detached entity (one that has an ID
     * but no active session) throws an exception. merge() copies the detached state
     * onto a new managed instance and returns that new managed instance.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   session.merge(post)
     *   session.commit()
     * </pre>
     *
     * @param post a detached Post whose fields have been modified
     * @return the new managed Post instance with the merged state
     */
    public Post update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // WHY merge returns a new managed instance:
                // The detached `post` argument is NOT made persistent — only the returned
                // object is tracked by this session. Callers should use the returned value.
                Post managed = session.merge(post);
                tx.commit();
                return managed;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Deletes the Post with the given ID, cascading to its Comments.
     *
     * <p>WHY re-load before delete: session.remove() requires a managed (PERSISTENT)
     * entity. Passing a detached Post would throw {@code IllegalArgumentException}.
     * We load it fresh in this session so it's managed, then remove it.
     *
     * <p>Because {@code Post.comments} has {@code cascade=ALL, orphanRemoval=true},
     * Hibernate automatically DELETEs all associated Comments before the Post DELETE.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   post = session.get(Post, post_id)
     *   session.delete(post)
     *   session.commit()
     * </pre>
     *
     * @param postId the primary key of the Post to delete
     */
    public void delete(Long postId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Post post = session.get(Post.class, postId);
                if (post != null) {
                    // WHY session.remove(): JPA-standard method for transitioning PERSISTENT → REMOVED
                    session.remove(post);
                }
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    // ─────────────────────────────── READ ──────────────────────────────────── //

    /**
     * Loads a Post by its primary key without eagerly loading collections.
     *
     * <p>WHY session.get(): returns null for unknown IDs (no exception). Prefer
     * this over {@code session.load()} in application code — load() returns a
     * proxy that throws when you access its fields if the row doesn't exist.
     *
     * <p>Collections (comments, tags) are LAZY — do not access them after this
     * session closes or you will get {@code LazyInitializationException}.
     * Use {@link #findByIdWithComments} when you need comments in the result.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   return session.get(Post, post_id)
     * </pre>
     *
     * @param postId the primary key to look up
     * @return Optional containing the Post, or empty if not found
     */
    public Optional<Post> findById(Long postId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Post.class, postId));
        }
    }

    /**
     * Loads a Post with its Comments eagerly fetched via a single JOIN FETCH query.
     *
     * <p><b>WHY JOIN FETCH:</b> If we used a plain {@code session.get(Post.class, id)}
     * and then accessed {@code post.getComments()} outside the session, Hibernate would
     * throw {@code LazyInitializationException} (comments are LAZY by default).
     *
     * <p>JOIN FETCH tells Hibernate to issue one SQL JOIN that retrieves both the Post
     * row and all Comment rows in a single round-trip. This is the standard N+1 fix
     * for the case where you know you'll need the collection.
     *
     * <p><b>SQL generated:</b>
     * <pre>
     *   SELECT p.*, c.*
     *   FROM posts p
     *   LEFT JOIN comments c ON c.post_id = p.id
     *   WHERE p.id = ?
     * </pre>
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   session.query(Post).options(joinedload(Post.comments)).filter(Post.id == post_id).one()
     * </pre>
     *
     * @param postId the primary key to look up
     * @return Optional containing the Post with comments initialised, or empty if not found
     */
    public Optional<Post> findByIdWithComments(Long postId) {
        try (Session session = sessionFactory.openSession()) {
            // WHY "LEFT JOIN FETCH": LEFT means posts with no comments are still returned;
            // INNER JOIN FETCH would filter out posts that have zero comments
            String jpql = "SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :id";
            Post post = session.createQuery(jpql, Post.class)
                               .setParameter("id", postId)
                               .uniqueResult();
            return Optional.ofNullable(post);
        }
    }

    /**
     * Returns all Posts with their tags eagerly JOIN FETCHed.
     *
     * <p>WHY JOIN FETCH tags here: the MINDMAP and report sections in BlogApp
     * need to print tag names. Without JOIN FETCH, accessing {@code post.getTags()}
     * outside the session would throw {@code LazyInitializationException}.
     *
     * <p><b>SQL generated:</b>
     * <pre>
     *   SELECT DISTINCT p.*, t.*
     *   FROM posts p
     *   LEFT JOIN post_tags pt ON pt.post_id = p.id
     *   LEFT JOIN tags t ON t.id = pt.tag_id
     * </pre>
     *
     * <p>WHY DISTINCT: without it, Hibernate returns a Post row for each matching
     * tag row — N copies of the same Post object in the result list.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   session.query(Post).options(joinedload(Post.tags)).distinct().all()
     * </pre>
     *
     * @return list of all Posts with their tags initialised, ordered by id ascending
     */
    public List<Post> findAllWithTags() {
        try (Session session = sessionFactory.openSession()) {
            // WHY DISTINCT in JPQL: eliminates duplicate Post objects caused by
            // the cartesian product of the JOIN between posts and post_tags rows
            String jpql = "SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.tags ORDER BY p.id ASC";
            return session.createQuery(jpql, Post.class).getResultList();
        }
    }

    /**
     * Counts the total number of posts in the database.
     *
     * <p>WHY COUNT query: used by BlogApp for the "Final State" section to confirm
     * the correct number of posts remain after the cascade-delete demo.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   session.query(func.count(Post.id)).scalar()
     * </pre>
     *
     * @return total post count
     */
    public long countPosts() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT COUNT(p) FROM Post p", Long.class)
                          .uniqueResult();
        }
    }
}
