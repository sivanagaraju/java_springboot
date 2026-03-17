import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 2: JSON-like Data Structure                       ║
 * ║  Model a JSON value type using sealed + records + matching  ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * TASK: Create a type-safe JSON representation using modern Java.
 *
 * Requirements:
 * 1. sealed interface JsonValue permits:
 *    JsonString, JsonNumber, JsonBool, JsonNull, JsonArray, JsonObject
 * 2. Each variant is a record with appropriate fields
 * 3. Implement stringify(JsonValue) using pattern matching switch
 *    - JsonString → "value" (with quotes)
 *    - JsonNumber → 42 or 3.14
 *    - JsonBool   → true/false
 *    - JsonNull   → null
 *    - JsonArray  → [elem1, elem2]
 *    - JsonObject → {"key": value}
 * 4. Implement a builder API: json().obj().key("name").str("John").build()
 *
 * Expected Output:
 *   {"name": "John", "age": 25, "active": true, "address": null}
 *   ["hello", 42, true, null]
 *
 * HINTS:
 * - record JsonArray(List<JsonValue> elements)
 * - Use exhaustive switch for stringify
 * - StringBuilder for building output
 * - Stream + Collectors.joining for arrays/objects
 */
public class Ex02_JsonDataStructure {
    // TODO: Define sealed interface JsonValue
    // TODO: Define record types for each JSON variant
    // TODO: Implement stringify() with pattern matching
    // TODO: Build sample JSON and print it

    public static void main(String[] args) {
        System.out.println("Exercise 2: Build the JSON Data Structure!");
    }
}
