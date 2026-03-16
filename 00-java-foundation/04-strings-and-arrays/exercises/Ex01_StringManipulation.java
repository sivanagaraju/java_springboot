/**
 * ====================================================================
 * FILE    : Ex01_StringManipulation.java
 * MODULE  : 04 — Strings & Arrays
 * PURPOSE : Practice String operations, pool behavior, and StringBuilder
 * ====================================================================
 *
 * EXERCISES (implement each method below):
 *
 *   1. reverseString(s)        → reverse without StringBuilder.reverse()
 *   2. countVowels(s)          → count a,e,i,o,u (case-insensitive)
 *   3. isPalindrome(s)         → check if string reads same forwards/backwards
 *   4. compressString(s)       → "aaabbc" → "a3b2c1"
 *   5. areAnagrams(s1, s2)     → check if two strings are anagrams
 *
 * RULES:
 *   - Use StringBuilder where appropriate
 *   - Think about == vs .equals() in your comparisons
 *   - Consider edge cases: null, empty string, single char
 *
 * ====================================================================
 */
public class Ex01_StringManipulation {

    /**
     * Reverse a string WITHOUT using StringBuilder.reverse()
     * 
     * Hint: Use charAt() and iterate backwards, building with StringBuilder
     * 
     * Example: "Hello" → "olleH"
     * Python equivalent: s[::-1]
     */
    public static String reverseString(String s) {
        // TODO: Implement this
        return "";
    }

    /**
     * Count the number of vowels (a, e, i, o, u) in a string.
     * Case-insensitive.
     * 
     * Example: "Hello World" → 3
     * Python equivalent: sum(1 for c in s.lower() if c in 'aeiou')
     */
    public static int countVowels(String s) {
        // TODO: Implement this
        return 0;
    }

    /**
     * Check if a string is a palindrome (reads same forwards and backwards).
     * Ignore case and non-alphanumeric characters.
     * 
     * Example: "A man, a plan, a canal: Panama" → true
     * Python equivalent: cleaned = ''.join(c for c in s.lower() if c.isalnum())
     *                    return cleaned == cleaned[::-1]
     */
    public static boolean isPalindrome(String s) {
        // TODO: Implement this
        return false;
    }

    /**
     * Compress consecutive duplicate characters.
     * 
     * Example: "aaabbc" → "a3b2c1"
     * If compressed is not shorter, return original.
     * 
     * ALGORITHM HINT:
     *   ┌─────────────────────────────────────────┐
     *   │  Walk through string, count consecutive  │
     *   │  same characters. When char changes,     │
     *   │  append char + count to StringBuilder.   │
     *   │                                          │
     *   │  "aaabbc"                                │
     *   │   ^^^      → 'a' appears 3 times         │
     *   │      ^^    → 'b' appears 2 times          │
     *   │        ^   → 'c' appears 1 time           │
     *   │  Result: "a3b2c1"                         │
     *   └─────────────────────────────────────────┘
     */
    public static String compressString(String s) {
        // TODO: Implement this
        return "";
    }

    /**
     * Check if two strings are anagrams of each other.
     * 
     * Example: "listen", "silent" → true
     * 
     * APPROACH OPTIONS:
     *   1. Sort both strings, compare → O(n log n)
     *   2. Count character frequencies with int[26] → O(n)
     */
    public static boolean areAnagrams(String s1, String s2) {
        // TODO: Implement this
        return false;
    }

    // ── Test your implementations ───────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== String Manipulation Exercises ===\n");

        // Test reverseString
        System.out.println("reverse('Hello'):      " + reverseString("Hello"));      // olleH
        System.out.println("reverse(''):           '" + reverseString("") + "'");     // ""

        // Test countVowels
        System.out.println("vowels('Hello World'): " + countVowels("Hello World"));  // 3
        System.out.println("vowels('xyz'):         " + countVowels("xyz"));           // 0

        // Test isPalindrome
        System.out.println("palindrome('racecar'): " + isPalindrome("racecar"));     // true
        System.out.println("palindrome('Panama'):  " + isPalindrome("A man, a plan, a canal: Panama")); // true

        // Test compressString
        System.out.println("compress('aaabbc'):    " + compressString("aaabbc"));     // a3b2c1
        System.out.println("compress('abc'):       " + compressString("abc"));        // abc (not shorter)

        // Test areAnagrams
        System.out.println("anagram('listen','silent'): " + areAnagrams("listen", "silent")); // true
        System.out.println("anagram('hello','world'):   " + areAnagrams("hello", "world"));   // false
    }
}
