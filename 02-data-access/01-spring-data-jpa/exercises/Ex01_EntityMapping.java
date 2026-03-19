/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_EntityMapping.java                                ║
 * ║  MODULE : 02-data-access / 01-spring-data-jpa / exercises       ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — Create a JPA entity with proper    ║
 * ║                   annotations mapping to a database table        ║
 * ║  DIFFICULTY     : Beginner                                       ║
 * ║  PYTHON COMPARE : SQLAlchemy model class with Column() defs     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — JPA Entity → Table Mapping                      ║
 * ║                                                                   ║
 * ║    Java Object (Book)          Database Table (books)            ║
 * ║    ┌────────────────┐          ┌────────────────────┐            ║
 * ║    │ @Entity         │          │ CREATE TABLE books  │            ║
 * ║    │ Long id         │  ←───→  │ id SERIAL PK       │            ║
 * ║    │ String title    │  ←───→  │ title VARCHAR(300)  │            ║
 * ║    │ String author   │  ←───→  │ author VARCHAR(200) │            ║
 * ║    │ String isbn     │  ←───→  │ isbn VARCHAR(13) UQ │            ║
 * ║    │ BookGenre genre │  ←───→  │ genre VARCHAR(20)   │            ║
 * ║    │ BigDecimal price│  ←───→  │ price DECIMAL(10,2) │            ║
 * ║    │ boolean active  │  ←───→  │ available BOOLEAN   │            ║
 * ║    │ LocalDateTime   │  ←───→  │ created_at TIMESTAMP│            ║
 * ║    └────────────────┘          └────────────────────┘            ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : Not standalone — add to a Spring Boot app     ║
 * ║  EXPECTED OUTPUT: Check CREATE TABLE SQL in console             ║
 * ║  RELATED FILES  : 02-entities-and-annotations.md                ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * EXERCISE INSTRUCTIONS:
 * =====================
 * Create a JPA entity class called 'Book' that maps to the 'books' table.
 * Follow the TODOs below to add all required annotations and fields.
 *
 * PYTHON COMPARISON:
 * This is equivalent to creating a SQLAlchemy model:
 *
 *   class Book(Base):
 *       __tablename__ = "books"
 *       id = Column(Integer, primary_key=True, autoincrement=True)
 *       title = Column(String(300), nullable=False)
 *       author = Column(String(200), nullable=False)
 *       isbn = Column(String(13), unique=True)
 *       genre = Column(Enum(BookGenre))
 *       published_year = Column(Integer)
 *       price = Column(Numeric(10, 2))
 *       available = Column(Boolean, default=True)
 *       created_at = Column(DateTime, default=func.now())
 *
 * VERIFICATION:
 * If using spring.jpa.hibernate.ddl-auto=create, start the app and check
 * the console for CREATE TABLE SQL. All columns should match expectations.
 */

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// TODO 1: Add the @Entity annotation to mark this as a JPA entity.
// TODO 2: Add the @Table annotation to explicitly name the table "books".

public class Book {

    // TODO 3: Mark this field as the primary key with @Id.
    // TODO 4: Add @GeneratedValue with IDENTITY strategy for auto-increment.
    private Long id;

    // TODO 5: Add @Column with nullable=false and length=300.
    // This is the book title — it must not be null and can be long.
    private String title;

    // TODO 6: Add @Column with nullable=false and length=200.
    private String author;

    // TODO 7: Add @Column with unique=true and length=13.
    // ISBN-13 is always exactly 13 characters. Unique constraint prevents duplicates.
    private String isbn;

    // TODO 8: Add @Enumerated with EnumType.STRING.
    // Remember: NEVER use ORDINAL — it breaks when you reorder enum values!
    private BookGenre genre;

    // TODO 9: This field needs no special annotation — default @Column works.
    private Integer publishedYear;

    // TODO 10: Add @Column with precision=10 and scale=2 for monetary values.
    // BigDecimal is the correct type for money (never use float/double!).
    private BigDecimal price;

    // TODO 11: No annotation needed — boolean maps to BOOLEAN column by default.
    private boolean available = true;

    // TODO 12: Add @Column with updatable=false (creation timestamp shouldn't change).
    // TODO 13: Add a @PrePersist lifecycle callback method to set this field.
    private LocalDateTime createdAt;

    // TODO 14: Add a protected no-arg constructor (required by JPA).

    // TODO 15: Add a business constructor with title, author, isbn, genre, and price.

    // TODO 16: Add getters and setters for all fields.
}

/**
 * Book genre enum.
 * TODO 17: Add at least 5 genre values.
 */
enum BookGenre {
    // YOUR CODE HERE
}
