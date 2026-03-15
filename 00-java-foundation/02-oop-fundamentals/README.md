# 02 - OOP Fundamentals

Welcome to the foundational core of Java: Object-Oriented Programming (OOP). 

If you are coming from Python, you are used to a very loose definition of OOP. In Python, you can dynamically attach methods and properties to objects at runtime, access control (`_password`) is just a gentleman's agreement, and you can inherit from as many classes as you want.

**Java rejects all of this.**

In Java, OOP is about **Strict Contracts**. 
- A class is an immutable blueprint. 
- Access modifiers (`private`, `public`, `protected`) are violently enforced by the compiler.
- You inherit from exactly one parent.
- If you say an object "Can-Do" something (Interface), you must prove it at compile time.

This strictness is why Java scales better than almost any language on earth for massive enterprise teams. If you build a class correctly, it is impossible for a junior developer on another team to break its internal state.

## Learning Path

We recommend looking at the `MINDMAP.md` file first to visualize how the four pillars connect. Then, dive into the explanation files in order:

### Core Concepts
1. [`01-class-and-object.md`](explanation/01-class-and-object.md) - The Blueprint vs Instance
2. [`ClassAndObjectDemo.java`](explanation/ClassAndObjectDemo.java)
3. [`02-constructors.md`](explanation/02-constructors.md) - Object Initialization

### The Four Pillars
4. [`03-encapsulation.md`](explanation/03-encapsulation.md) - Data Hiding (Pillar 1)
5. [`04-inheritance.md`](explanation/04-inheritance.md) - The "Is-A" Relationship (Pillar 2)
6. [`InheritanceDemo.java`](explanation/InheritanceDemo.java)
7. [`05-polymorphism.md`](explanation/05-polymorphism.md) - Many Forms (Pillar 3)
8. [`PolymorphismDemo.java`](explanation/PolymorphismDemo.java)
9. [`06-abstraction.md`](explanation/06-abstraction.md) - The "Can-Do" Contract (Pillar 4)
10. [`AbstractDemo.java`](explanation/AbstractDemo.java)

### Advanced Class Architecture
11. [`InterfaceDemo.java`](explanation/InterfaceDemo.java) - Multiple Capabilities
12. [`07-access-modifiers.md`](explanation/07-access-modifiers.md) - The Visibility Grid
13. [`08-static-members.md`](explanation/08-static-members.md) - Class vs Instance Level State

## Practice

Once you've read through the explanations and ran the demos, head over to the `exercises/` folder and test your retention!

- [`Ex01_BankAccount.java`](exercises/Ex01_BankAccount.java)
- [`Ex02_ShapeHierarchy.java`](exercises/Ex02_ShapeHierarchy.java)
- [`Ex03_PaymentInterface.java`](exercises/Ex03_PaymentInterface.java)
