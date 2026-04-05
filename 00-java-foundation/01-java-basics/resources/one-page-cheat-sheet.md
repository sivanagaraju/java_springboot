# Java Basics — One-Page Cheat Sheet

## Primitive Types

```
byte    8-bit  −128 to 127              (Python: int, but unlimited)
short   16-bit −32,768 to 32,767
int     32-bit −2,147,483,648 to 2,147,483,647  ← default integer type
long    64-bit                          (append L: 100L)
float   32-bit                          (append f: 3.14f)
double  64-bit                          ← default decimal type
char    16-bit Unicode character        (single quotes: 'A')
boolean true/false
```

## Type Conversion

```
Widening (automatic):    byte → short → int → long → float → double
Narrowing (explicit):    (int) 3.14 → 3  (truncates, NOT rounds!)

int    → String:  String.valueOf(n)  or  "" + n
String → int:     Integer.parseInt("42")
int    → Integer: autoboxing (automatic)
Integer → int:    unboxing (automatic, can NPE if null!)
```

## Operators

```
Arithmetic:   +  -  *  /  %
  int / int = int (integer division!)  7 / 2 = 3 (NOT 3.5)
  7.0 / 2 = 3.5  ← one double makes result double

Comparison:   ==  !=  <  >  <=  >=
  NEVER use == for String comparison! Use .equals()
  == for primitives: value comparison ✅
  == for objects: reference comparison (pointer) ❌ use .equals()

Ternary:    condition ? valueIfTrue : valueIfFalse
Short-circuit: &&  ||  (right side not evaluated if result already known)
```

## Control Flow

```java
// switch expression (Java 14+) — preferred:
String label = switch (day) {
    case MON, TUE, WED, THU, FRI -> "Weekday";
    case SAT, SUN                 -> "Weekend";
};

// Enhanced for:
for (String item : list) { }      // for-each

// break/continue work the same as Python
// labels for breaking nested loops: outerLoop: for (...) { break outerLoop; }
```

## String Key Methods

```
s.length()                → int length
s.charAt(i)               → char at index
s.substring(from, to)     → [from, to)
s.indexOf("sub")          → first index, -1 if not found
s.contains("sub")         → boolean
s.startsWith / endsWith   → boolean
s.trim()                  → remove leading/trailing whitespace
s.strip()                 → trim() but Unicode-aware (Java 11+)
s.toLowerCase() / toUpperCase()
s.replace("old", "new")
s.split("regex")          → String[]
s.equals("other")         → case-sensitive comparison
s.equalsIgnoreCase("other")
String.valueOf(42)        → "42"
Integer.parseInt("42")    → 42
```

## Python Bridge

| Java | Python |
|------|--------|
| `int a = 5;` | `a = 5` |
| `String s = "hello";` | `s = "hello"` |
| `s.length()` | `len(s)` |
| `s.substring(1, 3)` | `s[1:3]` |
| `"hello".equals(s)` | `"hello" == s` |
| `Integer.parseInt("42")` | `int("42")` |
| `String.valueOf(42)` | `str(42)` |
| `System.out.println()` | `print()` |
| `Scanner.nextLine()` | `input()` |
| `int x = (int) 3.14` | Python auto-truncates with `int(3.14)` |

## Common Traps

```
TRAP 1: int division
  double avg = sum / count;  ← if both int, result is int (3, not 3.5)
  Fix: (double) sum / count  or  sum * 1.0 / count

TRAP 2: == on Strings
  String a = new String("hello"); String b = new String("hello");
  a == b → false (different objects!)
  a.equals(b) → true ✅

TRAP 3: Integer cache (-128 to 127)
  Integer a = 127; Integer b = 127; a == b → true (cached!)
  Integer a = 128; Integer b = 128; a == b → false (not cached!)
  Always use .equals() for Integer comparison.

TRAP 4: Overflow
  int max = Integer.MAX_VALUE; max + 1 → -2147483648 (wraps around!)
  Use long for numbers that might exceed 2 billion.

TRAP 5: Unboxing null
  Integer n = null; int x = n;  → NullPointerException!
```
