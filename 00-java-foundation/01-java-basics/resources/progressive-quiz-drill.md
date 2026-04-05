# Progressive Quiz Drill — Java Basics

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** What does `7 / 2` evaluate to in Java? What about `7.0 / 2`? Why is this different from Python?

**Q2.** A Spring Boot service stores user IDs as `int`. A senior engineer says "use `long`." Why?

**Q3.** Explain why `new String("hello") == new String("hello")` returns `false` but `"hello" == "hello"` may return `true`. What's the underlying mechanism?

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** You have a `Double` object that might be null. You need to format it as a price string. Write the full null-safe conversion chain.

**Q5.** A junior writes: `if (status == "ACTIVE") {...}`. It works in unit tests but fails in production. Diagnose and fix.

**Q6.** A `for` loop is supposed to print 0–9 but prints 0–8. The loop is `for (int i = 0; i < 9; i++)`. Why? What subtle assumption about < vs <= is the issue?

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code: `long total = 1000000 * 1000000;`. `total` is -727379968. Why? Fix it.

**Q8.** Code: `Integer x = 128; Integer y = 128; System.out.println(x == y);` prints `false`. But the same code with 127 prints `true`. Explain completely.

**Q9.** Code: `String result = 1 + 2 + " items";`. Expected: "3 items". Actual: "3 items". Now try `" items" + 1 + 2`. What's the result and why?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're reviewing a financial service that accumulates transaction amounts using `double`. A Staff Engineer rejects the PR. Why is `double` inappropriate for money? What should be used instead, and what are the performance trade-offs?

---

## Answer Key

**A1.** `7 / 2 = 3` (integer division — truncates). `7.0 / 2 = 3.5` (double). Python 3 always uses true division for `/` and integer division for `//`. Java uses the type of the operands — both int means integer division. Fix: cast one operand: `(double) 7 / 2`.

**A2.** User IDs grow over time. `int` max is ~2.1 billion. A large system (Meta, Amazon) can have billions of users. `long` max is ~9.2 × 10¹⁸ — effectively unlimited. Also, database auto-increment IDs are typically BIGINT (64-bit), matching `long`.

**A3.** String literals go into the String Pool (interned). `"hello" == "hello"` compares two references to the SAME pool entry — returns `true`. `new String("hello")` bypasses the pool, allocates a new heap object each time. Same characters, different references — `==` returns `false`. Always use `.equals()` for semantic equality.

**A4.** `String formatted = (price != null) ? String.format("$%.2f", price) : "N/A";` or with Optional: `Optional.ofNullable(price).map(p -> String.format("$%.2f", p)).orElse("N/A")`.

**A5.** `status` in production is likely returned from a database query or HTTP parameter — a new String object, not a literal. `==` compares references. Fix: `"ACTIVE".equals(status)` (put the literal first to prevent NPE if status is null).

**A6.** The condition `i < 9` stops before 9, printing 0,1,2,3,4,5,6,7,8 — 9 values, not 10. Change to `i < 10` or `i <= 9`. Common off-by-one: think "less than SIZE" vs "less than or equal to MAX_INDEX".

**A7.** `1000000 * 1000000` is computed as `int * int = int` (Java evaluates the right side before assigning to `long`). Result overflows int (~2.1B max), wraps to negative. Fix: `long total = 1_000_000L * 1_000_000L;` — the `L` suffix makes one operand `long`, promoting the entire expression.

**A8.** Java caches Integer objects for values -128 to 127 (the Integer Cache). `Integer.valueOf(127)` returns the same cached instance, so `==` works. `Integer.valueOf(128)` creates a new object each time — two different references, `==` is `false`. This is why `==` on boxed integers is unreliable. Always `.equals()`.

**A9.** `1 + 2 + " items"` = `3 + " items"` = `"3 items"` (left-to-right: int+int first, then string concat). `" items" + 1 + 2` = `" items1" + 2` = `" items12"` (string+int from left). Operator `+` is left-associative. The first string encountered converts all subsequent `+` to string concatenation.

**A10.** `double` uses binary floating point — `0.1 + 0.2 ≠ 0.3` exactly. Financial calculations with rounding errors lead to $0.01 discrepancies that compound. Use `BigDecimal` with explicit scale and `RoundingMode`. Trade-offs: `BigDecimal` is 50-100x slower and verbose. For high-frequency financial systems, some teams store amounts as `long` (in cents, no decimals) which is fast and exact.
