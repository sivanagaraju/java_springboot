# Stack Constraints — spring-mastery Repository

## Contents

- Java 21 LTS Requirements
- Spring Boot Version Rules
- Gradle Rules (Never Maven)
- Database Setup
- Package Conventions
- Spring Boot Layer Structure

---

## Java 21 LTS Requirements

Use modern Java features wherever taught. These are NOT optional — the repo
teaches modern Java, and using deprecated patterns defeats the purpose.

| Feature | Syntax | When to Use |
|---------|--------|-------------|
| Records | `record Point(int x, int y) {}` | DTOs, value objects, response types |
| Sealed classes | `sealed interface Shape permits Circle, Rect {}` | Domain hierarchies, ADTs |
| Switch expressions | `int result = switch (day) { case MON -> 1; ... }` | Replace traditional switch |
| Text blocks | `String sql = """ SELECT * FROM ... """;` | Multi-line SQL, JSON, HTML |
| Pattern matching instanceof | `if (obj instanceof String s) { s.length(); }` | Replace verbose casts |
| Virtual threads (Java 21) | `Thread.ofVirtual().start(task)` | High-throughput I/O concurrency |

Every `build.gradle` must include:
```groovy
sourceCompatibility = '21'
targetCompatibility = '21'
```

---

## Spring Boot Version Rules

- **Spring Boot:** 3.2.x (maps to Spring Framework 6.1.x)
- **Jakarta EE 10:** All `javax.*` imports are now `jakarta.*` — never write `javax.persistence`, always `jakarta.persistence`
- **Spring Security 6:** Lambda DSL only — `http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())`; the old `http.authorizeRequests().antMatchers()` was removed in Spring Security 6

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.0'
    id 'io.spring.dependency-management' version '1.1.4'
}
```

---

## Gradle Rules (Never Maven)

Always use the Gradle wrapper. Never call `gradle` directly (version mismatch risk).

```bash
# Run Spring Boot app
./gradlew :module-name:bootRun

# Run pure Java demo (no Spring Boot)
./gradlew :00-java-foundation:run --args="ClassName"

# Run tests (all modules)
./gradlew test

# Run tests (single module)
./gradlew :14-testing:test

# Clean build
./gradlew clean build

# List all tasks for a module
./gradlew :module-name:tasks
```

Never write `mvn`, `mvnw`, `pom.xml`, or Maven-style dependency syntax.
If you see a Maven example from an external source, translate it to Gradle Groovy DSL.

---

## Database Setup

| Environment | Database | Connection |
|------------|---------|-----------|
| Integration tests (running locally) | PostgreSQL via Docker | `localhost:5432/springdb` |
| Unit tests (fast, no Docker) | H2 in-memory | `jdbc:h2:mem:testdb` |

Docker Compose for PostgreSQL:
```yaml
services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: springdb
      POSTGRES_USER: spring
      POSTGRES_PASSWORD: spring
    ports:
      - "5432:5432"
```

---

## Package Conventions

```
com.learning.<module>/
├── controller/    ← @RestController — REST endpoints only, no logic
├── service/       ← @Service — business logic (always interface-backed)
├── repository/    ← JpaRepository / JdbcTemplate data access
├── entity/        ← @Entity JPA entities
├── dto/           ← Request/Response DTOs (never expose entities directly)
├── config/        ← @Configuration classes
├── exception/     ← Custom exceptions + @RestControllerAdvice handler
└── security/      ← Spring Security + JWT filters
```

Naming rules:
- Controllers: `ProductController`, `OrderController`
- Services: `ProductService` (interface) + `ProductServiceImpl` (class)
- Repositories: `ProductRepository` extends `JpaRepository<Product, Long>`
- DTOs: `CreateProductRequest`, `ProductResponse`, `ProductListResponse`
- Exceptions: `ProductNotFoundException`, `InsufficientStockException`

---

## Spring Boot Layer Structure

```
HTTP Request
    │
    ▼
[ @RestController ]  ← validates input, maps HTTP ↔ DTO
    │
    ▼
[ @Service ]         ← business rules, transaction boundary
    │
    ▼
[ Repository ]       ← JpaRepository / JdbcTemplate
    │
    ▼
[ Database ]
```

The service layer is ALWAYS interface-backed:
```java
// Interface in service/
public interface ProductService {
    ProductResponse create(CreateProductRequest req);
}

// Implementation in service/impl/ or service/
@Service
public class ProductServiceImpl implements ProductService { ... }
```

This enables testing with Mockito and follows Spring convention.
