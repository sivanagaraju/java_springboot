# Strings & Arrays

## Strings
- Immutability
  - String objects cannot be modified after creation
  - Every "modification" creates a new object on the Heap
  - Why: thread-safety, caching, security (class loading uses Strings)
- String Pool (Intern Pool)
  - JVM maintains a pool of unique String literals in Heap (since Java 7)
  - `"hello"` checks pool first → reuses existing → saves memory
  - `new String("hello")` bypasses pool → always creates new Heap object
  - `intern()` forces pool lookup
- The `==` vs `.equals()` Trap
  - `==` compares memory addresses (reference identity)
  - `.equals()` compares character sequences (value equality)
  - Python `==` = Java `.equals()` | Python `is` = Java `==`
- Common Methods
  - charAt, substring, indexOf, contains, replace, split, trim, strip
  - toUpperCase, toLowerCase, startsWith, endsWith
  - format, formatted (Java 15+)

## StringBuilder & StringBuffer
- StringBuilder (NOT thread-safe, faster)
  - Mutable char array internally
  - append(), insert(), delete(), reverse()
  - Python equivalent: `io.StringIO` or list accumulation
- StringBuffer (Thread-safe, slower)
  - Same API as StringBuilder
  - synchronized methods → safe for multi-threaded access
- Performance
  - String concat in loop: O(n²) — creates n intermediate objects
  - StringBuilder in loop: O(n) — mutates single buffer
  - Python `"".join(list)` ≈ Java `StringBuilder`

## Arrays
- Fixed-Size Typed Containers
  - Declared with type: `int[] arr = new int[5]`
  - Size is FINAL after creation — cannot grow or shrink
  - Python `list` = dynamic, any type | Java array = fixed, single type
- Memory Layout
  - Contiguous memory block on Heap
  - Object Header + length field + element data
  - CPU cache-friendly (sequential access is fast)
- Multi-dimensional
  - 2D: `int[][] matrix = new int[3][4]`
  - Jagged: rows can have different lengths
- Arrays Utility Class
  - Arrays.sort(), Arrays.binarySearch()
  - Arrays.fill(), Arrays.copyOf()
  - Arrays.toString(), Arrays.deepToString()
  - Arrays.stream() → bridge to Stream API
