/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex02_RepositoryMethods.java                            ║
 * ║  MODULE : 02-data-access / 01-spring-data-jpa / exercises       ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — Create repository interfaces with  ║
 * ║                   derived query methods (zero SQL!)              ║
 * ║  DIFFICULTY     : Intermediate                                   ║
 * ║  PYTHON COMPARE : crud.py functions (50+ lines each) →          ║
 * ║                   Spring method name = auto-generated SQL        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Derived Query Method Parsing                    ║
 * ║                                                                   ║
 * ║   findByGenreAndAvailableTrue                                    ║
 * ║       │                                                          ║
 * ║       ├── findBy    → SELECT * FROM books WHERE                  ║
 * ║       ├── Genre     → genre = ?                                  ║
 * ║       ├── And       → AND                                        ║
 * ║       └── AvailableTrue → available = true                       ║
 * ║                                                                   ║
 * ║   Generated SQL:                                                  ║
 * ║   SELECT * FROM books WHERE genre = ? AND available = true       ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : Not standalone — add to a Spring Boot app     ║
 * ║  EXPECTED OUTPUT: Enable spring.jpa.show-sql=true to verify     ║
 * ║  RELATED FILES  : 04-derived-query-methods.md                   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * EXERCISE INSTRUCTIONS:
 * =====================
 * Create a BookRepository interface with derived query methods and custom queries.
 * For each TODO, write the method signature that generates the described SQL.
 *
 * PYTHON COMPARISON:
 * In Python, each of these queries requires a separate function in crud.py:
 *   def find_books_by_author(db, author):
 *       return db.query(Book).filter(Book.author == author).all()
 *
 * In Spring Data JPA, you just NAME the method correctly and the SQL is generated.
 *
 * VERIFICATION:
 * Enable SQL logging in application.properties:
 *   spring.jpa.show-sql=true
 *   spring.jpa.properties.hibernate.format_sql=true
 * Then call each method and verify the generated SQL matches expectations.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Book entities.
 *
 * <p><b>ASCII — Repository method types:</b>
 * <pre>
 *   ┌─────────────────────┐
 *   │ JpaRepository<Book> │ ← 15+ inherited methods (save, findAll, delete...)
 *   ├─────────────────────┤
 *   │ Derived queries     │ ← Method name → SQL (TODOs 1–9)
 *   ├─────────────────────┤
 *   │ @Query (JPQL)       │ ← Custom queries (TODOs 10–11)
 *   └─────────────────────┘
 * </pre>
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    // ========================================================================
    // TODO 1: Find a book by its ISBN (should return Optional since ISBN is unique).
    // Expected SQL: SELECT * FROM books WHERE isbn = ?
    // Python: db.query(Book).filter(Book.isbn == isbn).first()
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 2: Find all books by a specific author.
    // Expected SQL: SELECT * FROM books WHERE author = ?
    // Python: db.query(Book).filter(Book.author == author).all()
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 3: Find all available books in a specific genre.
    // Expected SQL: SELECT * FROM books WHERE available = true AND genre = ?
    // Hint: Use 'AvailableTrue' in the method name.
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 4: Find books with title containing a search term (case-insensitive).
    // Expected SQL: SELECT * FROM books WHERE LOWER(title) LIKE LOWER('%?%')
    // Hint: Use 'ContainingIgnoreCase' keyword.
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 5: Find books cheaper than a given price, ordered by price ascending.
    // Expected SQL: SELECT * FROM books WHERE price < ? ORDER BY price ASC
    // Hint: Combine 'LessThan' and 'OrderBy...Asc' keywords.
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 6: Find the top 10 most recent books (by published year, descending).
    // Expected SQL: SELECT * FROM books ORDER BY published_year DESC LIMIT 10
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 7: Count books by genre.
    // Expected SQL: SELECT COUNT(*) FROM books WHERE genre = ?
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 8: Check if a book exists with the given ISBN.
    // Expected SQL: SELECT COUNT(*) > 0 FROM books WHERE isbn = ?
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 9: Find books by genre with pagination support.
    // Expected SQL: SELECT * FROM books WHERE genre = ? LIMIT ? OFFSET ?
    // Hint: Add Pageable as a parameter and return Page<Book>.
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 10 (BONUS): Write a custom @Query using JPQL to find books
    // published between two years.
    // JPQL: SELECT b FROM Book b WHERE b.publishedYear BETWEEN :start AND :end
    // ========================================================================

    // YOUR CODE HERE

    // ========================================================================
    // TODO 11 (BONUS): Write a @Modifying @Query to mark all books by an author
    // as unavailable (available = false).
    // JPQL: UPDATE Book b SET b.available = false WHERE b.author = :author
    // ========================================================================

    // YOUR CODE HERE
}
