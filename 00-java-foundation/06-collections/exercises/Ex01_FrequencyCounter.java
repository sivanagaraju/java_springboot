/**
 * ====================================================================
 * FILE    : Ex01_FrequencyCounter.java
 * MODULE  : 06 — Collections
 * PURPOSE : Practice HashMap operations with word frequency counting
 * ====================================================================
 *
 * EXERCISES:
 *
 *   1. countWordFrequency(text) → Map<String, Integer>
 *      - Split text on whitespace, count each word's occurrence
 *      - Case-insensitive (convert to lowercase)
 *      - HINT: Use map.merge(word, 1, Integer::sum)
 *
 *   2. topNWords(text, n) → List<Map.Entry<String, Integer>>
 *      - Return the top N most frequent words (sorted by count desc)
 *      - HINT: Sort entries by value using Comparator
 *
 *   3. groupAnagrams(words) → Map<String, List<String>>
 *      - Group words that are anagrams of each other
 *      - HINT: Sort each word's chars as the key
 *      - Example: ["eat","tea","tan","ate","nat","bat"]
 *        → {"aet": ["eat","tea","ate"], "ant": ["tan","nat"], "abt": ["bat"]}
 *
 *   4. findFirstNonRepeating(text) → char
 *      - Find the first character that appears only once
 *      - HINT: Use LinkedHashMap to preserve insertion order
 *
 * FREQUENCY MAP BUILDING:
 *
 *   Input: "the cat sat on the mat"
 *
 *   ┌─────────────────────────────────────┐
 *   │  HashMap<String, Integer>            │
 *   │  "the" → 2                          │
 *   │  "cat" → 1                          │
 *   │  "sat" → 1                          │
 *   │  "on"  → 1                          │
 *   │  "mat" → 1                          │
 *   └─────────────────────────────────────┘
 *
 * ====================================================================
 */
import java.util.*;
import java.util.stream.*;

public class Ex01_FrequencyCounter {

    // PYTHON EQUIVALENT: Counter(text.lower().split())
    public static Map<String, Integer> countWordFrequency(String text) {
        // TODO: Implement using HashMap and merge()
        return new HashMap<>();
    }

    // PYTHON EQUIVALENT: Counter(words).most_common(n)
    public static List<Map.Entry<String, Integer>> topNWords(String text, int n) {
        // TODO: Get frequency map, sort entries by value (descending), return top N
        return new ArrayList<>();
    }

    // PYTHON EQUIVALENT: defaultdict(list) with sorted chars as key
    public static Map<String, List<String>> groupAnagrams(String[] words) {
        // TODO: For each word, sort its chars to create a key
        //       Group words with the same key together
        return new HashMap<>();
    }

    // PYTHON EQUIVALENT: next(c for c in text if text.count(c) == 1)
    public static char findFirstNonRepeating(String text) {
        // TODO: Use LinkedHashMap to count chars, then find first with count == 1
        return ' ';
    }

    public static void main(String[] args) {
        System.out.println("=== Word Frequency Counter Exercise ===");

        String sample = "the quick brown fox jumps over the lazy dog the fox";
        System.out.println("Text: " + sample);
        System.out.println("Frequency: " + countWordFrequency(sample));
        System.out.println("Top 3: " + topNWords(sample, 3));

        String[] anagramInput = {"eat", "tea", "tan", "ate", "nat", "bat"};
        System.out.println("\nAnagram groups: " + groupAnagrams(anagramInput));

        System.out.println("\nFirst non-repeating in 'aabcbd': " + findFirstNonRepeating("aabcbd"));
    }
}
