# Top Resource Guide — Java New Features (Java 16–21)

## Official Documentation

- **[JEP 395: Records](https://openjdk.org/jeps/395)** — The original proposal. Explains design decisions and trade-offs.
- **[JEP 409: Sealed Classes](https://openjdk.org/jeps/409)** — Motivation, grammar, and exhaustiveness rules.
- **[JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)** — Complete spec for Java 21 pattern switch.
- **[JEP 444: Virtual Threads](https://openjdk.org/jeps/444)** — The definitive guide to virtual thread architecture, pinning, and limitations.
- **[Spring Boot 3.2 Virtual Threads](https://spring.io/blog/2022/10/11/embracing-virtual-threads)** — Official Spring blog on virtual thread integration.

## Books (Ordered by Priority)

1. **Modern Java in Action, 2nd Edition** — Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft
   - Best coverage of Java 8–17 features in context. Chapters on streams, Optional, pattern matching.
   - Gaps: doesn't cover Java 21 virtual threads (too new).

2. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 17: Minimize mutability (context for why records exist)
   - Items on data classes and value types

3. **Java 21 Features** — Sander Mak (Pluralsight course — not a book, but exceptional)
   - Covers records, sealed classes, pattern matching, virtual threads with real examples.

## Blog Posts

- **[Baeldung: Java Records](https://www.baeldung.com/java-record-keyword)** — Comprehensive guide with all edge cases
- **[Baeldung: Sealed Classes](https://www.baeldung.com/java-sealed-classes-interfaces)** — Good examples with sealed + records + pattern matching together
- **[Baeldung: Virtual Threads](https://www.baeldung.com/java-virtual-thread-vs-thread)** — Platform vs virtual thread comparison with benchmarks
- **[Nicolai Parlog (nipafx.dev)](https://nipafx.dev/)** — Best Java 21 feature coverage on the internet. Every post is high quality.
- **[InfoQ: Project Loom (Virtual Threads)](https://www.infoq.com/articles/java-virtual-threads/)** — Deep architectural explanation of carrier threads, pinning, structured concurrency

## Videos

- **[JEP Café by José Paumard](https://www.youtube.com/playlist?list=PLX8CzqL3ArzXJ2EGftrmz4SzS6NRr6p2n)** — Official Oracle series. Episodes on records, sealed classes, pattern matching are exceptional.
- **[Going further with Java Virtual Threads — Devoxx](https://www.youtube.com/results?search_query=devoxx+virtual+threads+java)** — Conference talks by the Loom team
- **[Java 21 New Features — Amigoscode](https://www.youtube.com/@amigoscode)** — Practical demos with Spring Boot integration

## Interactive Practice

- **[JavaAlmanac.io](https://javaalmanac.io/)** — See which features are available in which Java version, with examples
- **[Exercism Java track](https://exercism.org/tracks/java)** — Exercises that map to modern Java features

## Key Concepts to Verify You Know

- [ ] Why `default` in a sealed-interface switch defeats exhaustiveness checking
- [ ] Why records cannot extend classes (they extend `java.lang.Record`)
- [ ] The three permitted-subtype modifiers: `final`, `sealed`, `non-sealed`
- [ ] Why `synchronized` blocks pin virtual threads to carrier threads
- [ ] The single Spring Boot property to enable virtual threads: `spring.threads.virtual.enabled=true`
- [ ] Compact constructor: when fields are assigned (after the body, automatically)
- [ ] Pattern variable scope: a pattern variable bound in `if (x instanceof String s)` is NOT in scope in the `else` branch
