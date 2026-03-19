/*
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  MODEL — Employee Record                                       ║
 * ║  Mini-Project: 03-employee-jdbc / model                        ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║                                                                ║
 * ║  Immutable data carrier using Java 21 record:                  ║
 * ║                                                                ║
 * ║  Employee(id, name, department, salary, createdAt)             ║
 * ║       │                                                        ║
 * ║       ▼                                                        ║
 * ║  equals(), hashCode(), toString()  ← auto-generated           ║
 * ║  id(), name(), department(), ...   ← accessor methods          ║
 * ║                                                                ║
 * ║  Python equivalent:                                            ║
 * ║    @dataclass(frozen=True)                                     ║
 * ║    class Employee:                                             ║
 * ║        id: int; name: str; ...                                 ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.jdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Immutable Employee domain model using Java 21 record.
 *
 * <p>Records automatically generate:
 * <ul>
 *   <li>{@code equals()} and {@code hashCode()} — component-based</li>
 *   <li>{@code toString()} — includes all fields</li>
 *   <li>Accessor methods — {@code id()}, {@code name()}, etc.</li>
 *   <li>Canonical constructor — validates all fields</li>
 * </ul>
 *
 * <p><b>Python equivalent:</b> {@code @dataclass(frozen=True)} class with type hints
 *
 * @param id         unique auto-generated identifier
 * @param name       employee full name
 * @param department department name
 * @param salary     salary amount (BigDecimal for precision)
 * @param createdAt  timestamp of record creation
 */
public record Employee(
        Long id,
        String name,
        String department,
        BigDecimal salary,
        LocalDateTime createdAt
) {

    /**
     * Compact constructor — validates business rules.
     *
     * <pre>
     *   new Employee(null, "Alice", "Eng", 95000, now)
     *       │
     *       ▼
     *   Compact constructor validates:
     *       name != null        ✓
     *       department != null  ✓
     *       salary > 0          ✓
     * </pre>
     */
    public Employee {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Employee name cannot be null or blank");
        }
        if (department == null || department.isBlank()) {
            throw new IllegalArgumentException("Department cannot be null or blank");
        }
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be positive");
        }
    }

    /**
     * Factory method for creating new employees (without ID and timestamp).
     *
     * @param name       employee name
     * @param department department name
     * @param salary     salary amount
     * @return new Employee with null id and createdAt (assigned by database)
     */
    public static Employee createNew(String name, String department, BigDecimal salary) {
        return new Employee(null, name, department, salary, null);
    }
}
