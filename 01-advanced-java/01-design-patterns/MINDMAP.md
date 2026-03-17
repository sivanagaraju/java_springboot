# Design Patterns — Mind Map

```markmap
# Design Patterns

## Creational
### Singleton
- ONE instance globally
- Eager vs Lazy init
- Thread-safe: enum
- Spring: @Scope("singleton")
- Anti-pattern: hidden dependencies
### Factory Method
- Creator returns Product
- Subclass decides which
- Spring: BeanFactory
- Open/Closed Principle
### Builder
- Step-by-step construction
- Fluent API chaining
- Immutable objects
- Lombok @Builder
- Spring: UriComponentsBuilder

## Behavioral
### Observer
- Subject notifies observers
- Decoupled event system
- Spring ApplicationEvent
- @EventListener
- Async: @Async + @EventListener
### Strategy
- Interchangeable algorithms
- Context + Strategy interface
- Spring: authentication providers
- Java: Comparator is a strategy!
### Template Method
- Base class defines skeleton
- Subclass fills in steps
- Hook methods
- Spring: JdbcTemplate
- Don't overuse inheritance

## Structural
### Decorator
- Wrap to add behavior
- Same interface
- Java I/O streams
- Filter chains
- Spring: HandlerInterceptor
### Adapter
- Convert interface A to B
- Wrap legacy code
- Spring: HandlerAdapter
- MessageConverter
```
