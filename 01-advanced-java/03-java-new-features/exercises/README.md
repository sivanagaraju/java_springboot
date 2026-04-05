# Java New Features — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting these exercises.

## Exercises

### Ex01 — Shape Calculator (`Ex01_ShapeCalculator.java`)
Build a type-safe geometric shape system using `sealed interface`, `record`, and exhaustive `switch` expressions. Sort shapes by area.

**Skills:** Sealed interfaces, records, pattern matching switch, Heron's formula
**Time estimate:** 30 minutes

### Ex02 — JSON Data Structure (`Ex02_JsonDataStructure.java`)
Model a JSON value type as a sealed interface with record variants (`JsonString`, `JsonNumber`, `JsonArray`, etc.). Implement `stringify()` using pattern matching.

**Skills:** Sealed classes, records, pattern matching, recursive data structures
**Time estimate:** 35 minutes

## Solutions
See `solutions/` directory. Attempt each exercise before viewing.

## Key Points to Demonstrate
- **No `default`** in sealed-interface switches — let the compiler check exhaustiveness
- Records are immutable — all fields `final`, no setters
- Compact constructors for validation
- Text blocks for multi-line output formatting

## Connection to Spring Boot
- Ex01 → Domain model pattern for Spring REST API responses
- Ex02 → How Jackson internally models JSON nodes (`JsonNode` hierarchy)
