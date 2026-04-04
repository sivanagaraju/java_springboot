# Exercise Standards — spring-mastery

All exercise files and solution files follow these standards.

## Contents

- File Naming Conventions
- Difficulty Levels
- Exercise File Format
- Solution File Format
- Exercise README.md Format
- Module Exercise Allocation
- What Immediately Fails the Standard

---

## File Naming Conventions

```
exercises/
├── README.md                           ← How to compile and run all exercises
├── Ex01_ConceptName.java               ← Level 1 or 2 exercise
├── Ex02_ConceptName.java               ← Level 2 or 3 exercise
└── solutions/
    ├── Sol01_ConceptName.java          ← Solution for Ex01
    └── Sol02_ConceptName.java          ← Solution for Ex02
```

File naming rules:
- Exercise files: `Ex{NN}_{CamelCaseName}.java`
- Solution files: `Sol{NN}_{CamelCaseName}.java` (same NN as the exercise)
- Numbers are zero-padded: `Ex01`, `Ex02`, `Ex10`, `Ex11`
- CamelCase name describes what the exercise builds: `BankAccount`, `ConnectionPool`, `JwtFilter`

---

## Difficulty Levels

### Level 1 — Guided (target: <20 minutes)

For beginners encountering the concept for the first time.

Characteristics:
- Class skeleton + all imports provided
- 1–3 clearly marked `// TODO` comments
- Each TODO asks for one specific thing (add an annotation, fill a field, call one method)
- The TODO describes WHAT to do but not HOW (learner must look it up)
- Running the complete solution produces visible output

Example TODO style:
```java
// TODO: Add the Spring stereotype annotation that marks this as a service bean
// Hint: check 01-inversion-of-control.md — look at the annotations table
public class ProductService {
```

### Level 2 — Practitioner (target: 20–45 minutes)

For learners who have read the explanation files.

Characteristics:
- Full class skeleton with method signatures
- Method bodies are empty stubs with `// TODO: implement` and a description of the contract
- Learner writes all logic — no hints about which APIs to call
- Expected to produce working output when complete

Example stub style:
```java
/**
 * Find all employees in a given department.
 * Use PreparedStatement with a parameterized query.
 * Return an empty list if no employees are found — never return null.
 *
 * @param departmentId  Department to filter by
 * @return List of Employee objects, may be empty
 */
public List<Employee> findByDepartment(Long departmentId) {
    // TODO: Implement using PreparedStatement
    // The SQL: SELECT id, name, salary, active FROM employee WHERE department_id = ?
    // Handle ResultSet iteration — create one Employee per row
    throw new UnsupportedOperationException("Not implemented yet");
}
```

### Level 3 — Production (target: 45–90 minutes)

For learners who are ready to apply the concept independently.

Characteristics:
- No skeleton — only a scenario description as a class-level javadoc
- Learner designs the class structure, chooses APIs, handles edge cases
- Must satisfy a stated acceptance criteria (tested by manually running the app)
- Should include something non-obvious that requires the learner to think

Example Level 3 header:
```java
/**
 * Exercise 03 — Custom Spring Security Filter
 * Level: 3 (Production)
 * ============================================================
 *
 * SCENARIO
 * --------
 * You are building a rate-limiting filter for an API gateway.
 * Each client IP is allowed 10 requests per minute.
 * Requests exceeding the limit must receive HTTP 429 with a
 * Retry-After header indicating when the window resets.
 *
 * REQUIREMENTS
 * ------------
 * 1. Extend OncePerRequestFilter
 * 2. Track request counts per IP in an in-memory ConcurrentHashMap
 * 3. Reset counts every 60 seconds using a ScheduledExecutorService
 * 4. Return 429 with Retry-After header when limit exceeded
 * 5. Allow OPTIONS requests through without rate limiting (CORS pre-flight)
 *
 * SUCCESS CRITERIA
 * ----------------
 * - 10 requests from the same IP succeed (200 OK)
 * - The 11th request fails (429 Too Many Requests)
 * - The Retry-After header is present and shows seconds until window reset
 * - A second IP is tracked independently (not sharing the same count)
 */
public class RateLimitingFilter extends OncePerRequestFilter {
    // Your implementation here
}
```

---

## Exercise File Format

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_ConnectionPool.java                              ║
 * ║  MODULE : 03-jdbc / 01-jdbc-fundamentals                        ║
 * ║  GRADLE : ./gradlew :03-jdbc:run                                ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  EXERCISE LEVEL : 1 (Guided)                                    ║
 * ║  PURPOSE        : Configure HikariCP connection pool            ║
 * ║  COVERS         : HikariCP DataSource, pool sizing, timeouts    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  INSTRUCTIONS                                                    ║
 * ║  Fill in the 3 marked TODOs to configure HikariCP correctly.    ║
 * ║  Run the class to verify your configuration works.              ║
 * ║                                                                  ║
 * ║  SUCCESS: Console shows "Pool initialized with 10 connections"  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.exercises;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Ex01_ConnectionPool {

    public static void main(String[] args) throws SQLException {
        HikariConfig config = new HikariConfig();

        // TODO 1: Set the JDBC URL for PostgreSQL
        // Format: jdbc:postgresql://host:port/database
        // Our Docker database: host=localhost, port=5432, database=springdb
        config.setJdbcUrl("YOUR_URL_HERE");

        // TODO 2: Set the username and password
        // Credentials from our Docker setup: user=spring, password=spring
        config.setUsername("YOUR_USERNAME");
        config.setPassword("YOUR_PASSWORD");

        // TODO 3: Set the maximum pool size to 10
        // This caps the number of concurrent database connections
        // config.setMaximumPoolSize(???);

        HikariDataSource dataSource = new HikariDataSource(config);
        System.out.println("Pool initialized with " + config.getMaximumPoolSize() + " connections");

        // Test we can actually get a connection
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Got connection: " + conn.getMetaData().getDatabaseProductName());
        }

        dataSource.close();
    }
}
```

---

## Solution File Format

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_ConnectionPool.java                             ║
 * ║  SOLUTION FOR : Ex01_ConnectionPool.java                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  KEY LEARNINGS                                                   ║
 * ║  1. setMaximumPoolSize controls max concurrent connections       ║
 * ║  2. HikariCP validates the URL and credentials at startup        ║
 * ║  3. try-with-resources returns connection to pool automatically  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.exercises.solutions;

// ... full working implementation with WHY comments ...

/*
 * COMMON MISTAKES
 * ===============
 * 1. Using DriverManager.getConnection() instead of DataSource
 *    → DriverManager opens a new TCP connection every call (~100ms each)
 *    → DataSource reuses pooled connections (~1ms each)
 *    → Always use DataSource in any real application
 *
 * 2. Not calling dataSource.close() at shutdown
 *    → HikariCP keeps background threads running
 *    → In production, Spring Boot closes it automatically via @Bean lifecycle
 *    → In standalone demos, call close() explicitly
 *
 * 3. Setting maximumPoolSize too high (e.g., 100)
 *    → PostgreSQL defaults to max_connections=100
 *    → Multiple application instances × 100 = connection exhaustion
 *    → Rule of thumb: (2 × CPU cores) + effective_spindle_count for most apps
 */
```

---

## Exercise README.md Format

Every `exercises/README.md` must contain:

```markdown
# Exercises — [Sub-topic Name]

## How to Run

Each exercise is a standalone Java class with a `main()` method.

```bash
# Compile and run from repo root (replace ExNN with exercise number)
./gradlew :03-jdbc:run --args="com.learning.jdbc.exercises.Ex01_ConnectionPool"

# Or run all tests to validate solutions
./gradlew :03-jdbc:test
```

## Prerequisites

- Docker running with PostgreSQL container (see module README.md for setup)
- Java 21

## Exercises

| File | Level | Covers |
|------|-------|--------|
| `Ex01_ConnectionPool.java` | 1 (Guided) | HikariCP setup, pool sizing |
| `Ex02_CrudOperations.java` | 2 (Practitioner) | PreparedStatement CRUD, ResultSet |

## Solutions

Solutions are in `solutions/`. Do not look at them until you have made a genuine
attempt at the exercise. The learning happens in the struggle.
```

---

## Module Exercise Allocation

| Module type | Minimum exercises |
|------------|------------------|
| Foundation topics (00–01) | 2 per sub-topic |
| Framework topics (02–09) | 2–3 per sub-topic |
| Advanced topics (10–17) | 1–2 per sub-topic |

Every sub-topic with 3+ explanation files gets at least one Level 3 exercise.

---

## What Immediately Fails the Standard

| Failure | Standard violated |
|---------|------------------|
| Exercise has syntax errors that prevent compilation | Exercise format — must compile |
| No solution file for an exercise | Pairing rule — every exercise has a solution |
| Solution file doesn't compile or run | Solution quality |
| No COMMON MISTAKES block in solution | Solution format |
| No WHY comments in solution — just the code | Solution quality |
| Exercise TODO is too vague ("implement this") | Exercise format — clear contract |
| `javax.*` imports | Stack constraint |
| Maven commands | Stack constraint |
