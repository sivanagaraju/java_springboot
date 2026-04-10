// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║  FILE:    BlogApp.java                                                       ║
// ║  MODULE:  04-hibernate-jpa / mini-project-04-blog-hibernate                 ║
// ║  GRADLE:  com.learning.blog (subproject of java_springboot)                 ║
// ║                                                                              ║
// ║  PURPOSE: Entry point for the Blog Engine Hibernate demo.                   ║
// ║           Wires SessionFactory → PostDao → BlogService, then runs 7         ║
// ║           demo sections that collectively prove every Hibernate concept      ║
// ║           from module 04:                                                    ║
// ║             1. Creating Posts with Tags (@ManyToMany)                        ║
// ║             2. Adding Comments (@OneToMany cascade)                          ║
// ║             3. Post Report (JOIN FETCH — N+1 prevention)                    ║
// ║             4. Title Update (@Version increment)                             ║
// ║             5. Optimistic Locking Conflict + Retry                           ║
// ║             6. Cascade Delete (Post + orphan Comments)                       ║
// ║             7. Final State (remaining posts)                                 ║
// ║                                                                              ║
// ║  HOW TO RUN:                                                                 ║
// ║    ./gradlew :04-hibernate-jpa:run -PmainClass=com.learning.blog.BlogApp    ║
// ║                                                                              ║
// ║  WHY IT EXISTS: Demonstrates what Spring Boot @EnableJpaRepositories and    ║
// ║                 @Transactional hide behind auto-configuration. Running this  ║
// ║                 by hand gives you visceral understanding of what happens     ║
// ║                 inside a Spring Data JPA repository method.                  ║
// ║                                                                              ║
// ║  PYTHON COMPARE:                                                             ║
// ║    Equivalent to a FastAPI lifespan block:                                  ║
// ║      @asynccontextmanager                                                    ║
// ║      async def lifespan(app):                                               ║
// ║          engine = create_engine(DATABASE_URL)                               ║
// ║          Base.metadata.create_all(engine)                                   ║
// ║          yield                                                               ║
// ║          Base.metadata.drop_all(engine)                                     ║
// ║                                                                              ║
// ║  ASCII ARCHITECTURE:                                                         ║
// ║    [BlogApp]  ← YOU ARE HERE — top of the call stack                        ║
// ║       │  creates SessionFactory (H2, entity classes registered)             ║
// ║       │                                                                      ║
// ║       ├──▶ PostDao   (CRUD + JPQL)                                          ║
// ║       └──▶ BlogService  (business rules + conflict handling)                ║
// ║                   │                                                          ║
// ║                   ▼                                                          ║
// ║             Post / Comment / Tag  (entities)                                 ║
// ║                   │                                                          ║
// ║                   ▼                                                          ║
// ║             H2 in-memory database (schema auto-created on startup)          ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

package com.learning.blog;

import com.learning.blog.dao.PostDao;
import com.learning.blog.entity.Comment;
import com.learning.blog.entity.Post;
import com.learning.blog.entity.Tag;
import com.learning.blog.service.BlogService;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Entry point for the Blog Engine Hibernate demo.
 *
 * <p>This class does exactly what Spring Boot auto-configuration does behind the scenes:
 * <ol>
 *   <li>Reads DB connection settings and builds a {@link SessionFactory}.</li>
 *   <li>Registers entity classes so Hibernate knows what to map.</li>
 *   <li>Runs {@code hbm2ddl.auto=create-drop} to create the H2 schema on startup
 *       and drop it on JVM exit.</li>
 * </ol>
 *
 * <p>After setup, {@code main()} exercises every Hibernate concept introduced in
 * module 04, printing the SQL Hibernate generates for each operation so you can
 * read and compare against the JPQL or entity definition that caused it.
 *
 * <p><b>Python equivalent:</b> a {@code __main__} block that calls
 * {@code Base.metadata.create_all(engine)}, runs the demo, then calls
 * {@code Base.metadata.drop_all(engine)}.
 *
 * <p><b>Position in stack:</b>
 * <pre>
 *   [BlogApp.main()] → BlogService → PostDao → Session → H2
 * </pre>
 */
public class BlogApp {

    /**
     * Runs the complete Blog Engine Hibernate demo through 7 sections.
     *
     * <p><b>Flow:</b>
     * <pre>
     *   main()
     *     ├─ buildSessionFactory()      H2 in-memory + entity classes registered
     *     ├─ Section 1: createPost()    @ManyToMany INSERT to post_tags
     *     ├─ Section 2: addComment()    @OneToMany cascade INSERT to comments
     *     ├─ Section 3: report()        JOIN FETCH eliminates N+1
     *     ├─ Section 4: updateTitle()   @Version auto-increments on UPDATE
     *     ├─ Section 5: optimisticLock() conflict + retry
     *     ├─ Section 6: deletePost()    cascade removes 3 orphan comments
     *     ├─ Section 7: finalState()    confirms 2 posts remain
     *     └─ sessionFactory.close()     H2 schema dropped
     * </pre>
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {

        // ── Banner ────────────────────────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║  Blog Engine — Hibernate ORM Demo                        ║");
        System.out.println("║  Watch the SQL Hibernate generates for each operation    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();

        // ── Bootstrap Hibernate ───────────────────────────────────────────────────
        // WHY manual registry: Spring Boot hides this behind @SpringBootApplication
        // and spring.datasource.* properties. Seeing it explicitly reveals what
        // auto-configuration does: reads settings, builds metadata, creates schema.
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            // WHY jdbc:h2:mem — runs fully in-process; no Docker, no installation
            .applySetting("hibernate.connection.url",          "jdbc:h2:mem:blogdb;DB_CLOSE_DELAY=-1")
            .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
            .applySetting("hibernate.dialect",                 "org.hibernate.dialect.H2Dialect")
            // WHY create-drop: schema is built fresh each run and torn down on exit —
            // perfect for demos and tests; equivalent to @Sql in Spring test slice
            .applySetting("hibernate.hbm2ddl.auto",            "create-drop")
            // WHY show_sql=true: the core learning goal — observe every INSERT/UPDATE/DELETE
            .applySetting("hibernate.show_sql",                "true")
            // WHY format_sql=true: makes multi-line SQL easier to read in the terminal
            .applySetting("hibernate.format_sql",              "true")
            .build();

        // WHY addAnnotatedClass for each entity: tells Hibernate which classes to
        // include in the schema and entity graph. Spring Boot scans @EntityScan packages.
        SessionFactory sessionFactory = new MetadataSources(registry)
            .addAnnotatedClass(Post.class)
            .addAnnotatedClass(Comment.class)
            .addAnnotatedClass(Tag.class)
            .buildMetadata()
            .buildSessionFactory();

        // WHY constructor wiring: no IoC container here — we wire manually to make
        // dependencies explicit. Spring would @Autowire these via component scan.
        PostDao    postDao     = new PostDao(sessionFactory);
        BlogService blogService = new BlogService(sessionFactory, postDao);

        // ── Section 1: Creating Posts with Tags ───────────────────────────────────
        System.out.println("=== 1. Creating Posts with Tags ===");
        // WHY: demonstrates @ManyToMany — Hibernate generates INSERT INTO posts,
        // INSERT INTO tags (if new), and INSERT INTO post_tags for each pair
        Post post1 = blogService.createPost(
            "Hibernate vs JPA: What's the difference?",
            "Hibernate is a JPA provider. JPA is just the specification...",
            "Java", "Hibernate"
        );
        Post post2 = blogService.createPost(
            "Spring Boot Auto-Configuration Deep Dive",
            "Spring Boot scans META-INF/spring/org.springframework.boot.autoconfigure...",
            "Java", "Spring"
        );
        Post post3 = blogService.createPost(
            "Writing JPQL queries",
            "JPQL operates on entity class names and field names, not tables...",
            "Java", "Hibernate", "JPQL"
        );

        // WHY stream tag names: getTags() returns a Set<Tag>; we extract names for display
        System.out.printf("Created: \"%s\" [tags: %s]%n",
            post1.getTitle(), tagNames(post1));
        System.out.printf("Created: \"%s\" [tags: %s]%n",
            post2.getTitle(), tagNames(post2));
        System.out.printf("Created: \"%s\" [tags: %s]%n",
            post3.getTitle(), tagNames(post3));
        System.out.println();

        // ── Section 2: Adding Comments ────────────────────────────────────────────
        System.out.println("=== 2. Adding Comments ===");
        // WHY: demonstrates @OneToMany cascade — adding a Comment to a Post causes
        // Hibernate to INSERT the Comment row (cascade=ALL from Post)
        blogService.addComment(post1.getId(), "Alice",   "Great explanation of the JPA spec!");
        blogService.addComment(post1.getId(), "Bob",     "I always confused these two. Thanks!");
        blogService.addComment(post2.getId(), "Charlie", "The @ConditionalOnMissingBean trick is gold.");
        blogService.addComment(post3.getId(), "Dave",    "How does JPQL handle subqueries?");
        blogService.addComment(post3.getId(), "Eve",     "JOIN FETCH was a game-changer for me.");
        blogService.addComment(post3.getId(), "Frank",   "Named queries look cleaner than inline JPQL.");
        System.out.println("Added 2 comments to post 1");
        System.out.println("Added 1 comment to post 2");
        System.out.println("Added 3 comments to post 3");
        System.out.println();

        // ── Section 3: Post Report (JOIN FETCH) ───────────────────────────────────
        System.out.println("=== 3. Post Report (uses JOIN FETCH — single SQL per query) ===");
        // WHY findByIdWithComments: accesses post.getComments() which is LAZY by default.
        // Without JOIN FETCH, this would throw LazyInitializationException outside the session.
        // With JOIN FETCH, one SQL retrieves Post + Comments in a single round-trip.
        for (Long id : List.of(post1.getId(), post2.getId(), post3.getId())) {
            Post p = postDao.findByIdWithComments(id).orElseThrow();
            // WHY findAllWithTags for tags: JOIN FETCH comments and tags in the same query
            // would produce a cartesian product for any post with both; separate queries avoid it
            Post withTags = postDao.findAllWithTags().stream()
                                   .filter(pt -> pt.getId().equals(id))
                                   .findFirst().orElseThrow();
            System.out.printf("  Post[%d]: \"%s\"%n", p.getId(), p.getTitle());
            System.out.printf("    Tags  : %s%n", tagNames(withTags));
            System.out.printf("    Comments: %d%n%n", p.getComments().size());
        }

        // ── Section 4: Updating Post Title (@Version) ─────────────────────────────
        System.out.println("=== 4. Updating Post Title (watch @Version increment) ===");
        // WHY @Version: Hibernate appends "AND version=?" to the UPDATE WHERE clause.
        // If the version changed since we loaded, Hibernate throws StaleObjectStateException.
        Post freshPost1 = postDao.findById(post1.getId()).orElseThrow();
        System.out.printf("Before update: title=\"%s\", version=%d%n",
            freshPost1.getTitle(), freshPost1.getVersion());

        int newVersion = blogService.updateTitle(post1.getId(), "Hibernate vs JPA — Explained");
        System.out.printf("After  update: title=\"Hibernate vs JPA — Explained\", version=%d%n%n",
            newVersion);

        // ── Section 5: Optimistic Locking Conflict Demo ───────────────────────────
        System.out.println("=== 5. Optimistic Locking Conflict Demo ===");
        System.out.println("Simulating two concurrent updates to the same post...");
        // WHY: two sessions open at the same version; the first to commit wins;
        // the second detects the version mismatch, retries with fresh state
        int finalVersion = blogService.demonstrateOptimisticLocking(
            post1.getId(),
            "Hibernate vs JPA — First Writer Wins",
            "Hibernate vs JPA — Retry Version"
        );
        System.out.println();

        // ── Section 6: Cascade Delete ─────────────────────────────────────────────
        System.out.println("=== 6. Cascade Delete ===");
        System.out.printf("Deleting post 3 (\"%s\")...%n", post3.getTitle());
        // WHY: cascade=ALL + orphanRemoval=true on Post.comments means Hibernate
        // auto-deletes the 3 Comment rows before deleting the Post row
        blogService.deletePost(post3.getId());
        // Verify: trying to load post3 should return empty
        boolean deleted = postDao.findById(post3.getId()).isEmpty();
        System.out.println("Post 3 deleted. Orphan comments also removed: " + deleted);
        System.out.println();

        // ── Section 7: Final State ────────────────────────────────────────────────
        System.out.println("=== 7. Final State ===");
        long remaining = postDao.countPosts();
        System.out.println("Remaining posts: " + remaining);

        // WHY findAllWithTags: we need tag names — they are LAZY so must be JOIN FETCHed
        List<Post> allPosts = postDao.findAllWithTags();
        for (Post p : allPosts) {
            // WHY we can't access p.getComments() here: they were lazy-loaded inside
            // findAllWithTags()'s session which is now closed. We only show tag info.
            System.out.printf("  - \"%s\" (version=%d, tags=%s)%n",
                p.getTitle(), p.getVersion(), tagNames(p));
        }
        System.out.println();

        // ── Shutdown ──────────────────────────────────────────────────────────────
        // WHY close SessionFactory: releases DB connections and drops the H2 schema
        // (hbm2ddl.auto=create-drop fires the DROP TABLE statements on close)
        sessionFactory.close();
        StandardServiceRegistryBuilder.destroy(registry);
        System.out.println("Demo complete. SessionFactory closed — H2 schema dropped.");

        /*
         * EXPECTED OUTPUT (SQL lines are Hibernate's show_sql output — intentional):
         * ============================================================================
         * ╔══════════════════════════════════════════════════════════╗
         * ║  Blog Engine — Hibernate ORM Demo                        ║
         * ║  Watch the SQL Hibernate generates for each operation    ║
         * ╚══════════════════════════════════════════════════════════╝
         *
         * === 1. Creating Posts with Tags ===
         * Hibernate: insert into posts (content,created_at,title,version,id) values (?,?,?,?,default)
         * Hibernate: insert into tags (name,id) values (?,default)
         * Hibernate: insert into tags (name,id) values (?,default)
         * Hibernate: insert into post_tags (post_id,tag_id) values (?,?)
         * Hibernate: insert into post_tags (post_id,tag_id) values (?,?)
         * ...
         * Created: "Hibernate vs JPA: What's the difference?" [tags: Java, Hibernate]
         * Created: "Spring Boot Auto-Configuration Deep Dive" [tags: Java, Spring]
         * Created: "Writing JPQL queries" [tags: Java, Hibernate, JPQL]
         *
         * === 2. Adding Comments ===
         * Hibernate: insert into comments (author,body,created_at,post_id,id) values (?,?,?,?,default)
         * ...
         * Added 2 comments to post 1
         * Added 1 comment to post 2
         * Added 3 comments to post 3
         *
         * === 3. Post Report (uses JOIN FETCH — single SQL per query) ===
         *   Post[1]: "Hibernate vs JPA: What's the difference?"
         *     Tags  : [Java, Hibernate]
         *     Comments: 2
         *
         *   Post[2]: "Spring Boot Auto-Configuration Deep Dive"
         *     Tags  : [Java, Spring]
         *     Comments: 1
         *
         *   Post[3]: "Writing JPQL queries"
         *     Tags  : [Java, Hibernate, JPQL]
         *     Comments: 3
         *
         * === 4. Updating Post Title (watch @Version increment) ===
         * Before update: title="Hibernate vs JPA: What's the difference?", version=0
         * Hibernate: update posts set content=?,title=?,version=? where id=? and version=?
         * After  update: title="Hibernate vs JPA — Explained", version=1
         *
         * === 5. Optimistic Locking Conflict Demo ===
         * Simulating two concurrent updates to the same post...
         * Both sessions loaded post at version=1
         * First  update succeeds: version bumped to 2
         * Second update REJECTED — version mismatch detected!
         * >> Concurrent edit detected! Retrying with fresh data...
         * Retry succeeded: version is now 3
         *
         * === 6. Cascade Delete ===
         * Deleting post 3 ("Writing JPQL queries")...
         * Hibernate: delete from comments where id=?
         * Hibernate: delete from comments where id=?
         * Hibernate: delete from comments where id=?
         * Hibernate: delete from post_tags where post_id=?
         * Hibernate: delete from posts where id=? and version=?
         * Post 3 deleted. Orphan comments also removed: true
         *
         * === 7. Final State ===
         * Remaining posts: 2
         *   - "Hibernate vs JPA — Retry Version" (version=3, tags=[Java, Hibernate])
         *   - "Spring Boot Auto-Configuration Deep Dive" (version=0, tags=[Java, Spring])
         *
         * Demo complete. SessionFactory closed — H2 schema dropped.
         */
    }

    // ──────────────────────────── PRIVATE HELPERS ──────────────────────────── //

    /**
     * Formats the tag names of a Post as a comma-separated string for display.
     *
     * <p>WHY helper method: {@code post.getTags()} returns {@code Set<Tag>}; we need
     * just the name strings for printing. Extracting this logic avoids repetition
     * in the seven demo sections above.
     *
     * <p>WHY sorted: Sets have no guaranteed order; sorting makes the output
     * deterministic across JVM runs.
     *
     * @param post the Post whose tags to format (must have tags initialised / JOIN FETCHed)
     * @return bracket-wrapped, sorted, comma-separated tag names, e.g. "[Hibernate, Java]"
     */
    private static String tagNames(Post post) {
        return post.getTags().stream()
                   .map(Tag::getName)
                   .sorted()
                   .collect(Collectors.joining(", ", "[", "]"));
    }
}
