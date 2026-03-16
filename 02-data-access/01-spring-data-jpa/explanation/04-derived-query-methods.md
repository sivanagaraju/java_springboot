# Derived Query Methods

The `JpaRepository` interface natively gives you `findById()`, `findAll()`, `save()`, and `delete()`. 
But what if you need to fetch a user exactly by their specific Email Address?

## The Magic of Method Naming

Spring Data JPA employs a revolutionary architectural concept called **Derived Query Methods**.

Instead of writing a raw SQL query string inside the Java code, you simply **type an empty English sentence as a method signature**. Spring's internal engine physically parses the exact English words you used during startup, structurally maps them to the underlying database schema fields, and dynamically generates the perfect native SQL mathematically and securely!

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. Basic Exact Match
    // Spring translates to: SELECT * FROM user WHERE email = ?
    Optional<User> findByEmail(String email);

    // 2. Multiple Conditions (AND / OR)
    // Translates to: SELECT * FROM user WHERE first_name = ? AND last_name = ?
    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    // 3. Mathematical Conditionals
    // Translates to: SELECT * FROM user WHERE age > ?
    List<User> findByAgeGreaterThan(int baselineAge);

    // 4. Wildcards (LIKE)
    // Translates to: SELECT * FROM user WHERE email LIKE ?
    List<User> findByEmailContaining(String domain);

    // 5. Ordering
    List<User> findByLastNameOrderByFirstNameAsc(String lastName);
}
```

## The Strict Rules of English
Spring Boot is completely merciless about spelling. If your Entity has a private attribute named `emailAddress`, but you accidentally type `findByEmail(String email)` in the Repository, Spring will violently crash at application startup time with a `PropertyReferenceException` screaming: "No property 'email' found for type 'User'!"

You must match the exact camelCase field names natively and flawlessly.

## Custom Queries (`@Query`)

If a Derived Query Method name becomes too long (e.g., `findByFirstNameAndLastNameAndAgeGreaterThanOrderByLastNameDesc`), it becomes unreadable. In these cases, you can write pure explicit queries using JPQL (Java Persistence Query Language) or raw SQL:

```java
@Query("SELECT u FROM User u WHERE u.age > ?1 AND u.firstName = ?2")
List<User> findComplexUsers(int age, String firstName);

// Native Raw SQL
@Query(value = "SELECT * FROM users WHERE email LIKE '%@gmail.com'", nativeQuery = true)
List<User> findGmailUsersNative();
```
