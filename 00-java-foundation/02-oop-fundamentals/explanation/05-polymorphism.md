# 05 - Polymorphism

> **Python Bridge:** Python is dynamically typed ("duck typing"), so polymorphism happens automatically—if an object has a `.quack()` method, it works, regardless of the class. Java is Statically Typed. For `object.quack()` to compile in Java, the variable holding the object *must* be declared as a type that promises `.quack()` exists (usually a parent class or Interface).

Polymorphism means "many forms." It allows a single action to behave differently based on the exact object performing the action. It comes in two flavors: **Compile-Time** and **Runtime**.

## 1. Compile-Time Polymorphism (Method Overloading)

This happens within a single class when multiple methods have the **exact same name**, but a **different number or type of parameters**. The Java compiler figures out which one to call *before* the code even runs.

```java
class Calculator {
    // Overload 1
    int add(int a, int b) { return a + b; }
    
    // Overload 2
    double add(double a, double b) { return a + b; }
    
    // Overload 3
    int add(int a, int b, int c) { return a + b + c; }
}
```

> Python doesn't support method overloading natively; the second `add(a, b)` definition would simply overwrite the first one. Python relies on default arguments instead (`def add(a, b, c=0)`).

## 2. Runtime Polymorphism (Method Overriding)

This is the holy grail of OOP. It relies on *Upcasting* — storing a Child object inside a Parent reference variable.

```java
// Upcasting: Parent reference = Child Object
Animal myPet = new Dog(); 
myPet.speak(); // Will print "Bark!", NOT "Animal sound"
```

Even though the compiler only knows `myPet` is an `Animal`, when the JVM runs the code, it looks at the *actual object in heap memory* (`Dog`) and dynamically dispatches the `Dog`'s version of the `speak()` method.

### Dynamic Dispatch

```mermaid
sequenceDiagram
    participant Main
    participant Variable (Type: Animal)
    participant Heap (Object: Dog)

    Main->>Variable: myPet = new Dog();
    Main->>Variable: myPet.speak();
    Note over Variable: Compiler confirms 'Animal' has speak()
    Variable->>Heap: JVM follows pointer to memory
    Note over Heap: JVM finds 'Dog' object
    Heap-->>Main: Executes Dog's overridden speak() -> "Bark!"
```

---

## Interview Questions

### Conceptual
**Q: What is the difference between Overloading and Overriding?**
A: Overloading occurs in the same class (same name, different parameters) and is resolved at compile time. Overriding occurs between parent/child classes (exact same signature) and is dispatched at runtime based on the actual object in memory.

**Q: Does Java support method overriding for static methods?**
A: **No.** Static methods belong to the Class, not the Object. If a child class has a static method with the exact same name as the parent, it's called *Method Hiding*, not overriding. It resolves strictly based on the reference variable type at compile time.

### Scenario / Debug
**Q: `List<String> list = new ArrayList<>();` Why declare the variable as `List` instead of `ArrayList`?**
A: This is Runtime Polymorphism in action! By pointing an Interface reference (`List`) to a concrete implementation (`ArrayList`), you are programming to an interface, not an implementation. Later, you can swap it to `new LinkedList<>()` without changing any downstream code that relies on the `list` variable.

### Quick Fire
- **What is duck typing?** A Python concept where type is determined by an object's behavior (methods) rather than explicit inheritance. Java requires explicit interfaces/inheritance.
- **Can you overload a method by changing only the return type?** No. The parameter list must be different. Changing only the return type causes a compilation error.
