# Polymorphism: Bytecode Dispatch Mechanics

Polymorphism allows objects to be treated as instances of their parent class (or implemented interface) while ensuring their overridden behavior executes dynamically.

To truly understand Java Polymorphism, a Java Architect must understand the Bytecode instructions that handle method invocation:

## 1. `invokevirtual` (The Workhorse)
Whenever you call a standard instance method on a typical class, the compiler generates the `invokevirtual` instruction. 
This tells the JVM: *"Look at the actual object residing in Heap memory, traverse to its specific V-Table (Virtual Method Table) in the Metaspace, and resolve the method dynamically."*

```java
Parent obj = new Child();
obj.display(); // Compiles to `invokevirtual`
```
Because `obj` actually points to a `Child` on the Heap, the JVM traverses the `Child` V-Table and executes the overridden result. This is true **Dynamic Dispatch**.

## 2. `invokeinterface` (The Complex Router)
When you call a method utilizing an `Interface` reference, the JVM cannot utilize a mathematically predictable V-Table structure. 
```java
Runnable r = new MyTask();
r.run(); // Compiles to `invokeinterface`
```
Because classes can implement hundreds of random interfaces, an interface method like `.run()` does not live at a perfectly identical memory index across entirely unrelated classes.
The JVM uses special Interface Method Tables (I-Tables). `invokeinterface` is drastically slower natively than `invokevirtual` because the JVM must execute an internal caching search check to locate the function pointer inside a heavily fragmented architecture tree. 

*(Pro Tip: Over thousands of iterations, the JVM JIT compiler performs "Monomorphic Call Site" optimizations, stripping out the Interface search and perfectly predicting the method location if the execution loop constantly uses the exact same class object).*

## 3. `invokespecial` (The Fixed Router)
Sometimes, the JVM explicitly needs to bypass polymorphism completely. 
When you execute `super.display()` or initialize a `private` method, or invoke an `<init>` constructor, the JIT does not want to check V-Tables. It needs to securely execute **one exact specific method memory address**.
```java
super.print(); // Compiles to `invokespecial`
```
This forces the execution purely statically. It bypasses virtualization, achieving extreme speed and rigid absolute architectural safety.

## 4. `invokestatic` (The Global Router)
Invokes a standard `static` class method. Since there is no actual "object" instance, there is zero polymorphism and zero V-Table access. Execution directly jumps to a stationary point in Metaspace memory.

## Python Comparison: Duck Typing vs. Nominal Subtyping

Python utilizes **Duck Typing**: *"If it structurally implements a `quack()` function dynamically, it mathematically must be a Duck."*
In Python, objects don't share identical rigid class trees; if you pass any variable containing `.run()` into a function, it dynamically dynamically searches its internal `__dict__` hash and blindly executes.

Java purely uses **Nominal Subtyping**. The Java generic method execution rigorously checks interface names at strict compile-time signatures. You cannot pass random unrelated objects containing a `.run()` method payload into a `Runnable`-expecting parameter pipeline. Java structurally demands a verifiable ancestral compilation `implements Runnable` signature to securely build the `invokeinterface` bytecode tables.

---

## Interview Questions - Architect Level

**Q1: What prevents an application calling an Interface from executing at the same identical speed as a Class execution flow?**
> The `invokevirtual` instruction is structurally simple because it utilizes a guaranteed single-inheritance Class V-Table. The JVM looks up the exact class Klass pointer and extracts the integer indexed array function instantly. An `invokeinterface` requires complex mapping; since an object can arbitrarily inherit multiple distinct interfaces sporadically, the execution environment cannot rely on a fixed linear index. The JVM heavily caches I-Table lookups locally, but cold operations natively invoke minor overhead lookups processing fragmented multi-path trees.

**Q2: What is "Monomorphic Call Site Profiling" and how does the JIT weaponize it?**
> A monomorphic call site refers to an execution line evaluating polymorphism (like `animal.speak()`) where 100% of the actual objects traversing that line in runtime production happen to universally evaluate identically (e.g. they are all uniformly `Dog` objects for hours on end). The JVM aggressively weaponizes this telemetry; instead of wasting cycles querying the V-Table repeatedly, it totally disables the `invokevirtual` command dynamically and injects a single absolute machine code pointer directly into the `Dog.speak()` execution memory. This massively boosts Java hardware velocity over static interpretations.

**Q3: Is it possible to functionally override a `static` method if I execute a Child class matching it exactly?**
> No, this is structurally impossible in Java (it is called Method Hiding, not overriding). Static methods generate `invokestatic` bytecode instructions. These explicitly bypass the entire virtual dispatch pipeline architecture entirely. They rigidly bind identically to the specific explicit class signature declared at compilation time. Because the object Header's exact Klass pointer is absolutely irrelevant and completely ignored during execution, true polymorphism functionally ceases to exist.
