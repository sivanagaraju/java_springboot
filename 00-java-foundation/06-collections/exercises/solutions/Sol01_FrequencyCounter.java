/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol01_FrequencyCounter.java                            ║
 * ║  MODULE : 00-java-foundation / 06-collections                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — HashMap frequency counting          ║
 * ║  DEMONSTRATES   : merge(), groupBy, LinkedHashMap ordering       ║
 * ║  PYTHON COMPARE : Counter vs merge(); defaultdict vs computeIf  ║
 * ║                                                                  ║
 * ║  ALGORITHMS:                                                     ║
 * ║   countWordFrequency → map.merge(word, 1, Integer::sum)  O(n)   ║
 * ║   topNWords          → sort entries by value desc         O(n log n)║
 * ║   groupAnagrams      → sort chars as key                  O(n * k log k)║
 * ║   findFirstNonRep    → LinkedHashMap (insertion order)    O(n)   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

import java.util.*;
import java.util.stream.*;

/**
 * Solutions for word frequency counting and related HashMap operations.
 *
 * <p>Key insight: {@code map.merge(key, 1, Integer::sum)} is the idiomatic
 * Java way to increment a counter — equivalent to Python's
 * {@code Counter(words)} or {@code defaultdict(int)}.
 */
public class Sol01_FrequencyCounter {

    /**
     * Counts word frequency in a text string.
     *
     * <p>{@code merge(key, value, remappingFn)}: if key absent → puts value;
     * if key present → applies remappingFn(existingValue, value).
     * For counting: {@code merge(word, 1, Integer::sum)} starts at 1,
     * then adds 1 for each subsequent occurrence.
     *
     * <p>Python: {@code Counter(text.lower().split())}
     *
     * @param text space-separated text
     * @return map from word → occurrence count
     */
    public static Map<String, Integer> countWordFrequency(String text) {
        if (text == null || text.isBlank()) {
            return new HashMap<>();
        }
        Map<String, Integer> freq = new HashMap<>();
        // WHY split("\\s+"): handles multiple spaces, tabs, and newlines
        for (String word : text.toLowerCase().split("\\s+")) {
            if (!word.isEmpty()) {
                // WHY merge over put: merge(k, 1, Integer::sum) is atomic
                // conceptually and cleaner than:
                //   freq.put(word, freq.getOrDefault(word, 0) + 1);
                freq.merge(word, 1, Integer::sum);
            }
        }
        return freq;
    }

    /**
     * Returns the top N most frequent words, sorted by count descending.
     *
     * <p>Python: {@code Counter(text.lower().split()).most_common(n)}
     *
     * @param text space-separated text
     * @param n    number of top entries to return
     * @return list of (word, count) entries, highest count first
     */
    public static List<Map.Entry<String, Integer>> topNWords(String text, int n) {
        Map<String, Integer> freq = countWordFrequency(text);
        return freq.entrySet().stream()
                // WHY comparing by value reversed: sort descending by count
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Groups words that are anagrams of each other.
     *
     * <p>Key insight: anagrams have the same characters — sorting a word's
     * characters produces an identical key for all anagrams of that word.
     *
     * <p>Python: {@code defaultdict(list)} with sorted chars as key.
     *
     * @param words array of words to group
     * @return map from sorted-char-key → list of anagram words
     */
    public static Map<String, List<String>> groupAnagrams(String[] words) {
        Map<String, List<String>> groups = new HashMap<>();
        for (String word : words) {
            // WHY sort chars: "eat", "tea", "ate" all sort to "aet"
            char[] chars = word.toLowerCase().toCharArray();
            Arrays.sort(chars);
            String key = new String(chars); // WHY new String(char[]): convert sorted chars back to string

            // WHY computeIfAbsent: atomically creates the list if absent,
            // then adds the word. Cleaner than checking containsKey + put.
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }
        return groups;
    }

    /**
     * Finds the first character in a string that appears exactly once.
     *
     * <p>WHY LinkedHashMap: preserves insertion order so the "first" is
     * genuinely the first encountered character in the input.
     * A regular HashMap would give arbitrary ordering.
     *
     * <p>Python: {@code next(c for c in text if text.count(c) == 1, ' ')}
     *
     * @param text the input string
     * @return the first non-repeating character, or {@code ' '} if none found
     */
    public static char findFirstNonRepeating(String text) {
        if (text == null || text.isEmpty()) {
            return ' ';
        }
        // WHY LinkedHashMap: HashMap would not preserve insertion order —
        // iterating it later would not find the "first" char, just any char.
        Map<Character, Integer> freq = new LinkedHashMap<>();
        for (char c : text.toCharArray()) {
            freq.merge(c, 1, Integer::sum);
        }
        // WHY iterate entrySet (not text again): entrySet preserves insertion order
        // and avoids a second O(n) pass through the text
        for (Map.Entry<Character, Integer> entry : freq.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        return ' '; // WHY ' ': sentinel for "no non-repeating character found"
    }

    /**
     * Runs all test cases.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("=== Frequency Counter Solutions ===\n");

        String sample = "the quick brown fox jumps over the lazy dog the fox";
        System.out.println("Text: " + sample);
        System.out.println("Frequency: " + countWordFrequency(sample));
        System.out.println("Top 3: " + topNWords(sample, 3));
        // Expected top 3: the=3, fox=2, then any word with 1

        String[] anagramInput = {"eat", "tea", "tan", "ate", "nat", "bat"};
        System.out.println("\nAnagram groups:");
        groupAnagrams(anagramInput).forEach((key, group) ->
                System.out.println("  " + key + " → " + group));
        // Expected: aet→[eat,tea,ate], ant→[tan,nat], abt→[bat]

        System.out.println("\nFirst non-repeating in 'aabcbd': "
                + findFirstNonRepeating("aabcbd")); // 'c'

        System.out.println("First non-repeating in 'aabb': "
                + "'" + findFirstNonRepeating("aabb") + "'"); // ' ' (none)
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using getOrDefault instead of merge
 *   WRONG: freq.put(word, freq.getOrDefault(word, 0) + 1);
 *   WORKS but is verbose. merge(word, 1, Integer::sum) is idiomatic.
 *   Also: merge handles null values correctly; getOrDefault does not
 *   if the map explicitly contains null as a value.
 *
 * MISTAKE 2: Using HashMap instead of LinkedHashMap for findFirstNonRepeating
 *   WRONG: Map<Character, Integer> freq = new HashMap<>();
 *   PROBLEM: Iterating HashMap has no order guarantee. "First" non-repeating
 *            could return any character, not the actual first.
 *   RIGHT: LinkedHashMap maintains insertion order.
 *
 * MISTAKE 3: Sorting char[] but not converting back to String as key
 *   WRONG: Arrays.sort(chars); String key = chars.toString(); // "[C@1234" — wrong!
 *   RIGHT: new String(chars)  — converts the char array to a proper String.
 *
 * MISTAKE 4: Integer overflow in topNWords comparator
 *   WRONG: .sorted((a, b) -> b.getValue() - a.getValue())
 *   PROBLEM: If values are Integer.MIN_VALUE and Integer.MAX_VALUE, subtraction
 *            overflows to a wrong sign. Use Integer.compare() or comparingByValue().
 *   RIGHT: Map.Entry.<String, Integer>comparingByValue().reversed()
 *
 * MISTAKE 5: Not lowercasing in groupAnagrams
 *   "Eat" and "eat" should be in the same group but sorted chars differ:
 *   "Eat" → 'E','a','t' → sorted: 'E','a','t' (uppercase sorts before lowercase).
 *   Always normalize to lowercase before sorting.
 * ═══════════════════════════════════════════════════════════════════ */
