# Java Demo Standards — spring-mastery

All `explanation/*.java` and demo files must follow these standards.

## Contents

- File Structure (in order)
- Layer 1: File Header Template
- Layer 2: Class Javadoc Template
- Layer 3: Method Javadoc Template
- Layer 4: Inline Comment Rules
- ASCII Diagram Type Reference
- Import Order
- Java 21 Feature Usage Guide
- EXPECTED OUTPUT Comment Block
- What Immediately Fails the Standard

---

## File Structure (in order)

```
1. Layer 1 — File header javadoc (╔══╗ box)
2. Package declaration
3. Imports (stdlib → third-party → jakarta → spring)
4. Layer 2 — Class-level javadoc
5. Class declaration + annotations
6. Fields (constants first, then instance fields)
7. Constructor(s)
8. Public methods (each with Layer 3 javadoc)
9. Private methods (Layer 3 if non-trivial)
10. main() method (if standalone demo)
11. // EXPECTED OUTPUT comment block (if main() present)
```

---

## Layer 1: File Header Template

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ClassName.java                                        ║
 * ║  MODULE : XX-module-name / YY-subtopic                          ║
 * ║  GRADLE : ./gradlew :XX-module-name:run                         ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : What this file demonstrates (one sentence)    ║
 * ║  WHY IT EXISTS  : What problem existed before this feature      ║
 * ║  PYTHON COMPARE : Python/FastAPI equivalent                     ║
 * ║  USE CASES      : 3–4 real-world scenarios                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM  (must match the concept being demonstrated)      ║
 * ║                                                                   ║
 * ║    [Component A]                                                  ║
 * ║         │                                                         ║
 * ║         ▼                                                         ║
 * ║    [Component B]   ← brief label                                 ║
 * ║         │                                                         ║
 * ║         ▼                                                         ║
 * ║    [Component C]                                                  ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :module:run (or :bootRun for Spring) ║
 * ║  EXPECTED OUTPUT: What you see in the console                   ║
 * ║  RELATED FILES  : OtherClass.java, 01-topic.md                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
```

The ASCII diagram must show the concept being demonstrated — not a generic box.
For a JDBC demo: show the connection flow. For a Service class: show the layer stack.
For a design pattern: show the object relationships.

---

## Layer 2: Class Javadoc Template

```java
/**
 * [ClassName] — [one-sentence responsibility description].
 *
 * <p>[2–3 sentences explaining what this class does, what it owns,
 * and what it delegates.]</p>
 *
 * <p><b>Python FastAPI equivalent:</b>
 * <pre>
 *   class [PythonEquivalent]:
 *       def __init__(self, dep: Dependency = Depends()):
 *           self.dep = dep
 * </pre>
 *
 * <p><b>ASCII — Architecture position:</b>
 * <pre>
 *   [Caller]
 *       │
 *       ▼
 *   [This Class]   ← YOU ARE HERE
 *       │
 *       ▼
 *   [Downstream]
 * </pre>
 */
```

---

## Layer 3: Method Javadoc Template

```java
/**
 * [Short imperative description of what this method does.]
 *
 * <p>[2–3 sentences: contract, preconditions, what happens on error.]</p>
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   async def method_name(param: Type) -> ReturnType:
 *       ...
 * </pre>
 *
 * <p><b>Flow (for non-trivial methods):</b>
 * <pre>
 *   methodName(param)
 *       │
 *       ▼
 *   [Step 1] ──→ condition? → throw SomeException
 *       │
 *       ▼
 *   [Step 2]
 *       │
 *       ▼
 *   return result
 * </pre>
 *
 * @param paramName  Description (include valid range if applicable)
 * @return           Description of the return value
 * @throws SomeException  When and why this is thrown
 */
```

For trivial getters/setters: a single-line javadoc `/** Returns the product name. */` is fine.
Layer 3 full treatment is for every public business method with meaningful logic.

---

## Layer 4: Inline Comment Rules

Add inline comments ONLY for:
- Non-obvious business rules: `// BCrypt factor 12 = ~250ms — high enough to slow brute force`
- Gotchas: `// JPA dirty checking: mutating entity auto-generates UPDATE at flush — no explicit save() needed`
- WHY not HOW: `// REQUIRES_NEW: audit must commit even if outer transaction rolls back`
- Performance decisions: `// readOnly=true disables Hibernate dirty checking — saves CPU on read-heavy endpoints`

NEVER add inline comments for obvious code:
```java
// BAD — obvious
i++;          // increment i
list.clear(); // clear the list
return result; // return the result

// GOOD — explains why, not what
// @Version enables optimistic locking: if another thread committed first,
// version won't match and Hibernate throws OptimisticLockException
@Version
private Long version;
```

---

## ASCII Diagram Type Reference

| Concept | ASCII Pattern | Characters |
|---------|--------------|-----------|
| Request pipeline / algorithm flow | Vertical flow | `│ ▼` |
| Architecture layers | Stack with labels | `[ Layer ]` |
| Decision / branching | Decision tree | `──→ condition? → result` |
| Entity state transitions | State machine | `State1 ──→ State2` |
| Comparison table | Aligned columns | Header + `---` separator |
| Component boundaries | Box drawing | `┌─────┐ │ └─────┘` |

---

## Import Order

```java
// 1. Java standard library
import java.util.List;
import java.time.LocalDateTime;
import java.math.BigDecimal;

// 2. Third-party (non-Spring, non-Jakarta)
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Data;

// 3. Jakarta EE (never javax.*)
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

// 4. Spring Framework
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
```

---

## Java 21 Feature Usage Guide

Use these where they genuinely improve the code — do not force them:

```java
// Records for DTOs and value objects (replaces Lombok @Data + constructor boilerplate)
public record CreateProductRequest(
    @NotBlank String name,
    @Min(0) BigDecimal price,
    @Min(0) int stockQuantity
) {}

// Sealed interfaces for domain hierarchies
public sealed interface PaymentResult
    permits PaymentResult.Success, PaymentResult.Failure, PaymentResult.Pending {}

// Switch expressions (not statements) for mapping
String statusLabel = switch (order.getStatus()) {
    case PENDING   -> "Awaiting payment";
    case CONFIRMED -> "Order confirmed";
    case SHIPPED   -> "In transit";
    case DELIVERED -> "Delivered";
};

// Text blocks for SQL, JSON, error messages
String sql = """
    SELECT p.*, c.name AS category_name
    FROM products p
    JOIN categories c ON p.category_id = c.id
    WHERE p.price > :minPrice
    ORDER BY p.price DESC
    """;

// Pattern matching instanceof (replaces cast)
if (exception instanceof DataIntegrityViolationException dive) {
    String constraint = dive.getMostSpecificCause().getMessage();
    throw new DuplicateProductException(constraint);
}
```

---

## EXPECTED OUTPUT Comment Block

Add this at the end of every `main()` method:

```java
public static void main(String[] args) {
    // ... demo code ...

    /*
     * EXPECTED OUTPUT:
     * ================
     * Connecting to PostgreSQL at localhost:5432...
     * Connected successfully. Pool size: 10
     * Inserted product: Product{id=1, name='Laptop', price=999.99}
     * Query returned 3 products
     * Transaction committed successfully
     * Connection returned to pool
     */
}
```

---

## What Immediately Fails the Standard

| Failure | Layer violated |
|---------|---------------|
| No ╔══╗ file header | Layer 1 |
| File header missing ASCII diagram | Layer 1 |
| Class has no javadoc | Layer 2 |
| Class javadoc missing Python equivalent | Layer 2 |
| Public method with no javadoc | Layer 3 |
| Javadoc missing @param for documented params | Layer 3 |
| Inline comment explains WHAT not WHY | Layer 4 |
| `javax.persistence` instead of `jakarta.persistence` | Stack constraint |
| Maven commands in comments | Stack constraint |
| No EXPECTED OUTPUT block in main() | Demo standard |
