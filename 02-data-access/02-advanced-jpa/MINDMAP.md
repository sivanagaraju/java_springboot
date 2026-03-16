```mermaid
mindmap
  root((Advanced JPA))
    Relationships
      The Owner Side
        @ManyToOne
        @JoinColumn(name)
        Holds Physical Foreign Key
      The Inverse Side
        @OneToMany(mappedBy)
        Holds Java List
        No Join Table Generated
    Fetch Strategies
      FetchType.LAZY
        Delayed N+1 Risk
      FetchType.EAGER
        Instant JOINs Always
    Transactions
      @Transactional
      Spring Proxy Bean
        Intercepts Method Calls
        setAutoCommit(false)
        commit()
        rollback() on RuntimeException
      ACID Properties
        Atomicity Guarantee
```
