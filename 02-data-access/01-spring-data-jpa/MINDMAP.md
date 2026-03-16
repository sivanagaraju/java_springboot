```mermaid
mindmap
  root((Spring Data JPA))
    The Layers
      Spring Data (Abstractions)
      Hibernate (Implementation engine)
      JPA (Specification rules)
      JDBC (Core Driver)
    Entities
      @Entity
      @Id
      @GeneratedValue
        GenerationType.IDENTITY
      @Column
      @Transient
      No-Arg Constructor Required
    Spring Data Repositories
      JpaRepository interface
      Built-in functionality
        save()
        findById()
        findAll()
        deleteById()
      Dynamic Proxies
    Query Generation
      Derived Query Methods
        findByFirstName(String)
        findByAgeGreaterThan(int)
        findByLastNameOrderByFirstNameAsc()
      @Query
        JPQL
        Native SQL
```
