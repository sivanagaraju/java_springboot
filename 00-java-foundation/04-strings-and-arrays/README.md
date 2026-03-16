# 04 — Strings & Arrays

## WHY This Topic Matters

Strings are the most used objects in any Java application. And the most misunderstood. The `==` vs `.equals()` trap has caused more production bugs than almost any other Java gotcha.

Arrays are the foundation of every Collection in Java. `ArrayList` is literally an array with auto-resize. Understanding fixed-size, typed arrays first makes Collections intuitive.

## Python Comparison

| Concept | Python | Java |
|---------|--------|------|
| String type | `str` (immutable) | `String` (immutable) |
| Mutability | `str` is immutable, use `list` | `String` is immutable, use `StringBuilder` |
| Comparison | `==` compares value | `==` compares **reference**, `.equals()` compares value |
| Concat perf | `"".join(items)` is fast | `StringBuilder.append()` is fast |
| Array | `list` (dynamic, any type) | `int[]` (fixed-size, single type) |
| Array resize | `list.append()` auto | Must create new array, copy manually |

## Study Path

1. `01-strings.md` → `StringDemo.java`
2. `02-stringbuilder.md` → `StringBuilderDemo.java`
3. `03-arrays.md` → `ArraysDemo.java`
4. Complete `Ex01` and `Ex02` in `exercises/`

Review the [MINDMAP.md](MINDMAP.md) to visualize how String pool, StringBuilder, and Arrays connect.
