# Advanced JPA

## Entity Relationships
- @OneToOne
  - Shared primary key
  - Foreign key
  - Unidirectional vs Bidirectional
- @OneToMany / @ManyToOne
  - Parent-child pattern
  - Owning side (ManyToOne)
  - Inverse side (OneToMany with mappedBy)
- @ManyToMany
  - Join table
  - @JoinTable annotation
  - Extra columns → Promote to entity
- Cascade Types
  - PERSIST
  - MERGE
  - REMOVE
  - REFRESH
  - DETACH
  - ALL
- Fetch Strategies
  - LAZY (default for collections)
  - EAGER (default for single)
  - LazyInitializationException
  - JOIN FETCH solution
  - @EntityGraph solution

## Transactions
- @Transactional Annotation
  - On service methods (not controllers)
  - Proxy-based (external calls only)
  - Self-invocation trap
- Propagation Types
  - REQUIRED (default)
  - REQUIRES_NEW
  - NESTED
  - SUPPORTS
  - NOT_SUPPORTED
  - MANDATORY
  - NEVER
- Isolation Levels
  - READ_UNCOMMITTED
  - READ_COMMITTED
  - REPEATABLE_READ
  - SERIALIZABLE
- Rollback Behavior
  - Runtime exceptions → auto rollback
  - Checked exceptions → NO rollback (default)
  - rollbackFor attribute
- Read-Only Optimization
  - readOnly = true
  - Disables dirty checking
  - Performance boost for queries

## Performance Patterns
- N+1 Query Problem
  - Detection with SQL logging
  - JOIN FETCH in JPQL
  - @EntityGraph annotation
  - @BatchSize for lazy loading
- Connection Pooling
  - HikariCP (default in Spring Boot)
  - Pool sizing
  - Connection timeout
- Second-Level Cache
  - Entity cache
  - Query cache
  - Cache providers (EhCache, Hazelcast)
