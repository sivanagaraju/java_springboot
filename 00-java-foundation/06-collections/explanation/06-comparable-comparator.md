# Comparable vs Comparator — The Sorting Contract

## Python → Java Mental Map

| Python | Java |
|--------|------|
| `__lt__`, `__eq__` (dunder methods) | `Comparable<T>.compareTo(T)` |
| `key=lambda x: x.age` | `Comparator.comparing(Person::getAge)` |
| `sorted(list, key=...)` | `list.sort(Comparator)` or `Collections.sort()` |
| `functools.cmp_to_key` | Not needed — Comparator IS the key |

---

## 1. Comparable — Natural Ordering (Built-In)

```
┌──────────────────────────────────────────────────┐
│              Comparable<T> Contract               │
├──────────────────────────────────────────────────┤
│                                                  │
│  class Student implements Comparable<Student> {   │
│      int compareTo(Student other) {              │
│          return this.gpa - other.gpa;            │
│      }                                           │
│  }                                               │
│                                                  │
│  Return value meaning:                           │
│  ┌──────────┬──────────────────────────┐         │
│  │ negative │ this < other (before)    │         │
│  │ zero     │ this == other (equal)    │         │
│  │ positive │ this > other (after)     │         │
│  └──────────┴──────────────────────────┘         │
│                                                  │
│  ⚠️ TRAP: Don't use subtraction for doubles!    │
│     0.1 - 0.2 = -0.1 → truncated to 0 as int   │
│     Use Double.compare(this.gpa, other.gpa)      │
└──────────────────────────────────────────────────┘
```

### Which JDK Classes Are Comparable?

```
Already Comparable          NOT Comparable
─────────────────           ──────────────
String                      StringBuilder
Integer, Double, etc.       Arrays
LocalDate, LocalDateTime    ArrayList
BigDecimal                  HashMap
Enum                        Object
Path                        Thread
```

---

## 2. Comparator — External / Custom Ordering

```
When to use which?

┌─────────────────────────────────────────────────────────┐
│ "Do I own the class and is there ONE natural order?"    │
│                                                         │
│              YES                    NO                   │
│               │                     │                    │
│               ▼                     ▼                    │
│        Comparable             Comparator                 │
│   (inside the class)    (outside, as lambda/class)       │
│                                                         │
│   student.compareTo()    Comparator.comparing(...)       │
└─────────────────────────────────────────────────────────┘
```

### Comparator Factory Methods (Java 8+)

```java
// ── Single field ──
Comparator<Employee> byName = Comparator.comparing(Employee::getName);

// ── Chained sorting (multi-level) ──
Comparator<Employee> byDeptThenSalary = Comparator
    .comparing(Employee::getDepartment)
    .thenComparingDouble(Employee::getSalary)
    .reversed();  // highest salary first

// ── Null-safe ──
Comparator<Employee> nullSafe = Comparator
    .comparing(Employee::getManager,
               Comparator.nullsLast(Comparator.naturalOrder()));

// ── Reverse ──
Comparator<Integer> descending = Comparator.reverseOrder();
```

### Comparator Composition Diagram

```
Input: List<Employee>

Step 1: comparing(Employee::getDepartment)
        ┌────────────────────────┐
        │ Engineering < Marketing │  (String natural order)
        └────────────────────────┘

Step 2: .thenComparingDouble(Employee::getSalary)
        ┌────────────────────────────────┐
        │ Within same dept, sort by salary│
        └────────────────────────────────┘

Step 3: .reversed()
        ┌──────────────────────────────────────┐
        │ Flip entire chain: Marketing first,  │
        │ then highest salary within each dept  │
        └──────────────────────────────────────┘

Result: Marketing/$150k, Marketing/$120k, Engineering/$130k, ...
```

---

## 3. The Consistency Contract

```
⚠️ CRITICAL RULE: compareTo() must be consistent with equals()

If a.compareTo(b) == 0, then a.equals(b) should return true

VIOLATION EXAMPLE: BigDecimal
  new BigDecimal("1.0").equals(new BigDecimal("1.00"))     → false
  new BigDecimal("1.0").compareTo(new BigDecimal("1.00"))  → 0

Consequence with TreeSet:
  TreeSet uses compareTo → treats 1.0 and 1.00 as SAME
  HashSet uses equals    → treats 1.0 and 1.00 as DIFFERENT

  TreeSet<BigDecimal> tree = new TreeSet<>();
  tree.add(new BigDecimal("1.0"));
  tree.add(new BigDecimal("1.00"));
  tree.size();  // → 1  (surprise!)

  HashSet<BigDecimal> hash = new HashSet<>();
  hash.add(new BigDecimal("1.0"));
  hash.add(new BigDecimal("1.00"));
  hash.size();  // → 2
```

---

## 4. Sorting APIs Summary

```
┌─────────────────────────────────┬──────────────────────────────────┐
│ API                             │ Notes                            │
├─────────────────────────────────┼──────────────────────────────────┤
│ Collections.sort(list)          │ Uses Comparable (natural order)  │
│ Collections.sort(list, comp)    │ Uses provided Comparator         │
│ list.sort(comp)                 │ Java 8+ preferred syntax         │
│ Arrays.sort(array)              │ Dual-pivot Quicksort (primitives)│
│ Arrays.sort(array, comp)        │ TimSort (objects)                │
│ stream.sorted()                 │ Natural order                    │
│ stream.sorted(comp)             │ Custom Comparator                │
│ TreeSet / TreeMap               │ Always sorted (red-black tree)   │
└─────────────────────────────────┴──────────────────────────────────┘

Algorithm used internally:
  Primitives: Dual-Pivot QuickSort  (not stable, O(n log n))
  Objects:    TimSort               (stable, O(n log n))
  
  "Stable" = equal elements keep their original order
```

---

## 5. Spring Boot Connection

```
Spring Data JPA uses Sort + Comparator patterns:

// Repository method
Page<Employee> findAll(Pageable pageable);

// Controller
Pageable pageable = PageRequest.of(
    0, 20,
    Sort.by(Sort.Direction.DESC, "salary")
         .and(Sort.by("name"))
);

Under the hood: Spring translates Sort → SQL ORDER BY
  Sort.by("salary").descending() → ORDER BY salary DESC
```

---

## 🎯 Interview Questions

**Q1: When would you implement Comparable vs use a Comparator?**
> Implement `Comparable` when there's a single natural ordering that makes sense for the class (e.g., `String` by alphabetical order). Use `Comparator` when you need multiple sort orders, don't own the class, or need ad-hoc sorting logic.

**Q2: Why is `a - b` dangerous in compareTo for integers?**
> Integer overflow. If `a = Integer.MAX_VALUE` and `b = -1`, then `a - b` overflows to a negative value, giving the wrong ordering. Use `Integer.compare(a, b)` instead.

**Q3: What sorting algorithm does Java use internally?**
> Arrays of primitives use Dual-Pivot QuickSort (unstable, O(n log n) average). Arrays/Lists of objects use TimSort (stable, O(n log n)), which is a hybrid merge + insertion sort. Stability matters when sorting by multiple keys sequentially.
