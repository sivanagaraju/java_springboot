# Java Foundation

## 01 Java Basics
- JVM Execution Pipeline
  - javac → .class bytecode
  - ClassLoader → Verification → Execution Engine
  - Interpreter → JIT Compiler (Hot Methods)
- Variables & Data Types
  - Primitives (Stack): byte, short, int, long, float, double, char, boolean
  - References (Heap): String, Arrays, Objects
  - Autoboxing / Unboxing Traps
- Operators
  - Arithmetic (integer division truncation)
  - Relational & Logical (short-circuit)
  - Ternary (`condition ? a : b`)
  - `==` (identity) vs `.equals()` (value)
- Control Flow
  - if/else, switch expressions (Java 14+)
  - for, for-each, while, do-while
  - Block scoping `{}` vs Python indentation

## 02 OOP Fundamentals
- Classes & Objects
  - Blueprint → Instance (Heap allocation)
  - Object Header (Mark Word + Klass Pointer)
- Constructors
  - Default, Parameterized, `this()` chaining
  - Python `__init__` comparison
- Encapsulation
  - private fields + getters/setters
  - Python `_convention` vs Java enforcement
- Inheritance
  - `extends` (single only), `super()`
  - Method overriding, `@Override`
- Polymorphism
  - Compile-time (overloading) vs Runtime (overriding)
  - Virtual method dispatch
- Abstraction
  - Abstract classes (shared state) vs Interfaces (contracts)
- Access Modifiers
  - public → protected → default → private
- Static Members
  - Class-level state, static blocks
  - Python `@classmethod` comparison

## 03 Advanced OOP
- Inner Classes
  - Static nested, Member inner, Anonymous, Local
  - Memory leak trap (Outer.this reference)
- Enums
  - Full class with methods and fields
  - State machines (OrderStatus transitions)
- Generics
  - `<T>`, bounded wildcards `<? extends Number>`
  - Type erasure at runtime
- Annotations
  - `@interface`, meta-annotations, retention
  - Preview of Spring annotations
- Wrapper Classes
  - Autoboxing traps, Integer cache (-128 to 127)
- Object Class
  - equals()/hashCode()/toString() contract

## 04 Strings & Arrays
- String Immutability
  - String Pool (intern())
  - `==` vs `.equals()` trap
  - Concatenation performance pitfall
- StringBuilder vs StringBuffer
  - Mutable, thread-safe vs non-thread-safe
  - Python `"".join()` comparison
- Arrays
  - Fixed-size, typed, 1D/2D/jagged
  - Arrays utility class

## 05 Exception Handling
- Exception Hierarchy
  - Throwable → Error / Exception
  - Checked (compile-time) vs Unchecked (runtime)
- Custom Exceptions
  - Domain-specific exceptions
  - When checked vs unchecked
- try-with-resources
  - AutoCloseable = Python `with` statement
  - Close order guarantees
- throws vs throw
  - Declaration vs instantiation

## 06 Collections
- Collection Hierarchy
  - Collection (List, Set, Queue) vs Map
- List
  - ArrayList (Python list) vs LinkedList
  - Big-O: get O(1) vs O(n)
- Set
  - HashSet, LinkedHashSet, TreeSet
  - Uniqueness via hashCode()/equals()
- Map
  - HashMap (Python dict), TreeMap, LinkedHashMap
  - Bucket + linked list → red-black tree (Java 8+)
- Queue & Deque
  - PriorityQueue, ArrayDeque
- Comparable vs Comparator
  - Natural order vs custom sort
  - `Comparator.comparing()` = Python `key=lambda`

## 07 Functional Java
- Functional Interfaces
  - Predicate, Function, Consumer, Supplier
  - `@FunctionalInterface`
- Lambda Expressions
  - `(params) -> expression` syntax
  - Closures: effectively final rule
- Method References
  - 4 types: Static, Instance, Arbitrary, Constructor
- Stream API
  - filter → map → reduce → collect
  - Lazy evaluation, terminal vs intermediate
  - Python generators comparison
- Optional
  - Null-safe pipelines
  - orElse, orElseGet, orElseThrow
- Parallel Streams
  - ForkJoinPool, when to use, thread safety traps

## 08 Multithreading
- Thread Lifecycle
  - NEW → RUNNABLE → BLOCKED → WAITING → TERMINATED
- Runnable vs Callable
  - void vs return value (Future)
- Race Conditions
  - synchronized, volatile
  - Python GIL comparison
- ExecutorService
  - Thread pools, never `new Thread()` in production
  - submit() → Future.get()
- Concurrent Collections
  - ConcurrentHashMap, CopyOnWriteArrayList
  - Lock striping vs full synchronization
