# JDBC Fundamentals — Self-Check Quiz

> Complete this quiz after finishing `03-jdbc/01-jdbc-fundamentals/`.
> Attempt all questions before checking the Answer Key at the bottom.

## Contents

- Section 1: Conceptual (Q1–Q5)
- Section 2: Code Reading (Q6–Q8)
- Section 3: Spot the Bug (Q9–Q11)
- Section 4: Scenario / Debug (Q12–Q14)
- Section 5: Quick Fire (Q15–Q20)
- Answer Key

---

## Section 1 — Conceptual

**Q1.** Why does `DriverManager.getConnection()` cause performance problems in
production, and what does HikariCP do differently?

**Q2.** JDBC defines interfaces like `Connection`, `Statement`, and `ResultSet`
rather than concrete classes. Why? What design pattern does this enable?

**Q3.** Why should you always use `PreparedStatement` instead of `Statement`
for queries that include user-provided input?

**Q4.** What is the difference between `connection.setAutoCommit(false)` and
the default auto-commit mode? When would you explicitly disable auto-commit?

**Q5.** The PostgreSQL JDBC driver is a "Type 4" driver. What does this mean
and why is Type 4 preferred over other types?

---

## Section 2 — Code Reading

**Q6.** What does this code print? Is there a problem with it?

```java
try (Connection conn = dataSource.getConnection()) {
    Statement stmt = conn.createStatement();
    String name = "O'Brien";
    ResultSet rs = stmt.executeQuery(
        "SELECT * FROM employee WHERE name = '" + name + "'"
    );
    while (rs.next()) {
        System.out.println(rs.getString("name"));
    }
}
```

A) Prints "O'Brien" correctly — no problem
B) Throws a `SQLException` due to the apostrophe in "O'Brien"
C) Compiles and runs but is vulnerable to SQL injection
D) Both B and C

**Q7.** What is the output of this code? Explain each step.

```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:postgresql://localhost:5432/springdb");
config.setUsername("spring");
config.setPassword("spring");
config.setMaximumPoolSize(5);
config.setMinimumIdle(2);

HikariDataSource ds = new HikariDataSource(config);
System.out.println("Pool created");

Connection c1 = ds.getConnection();
Connection c2 = ds.getConnection();
System.out.println("Got 2 connections");

c1.close();
System.out.println("c1 closed — connections returned: 1");

ds.close();
System.out.println("Pool shut down");
```

**Q8.** This code inserts an employee. What is printed when it runs successfully?

```java
String sql = "INSERT INTO employee (name, salary) VALUES (?, ?) RETURNING id";
try (PreparedStatement ps = conn.prepareStatement(sql)) {
    ps.setString(1, "Alice");
    ps.setBigDecimal(2, new BigDecimal("75000.00"));
    try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            System.out.println("Inserted ID: " + rs.getLong(1));
        }
    }
}
```

A) Nothing — INSERT does not return a ResultSet
B) "Inserted ID: 1" (or whatever the auto-generated ID is)
C) A `SQLException` — PreparedStatement cannot use `RETURNING`
D) "Inserted ID: 0" — RETURNING is not supported by the JDBC driver

---

## Section 3 — Spot the Bug

**Q9.** This code has a resource leak. Identify exactly what is not being closed
and what consequence this has under load.

```java
public List<Employee> findAll(DataSource dataSource) throws SQLException {
    List<Employee> employees = new ArrayList<>();
    Connection conn = dataSource.getConnection();
    PreparedStatement ps = conn.prepareStatement("SELECT id, name, salary FROM employee");
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
        employees.add(new Employee(rs.getLong("id"), rs.getString("name")));
    }
    return employees;
}
```

**Q10.** This transaction code has a bug that means the rollback never executes.
Identify it.

```java
public void transfer(Long fromId, Long toId, BigDecimal amount) throws SQLException {
    Connection conn = dataSource.getConnection();
    conn.setAutoCommit(false);
    try {
        debit(conn, fromId, amount);
        credit(conn, toId, amount);
        conn.commit();
    } catch (Exception e) {
        conn.rollback();
    }
}
```

**Q11.** This code is supposed to update an employee's salary and return the
updated employee. Find the bug.

```java
@Transactional
public Employee updateSalary(Long id, BigDecimal newSalary) {
    Employee emp = employeeRepo.findById(id).orElseThrow();
    employeeRepo.save(new Employee(id, emp.getName(), newSalary));
    return employeeRepo.findById(id).orElseThrow();
}
```

---

## Section 4 — Scenario / Debug

**Q12.** A fintech startup's payment service starts throwing
`HikariPool-1 - Connection is not available, request timed out after 30000ms`
under load on Black Friday. The database is running fine and accepting connections.
What are the 3 most likely causes and how do you diagnose each?

**Q13.** A developer tells you: "My JDBC code works fine in tests but throws
`PSQLException: FATAL: remaining connection slots are reserved for non-replication
superuser connections` in production." The database max_connections is set to 100.
What happened and how do you fix it?

**Q14.** You are reviewing a pull request. The developer has written this
helper method:

```java
public void executeWithTransaction(Runnable operation) throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
        conn.setAutoCommit(false);
        operation.run();
        conn.commit();
    } catch (Exception e) {
        // rollback happens automatically when connection closes
    }
}
```

What is wrong with this design and what are the consequences in production?

---

## Section 5 — Quick Fire

**Q15.** Does `rs.close()` need to be called explicitly if the `PreparedStatement`
is closed?

**Q16.** What is the JDBC URL protocol prefix for PostgreSQL?

**Q17.** What `HikariConfig` property sets how long a thread waits for a connection
from the pool before timing out?

**Q18.** Is a `ResultSet` scrollable by default? What does this mean?

**Q19.** What does `connection.setReadOnly(true)` do at the database level for
PostgreSQL?

**Q20.** Name the Java 7+ feature that makes JDBC connection management safe
without finally blocks.

---
---

## Answer Key

> ⚠️ Do not read below until you have attempted all 20 questions.

---

### Section 1 — Conceptual

**A1.** `DriverManager.getConnection()` opens a brand new TCP connection to the
database on every call. A TCP connection takes 50–200ms to establish. Under any
meaningful load, this becomes the bottleneck — a service handling 50 requests per
second would spend most of its time just opening connections.
HikariCP pre-opens a pool of connections at startup and reuses them. Borrowing a
connection from the pool takes ~1ms. Returning it is just marking it as available.
The TCP connection stays open throughout the application's lifetime.

**A2.** JDBC defines interfaces to apply the Strategy pattern. `Connection`,
`Statement`, and `ResultSet` are contracts — `java.sql` interfaces. Each database
vendor writes their own concrete implementation. `org.postgresql.PGConnection`
implements `Connection`. `com.mysql.cj.jdbc.ConnectionImpl` implements `Connection`.
Your code never imports vendor-specific classes — it only works with the interfaces.
This means you can switch from PostgreSQL to MySQL by changing the driver JAR and
connection URL, with zero changes to your SQL execution code.

**A3.** String concatenation creates queries like `SELECT * FROM user WHERE name = 'O'Brien'` —
the apostrophe in the data breaks the SQL syntax. More critically, an attacker can
inject `' OR '1'='1` to bypass authentication or `'; DROP TABLE users; --` to destroy
data. `PreparedStatement` separates query structure from values: the SQL is compiled
first (`SELECT * FROM employee WHERE name = ?`), then parameter values are sent
separately. The driver escapes them properly — a `?` can never become a SQL keyword.

**A4.** Auto-commit mode (default) commits every statement immediately and independently.
`setAutoCommit(false)` starts a manual transaction — you control exactly when to commit
or rollback. Use manual transactions when multiple statements must succeed or fail as
one atomic unit: e.g., debit + credit in a bank transfer. If auto-commit is on and your
app crashes between the debit and credit, the debit is permanently committed and money
disappears.

**A5.** Type 4 is "pure Java" — the driver is written entirely in Java and communicates
directly with PostgreSQL using its native wire protocol over TCP. No native C libraries,
no middleware server, no ODBC bridge. Just a JAR on the classpath. Types 1–3 were
earlier architectures requiring native OS libraries or middleware servers, which made
deployment complex and platform-dependent. Type 4 works identically on Windows, Linux,
and macOS.

---

### Section 2 — Code Reading

**A6.** Answer: **D — Both B and C.**
The apostrophe in `O'Brien` produces malformed SQL: `WHERE name = 'O'Brien'` which
causes a `SQLException`. But even if the name didn't have an apostrophe, building SQL
by string concatenation is vulnerable to SQL injection — a user could pass
`' OR '1'='1` as input. Always use `PreparedStatement` with `?` parameters.

**A7.** Output (in order):
1. HikariCP startup logs (internal) then `"Pool created"`
2. `"Got 2 connections"` — two connections successfully borrowed from the pool
3. `"c1 closed — connections returned: 1"` — `c1.close()` does NOT close the TCP
   connection; it returns it to the pool. The pool now has 1 available slot again.
4. `"Pool shut down"` — `ds.close()` closes all pooled connections and terminates
   HikariCP's background threads.

Key insight: calling `.close()` on a HikariCP `Connection` returns it to the pool —
it does not close the underlying TCP connection.

**A8.** Answer: **B** — `"Inserted ID: 1"` (or whatever the sequence generates).
`RETURNING id` is a PostgreSQL extension that makes INSERT behave like a SELECT —
it returns the generated value. `ps.executeQuery()` correctly handles this.
Note: using `ps.executeUpdate()` would lose the returned ID.

---

### Section 3 — Spot the Bug

**A9.** Three resources are opened and NONE are closed: `Connection`, `PreparedStatement`,
and `ResultSet`. If an exception occurs during iteration, none of them will be closed.
Under load this causes connection pool exhaustion — `Connection` objects are borrowed
from HikariCP but never returned, eventually causing `Connection is not available` timeouts.
Fix: wrap all three in nested `try-with-resources`:
```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement("SELECT ...");
     ResultSet rs = ps.executeQuery()) { ... }
```

**A10.** The `conn` object is not closed in the catch block. If `conn.rollback()` itself
throws a `SQLException`, the connection leaks. More importantly, the connection is
never closed at all — it is borrowed from the pool in `getConnection()` but never
returned. Under load this exhausts the pool. Fix: use try-with-resources with a
`try-finally` pattern or use Spring's `@Transactional` which handles all of this.

**A11.** The bug is `new Employee(id, emp.getName(), newSalary)` — this creates a new
Employee object with the same ID and saves it. In JPA, calling `save()` on a detached
entity (one created with `new` but having an existing ID) calls `merge()`, which works
but is semantically wrong and performance-wasteful. The idiomatic JPA approach is to
mutate the managed entity directly:
```java
Employee emp = employeeRepo.findById(id).orElseThrow();
emp.setSalary(newSalary);  // JPA dirty checking handles the UPDATE
return emp;  // no explicit save() needed
```

---

### Section 4 — Scenario / Debug

**A12.** Three most likely causes of `Connection is not available, request timed out`:
1. **Pool size too small for load:** Check `maximumPoolSize`. Black Friday traffic
   is much higher than normal. Diagnose: query `pg_stat_activity` to see active
   connections; if the pool is fully utilized, increase `maximumPoolSize`.
2. **Connection leak:** A method borrows a connection and never returns it (missing
   close or missing try-with-resources). Diagnose: enable HikariCP leak detection:
   `config.setLeakDetectionThreshold(2000)`. Check logs for "Connection leak detection
   triggered" messages.
3. **Long-running transactions holding connections:** A slow query or external API call
   inside a `@Transactional` block holds the connection for the full duration. Diagnose:
   check `pg_stat_activity` for long-running queries. Add `execution_timeout` or move
   external API calls outside the transaction boundary.

**A13.** This is connection pool exhaustion. PostgreSQL's `max_connections=100` means
it accepts at most 100 simultaneous connections across ALL clients. PostgreSQL reserves
the last few slots for superuser connections, hence "remaining connection slots reserved".
What happened: the application's HikariCP pool is configured with `maximumPoolSize` too
high relative to `max_connections`. If `maximumPoolSize=100` and there are 2 application
pods, that's potentially 200 connection attempts for 100 available slots.
Fix: calculate `maximumPoolSize` as `(max_connections - 5 for superuser) / number of pods`.
Also use PgBouncer (connection pooler in front of PostgreSQL) for high-connection environments.

**A14.** Critical flaw: the `Runnable operation` runs SQL using... which connection?
It doesn't have access to `conn`. The design is broken — the lambda can't use the
connection that the helper opened. Beyond that, the `catch` block silently swallows
all exceptions without calling `rollback()`. If `operation.run()` throws, the transaction
is committed (since there's no explicit rollback), or left open. In production this causes
partial updates and phantom data. Use Spring's `@Transactional` instead of building
transaction helpers — it handles all edge cases correctly via AOP proxy.

---

### Section 5 — Quick Fire

**A15.** No — closing the `PreparedStatement` automatically closes its `ResultSet`.
However, it is good practice to close them explicitly in the right order (ResultSet →
PreparedStatement → Connection) for clarity. Use try-with-resources — the reverse
close order is handled automatically.

**A16.** `jdbc:postgresql://`

**A17.** `connectionTimeout` (default: 30,000ms). Set via `config.setConnectionTimeout(millis)`.

**A18.** No — JDBC `ResultSet` is forward-only by default (`ResultSet.TYPE_FORWARD_ONLY`).
You can only call `rs.next()` to move forward, never `rs.previous()`. To get a scrollable
ResultSet, use `conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)`.
In practice, almost all production JDBC code uses forward-only — you process rows and move on.

**A19.** For PostgreSQL, `setReadOnly(true)` is a hint. The JDBC driver may or may not
send a `SET SESSION CHARACTERISTICS AS TRANSACTION READ ONLY` statement. PostgreSQL honours
this by rejecting write operations. HikariCP also uses this to route read connections to
read replicas if configured.

**A20.** Try-with-resources (Java 7, `AutoCloseable` interface). Any resource that
implements `AutoCloseable` is guaranteed to have `close()` called at the end of the
`try` block, even if an exception is thrown.
