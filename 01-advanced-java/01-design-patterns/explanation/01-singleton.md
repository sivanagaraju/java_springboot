# Singleton Pattern — One Instance to Rule Them All

## The Problem It Solves

```
Without Singleton:
  new DatabaseConnection()  ← connection 1
  new DatabaseConnection()  ← connection 2
  new DatabaseConnection()  ← connection 3
  ... 1000 connections → database crashes!

With Singleton:
  DatabaseConnection.getInstance()  ← always the SAME connection pool
  DatabaseConnection.getInstance()  ← returns existing instance
  DatabaseConnection.getInstance()  ← still the same one
```

---

## 1. Five Ways to Create a Singleton

### Way 1: Eager Initialization (Simplest)

```java
public class EagerSingleton {
    // Created at class loading — thread-safe by JVM guarantee
    private static final EagerSingleton INSTANCE = new EagerSingleton();
    
    private EagerSingleton() {}  // private constructor!
    
    public static EagerSingleton getInstance() {
        return INSTANCE;
    }
}
```

```
Class Loading Timeline:
┌──────────────────────────────────────────────┐
│ JVM loads class → static field initialized   │
│ INSTANCE created ONCE, BEFORE any call       │
│ Thread-safe: JVM guarantees class loading    │
└──────────────────────────────────────────────┘
✅ Simple, thread-safe
❌ Created even if never used (wastes memory for heavy objects)
```

### Way 2: Lazy Initialization (NOT thread-safe!)

```java
public class LazySingleton {
    private static LazySingleton instance;
    
    private LazySingleton() {}
    
    public static LazySingleton getInstance() {
        if (instance == null) {          // Thread-1 checks: null
            instance = new LazySingleton(); // Thread-2 also checks: null!
        }                                // TWO INSTANCES CREATED!
        return instance;
    }
}
```

```
❌ Race condition:
Thread-1: if (null) → YES → creating...
Thread-2: if (null) → YES → creating...  ← TWO INSTANCES!
```

### Way 3: Double-Checked Locking

```java
public class DCLSingleton {
    private static volatile DCLSingleton instance;  // volatile is REQUIRED!
    
    private DCLSingleton() {}
    
    public static DCLSingleton getInstance() {
        if (instance == null) {              // First check (no lock)
            synchronized (DCLSingleton.class) {
                if (instance == null) {       // Second check (with lock)
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }
}
```

```
Why volatile? Without it:
  instance = new DCLSingleton() is actually 3 steps:
  1. Allocate memory
  2. Initialize object
  3. Assign to 'instance'
  
  JVM may reorder to: 1 → 3 → 2
  Thread-2 sees instance != null, but object not fully constructed!
  volatile prevents this reordering.
```

### Way 4: Enum Singleton (✅ RECOMMENDED)

```java
public enum DatabaseConfig {
    INSTANCE;
    
    private String url;
    
    DatabaseConfig() {
        this.url = "jdbc:mysql://localhost:3306/mydb";
    }
    
    public String getUrl() { return url; }
}

// Usage: DatabaseConfig.INSTANCE.getUrl()
```

```
✅ Thread-safe (JVM guarantee)
✅ Serialization-safe (can't create second instance)
✅ Reflection-safe (can't call enum constructor)
✅ Simplest code
```

### Way 5: Spring @Component (What You'll Actually Use)

```java
@Component  // Spring creates ONE instance by default
public class UserService {
    // Spring manages the singleton lifecycle
    // No need for private constructor or getInstance()
}
```

---

## 2. Spring Singleton vs GoF Singleton

```
┌───────────────────┬─────────────────────┬─────────────────────┐
│                   │ GoF Singleton       │ Spring Singleton    │
├───────────────────┼─────────────────────┼─────────────────────┤
│ Scope             │ Per ClassLoader     │ Per ApplicationContext│
│ Creation          │ Developer manages   │ Spring manages      │
│ Thread safety     │ Developer's job     │ Spring handles      │
│ Testing           │ Hard to mock        │ Easy with @MockBean │
│ Lifecycle         │ Lives forever       │ Container-managed   │
│ Dependencies      │ Hidden (static)     │ Injected (visible)  │
└───────────────────┴─────────────────────┴─────────────────────┘

⚠️ GoF Singleton is often an ANTI-PATTERN in modern code
   because it hides dependencies and makes testing hard.
   Spring's DI-based singleton is the correct approach.
```

---

## 🎯 Interview Questions

**Q1: Why is the enum singleton considered the best approach in Java?**
> Enums are inherently thread-safe (JVM guarantees single instance at class loading), serialization-safe (deserialization returns the same instance), and reflection-safe (you can't instantiate enums via reflection). Joshua Bloch's Effective Java recommends it as the best singleton pattern.

**Q2: What's the difference between Spring singleton scope and the GoF Singleton pattern?**
> GoF Singleton guarantees one instance per ClassLoader. Spring singleton guarantees one instance per ApplicationContext (IoC container). You can have multiple ApplicationContexts in the same JVM, each with its own "singleton" instance. Also, Spring manages lifecycle and DI; GoF requires manual static management.

**Q3: Why is `volatile` required in double-checked locking?**
> Without `volatile`, the JVM can reorder the initialization steps: allocate memory → assign reference → construct object. Another thread could see a non-null reference to an incompletely constructed object. `volatile` establishes a happens-before relationship that prevents this reordering.
