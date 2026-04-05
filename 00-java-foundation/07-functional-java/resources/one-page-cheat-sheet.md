# Functional Java — One-Page Cheat Sheet

## Core Functional Interfaces

```java
Function<T, R>       R apply(T t)                 // transform
BiFunction<T, U, R>  R apply(T t, U u)            // transform with 2 inputs
Predicate<T>         boolean test(T t)             // filter/condition
Consumer<T>          void accept(T t)              // side-effect, no return
Supplier<T>          T get()                       // produce, no input
UnaryOperator<T>     T apply(T t)                 // Function where T=R
BinaryOperator<T>    T apply(T t1, T t2)          // BiFunction where T=U=R

// Composition
Function<A,C> f = first.andThen(second);    // first(a) then second(result)
Function<A,C> f = second.compose(first);    // same as andThen but reversed
Predicate<T>  p = p1.and(p2).or(p3).negate();
```

## Lambda Syntax

```java
// Zero params
Supplier<String> s = () -> "hello";

// One param (parens optional)
Predicate<String> p = s -> s.isEmpty();
Predicate<String> p = (String s) -> s.isEmpty();  // explicit type

// Multi-line body
Function<Integer, Integer> f = n -> {
    int doubled = n * 2;
    return doubled + 1;
};
```

## Method References (4 Types)

```java
// 1. Static method
Function<String, Integer>  f = Integer::parseInt;
// equivalent: s -> Integer.parseInt(s)

// 2. Instance method on a parameter
Function<String, String>   f = String::toUpperCase;
// equivalent: s -> s.toUpperCase()

// 3. Instance method on a specific object
Consumer<String>           c = System.out::println;
// equivalent: s -> System.out.println(s)

// 4. Constructor reference
Supplier<ArrayList<String>> s = ArrayList::new;
// equivalent: () -> new ArrayList<>()
```

## Stream Pipeline

```java
list.stream()                              // source
    .filter(e -> e.isActive())             // intermediate (lazy)
    .map(Employee::getSalary)              // intermediate (lazy)
    .mapToDouble(Double::doubleValue)      // primitive stream (no boxing)
    .average()                             // terminal (triggers execution)
    .orElse(0.0);

// FlatMap — flatten nested collections
orders.stream()
    .flatMap(o -> o.getItems().stream())   // Stream<List<Item>> → Stream<Item>
    .collect(Collectors.toList());

// Key collectors
Collectors.toList()
Collectors.toUnmodifiableList()            // Java 10+
Collectors.toSet()
Collectors.joining(", ", "[", "]")         // String concatenation
Collectors.groupingBy(Employee::getDept)   // Map<String, List<Employee>>
Collectors.groupingBy(Employee::getDept, Collectors.counting())
Collectors.toMap(Employee::getId, e -> e)
Collectors.partitioningBy(e -> e.getSalary() > 50_000)  // Map<Boolean, List>
```

## Optional

```java
Optional<User> opt = repo.findById(id);

// Safe extraction
String name = opt.map(User::getName).orElse("Unknown");
String name = opt.map(User::getName).orElseGet(() -> defaultName());
User   user = opt.orElseThrow(() -> new NotFoundException(id));

// Chaining
opt.filter(u -> u.isActive())
   .map(User::getEmail)
   .ifPresent(email -> sendWelcome(email));

// Creating
Optional.of(value)         // NullPointerException if null
Optional.ofNullable(value) // empty if null — use at null-unsafe boundaries
Optional.empty()
```

## Python Bridge

| Java | Python |
|------|--------|
| `list.stream().filter(p)` | `filter(p, lst)` or `[x for x in lst if p(x)]` |
| `stream.map(f)` | `map(f, lst)` or `[f(x) for x in lst]` |
| `stream.flatMap(f)` | `[item for sub in lst for item in f(sub)]` |
| `Collectors.toList()` | `list(...)` |
| `Collectors.groupingBy(f)` | `itertools.groupby` or `defaultdict` |
| `stream.reduce(0, Integer::sum)` | `functools.reduce(operator.add, lst, 0)` |
| `Optional<T>` | `T \| None` or `typing.Optional[T]` |
| `Optional.orElse(d)` | `value or default` |
| `Predicate<T>` | `Callable[[T], bool]` |
| `Function<T,R>` | `Callable[[T], R]` |
| `Consumer<T>` | `Callable[[T], None]` |

## Common Traps

```
TRAP 1: Stream consumed twice
  stream.filter(...); stream.map(...);  // IllegalStateException!
  Fix: chain all operations in one pipeline

TRAP 2: orElse evaluates eagerly
  opt.orElse(db.expensiveQuery())  // query ALWAYS runs
  Fix: opt.orElseGet(() -> db.expensiveQuery())

TRAP 3: parallel stream with non-thread-safe collection
  .parallel().forEach(list::add)  // race condition!
  Fix: use collect(Collectors.toList()) instead

TRAP 4: Optional.get() without isPresent()
  opt.get()  // NoSuchElementException if empty
  Fix: opt.orElseThrow() or opt.orElse(default)

TRAP 5: Stateful lambda in parallel stream
  int[] count = {0}; stream.parallel().forEach(x -> count[0]++);
  Fix: use stream.count() or Collectors

TRAP 6: Returning Optional from getters
  private Optional<String> name;  // Bloch says NO
  Optional should only be return types of methods — not fields, not params
```
