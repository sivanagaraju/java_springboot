/**
 * ====================================================================
 * FILE    : ArraysDemo.java
 * MODULE  : 04 — Strings & Arrays
 * PURPOSE : Demonstrate array memory layout, operations, and utilities
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: my_list = [1, 2, 3]          →  dynamic, any type
 *   Java:   int[] arr = {1, 2, 3};       →  fixed-size, single type
 *   Python: my_list.append(4)            →  works (grows automatically)
 *   Java:   arr.append(4)               →  DOES NOT EXIST
 *
 * ARRAY MEMORY LAYOUT (int[4]):
 *
 *   Stack                    Heap
 *   ┌─────────┐             ┌─────────────────────────────┐
 *   │ arr ref │────────────▶│ Object Header  (12 bytes)    │
 *   └─────────┘             │ ┌────────────┐              │
 *                           │ │ length = 4 │ (immutable)  │
 *                           │ ├────────────┤              │
 *                           │ │  [0] = 0   │  4 bytes     │
 *                           │ │  [1] = 0   │  4 bytes     │
 *                           │ │  [2] = 0   │  4 bytes     │
 *                           │ │  [3] = 0   │  4 bytes     │
 *                           │ ├────────────┤              │
 *                           │ │ padding    │  align to 8B │
 *                           │ └────────────┘              │
 *                           │ Total: 12 + 4 + 16 = 32 B  │
 *                           └─────────────────────────────┘
 *
 * OBJECT ARRAY vs PRIMITIVE ARRAY:
 *
 *   int[] (primitives stored inline):
 *   ┌────┬────┬────┬────┐
 *   │ 10 │ 20 │ 30 │ 40 │  ← actual values, contiguous
 *   └────┴────┴────┴────┘
 *
 *   String[] (references stored, objects elsewhere):
 *   ┌──────┬──────┬──────┐
 *   │ ref1 │ ref2 │ ref3 │  ← references (8 bytes each)
 *   └──┬───┴──┬───┴──┬───┘
 *      ▼      ▼      ▼
 *   "Hello" "World" "!"     ← actual String objects on Heap
 *
 * ====================================================================
 */
import java.util.Arrays;

public class ArraysDemo {

    public static void main(String[] args) {

        System.out.println("=== DECLARATION & INITIALIZATION ===");

        // ── Three ways to create arrays ─────────────────────────────
        //
        // Way 1: Declare + allocate (all zeros)
        //   [0, 0, 0, 0, 0]  ← JVM zeroes all slots automatically
        //
        int[] scores = new int[5];
        System.out.println("Default int[]:  " + Arrays.toString(scores));

        // Way 2: Declare + initialize with values
        //   [2, 3, 5, 7, 11]
        int[] primes = {2, 3, 5, 7, 11};
        System.out.println("Initialized:    " + Arrays.toString(primes));

        // Way 3: Default values for different types
        //   boolean[] → [false, false, ...]
        //   String[]  → [null, null, ...]
        boolean[] flags = new boolean[3];
        String[] names = new String[3];
        System.out.println("Default bool[]: " + Arrays.toString(flags));   // [false, false, false]
        System.out.println("Default str[]:  " + Arrays.toString(names));   // [null, null, null]

        System.out.println("\n=== ACCESS & ITERATION ===");

        // ── Index-based access (0-indexed, like Python) ─────────────
        //
        //   Index:  [0]  [1]  [2]  [3]  [4]
        //   Value:  [ 2]  [ 3]  [ 5]  [ 7]  [11]
        //
        //   Address calculation: base + (index × element_size)
        //   primes[2] = base + (2 × 4) = base + 8 → O(1)
        //
        System.out.println("First element:  " + primes[0]);                // 2
        System.out.println("Last element:   " + primes[primes.length - 1]); // 11

        // ── for-each loop (read-only, like Python's `for x in list`) ──
        System.out.print("for-each:       ");
        for (int p : primes) {
            System.out.print(p + " ");
        }
        System.out.println();

        // ── Python negative indexing: arr[-1] → Java: NO EQUIVALENT ──
        // Java throws ArrayIndexOutOfBoundsException for negative index
        // Must use: arr[arr.length - 1]

        System.out.println("\n=== ArrayIndexOutOfBoundsException ===");

        // ── The most common runtime error for beginners ─────────────
        //
        //   Valid indices for int[5]:  0, 1, 2, 3, 4
        //   Accessing index 5 or -1 = CRASH
        //
        //   ┌────┬────┬────┬────┬────┐
        //   │ [0]│ [1]│ [2]│ [3]│ [4]│  ← valid
        //   └────┴────┴────┴────┴────┘
        //                              ↑ [5] = OUT OF BOUNDS!
        //
        try {
            int invalid = primes[5];  // ArrayIndexOutOfBoundsException
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        System.out.println("\n=== ARRAYS UTILITY CLASS ===");

        // ── Arrays.sort() — in-place, dual-pivot quicksort ──────────
        //
        //   BEFORE: [5, 3, 8, 1, 9, 2]
        //   AFTER:  [1, 2, 3, 5, 8, 9]
        //   Time: O(n log n) average for primitives (DualPivotQuicksort)
        //         O(n log n) for objects (TimSort — stable)
        //
        int[] data = {5, 3, 8, 1, 9, 2};
        System.out.println("Before sort:    " + Arrays.toString(data));
        Arrays.sort(data);
        System.out.println("After sort:     " + Arrays.toString(data));

        // ── Arrays.binarySearch() — requires sorted array ───────────
        //
        //   Searches [1, 2, 3, 5, 8, 9] for value 5:
        //   mid=2 → arr[2]=3 < 5 → search right
        //   mid=4 → arr[4]=8 > 5 → search left
        //   mid=3 → arr[3]=5 = 5 → FOUND at index 3
        //
        int index = Arrays.binarySearch(data, 5);
        System.out.println("binarySearch(5): index=" + index);

        // ── Arrays.copyOf() — creates a new array with new length ───
        //
        //   Source:  [1, 2, 3, 5, 8, 9]
        //   copyOf(data, 10):
        //   Result:  [1, 2, 3, 5, 8, 9, 0, 0, 0, 0]  ← padded with 0
        //
        int[] expanded = Arrays.copyOf(data, 10);
        System.out.println("copyOf(10):     " + Arrays.toString(expanded));

        // ── Arrays.fill() — set all elements to a value ─────────────
        int[] filled = new int[5];
        Arrays.fill(filled, 42);
        System.out.println("fill(42):       " + Arrays.toString(filled));

        // ── Arrays.equals() — value comparison (not ==) ─────────────
        //
        //   arrA == arrB        → compares REFERENCES (almost always false)
        //   Arrays.equals(a,b)  → compares ELEMENT VALUES (what you want)
        //
        int[] a = {1, 2, 3};
        int[] b = {1, 2, 3};
        System.out.println("a == b:         " + (a == b));              // false
        System.out.println("equals(a,b):    " + Arrays.equals(a, b));   // true

        System.out.println("\n=== MULTI-DIMENSIONAL ARRAYS ===");

        // ── 2D Array — matrix representation ────────────────────────
        //
        //   MEMORY: array of arrays (NOT contiguous matrix!)
        //
        //   matrix ref ──▶ [ ref0, ref1, ref2 ]
        //                     │      │      │
        //                     ▼      ▼      ▼
        //                  [1,2,3] [4,5,6] [7,8,9]
        //
        //   matrix[1][2] → row 1 → col 2 → value 6
        //
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("matrix[1][2]:   " + matrix[1][2]);  // 6
        System.out.println("Row 0:          " + Arrays.toString(matrix[0]));
        System.out.println("Full matrix:    " + Arrays.deepToString(matrix));

        // ── Jagged Array — rows of different lengths ────────────────
        //
        //   jagged ref ──▶ [ ref0, ref1, ref2 ]
        //                     │      │      │
        //                     ▼      ▼      ▼
        //                  [1,2]  [3,4,5] [6]
        //
        //   Useful for: adjacency lists, triangular matrices
        //
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1, 2};
        jagged[1] = new int[]{3, 4, 5};
        jagged[2] = new int[]{6};
        System.out.println("Jagged array:   " + Arrays.deepToString(jagged));

        System.out.println("\n=== ARRAY vs ARRAYLIST DECISION ===");
        System.out.println("┌─────────────────────────────────────────────┐");
        System.out.println("│  Fixed size + primitive type → int[]        │");
        System.out.println("│  Dynamic size + objects     → ArrayList<T>  │");
        System.out.println("│  Performance-critical       → int[]         │");
        System.out.println("│  Rich API needed            → ArrayList<T>  │");
        System.out.println("└─────────────────────────────────────────────┘");
    }
}
