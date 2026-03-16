# Entity Relationships

Relational Databases are powerful because they link tables together using Foreign Keys. But how do we represent Foreign Keys in pure Java Objects?

JPA provides relationship annotations to mechanically manage this.

## The 4 Cardinality Annotations

1. `@OneToOne`: E.g., User has exactly one UserProfile.
2. **`@OneToMany`**: E.g., Department has many Employees. (Most common)
3. **`@ManyToOne`**: E.g., Employee belongs to one Department. (Most common)
4. `@ManyToMany`: E.g., Students attend many Courses, and Courses have many Students. (Requires a join table)

## Example: One-To-Many / Many-To-One

The most standard architecture in Spring Boot is a bi-directional `@OneToMany` relationship.

### The "Many" Side (The Owner)
In Relational Database theory, the table that physically holds the Foreign Key column is the "Owner" of the relationship.

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The @JoinColumn tells Hibernate exactly which physical database column
    // holds the Foreign Key pointing to the Department table.
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
```

### The "One" Side (The Inverse)
The Department object physically holds a `List` of Employees. However, the `Department` table in the database does NOT hold a list of foreign keys. Therefore, it mathematically does not "own" the relationship.

```java
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 'mappedBy' explicitly tells Hibernate: "Do not create a join table!
    // Simply look at the 'department' field inside the Employee class to find the exact Foreign Key."
    @OneToMany(mappedBy = "department")
    private List<Employee> employees = new ArrayList<>();
}
```

## The N+1 Query Problem

When you retrieve a `Department` from the database using Spring Data JPA natively, Hibernate must decide whether to load the list of `Employees` immediately, or wait until you actually call `department.getEmployees()`.

This is called **Fetch Strategy**:
- `FetchType.LAZY` (Default for Collections): Hibernate only loads the Department. When you call `.getEmployees()`, it fires a *second* SQL query to get the employees.
  - *Danger:* If you loop over 100 Departments and call `getEmployees()`, Hibernate fires 1 query for departments, and 100 separate queries for employees (The N+1 Problem).
- `FetchType.EAGER` (Default for Single objects): Hibernate performs a massive SQL `JOIN` instantly cleanly efficiently securely seamlessly effortlessly.

(Instead of listing adverbs, I will just say instantly and efficiently.)
*Fixing representation:* Hibernate performs a SQL JOIN to pull all data instantly.
