// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║  FILE:    Comment.java                                                       ║
// ║  MODULE:  04-hibernate-jpa / mini-project-04-blog-hibernate                 ║
// ║  GRADLE:  com.learning.blog (subproject of java_springboot)                 ║
// ║                                                                              ║
// ║  PURPOSE: JPA/Hibernate entity representing a reader comment on a blog      ║
// ║           post. Demonstrates:                                                ║
// ║             • @ManyToOne with LAZY fetch (FK lives here — owning side)      ║
// ║             • @CreationTimestamp — Hibernate sets createdAt on INSERT       ║
// ║             • cascade ALL + orphanRemoval from parent Post                  ║
// ║                                                                              ║
// ║  WHY IT EXISTS: Comment is the "many" side of the Post→Comment OneToMany.  ║
// ║                 It owns the foreign key column (post_id) which is the       ║
// ║                 canonical Hibernate pattern for bidirectional relationships. ║
// ║                                                                              ║
// ║  PYTHON COMPARE:                                                             ║
// ║    SQLAlchemy equivalent:                                                    ║
// ║      class Comment(Base):                                                    ║
// ║          __tablename__ = "comments"                                          ║
// ║          id         = Column(Integer, primary_key=True)                     ║
// ║          body       = Column(Text, nullable=False)                           ║
// ║          author     = Column(String(100), nullable=False)                   ║
// ║          post_id    = Column(Integer, ForeignKey("posts.id"), nullable=F)   ║
// ║          post       = relationship("Post", back_populates="comments")       ║
// ║                                                                              ║
// ║  ASCII ARCHITECTURE:                                                         ║
// ║    ┌──────────────────────────────────────────────┐                         ║
// ║    │  BlogApp (main)                              │                         ║
// ║    │       ↓                                      │                         ║
// ║    │  BlogService  ←→  PostDao                    │                         ║
// ║    │       ↓                  ↓                   │                         ║
// ║    │  [Post]  ─── OneToMany ──→  [Comment]        │  ← YOU ARE HERE        ║
// ║    │    │           (LAZY)            owns FK      │                         ║
// ║    │    └── ManyToMany ──→ [Tag]                  │                         ║
// ║    └──────────────────────────────────────────────┘                         ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

package com.learning.blog.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Hibernate entity for a reader comment on a blog post.
 *
 * <p>Comment is the <em>owning side</em> of the {@code Post} ↔ {@code Comment}
 * bidirectional {@code @OneToMany} / {@code @ManyToOne} relationship because it
 * holds the foreign key column ({@code post_id}). Hibernate reads the FK from
 * the owning side when generating INSERT/UPDATE statements.
 *
 * <p><b>Lifecycle:</b> Comments are created and destroyed entirely through their
 * parent {@link Post}. When a Post is deleted, Hibernate cascades the DELETE
 * (cascade=ALL + orphanRemoval=true on the Post side), so you never need to
 * delete a Comment directly.
 *
 * <p><b>Python equivalent:</b> a SQLAlchemy ORM model with a {@code ForeignKey}
 * column and a {@code relationship} with {@code back_populates="comments"}.
 *
 * <p><b>Position in stack:</b>
 * <pre>
 *   BlogApp → BlogService → PostDao → Post ─OneToMany→ [Comment] ← Hibernate → H2
 * </pre>
 */
@Entity
// WHY @Table: explicit name prevents surprises if the class is ever renamed or moved
@Table(name = "comments")
public class Comment {

    // ─────────────────────────────── FIELDS ────────────────────────────────── //

    @Id
    // WHY IDENTITY: matches H2 and PostgreSQL auto-increment; keeps the demo simple
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    // WHY TEXT: comments can be multi-paragraph; VARCHAR(255) would silently truncate
    private String body;

    @Column(nullable = false, length = 100)
    // WHY length=100: display names beyond 100 chars are UI edge cases; DB enforces it
    private String author;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    // WHY updatable=false: timestamp must never change after INSERT;
    // @CreationTimestamp writes it automatically at flush time so callers don't need to
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    // WHY LAZY: when loading a Comment we rarely need the full Post tree.
    // EAGER would join posts on every comment SELECT — wasteful when fetching many comments.
    @JoinColumn(name = "post_id", nullable = false)
    // WHY @JoinColumn: names the FK column explicitly; avoids Hibernate's default
    // "post_id" (it's the same here, but explicit naming documents intent)
    private Post post;

    // ──────────────────────────── CONSTRUCTORS ─────────────────────────────── //

    /**
     * Required no-arg constructor for Hibernate proxying.
     *
     * <p>WHY: Hibernate instantiates entities via reflection and requires at
     * least a {@code protected} no-arg constructor to create proxy subclasses.
     */
    protected Comment() {
        // WHY protected not private: JPA spec requires at least protected visibility
    }

    /**
     * Convenience constructor for creating a new comment with author and body.
     *
     * <p>The parent {@link Post} is wired separately via
     * {@link Post#addComment(Comment)} to keep both sides of the bidirectional
     * association consistent.
     *
     * <p>WHY: Excludes post and id — post is set by Post.addComment();
     * id is generated by Hibernate on INSERT.
     *
     * @param author display name of the commenter (max 100 chars)
     * @param body   the comment text
     */
    public Comment(String author, String body) {
        this.author = author;
        this.body = body;
    }

    // ──────────────────────────────── GETTERS ──────────────────────────────── //

    /** @return the database-generated primary key, or null before first persist */
    public Long getId() { return id; }

    /** @return the commenter's display name */
    public String getAuthor() { return author; }

    /** @return the full comment text */
    public String getBody() { return body; }

    /** @return the timestamp at which this Comment was first persisted */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Returns the parent Post.
     *
     * <p>WHY: Package-accessible getter used by {@link Post#addComment} and
     * {@link Post#removeComment} to maintain the inverse side of the
     * bidirectional association within the same Hibernate session.
     *
     * @return the owning Post, or null if this Comment has been detached from its Post
     */
    public Post getPost() { return post; }

    // ──────────────────────────────── SETTERS ──────────────────────────────── //

    /**
     * Sets the parent Post FK reference.
     *
     * <p>WHY package-visible: only {@link Post#addComment} and
     * {@link Post#removeComment} should set this — callers outside the entity
     * graph should never set the FK directly to avoid inconsistent state.
     *
     * @param post the owning Post; null when detaching via removeComment
     */
    void setPost(Post post) {
        // WHY package-private: enforces that Post.addComment/removeComment are the
        // only entry points, keeping both sides of the bidirectional link in sync
        this.post = post;
    }

    // ─────────────────────────────── toString ──────────────────────────────── //

    /**
     * Human-readable summary for logging, omitting the Post reference to avoid
     * triggering lazy-loading outside an active Hibernate session.
     *
     * @return formatted string representation
     */
    @Override
    public String toString() {
        return "Comment{" +
               "id=" + id +
               ", author='" + author + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }

    /**
     * Equality based on database identity.
     *
     * <p>WHY: Hibernate proxies override equals() — using all fields would
     * break equality when comparing a proxy to a real instance. ID-based
     * equality is stable across proxy and concrete instances.
     *
     * @param o the object to compare
     * @return true if both Comments share the same non-null id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return id != null && Objects.equals(id, comment.id);
    }

    /**
     * Hash code consistent with equals() — based on class constant.
     *
     * <p>WHY getClass().hashCode(): safe before and after persist;
     * avoids the null-id collision that {@code Objects.hash(id)} would cause
     * when all transient instances land in the same hash bucket.
     *
     * @return stable hash code for both transient and persistent instances
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
