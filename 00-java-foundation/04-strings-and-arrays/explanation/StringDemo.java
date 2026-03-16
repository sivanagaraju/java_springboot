/**
 * ====================================================================
 * FILE    : StringDemo.java
 * MODULE  : 04 — Strings & Arrays
 * PURPOSE : Demonstrate String immutability, pool, == vs .equals()
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: s1 = "hello"        →  s1 == "hello"     →  True (VALUE comparison)
 *   Java:   String s1 = "hello" →  s1 == "hello"     →  true (SAME pool ref!)
 *   Java:   String s1 = new String("hello")
 *           s1 == "hello"  →  false (DIFFERENT objects!)
 *           s1.equals("hello") → true (VALUE comparison)
 *
 * MENTAL MODEL — STRING CREATION:
 *
 *   Literal: "Hello"
 *   ┌────────────────────────────────────────────────────┐
 *   │  1. JVM looks up "Hello" in the String Pool        │
 *   │  2. If found → return existing reference            │
 *   │  3. If NOT found → create in Pool, return ref      │
 *   └────────────────────────────────────────────────────┘
 *
 *   Constructor: new String("Hello")
 *   ┌────────────────────────────────────────────────────┐
 *   │  1. Creates "Hello" in Pool (if not exists)        │
 *   │  2. THEN creates ANOTHER copy on the Heap          │
 *   │  3. Returns reference to the HEAP copy             │
 *   └────────────────────────────────────────────────────┘
 *
 * STRING MEMORY ARCHITECTURE:
 *
 *          Stack          │         Heap
 *   ──────────────────────│──────────────────────────────
 *   s1 ─────────────────┐ │  ┌─────────────────────────┐
 *                        │ │  │    STRING POOL           │
 *   s2 ──────────────┐  │ │  │  ┌─────────┐            │
 *                     │  ├─┼──┼─▶│ "Hello" │            │
 *                     ├──┘ │  │  └─────────┘            │
 *                        │  └─────────────────────────┘
 *                          │
 *   s3 ──────────────────┼──▶ ┌─────────┐
 *                          │   │ "Hello" │  (SEPARATE!)
 *                          │   └─────────┘
 *
 *   s1 == s2  → true  (same pool reference)
 *   s1 == s3  → false (different memory locations)
 *   s1.equals(s3) → true  (same character sequence)
 *
 * ====================================================================
 */
public class StringDemo {

    public static void main(String[] args) {

        System.out.println("=== STRING IMMUTABILITY ===");

        // ── Immutability demonstration ──────────────────────────────
        // Strings cannot be modified. Every "change" creates a NEW object.
        //
        // MEMORY TRACE:
        //   Step 1: greeting → "Hello" (in Pool)
        //   Step 2: greeting → "Hello World" (NEW object)
        //           "Hello" still exists in Pool (orphaned reference)
        String greeting = "Hello";
        System.out.println("Identity hashCode before: " + System.identityHashCode(greeting));
        greeting = greeting + " World";
        System.out.println("Identity hashCode after:  " + System.identityHashCode(greeting));
        System.out.println("Different hashCodes = different objects on Heap!\n");

        System.out.println("=== STRING POOL & REFERENCE COMPARISON ===");

        // ── String Pool demonstration ───────────────────────────────
        //
        // ASCII DIAGRAM — What happens here:
        //
        //   "Hello" → Pool lookup → CREATE in Pool → s1 points to it
        //   "Hello" → Pool lookup → FOUND → s2 points to SAME object
        //   new String("Hello") → creates a SEPARATE Heap object
        //
        //   Pool:  [ "Hello" ] ← s1, s2 both point here
        //   Heap:  [ "Hello" ] ← s3 points here (different object)
        //
        String s1 = "Hello";
        String s2 = "Hello";
        String s3 = new String("Hello");

        System.out.println("s1 == s2 (both pool):    " + (s1 == s2));     // true
        System.out.println("s1 == s3 (pool vs heap): " + (s1 == s3));     // false
        System.out.println("s1.equals(s3) (value):   " + s1.equals(s3)); // true

        // ── intern() — forcing pool lookup ──────────────────────────
        // intern() returns the Pool reference for this character sequence.
        //
        // BEFORE: s3 → Heap "Hello" (separate from pool)
        // AFTER:  s4 → Pool "Hello" (same as s1, s2)
        String s4 = s3.intern();
        System.out.println("s1 == s3.intern():       " + (s1 == s4));     // true
        System.out.println();

        System.out.println("=== CONCATENATION PERFORMANCE TRAP ===");

        // ── BAD: String += in a loop ────────────────────────────────
        //
        // Each iteration creates a NEW String object:
        //   i=0: "" + "0" → new "0"       (copy 1 char)
        //   i=1: "0" + "1" → new "01"     (copy 2 chars)
        //   i=2: "01" + "2" → new "012"   (copy 3 chars)
        //   ...
        //   Total: O(n²) characters copied, n garbage objects
        //
        long start = System.nanoTime();
        String bad = "";
        for (int i = 0; i < 10_000; i++) {
            bad += i;                  // ← O(n²) time complexity!
        }
        long badTime = System.nanoTime() - start;
        System.out.println("String += time:       " + (badTime / 1_000_000) + " ms");

        // ── GOOD: StringBuilder in a loop ───────────────────────────
        //
        // Internal buffer mutated in place:
        //   Buffer: [_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _]  capacity=16
        //   append("0"): [0 _ _ _ _ _ _ _ _ _ _ _ _ _ _]
        //   append("1"): [0 1 _ _ _ _ _ _ _ _ _ _ _ _]
        //   (buffer full → doubles capacity automatically)
        //   Total: O(n) time, 1 object + a few resize copies
        //
        start = System.nanoTime();
        StringBuilder good = new StringBuilder();
        for (int i = 0; i < 10_000; i++) {
            good.append(i);            // ← O(n) time complexity
        }
        String result = good.toString();
        long goodTime = System.nanoTime() - start;
        System.out.println("StringBuilder time:   " + (goodTime / 1_000_000) + " ms");
        System.out.println("StringBuilder is ~" + (badTime / Math.max(goodTime, 1)) + "x faster\n");

        System.out.println("=== USEFUL STRING METHODS ===");

        // ── Common operations with Python equivalents ───────────────
        String text = "  Hello, Java World!  ";

        System.out.println("strip():       '" + text.strip() + "'");       // Python: text.strip()
        System.out.println("toLowerCase(): " + text.strip().toLowerCase()); // Python: text.lower()
        System.out.println("contains:      " + text.contains("Java"));     // Python: "Java" in text
        System.out.println("indexOf:       " + text.indexOf("Java"));      // Python: text.find("Java")
        System.out.println("replace:       " + text.strip().replace("Java", "Spring"));
        System.out.println("split:         " + java.util.Arrays.toString("a,b,c".split(",")));
        System.out.println("charAt(0):     " + text.strip().charAt(0));    // Python: text[0]
        System.out.println("substring(0,5):" + text.strip().substring(0, 5)); // Python: text[:5]

        // Java 15+ formatted strings (like Python f-strings)
        String name = "Spring";
        int version = 6;
        System.out.println("formatted:     " + "Hello %s %d".formatted(name, version));
    }
}
