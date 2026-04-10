// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║  FILE:    BlogService.java                                                   ║
// ║  MODULE:  04-hibernate-jpa / mini-project-04-blog-hibernate                 ║
// ║  GRADLE:  com.learning.blog (subproject of java_springboot)                 ║
// ║                                                                              ║
// ║  PURPOSE: Business-logic layer for the blog engine. Orchestrates            ║
// ║           PostDao calls and demonstrates:                                    ║
// ║             • Optimistic locking conflict simulation and retry               ║
// ║             • Cascade delete via parent Post removal                         ║
// ║             • Title update with @Version increment                           ║
// ║             • Tag find-or-create (getOrCreateTag)                           ║
// ║                                                                              ║
// ║  WHY IT EXISTS: Service layer keeps business rules out of DAO and out of    ║
// ║                 the entry-point main(). In Spring Boot this would be a      ║
// ║                 @Service bean with @Transactional on each method.           ║
// ║                                                                              ║
// ║  PYTHON COMPARE:                                                             ║
// ║    Equivalent to a FastAPI/SQLAlchemy service class:                         ║
// ║      class BlogService:                                                      ║
// ║          def create_post(self, db, title, content, *tags): ...              ║
// ║          def add_comment(self, db, post_id, author, body): ...              ║
// ║          def update_title(self, db, post_id, title): ...                    ║
// ║          def delete_post(self, db, post_id): ...                            ║
// ║                                                                              ║
// ║  ASCII ARCHITECTURE:                                                         ║
// ║    BlogApp                                                                   ║
// ║       │                                                                      ║
// ║       ▼                                                                      ║
// ║    [BlogService]  ← YOU ARE HERE                                            ║
// ║       │  business rules + conflict handling                                 ║
// ║       ▼                                                                      ║
// ║    PostDao  → Session → H2                                                  ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

package com.learning.blog.service;

import com.learning.blog.dao.PostDao;
import com.learning.blog.entity.Comment;
import com.learning.blog.entity.Post;
import com.learning.blog.entity.Tag;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.Map;

/**
 * Business-logic layer for the blog engine.
 *
 * <p>Orchestrates {@link PostDao} operations and encapsulates all domain rules:
 * tag de-duplication, optimistic-lock conflict handling, cascade-delete, and
 * title updates with {@code @Version} increment tracking.
 *
 * <p><b>Optimistic locking:</b> When two callers try to update the same Post
 * at the same time, Hibernate detects the version mismatch and throws
 * {@link StaleObjectStateException}. This service catches that exception,
 * re-loads the current state from DB, applies the intended change on top,
 * and retries — the standard <em>read-modify-write</em> retry pattern.
 *
 * <p><b>Python equivalent:</b> a FastAPI service class whose methods accept a
 * SQLAlchemy {@code Session} (or use a dependency-injected session factory)
 * and raise {@code sqlalchemy.exc.StaleDataError} on version conflicts.
 *
 * <p><b>Position in stack:</b>
 * <pre>
 *   BlogApp → [BlogService] → PostDao → Session → H2
 * </pre>
 */
public class BlogService {

    // WHY two fields: PostDao handles persistence; SessionFactory is kept here
    // for the optimistic-lock demo which needs to open two concurrent sessions
    private final PostDao postDao;
    private final SessionFactory sessionFactory;

    // WHY in-memory map: demo project; tags are stored here after first persist
    // so we can look up "Java" without hitting the DB every time (find-or-create)
    private final Map<String, Tag> tagCache = new HashMap<>();

    // ──────────────────────────── CONSTRUCTOR ──────────────────────────────── //

    /**
     * Constructs a BlogService wired to a Hibernate session factory.
     *
     * <p>WHY both parameters: PostDao wraps simple CRUD; SessionFactory is needed
     * for the advanced optimistic-lock demo that requires two separate sessions
     * operating on the same Post row simultaneously.
     *
     * @param sessionFactory the application-scoped Hibernate session factory
     * @param postDao        the data-access object for Post operations
     */
    public BlogService(SessionFactory sessionFactory, PostDao postDao) {
        this.sessionFactory = sessionFactory;
        this.postDao = postDao;
    }

    // ─────────────────────────────── WRITE ─────────────────────────────────── //

    /**
     * Creates and persists a new blog post with the given tags.
     *
     * <p>Tags are de-duplicated: if a tag named "Java" was already created in a
     * previous call, this method reuses the existing {@link Tag} entity rather than
     * creating a duplicate. This prevents the unique constraint on {@code tags.name}
     * from being violated.
     *
     * <p>WHY: In production this would be a DB query (SELECT tag WHERE name=?);
     * here we use an in-memory map to keep the demo self-contained.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   def create_post(db, title, content, *tag_names):
     *       post = Post(title=title, content=content)
     *       for name in tag_names:
     *           tag = db.query(Tag).filter(Tag.name == name).first()
     *           if not tag:
     *               tag = Tag(name=name)
     *               db.add(tag)
     *           post.tags.append(tag)
     *       db.add(post)
     *       db.commit()
     * </pre>
     *
     * @param title    the post headline
     * @param content  the body text
     * @param tagNames zero or more tag labels to associate (e.g. "Java", "Hibernate")
     * @return the persisted Post with its database-assigned ID
     */
    public Post createPost(String title, String content, String... tagNames) {
        Post post = new Post(title, content);

        for (String tagName : tagNames) {
            // WHY computeIfAbsent: creates a new Tag only if this name hasn't been
            // seen before; existing Tags are reused so Hibernate cascades a MERGE
            // rather than inserting a duplicate tag row
            Tag tag = tagCache.computeIfAbsent(tagName, Tag::new);
            post.addTag(tag);
        }

        postDao.save(post);
        return post;
    }

    /**
     * Adds a reader comment to the post with the given ID.
     *
     * <p>WHY we load Post inside a fresh session: the Post returned by
     * {@code createPost()} is detached (its session is closed). We must load
     * it fresh in a new session to get a managed instance before we can add a
     * Comment and let Hibernate cascade the INSERT.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   post = session.get(Post, post_id)
     *   comment = Comment(author=author, body=body)
     *   post.comments.append(comment)
     *   comment.post = post
     *   session.commit()
     * </pre>
     *
     * @param postId the ID of the Post to comment on
     * @param author the commenter's display name
     * @param body   the comment text
     * @return the newly persisted Comment
     */
    public Comment addComment(Long postId, String author, String body) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Post post = session.get(Post.class, postId);
                Comment comment = new Comment(author, body);
                // WHY addComment not direct list add: addComment wires both sides of
                // the bidirectional relationship (comment.post = this) in a single call
                post.addComment(comment);
                // WHY no explicit persist(comment): cascade=ALL on Post.comments means
                // Hibernate cascades persist() from Post to Comment automatically
                tx.commit();
                return comment;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Updates the title of the post with the given ID and returns the new version number.
     *
     * <p>Because {@link Post} has a {@code @Version} field, every successful UPDATE
     * increments the version column. The returned integer lets callers (e.g. BlogApp)
     * print "before" and "after" version numbers to observe the increment live.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   post = session.get(Post, post_id)
     *   post.title = new_title
     *   session.commit()   # emits UPDATE posts SET title=?, version=N+1 WHERE id=? AND version=N
     * </pre>
     *
     * @param postId   the ID of the Post to update
     * @param newTitle the replacement headline
     * @return the version number after the successful UPDATE
     */
    public int updateTitle(Long postId, String newTitle) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Post post = session.get(Post.class, postId);
                post.setTitle(newTitle);
                // WHY no explicit update call: Hibernate dirty-checks on commit.
                // The session holds a snapshot of the entity taken at load time.
                // On flush/commit it compares current state to snapshot and auto-generates UPDATE.
                tx.commit();
                return post.getVersion();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Deletes the post with the given ID, cascading to all its Comments.
     *
     * <p>Because {@code Post.comments} is configured with
     * {@code cascade=ALL, orphanRemoval=true}, Hibernate automatically DELETEs
     * all child Comments before the parent Post DELETE — no manual cleanup needed.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   post = session.get(Post, post_id)
     *   session.delete(post)   # cascade="all, delete-orphan" removes comments first
     *   session.commit()
     * </pre>
     *
     * @param postId the ID of the Post to delete
     */
    public void deletePost(Long postId) {
        postDao.delete(postId);
    }

    // ─────────────────────── OPTIMISTIC LOCK DEMO ──────────────────────────── //

    /**
     * Simulates two concurrent sessions updating the same Post to demonstrate
     * optimistic locking with automatic retry.
     *
     * <p><b>What happens step by step:</b>
     * <ol>
     *   <li>Session A loads Post at version N.</li>
     *   <li>Session B also loads Post at version N.</li>
     *   <li>Session A commits UPDATE — version becomes N+1.</li>
     *   <li>Session B tries UPDATE with {@code WHERE version = N} — row not found
     *       (it is now N+1) → Hibernate throws {@link StaleObjectStateException}.</li>
     *   <li>Service catches the exception, re-loads Post at version N+1,
     *       applies the intended change, and retries successfully → version becomes N+2.</li>
     * </ol>
     *
     * <p>WHY optimistic locking: no DB row-level lock is held between steps 1-3.
     * For read-heavy workloads (blogs) this avoids lock contention while still
     * preventing lost-update anomalies. The failure rate is low; retries are cheap.
     *
     * <p><b>Python note:</b> SQLAlchemy has no built-in optimistic locking. You would
     * manually add {@code WHERE version = :v} to your UPDATE and handle the 0-row result.
     *
     * @param postId    the ID of the Post to use in the conflict demo
     * @param titleA    title applied by the "first" (winning) concurrent update
     * @param titleB    title attempted by the "second" (losing then retrying) update
     * @return the final version number after both updates complete
     */
    public int demonstrateOptimisticLocking(Long postId, String titleA, String titleB) {
        // ── Step 1: Two sessions load the SAME post at the SAME version ──────────
        // WHY separate get() calls: each openSession() creates an isolated unit of
        // work. Both sessions read the same row and see the same version number.
        Session sessionA = sessionFactory.openSession();
        Session sessionB = sessionFactory.openSession();

        Post postInA = sessionA.get(Post.class, postId);
        Post postInB = sessionB.get(Post.class, postId);

        System.out.println("Both sessions loaded post at version=" + postInA.getVersion());

        // ── Step 2: Session A wins — commits first ────────────────────────────────
        Transaction txA = sessionA.beginTransaction();
        postInA.setTitle(titleA);
        txA.commit();   // UPDATE posts SET title=?, version=N+1 WHERE id=? AND version=N → 1 row updated
        sessionA.close();
        System.out.println("First  update succeeds: version bumped to " + postInA.getVersion());

        // ── Step 3: Session B tries to commit — version mismatch ─────────────────
        Transaction txB = sessionB.beginTransaction();
        postInB.setTitle(titleB);

        int finalVersion;
        try {
            txB.commit();   // UPDATE ... WHERE version=N → 0 rows updated → StaleObjectStateException
            sessionB.close();
            finalVersion = postInB.getVersion();
        } catch (StaleObjectStateException ex) {
            // WHY rollback: the failed transaction must be rolled back before we can
            // open a new session to retry. Leaving it open would hold DB resources.
            txB.rollback();
            sessionB.close();

            System.out.println("Second update REJECTED — version mismatch detected!");
            System.out.println(">> Concurrent edit detected! Retrying with fresh data...");

            // ── Step 4: Retry in a fresh session with the current DB state ────────
            // WHY fresh session: the old sessionB is now closed. We open a new one,
            // re-read the post at its current version (N+1), and apply titleB on top.
            finalVersion = retryTitleUpdate(postId, titleB);
            System.out.println("Retry succeeded: version is now " + finalVersion);
        }

        return finalVersion;
    }

    /**
     * Retries a title update in a fresh session after an optimistic lock conflict.
     *
     * <p>WHY private helper: isolates the retry logic so {@link #demonstrateOptimisticLocking}
     * stays readable. In production this would be a generic retry decorator / aspect.
     *
     * @param postId   the Post to update
     * @param newTitle the title to apply on the fresh state
     * @return the version number after the successful retry commit
     */
    private int retryTitleUpdate(Long postId, String newTitle) {
        // WHY new session: the previous session was closed after rollback; we must
        // load the entity fresh at its current version before attempting any write
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Post post = session.get(Post.class, postId);
                post.setTitle(newTitle);
                tx.commit();
                return post.getVersion();
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }
}
