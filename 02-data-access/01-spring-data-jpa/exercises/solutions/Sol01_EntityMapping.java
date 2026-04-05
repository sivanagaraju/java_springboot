/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_EntityMapping.java                               ║
 * ║  MODULE : 02-data-access / 01-spring-data-jpa / exercises/solutions ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : SOLUTION — JPA entity with all annotations    ║
 * ║  DIFFICULTY     : Beginner                                       ║
 * ║  PYTHON COMPARE : SQLAlchemy model class with Column() defs     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — JPA Entity Annotation Stack                    ║
 * ║                                                                  ║
 * ║   @Entity  ──────────────────────► marks class as DB table      ║
 * ║   @Table(name="books") ─────────► explicit table name           ║
 * ║   @Id + @GeneratedValue ────────► primary key + auto-increment  ║
 * ║   @Column(nullable=false) ──────► NOT NULL constraint           ║
 * ║   @Enumerated(STRING) ──────────► store enum as VARCHAR         ║
 * ║   @PrePersist ──────────────────► lifecycle hook (insert only)  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

package solutions;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity mapping to the {@code books} database table.
 *
 * <p>Demonstrates all common JPA annotation patterns:
 * - Primary key with auto-generation
 * - Column constraints (nullable, unique, length, precision)
 * - Enum mapping (always STRING, never ORDINAL)
 * - @PrePersist lifecycle callback for audit timestamps
 *
 * <p><b>Python SQLAlchemy equivalent:</b>
 * <pre>
 *   class Book(Base):
 *       __tablename__ = "books"
 *       id = Column(Integer, primary_key=True, autoincrement=True)
 *       title = Column(String(300), nullable=False)
 *       ...
 * </pre>
 */
@Entity
@Table(name = "books")
public class Sol01_Book {

    /**
     * Auto-generated primary key using database IDENTITY strategy.
     *
     * <p>WHY IDENTITY over SEQUENCE? IDENTITY is simpler and supported by all databases.
     * SEQUENCE is more efficient for batch inserts (generates IDs in advance).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Book title — required, up to 300 characters.
     *
     * <p>WHY length=300? Titles can be long (legal titles, academic books).
     * Default JPA column length is 255 — always set explicitly for clarity.
     */
    @Column(nullable = false, length = 300)
    private String title;

    /** Author name — required. */
    @Column(nullable = false, length = 200)
    private String author;

    /**
     * ISBN-13 identifier — unique across all books.
     *
     * <p>WHY unique=true? Business rule: no two books can share the same ISBN.
     * JPA adds a UNIQUE constraint at the DDL level.
     */
    @Column(unique = true, length = 13)
    private String isbn;

    /**
     * Book genre — stored as a string in the database.
     *
     * <p>WHY EnumType.STRING and NOT EnumType.ORDINAL?
     * ORDINAL stores 0, 1, 2... — if you ever reorder or insert enum values,
     * all existing data becomes corrupted. STRING is stable even if you add/reorder enums.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BookGenre genre;

    /** Year of publication — optional field, no special annotation needed. */
    private Integer publishedYear;

    /**
     * Book price — uses BigDecimal for monetary precision.
     *
     * <p>WHY BigDecimal and not double? Floating-point arithmetic introduces rounding errors.
     * 0.1 + 0.2 = 0.30000000000000004 in double. BigDecimal is exact for financial data.
     * precision=10 allows up to 9,999,999.99; scale=2 means 2 decimal places.
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Whether the book is available for purchase.
     *
     * <p>Primitive boolean maps to BOOLEAN/BIT column — no annotation needed.
     * Default is true: newly added books are available by default.
     */
    private boolean available = true;

    /**
     * Timestamp when this record was created — set automatically, never updated.
     *
     * <p>WHY updatable=false? The creation timestamp should be immutable after first insert.
     * Without this, Hibernate would include it in UPDATE statements unnecessarily.
     */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Lifecycle callback — automatically sets createdAt before the first INSERT.
     *
     * <p>WHY @PrePersist and not a constructor? This hook fires even when Hibernate
     * creates the entity via its no-arg constructor (e.g., when loading from a form).
     * The constructor might not be called with all fields in some frameworks.
     */
    @PrePersist
    protected void onCreate() {
        // WHY protected (not private)? JPA providers may subclass entities and need access.
        this.createdAt = LocalDateTime.now();
    }

    /**
     * JPA requires a no-arg constructor for proxy generation.
     *
     * <p>WHY protected (not public)? Prevents external code from creating
     * an invalid entity with no fields set. Only JPA internals use this.
     */
    protected Sol01_Book() {}

    /**
     * Business constructor for creating a valid book.
     *
     * @param title  the book title (non-null)
     * @param author the author name (non-null)
     * @param isbn   the ISBN-13 identifier (optional)
     * @param genre  the book genre (optional)
     * @param price  the price (optional)
     */
    public Sol01_Book(String title, String author, String isbn, BookGenre genre, BigDecimal price) {
        this.title = Objects.requireNonNull(title, "Title must not be null");
        this.author = Objects.requireNonNull(author, "Author must not be null");
        this.isbn = isbn;
        this.genre = genre;
        this.price = price;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public BookGenre getGenre() { return genre; }
    public void setGenre(BookGenre genre) { this.genre = genre; }
    public Integer getPublishedYear() { return publishedYear; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author + "', isbn='" + isbn + "'}";
    }
}

/**
 * Book genre enumeration.
 *
 * <p>ALWAYS use {@code @Enumerated(EnumType.STRING)} on fields of this type.
 * Never use ORDINAL — adding values between existing ones corrupts all stored data.
 */
enum BookGenre {
    FICTION,
    NON_FICTION,
    SCIENCE,
    HISTORY,
    BIOGRAPHY,
    TECHNOLOGY,
    SELF_HELP,
    MYSTERY
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Using @Enumerated(EnumType.ORDINAL) (the default!)
 *   WRONG: @Enumerated  // defaults to ORDINAL
 *   WHY BAD: Stores 0,1,2... — if FICTION is at index 0 and you insert ADVENTURE at index 0,
 *            all existing FICTION rows now read as ADVENTURE. Catastrophic data corruption.
 *   FIX: ALWAYS use @Enumerated(EnumType.STRING)
 *
 * MISTAKE 2: Forgetting the JPA no-arg constructor
 *   If you ONLY have a parameterized constructor, JPA cannot instantiate the entity
 *   for proxy generation and throws InstantiationException.
 *   FIX: Always add protected Entity() {} alongside your business constructors.
 *
 * MISTAKE 3: Using float or double for monetary values
 *   WRONG: private double price;
 *   WHY BAD: 0.1 + 0.2 = 0.30000000000000004 — floating-point imprecision in financial data.
 *   FIX: Use BigDecimal with @Column(precision=10, scale=2)
 *
 * MISTAKE 4: Missing updatable=false on creation timestamps
 *   Without updatable=false, every UPDATE operation will include createdAt in the SQL.
 *   The value won't change (it's set by @PrePersist) but the unnecessary column in
 *   UPDATE statements wastes resources and confuses auditing.
 *
 * MISTAKE 5: Making the no-arg constructor public
 *   public Book() {} allows external code to create a Book with all null fields.
 *   This can enter a broken state into the database.
 *   Use protected — only JPA (and subclasses) need access.
 */
