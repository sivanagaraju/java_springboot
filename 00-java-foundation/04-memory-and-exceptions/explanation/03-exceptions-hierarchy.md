# The Exceptions Hierarchy

At a junior level, Exceptions are errors you print to the console when the program crashes.
To a Java Architect, the Exception Hierarchy explicitly models the exact physical point of catastrophic failure within the system architecture, separating recoverable network anomalies from fatal JVM memory collapses.

## `java.lang.Throwable`

The root of the Java error system is `Throwable`. Only objects inheriting from `Throwable` can be thrown using the `throw` keyword.

`Throwable` branches into two primary subclasses: `Error` and `Exception`.

### 1. `java.lang.Error` (Fatal System Collapse)
- **What it represents:** A catastrophic breakdown of the JVM physical infrastructure.
- **Examples:** `OutOfMemoryError`, `StackOverflowError`, `VirtualMachineError`.
- **Architect Rule:** Never catch an `Error`. If the JVM runs out of physical RAM, your Application code cannot fix it. The application must crash immediately to trigger infrastructure alerts (like Kubernetes restarting the pod).

### 2. `java.lang.Exception` (Application Failures)
- **What it represents:** Logic flaws, missing files, dead databases, or invalid user input.
- **Architect Rule:** These are failures your code might recover from or log gracefully.

The `Exception` tree further splits into two heavily debated categories.

#### A. Checked Exceptions (Compile-Time Validated)
- **Examples:** `IOException`, `SQLException`.
- **Concept:** The compiler forces you to either `catch` the exception or declare `throws` in the method signature.
- **Modern Consensus:** Checked Exceptions are largely considered a failed experiment in modern architecture. Spring Boot, Hibernate, and Kotlin bypass them entirely. They pollute method signatures and break abstraction barriers.

#### B. Unchecked Exceptions (`RuntimeException`)
- **Examples:** `NullPointerException`, `IllegalArgumentException`, `IllegalStateException`.
- **Concept:** The compiler ignores them. You are not forced to catch them.
- **Modern Consensus:** All custom business exceptions in modern microservices should inherit from `RuntimeException`. This keeps method signatures clean and allows global `@ControllerAdvice` handlers to catch them at the API layer.
