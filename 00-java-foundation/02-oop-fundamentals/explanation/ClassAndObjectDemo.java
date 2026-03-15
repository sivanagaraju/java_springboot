package com.learning.oop;

/**
 * Demonstrates the strict separation between Class (blueprint) and Object (instance),
 * and the memory allocation process via the 'new' keyword.
 * 
 * ASCII Memory Diagram:
 * 
 *  STACK MEMORY                  HEAP MEMORY
 *  +---------------+            +---------------------------+
 *  | sedanRef      |----------->| [Vehicle Object A]        |
 *  | (0x1A2B)      |            | make: "Honda", year: 2022 |
 *  +---------------+            +---------------------------+
 *  
 *  +---------------+            +---------------------------+
 *  | truckRef      |----------->| [Vehicle Object B]        |
 *  | (0x9F8C)      |            | make: "Ford", year: 2020  |
 *  +---------------+            +---------------------------+
 *  
 *  +---------------+
 *  | shallowCopy   |-----------> (Points to same Object A as sedanRef)
 *  | (0x1A2B)      |
 *  +---------------+
 */

// 1. The Blueprint (Class)
class Vehicle {
    // State (Fields / Instance Variables)
    // These MUST be declared here. You cannot add them dynamically later.
    String make;
    String model;
    int year;

    // Behavior (Methods)
    void startEngine() {
        System.out.println("The " + year + " " + make + " " + model + " engine is starting... Vroom!");
    }
    
    // In Java, there is no "self." passed to every method explicitly. 
    // "this.make" is implied when you type "make", though using "this.make" is good practice if parameters shadow it.
}

public class ClassAndObjectDemo {
    public static void main(String[] args) {
        
        System.out.println("--- 1. Instantiation ---");
        // Vehicle reference on stack = new Object allocated on heap
        Vehicle sedanRef = new Vehicle();
        
        // Setting state
        sedanRef.make = "Honda";
        sedanRef.model = "Civic";
        sedanRef.year = 2022;

        Vehicle truckRef = new Vehicle();
        truckRef.make = "Ford";
        truckRef.model = "F-150";
        truckRef.year = 2020;

        // Invoking behavior
        sedanRef.startEngine();
        truckRef.startEngine();

        System.out.println("\n--- 2. Reference Equality Trap ---");
        // shallowCopy does NOT create a new object. It just copies the memory address.
        Vehicle shallowCopy = sedanRef;
        
        System.out.println("Changing shallowCopy's year to 2099...");
        shallowCopy.year = 2099;
        
        // This will print 2099 because sedanRef and shallowCopy point to the SAME heap object.
        System.out.println("sedanRef year is now: " + sedanRef.year); 
        
        // Python Equivalent:
        // sedan = Vehicle()
        // shallow_copy = sedan
        // shallow_copy.year = 2099 (sedan is also modified!)
    }
}
