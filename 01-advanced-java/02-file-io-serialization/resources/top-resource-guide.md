# Top Resource Guide — File I/O & Serialization

## Official Documentation

- **[java.nio.file.Files Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/nio/file/Files.html)** — Complete NIO.2 API reference. Bookmark this for production code.
- **[Jackson Documentation](https://github.com/FasterXML/jackson-docs)** — Official Jackson ObjectMapper documentation, annotations reference, and configuration guide
- **[Spring Boot Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)** — `spring.jackson.*` properties to configure global Jackson behavior

## Books (Ordered by Priority)

1. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 85: Prefer alternatives to Java serialization
   - Item 86: Implement Serializable with great caution
   - Items 87-90: Custom serialization techniques
   - The authoritative word on why to avoid Java's native serialization.

2. **Java: The Complete Reference, 12th Edition** — Herbert Schildt
   - Chapter 20: I/O, Try-with-Resources, and NIO.2 — comprehensive but readable

3. **Java I/O, 2nd Edition** — Elliotte Rusty Harold (O'Reilly)
   - Deep dive into the entire Java I/O system. More detailed than needed day-to-day, but excellent reference.

## Blog Posts

- **[Baeldung: Java NIO.2 File API](https://www.baeldung.com/java-nio-2-file-api)** — Best practical guide to `Path`, `Files`, and `WatchService`
- **[Baeldung: Jackson ObjectMapper Guide](https://www.baeldung.com/jackson-object-mapper-tutorial)** — Most comprehensive Jackson tutorial, well-indexed
- **[Baeldung: Java Serialization](https://www.baeldung.com/java-serialization)** — Covers `Serializable`, `serialVersionUID`, `transient`, `readObject`/`writeObject`
- **[Snyk: Java Deserialization Vulnerabilities](https://learn.snyk.io/lesson/insecure-deserialization/)** — Security context for why to avoid native deserialization with untrusted data

## Videos

- **[Java Brains: Java I/O series](https://www.youtube.com/@JavaBrains)** — Clear walkthrough of streams, readers, NIO
- **[Devoxx: Jackson in Depth](https://www.youtube.com/results?search_query=devoxx+jackson+java)** — Conference talk covering advanced Jackson patterns

## Interactive Practice

- **[Baeldung: Java I/O Exercises](https://www.baeldung.com/java-io)** — Series of practical examples to try yourself
- **[HackerRank Java Advanced](https://www.hackerrank.com/domains/java)** — Java I/O challenges under "Advanced" section

## Key Concepts to Verify You Know

- [ ] When to use `InputStream` vs `Reader` (bytes vs characters)
- [ ] Default buffer size of `BufferedReader` (8192 chars) and when to increase it
- [ ] Why `Files.lines()` is better than `Files.readAllLines()` for large files
- [ ] The three NIO.2 file options: `StandardOpenOption.APPEND`, `CREATE`, `TRUNCATE_EXISTING`
- [ ] Why Java native serialization is a security risk with untrusted sources
- [ ] How `@JsonCreator` enables custom Jackson deserialization
- [ ] The difference between `mapper.readTree()` (JsonNode) and `readValue()` (typed)
