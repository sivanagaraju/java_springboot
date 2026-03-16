package exercises;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 1    : Generic Pair                                    ║
 * ║  MODULE        : 00-java-foundation / 03-advanced-oop            ║
 * ║  GRADLE        : ./gradlew :00-java-foundation:run --args="Ex01_GenericPair" ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  GOAL          : Build a generic class that accepts TWO types.   ║
 * ║                  You must create an `A` and `B` or `K` and `V`.  ║
 * ║  REQUIREMENTS  : 1. A parameterized class `Pair<K, V>`           ║
 * ║                  2. Constructor that takes `K` and `V`           ║
 * ║                  3. Getters for both values.                     ║
 * ║                  4. Use this class in `main` below to store a    ║
 * ║                     `String` and an `Integer`.                   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class Ex01_GenericPair {

    // TODO: Define your generic class `Pair<K, V>` here (make it static so we can use it in main)
    public static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public static void main(String[] args) {
        
        System.out.println("--- Generic Pair Test ---");

        // TODO: Create a Pair representing a Student's Grade (String name, Integer score)
        // e.g., Pair<String, Integer> studentGrade = new Pair<>("Alice", 95);
        Pair<String, Integer> studentGrade = new Pair<>("Alice", 95);

        // TODO: Create a Pair representing an HTTP Response (Integer status, String message)
        // e.g., Pair<Integer, String> response = new Pair<>(404, "Not Found");
        Pair<Integer, String> response = new Pair<>(404, "Not Found");

        // TODO: Print the values out cleanly
        System.out.println("Student Grade: " + studentGrade.getKey() + " scored " + studentGrade.getValue());
        System.out.println("HTTP Response: " + response.getKey() + " " + response.getValue());

        // EXPECTED OUTPUT:
        // Student Grade: Alice scored 95
        // HTTP Response: 404 Not Found
    }
}
