package exercises;

import java.util.ArrayList;
import java.util.List;

// Pretend SDK Imports
@interface Entity {}
@interface Id {}
@interface ManyToOne {}
@interface JoinColumn { String name(); boolean nullable() default true; }
@interface OneToMany { String mappedBy(); }

/**
 * EXERCISE 1: Bi-Directional Relationships
 * 
 * TASK:
 * An Author can have many Books. A Book belongs to exactly one Author.
 * 
 * 1. Go to the Book class (the database owner). 
 *    Configure the "author" field to be @ManyToOne and map the foreign key to "author_id".
 * 
 * 2. Go to the Author class (the inverse).
 *    Configure the "books" list to be @OneToMany, properly setting 'mappedBy'
 *    so Hibernate doesn't accidentally create a redundant Join Table.
 */
public class Ex01_Relationships {

    @Entity
    public static class Author {
        
        @Id
        private Long id;
        private String name;

        // TODO 2: Configure the inverse mappedBy relation here
        private List<Book> books = new ArrayList<>();

        public Author() {}
    }

    @Entity
    public static class Book {
        
        @Id
        private Long id;
        private String title;

        // TODO 1: Configure the Foreign Key constraints here
        private Author author;

        public Book() {}
    }

}
