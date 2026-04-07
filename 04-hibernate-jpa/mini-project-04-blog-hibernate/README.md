# Mini-Project 04 — Blog Engine with Hibernate ORM

A hands-on blog engine that demonstrates every core Hibernate concept from
module 04 using **pure Hibernate ORM** (no Spring Boot — just raw SessionFactory).
The database is **H2 in-memory**, so there is nothing to install or configure.

---

## What This Project Demonstrates

| Hibernate Concept | Where You See It |
|---|---|
| `@OneToMany` / `@ManyToOne` | `Post` → `Comment` (one post, many comments) |
| `@ManyToMany` | `Post` ↔ `Tag` (posts share tags; tags reused across posts) |
| `@Version` optimistic locking | `Post.version` increments on every `UPDATE` |
| `cascade = ALL` + `orphanRemoval` | Deleting a post automatically deletes its comments |
| `fetch = LAZY` | Comments and tags are **not** loaded until explicitly accessed |
| `JOIN FETCH` JPQL | `findByIdWithComments()` issues one SQL JOIN — kills the N+1 |
| `@CreationTimestamp` | `createdAt` is set by Hibernate on first `INSERT` |
| `@BatchSize` | Controls how many lazy-loaded items are fetched per round-trip |
| Entity lifecycle | `TRANSIENT → PERSISTENT → DETACHED` demonstrated in `BlogApp` |
| `hbm2ddl.auto=create-drop` | Schema is created on startup, dropped on JVM exit |

---

## Prerequisites

- **Java 21** (check: `java -version`)
- **No Docker needed** — H2 runs in-memory inside the JVM
- **No database setup** — Hibernate creates all tables automatically

---

## How to Run

From the repository root:

```bash
# Run the full blog demo (recommended starting point)
./gradlew :04-hibernate-jpa:run -PmainClass=com.learning.blog.BlogApp

# Run with verbose Hibernate SQL output (already enabled in BlogApp)
./gradlew :04-hibernate-jpa:run -PmainClass=com.learning.blog.BlogApp --info
```

---

## Project Structure

```
mini-project-04-blog-hibernate/
├── README.md                          ← You are here
├── ARCHITECTURE.md                    ← C4 + ERD diagrams
└── src/main/java/com/learning/blog/
    ├── BlogApp.java                   ← Entry point — wires everything together
    ├── entity/
    │   ├── Post.java                  ← @Entity with @Version, @OneToMany, @ManyToMany
    │   ├── Comment.java               ← @Entity with @ManyToOne(fetch=LAZY)
    │   └── Tag.java                   ← @Entity shared across posts via @ManyToMany
    ├── dao/
    │   └── PostDao.java               ← Data access — JPQL, JOIN FETCH, CRUD
    └── service/
        └── BlogService.java           ← Business logic — creates posts, handles conflicts
```

---

## Expected Output

When you run `BlogApp`, you will see output similar to the following. The exact
SQL lines come from Hibernate's `show_sql=true` setting and are intentional —
read them to understand what Hibernate generates.

```
╔══════════════════════════════════════════════════════════╗
║  Blog Engine — Hibernate ORM Demo                        ║
║  Watch the SQL Hibernate generates for each operation    ║
╚══════════════════════════════════════════════════════════╝

=== 1. Creating Posts with Tags ===
[Hibernate SQL: INSERT INTO posts ...]
[Hibernate SQL: INSERT INTO tags ...]
[Hibernate SQL: INSERT INTO post_tags ...]
Created: "Hibernate vs JPA: What's the difference?" [tags: Java, Hibernate]
Created: "Spring Boot Auto-Configuration Deep Dive" [tags: Java, Spring]
Created: "Writing JPQL queries" [tags: Java, Hibernate, JPQL]

=== 2. Adding Comments ===
[Hibernate SQL: INSERT INTO comments ...]
Added 2 comments to post 1
Added 1 comment to post 2
Added 3 comments to post 3

=== 3. Post Report (uses JOIN FETCH — single SQL per query) ===
  Post[1]: "Hibernate vs JPA: What's the difference?"
    Tags  : [Java, Hibernate]
    Comments: 2

  Post[2]: "Spring Boot Auto-Configuration Deep Dive"
    Tags  : [Java, Spring]
    Comments: 1

  Post[3]: "Writing JPQL queries"
    Tags  : [Java, Hibernate, JPQL]
    Comments: 3

=== 4. Updating Post Title (watch @Version increment) ===
Before update: title="Hibernate vs JPA: What's the difference?", version=0
[Hibernate SQL: UPDATE posts SET title=?, version=1 WHERE id=? AND version=0]
After  update: title="Hibernate vs JPA — Explained", version=1

=== 5. Optimistic Locking Conflict Demo ===
Simulating two concurrent updates to the same post...
First  update succeeds: version bumped to 2
Second update REJECTED — version mismatch detected!
>> Concurrent edit detected! Retrying with fresh data...
Retry succeeded: version is now 3

=== 6. Cascade Delete ===
Deleting post 3 ("Writing JPQL queries")...
[Hibernate SQL: DELETE FROM comments WHERE id=?]   ← cascade removes 3 comments
[Hibernate SQL: DELETE FROM post_tags WHERE post_id=?]
[Hibernate SQL: DELETE FROM posts WHERE id=?]
Post 3 deleted. Orphan comments also removed: confirmed.

=== 7. Final State ===
Remaining posts: 2
  - "Hibernate vs JPA — Explained" (version=3, comments=2)
  - "Spring Boot Auto-Configuration Deep Dive" (version=0, comments=1)

Demo complete. SessionFactory closed — H2 schema dropped.
```

---

## Key Observations to Make

1. **SQL on INSERT with `@ManyToMany`** — Hibernate inserts into the join table
   `post_tags` automatically; you never write that SQL yourself.

2. **`@Version` in UPDATE** — Every UPDATE SQL includes `AND version=?` in the
   WHERE clause. If the version has changed since you loaded the entity,
   Hibernate throws `StaleObjectStateException` (caught by `BlogService`).

3. **N+1 prevention with JOIN FETCH** — `findByIdWithComments()` issues ONE SQL
   statement (`SELECT p.*, c.* FROM posts p LEFT JOIN comments c ON ...`) instead
   of 1 post query + N comment queries.

4. **Cascade DELETE** — When `BlogService.deletePost()` removes the parent `Post`,
   Hibernate first DELETEs all child `Comment` rows (because of
   `cascade=ALL, orphanRemoval=true`), then DELETEs the post. No manual cleanup.

5. **LAZY loading default** — If you access `post.getComments()` outside a session
   (after the session is closed), Hibernate throws `LazyInitializationException`.
   `findByIdWithComments()` avoids this with JOIN FETCH.

---

## Extension Exercises

1. **Add pagination** — Modify `PostDao.findAll()` to accept `page` and `size`
   parameters and use `setFirstResult()` / `setMaxResults()` on the Query.

2. **Second-level cache** — Add `@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)`
   to `Tag` and configure Ehcache as the L2 cache provider. Observe the SQL count
   drop on repeated reads.

3. **Pessimistic locking** — Add a `PostDao.findForUpdate(Long id)` that uses
   `LockMode.PESSIMISTIC_WRITE` and compare it with the existing optimistic lock.

4. **Named queries** — Move the JPQL strings in `PostDao` to `@NamedQuery`
   annotations on the `Post` entity and compare readability.

5. **Author entity** — Add an `Author` entity with a `@ManyToOne` from `Post`
   to `Author`, then write a JPQL query that fetches all posts by a given author
   with their comment counts using `GROUP BY`.

---

## Python Bridge

If you come from SQLAlchemy / FastAPI, here is how the concepts map:

| This Project | SQLAlchemy Equivalent |
|---|---|
| `@Entity` + `@Table` | `class Post(Base): __tablename__ = "posts"` |
| `SessionFactory` | `engine = create_engine(...)` |
| `session.openSession()` | `with Session(engine) as session:` |
| `tx.commit()` | `session.commit()` |
| `tx.rollback()` | `session.rollback()` |
| `@OneToMany(cascade=ALL)` | `relationship("Comment", back_populates="post", cascade="all, delete-orphan")` |
| `@ManyToMany` + `@JoinTable` | `relationship("Tag", secondary=post_tags_table)` |
| `@Version` | Manual `version` column + `WHERE version=:v` in UPDATE (SQLAlchemy has no built-in) |
| `JOIN FETCH` JPQL | `.options(joinedload(Post.comments))` in SQLAlchemy |
| `hbm2ddl.auto=create-drop` | `Base.metadata.create_all(engine)` / `drop_all()` |
| `@CreationTimestamp` | `Column(DateTime, default=datetime.utcnow)` |

The key conceptual difference: in SQLAlchemy you manage the session explicitly via
a context manager. In Hibernate the `SessionFactory` is the heavyweight singleton
(created once), and each `Session` is the lightweight unit-of-work (open/close per
operation). This maps directly to SQLAlchemy's `Engine` (heavyweight) vs
`Session` (lightweight).
