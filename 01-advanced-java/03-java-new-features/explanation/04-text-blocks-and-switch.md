# Text Blocks & Enhanced Switch — Clean Syntax (Java 13+)

## Diagram: Enhanced Switch Expression Forms

```mermaid
flowchart TD
    A["switch expression\n(Java 14+ — returns value)"] --> B{"Arrow or\nColon form?"}
    B --> C["Arrow →\ncase X -> expression\nNo fall-through\nNo break needed"]
    B --> D["Colon :\ncase X: yield value;\nExplicit yield\nCan fall-through"]

    C --> E["String result = switch(day) {\n  case MON, TUE -> 'Weekday';\n  case SAT, SUN -> 'Weekend';\n};\n// Compiler checks exhaustiveness"]

    F["Text Block\nJava 13+"] --> G["\"\"\" ... \"\"\"\nTriple-quote delimiter\nLeading whitespace stripped\nEscape sequences preserved"]
    G --> H["String sql = \"\"\"\n    SELECT *\n    FROM users\n    WHERE id = ?\n    \"\"\";\n// Clean, readable, no \\n needed"]

    style E fill:#51cf66
    style H fill:#51cf66
```

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

## Python Bridge

| Java Feature | Python Equivalent |
|---|---|
| Text block `""" ... """` | Triple-quoted string `""" ... """` — identical syntax! |
| `\s` to preserve trailing space | No equivalent needed — Python preserves spaces |
| `String.formatted("Hello %s")` | `f"Hello {name}"` (f-string) |
| Switch expression `case X -> value` | No direct equivalent — Python uses dict or match |
| `switch` with multiple labels `case A, B ->` | `case 'A' | 'B':` in Python match |
| `var x = new ArrayList<String>()` | Python has dynamic typing — `var` equivalent is normal |

**Critical Difference:** Java text blocks use `"""` — the same syntax as Python triple-quoted strings. However, Java text blocks intelligently strip common leading whitespace (incidental indentation), making indented code readable. Python triple-quoted strings preserve ALL whitespace literally — you need `textwrap.dedent()` for the same effect. Java's enhanced switch expression (returns a value) is a major improvement over Python's lack of switch — Python's `match` statement is closer but still statement-based.

---

## 🎯 Interview Questions

**Q1: Text blocks vs string concatenation — when to use?**
> Text blocks for multi-line content (SQL, JSON, HTML, XML, error messages). Regular strings for single-line values. Text blocks strip common leading whitespace automatically based on closing `"""` position.

**Q2: Switch expression vs switch statement — what changed?**
> Switch expressions return a value, use `->` syntax to prevent fall-through, support multiple labels per case, and require exhaustiveness. `yield` replaces `return` inside multi-line blocks. Switch statements remain for backward compatibility.

**Q3: Why is `var` not the same as dynamic typing?**
> `var` is compile-time type inference — the compiler deduces the type and enforces it. `var x = "hello"; x = 42;` is a compile error. Python's variables are dynamically typed — they can change type at runtime. `var` reduces typing, not type safety.
