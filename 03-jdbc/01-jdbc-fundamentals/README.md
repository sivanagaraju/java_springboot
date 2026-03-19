# 01 — JDBC Fundamentals

## Overview
This sub-topic covers the **complete JDBC API** — from raw connections to transactions to connection pooling. These 8 concepts are the building blocks that every higher-level framework (Hibernate, Spring Data) uses internally.

## Learning Path

```mermaid
flowchart LR
    A[01 Architecture] --> B[02 Connections]
    B --> C[03 Statement Types]
    C --> D[04 PreparedStatement]
    D --> E[05 ResultSet]
    E --> F[06 Transactions]
    F --> G[07 Connection Pooling]
    G --> H[08 HikariCP]

    style A fill:#e8f5e9
    style D fill:#fff3e0,stroke:#ff9800,stroke-width:2px
    style F fill:#ffebee,stroke:#f44336,stroke-width:2px
    style H fill:#e3f2fd,stroke:#2196f3,stroke-width:2px
```

**Critical concepts** highlighted: PreparedStatement (security), Transactions (data integrity), HikariCP (production performance).

## Files in This Sub-Topic

| File | Type | Description |
|---|---|---|
| `01-jdbc-architecture.md` | Explanation | How JDBC works, driver types, architecture layers |
| `02-connection-management.md` | Explanation | Getting, configuring, and closing connections |
| `03-statement-types.md` | Explanation | Statement vs PreparedStatement vs CallableStatement |
| `04-prepared-statement.md` | Explanation | Parameterized queries, batch ops, SQL injection prevention |
| `05-resultset.md` | Explanation | Reading query results, type mapping, cursors |
| `06-transactions.md` | Explanation | ACID, commit/rollback, isolation levels, savepoints |
| `07-connection-pooling.md` | Explanation | Why pooling, pool lifecycle, sizing strategies |
| `08-hikaricp.md` | Explanation | Spring Boot's default pool, configuration, monitoring |
| `JdbcConnectionDemo.java` | Demo | Working connection examples with ASCII diagrams |
| `PreparedStatementDemo.java` | Demo | Parameterized queries and batch operations |
| `TransactionDemo.java` | Demo | Manual transaction management patterns |
| `HikariCPDemo.java` | Demo | Connection pool setup and configuration |
| `Ex01_CrudOperations.java` | Exercise | Build CRUD operations with PreparedStatement |
| `Ex02_BatchProcessing.java` | Exercise | Implement batch inserts with transaction control |
