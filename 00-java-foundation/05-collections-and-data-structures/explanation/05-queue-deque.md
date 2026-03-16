# Queues and Deques

A `Queue` models an assembly line. A `Deque` models a Double-Ended Queue where you can pull or push from both sides.

## 1. `Queue` Interface (FIFO Pipeline)

**First-In, First-Out (FIFO):** Just like a checkout line at a grocery store.

The standard implementation is `LinkedList`. Because `LinkedList` implements `Queue`, you can cast it natively:

```java
Queue<String> tasks = new LinkedList<>();
tasks.offer("Task 1"); // Adds to tail safely
tasks.offer("Task 2");

String next = tasks.poll(); // Pulls "Task 1" from the head
```

**Architectural Rules:**
- DO NOT use `.add()` and `.remove()`. If the Queue is utterly full or entirely empty, they violently throw Exceptions.
- INSTEAD use `.offer()` and `.poll()`. They explicitly return `false` or `null` gracefully, allowing smooth logic flow without exception handling costs.

## 2. `PriorityQueue` (The Urgency Pipeline)

**Backing Data Structure:** A native binary Heap.

**Architectural Mechanics:**
- Elements are not processed FIFO. They are processed precisely by their sorted Priority.
- Elements explicitly must implement `Comparable` (or you pass a `Comparator`).
- E.g., An emergency VIP support ticket can be inserted last, but it will be immediately polled first.
- Inserts are `O(log N)`. Polls are `O(log N)`.

## 3. `ArrayDeque` (The Stack & Queue Master)

**Backing Data Structure:** A dynamic resizing circular array.

**Architectural Mechanics:**
- Historically, Java developers used `java.util.Stack` for LIFO (Last-In, First-Out) operations. `Stack` extends `Vector` and is violently synchronized natively, making it extremely slow.
- **Architect Rule:** Never use `Stack`. Erase it from your vocabulary. Always use `ArrayDeque`.
- `ArrayDeque` physically performs faster than a `LinkedList` for Queue operations, and remarkably faster than a `Stack` for LIFO operations because it aggressively avoids native thread locks and relies on contiguous block memory.
