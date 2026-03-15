package com.learning.oop;

/**
 * Demonstrates Single Inheritance across multiple tiers.
 * Shows method overriding, the 'super' keyword, and polymorphic behavior limits.
 * 
 * ASCII Hierarchy:
 * 
 *        [java.lang.Object]  <-- The silent parent of all Java classes
 *                ^
 *                | (extends)
 *            [Animal]        <-- Base/Super class (Parent)
 *                ^
 *                | (extends)
 *              [Dog]         <-- Sub-class (Child)
 *                ^
 *                | (extends)
 *           [GuideDog]       <-- Sub-class (Grandchild)
 */

class Animal {
    protected String name; // protected means visible to children

    // 1. Parameterized Constructor
    public Animal(String name) {
        System.out.println("Building an Animal named: " + name);
        this.name = name;
    }

    public void eat() {
        System.out.println(name + " is consuming food.");
    }
}

class Dog extends Animal {
    
    // 2. Because Animal has NO default constructor, Dog MUST call super(name)
    //    If we delete this constructor, the Java compiler throws an error.
    public Dog(String name) {
        super(name); // MUST be the first line
        System.out.println("The animal has been clarified as a Dog.");
    }

    // 3. Method Overriding (Replacing the parent's behavior)
    //    @Override is an annotation. It tells the compiler to double-check that
    //    we didn't accidentally misspell the method name we intend to override.
    @Override
    public void eat() {
        // We can call the parent's version using 'super'
        super.eat(); 
        System.out.println(name + " is chewing dog food loudly.");
    }

    public void bark() {
        System.out.println(name + " says: WOOF!");
    }
}

// 4. Multi-level Inheritance is completely fine (it's multiple-inheritance that's banned)
class GuideDog extends Dog {
    
    public GuideDog(String name) {
        super(name);
        System.out.println("The dog has been designated as a Guide Dog.");
    }

    public void assist() {
        System.out.println(name + " is helping its owner navigate.");
    }
}

public class InheritanceDemo {
    public static void main(String[] args) {
        
        System.out.println("--- 1. Instantiating a regular Dog ---");
        // Watch the constructor chain! It goes Top-Down (Animal -> Dog)
        Dog myBuddy = new Dog("Buddy");
        
        System.out.println("\n--- 2. Invoking overridden behavior ---");
        myBuddy.eat();  // Calls Dog's overridden eat()
        myBuddy.bark(); // Calls Dog's specific bark()

        System.out.println("\n--- 3. Instantiating a GuideDog ---");
        // Constructor chain: Animal -> Dog -> GuideDog
        GuideDog max = new GuideDog("Max");
        max.eat();      // Inherited from Dog
        max.bark();     // Inherited from Dog
        max.assist();   // Specific to GuideDog
    }
}
