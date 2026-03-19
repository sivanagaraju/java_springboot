# Data Access

## JPA (Jakarta Persistence API)
- Specification, not implementation
- Defines annotations and interfaces
- Hibernate is the default implementation
- Python comparison: No standard — SQLAlchemy is de facto

## Entities
- @Entity annotation
- @Table mapping
- @Id and primary keys
  - @GeneratedValue strategies
    - IDENTITY (auto-increment)
    - SEQUENCE (database sequence)
    - UUID (unique identifier)
- Field annotations
  - @Column (name, nullable, length)
  - @Temporal (dates)
  - @Enumerated (enum handling)
  - @Lob (large objects)
- Lifecycle callbacks
  - @PrePersist
  - @PreUpdate
  - @PostLoad

## Spring Data Repositories
- Repository hierarchy
  - Repository (marker)
  - CrudRepository (CRUD)
  - PagingAndSortingRepository
  - JpaRepository (JPA-specific)
- Derived Query Methods
  - findBy conventions
  - Keyword mapping
  - AND / OR / Between / Like
  - OrderBy
  - Top / First / Distinct
- Custom Queries
  - @Query with JPQL
  - @Query with native SQL
  - @Modifying for updates/deletes

## Entity Relationships
- @OneToOne
- @OneToMany / @ManyToOne
- @ManyToMany
- Cascade types
  - PERSIST, MERGE, REMOVE
  - ALL
- Fetch strategies
  - LAZY (default for collections)
  - EAGER (default for single)
- JoinColumn and mappedBy

## Transactions
- @Transactional annotation
- Propagation types
  - REQUIRED (default)
  - REQUIRES_NEW
  - NESTED
- Isolation levels
  - READ_COMMITTED
  - REPEATABLE_READ
  - SERIALIZABLE
- Rollback behavior
  - Runtime exceptions (auto)
  - Checked exceptions (manual)

## Advanced Topics
- N+1 Query Problem
  - Detection
  - JOIN FETCH solution
  - @EntityGraph solution
- Connection Pooling (HikariCP)
- Second-Level Cache
- Auditing (@CreatedDate, @LastModifiedDate)
