# File I/O & Serialization — One-Page Cheat Sheet

## Stream Selection Guide

```
Data type?
  Raw bytes (images, zips, network) → InputStream / OutputStream
  Text / characters                 → Reader / Writer

Always wrap with Buffered*:
  new BufferedReader(new FileReader(path))
  new BufferedWriter(new FileWriter(path))
  new BufferedInputStream(new FileInputStream(path))

Always use try-with-resources:
  try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;
      while ((line = br.readLine()) != null) { ... }
  }  // auto-closes even on exception
```

## NIO.2 Quick Reference

| Operation | NIO.2 Code |
|-----------|-----------|
| Read whole file | `Files.readString(Path.of("file.txt"))` |
| Read lines | `Files.readAllLines(path)` or `Files.lines(path)` (lazy) |
| Write whole file | `Files.writeString(path, content)` |
| Write lines | `Files.write(path, lines)` |
| Check exists | `Files.exists(path)` |
| Create directories | `Files.createDirectories(path)` |
| List directory | `Files.list(dirPath)` → `Stream<Path>` |
| Walk tree | `Files.walk(root)` → `Stream<Path>` |
| Copy file | `Files.copy(src, dst, REPLACE_EXISTING)` |
| Delete | `Files.deleteIfExists(path)` |

## Jackson ObjectMapper Quick Reference

```java
ObjectMapper mapper = new ObjectMapper();

// Serialize
String json = mapper.writeValueAsString(object);
mapper.writeValue(new File("out.json"), object);

// Deserialize
User user = mapper.readValue(jsonString, User.class);
List<User> users = mapper.readValue(json, new TypeReference<List<User>>() {});

// Config
mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
```

## Jackson Annotations

| Annotation | Purpose |
|-----------|---------|
| `@JsonProperty("user_name")` | Map field to different JSON key |
| `@JsonIgnore` | Exclude field from JSON |
| `@JsonFormat(pattern="yyyy-MM-dd")` | Date format |
| `@JsonInclude(NON_NULL)` | Omit null fields |
| `@JsonAlias({"name","full_name"})` | Accept multiple input names |
| `@JsonCreator` + `@JsonProperty` | Custom deserialization constructor |

## Python Bridge

| Java | Python |
|------|--------|
| `BufferedReader.readLine()` | `f.readline()` (buffered by default) |
| `Files.readString(path)` | `Path(p).read_text()` |
| `Files.lines(path)` (lazy stream) | `for line in open(path):` |
| `ObjectMapper.readValue()` | `json.loads()` / Pydantic `.parse_raw()` |
| `@JsonIgnore` | Pydantic `Field(exclude=True)` |
| `try-with-resources` | `with open(...) as f:` |

## Common Traps

```
TRAP 1: FileReader without BufferedReader
  1000x slower — each read = syscall
  Fix: always wrap with BufferedReader

TRAP 2: Not closing streams
  File descriptor leak → "Too many open files"
  Fix: always use try-with-resources

TRAP 3: Missing serialVersionUID
  Adding a field → InvalidClassException on old data
  Fix: private static final long serialVersionUID = 1L;

TRAP 4: Deserializing untrusted data with ObjectInputStream
  Remote code execution via gadget chains
  Fix: use JSON (Jackson) instead

TRAP 5: Files.writeString without creating parent dirs
  NoSuchFileException if parent directory missing
  Fix: Files.createDirectories(path.getParent()) first

TRAP 6: Loading entire large file into memory
  Files.readAllLines(path) loads ALL lines → OOM for large files
  Fix: Files.lines(path) returns a lazy Stream<String>
```
