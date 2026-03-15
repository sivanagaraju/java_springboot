# 02 - Constructors

> **Python Bridge:** In Python, you initialize state using the `__init__(self)` dunder method. In Java, initialization is handled by **Constructors**, which are special methods that share the exact same name as the Class and have *no return type* (not even `void`).

## What is a Constructor?

A constructor's only job is to properly set up the initial state of an object immediately after the `new` keyword allocates heap memory.

### Three Constructor Types

1. **Default (No-Arg) Constructor:** Takes no arguments. If you write zero constructors in your class, Java secretly generates an empty default constructor for you. 
2. **Parameterized Constructor:** Takes arguments to initialize fields with specific values. If you write *any* parameterized constructor, Java **stops** generating the default one.
3. **Copy Constructor:** Takes an instance of the same class and copies its values (Java's alternative to Python's `copy` module).

## Constructor Chaining (`this()`)

Java heavily utilizes method overloading (multiple methods with the same name but different parameters). Constructors can be overloaded too. To prevent code duplication, constructors can call other constructors inside the same class using `this()`. 

> **Rule:** `this()` must be the very first line in the constructor body.

### Diagram: Constructor Chaining Workflow

```mermaid
sequenceDiagram
    participant Main
    participant Constructor(String, String)
    participant Constructor(String, String, int)

    Main->>Constructor(String, String): new User("sivan", "sivan@test.com")
    note over Constructor(String, String): Calls this(user, email, 18)
    Constructor(String, String)->>Constructor(String, String, int): this() call
    note over Constructor(String, String, int): Performs actual field assignment
    Constructor(String, String, int)-->>Constructor(String, String): Return
    Constructor(String, String)-->>Main: Object Init Complete
```

## Python `__init__` vs Java Constructor

**Python:**
```python
class User:
    # Python uses default arguments instead of overloading
    def __init__(self, name, age=18):
        self.name = name
        self.age = age

user1 = User("Sivan")
user2 = User("Alex", 25)
```

**Java:**
```java
public class User {
    String name;
    int age;

    // Overload 1 (Java doesn't have default parameters natively)
    public User(String name) {
        this(name, 18); // Chains to Overload 2
    }

    // Overload 2
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

User user1 = new User("Sivan");
User user2 = new User("Alex", 25);
```

---

## Interview Questions

### Conceptual
**Q: Is a constructor a method?**
A: Technically, no. Constructors are special routines. They do not have a return type, cannot be overridden, and are only called during object instantiation alongside the `new` keyword.

**Q: Can a constructor return a value?**
A: No. It cannot even specify `void`. If you write `public void User() {}`, it ceases to be a constructor and becomes a normal method that happens to be named "User" (which is universally considered terrible practice).

### Scenario / Debug
**Q: You write a class with `public Car(String model) { this.model = model; }`. In another file, you call `new Car();`. Will it compile?**
A: **No.** Because you provided a parameterized constructor, the Java compiler retracts the free, invisible default constructor. You must explicitly write `public Car() {}` if you still want to allow no-argument instantiation.

### Quick Fire
- **What keyword restricts a constructor so the class can't be instantiated from the outside?** `private` (this is the core of the Singleton design pattern!).
- **Can `this()` be the second line of a constructor?** No, it must be the absolute first statement.
