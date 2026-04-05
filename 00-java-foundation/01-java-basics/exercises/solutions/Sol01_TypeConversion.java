/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_TypeConversion.java                              ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — implicit vs explicit type casting   ║
 * ║  DEMONSTRATES   : Widening, narrowing, overflow, String/int      ║
 * ║  PYTHON COMPARE : Python auto-promotes; Java truncates on cast   ║
 * ║                                                                  ║
 * ║  FLOW:                                                           ║
 * ║   int ──(widening)──▶ double   (no cast, no data loss)          ║
 * ║   double ──(narrow)──▶ int     (explicit cast, fractional lost) ║
 * ║   int ──(overflow)──▶ byte     (bit truncation, sign flip)      ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

/**
 * Demonstrates all forms of Java type conversion.
 *
 * <p>Key rule: Java will auto-widen (safe) but never auto-narrow (lossy).
 * Narrowing requires an explicit cast — the compiler forces you to acknowledge
 * the potential data loss.
 *
 * <p>Python developers: Python 3 has one int type of arbitrary size.
 * Java has 8 primitive types with fixed sizes — overflow is silent and
 * wraps around (modular arithmetic on the bit representation).
 */
public class Sol01_TypeConversion {

    /**
     * Demonstrates widening, narrowing, overflow, and String/number conversion.
     *
     * <p>Run directly — no args needed.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("--- Type Conversion Solution ---\n");

        // ── 1. Widening (implicit) ──────────────────────────────────────
        // Java automatically promotes to a wider type — no cast needed.
        // Order: byte → short → int → long → float → double
        int score = 100;
        double decimalScore = score; // WHY: compiler inserts implicit cast, no precision lost
        long bigScore = score;       // WHY: int fits in long without truncation
        System.out.println("Widening int→double: " + score + " → " + decimalScore);
        System.out.println("Widening int→long:   " + score + " → " + bigScore);

        // ── 2. Narrowing (explicit cast required) ───────────────────────
        // WHY cast is needed: the programmer must acknowledge potential data loss.
        // The fractional part is TRUNCATED (not rounded) — same as Python int().
        double exactPrice = 99.99;
        int truncatedPrice = (int) exactPrice; // WHY: 99.99 → 99, not 100
        System.out.println("\nNarrowing double→int: " + exactPrice + " → " + truncatedPrice);
        System.out.println("  NOTE: truncated, not rounded. Use Math.round() for rounding.");

        // ── 3. Overflow trap ────────────────────────────────────────────
        // WHY: byte is 8 bits, range -128 to 127.
        // 130 in binary = 10000010. As a signed byte, the leading 1 means negative.
        // Result = 130 - 256 = -126 (modular wrap-around).
        int largeNumber = 130;
        byte wrapped = (byte) largeNumber; // WHY: bits are preserved, interpretation changes
        System.out.println("\nOverflow byte cast: int " + largeNumber + " → byte " + wrapped);
        System.out.println("  FORMULA: 130 - 256 = -126 (wraps around at 256 = 2^8)");

        // ── 4. char ↔ int duality ───────────────────────────────────────
        // WHY: char is stored as a 16-bit Unicode code point.
        // Arithmetic on char widens to int implicitly.
        char letter = 'A';
        int codePoint = letter;        // WHY: widening — 'A' = Unicode 65
        char next = (char) (letter + 1); // WHY: +1 widens to int, must cast back to char
        System.out.println("\nchar↔int: 'A' code = " + codePoint + ", 'A'+1 = '" + next + "'");

        // ── 5. String → number conversion ──────────────────────────────
        // WHY: Strings are not primitives. Integer.parseInt() does the conversion.
        // Python: int("42") — same idea but different syntax.
        String numberStr = "42";
        int parsed = Integer.parseInt(numberStr); // WHY: parse, not cast
        System.out.println("\nString→int: \"" + numberStr + "\" → " + (parsed + 1));
        System.out.println("  NOTE: casting (int)\"42\" would be a compile error");

        // ── 6. int → String conversion ──────────────────────────────────
        // Three ways — all produce the same result:
        int value = 99;
        String s1 = String.valueOf(value);    // WHY: preferred — explicit, works for all types
        String s2 = Integer.toString(value);  // WHY: alternative — integer-specific
        String s3 = "" + value;               // WHY: works but creates intermediate String — avoid in loops
        System.out.println("\nint→String: " + s1 + ", " + s2 + ", " + s3);
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using (int) to round a double
 *   WRONG: int rounded = (int) 99.99;  // → 99, NOT 100!
 *   RIGHT: int rounded = (int) Math.round(99.99);  // → 100
 *
 * MISTAKE 2: Assuming casting works on String
 *   WRONG: int n = (int) "42";          // compile error
 *   RIGHT: int n = Integer.parseInt("42");
 *
 * MISTAKE 3: Integer overflow in arithmetic without cast
 *   WRONG: long big = Integer.MAX_VALUE + 1;  // overflow BEFORE assignment!
 *   RIGHT: long big = (long) Integer.MAX_VALUE + 1;  // cast first
 *
 * MISTAKE 4: char arithmetic surprises
 *   WRONG: char result = 'A' + 1;       // compile error: int can't fit in char
 *   RIGHT: char result = (char) ('A' + 1);
 *
 * MISTAKE 5: float vs double precision
 *   float f = 1.1f;   // ~7 significant digits
 *   double d = 1.1;   // ~15 significant digits
 *   f == d is FALSE   // never compare floats with ==; use Math.abs(a-b) < epsilon
 * ═══════════════════════════════════════════════════════════════════ */
