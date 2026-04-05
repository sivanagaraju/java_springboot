# Progressive Quiz Drill — OOP Fundamentals

---

## Round 1: Core Recall (2 minutes per question)

**Q1.** What is the difference between method overriding and method overloading? Give one example of each using a `Payment` domain.

**Q2.** A constructor in class `Child` calls `super(args)`. When must this call appear? What happens if it's omitted?

**Q3.** An interface `Taxable` and a `Product` abstract class both declare `double calculateTax()`. A `PhysicalProduct` needs both. What is the compile error and how do you resolve it?

---

## Round 2: Apply & Compare (5 minutes per question)

**Q4.** Design a `Shape` hierarchy for a Spring REST API that returns area calculations. Use abstract class where needed. Show the class structure and explain why `area()` is abstract.

**Q5.** A Spring `@Service` has a method that calls `this.validateAndSave()` and `validateAndSave()` is `@Transactional`. Will the transaction work? Why or why not?

**Q6.** Explain the difference between `final`, `finally`, and `finalize()`. Which one is relevant in modern Spring development and which should never be used?

---

## Round 3: Debug the Bug (3 minutes per question)

**Q7.** Code:
```java
class Counter {
    static int count = 0;
    Counter() { count++; }
}
Counter a = new Counter();
Counter b = new Counter();
System.out.println(a.count);  // prints 2
```
Is this a bug? What's the design problem, and how should `count` be accessed?

**Q8.** Class `Dog extends Animal`. Both have a `name` field (no getter/setter). `Animal a = new Dog(); System.out.println(a.name);` prints Animal's name, not Dog's. Why? How do you make it print Dog's name?

**Q9.** A `toString()` override throws `StackOverflowError`. What's the likely cause?

---

## Round 4: Staff-Level Scenario (10 minutes)

**Q10.** You're building a payment processing system. `CreditCard`, `UPI`, and `Crypto` must all be usable as payment methods. `CreditCard` and `UPI` share a `validateCvv()` method and a `transactionLimit` field. `Crypto` has completely different validation. Design the full class hierarchy explaining every choice between abstract class, interface, and concrete class.

---

## Answer Key

**A1.** Overriding: same signature in subclass — runtime polymorphism. `CreditCard extends Payment { @Override boolean authorize() { ... } }`. Overloading: same name, different parameters — compile-time resolution. `authorize(double amount)` and `authorize(double amount, String currency)` in the same class.

**A2.** `super()` must be the FIRST statement. If omitted, Java implicitly calls `super()` (no-arg). If parent has no no-arg constructor, it's a compile error.

**A3.** Both `Taxable` (interface, `default`) and `Product` (abstract) define `calculateTax()`. The compiler sees an ambiguity (diamond problem). Resolution: `PhysicalProduct` must explicitly `@Override calculateTax()` and call whichever parent it wants: `super.calculateTax()` (for abstract class) or `Taxable.super.calculateTax()` (for interface default).

**A4.** Abstract class `Shape` with `protected double[] dimensions` field and `abstract double area()`. Concrete: `Circle(radius)`, `Rectangle(w, h)`. Abstract because `area()` has no meaningful default — each shape's formula is different. No interface needed since all shapes share state (dimensions).

**A5.** No — the transaction will NOT work. `this.validateAndSave()` calls the real method on `this`, bypassing Spring's AOP proxy. The `@Transactional` advice is applied to the proxy, not to `this`. Fix: inject a reference to the proxy (`ApplicationContext.getBean()`), or extract `validateAndSave()` into a separate `@Service` class.

**A6.** `final` = keyword (prevents extension/override/reassignment). `finally` = try-finally block (always executes). `finalize()` = deprecated Object method called before GC — unreliable, should NEVER be used. In Spring: `final` on methods prevents AOP proxy overriding (important to know!). `finally` is still used in legacy code but replaced by try-with-resources. `finalize()` removed from practice.

**A7.** Design problem: `count` is `static` but accessed via instance `a.count`. This is misleading — it makes `count` look like it belongs to `a`. The compiler accesses `Counter.count` regardless of which reference you use. Always access static members via class: `Counter.count`. This is also detected by IDEs with a warning.

**A8.** Field access is NOT polymorphic in Java — it resolves by declared reference type at compile time. `a.name` uses `Animal.name` because `a` is declared as `Animal`. Methods ARE polymorphic (virtual dispatch). Solution: add a `getName()` method to `Animal`, override it in `Dog`. Accessing `a.getName()` will dispatch to `Dog.getName()`.

**A9.** `toString()` calls itself — infinite recursion. Common cause: `return "Dog{" + this + "}";` where `this + ""` implicitly calls `toString()`. Fix: `return "Dog{name=" + name + "}"` — reference fields directly, not `this`.

**A10.** Interface `Payable` with `boolean authorize(double amount); boolean capture();`. Abstract class `CardBasedPayment implements Payable` with `protected double transactionLimit` and `protected boolean validateCvv(String cvv)`. `CreditCard extends CardBasedPayment`, `UPI extends CardBasedPayment`. `Crypto implements Payable` directly — completely different behavior. Result: `CreditCard` and `UPI` share state and validation via abstract class; all three are usable as `Payable` via interface. Spring service accepts `List<Payable>` and iterates to find the right one (Strategy pattern).
