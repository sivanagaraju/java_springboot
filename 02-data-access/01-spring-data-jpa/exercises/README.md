# Exercises: Spring Data JPA

## Exercise 1: Entity Mapping (`Ex01_EntityMapping.java`)
**Goal:** Annotate a POJO as a proper JPA entity with all required constraints.
**Tasks:**
1. Add `@Entity` and `@Table` annotations to the `Book` class.
2. Configure the primary key with `@Id` and `@GeneratedValue`.
3. Add column constraints: `nullable`, `unique`, `length`, `precision/scale`.
4. Add `@Enumerated(EnumType.STRING)` for the genre field.
5. Implement a `@PrePersist` lifecycle callback for the creation timestamp.

## Exercise 2: Derived Query Methods (`Ex02_RepositoryMethods.java`)
**Goal:** Write repository methods using Spring Data naming conventions.
**Tasks:**
1. Write 8 derived query method signatures for a `BookRepository`.
2. Write 2 custom `@Query` methods using JPQL.
3. Write 1 `@Modifying` update query.
4. Verify generated SQL matches expectations using `spring.jpa.show-sql=true`.
