# OOP Fundamentals — Concept Mindmap

## `00-java-foundation/02-oop-fundamentals`

### `explanation/`
- **[`01-class-and-object.md`](explanation/01-class-and-object.md)** & **[`ClassAndObjectDemo.java`](explanation/ClassAndObjectDemo.java)**
  - Class vs Object
    - Blueprint vs Instance
    - Heap Memory Allocation
    - Reference Variables
- **[`02-constructors.md`](explanation/02-constructors.md)**
  - Object Initialization
    - Default Constructor
    - Parameterized Constructor
    - Constructor Overloading
    - `this()` chaining
- **[`03-encapsulation.md`](explanation/03-encapsulation.md)**
  - Data Hiding
    - `private` fields
    - `public` getters and setters
    - Protecting internal integrity
- **[`04-inheritance.md`](explanation/04-inheritance.md)** & **[`InheritanceDemo.java`](explanation/InheritanceDemo.java)**
  - The "Is-A" Relationship
    - `extends` keyword
    - Single Inheritance restriction
    - `super` keyword reference
- **[`05-polymorphism.md`](explanation/05-polymorphism.md)** & **[`PolymorphismDemo.java`](explanation/PolymorphismDemo.java)**
  - Many Forms
    - Compile-Time (Method Overloading)
    - Run-Time (Method Overriding & Dynamic Dispatch)
    - Upcasting and Downcasting
- **[`06-abstraction.md`](explanation/06-abstraction.md)** & **[`AbstractDemo.java`](explanation/AbstractDemo.java)**
  - Hiding Complexity
    - `abstract` classes and methods
    - Partial Implementation (Identity + State)
- **[`InterfaceDemo.java`](explanation/InterfaceDemo.java)**
  - Interfaces
    - Total Abstraction (Can-Do Contract)
    - Multiple Capability Implementation
    - Default methods
- **[`07-access-modifiers.md`](explanation/07-access-modifiers.md)**
  - Visibility Grid
    - `private` (same class)
    - `(default)` (same package)
    - `protected` (package + child classes)
    - `public` (anywhere)
- **[`08-static-members.md`](explanation/08-static-members.md)**
  - Class-Level State
    - Stored in Metaspace
    - Static Variables (Shared state)
    - Static Methods (Utility behavior)
    - Static Initialization Blocks

### `exercises/`
- **[`README.md`](exercises/README.md)** 
  - Execution Instructions
- **[`Ex01_BankAccount.java`](exercises/Ex01_BankAccount.java)**
  - Encapsulation Practice
  - Boundary validation on deposit/withdraw
- **[`Ex02_ShapeHierarchy.java`](exercises/Ex02_ShapeHierarchy.java)**
  - Inheritance & Abstraction Practice
  - Array tracking and Runtime Polymorphism
- **[`Ex03_PaymentInterface.java`](exercises/Ex03_PaymentInterface.java)**
  - Interface Practice
  - Cross-domain Polymorphism

### `MINDMAP.md`
- Visual Markdown Tree (Current File)
- VS Code Markmap extension compatible

### Interview QA Focus
- Method Overloading vs Overriding
- `abstract` class vs `interface`
- `protected` vs `(default)` scope differences
- `static` vs Instance state
