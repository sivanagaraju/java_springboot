# Java New Features (9–21) — Mind Map

```markmap
# Java New Features

## Java 9 (2017)
### Module System
- module-info.java
- exports, requires
### Collection Factories
- List.of(), Set.of(), Map.of()
- Immutable collections
### Optional Enhancements
- ifPresentOrElse()
- or()
- stream()
### Private Interface Methods

## Java 10 (2018)
### var Keyword
- Local variable type inference
- Cannot use for fields/params
- Still statically typed!
### Unmodifiable Collectors
- toUnmodifiableList()

## Java 11 (LTS)
### String Methods
- isBlank(), strip()
- lines(), repeat()
### HttpClient
- Replaces HttpURLConnection
- Async support
### Files.readString()
### var in lambda

## Java 14–16
### Records
- Immutable data carriers
- Auto: constructor, getters, equals, hashCode, toString
- Replaces Lombok @Value
### Switch Expressions
- Arrow syntax →
- Yield keyword
- Return values
### instanceof Pattern
- if (obj instanceof String s)
- No explicit cast needed
### Text Blocks
- Multi-line strings """
- Preserves formatting
- Great for SQL, JSON

## Java 17 (LTS)
### Sealed Classes
- sealed, permits
- Exhaustive switch
- Controlled inheritance
### Pattern Matching
- Guarded patterns
- Null handling

## Java 21 (LTS)
### Virtual Threads
- Lightweight (Project Loom)
- Thread.ofVirtual()
- Executor.newVirtualThreadPerTask()
- 1M+ concurrent threads
### Sequenced Collections
- SequencedCollection
- getFirst(), getLast()
- reversed()
### Record Patterns
- Destructuring in switch
### String Templates (Preview)
- STR."Hello \\{name}"
```
