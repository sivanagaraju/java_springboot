# Strings & Arrays — One-Page Cheat Sheet

## String Key Methods

```java
String s = "Hello, World!";
s.length()                      // 13
s.charAt(0)                     // 'H'
s.substring(7)                  // "World!"
s.substring(7, 12)              // "World"  [7,12)
s.indexOf("World")              // 7, returns -1 if not found
s.lastIndexOf("l")              // 10
s.contains("World")             // true
s.startsWith("Hello")           // true
s.endsWith("!")                 // true
s.replace("World", "Java")      // "Hello, Java!"
s.replaceAll("[aeiou]", "*")    // regex replace
s.toLowerCase() / toUpperCase()
s.trim()                        // remove leading/trailing whitespace
s.strip()                       // Unicode-aware trim (Java 11+)
s.split(", ")                   // String[] {"Hello", "World!"}
s.split(", ", 2)                // limit splits
s.join("-", "a", "b", "c")     // "a-b-c" (static method)
String.format("%.2f", 3.14159) // "3.14"
s.isEmpty()                     // length == 0
s.isBlank()                     // all whitespace (Java 11+)
s.chars()                       // IntStream of char values
s.toCharArray()                 // char[]
```

## StringBuilder

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello").append(", ").append("World");
sb.insert(5, " Beautiful");
sb.delete(5, 15);
sb.reverse();
sb.length();
sb.charAt(0);
sb.setCharAt(0, 'h');
sb.toString();  // convert to String

// Use StringBuilder for loops:
for (String s : list) { sb.append(s).append(","); }  // O(n)
// NOT: String s = ""; for (...) s += item;          // O(n²)!
```

## Arrays

```java
int[] arr = {5, 3, 1, 4, 2};
Arrays.sort(arr);                      // in-place sort, O(n log n)
Arrays.sort(arr, 1, 4);               // sort range [1, 4)
int idx = Arrays.binarySearch(arr, 3); // after sort — O(log n)
int[] copy = Arrays.copyOf(arr, 5);    // copy first 5 elements
int[] range = Arrays.copyOfRange(arr, 1, 4);  // [1, 4)
Arrays.fill(arr, 0);                   // fill all with 0
Arrays.equals(arr1, arr2);             // deep value comparison
Arrays.deepEquals(2dArr1, 2dArr2);    // 2D array comparison
System.arraycopy(src, 0, dest, 0, len); // fast bulk copy

// 2D arrays:
int[][] matrix = new int[3][4];        // 3 rows, 4 columns
int[][] jagged = {{1,2}, {3,4,5}, {6}};  // rows can vary in length
matrix[row][col] = 42;
matrix.length          // rows
matrix[0].length       // columns in first row
```

## Python Bridge

| Java | Python |
|------|--------|
| `s.length()` | `len(s)` |
| `s.substring(1, 3)` | `s[1:3]` |
| `s.charAt(0)` | `s[0]` |
| `s.split(",")` | `s.split(",")` |
| `String.join(",", list)` | `",".join(list)` |
| `String.format("%d", n)` | `f"{n}"` |
| `StringBuilder.append()` | List then `"".join()` |
| `Arrays.sort(arr)` | `arr.sort()` or `sorted(arr)` |
| `int[] arr = new int[5]` | `arr = [0] * 5` |
| `arr.length` | `len(arr)` |
| `Arrays.copyOf(arr, n)` | `arr[:n]` |

## Common Traps

```
TRAP 1: String concatenation in loop (O(n²))
  String s = ""; for (...) s += item;  // creates new String each time!
  Fix: StringBuilder

TRAP 2: split(".")  → splits every char (. is regex wildcard)
  Fix: split("\\.") to split on literal dot

TRAP 3: 2D array dimensions
  matrix[row][col] — row first, then column
  Common mistake: matrix[col][row]

TRAP 4: Arrays.sort() on 2D array with Comparator
  int[][] points = {{1,3},{2,1}};
  Arrays.sort(points, (a, b) -> a[0] - b[0]);  // sort by first element

TRAP 5: binarySearch on unsorted array
  Returns undefined value. Sort FIRST, then binarySearch.
```
