# Spring Data JPA

## What is JPA
- Jakarta Persistence API
- Specification (not implementation)
- Hibernate is the default provider
- Sits above JDBC
- Python comparison: No standard — SQLAlchemy is de facto ORM

## JPA Architecture
- Entity Manager
  - Persistence Context (First-Level Cache)
  - Entity Lifecycle (NEW → MANAGED → DETACHED → REMOVED)
- EntityManagerFactory
- Persistence Unit (persistence.xml / Spring Boot auto-config)

## Entities
- @Entity annotation
- @Table (name, schema)
- @Id (primary key)
  - @GeneratedValue strategies
    - GenerationType.IDENTITY
    - GenerationType.SEQUENCE
    - GenerationType.UUID
- @Column (name, nullable, unique, length)
- @Enumerated (EnumType.STRING vs ORDINAL)
- @Temporal (DATE, TIME, TIMESTAMP)
- @Transient (not persisted)
- @Lob (large objects)
- Java records NOT supported as entities

## Repository Hierarchy
- Repository (marker interface)
- CrudRepository
  - save, findById, findAll, deleteById, count
- ListCrudRepository
- PagingAndSortingRepository
  - findAll(Pageable), findAll(Sort)
- JpaRepository
  - flush, saveAndFlush, deleteInBatch

## Derived Query Methods
- Naming conventions
  - findBy + PropertyName
  - countBy + PropertyName
  - deleteBy + PropertyName
  - existsBy + PropertyName
- Keywords
  - And, Or, Between, LessThan, GreaterThan
  - Like, Containing, StartingWith, EndingWith
  - In, NotIn, IsNull, IsNotNull
  - OrderBy + Asc/Desc
  - Top, First, Distinct

## Custom Queries
- @Query with JPQL
- @Query with nativeQuery = true
- @Modifying for UPDATE/DELETE
- Named parameters (:paramName)
- Positional parameters (?1, ?2)
