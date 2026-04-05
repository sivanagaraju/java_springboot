# Top Resource Guide — OOP Fundamentals

## Official Documentation

- **[Java Tutorials: Object-Oriented Programming Concepts](https://docs.oracle.com/javase/tutorial/java/concepts/)** — Oracle's official OOP tutorial with clear examples
- **[Java Language Specification: Classes](https://docs.oracle.com/javase/specs/jls/se21/html/jls-8.html)** — Authoritative spec for class mechanics, constructors, inheritance

## Books (Ordered by Priority)

1. **Effective Java, 3rd Edition** — Joshua Bloch
   - Item 17: Minimize mutability (design for encapsulation)
   - Item 18: Favor composition over inheritance
   - Item 20: Prefer interfaces to abstract classes
   - Item 41: Use marker interfaces to define types

2. **Head First Object-Oriented Analysis & Design** — McLaughlin, Pollice, West
   - Best conceptual explanation of SOLID principles and OOP design decisions

3. **Clean Code** — Robert C. Martin
   - Chapters on classes, SRP, and cohesion — directly applicable to Spring service design

## Blog Posts

- **[Baeldung: Java Inheritance](https://www.baeldung.com/java-inheritance)** — Complete with overriding, super, final
- **[Baeldung: Abstract Class vs Interface](https://www.baeldung.com/java-abstract-class)** — When to use each with modern Java 8+ context
- **[Reflectoring.io: SOLID Principles in Spring](https://reflectoring.io/solid-principles-in-java/)** — Maps OOP principles directly to Spring Boot patterns

## Key Concepts to Verify You Know

- [ ] Why non-final method calls in constructors are dangerous (subclass override + uninitialized state)
- [ ] How `super()` constructor chaining works — always first statement, compiler inserts it implicitly
- [ ] Why fields are NOT polymorphic but methods ARE
- [ ] The Diamond Problem with default interface methods and how to resolve it
- [ ] Why Spring's AOP breaks with `this.method()` — self-invocation bypasses the proxy
- [ ] `final` on methods prevents Spring AOP from proxying them — never make `@Transactional` methods `final`
