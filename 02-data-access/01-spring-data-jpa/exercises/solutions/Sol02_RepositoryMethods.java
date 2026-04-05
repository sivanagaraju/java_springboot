/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_RepositoryMethods.java                           ║
 * ║  MODULE : 02-data-access / 01-spring-data-jpa / exercises/solutions ║
 * ║  GRADLE : ./gradlew :02-data-access:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : SOLUTION — Spring Data JPA derived queries    ║
 * ║  DIFFICULTY     : Intermediate                                   ║
 * ║  PYTHON COMPARE : crud.py functions → Spring method name = SQL  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Derived Query Method Name Parsing               ║
 * ║                                                                  ║
 * ║   findByGenreAndAvailableTrue                                    ║
 * ║       │                                                          ║
 * ║       ├── findBy     → SELECT ... FROM books WHERE              ║
 * ║       ├── Genre      → genre = ?                                ║
 * ║       ├── And        → AND                                      ║
 * ║       └── AvailableTrue → available = true                      ║
 * ║                                                                  ║
 * ║   Spring Data parses the method name at startup and generates   ║
 * ║   a HQL query — zero SQL written by the developer.             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

package solutions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Sol01_Book} entities.
 *
 * <p>Demonstrates three query styles Spring Data supports:
 * <ol>
 *   <li>Inherited CRUD methods from JpaRepository (save, findAll, deleteById...)</li>
 *   <li>Derived query methods — method name is parsed to generate SQL</li>
 *   <li>Custom @Query methods — explicit JPQL when derived names become too long</li>
 * </ol>
 *
 * <p><b>Python comparison — each method here replaces a crud.py function:</b>
 * <pre>
 *   # Python: 5 lines per query
 *   def find_by_author(db, author):
 *       return db.query(Book).filter(Book.author == author).all()
 *
 *   // Java: 1 line per query
 *   List&lt;Book&gt; findByAuthor(String author);
 * </pre>
 *
 * <p><b>ASCII — Repository layer in the Spring stack:</b>
 * <pre>
 *   Controller → Service → Repository → JPA → Hibernate → DB
 *                              ↑
 *                      This interface only —
 *                      Spring generates the impl
 * </pre>
 */
public interface Sol02_BookRepository extends JpaRepository<Sol01_Book, Long> {

    /**
     * Find a book by its ISBN (unique lookup returns Optional).
     *
     * <p>Generated SQL: {@code SELECT * FROM books WHERE isbn = ?}
     * <p>WHY Optional? ISBN is unique — either one book exists or none.
     * Optional forces the caller to handle the absent case explicitly.
     *
     * @param isbn the ISBN-13 to search for
     * @return the matching book wrapped in Optional, or empty if not found
     */
    Optional<Sol01_Book> findByIsbn(String isbn);

    /**
     * Find all books by a specific author.
     *
     * <p>Generated SQL: {@code SELECT * FROM books WHERE author = ?}
     *
     * @param author the author name (exact match)
     * @return all books by the author (empty list if none)
     */
    List<Sol01_Book> findByAuthor(String author);

    /**
     * Find all available books in a specific genre.
     *
     * <p>Generated SQL: {@code SELECT * FROM books WHERE available = true AND genre = ?}
     * <p>WHY AvailableTrue (not AvailableEquals)? Spring Data supports boolean-specific
     * keywords: True/False, which generate = true / = false respectively.
     *
     * @param genre the genre to filter by
     * @return available books in the given genre
     */
    List<Sol01_Book> findByAvailableTrueAndGenre(BookGenre genre);

    /**
     * Find books with title containing a search term (case-insensitive).
     *
     * <p>Generated SQL: {@code SELECT * FROM books WHERE LOWER(title) LIKE LOWER('%?%')}
     * <p>WHY ContainingIgnoreCase? Users searching for "spring" should also find "Spring Boot".
     *
     * @param titleFragment the search term to look for in title
     * @return books whose titles contain the fragment (case-insensitive)
     */
    List<Sol01_Book> findByTitleContainingIgnoreCase(String titleFragment);

    /**
     * Find books cheaper than a given price, ordered by price ascending.
     *
     * <p>Generated SQL: {@code SELECT * FROM books WHERE price < ? ORDER BY price ASC}
     * <p>WHY LessThan and not LessThanEqual? LessThan = strictly less than ({@code <}).
     * Use LessThanEqual for ({@code <=}).
     *
     * @param maxPrice the upper price bound (exclusive)
     * @return books under the given price, cheapest first
     */
    List<Sol01_Book> findByPriceLessThanOrderByPriceAsc(BigDecimal maxPrice);

    /**
     * Find the top 10 most recently published books.
     *
     * <p>Generated SQL: {@code SELECT * FROM books ORDER BY published_year DESC LIMIT 10}
     * <p>WHY Top10? Spring Data supports Top/First keyword for limiting results
     * without needing Pageable.
     *
     * @return the 10 books with the highest publishedYear values
     */
    List<Sol01_Book> findTop10ByOrderByPublishedYearDesc();

    /**
     * Count books in a given genre.
     *
     * <p>Generated SQL: {@code SELECT COUNT(*) FROM books WHERE genre = ?}
     *
     * @param genre the genre to count
     * @return the number of books in the genre
     */
    long countByGenre(BookGenre genre);

    /**
     * Check if a book with the given ISBN already exists.
     *
     * <p>Generated SQL: {@code SELECT COUNT(*) > 0 FROM books WHERE isbn = ?}
     * <p>WHY existsBy and not findBy? existsBy stops at the first match — more efficient
     * than loading the full entity just to check existence.
     *
     * @param isbn the ISBN to check
     * @return true if any book with this ISBN exists
     */
    boolean existsByIsbn(String isbn);

    /**
     * Find books in a genre with pagination support.
     *
     * <p>Generated SQL: {@code SELECT * FROM books WHERE genre = ? LIMIT ? OFFSET ?}
     * <p>WHY Page instead of List? Page carries total count metadata — the UI needs to
     * know how many pages exist, not just the current page's rows.
     *
     * @param genre    the genre to filter by
     * @param pageable pagination and sorting specification
     * @return a Page containing the matching books and total count
     */
    Page<Sol01_Book> findByGenre(BookGenre genre, Pageable pageable);

    /**
     * Find books published between two years using custom JPQL.
     *
     * <p>WHY @Query here and not derived name? The derived name would be
     * {@code findByPublishedYearGreaterThanEqualAndPublishedYearLessThanEqual} — far too verbose.
     * Custom JPQL is cleaner for multi-range conditions.
     *
     * <p>Note: JPQL uses the Java entity class name ({@code Book}) and field names,
     * NOT the SQL table/column names.
     *
     * @param startYear start of the year range (inclusive)
     * @param endYear   end of the year range (inclusive)
     * @return books published in the given range
     */
    @Query("SELECT b FROM Sol01_Book b WHERE b.publishedYear BETWEEN :start AND :end ORDER BY b.publishedYear ASC")
    List<Sol01_Book> findByPublishedYearRange(@Param("start") int startYear, @Param("end") int endYear);

    /**
     * Mark all books by an author as unavailable using a bulk UPDATE query.
     *
     * <p>WHY @Modifying? Without it, Spring Data treats @Query as a SELECT query.
     * @Modifying is required for INSERT, UPDATE, DELETE operations.
     * <p>WHY @Transactional needed on callers? @Modifying queries require an active
     * transaction. Add @Transactional to the service method that calls this.
     *
     * @param author the author whose books to mark unavailable
     * @return the number of rows affected
     */
    @Modifying
    @Query("UPDATE Sol01_Book b SET b.available = false WHERE b.author = :author")
    int markAllBooksByAuthorUnavailable(@Param("author") String author);
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Using findBy when existsBy would suffice
 *   WRONG: findByIsbn(isbn) != null  ← loads the whole entity just to null-check
 *   CORRECT: existsByIsbn(isbn)  ← generates SELECT COUNT, stops at first match
 *
 * MISTAKE 2: Forgetting @Transactional on callers of @Modifying queries
 *   @Modifying queries need an active transaction.
 *   If the calling service method has no @Transactional, Spring throws:
 *   "Executing an update/delete query — javax.persistence.TransactionRequiredException"
 *   FIX: Add @Transactional to the service method.
 *
 * MISTAKE 3: Using EnumType.ORDINAL in the entity but filtering by enum value here
 *   If the entity uses ORDINAL and you insert a new enum value, findByGenre(MYSTERY)
 *   returns wrong results because the stored ordinal no longer maps to MYSTERY.
 *   FIX: Use @Enumerated(EnumType.STRING) in the entity.
 *
 * MISTAKE 4: Returning List when Page is needed for large datasets
 *   findByGenre(genre) without Pageable loads ALL matching rows into memory.
 *   For large tables, this causes OutOfMemoryError.
 *   FIX: Add Pageable parameter and return Page<Book> for user-facing queries.
 *
 * MISTAKE 5: Using entity property names wrong in @Query JPQL
 *   JPQL uses Java field names (camelCase), not SQL column names.
 *   WRONG: "SELECT b FROM books WHERE published_year > :year"  ← SQL table name
 *   CORRECT: "SELECT b FROM Sol01_Book b WHERE b.publishedYear > :year"  ← JPQL
 */
