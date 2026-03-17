# Text Blocks & Enhanced Switch — Clean Syntax (Java 13+)

## 1. Text Blocks

```
BEFORE (Java 8):
┌──────────────────────────────────────────────────────┐
│ String sql = "SELECT u.name, u.email\n"             │
│            + "FROM users u\n"                        │
│            + "JOIN orders o ON u.id = o.user_id\n"   │
│            + "WHERE o.total > 100.0";                │
│                                                       │
│ String json = "{\n"                                   │
│             + "  \"name\": \"John\",\n"              │
│             + "  \"age\": 25\n"                       │
│             + "}";                                    │
└──────────────────────────────────────────────────────┘
  Painful: escape quotes, explicit \n, concat with +

AFTER (Java 15+):
┌──────────────────────────────────────────────────────┐
│ String sql = """                                      │
│     SELECT u.name, u.email                            │
│     FROM users u                                      │
│     JOIN orders o ON u.id = o.user_id                 │
│     WHERE o.total > 100.0                             │
│     """;                                              │
│                                                       │
│ String json = """                                     │
│     {                                                 │
│       "name": "John",                                 │
│       "age": 25                                       │
│     }                                                 │
│     """;                                              │
└──────────────────────────────────────────────────────┘
  Clean: no escaping, natural formatting, auto-indentation
```

### Indentation Rules

```
Text block indentation is relative to closing """:

    String s = """           ← opening
        Hello                ← 4 spaces relative to closing
        World                ← 4 spaces relative to closing
        """;                 ← closing determines base indent

Result: "    Hello\n    World\n"

    String s = """
    Hello
    World
    """;                     ← closing at same level

Result: "Hello\nWorld\n"     ← no leading spaces!
```

---

## 2. Enhanced Switch (Java 14+)

```
BEFORE (statement switch):          AFTER (expression switch):
┌──────────────────────────┐       ┌──────────────────────────┐
│ String result;            │       │ String result = switch    │
│ switch (day) {            │       │   (day) {                 │
│   case MON:               │       │   case MON, FRI  → "Work";│
│   case FRI:               │ →     │   case SAT, SUN  → "Rest";│
│     result = "Work";      │       │   default → "Midweek";    │
│     break;  // forget = bug│      │ };                        │
│   case SAT:               │       └──────────────────────────┘
│   case SUN:               │       ✅ No fall-through
│     result = "Rest";      │       ✅ Returns value
│     break;                │       ✅ Multiple labels
│   default:                │       ✅ Exhaustive check
│     result = "Midweek";   │
│ }                         │
└──────────────────────────┘
```

```java
// Multi-line cases with yield:
int numLetters = switch (day) {
    case MON, FRI, SUN -> 6;
    case TUE            -> 7;
    case THU, SAT       -> 8;
    case WED -> {
        System.out.println("Wednesday!");
        yield 9;  // yield for multi-line blocks
    }
};
```

---

## 3. Other Useful Features

```java
// var (Java 10) — local type inference
var users = new ArrayList<String>();  // compiler infers ArrayList<String>
var count = users.size();             // infers int

// Collection factories (Java 9) — immutable!
var list = List.of("a", "b", "c");     // immutable List
var set  = Set.of(1, 2, 3);           // immutable Set
var map  = Map.of("key", "value");    // immutable Map

// String methods (Java 11)
"  hello  ".strip();      // "hello" (Unicode-aware trim())
"  ".isBlank();            // true
"abc\ndef".lines().count(); // 2
"ha".repeat(3);            // "hahaha"

// Helpful NullPointerException (Java 14)
// Before: NullPointerException at line 42
// After:  Cannot invoke "String.length()" because "user.getAddress().getCity()" is null
```

---

## 🎯 Interview Questions

**Q1: Text blocks vs string concatenation — when to use?**
> Text blocks for multi-line content (SQL, JSON, HTML, XML, error messages). Regular strings for single-line values. Text blocks strip common leading whitespace automatically based on closing `"""` position.

**Q2: Switch expression vs switch statement — what changed?**
> Switch expressions return a value, use `->` syntax to prevent fall-through, support multiple labels per case, and require exhaustiveness. `yield` replaces `return` inside multi-line blocks. Switch statements remain for backward compatibility.

**Q3: Why is `var` not the same as dynamic typing?**
> `var` is compile-time type inference — the compiler deduces the type and enforces it. `var x = "hello"; x = 42;` is a compile error. Python's variables are dynamically typed — they can change type at runtime. `var` reduces typing, not type safety.
