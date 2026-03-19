# Architecture — Employee JDBC Mini-Project

## Overview

A complete **CRUD application** demonstrating raw JDBC with HikariCP connection pooling. This project applies every concept from `01-jdbc-fundamentals` into a working, production-patterned application.

> **Python Bridge:** This is equivalent to a Python project with `create_engine()` → `Repository` class → `main.py` CLI runner.

---

## System Architecture

```mermaid
C4Container
    title Employee JDBC — Container Diagram

    Person(user, "Developer", "Runs CLI to test CRUD")

    Container_Boundary(app, "Employee JDBC App") {
        Component(main, "Main.java", "Java 21", "Entry point — exercises all DAO methods")
        Component(dao, "EmployeeDao.java", "Java 21", "Data Access Object — all SQL lives here")
        Component(config, "DatabaseConfig.java", "Java 21", "HikariCP pool — singleton DataSource")
    }

    ContainerDb(db, "PostgreSQL", "employees table")

    Rel(user, main, "runs via CLI")
    Rel(main, dao, "calls CRUD methods")
    Rel(dao, config, "borrows Connection from pool")
    Rel(config, db, "JDBC TCP connection")
```

## Component Diagram

```mermaid
flowchart TD
    A["Main.java<br/>(Entry Point)"] --> B["EmployeeDao.java<br/>(Data Access Object)"]
    B --> C["DatabaseConfig.java<br/>(HikariCP Pool)"]
    C --> D[("PostgreSQL<br/>employees table")]

    style A fill:#e8f5e9,stroke:#388e3c
    style B fill:#fff3e0,stroke:#f57c00
    style C fill:#e3f2fd,stroke:#1976d2
    style D fill:#f3e5f5,stroke:#7b1fa2
```

## Layer Responsibilities

| Component | Responsibility | Design Pattern | Python Equivalent |
|---|---|---|---|
| `Main.java` | CLI entry point, demo orchestration | Application entry | `if __name__ == "__main__":` |
| `EmployeeDao.java` | All SQL operations (CRUD + batch + search) | DAO (Data Access Object) | Repository class with `cursor.execute()` |
| `DatabaseConfig.java` | HikariCP pool lifecycle (init + shutdown) | Singleton | `engine = create_engine(url, pool_size=10)` |

## Data Flow

```mermaid
sequenceDiagram
    participant Main
    participant DAO as EmployeeDao
    participant Pool as HikariCP Pool
    participant DB as PostgreSQL

    Main->>DAO: create("Alice", "Eng", 95000)
    DAO->>Pool: getConnection()
    Pool-->>DAO: Connection (from pool)
    DAO->>DB: INSERT INTO employees VALUES (?, ?, ?)
    DB-->>DAO: Generated ID = 1
    DAO-->>Main: return 1L
    DAO->>Pool: close() → returns to pool
```

## Key Design Decisions

| Decision | Rationale |
|---|---|
| **DAO pattern** (not Repository) | Raw JDBC has no framework — DAO isolates SQL from business logic |
| **DataSource injection** | DAO accepts `DataSource`, not raw URL — enables pool sharing and testing |
| **PreparedStatement everywhere** | Prevents SQL injection; enables parameter binding |
| **try-with-resources** | Guarantees `Connection`, `PreparedStatement`, `ResultSet` are closed |
| **RETURN_GENERATED_KEYS** | Gets DB-assigned ID after INSERT without a separate SELECT |
| **Manual transaction in batch** | `setAutoCommit(false)` + `commit()` / `rollback()` for atomicity |

## Database Schema

```sql
CREATE TABLE employees (
    id          BIGSERIAL       PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    department  VARCHAR(50)     NOT NULL,
    salary      DECIMAL(10,2)   NOT NULL CHECK (salary > 0),
    created_at  TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
);
```

```mermaid
erDiagram
    EMPLOYEES {
        bigserial id PK
        varchar name "NOT NULL"
        varchar department "NOT NULL"
        decimal salary "CHECK > 0"
        timestamp created_at "DEFAULT now()"
    }
```

## Interview Questions

### Conceptual
1. **Why use the DAO pattern instead of putting SQL directly in Main?**
   *Hint:* Separation of concerns — DAO isolates data access, making it testable and swappable.

2. **Why inject `DataSource` instead of creating connections with `DriverManager` inside the DAO?**
   *Hint:* Pool management — `DataSource` wraps HikariCP, enabling connection reuse. `DriverManager` creates a new TCP connection every time (~50ms overhead).

### Scenario/Debug
3. **What happens if you forget `conn.setAutoCommit(true)` in the finally block of `batchCreate()`?**
   *Hint:* The connection is returned to the pool still in manual-commit mode. The next borrower's operations won't commit automatically — silent data loss.

### Quick Fire
4. **`Statement.RETURN_GENERATED_KEYS` — what SQL does this translate to in PostgreSQL?**
   *Answer:* `INSERT ... RETURNING *` (PostgreSQL-specific; JDBC driver handles the translation).
