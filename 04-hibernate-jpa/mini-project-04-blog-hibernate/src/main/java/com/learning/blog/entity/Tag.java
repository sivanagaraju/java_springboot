// ╔══════════════════════════════════════════════════════════════════════════════╗
// ║  FILE:    Tag.java                                                           ║
// ║  MODULE:  04-hibernate-jpa / mini-project-04-blog-hibernate                 ║
// ║  GRADLE:  com.learning.blog (subproject of java_springboot)                 ║
// ║                                                                              ║
// ║  PURPOSE: JPA/Hibernate entity for a reusable blog tag (e.g. "Java",       ║
// ║           "Spring"). Tags are shared across many Posts via a @ManyToMany    ║
// ║           association. Tag is the INVERSE side — Post owns the join table.  ║
// ║                                                                              ║
// ║  WHY IT EXISTS: Demonstrates the inverse (non-owning) side of @ManyToMany. ║
// ║                 The join table (post_tags) is owned by Post, so Hibernate   ║
// ║                 only writes to it when Post.tags changes — NOT Tag.posts.   ║
// ║                 This is a critical concept to avoid double-insert bugs.     ║
// ║                                                                              ║
// ║  PYTHON COMPARE:                                                             ║
// ║    SQLAlchemy equivalent:                                                    ║
// ║      post_tags = Table("post_tags", Base.metadata,                          ║
// ║          Column("post_id", ForeignKey("posts.id")),                         ║
// ║          Column("tag_id",  ForeignKey("tags.id")))                          ║
// ║      class Tag(Base):                                                        ║
// ║          __tablename__ = "tags"                                              ║
// ║          id    = Column(Integer, primary_key=True)                          ║
// ║          name  = Column(String(50), unique=True, nullable=False)            ║
// ║          posts = relationship("Post", secondary=post_tags,                  ║
// ║                               back_populates="tags")                        ║
// ║                                                                              ║
// ║  ASCII ARCHITECTURE:                                                         ║
// ║    ┌──────────────────────────────────────────────┐                         ║
// ║    │  BlogApp (main)                              │                         ║
// ║    │       ↓                                      │                         ║
// ║    │  BlogService  ←→  PostDao                    │                         ║
// ║    │       ↓                  ↓                   │                         ║
// ║    │  [Post]  ─── ManyToMany ──→  [Tag]           │  ← YOU ARE HERE        ║
// ║    │  (owns join table)           (inverse side)  │                         ║
// ║    └──────────────────────────────────────────────┘                         ║
// ╚══════════════════════════════════════════════════════════════════════════════╝

package com.learning.blog.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Hibernate entity for a blog tag — the inverse side of the Post ↔ Tag
 * {@code @ManyToMany} relationship.
 *
 * <p><b>Key design decision — who owns the join table:</b><br>
 * {@link Post} owns {@code post_tags} via {@code @JoinTable}. This means:
 * <ul>
 *   <li>Hibernate only INSERT/DELETEs rows in {@code post_tags} when
 *       {@code Post.tags} changes.</li>
 *   <li>Mutating {@code Tag.posts} directly does <em>not</em> persist
 *       the relationship — always use {@link Post#addTag} / {@link Post#removeTag}.</li>
 * </ul>
 *
 * <p><b>Cascade policy:</b> Tags are shared across many Posts. Deleting a Post
 * must NOT cascade to Tags (other Posts still reference them). Therefore no
 * CascadeType is set on the Tag side.
 *
 * <p><b>Python equivalent:</b> a SQLAlchemy model with a {@code secondary=}
 * association table and {@code back_populates="tags"}.
 *
 * <p><b>Position in stack:</b>
 * <pre>
 *   BlogApp → BlogService → PostDao → Post ─ManyToMany→ [Tag] ← Hibernate → H2
 * </pre>
 */
@Entity
// WHY @Table: explicit name prevents surprises on class rename; tags is clearer than "Tag"
@Table(name = "tags")
public class Tag {

    // ─────────────────────────────── FIELDS ────────────────────────────────── //

    @Id
    // WHY IDENTITY: simple auto-increment; no sequence overhead for a demo project
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    // WHY unique=true: "Java" and "java" should resolve to the same tag; uniqueness
    // enforces that at the DB level (the service layer handles find-or-create logic)
    // WHY length=50: tag names beyond 50 chars are user-experience anti-patterns
    private String name;

    @ManyToMany(mappedBy = "tags")
    // WHY mappedBy="tags": Post.tags owns the join table.
    // mappedBy tells Hibernate "the other side is authoritative — don't manage the FK
    // from here". Omitting this would cause Hibernate to try to manage post_tags from
    // both sides, leading to duplicate insert or delete-then-reinsert anomalies.
    private Set<Post> posts = new HashSet<>();
    // WHY Set not List: no ordering needed; Set prevents duplicate Post references

    // ──────────────────────────── CONSTRUCTORS ─────────────────────────────── //

    /**
     * Required no-arg constructor for Hibernate proxying.
     *
     * <p>WHY: Hibernate needs at least protected visibility to create proxy
     * subclasses for lazy-loaded associations.
     */
    protected Tag() {
        // WHY protected: JPA spec requires at minimum protected for proxy support
    }

    /**
     * Convenience constructor for creating a new Tag by name.
     *
     * <p>WHY: A Tag is entirely identified by its name; the id is database-assigned.
     * Callers create tags like {@code new Tag("Java")} and Hibernate handles the INSERT.
     *
     * @param name the tag label (e.g. "Java", "Hibernate", "JPQL"); must be unique
     */
    public Tag(String name) {
        this.name = name;
    }

    // ──────────────────────────────── GETTERS ──────────────────────────────── //

    /** @return the database-generated primary key, or null before first persist */
    public Long getId() { return id; }

    /** @return the tag label (e.g. "Java") */
    public String getName() { return name; }

    /**
     * Returns the live posts set.
     *
     * <p>WHY returns the actual set: {@link Post#addTag} and {@link Post#removeTag}
     * call {@code tag.getPosts().add(this)} to keep both sides of the bidirectional
     * relationship consistent in memory within the same Hibernate session.
     * This does NOT write to the database — only Post.tags mutations do.
     *
     * @return mutable set of Posts that carry this Tag (inverse side, in-memory only)
     */
    public Set<Post> getPosts() { return posts; }

    // ─────────────────────────────── toString ──────────────────────────────── //

    /**
     * Human-readable summary for logging.
     *
     * <p>WHY we exclude posts collection: printing it would trigger lazy-loading
     * outside a session, causing {@code LazyInitializationException}.
     *
     * @return formatted string representation
     */
    @Override
    public String toString() {
        return "Tag{id=" + id + ", name='" + name + "'}";
    }

    /**
     * Equality based on database identity.
     *
     * <p>WHY: ID-based equality is proxy-safe. Two Tag references with the same
     * ID represent the same row regardless of whether one is a Hibernate proxy.
     *
     * @param o the object to compare
     * @return true if both Tags share the same non-null id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag)) return false;
        return id != null && Objects.equals(id, tag.id);
    }

    /**
     * Hash code consistent with equals() — based on class constant.
     *
     * <p>WHY getClass().hashCode(): avoids the null-id collision for transient
     * Tags, which would all hash to 0 if we used {@code Objects.hash(id)}.
     *
     * @return stable hash code for both transient and persistent instances
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
