# 02 — Data Access

## Why This Module Matters

Every backend application needs to persist and retrieve data from a database. In the Spring Boot ecosystem, **Spring Data JPA** is the dominant data access framework — it sits on top of JPA (Jakarta Persistence API) and Hibernate to eliminate boilerplate SQL and repository code.

If you come from Python, think of Spring Data JPA as the combination of **SQLAlchemy ORM + Alembic migrations + Repository pattern** — but with compile-time type safety and convention-based query generation.

## Python/FastAPI Comparison

| Concern | Python/FastAPI | Java/Spring Data JPA |
|---|---|---|
| ORM | SQLAlchemy | Hibernate (JPA implementation) |
| Specification | No standard (SQLAlchemy is de facto) | JPA (Jakarta Persistence API) — official standard |
| Model definition | Python class + `Column()` | Java class + `@Entity` / `@Column` |
| Repository | Custom `crud.py` functions | `JpaRepository<T, ID>` (auto-generated) |
| Query builder | SQLAlchemy query API | Derived query methods / `@Query` JPQL |
| Migrations | Alembic | Flyway / Liquibase |
| Transactions | `session.begin()` / `commit()` | `@Transactional` annotation |
| Connection pool | SQLAlchemy pool | HikariCP (default in Spring Boot) |
| Relationships | `relationship()` + `ForeignKey` | `@OneToMany` / `@ManyToOne` annotations |

## Module Structure

- **[01-spring-data-jpa/](01-spring-data-jpa/)** — Core JPA concepts: entities, annotations, repositories, derived queries
- **[02-advanced-jpa/](02-advanced-jpa/)** — Entity relationships, transactions, performance tuning

## Mindmap

See [MINDMAP.md](MINDMAP.md) for a visual overview of all data access concepts covered in this module.
