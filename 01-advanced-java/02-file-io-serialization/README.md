# 02 — File I/O & Serialization

> **Reading, writing, and converting data — the bridge between memory and disk.**

## Why This Matters

Every Spring Boot app reads config files, processes uploads, exports reports, and converts JSON. This module teaches the I/O foundations that make `@RequestBody` and `Resource` work.

## Topic Map

| Topic | What You'll Learn | Spring Connection |
|-------|-------------------|-------------------|
| **File I/O Basics** | byte/char streams, try-with-resources | `Resource`, `ResourceLoader` |
| **Buffered Streams** | Performance via buffering | Why Spring defaults to buffered I/O |
| **NIO.2 API** | `Path`, `Files`, `WatchService` | `ClassPathResource`, file watching |
| **Serialization** | Object ↔ bytes | Why Spring avoids Java serialization |
| **JSON Processing** | Jackson `ObjectMapper` | `@RequestBody`, `@ResponseBody` |

## Python → Java

| Python | Java |
|--------|------|
| `open("f.txt", "r")` | `new FileReader("f.txt")` or `Files.readString(Path.of("f.txt"))` |
| `with open(...) as f:` | `try (var r = new FileReader(...))` |
| `pathlib.Path` | `java.nio.file.Path` |
| `json.dumps(obj)` | `objectMapper.writeValueAsString(obj)` |
| `json.loads(str)` | `objectMapper.readValue(str, Type.class)` |
| `pickle.dump(obj)` | `ObjectOutputStream` (but avoid in production!) |
