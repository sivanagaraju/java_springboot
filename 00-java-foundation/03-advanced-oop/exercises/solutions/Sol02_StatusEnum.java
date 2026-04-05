/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Sol02_StatusEnum.java                                  ║
 * ║  MODULE : 00-java-foundation / 03-advanced-oop                   ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Solution — enum with fields and methods        ║
 * ║  DEMONSTRATES   : enum constructor, custom fields, switch enum   ║
 * ║  PYTHON COMPARE : Python IntEnum / Enum; Java enum is a class    ║
 * ║                                                                  ║
 * ║  ENUM ANATOMY:                                                   ║
 * ║   OK(200,"Success") ──▶ constant with field values               ║
 * ║   getCode() / getMessage() ──▶ instance methods on each constant ║
 * ║   isError() ──▶ computed property (code >= 400)                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises.solutions;

/**
 * HTTP status code enum demonstrating enum-as-class pattern.
 *
 * <p>WHY enum (not int constants): {@code static final int OK = 200} provides
 * no type safety — any integer is accepted where OK is expected.
 * An enum is a distinct type: {@code HttpStatus.OK} can only be an
 * {@code HttpStatus}, never confused with another enum or plain int.
 *
 * <p>Python equivalent:
 * <pre>{@code
 *   from enum import Enum
 *   class HttpStatus(Enum):
 *       OK = (200, "Success")
 *       def __init__(self, code, message): ...
 * }</pre>
 *
 * <p>Critical Java difference: Java enum constants are singleton instances
 * created once at class-loading time. {@code HttpStatus.OK == HttpStatus.OK}
 * is always {@code true} — safe to use {@code ==} for enum comparison.
 */
enum HttpStatus {

    // WHY each constant calls the constructor with (code, message):
    // Enums in Java are special classes. Each constant is an instance
    // created by calling the private constructor below.
    OK(200, "Success"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    SERVER_ERROR(500, "Internal Error");

    // WHY private final: enum fields must be final — constants are immutable.
    // private prevents direct field access from outside.
    private final int code;
    private final String message;

    /**
     * Enum constructor — called once per constant at class initialization.
     *
     * <p>WHY private: Java enum constructors are implicitly private.
     * They can only be called by the enum constants above, never by
     * external code. This enforces the closed-set guarantee.
     *
     * @param code    the HTTP status code
     * @param message the human-readable status message
     */
    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Returns the numeric HTTP status code.
     *
     * @return the HTTP status code (e.g., 200, 404)
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the human-readable status message.
     *
     * @return the status message (e.g., "Not Found")
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether this status represents an error (4xx or 5xx).
     *
     * <p>HTTP convention: 1xx=informational, 2xx=success, 3xx=redirect,
     * 4xx=client error, 5xx=server error.
     *
     * @return {@code true} if code is 400 or greater
     */
    public boolean isError() {
        return this.code >= 400;
    }

    /**
     * Returns a formatted string like {@code "404 Not Found"}.
     *
     * @return formatted HTTP status line
     */
    @Override
    public String toString() {
        return code + " " + message;
    }

    /**
     * Looks up an HttpStatus by its numeric code.
     *
     * <p>Demonstrates a factory-style static method on an enum.
     *
     * @param code the HTTP status code to find
     * @return the matching HttpStatus
     * @throws IllegalArgumentException if no constant has the given code
     */
    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }
}

/**
 * Test harness for the HttpStatus enum.
 */
public class Sol02_StatusEnum {

    /**
     * Demonstrates enum iteration, comparison, and switch.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        System.out.println("--- HTTP Status Enum Solution ---\n");

        // WHY values(): returns all constants in declaration order
        for (HttpStatus status : HttpStatus.values()) {
            System.out.printf("[%d] %s -> isError: %b%n",
                    status.getCode(), status.getMessage(), status.isError());
        }

        // Enum comparison: == is safe and preferred for enums
        // WHY ==: enum constants are singletons — == checks object identity
        // which is exactly what we want. .equals() works too but is redundant.
        System.out.println("\n== comparison: " + (HttpStatus.OK == HttpStatus.OK));        // true
        System.out.println("== comparison: " + (HttpStatus.OK == HttpStatus.NOT_FOUND));  // false

        // Switch on enum — compiler warns if a constant is missing (exhaustiveness)
        HttpStatus current = HttpStatus.NOT_FOUND;
        String category = switch (current) {
            case OK, CREATED -> "Success";
            case BAD_REQUEST, NOT_FOUND -> "Client Error";
            case SERVER_ERROR -> "Server Error";
        };
        System.out.println("\n" + current + " category: " + category);

        // Reverse lookup by code
        System.out.println("\nLookup 201: " + HttpStatus.fromCode(201));
        System.out.println("Lookup 404: " + HttpStatus.fromCode(404));

        // name() and ordinal() — built-in enum methods
        System.out.println("\nname(): " + HttpStatus.NOT_FOUND.name());     // "NOT_FOUND"
        System.out.println("ordinal(): " + HttpStatus.NOT_FOUND.ordinal()); // 3 (0-indexed position)
    }
}

/* ═══════════════════════════════════════════════════════════════════
 * COMMON MISTAKES
 * ═══════════════════════════════════════════════════════════════════
 *
 * MISTAKE 1: Using .equals() instead of == for enum comparison
 *   WRONG: if (status.equals(HttpStatus.OK)) { }   // works but redundant
 *   RIGHT: if (status == HttpStatus.OK) { }         // preferred
 *   Enum constants are singletons — == is always correct and cleaner.
 *
 * MISTAKE 2: Relying on ordinal() for persistence
 *   WRONG: db.save(status.ordinal());   // ordinal = declaration position
 *   PROBLEM: Adding a new constant between OK and NOT_FOUND shifts
 *            all subsequent ordinals — stored values become wrong.
 *   RIGHT: db.save(status.getCode()) or db.save(status.name())
 *
 * MISTAKE 3: Using mutable fields in enum
 *   WRONG: private int code;   (without final)
 *   PROBLEM: Enum constants are shared globally (singletons).
 *            Mutable shared state → thread safety issues.
 *   RIGHT: All enum fields should be private final.
 *
 * MISTAKE 4: Forgetting to handle new constants in switch
 *   When you add UNAUTHORIZED(401, ...) later, every switch without
 *   a 'default' or explicit UNAUTHORIZED case becomes stale.
 *   Use sealed interface + records for truly exhaustive type hierarchies.
 *
 * MISTAKE 5: Enum in JPA entity as ORDINAL (the JPA default)
 *   WRONG: @Enumerated  (defaults to EnumType.ORDINAL)
 *   RIGHT: @Enumerated(EnumType.STRING)
 *   ORDINAL breaks when enum order changes; STRING is stable.
 * ═══════════════════════════════════════════════════════════════════ */
