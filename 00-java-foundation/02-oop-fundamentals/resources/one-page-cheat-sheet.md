# OOP Fundamentals — One-Page Cheat Sheet

## The 4 Pillars Quick Reference

```
Encapsulation   → private fields + public methods
                  Controls access, hides implementation
                  Spring: @Service beans hide their implementation from controllers

Inheritance     → extends (class), implements (interface)
                  Child inherits parent's non-private members
                  Single inheritance only (but multiple interfaces allowed!)

Polymorphism    → same method name, different behavior
                  Runtime (overriding): which class's method runs decided at runtime
                  Compile-time (overloading): method selected by parameter types

Abstraction     → abstract class / interface
                  Define what, not how
                  Spring: @Service behind @Repository interface
```

## Inheritance Keywords

```java
class Animal { void speak() { System.out.println("..."); } }

class Dog extends Animal {
    @Override
    void speak() {
        super.speak();          // call parent implementation
        System.out.println("Woof!");
    }
}

// Preventing extension:
final class ImmutablePoint { }  // cannot be extended
```

## Abstract Class vs Interface

```
Abstract Class                    Interface
──────────────────────────────    ──────────────────────────────
extends only ONE                  implements MANY
can have fields                   only public static final fields
can have constructors             no constructors
can have concrete methods         default/static methods (Java 8+)

USE ABSTRACT when:                USE INTERFACE when:
  Shared state (instance fields)    Pure behavior contract
  IS-A relationship                 CAN-DO capability
  Template Method pattern           Multiple inheritance needed
```

## Key OOP Rules

```
super() must be FIRST statement in constructor
@Override annotation: protects from typos, self-documenting
final: class (no extend), method (no override), field (no reassign)
static: belongs to class, not instance (no this, no @Override)

Constructor chaining:
  this(args)  → calls another constructor in SAME class
  super(args) → calls constructor in PARENT class
```

## Python Bridge

| Java | Python |
|------|--------|
| `class Dog extends Animal` | `class Dog(Animal):` |
| `super.method()` | `super().method()` |
| `abstract class` | `ABC` + `@abstractmethod` |
| `interface Runnable` | Protocol or ABC |
| `@Override` | No annotation — duck typing |
| `private` field | `_field` (convention, not enforced) |
| `instanceof` | `isinstance(obj, Type)` |
| `final class` | No equivalent — use `__init_subclass__` |
| Method overloading | No overloading — use default args |

## Common Traps

```
TRAP 1: Calling overridden method from constructor
  class Parent {
      Parent() { printInfo(); }         // calls CHILD's override!
      void printInfo() { ... }
  }
  class Child extends Parent {
      String name = "Child";
      @Override void printInfo() { print(name); }  // name is NULL here!
  }
  Fix: avoid non-final method calls in constructors.

TRAP 2: Forgetting @Override
  class Dog extends Animal {
      void Speak() { ... }  // typo! Creates new method, doesn't override
  }
  @Override catches this at compile time.

TRAP 3: Covariant return types in overrides
  Parent: Animal getAnimal() { return new Animal(); }
  Child:  Dog    getAnimal() { return new Dog(); }   // valid! covariant return
  This compiles because Dog IS-A Animal.
```
