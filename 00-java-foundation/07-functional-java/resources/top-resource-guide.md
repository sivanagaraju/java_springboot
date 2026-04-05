# Top Resource Guide — Functional Java

## Official Documentation

- **[java.util.function Package](https://docs.oracle.com/en/java/docs/api/java.base/java/util/function/package-summary.html)** — All 43 functional interfaces with signatures
- **[Stream Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/stream/Stream.html)** — Every stream operation: lazy, stateful, terminal
- **[Collectors Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/stream/Collectors.html)** — `groupingBy`, `partitioningBy`, `joining`, `toMap`, `teeing`
- **[Optional Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/Optional.html)** — Every method including Java 9 `ifPresentOrElse`, Java 11 `isEmpty`

## Books

1. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 42: Prefer lambdas to anonymous classes
   - Item 43: Prefer method references to lambdas (when clearer)
   - Item 44: Favor the use of standard functional interfaces
   - Item 45: Use streams judiciously (don't overuse)
   - Item 46: Prefer side-effect-free functions in streams
   - Item 55: Return optionals judiciously

2. **Modern Java in Action** — Urma, Fusco, Mycroft (Manning)
   - Best comprehensive resource for Java 8+ functional programming
   - Chapters on streams, Optional, CompletableFuture, reactive streams

## Blog Posts

- **[Baeldung: Java 8 Streams](https://www.baeldung.com/java-8-streams)** — Complete guide with all operations and collectors
- **[Baeldung: Optional in Java](https://www.baeldung.com/java-optional)** — Correct usage, anti-patterns, Spring integration
- **[Baeldung: Functional Interfaces](https://www.baeldung.com/java-8-functional-interfaces)** — All built-in interfaces with composition examples
- **[Baeldung: Java Method References](https://www.baeldung.com/java-method-references)** — All 4 types with clear examples

## Key Concepts to Verify You Know

- [ ] Why `orElse(default)` is eager and `orElseGet(() -> default)` is lazy — critical for expensive defaults
- [ ] `flatMap` vs `map`: `flatMap` applies function AND flattens one level — key for nested collections
- [ ] Stream operations that break laziness: `sorted()`, `distinct()`, `limit()` on infinite streams can buffer
- [ ] Why stateful lambdas in parallel streams cause race conditions — lambdas must be stateless and non-interfering
- [ ] `Collectors.toMap()` throws `IllegalStateException` on duplicate keys — use merge function overload
- [ ] `Optional` should only be used as a return type — not as field type, parameter type, or in Collections (Bloch Item 55)
- [ ] Method references can be less readable when the parameter name aids understanding — prefer lambda in those cases (Bloch Item 43)
