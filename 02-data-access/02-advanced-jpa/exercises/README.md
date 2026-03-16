# Module 2 (Advanced): JPA Relationships & Transactions

These exercises force you to master mapping Foreign Keys in Java and understanding transaction proxy behavior conceptually.

## Exercise 1: Building a `@OneToMany` Relationship (`Ex01_Relationships.java`)
**Goal:** Prove you can link two entities together bi-directionally flawlessly.
**Tasks:**
1. You have an `Author` class and a `Book` class.
2. Annotate the `Book` as the physical database owner of the relationship using `@ManyToOne` and `@JoinColumn`.
3. Annotate the `Author` to hold a List of Books using `@OneToMany` with the exact correct `mappedBy` command pointing back to the Book's internal variable.
