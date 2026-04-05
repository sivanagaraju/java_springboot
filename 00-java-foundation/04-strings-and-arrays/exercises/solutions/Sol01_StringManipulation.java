/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_StringManipulation.java                          ║
 * ║  MODULE : 00-java-foundation / 04-strings-and-arrays             ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — String operations, palindrome, etc  ║
 * ║  DEMONSTRATES   : StringBuilder, two-pointer, char[], regex      ║
 * ║  PYTHON COMPARE : s[::-1] vs manual loop; Counter vs int[26]     ║
 * ║                                                                  ║
 * ║  ALGORITHMS:                                                     ║
 * ║   reverseString  → two-pointer or backwards iteration O(n)      ║
 * ║   countVowels    → char scan O(n)                               ║
 * ║   isPalindrome   → clean + two-pointer O(n)                     ║
 * ║   compressString → sliding count O(n)                           ║
 * ║   areAnagrams    → int[26] frequency array O(n)                 ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

/**
 * Solutions for String manipulation exercises.
 *
 * <p>All methods are O(n) time. Key pattern: prefer {@link StringBuilder}
 * for building results — never {@code String +=} inside a loop (O(n²)).
 *
 * <p>Python bridge: Python's {@code s[::-1]} reversal is idiomatic because
 * Python strings support slicing. Java strings do not — we use a char scan.
 * Python's {@code collections.Counter} maps to Java's {@code int[26]} array
 * for fixed alphabets (O(1) space vs O(k) for maps).
 */
public class Sol01_StringManipulation {

    /**
     * Reverses a string WITHOUT using {@code StringBuilder.reverse()}.
     *
     * <p>Manually iterates backwards using {@code charAt()} and appends
     * each character to a {@code StringBuilder}. Using {@code sb.append()}
     * is O(1) amortized per char — total O(n).
     *
     * <p>Python: {@code s[::-1]}
     *
     * @param s the input string
     * @return the reversed string, or {@code ""} if input is null/empty
     */
    public static String reverseString(String s) {
        if (s == null || s.isEmpty()) {
            return s == null ? "" : s;
        }
        // WHY StringBuilder: builds result O(n). String += would be O(n²).
        StringBuilder sb = new StringBuilder(s.length());
        // WHY iterate backwards: simplest way to reverse without extra data structure
        for (int i = s.length() - 1; i >= 0; i--) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    /**
     * Counts vowels (a, e, i, o, u) in a string, case-insensitively.
     *
     * <p>Python: {@code sum(1 for c in s.lower() if c in 'aeiou')}
     *
     * @param s the input string
     * @return count of vowel characters
     */
    public static int countVowels(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        int count = 0;
        // WHY toLowerCase once: avoids calling toLowerCase inside the loop
        // which would create n intermediate String objects
        String lower = s.toLowerCase();
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            // WHY switch expression: cleaner and compiler-verified than a long if-chain
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if a string is a palindrome, ignoring case and non-alphanumeric chars.
     *
     * <p>Two-pointer approach: O(n) time, O(n) space (for cleaned string).
     * O(1) space variant: skip non-alphanumeric chars inline with two pointers.
     *
     * <p>Python: {@code cleaned = re.sub(r'[^a-z0-9]','',s.lower()); cleaned == cleaned[::-1]}
     *
     * @param s the input string (e.g., "A man, a plan, a canal: Panama")
     * @return {@code true} if palindrome
     */
    public static boolean isPalindrome(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        }
        // WHY replaceAll + toLowerCase: strips non-alphanumeric and normalizes case
        // in one pass. The regex [^a-zA-Z0-9] matches any char NOT in the set.
        String clean = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

        // WHY two-pointer: avoids creating a reversed copy of the string
        int left = 0;
        int right = clean.length() - 1;
        while (left < right) {
            if (clean.charAt(left) != clean.charAt(right)) {
                return false; // WHY early return: first mismatch proves not a palindrome
            }
            left++;
            right--;
        }
        return true;
    }

    /**
     * Compresses consecutive duplicate characters.
     *
     * <p>Example: {@code "aaabbc"} → {@code "a3b2c1"}
     *
     * <p>If the compressed version is not shorter, returns the original.
     * This prevents "expanding" already-short strings like {@code "abc"} → {@code "a1b1c1"}.
     *
     * @param s the input string
     * @return compressed string, or original if compression doesn't reduce length
     */
    public static String compressString(String s) {
        if (s == null || s.length() <= 1) {
            return s == null ? "" : s;
        }
        StringBuilder sb = new StringBuilder();
        int count = 1;
        // WHY start at index 1: compare current char to previous (index 0 is the first)
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                count++; // same char — keep counting
            } else {
                // WHY append here (not inside the loop): batch append for each run
                sb.append(s.charAt(i - 1));
                sb.append(count);
                count = 1; // WHY reset: new character run starts
            }
        }
        // WHY append after loop: flush the last run (loop exits before appending it)
        sb.append(s.charAt(s.length() - 1));
        sb.append(count);

        // WHY length check: "abc" → "a1b1c1" is longer, return original
        return sb.length() < s.length() ? sb.toString() : s;
    }

    /**
     * Checks if two strings are anagrams.
     *
     * <p>Uses an {@code int[26]} frequency array — O(n) time, O(1) space
     * (26 buckets is a constant regardless of input size).
     * Works only for lowercase English letters. For Unicode, use a {@code HashMap}.
     *
     * <p>Python: {@code sorted(s1) == sorted(s2)} — O(n log n).
     * Or: {@code collections.Counter(s1) == collections.Counter(s2)} — O(n).
     *
     * @param s1 first string
     * @param s2 second string
     * @return {@code true} if s1 and s2 are anagrams
     */
    public static boolean areAnagrams(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false; // WHY early return: different lengths can never be anagrams
        }
        // WHY int[26]: maps each lowercase letter to index 0-25 via 'c' - 'a'
        // This is O(1) space — 26 fixed buckets regardless of string length
        int[] freq = new int[26];
        for (int i = 0; i < s1.length(); i++) {
            freq[Character.toLowerCase(s1.charAt(i)) - 'a']++; // increment for s1
            freq[Character.toLowerCase(s2.charAt(i)) - 'a']--; // decrement for s2
        }
        // WHY check all zeros: if s1 and s2 are anagrams, every frequency
        // increment is cancelled by a corresponding decrement
        for (int f : freq) {
            if (f != 0) return false;
        }
        return true;
    }

    /**
     * Runs all test cases.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== String Manipulation Solutions ===\n");

        System.out.println("reverse('Hello'):      " + reverseString("Hello"));     // olleH
        System.out.println("reverse(''):           '" + reverseString("") + "'");    // ""

        System.out.println("vowels('Hello World'): " + countVowels("Hello World")); // 3
        System.out.println("vowels('xyz'):         " + countVowels("xyz"));          // 0

        System.out.println("palindrome('racecar'): " + isPalindrome("racecar"));    // true
        System.out.println("palindrome('Panama'):  "
                + isPalindrome("A man, a plan, a canal: Panama"));                  // true
        System.out.println("palindrome('hello'):   " + isPalindrome("hello"));      // false

        System.out.println("compress('aaabbc'):    " + compressString("aaabbc"));    // a3b2c1
        System.out.println("compress('abc'):       " + compressString("abc"));       // abc

        System.out.println("anagram('listen','silent'): " + areAnagrams("listen", "silent")); // true
        System.out.println("anagram('hello','world'):   " + areAnagrams("hello", "world"));   // false
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: String concatenation in loop
 *   WRONG: String result = ""; for (int i = n-1; i >= 0; i--) result += s.charAt(i);
 *   PROBLEM: O(n²) — each += creates a new String copying all previous chars.
 *   RIGHT: StringBuilder sb = new StringBuilder(); ... sb.append(c); sb.toString();
 *
 * MISTAKE 2: Forgetting the last run in compressString
 *   The loop ends after processing s[n-2] vs s[n-1]. The final run (last char)
 *   is never appended inside the loop. Always flush after the loop.
 *
 * MISTAKE 3: Not returning original in compressString when not shorter
 *   "abc" → "a1b1c1" is LONGER. Always compare lengths and return original if not beneficial.
 *
 * MISTAKE 4: areAnagrams — not lowercasing before frequency count
 *   "Listen" and "Silent" are anagrams but 'L' - 'a' = 43 (out of range for int[26]).
 *   Always normalize case before the frequency count.
 *
 * MISTAKE 5: isPalindrome — not cleaning non-alphanumeric chars
 *   "A man, a plan, a canal: Panama" has spaces, commas, colons.
 *   Without cleaning, the two-pointer comparison fails at the comma vs 'a'.
 * ═══════════════════════════════════════════════════════════════════ */
