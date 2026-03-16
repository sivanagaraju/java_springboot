# Track 3: Spring Data JPA

## Overview
This module explores the exact standard mechanism for interacting with Relational Databases in the Spring Ecosystem. We move from raw JDBC, past manual Hibernate mappings, and directly into the architectural magic of Spring Data Repositories.

## Core Concepts
- **JDBC & ORM:** Understand the historical pain of raw SQL and how Object-Relational Mappers (ORMs) solve the mapping problem conceptually.
- **JPA vs. Hibernate:** JPA is the rulebook (specification). Hibernate is the engine (implementation).
- **Entities:** How to map a generic Java Object perfectly to a database table utilizing `@Entity`, `@Id`, and `@Column`.
- **The Application Context Connection:** How Spring manages the `EntityManager` and Data Sources behind the scenes.
- **Spring Data Repositories:** The abstraction layer on top of Hibernate. By defining empty interfaces extending `JpaRepository`, Spring builds the entire concrete DAOs for you dynamically.
- **Derived Query Methods:** Writing English method signatures matching your class fields and allowing Spring to mathematically generate the SQL query automatically (e.g., `findByEmail`).

## Structure
- `explanation/`: Deep dives into Entity mapping, Repository creation, and Query Method syntax organically.
- `exercises/`: Challenges pushing you to write Entity standard annotations and mechanically flawless repository signatures.
