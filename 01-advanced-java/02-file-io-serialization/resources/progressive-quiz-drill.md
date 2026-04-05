# Progressive Quiz Drill — File I/O & Serialization

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** A developer uses `new FileReader("data.csv")` in a loop reading 10,000 lines. Performance is terrible — 30 seconds instead of expected < 1 second. What's the cause and the one-line fix?

**Q2.** Code review: `ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()); Object data = ois.readObject();` — this receives data from an untrusted external source. What's the security risk, and what should you use instead?

**Q3.** A class has `private String username; private String password;`. It implements `Serializable`. If you serialize and deserialize it, what happens to `password`? What keyword makes it excluded from serialization?

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** You need to process a 5GB log file line by line to count occurrences of ERROR entries. Which Java I/O approach do you use? Write the 5-line core solution. What happens to memory usage as the file size grows?

**Q5.** Your Spring Boot app needs to read a configuration JSON file at startup and parse it into a `AppConfig` object. Outline the 3 Jackson ObjectMapper approaches for doing this (from simplest to most production-appropriate) and when to use each.

**Q6.** A team is debating: "Should we use Java's `Serializable` to persist session state to Redis?" Argue both sides and give the correct recommendation for a production system.

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code:
```java
FileWriter fw = new FileWriter("output.txt");
for (String line : lines) {
    fw.write(line + "\n");
}
fw.close();
```
Two bugs. Name them.

**Q8.** Code:
```java
public class User implements Serializable {
    private String name;
    private int age;
}
// Later: added 'private String email;' field
```
Old serialized `User` objects are in the database. Deserialization now throws `InvalidClassException`. Why? What's the fix?

**Q9.** NIO.2 code:
```java
Files.writeString(Path.of("config.json"), json);
```
This works locally but fails in production with `NoSuchFileException`. Why?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're designing a file processing microservice that: (1) receives large CSV files (up to 1GB) via S3 event; (2) parses each row into a domain object; (3) validates and enriches each row; (4) writes results to PostgreSQL; (5) outputs a JSON summary report. Design the I/O pipeline specifying: which Java I/O classes to use, buffering strategy, memory management approach, error handling, and how you'd handle partial failures mid-file.

---

## Answer Key

**A1.** `FileReader` is unbuffered — each `readLine()` equivalent causes a syscall. Fix: `new BufferedReader(new FileReader("data.csv"))`. Performance improves 100x because `BufferedReader` reads 8192 chars at once into memory.

**A2.** Java deserialization of untrusted data is a remote code execution vulnerability (CVE-2015-4852, Log4Shell used similar chains). `readObject()` executes code in serialized "gadget chains". Use JSON (Jackson) or Protobuf instead. If you must use Java serialization, implement a `ObjectInputFilter` allowlist.

**A3.** Without modification, `password` IS serialized — it's a security vulnerability. The `transient` keyword excludes it: `private transient String password;`. After deserialization, `password` will be `null`.

**A4.** Use `Files.lines(path)` which returns a lazy `Stream<String>` — reads line by line without loading the whole file: `long errors = Files.lines(Path.of("app.log")).filter(l -> l.contains("ERROR")).count();`. Memory usage stays constant (O(1)) regardless of file size because lines are processed and discarded one at a time.

**A5.** (1) `mapper.readValue(file, AppConfig.class)` — simplest, direct; (2) `@ConfigurationProperties` — Spring Boot native, type-safe, with validation via `@Valid`; (3) `@Value` + custom `@Bean` with `ObjectMapper` — for complex transformations. For production: use `@ConfigurationProperties` — it integrates with Spring's relaxed binding, validation, and property source precedence.

**A6.** For: Java serialization is built-in, no dependency. Against: fragile (any field change breaks compatibility), verbose binary format, security risk, poor debuggability. Recommendation: Use JSON serialization via Jackson. Store as a JSON string in Redis. Survives schema evolution, human-readable for debugging, safe. `redisTemplate.opsForValue().set(key, mapper.writeValueAsString(session))`.

**A7.** Bug 1: No `try-with-resources` — if an exception is thrown mid-loop, `fw.close()` never runs, leaking the file handle. Bug 2: `FileWriter` is unbuffered — each `write()` call hits the OS. Fix: wrap with `BufferedWriter` and use `try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt")))`.

**A8.** Java uses `serialVersionUID` to verify class identity during deserialization. When you add a field, Java auto-computes a new `serialVersionUID` from the class structure, which doesn't match the stored value. Fix: explicitly declare `private static final long serialVersionUID = 1L;` in the class. Never let Java auto-compute it for production classes.

**A9.** `Files.writeString` creates or overwrites the file, but does NOT create parent directories. If `config.json` is in a subdirectory that doesn't exist yet, it throws `NoSuchFileException`. Fix: `Files.createDirectories(path.getParent())` before writing.

**A10.** Pipeline: `S3InputStream` → `BufferedReader` (8192-char buffer) → CSV parser (OpenCSV) stream → `Stream<Row>` → validate/enrich each row → `JdbcTemplate.batchUpdate()` in batches of 1000 → count successes/failures. Memory: process row-by-row, never load entire file. Error handling: skip invalid rows, log them, include error count in summary. Partial failure: use database transactions per batch (REQUIRES_NEW), not one global transaction — partial success is better than complete rollback of a 1GB file.
