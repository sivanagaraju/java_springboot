# Java New Features — One-Page Cheat Sheet (Java 16–21)

## Records

```java
// Declaration
public record User(String name, int age) {}

// Auto-generated: constructor, name(), age(), equals(), hashCode(), toString()
// Fields: private final (immutable!)

// Compact constructor for validation:
public record User(String name, int age) {
    public User {  // no parameter list — uses the record's params
        Objects.requireNonNull(name, "name required");
        if (age < 0) throw new IllegalArgumentException("age must be >= 0");
        // Parameters auto-assigned AFTER this block
    }
}

// Custom accessor (override the auto-generated one):
public String name() { return name.trim(); }
```

## Sealed Classes

```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double w, double h) implements Shape {}
final class Triangle implements Shape { ... }  // non-record permitted subtype

// Exhaustive switch — NO default needed:
double area = switch (shape) {
    case Circle c    -> Math.PI * c.radius() * c.radius();
    case Rectangle r -> r.w() * r.h();
    case Triangle t  -> t.base() * t.height() / 2.0;
};
```

## Pattern Matching

```java
// instanceof pattern (Java 16):
if (obj instanceof String s && s.length() > 5) { ... }

// Switch pattern (Java 21):
return switch (obj) {
    case String s when s.isEmpty() -> "empty string";
    case String s                  -> s.toUpperCase();
    case Integer i                 -> i * 2;
    case null                      -> "null!";
    default                        -> obj.toString();
};
```

## Text Blocks & Switch Expression

```java
// Text block (Java 15+):
String sql = """
    SELECT *
    FROM users
    WHERE id = ?
    """;  // trailing """ sets indent base

// Switch expression (Java 14+):
String label = switch (day) {
    case MON, TUE, WED, THU, FRI -> "Weekday";
    case SAT, SUN                 -> "Weekend";
};  // exhaustive for enums — no default
```

## Virtual Threads (Java 21)

```java
// Create virtual thread:
Thread.ofVirtual().start(() -> doWork());

// With Spring Boot 3.2:
// application.properties: spring.threads.virtual.enabled=true
// No code changes needed!

// ExecutorService with virtual threads:
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (var task : tasks) executor.submit(task);
}  // structured concurrency — waits for all tasks
```

## Python Bridge

| Java (Java 21) | Python |
|---|---|
| `record User(String name, int age)` | `@dataclass(frozen=True) class User` |
| `sealed interface + permits` | `Union[A, B]` type hint (no runtime enforcement) |
| `case null` in switch | `case None:` in `match` |
| Text block `"""..."""` | Triple-quoted string (same syntax!) |
| `Thread.ofVirtual()` | `asyncio.create_task()` (but blocking code, no await) |
| `var x = new ArrayList<>()` | Normal Python (dynamic type) |

## Common Traps

```
TRAP 1: Record accessor naming
  record User(String name)  → accessor is user.name() NOT user.getName()
  Lombok: user.getName()     → different convention!

TRAP 2: default in sealed switch
  Defeats exhaustiveness checking — compiler won't warn about missing cases
  Fix: remove default from sealed-type switches

TRAP 3: Compact constructor — fields assigned AFTER body
  In compact constructor: name = name.trim() WORKS (you're modifying the parameter)
  But: this.name = name.trim() WRONG — 'this.name' doesn't exist yet

TRAP 4: Virtual threads + ThreadLocal
  ThreadLocal works but is not automatically cleared between tasks on carrier threads
  Use Scoped Values (JEP 446) for clean value propagation

TRAP 5: synchronized blocks pin virtual threads to carrier threads
  Avoid synchronized in virtual-thread code — use ReentrantLock instead
  Spring 3.2 virtual thread support works best with lock-free or ReentrantLock code
```
