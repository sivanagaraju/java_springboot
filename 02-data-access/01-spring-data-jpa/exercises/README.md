# Module 3 Exercises: Spring Data JPA

These exercises validate that you know exactly how to write Entities and Repository interfaces without compiling a full application. The rules of JPA are strict and mathematical.

## Exercise 1: Entity Mapping (`Ex01_EntityMapping.java`)
**Goal:** Prove you can properly decorate a POJO with JPA constraints.
**Tasks:**
1. A plain `Product` Java object is provided.
2. Annotate it so it maps exclusively to a table named `inventory_products`.
3. Provide the Primary Key mapping strategy appropriately.
4. Enforce `NOT NULL` and `UNIQUE` constraints correctly on the `sku` field.

## Exercise 2: Derived Query Methods (`Ex02_RepositoryMethods.java`)
**Goal:** Prove you can command the Spring internal engine purely via English sentence grammar.
**Tasks:**
1. Given an `Employee` entity with fields: `id`, `department`, `salary`, `isActive`.
2. Write an empty Spring Data Repository Interface.
3. Write exactly three Derived Query Method signatures mapping complex criteria without using `@Query` natively.
