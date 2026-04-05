# Top Resource Guide — Strings & Arrays

## Official Documentation

- **[String Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/String.html)** — Every string method with examples
- **[StringBuilder Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/lang/StringBuilder.html)** — Mutable string building
- **[Arrays Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/Arrays.html)** — sort, binarySearch, copyOf, fill, equals

## Books

1. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 63: Beware the performance of string concatenation

2. **Cracking the Coding Interview** — Gayle McDowell
   - Chapters on Strings and Arrays — directly maps to FAANG interview questions

## Blog Posts

- **[Baeldung: Java String Operations](https://www.baeldung.com/java-string)** — Comprehensive guide with all common operations
- **[Baeldung: StringBuilder vs String](https://www.baeldung.com/java-string-builder-string-buffer)** — Performance comparison with benchmarks
- **[Baeldung: 2D Arrays in Java](https://www.baeldung.com/java-arrays-guide)** — Multidimensional array operations

## Interview Practice

- **[LeetCode String Problems](https://leetcode.com/tag/string/)** — Filter by Easy/Medium, focus on: Valid Palindrome, Longest Substring Without Repeating Characters, Group Anagrams, Minimum Window Substring
- **[LeetCode Array Problems](https://leetcode.com/tag/array/)** — Rotate Array, Find Duplicate, Spiral Matrix

## Key Concepts to Verify You Know

- [ ] Why `+=` in loop is O(n²) — immutable String creates new object each time
- [ ] `split(".", -1)` vs `split(".")` — trailing empty string handling
- [ ] `substring(from, to)` — to is exclusive, from is inclusive
- [ ] `Arrays.sort()` time complexity: O(n log n) using DualPivotQuicksort for primitives, TimSort for objects
- [ ] How to sort a 2D array by first column: `Arrays.sort(points, (a, b) -> a[0] - b[0])`
- [ ] String interning and why `String.intern()` can be a memory leak in old JVMs
