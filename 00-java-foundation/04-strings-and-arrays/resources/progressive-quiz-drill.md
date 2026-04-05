# Progressive Quiz Drill — Strings & Arrays

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** Why is concatenating strings in a loop (`s += item`) O(n²)? What's the fix and what class should you use?

**Q2.** `"hello".split(".")` returns an empty array instead of splitting every character. Why? What's the fix?

**Q3.** You call `Arrays.binarySearch(arr, 42)` but get a wrong index. What's the most likely cause?

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** Write a method that checks if a string is a palindrome. Handle case-insensitivity and ignore non-alphanumeric characters. What's the time and space complexity?

**Q5.** Given `int[][] matrix`, write the code to rotate it 90° clockwise in-place (for an NxN matrix). Explain the index transformation.

**Q6.** A teammate uses `String.format()` to build SQL queries: `"SELECT * FROM users WHERE name = '" + name + "'"`. What's the security issue and what's the correct Spring Boot approach?

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code: `String[] parts = "a,,b".split(",");`. How many elements does `parts` have? (Hint: it's not 3.)

**Q8.** Code:
```java
int[] arr = {3, 1, 4, 1, 5};
int idx = Arrays.binarySearch(arr, 4);
```
This sometimes returns a wrong index or -1. Why?

**Q9.** Code: `System.out.println("ab".substring(1, 5))`. What happens?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're building a search autocomplete endpoint. Given a list of 1 million product names and a prefix, return the top 10 matches. Design the data structure and algorithm. What's the time complexity of each approach (brute force stream filter, sorted array + binary search, Trie)?

---

## Answer Key

**A1.** String is immutable — `s += item` creates a new String each iteration, copying all previous content. For n items of length L: O(1 + 2 + 3 + ... + n) = O(n²) copies. Fix: `StringBuilder sb = new StringBuilder(); sb.append(item);` — O(n) amortized. `String.join()` and `Collectors.joining()` also use StringBuilder internally.

**A2.** `split()` takes a regex. `.` in regex means "any character" — splits on everything, resulting in empty strings. Fix: `split("\\.")` to escape the dot as a literal character. General rule: always escape regex metacharacters: `\\.`, `\\|`, `\\+`, `\\*`, `\\(`, `\\)`.

**A3.** Most likely the array is unsorted. `Arrays.binarySearch()` requires the array to be sorted first. On an unsorted array, results are undefined (may return wrong positive index or negative value). Always `Arrays.sort(arr)` before `binarySearch()`.

**A4.** Clean approach: strip non-alphanumeric, lowercase, then two-pointer compare. `s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase()` then `left=0, right=len-1`. While `left < right`: if `s.charAt(left) != s.charAt(right)` return false, else advance. Time: O(n). Space: O(n) for cleaned string (or O(1) if you skip non-alphanumeric inline).

**A5.** For NxN clockwise 90°: `result[col][n-1-row] = matrix[row][col]` for all (row, col). In-place: transpose first (`matrix[i][j] ↔ matrix[j][i]`), then reverse each row. Transpose: `for i: for j > i: swap(matrix[i][j], matrix[j][i])`. Reverse rows: `for i: reverse(matrix[i])`. O(n²) time, O(1) space.

**A6.** SQL injection vulnerability — `name` could be `'; DROP TABLE users; --`. Fix: use Spring Data JPA repository methods (`findByName(name)`) or `JdbcTemplate` with `?` parameters: `jdbcTemplate.query("SELECT * FROM users WHERE name = ?", mapper, name)`. Never concatenate user input into SQL strings.

**A7.** `"a,,b".split(",")` returns `["a", "", "b"]` — 3 elements. The middle `""` is included. But `"a,,b,".split(",")` returns `["a", "", "b"]` — 3 elements! Trailing empty strings are discarded by default. Use `split(",", -1)` to keep trailing empty strings.

**A8.** `arr = {3, 1, 4, 1, 5}` is not sorted. `binarySearch` requires sorted input. Even sorting: `Arrays.sort(arr)` → `{1, 1, 3, 4, 5}`, then `binarySearch(arr, 4)` → 3 (correct). On unsorted input, result is undefined.

**A9.** `StringIndexOutOfBoundsException` because `"ab"` has length 2. `substring(1, 5)` requests index 5 which doesn't exist. `substring(from, to)` requires `0 <= from <= to <= length`. The to index is exclusive, so maximum valid call is `substring(0, 2)`.

**A10.** Brute force: `names.stream().filter(n -> n.startsWith(prefix)).limit(10).collect(...)`. O(n) per query — 1M comparisons. Sorted array + binary search: binary search for the first string ≥ prefix, then scan forward. O(log n + k) per query where k = results. But `String.compareTo` is slow for long strings. Trie: O(P) to find prefix node (P = prefix length), then BFS/DFS for top-10. O(P + k) per query. Best for autocomplete: Trie with top-10 cached at each node for O(P) queries. In production: use Elasticsearch or database full-text index.
