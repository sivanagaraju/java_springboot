# Progressive Quiz Drill — Functional Java

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** What is the difference between `map()` and `flatMap()` on a Stream? Give a concrete example where `map()` produces `Stream<Stream<T>>` and `flatMap()` fixes it.

**Q2.** `Optional.orElse(default)` vs `Optional.orElseGet(() -> default)` — when do they behave differently? Which should you prefer?

**Q3.** Name the four standard functional interface types in `java.util.function`. What are their method signatures?

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** Given `List<Order> orders` where each order has `List<LineItem> items`, write a stream pipeline that returns the total value of all items across all orders. Show the pipeline step by step and explain why `flatMap` is needed.

**Q5.** A colleague writes: `Stream<String> s = list.stream(); s.filter(...); s.map(...); s.collect(...)`. This throws `IllegalStateException`. Why? Fix it. What does "stream pipelines are lazy and terminal operations are eager" mean?

**Q6.** Write a method `Optional<User> findUserByEmail(String email)` that queries a repository and returns an `Optional`. Then show a call-site that: (1) gets the user's display name if present, (2) throws `UserNotFoundException` if absent, (3) returns a default "Guest" name without throwing.

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code:
```java
List<String> result = new ArrayList<>();
list.parallelStream()
    .filter(s -> s.length() > 3)
    .forEach(result::add);
```
What's the concurrency bug? How do you fix it?

**Q8.** Code:
```java
Optional<String> opt = Optional.of("hello");
String upper = opt.map(String::toUpperCase)
                  .orElse(null);
```
Is this correct? What anti-pattern does `orElse(null)` indicate?

**Q9.** Code:
```java
long count = IntStream.range(0, 1_000_000)
    .parallel()
    .filter(n -> n % 2 == 0)
    .count();
```
Does this produce the correct answer? Is it faster than sequential? When would `parallel()` NOT help here?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're building a data processing pipeline for an e-commerce platform. Given a `Stream<RawEvent>` from Kafka (potentially millions of events per second), you need to: (1) filter out malformed events; (2) group by user ID; (3) for each user, compute total spend in the last 30 days; (4) emit alerts for users over $10,000 spend; (5) the pipeline must handle backpressure. Why is `Stream` the wrong tool here? What should you use instead? Compare `Stream` vs reactive streams (`Flux`/`Mono` from Project Reactor) on lazy evaluation, backpressure, async execution, and error handling.

---

## Answer Key

**A1.** `map()` applies a function and wraps result in the stream — if the function returns a `Stream`, you get `Stream<Stream<T>>`. Example: `Stream<List<String>> nested = orders.stream().map(Order::getTags)` — each order has a tag list, result is a stream of lists. `flatMap()` applies the function AND flattens one level: `orders.stream().flatMap(o -> o.getTags().stream())` — result is `Stream<String>`, all tags from all orders in one flat stream. Mnemonic: `flatMap` = `map` + `flatten`.

**A2.** `orElse(default)` evaluates `default` EAGERLY — the default expression is computed even if the Optional is present. `orElseGet(() -> default)` evaluates the supplier LAZILY — only computed if Optional is empty. Difference matters when default is expensive: `opt.orElse(expensiveDb.query())` always runs the query. `opt.orElseGet(() -> expensiveDb.query())` only runs if empty. Prefer `orElseGet` for expensive defaults. For constants (literals, static values), `orElse` is fine and slightly more readable.

**A3.** Four types: `Function<T,R>` — `R apply(T t)`, transforms T to R. `Predicate<T>` — `boolean test(T t)`, returns boolean for filtering. `Consumer<T>` — `void accept(T t)`, consumes value, no return. `Supplier<T>` — `T get()`, produces value, no input. Bonus: `BiFunction<T,U,R>`, `UnaryOperator<T>` (Function where T=R), `BinaryOperator<T>` (BiFunction where T=U=R).

**A4.** `double total = orders.stream()` — stream of Order objects. `.flatMap(order -> order.getItems().stream())` — flatten to stream of all LineItems. `.mapToDouble(LineItem::getValue)` — extract value as primitive double stream (avoids boxing). `.sum()` — terminal operation. Why `flatMap`: each order has multiple items; `map(Order::getItems)` gives `Stream<List<LineItem>>`; `flatMap` flattens to `Stream<LineItem>`.

**A5.** A stream can only be consumed ONCE. `s.filter(...)` starts an intermediate operation and marks the stream as consumed. Any further operation on the same `s` reference throws `IllegalStateException: stream has already been operated upon or closed`. Fix: chain everything: `list.stream().filter(...).map(...).collect(...)`. Laziness: intermediate operations (`filter`, `map`) build a pipeline description but execute nothing. Terminal operations (`collect`, `count`, `forEach`) trigger actual processing — the entire pipeline executes in a single pass over the data.

**A6.** Method: `return userRepository.findByEmail(email)`. Usage:
```java
// Get name if present
String displayName = findUserByEmail(email)
    .map(User::getDisplayName)
    .orElse("Guest");

// Throw if absent
User user = findUserByEmail(email)
    .orElseThrow(() -> new UserNotFoundException("No user: " + email));

// Default without throwing — already shown above with orElse("Guest")
```
Anti-pattern to avoid: `if (opt.isPresent()) { return opt.get(); }` — use `orElse`, `map`, or `orElseThrow` instead. `Optional.get()` without `isPresent()` throws `NoSuchElementException`.

**A7.** `result::add` on an `ArrayList` is not thread-safe. Multiple threads call `add()` concurrently — race condition on the internal array and size counter. May result in null entries, wrong size, or `ArrayIndexOutOfBoundsException`. Fix 1: `List<String> result = stream.filter(...).collect(Collectors.toList())` — use a collector instead of `forEach`. Fix 2: if you must use `forEach`, use `CopyOnWriteArrayList` or `Collections.synchronizedList()`. Fix 3: `stream.filter(...).toList()` (Java 16+). Collectors are the correct way to accumulate parallel stream results.

**A8.** Technically correct but anti-pattern. `orElse(null)` defeats the entire purpose of `Optional` — you're back to nullable reference. The call-site now has to null-check `upper`. Better: use `orElse("")`, `orElse("default")`, or restructure to handle absence explicitly. `Optional` should never return null from `orElse` — it reintroduces the problem it was designed to solve.

**A9.** Correct answer — the count is accurate. Whether it's faster depends on: available CPU cores (parallel helps if N cores > 1), overhead of splitting/merging. For simple stateless operations like `filter + count` on 1M ints, parallel CAN be faster on multicore machines. It would NOT help if: (1) the stream source is not efficiently splittable (e.g., `Stream.iterate`); (2) the operation is I/O bound (parallel doesn't help I/O); (3) the overhead of thread coordination exceeds computation time (small data sets, < ~10K elements). For `IntStream` of primitives with no boxing, sequential is already fast.

**A10.** `Stream` limitations: no backpressure (consumer can't signal producer to slow down), synchronous/blocking, no built-in retry or error recovery per element, doesn't model time or async operations. Use **Project Reactor** (`Flux<RawEvent>`): lazy and async, built-in backpressure via reactive streams spec, operators: `filter()`, `groupBy()`, `window(Duration.ofDays(30))`, `flatMap()` with concurrency control, `doOnError()` / `onErrorResume()`. Comparison: Stream — synchronous, no backpressure, eager terminal, single JVM thread or fork-join for parallel. Flux — async, backpressure via `request(n)`, subscription model, composable error handling, compatible with Netty/WebFlux non-blocking I/O. For Kafka + millions/sec: Reactor is correct; `Stream` would block the consumer thread and cause consumer lag.
