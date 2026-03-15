package com.learning.oop;

/**
 * Demonstrates Interfaces and Multiple "Can-Do" Inheritance.
 * Shows how classes can implement multiple interfaces, separating capabilities.
 */

// Capability 1: Can process payments
interface Payable {
    // Implicitly public static final (Constants)
    double TRANSACTION_FEE = 0.50;

    // Implicitly public abstract (Contracts)
    boolean processPayment(double amount);
}

// Capability 2: Can be audited for accounting
interface Auditable {
    void printAuditLog();
}

// Concrete Class implementing MULTIPLE capabilities
class CreditCardProcessor implements Payable, Auditable {
    private String vendor;

    public CreditCardProcessor(String vendor) {
        this.vendor = vendor;
    }

    // Implementing Payable Contract
    @Override
    public boolean processPayment(double amount) {
        double total = amount + TRANSACTION_FEE;
        System.out.println("Processing " + vendor + " payment for $" + total);
        return true; 
    }

    // Implementing Auditable Contract
    @Override
    public void printAuditLog() {
        System.out.println("Audit Entry: Visa Transaction processed successfully.");
    }
}

public class InterfaceDemo {
    public static void main(String[] args) {
        
        System.out.println("--- Multiple Interfaces ---");
        CreditCardProcessor visa = new CreditCardProcessor("Visa");

        // Treating the object as a pure Payment processor (hides Audit capabilities)
        Payable basicPayment = visa;
        basicPayment.processPayment(100.00);

        // Treating the object as a pure Audit target (hides Payment capabilities)
        Auditable auditTarget = visa;
        auditTarget.printAuditLog();

        // Print Constant from Interface statically
        System.out.println("Global Network Fee: $" + Payable.TRANSACTION_FEE);
    }
}
