# File I/O & Serialization — Mind Map

```markmap
# File I/O & Serialization

## File I/O Basics
### Byte Streams
- InputStream / OutputStream
- FileInputStream / FileOutputStream
- read() returns -1 at EOF
### Character Streams
- Reader / Writer
- FileReader / FileWriter
- Character encoding
### try-with-resources
- AutoCloseable
- Implicit finally
- Multiple resources

## Buffered Streams
### Why Buffer?
- Disk I/O is slow (ms vs ns)
- Batch reads: 8KB default
- 100x performance gain
### BufferedReader
- readLine() convenience
- Wraps any Reader
### BufferedWriter
- flush() semantics
- Auto-flush on close

## NIO.2 API (Java 7+)
### Path
- Path.of() factory
- resolve, relativize
- vs File (legacy)
### Files utility
- readAllLines()
- readString() (Java 11)
- walk() directory tree
- copy, move, delete
### WatchService
- File system events
- CREATE, MODIFY, DELETE

## Serialization
### Java Serialization
- Serializable marker
- serialVersionUID
- ObjectOutputStream
- ⚠️ Security risks!
### Alternatives
- JSON (Jackson)
- Protocol Buffers
- Avro

## JSON Processing
### Jackson
- ObjectMapper
- readValue / writeValue
- @JsonProperty
- @JsonIgnore
- Custom serializers
### Spring Integration
- @RequestBody → deserialize
- @ResponseBody → serialize
- HttpMessageConverter
```
