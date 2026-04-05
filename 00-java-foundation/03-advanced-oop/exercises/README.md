# Advanced OOP — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting.

## Exercises

### Ex01 — Generic Pair (`Ex01_GenericPair.java`)
Build a type-safe `Pair<K, V>` generic class with bounded type parameters and utility methods.

**Skills:** Generics, type bounds, wildcard parameters
**Time estimate:** 15 minutes

### Ex02 — HTTP Status Enum (`Ex02_StatusEnum.java`)
Build `HttpStatus` enum with code, message fields, and `isError()` method. Demonstrates enums as full objects.

**Skills:** Enum with fields/constructors, `values()`, `valueOf()`, enum in switch
**Time estimate:** 15 minutes

## Solutions
See `solutions/` directory after attempting.

## Connection to Spring Boot
- Ex01 → `ResponseEntity<T>`, `Optional<T>`, `Page<T>` — Spring's generic containers
- Ex02 → `HttpStatus` enum in Spring MVC (`ResponseEntity.status(HttpStatus.NOT_FOUND)`)
