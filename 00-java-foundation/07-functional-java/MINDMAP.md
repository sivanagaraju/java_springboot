# Functional Java — Concept Map

```markmap
# Functional Java

## Lambda Expressions
- Syntax: `(params) -> expression`
- Single abstract method (SAM)
- **Functional Interfaces**
  - `Predicate<T>` → boolean test(T)
  - `Function<T,R>` → R apply(T)
  - `Consumer<T>` → void accept(T)
  - `Supplier<T>` → T get()
  - `Comparator<T>` → int compare(T,T)
  - `Runnable` → void run()
- **Method References** (::)
  - Static: `Integer::parseInt`
  - Instance: `String::toLowerCase`
  - Constructor: `ArrayList::new`

## Streams API
- **Source** → `.stream()`, `.of()`, `.generate()`
- **Intermediate** (lazy, return Stream)
  - `filter(Predicate)`
  - `map(Function)`
  - `flatMap(Function)`
  - `sorted(Comparator)`
  - `distinct()`, `limit()`, `skip()`
  - `peek()` (debugging)
- **Terminal** (trigger execution)
  - `collect(Collector)` → List, Map, Set
  - `forEach(Consumer)`
  - `reduce(identity, BinaryOperator)`
  - `count()`, `min()`, `max()`
  - `anyMatch()`, `allMatch()`, `noneMatch()`
  - `findFirst()`, `findAny()`
- **Key principle**: Streams are LAZY
  - Nothing executes until terminal operation
  - *Can only be consumed ONCE*

## Optional<T>
- Replaces null for return types
- **Creation**
  - `Optional.of(value)` (throws if null)
  - `Optional.ofNullable(value)` (safe)
  - `Optional.empty()`
- **Safe Access**
  - `isPresent()` / `isEmpty()`
  - `orElse(default)`
  - `orElseThrow()`
  - `map(Function)`
  - `flatMap(Function)`
  - `ifPresent(Consumer)`
- **Anti-patterns**
  - Never use Optional for fields
  - Never use Optional for parameters
  - Never call `get()` without `isPresent()`

## Interview Hot Spots
- Lazy evaluation in streams
- Stream vs Collection
- When to use parallel streams (almost never)
- Optional vs null checks
- Effectively final in lambdas
```
