# Entities and Annotations

How does Hibernate know how to map a Java Object onto a Relational Database Table? We use standardized JPA annotations.

## The `@Entity` Annotation

The `@Entity` annotation is the absolute fundamental requirement. It marks a plain Java Class (POJO) as a class that maps directly to a database table.

```java
import jakarta.persistence.*;

@Entity
@Table(name = "users_table") // Optional: If omitted, Hibernate assumes the table is named "user"
public class User {

    // Every Entity REQUIRES a Primary Key mapped with @Id.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email_address", unique = true, nullable = false)
    private String email;

    // ... Getters, Setters, Default Constructors
}
```

## Core Annotations

1. **`@Id`:** Flags the field as the Primary Key for the relational row.
2. **`@GeneratedValue`:** Instructs Hibernate exactly *how* to generate the ID.
   - `GenerationType.IDENTITY`: Relies absolutely on the database's native Auto-Increment feature. This is the most common constraint for MySQL/Postgres.
   - `GenerationType.UUID`: Creates a globally unique cryptographic random string.
3. **`@Column`:** Defines structural constraints on the column. If omitted, Hibernate assumes the column name matches the field name implicitly.
   - `nullable = false`: Enforces `NOT NULL` on the database schema.
   - `unique = true`: Enforces a `UNIQUE` index constraint on the database.
4. **`@Transient`:** Instructs Hibernate to explicitly **ignore** this field completely. It will exist in Java memory, but Hibernate will never attempt to save it to a column.

## The Default Constructor Requirement

Because Hibernate uses deep Java Reflection physically, it requires a plain **No-Argument Default Constructor** to instantiate the object natively before it can violently populate the fields with database row data recursively.

```java
@Entity
public class Product {
    @Id
    private Long id;
    
    // REQUIRED BY HIBERNATE
    // You can make this protected if you want to hide it from your own business logic.
    protected Product() {} 
    
    // Your actual constructor logically used by your code
    public Product(String name) { ... }
}
```
