# Functional Java — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting.

## Exercises

### Ex01 — Stream Practice (`Ex01_StreamPractice.java`)
Process a list of `Student` records using Stream pipelines: `getTopStudents`, `averageGpaByMajor`, `countByGrade`, `getFlattenedCourses`.

**Skills:** `filter`, `map`, `flatMap`, `sorted`, `groupingBy`, `averagingDouble`, `distinct`
**Time estimate:** 35 minutes

### Ex02 — Optional Chaining (`Ex02_OptionalChaining.java`)
Navigate `User → Profile → Address → City` chains using `Optional.flatMap()`. Implement `getUserCity`, `getUserDisplayName`, `processValidUsers`.

**Skills:** `Optional.flatMap`, `Optional.or()`, `Optional::stream`, `orElse` vs `orElseGet`
**Time estimate:** 25 minutes

## Solutions
See `solutions/` directory after attempting.

## Connection to Spring Boot
- Stream pipelines → Spring Data `Page<T>` processing, DTO transformation in service layer
- Optional → Spring Data `findById()` returns `Optional<T>` — use `flatMap/map/orElseThrow`
- `Collectors.groupingBy` → aggregation in analytics/reporting endpoints
