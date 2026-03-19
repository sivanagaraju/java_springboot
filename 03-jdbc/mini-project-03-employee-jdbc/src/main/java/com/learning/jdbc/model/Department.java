/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  MODEL — Department Record                                     ║
 * ║  Mini-Project: 03-employee-jdbc / model                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Department(id, name, location)                                ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  Lightweight record for department reference data              ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    @dataclass(frozen=True)                                     ║
 * ║    class Department:                                           ║
 * ║        id: int; name: str; location: str                       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.model;

/**
 * Immutable Department domain model using Java 21 record.
 *
 * <p>Represents a department entity mapped to the {@code departments} table.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   @dataclass(frozen=True)
 *   class Department:
 *       id: int
 *       name: str
 *       location: str
 * </pre>
 *
 * @param id       unique auto-generated identifier
 * @param name     department name (e.g., "Engineering")
 * @param location office location (e.g., "Building A, Floor 3")
 */
public record Department(
        Long id,
        String name,
        String location
) {

    /**
     * Compact constructor — validates required fields.
     */
    public Department {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Department name cannot be null or blank");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Location cannot be null or blank");
        }
    }

    /**
     * Factory method for creating new departments (without ID).
     *
     * @param name     department name
     * @param location office location
     * @return new Department with null id (assigned by database)
     */
    public static Department createNew(String name, String location) {
        return new Department(null, name, location);
    }
}
