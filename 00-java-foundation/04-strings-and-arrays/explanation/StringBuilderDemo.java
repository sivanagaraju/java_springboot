/**
 * ====================================================================
 * FILE    : StringBuilderDemo.java
 * MODULE  : 04 — Strings & Arrays
 * PURPOSE : Demonstrate StringBuilder vs StringBuffer internal mechanics
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: parts = []; parts.append("a"); "".join(parts)
 *   Java:   StringBuilder sb = new StringBuilder(); sb.append("a"); sb.toString()
 *
 * INTERNAL BUFFER ARCHITECTURE:
 *
 *   new StringBuilder()  → default capacity = 16
 *
 *   ┌──────────────────────────────────────────────────────────┐
 *   │  Internal char[] value                                    │
 *   │  ┌──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┐    │
 *   │  │  │  │  │  │  │  │  │  │  │  │  │  │  │  │  │  │    │
 *   │  └──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┘    │
 *   │  capacity = 16, length = 0                                │
 *   └──────────────────────────────────────────────────────────┘
 *
 *   After sb.append("Hello World!")  → 12 chars written in-place
 *
 *   ┌──────────────────────────────────────────────────────────┐
 *   │  ┌──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┬──┐    │
 *   │  │H │e │l │l │o │  │W │o │r │l │d │! │  │  │  │  │    │
 *   │  └──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┴──┘    │
 *   │  capacity = 16, length = 12                               │
 *   └──────────────────────────────────────────────────────────┘
 *
 *   When capacity exceeded → new_cap = old_cap * 2 + 2
 *   16 → 34 → 70 → 142 → ...
 *
 * StringBuilder vs StringBuffer DECISION:
 *
 *   ┌───────────────────────────────────────────┐
 *   │  Is the buffer shared between threads?    │
 *   │      YES → StringBuffer (synchronized)   │
 *   │      NO  → StringBuilder (99% of cases)  │
 *   └───────────────────────────────────────────┘
 *
 * ====================================================================
 */
public class StringBuilderDemo {

    public static void main(String[] args) {

        System.out.println("=== BASIC OPERATIONS ===");

        // ── append() — the primary operation ────────────────────────
        //
        // Each append mutates the internal char[] buffer in-place.
        // No new objects are created (unless buffer needs resizing).
        //
        // BUFFER STATE TRACE:
        //   [_ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _]  cap=16, len=0
        //   append("Java"):
        //   [J a v a _ _ _ _ _ _ _ _ _ _ _ _]  cap=16, len=4
        //   append(" is"):
        //   [J a v a   i s _ _ _ _ _ _ _ _ _]  cap=16, len=7
        //   append(" powerful"):
        //   [J a v a   i s   p o w e r f u l]  cap=16, len=15
        //
        StringBuilder sb = new StringBuilder();
        System.out.println("Initial capacity: " + sb.capacity());  // 16
        System.out.println("Initial length:   " + sb.length());    // 0

        sb.append("Java");
        sb.append(" is");
        sb.append(" powerful");
        System.out.println("After appends:    '" + sb + "'");
        System.out.println("Capacity now:     " + sb.capacity());  // still 16
        System.out.println("Length now:        " + sb.length());   // 15

        // ── Append more to trigger resize ───────────────────────────
        //
        // RESIZE EVENT:
        //   Current: cap=16, len=15, need to fit 1 more char
        //   append("!"): len becomes 16 (full but fits)
        //   append(" Very"): needs 5 more → NEW array: cap = 16*2+2 = 34
        //
        //   OLD: [J a v a   i s   p o w e r f u l !]  cap=16  ← GC
        //   NEW: [J a v a   i s   p o w e r f u l !   V e r y _ _ _ ...]  cap=34
        //
        sb.append("!");
        sb.append(" Very");
        System.out.println("After resize:     cap=" + sb.capacity());  // 34

        System.out.println("\n=== CHAINED OPERATIONS ===");

        // ── Method chaining — each method returns `this` ────────────
        // This pattern is called "Fluent API" — same concept used
        // in Spring's MockMvcRequestBuilders and JPA Criteria API.
        //
        StringBuilder chain = new StringBuilder()
            .append("Hello")         // returns this
            .append(" ")             // returns this
            .append("World")        // returns this
            .reverse()               // returns this → "dlroW olleH"
            .insert(5, " - ");       // returns this → "dlroW -  olleH"

        System.out.println("Chained result: " + chain);

        System.out.println("\n=== INSERT / DELETE / REPLACE ===");

        // ── insert() — shifts existing content right ────────────────
        //
        // BUFFER TRACE for insert(5, "Beautiful "):
        //   BEFORE: [H e l l o   W o r l d]
        //   AFTER:  [H e l l o   B e a u t i f u l   W o r l d]
        //   Elements after index 5 are shifted right
        //
        StringBuilder edit = new StringBuilder("Hello World");
        edit.insert(6, "Beautiful ");
        System.out.println("insert(6,...):  " + edit);  // Hello Beautiful World

        // ── delete() — removes range [start, end) ──────────────────
        //
        // BUFFER TRACE for delete(6, 16):
        //   BEFORE: [H e l l o   B e a u t i f u l   W o r l d]
        //                        ^start=6         ^end=16
        //   AFTER:  [H e l l o   W o r l d]
        //   Characters after end shift left to fill gap
        //
        edit.delete(6, 16);
        System.out.println("delete(6,16):   " + edit);  // Hello World

        // ── replace() — deletes range then inserts new text ─────────
        edit.replace(6, 11, "Java");
        System.out.println("replace(6,11):  " + edit);  // Hello Java

        System.out.println("\n=== CAPACITY vs LENGTH ===");

        // ── Capacity is the buffer size; Length is actual content ────
        //
        //   ┌──────────────────────────────────────┐
        //   │ length = 10     | unused capacity     │
        //   │ [H e l l o J a v a]  [_ _ _ _ _ _]   │
        //   │ ◄── length ──▶  ◄─── wasted ──▶      │
        //   │         capacity = 16                  │
        //   └──────────────────────────────────────┘
        //
        //   Tip: new StringBuilder(100) pre-sizes to avoid resizes
        //
        StringBuilder sized = new StringBuilder(100);  // pre-allocate
        sized.append("Pre-sized buffer");
        System.out.println("Pre-sized: cap=" + sized.capacity() + ", len=" + sized.length());

        System.out.println("\n=== STRINGBUILDER vs STRINGBUFFER ===");

        // ── StringBuffer uses synchronized methods ──────────────────
        //
        // StringBuffer.append():
        //   public synchronized StringBuffer append(String str) {
        //       // thread must acquire the monitor lock first
        //       super.append(str);
        //       return this;
        //   }
        //
        // StringBuilder.append():
        //   public StringBuilder append(String str) {
        //       // NO synchronization → no lock overhead
        //       super.append(str);
        //       return this;
        //   }
        //
        final int ITERATIONS = 500_000;

        long start = System.nanoTime();
        StringBuilder sbPerf = new StringBuilder();
        for (int i = 0; i < ITERATIONS; i++) sbPerf.append("a");
        long sbTime = System.nanoTime() - start;

        start = System.nanoTime();
        StringBuffer bufPerf = new StringBuffer();
        for (int i = 0; i < ITERATIONS; i++) bufPerf.append("a");
        long bufTime = System.nanoTime() - start;

        System.out.println("StringBuilder:  " + (sbTime / 1_000_000) + " ms");
        System.out.println("StringBuffer:   " + (bufTime / 1_000_000) + " ms");
        System.out.println("StringBuffer is ~" + (bufTime / Math.max(sbTime, 1)) + "x slower (lock overhead)");
    }
}
