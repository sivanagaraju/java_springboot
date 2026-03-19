# 01 — JDBC Architecture

## What is JDBC?

JDBC (Java Database Connectivity) is Java's **standard API** for connecting to relational databases. It defines interfaces in the `java.sql` package that every database driver must implement.

> **Python Bridge:** JDBC is Java's equivalent of Python's DB-API 2.0 (PEP 249). Just like `psycopg2` implements PEP 249 for PostgreSQL, the PostgreSQL JDBC driver implements `java.sql.*` interfaces.

## Architecture Layers

```mermaid
flowchart TD
    A["Your Java Application"] --> B["JDBC API<br/>(java.sql.*)"]
    B --> C["JDBC Driver Manager"]
    C --> D["PostgreSQL JDBC Driver<br/>(Type 4 — pure Java)"]
    C --> E["MySQL JDBC Driver<br/>(Type 4 — pure Java)"]
    C --> F["Oracle JDBC Driver<br/>(Type 4 — pure Java)"]
    D --> G[("PostgreSQL")]
    E --> H[("MySQL")]
    F --> I[("Oracle")]

    style B fill:#ffebee,stroke:#f44336,stroke-width:2px
    style C fill:#fff3e0
```

## The Key Interfaces

| Interface | Responsibility | Python Equivalent |
|---|---|---|
| `DriverManager` | Selects the right driver for a URL | `psycopg2.connect()` |
| `Connection` | Session to the database | `connection` object |
| `Statement` | Executes static SQL | `cursor.execute("SELECT * ...")` |
| `PreparedStatement` | Executes parameterized SQL (safe!) | `cursor.execute(sql, params)` |
| `ResultSet` | Iterates over query results | `cursor.fetchall()` |
| `DataSource` | Factory for connections (production) | `create_engine()` in SQLAlchemy |

## Connection URL Format

```
jdbc:postgresql://localhost:5432/mydb?sslmode=require

jdbc:     ← protocol prefix
postgresql: ← driver type
localhost:5432 ← host:port
/mydb     ← database name
?sslmode=require ← connection parameters
```

**Python comparison:**
```python
# Python psycopg2
conn = psycopg2.connect("postgresql://localhost:5432/mydb?sslmode=require")

# Python SQLAlchemy
engine = create_engine("postgresql://user:pass@localhost:5432/mydb")
```

## Driver Types (History → Modern)

```mermaid
timeline
    title JDBC Driver Evolution
    1996 : Type 1 — JDBC-ODBC Bridge (deprecated)
    1998 : Type 2 — Native API (C library required)
    2000 : Type 3 — Network Protocol (middleware server)
    2004 : Type 4 — Pure Java (modern standard — use THIS)
```

**Type 4 is the only relevant type today.** It's a pure Java implementation that speaks the database's native wire protocol directly over TCP.

## How Drivers Are Loaded (Java 6+)

```mermaid
sequenceDiagram
    participant App as Your Code
    participant DM as DriverManager
    participant SL as ServiceLoader
    participant Dr as PostgreSQL Driver

    App->>DM: getConnection("jdbc:postgresql://...")
    DM->>SL: Load META-INF/services/java.sql.Driver
    SL->>Dr: Found org.postgresql.Driver
    DM->>Dr: acceptsURL("jdbc:postgresql://...")?
    Dr-->>DM: true
    DM->>Dr: connect(url, properties)
    Dr-->>App: Connection object
```

**No manual `Class.forName("org.postgresql.Driver")` needed!** Since Java 6, drivers are loaded automatically via `ServiceLoader`.

## Interview Questions

### Conceptual

**Q1: What is the relationship between JDBC and database drivers?**
> JDBC defines the API interfaces (`Connection`, `Statement`, `ResultSet`). Database vendors provide driver implementations. Your code programs to the interfaces, making it database-agnostic. This is the **Strategy Pattern** — the same interface, different implementations.

**Q2: Why is Type 4 the preferred JDBC driver type?**
> Type 4 drivers are pure Java — no native libraries, no middleware servers. They communicate directly with the database over TCP using the native wire protocol. This makes them portable, fast, and easy to deploy (just add a JAR to classpath).

### Scenario/Debug

**Q3: You get "No suitable driver found for jdbc:postgresql://localhost:5432/mydb". What's wrong?**
> The PostgreSQL JDBC driver JAR is not on the classpath. Fix: add `implementation 'org.postgresql:postgresql:42.7.1'` to your `build.gradle` dependencies.

**Q4: Why does `DriverManager.getConnection()` return a `Connection` interface, not a concrete class?**
> This is classical OOP polymorphism. The actual class is `org.postgresql.PGConnection` (or similar), but returning the interface decouples your code from the specific driver. You can switch databases by changing the URL and driver JAR alone.

### Quick Fire

**Q5: What package contains the core JDBC interfaces?**
> `java.sql` (part of the JDK — no external dependency).

**Q6: What's the Python equivalent of `DriverManager.getConnection(url)`?**
> `psycopg2.connect(dsn)` or `sqlite3.connect(path)`.
