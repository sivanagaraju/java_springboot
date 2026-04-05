# File I/O & Serialization — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting these exercises.

## Exercises

### Ex01 — File Word Counter (`Ex01_FileWordCounter.java`)
Count word frequencies in a text file using NIO.2 `Files.lines()` and Streams. Print top 10 and write results to output file.

**Skills:** NIO.2, `Files.lines()`, `Stream`, `flatMap`, `Collectors.groupingBy`
**Time estimate:** 25 minutes

### Ex02 — Config Parser (`Ex02_ConfigParser.java`)
Parse a `.properties`-style config file into a `Map<String, String>`. Support `get()`, `getOrDefault()`, and `getInt()`.

**Skills:** `BufferedReader`, line parsing, `split("=", 2)`, `Map`
**Time estimate:** 20 minutes

## Solutions
See `solutions/` directory. Attempt each exercise before viewing.

## Key Points to Demonstrate
- `Files.lines()` vs `Files.readAllLines()` — when each is appropriate
- `try-with-resources` for all I/O resources
- `BufferedReader` wrapping for performance
- NIO.2 `Path.of()` vs legacy `new File()`

## Connection to Spring Boot
- Ex01 → How Spring Boot reads application logs and reports
- Ex02 → How Spring loads `application.properties` (`PropertiesPropertySource`)
