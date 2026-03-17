# Advanced Java — Concept Mind Map

```markmap
# Advanced Java

## Design Patterns
### Creational
#### Singleton
- Eager vs Lazy
- Thread-safe (enum)
- Spring @Component
#### Factory Method
- Creator hierarchy
- Spring BeanFactory
#### Builder
- Fluent API
- Lombok @Builder

### Behavioral
#### Observer
- Event-driven
- Spring ApplicationEvent
#### Strategy
- Algorithm swap
- Spring auth providers
#### Template Method
- Hook methods
- JdbcTemplate

### Structural
#### Decorator
- Wrapping layers
- I/O streams
- Filter chains
#### Adapter
- Interface bridge
- Legacy integration

## File I/O & Serialization
### Classic I/O
#### InputStream/OutputStream
- byte[] streams
#### Reader/Writer
- char streams
- Buffering
### NIO.2
#### Path + Files
- Modern API
- Walking directories
#### AsynchronousFileChannel
### Serialization
#### ObjectOutputStream
- Serializable
- serialVersionUID
#### Jackson JSON
- ObjectMapper
- Annotations
- @RequestBody

## Java New Features (9–21)
### Records (16)
- Immutable data
- Compact constructors
- vs Lombok @Data
### Sealed Classes (17)
- Closed hierarchies
- permits clause
- Pattern matching
### Pattern Matching
#### instanceof (16)
- Type + variable
#### switch (21)
- Exhaustiveness
- Guard clauses
### Text Blocks (15)
- Multi-line strings
- Indentation
### Switch Expressions (14)
- Arrow syntax
- Yield keyword
### Virtual Threads (21)
- Project Loom
- Thread.ofVirtual()
- vs Platform threads
```
