# Java Foundation Progressive Quiz Drill

Use this drill after finishing the Java Foundation module once. The rounds get harder on purpose.

```mermaid
flowchart LR
    A[Round 1 Recall] --> B[Round 2 Apply and Compare]
    B --> C[Round 3 Debug the Bug]
    C --> D[Round 4 Staff-Level Scenario]
```

## Round 1 - Core Recall

**Q1.** Why does Java distinguish between `int` and `Integer`, and why does that matter inside collections?

**Q2.** Why is `==` dangerous for `String` comparison, even if it "works" in some demos?

**Q3.** What problem do Java records solve that ordinary mutable classes often create?

**Q4.** What is the difference between `ArrayList` and `HashSet`, and why does the difference matter for Spring applications?

## Round 2 - Apply and Compare

**Q5.** You need to return an API response snapshot from a service. Would you choose a mutable POJO or a record? Explain why.

**Q6.** A method runs CPU-light tasks for hundreds of requests. Would you create a new `Thread` per request or use an `ExecutorService`? Explain why.

**Q7.** You need to store products in insertion order but prevent duplicates. Would `List`, `HashSet`, or `LinkedHashSet` be the best fit? Why?

## Round 3 - Debug the Bug

**Q8.** What is wrong with this code, and why is it risky?

```java
String a = new String("spring");
String b = new String("spring");
if (a == b) {
    System.out.println("same");
}
```

**Q9.** What is wrong with this record usage?

```java
record UserProfile(String name, List<String> roles) { }
```

A caller passes a mutable `ArrayList` into `roles`, then mutates that list later.

**Q10.** What is the concurrency problem here?

```java
for (Task task : tasks) {
    new Thread(task::run).start();
}
```

## Round 4 - Staff-Level Scenario

**Q11.** A Spring Boot team keeps adding shared mutable state into singleton services and sees flaky bugs under load. Which Java Foundation topics should you review first, and why?

**Q12.** A codebase uses records, streams, collections, and executors, but production debugging is still weak. What foundation skills would you require before letting a junior developer touch service-layer concurrency?

---

## Answer Key

### Round 1 - Core Recall

**A1.** `int` is a primitive and `Integer` is an object wrapper. Collections can only hold objects, so autoboxing converts between them. That matters because wrappers can be `null`, have allocation cost, and behave differently from primitives in equality and API calls.

**A2.** `==` compares references, not string content. String literals are interned, so some examples appear to work, but runtime-created strings usually break that assumption. Use `.equals()` for value comparison.

**A3.** Records remove constructor/getter/`equals()`/`hashCode()`/`toString()` boilerplate for immutable data carriers. They make DTO and snapshot intent explicit and reduce mutation bugs at boundaries.

**A4.** `ArrayList` preserves order and allows duplicates. `HashSet` removes duplicates but does not preserve insertion order. In Spring applications, that choice affects endpoint output order, deduplication, and performance assumptions.

### Round 2 - Apply and Compare

**A5.** Choose a record when the response is a stable snapshot. It communicates immutability, generates value semantics automatically, and reduces accidental mutation after the service returns data.

**A6.** Use `ExecutorService`. A thread pool bounds resource usage, reuses worker threads, supports cancellation and shutdown, and scales far better than creating raw threads for every request.

**A7.** `LinkedHashSet` is the best fit because it preserves insertion order and prevents duplicates. `List` preserves order but allows duplicates. `HashSet` removes duplicates but does not preserve stable iteration order.

### Round 3 - Debug the Bug

**A8.** The bug is `a == b`. Those are two different `String` objects, so reference comparison is false even though the content matches. The correct check is `a.equals(b)`.

**A9.** The record is not truly safe if it stores a mutable list directly. The caller can mutate the list later and change the record's visible state. Fix it with defensive copying such as `roles = List.copyOf(roles);` in the compact constructor.

**A10.** The code creates an unbounded number of raw threads. That can exhaust memory, add context-switch overhead, and make shutdown hard. A bounded executor is the safer production choice.

### Round 4 - Staff-Level Scenario

**A11.** Review object lifecycle, mutability, collections, and multithreading first. Singleton Spring services are shared across requests, so Java-level shared-state mistakes become production race conditions quickly.

**A12.** I would require strong understanding of immutability, collection semantics, exception flow, thread safety, and executor lifecycle. Without that foundation, service-layer concurrency turns into hidden data races and poor failure handling.
