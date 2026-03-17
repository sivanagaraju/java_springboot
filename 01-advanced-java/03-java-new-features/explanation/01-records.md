# Records — Immutable Data Classes (Java 16+)

## The Problem Records Solve

```
BEFORE Records (Java 8):                    AFTER Records (Java 16):
┌──────────────────────────────────────┐    ┌──────────────────────────┐
│ public class User {                   │    │ public record User(      │
│   private final String name;          │    │     String name,         │
│   private final int age;              │    │     int age              │
│                                       │    │ ) {}                     │
│   public User(String name, int age) { │    └──────────────────────────┘
│     this.name = name;                 │    
│     this.age = age;                   │    That's it! You get ALL of
│   }                                   │    the left side for FREE:
│                                       │    ✅ Constructor
│   public String name() { return name; }│   ✅ Getters (name(), age())
│   public int age() { return age; }    │    ✅ equals()
│                                       │    ✅ hashCode()
│   @Override                           │    ✅ toString()
│   public boolean equals(Object o) {   │    ✅ Immutability (final)
│     // 10+ lines...                   │
│   }                                   │
│   @Override                           │
│   public int hashCode() { ... }       │
│   @Override                           │
│   public String toString() { ... }    │
│ }                                     │
│ ~40 lines of boilerplate!             │
└──────────────────────────────────────┘
```

---

## 1. Record Basics

```java
// Declaration
public record User(String name, int age, String email) {}

// Usage
User user = new User("Alice", 25, "alice@mail.com");
user.name();      // "Alice"  (NOT getName()!)
user.age();       // 25
user.toString();  // User[name=Alice, age=25, email=alice@mail.com]

// Value equality
User u1 = new User("Alice", 25, "alice@mail.com");
User u2 = new User("Alice", 25, "alice@mail.com");
u1.equals(u2);    // true (compares ALL fields)
u1.hashCode() == u2.hashCode();  // true

// Immutable
user.name = "Bob";  // ❌ Compile error! Fields are final.
```

---

## 2. Custom Constructors & Validation

```java
public record Product(String name, double price, int quantity) {

    // Compact constructor — validation only, no field assignment
    public Product {
        if (price < 0) throw new IllegalArgumentException("Price < 0");
        if (quantity < 0) throw new IllegalArgumentException("Qty < 0");
        name = name.strip();  // transform before assignment
    }

    // Additional constructors must delegate to canonical
    public Product(String name, double price) {
        this(name, price, 0);  // default quantity = 0
    }

    // Custom methods are allowed
    public double totalValue() {
        return price * quantity;
    }
}
```

---

## 3. Records vs Lombok vs POJO

```
┌──────────────────┬──────────┬──────────┬──────────────────┐
│ Feature          │ Record   │ @Value   │ POJO             │
├──────────────────┼──────────┼──────────┼──────────────────┤
│ Immutable        │ ✅ Always│ ✅ Always│ Manual (final)   │
│ Boilerplate      │ ✅ Zero  │ ✅ Zero  │ ❌ 40+ lines     │
│ Inheritance      │ ❌ No    │ ❌ No    │ ✅ Yes           │
│ Mutable fields   │ ❌ No    │ ❌ No    │ ✅ Yes           │
│ JPA Entity       │ ❌ No    │ ❌ No    │ ✅ Yes           │
│ Spring DTO       │ ✅ Great │ ✅ Great │ ✅ Works         │
│ Dependency       │ None     │ Lombok   │ None             │
│ Method names     │ name()   │ getName()│ getName()        │
└──────────────────┴──────────┴──────────┴──────────────────┘

Use Records for: DTOs, value objects, API responses, config properties.
Use POJO for: JPA entities (need mutable state + default constructor).
```

---

## 🎯 Interview Questions

**Q1: Can a record extend another class?**
> No. Records implicitly extend `java.lang.Record` and Java doesn't support multiple inheritance. Records can implement interfaces though — this is the recommended way to add behavior.

**Q2: Can you use records as JPA entities?**
> Not directly. JPA requires a no-arg constructor and mutable state (setters) for proxying. Records are final and immutable. Use records for DTOs (data transfer between layers), not entities.

**Q3: How do records compare to Python's `@dataclass`?**
> Both auto-generate boilerplate (constructor, equality, repr). But records are always immutable (like `@dataclass(frozen=True)`), have no inheritance, and use `name()` instead of `.name` attribute access.
