// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║  FILE:    Post.java                                                          ║
// ║  MODULE:  04-hibernate-jpa / mini-project-04-blog-hibernate                 ║
// ║  GRADLE:  com.learning.blog (subproject of java_springboot)                 ║
// ║                                                                              ║
// ║  PURPOSE: JPA/Hibernate entity representing a blog post.                    ║
// ║           Owns two relationships:                                            ║
// ║             1. OneToMany → Comment  (Post is the "one", bidirectional)      ║
// ║             2. ManyToMany ↔ Tag     (Post owns the join table)              ║
// ║           Demonstrates optimistic locking via @Version.                     ║
// ║                                                                              ║
// ║  WHY IT EXISTS: Every Hibernate blog demo needs a central aggregate root.   ║
// ║                 Post is that root — it cascades to Comments and shares Tags. ║
// ║                                                                              ║
// ║  PYTHON COMPARE:                                                             ║
// ║    SQLAlchemy equivalent:                                                    ║
// ║      class Post(Base):                                                       ║
// ║          __tablename__ = "posts"                                             ║
// ║          id       = Column(Integer, primary_key=True)                       ║
// ║          title    = Column(String(200), nullable=False)                      ║
// ║          content  = Column(Text, nullable=False)                             ║
// ║          version  = Column(Integer)  # manual optimistic lock               ║
// ║          comments = relationship("Comment", back_populates="post",          ║
// ║                                   cascade="all, delete-orphan")             ║
// ║          tags     = relationship("Tag", secondary=post_tags,                ║
// ║                                   back_populates="posts")                   ║
// ║                                                                              ║
// ║  ASCII ARCHITECTURE:                                                         ║
// ║    ┌──────────────────────────────────────────────┐                         ║
// ║    │  BlogApp (main)                              │                         ║
// ║    │       ↓                                      │                         ║
// ║    │  BlogService  ←→  PostDao                    │                         ║
// ║    │       ↓                  ↓                   │                         ║
// ║    │  [Post]  ─── OneToMany ──→  [Comment]        │  ← YOU ARE HERE        ║
// ║    │    │                                         │                         ║
// ║    │    └── ManyToMany ──→ [Tag]                  │                         ║
// ║    └──────────────────────────────────────────────┘                         ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

package com.learning.blog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Hibernate entity for a blog post — the aggregate root of this domain.
 *
 * <p>Owns the lifecycle of its {@link Comment} children (cascade ALL + orphanRemoval)
 * and co-owns a many-to-many relationship with {@link Tag}.
 *
 * <p><b>Optimistic locking:</b> the {@code version} column allows Hibernate to
 * detect concurrent updates without holding a DB-level lock — essential for
 * read-heavy blog workloads.
 *
 * <p><b>Python equivalent:</b> a SQLAlchemy ORM model class with
 * {@code relationship(..., cascade="all, delete-orphan")} for comments and
 * a secondary association table for tags.
 *
 * <p><b>Position in stack:</b>
 * <pre>
 *   BlogApp → BlogService → PostDao → [Post] ← Hibernate → H2
 * </pre>
 */
@Entity
// WHY @Table: explicit table name avoids surprises if class is renamed; H2 creates "posts" DDL
@Table(name = "posts")
public class Post {

    // ─────────────────────────────── FIELDS ────────────────────────────────── //

    @Id
    // WHY IDENTITY: H2 and PostgreSQL both support auto-increment; simpler than SEQUENCE for demos
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    // WHY length=200: titles beyond 200 chars are UX anti-patterns; DB enforces the constraint
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    // WHY TEXT: blog posts can be thousands of characters; VARCHAR(255) would truncate
    private String content;

    @Version
    // WHY @Version: optimistic locking — Hibernate adds "WHERE version = N" to every UPDATE.
    // If another session already incremented the version, Hibernate throws StaleObjectStateException.
    // This is cheaper than pessimistic DB locks for read-heavy systems (e.g., a blog).
    private Integer version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    // WHY updatable=false: created timestamp must never change after INSERT;
    // @CreationTimestamp sets it automatically at flush time so app code doesn't need to
    private LocalDateTime createdAt;

    @OneToMany(
        mappedBy = "post",                                   // WHY mappedBy: Comment.post owns the FK; Post is the inverse side
        cascade = CascadeType.ALL,                           // WHY ALL: post owns comments — persist/merge/remove all cascade
        orphanRemoval = true,                                // WHY orphanRemoval: removing from list should DELETE the DB row
        fetch = FetchType.LAZY                               // WHY LAZY: don't load all comments just because a Post was loaded
    )
    @BatchSize(size = 10)
    // WHY @BatchSize(10): when Hibernate lazy-loads comments for N posts, it batches
    // the SELECT into groups of 10 IDs instead of firing N individual queries → prevents N+1
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE}
        // WHY NOT CascadeType.REMOVE: Tags are shared across many Posts.
        // Deleting a Post must NOT delete a Tag that other Posts reference.
        // Only PERSIST and MERGE propagate so new Tags created with a Post are saved.
    )
    @JoinTable(
        name = "post_tags",                                  // WHY explicit name: clearer than Hibernate's default "Post_Tag"
        joinColumns = @JoinColumn(name = "post_id"),         // FK column referencing posts.id
        inverseJoinColumns = @JoinColumn(name = "tag_id")   // FK column referencing tags.id
    )
    @BatchSize(size = 10)
    // WHY @BatchSize: same N+1 prevention logic as comments — batch-loads tags for collections of Posts
    private Set<Tag> tags = new HashSet<>();
    // WHY Set not List: Tags have no meaningful ordering; Set prevents accidental duplicates

    // ──────────────────────────── CONSTRUCTORS ─────────────────────────────── //

    /**
     * Required no-arg constructor for Hibernate proxying.
     *
     * <p>WHY: Hibernate instantiates entities via reflection (or bytecode proxies)
     * and requires a no-arg constructor to do so. It does not need to be public.
     */
    protected Post() {
        // WHY protected not private: JPA spec requires at least protected visibility
    }

    /**
     * Convenience constructor for creating a new Post with title and content.
     *
     * <p>WHY: Avoids verbose setter chains in application code; ID and version
     * are assigned by Hibernate, so they are excluded here.
     *
     * @param title   the post headline (max 200 chars)
     * @param content the full body text
     */
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // ─────────────────────────── CONVENIENCE METHODS ───────────────────────── //

    /**
     * Adds a Comment to this Post, wiring both sides of the bidirectional association.
     *
     * <p>WHY: Hibernate requires both sides of a bidirectional relationship to be
     * consistent in memory. If you only add to Post.comments without setting
     * Comment.post, the in-memory object graph is out of sync with what Hibernate
     * will persist — leading to subtle bugs (e.g., comment.getPost() returns null
     * within the same session).
     *
     * <p>Python equivalent: {@code post.comments.append(comment); comment.post = post}
     *
     * @param comment the Comment to attach; must not be null
     */
    public void addComment(Comment comment) {
        comments.add(comment);
        // WHY: set the owning side so the FK (post_id) is populated on INSERT
        comment.setPost(this);
    }

    /**
     * Removes a Comment from this Post, clearing both sides of the bidirectional link.
     *
     * <p>WHY: orphanRemoval=true on the collection means Hibernate will issue a
     * DELETE for any Comment removed from the list at flush time. We also null out
     * comment.post to keep the in-memory graph consistent.
     *
     * @param comment the Comment to detach; no-op if not present
     */
    public void removeComment(Comment comment) {
        comments.remove(comment);
        // WHY: clear the back-reference so the detached Comment is not accidentally re-persisted
        comment.setPost(null);
    }

    /**
     * Adds a Tag to this Post, wiring both sides of the ManyToMany association.
     *
     * <p>WHY: Post owns the join table (post_tags). Adding a Tag to Post.tags causes
     * Hibernate to INSERT a row into post_tags at flush time. We also add this Post
     * to tag.posts to keep the in-memory graph consistent for the same reason as
     * the bidirectional OneToMany above.
     *
     * @param tag the Tag to associate; must not be null
     */
    public void addTag(Tag tag) {
        tags.add(tag);
        // WHY: maintain the inverse side so tag.getPosts() reflects reality within this session
        tag.getPosts().add(this);
    }

    /**
     * Removes a Tag from this Post, clearing both sides of the ManyToMany link.
     *
     * <p>WHY: Hibernate will DELETE the corresponding row from post_tags at flush
     * time (Post owns the join table). Removing from tag.posts keeps the graph consistent.
     *
     * @param tag the Tag to disassociate; no-op if not present
     */
    public void removeTag(Tag tag) {
        tags.remove(tag);
        // WHY: symmetric removal to prevent stale inverse-side state within the same session
        tag.getPosts().remove(this);
    }

    // ──────────────────────────────── GETTERS ──────────────────────────────── //

    /** @return the database-generated primary key, or null before first persist */
    public Long getId() { return id; }

    /** @return the post headline */
    public String getTitle() { return title; }

    /** @return the full body text */
    public String getContent() { return content; }

    /**
     * Returns the optimistic-lock version counter.
     *
     * <p>WHY: Callers (e.g., BlogApp demos) read this to confirm Hibernate incremented
     * the version after a successful UPDATE.
     *
     * @return current version number; 0 after first persist, increments on each UPDATE
     */
    public Integer getVersion() { return version; }

    /** @return the timestamp at which this Post was first persisted */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Returns the live comments collection.
     *
     * <p>WHY returns the actual list (not a copy): callers like BlogService use
     * {@code addComment()} for mutations; returning the live collection lets
     * Hibernate track changes via its PersistentList proxy.
     *
     * @return mutable list of associated Comments (lazy-loaded)
     */
    public List<Comment> getComments() { return comments; }

    /**
     * Returns the live tags set.
     *
     * <p>WHY returns the actual set: same reasoning as getComments() — Hibernate's
     * PersistentSet must be the object that changes are made to.
     *
     * @return mutable set of associated Tags (lazy-loaded)
     */
    public Set<Tag> getTags() { return tags; }

    // ──────────────────────────────── SETTERS ──────────────────────────────── //

    /**
     * Sets the post title.
     *
     * <p>WHY setter exists: needed by BlogService.updateTitle() to change just the
     * title of an already-persisted Post within a transaction.
     *
     * @param title new headline; should not exceed 200 characters
     */
    public void setTitle(String title) { this.title = title; }

    /** @param content new body text */
    public void setContent(String content) { this.content = content; }

    // ─────────────────────────────── toString ──────────────────────────────── //

    /**
     * Human-readable summary for logging and EXPECTED OUTPUT blocks.
     *
     * <p>WHY we exclude comments/tags collections: printing them would trigger
     * lazy-loading outside a session, causing LazyInitializationException.
     * Show only scalar fields + collection sizes.
     *
     * @return formatted string representation
     */
    @Override
    public String toString() {
        return "Post{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", version=" + version +
               ", comments=" + comments.size() +
               ", tags=" + tags.size() +
               ", createdAt=" + createdAt +
               '}';
    }

    /**
     * Equality based on database identity (id).
     *
     * <p>WHY: Hibernate entities should use database ID for equality when persisted.
     * Using all fields would break equals() across proxy objects (which Hibernate
     * creates for lazy associations). Null-safe for new (transient) entities.
     *
     * @param o the object to compare
     * @return true if both Posts have the same non-null id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post post)) return false;
        // WHY: only compare by ID; two Post objects with the same ID are the same row
        return id != null && Objects.equals(id, post.id);
    }

    /**
     * Hash code consistent with equals() — based on class, not id.
     *
     * <p>WHY return class hashCode (not id): before the entity is persisted, id is null.
     * Using {@code Objects.hash(id)} would return 0 for all transient instances,
     * causing all new Posts to collide in HashMaps/HashSets. The class constant
     * is stable across the entity lifecycle.
     *
     * @return stable hash code for both transient and persistent instances
     */
    @Override
    public int hashCode() {
        // WHY getClass().hashCode(): safe before and after persist; see Vlad Mihalcea's recommendation
        return getClass().hashCode();
    }
}
