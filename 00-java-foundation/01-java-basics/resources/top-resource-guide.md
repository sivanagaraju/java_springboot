# Top Resource Guide — Java Basics

## Official Documentation

- **[Java Language Specification — Types](https://docs.oracle.com/javase/specs/jls/se21/html/jls-4.html)** — Authoritative spec for primitive types, widening/narrowing conversions
- **[String Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/String.html)** — Complete String API reference. Every Java developer needs this bookmarked.
- **[Integer Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/Integer.html)** — MAX_VALUE, MIN_VALUE, parseInt, valueOf, cache behavior

## Books (Ordered by Priority)

1. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 61: Prefer primitive types to boxed primitives
   - Item 67: Optimize judiciously (BigDecimal cost)

2. **Head First Java, 3rd Edition** — Sierra & Bates
   - Best visual introduction to Java types and primitives for Python developers

3. **Java: The Complete Reference** — Herbert Schildt
   - Chapter 3 (Data Types) and Chapter 5 (Control Statements) for complete reference

## Blog Posts

- **[Baeldung: Java String Pool](https://www.baeldung.com/java-string-pool)** — String interning, pool, and == vs equals
- **[Baeldung: Java Integer Cache](https://www.baeldung.com/java-integer-cache)** — The -128 to 127 caching behavior explained with JVM internals
- **[Baeldung: BigDecimal vs double](https://www.baeldung.com/java-bigdecimal-biginteger)** — Why doubles fail for money

## Videos

- **[Amigoscode: Java for Beginners](https://www.youtube.com/@amigoscode)** — Full Java basics course, well-paced for Python developers
- **[Java Brains: Java Fundamentals](https://www.youtube.com/@JavaBrains)** — Clear explanations of primitives, types, OOP foundations

## Key Concepts to Verify You Know

- [ ] Why `int / int` gives integer division and how to force floating-point
- [ ] The Integer cache range (-128 to 127) and why `==` on boxed integers is unsafe
- [ ] String pool interning — when `==` works and when it doesn't
- [ ] Overflow behavior and when to use `long` vs `int`
- [ ] `BigDecimal` for money — scale, precision, `setScale(2, RoundingMode.HALF_UP)`
- [ ] Autoboxing NPE: `Integer n = null; int x = n;` → NullPointerException
