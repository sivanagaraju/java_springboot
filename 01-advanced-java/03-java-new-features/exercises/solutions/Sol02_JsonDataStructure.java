import java.util.*;
import java.util.stream.*;

/**
 * ╔══════════════════════════════════════════════════════════════════════╗
 * ║  SOLUTION 2: JSON-like Data Structure                               ║
 * ║  Sealed interface + records + pattern matching for JSON modeling    ║
 * ║                                                                      ║
 * ║  TYPE HIERARCHY:                                                     ║
 * ║                                                                      ║
 * ║   sealed interface JsonValue                                        ║
 * ║     ├── record JsonString(String value)                             ║
 * ║     ├── record JsonNumber(double value)                             ║
 * ║     ├── record JsonBool(boolean value)                              ║
 * ║     ├── record JsonNull()                                           ║
 * ║     ├── record JsonArray(List<JsonValue> elements)                  ║
 * ║     └── record JsonObject(Map<String, JsonValue> fields)            ║
 * ║                                                                      ║
 * ║  OUTPUT EXAMPLE:                                                     ║
 * ║   {"name": "John", "age": 25, "active": true, "address": null}      ║
 * ╚══════════════════════════════════════════════════════════════════════╝
 */

/**
 * Demonstrates recursive sealed interface modeling.
 * This is structurally identical to how Jackson's JsonNode hierarchy works:
 * TextNode, IntNode, BooleanNode, NullNode, ArrayNode, ObjectNode.
 */
public class Sol02_JsonDataStructure {

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 1: Sealed type hierarchy — all JSON value types
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Sealed root interface — models any JSON value.
     * The 6 permitted types cover the complete JSON specification (RFC 8259).
     */
    sealed interface JsonValue permits
        JsonString, JsonNumber, JsonBool, JsonNull, JsonArray, JsonObject {}

    /** JSON string: "hello" */
    record JsonString(String value) implements JsonValue {
        public JsonString {
            Objects.requireNonNull(value, "JSON string value must not be null");
        }
    }

    /** JSON number: 42 or 3.14 */
    record JsonNumber(double value) implements JsonValue {}

    /** JSON boolean: true or false */
    record JsonBool(boolean value) implements JsonValue {}

    /** JSON null: null */
    record JsonNull() implements JsonValue {}

    /** JSON array: [elem1, elem2, ...] */
    record JsonArray(List<JsonValue> elements) implements JsonValue {
        public JsonArray {
            // WHY copyOf: defensive copy prevents external mutation of the list
            elements = List.copyOf(elements);
        }
    }

    /** JSON object: {"key": value, ...} */
    record JsonObject(Map<String, JsonValue> fields) implements JsonValue {
        public JsonObject {
            // WHY copyOf: LinkedHashMap for insertion order; immutable after construction
            fields = Collections.unmodifiableMap(new LinkedHashMap<>(fields));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 2: stringify — recursive pattern matching converts to JSON string
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Converts any JsonValue to its JSON string representation.
     * Uses exhaustive pattern matching — no default branch needed.
     *
     * @param value the JSON value to stringify
     * @return JSON string representation
     */
    static String stringify(JsonValue value) {
        return switch (value) {
            // WHY escaped quotes: JSON strings require double-quote delimiters
            case JsonString s  -> "\"" + escapeString(s.value()) + "\"";

            // WHY format: 42.0 → "42" (not "42.0") for integers stored as double
            case JsonNumber n  -> n.value() == Math.floor(n.value()) && !Double.isInfinite(n.value())
                ? String.valueOf((long) n.value())
                : String.valueOf(n.value());

            case JsonBool b    -> String.valueOf(b.value());
            case JsonNull ignored -> "null";    // WHY ignored: we don't use the instance

            case JsonArray arr -> arr.elements().stream()
                .map(Sol02_JsonDataStructure::stringify)   // recursive!
                .collect(Collectors.joining(", ", "[", "]"));

            case JsonObject obj -> obj.fields().entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\": " + stringify(e.getValue())) // recursive!
                .collect(Collectors.joining(", ", "{", "}"));
        };
    }

    /**
     * Escapes special characters in JSON strings.
     * Handles the most common cases for correctness.
     *
     * @param s raw string value
     * @return escaped string safe for JSON embedding
     */
    private static String escapeString(String s) {
        // WHY escape: unescaped quotes inside strings break JSON syntax
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 3: Builder API — fluent construction of JsonObject
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fluent builder for {@link JsonObject}.
     * Provides type-specific methods to reduce casting at call sites.
     */
    static class ObjectBuilder {

        // WHY LinkedHashMap: preserves key insertion order in the output
        private final Map<String, JsonValue> fields = new LinkedHashMap<>();

        /** Adds a string field. */
        public ObjectBuilder str(String key, String value) {
            fields.put(key, new JsonString(value));
            return this;
        }

        /** Adds a numeric field. */
        public ObjectBuilder num(String key, double value) {
            fields.put(key, new JsonNumber(value));
            return this;
        }

        /** Adds a boolean field. */
        public ObjectBuilder bool(String key, boolean value) {
            fields.put(key, new JsonBool(value));
            return this;
        }

        /** Adds a null field. */
        public ObjectBuilder nil(String key) {
            fields.put(key, new JsonNull());
            return this;
        }

        /** Adds any JsonValue field. */
        public ObjectBuilder val(String key, JsonValue value) {
            fields.put(key, value);
            return this;
        }

        /** Builds the immutable JsonObject. */
        public JsonObject build() {
            return new JsonObject(fields);
        }
    }

    /** Creates a new ObjectBuilder. */
    static ObjectBuilder obj() {
        return new ObjectBuilder();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LAYER 4: Main — demonstrates construction and stringification
    // ─────────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Build a JSON object using the fluent builder
        JsonObject user = obj()
            .str("name", "John")
            .num("age", 25)
            .bool("active", true)
            .nil("address")
            .build();

        System.out.println(stringify(user));

        // Build a JSON array
        JsonArray numbers = new JsonArray(List.of(
            new JsonString("hello"),
            new JsonNumber(42),
            new JsonBool(true),
            new JsonNull()
        ));

        System.out.println(stringify(numbers));

        // Nested object
        JsonObject nested = obj()
            .str("name", "Alice")
            .val("scores", new JsonArray(List.of(
                new JsonNumber(95), new JsonNumber(87), new JsonNumber(92)
            )))
            .val("address", obj()
                .str("city", "New York")
                .str("country", "US")
                .build()
            )
            .build();

        System.out.println(stringify(nested));

        // Pattern matching to extract values
        JsonValue value = new JsonString("extracted");
        if (value instanceof JsonString s) {
            System.out.println("String value: " + s.value());
        }

        // Exhaustive switch to get Java-native value
        Object nativeValue = switch (value) {
            case JsonString s  -> s.value();
            case JsonNumber n  -> n.value();
            case JsonBool b    -> b.value();
            case JsonNull ignored -> null;
            case JsonArray arr -> arr.elements();
            case JsonObject obj -> obj.fields();
        };
        System.out.println("Native value: " + nativeValue);
    }
}

/* ─────────────────────────────────────────────────────────────────────────────
   COMMON MISTAKES

   MISTAKE 1: default in exhaustive switch on sealed type
     Defeats compile-time exhaustiveness checking.
     Remove default — let the compiler verify all 6 variants are covered.

   MISTAKE 2: Mutable List/Map in records
     record JsonArray(List<JsonValue> elements) — if elements is mutable,
     callers can modify the array after construction.
     Fix: List.copyOf(elements) in compact constructor.

   MISTAKE 3: Not escaping strings
     "value": "it's a "quote""  ← broken JSON
     Always escape " → \" and \ → \\ before embedding in JSON strings.

   MISTAKE 4: Not handling integer display for numbers
     42.0 vs 42 — JavaScript JSON.stringify outputs "42" for integer-valued doubles.
     Fix: check if value == Math.floor(value) and format as long.

   MISTAKE 5: Pattern variable named same as class
     case JsonNull n -> "null"  ← 'n' is never used — use 'ignored' to signal intent
     case JsonNull ignored -> "null"  ← clear that the instance is not needed
   ───────────────────────────────────────────────────────────────────────────── */
