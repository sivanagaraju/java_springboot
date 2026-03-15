package com.learning.oop;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates Runtime Polymorphism (Dynamic Method Dispatch)
 * Shows how a single array of Parent references can execute entirely different
 * code based on the concrete Objects residing in Heap memory.
 */

class Payment {
    public void processPayment() {
        System.out.println("Processing a generic payment...");
    }
}

class CreditCardPayment extends Payment {
    @Override
    public void processPayment() {
        System.out.println("Processing CREDIT CARD: Contacting Visa/Mastercard network...");
    }
}

class CryptoPayment extends Payment {
    @Override
    public void processPayment() {
        System.out.println("Processing CRYPTO: Verifying blockchain confirmations...");
    }
}

class UPIPayment extends Payment {
    @Override
    public void processPayment() {
        System.out.println("Processing UPI: Verifying VPA and sending push notification...");
    }
}

public class PolymorphismDemo {
    public static void main(String[] args) {
        
        System.out.println("--- 1. Polymorphic Collection ---");
        // We create a List that only knows how to hold "Payment" objects.
        // But because of the "Is-A" relationship, it can hold ANY child of Payment.
        List<Payment> paymentQueue = new ArrayList<>();
        
        paymentQueue.add(new CreditCardPayment());
        paymentQueue.add(new CryptoPayment());
        paymentQueue.add(new UPIPayment());
        paymentQueue.add(new Payment());

        System.out.println("\n--- 2. Dynamic Dispatch in Action ---");
        // The beauty of polymorphism: The loop doesn't know, and doesn't CARE, 
        // what specific type of payment it is processing. It just issues the command.
        for (Payment p : paymentQueue) {
            
            // At Compile Time: The compiler just checks "Does Payment have a process() method?" Yes.
            // At Runtime: The JVM looks at the heap object and runs the specific overridden behavior.
            p.processPayment();
        }

        System.out.println("\n--- 3. Overloading (Compile-Time) ---");
        MathUtils math = new MathUtils();
        System.out.println("Integer Add: " + math.add(5, 10));
        System.out.println("Double Add:  " + math.add(5.5, 10.5));
    }
}

// Helper class to demonstrate Overloading
class MathUtils {
    public int add(int a, int b) {
        return a + b;
    }

    public double add(double a, double b) {
        return a + b;
    }
}
