# 01 â€” JDBC Fundamentals

## Overview

This sub-topic covers the **complete JDBC API** â€” from raw connections to transactions to connection pooling. These building blocks are what Hibernate and Spring Data JPA later hide behind a simpler programming model.

## Why Raw JDBC Before JPA

If you start with JPA first, it is easy to treat persistence as magic. If you start with JDBC, you see the actual SQL, connection, and transaction contract first. That makes the next layer easier to reason about and debug.

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
    H --> I[CRUDWithJDBC]

    style A fill:#e8f5e9
    style D fill:#fff3e0,stroke:#ff9800,stroke-width:2px
    style F fill:#ffebee,stroke:#f44336,stroke-width:2px
    style I fill:#e3f2fd,stroke:#2196f3,stroke-width:2px
```

**Critical concepts** highlighted: PreparedStatement (security), Transactions (data integrity), HikariCP (production performance).

## Python Bridge

| JDBC Concept | Python Equivalent | Why It Helps |
|---|---|---|
| `DriverManager.getConnection()` | `psycopg2.connect()` | Opens a database session explicitly |
| `PreparedStatement` | `cursor.execute(sql, params)` | Safe parameter binding |
| `ResultSet.next()` | `fetchone()` / `fetchall()` | Cursor-style row iteration |
| `conn.commit()` / `rollback()` | `conn.commit()` / `rollback()` | Explicit unit-of-work control |
| `HikariCP` | SQLAlchemy pool | Reuses connections instead of creating new ones |

Python feels lighter, but the same mechanics are still there. JDBC just exposes them more directly, which is exactly why it is such a good teaching layer before Spring Data JPA.

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
| `09-crud-with-jdbc.md` | Explanation | End-to-end CRUD walkthrough with Python comparison |
| `JdbcConnectionDemo.java` | Demo | Working connection examples with ASCII diagrams |
| `PreparedStatementDemo.java` | Demo | Parameterized queries and batch operations |
| `TransactionDemo.java` | Demo | Manual transaction management patterns |
| `HikariCPDemo.java` | Demo | Connection pool setup and configuration |
| `CRUDWithJDBC.java` | Demo | Raw JDBC CRUD lifecycle, batch writes, and Python bridge |
| `Ex01_CrudOperations.java` | Exercise | Build CRUD operations with PreparedStatement |
| `Ex02_BatchProcessing.java` | Exercise | Implement batch inserts with transaction control |

## Structure

- `explanation/` - Deep-dive markdown files for each JDBC concept
- `exercises/` - Hands-on JDBC practice tasks
- `resources/` - Quiz drill, cheat sheet, and curated external resource guide

## Support Pack

- [progressive-quiz-drill.md](resources/progressive-quiz-drill.md)
- [one-page-cheat-sheet.md](resources/one-page-cheat-sheet.md)
- [top-resource-guide.md](resources/top-resource-guide.md)

## How to Use This Module

1. Read `01-jdbc-architecture.md` through `08-hikaricp.md` in order.
2. Run the demos to see the API in action.
3. Read `09-crud-with-jdbc.md` to connect the concepts into one flow.
4. Move into the mini-project to practice the same mechanics in a DAO.
