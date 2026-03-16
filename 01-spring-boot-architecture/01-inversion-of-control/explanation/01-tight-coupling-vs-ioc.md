# Tight Coupling vs. Inversion of Control (IoC)

Before understanding Spring Boot, you must fundamentally understand the architectural problem it was created to solve: **Tight Coupling**.

## The Problem: Tight Coupling

In traditional Java applications, an object physically instantiates its own dependencies using the `new` keyword.

```java
public class PaymentService {
    // TIGHT COUPLING: PaymentService is permanently glued to StripeGateway.
    private StripeGateway gateway = new StripeGateway();

    public void charge(double amount) {
        gateway.process(amount);
    }
}
```

**Why is this disastrous for Enterprise systems?**
1. **Testing is impossible:** You cannot unit test `PaymentService` without physically hitting the real Stripe API. You cannot swap in a `MockGateway`.
2. **Replacement is painful:** If the company migrates from Stripe to PayPal, you have to physically open the `PaymentService` source code and rewrite it. This violates the Open-Closed Principle (SOLID).

## The Solution: Inversion of Control (IoC)

Inversion of Control states: **Objects should not create their dependencies. Dependencies should be handed to them from the outside.**

```java
public class PaymentService {
    // LOOSE COUPLING: We depend on an Abstraction (Interface), not a Concrete Class.
    private PaymentGateway gateway;

    // The dependency is injected from the outside!
    public PaymentService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public void charge(double amount) {
        gateway.process(amount);
    }
}
```

**The Architectural Shift:**
The control over dependency creation has been "inverted." The `PaymentService` no longer controls *how* or *when* the gateway is created. It just blindly accepts whatever gateway is passed into its constructor.

## Who creates the object then?

If the object doesn't create its dependencies, someone else must. 
In a small app, the `main()` method does it:

```java
public static void main(String[] args) {
    // Manual IoC Configuration
    PaymentGateway stripe = new StripeGateway(); 
    PaymentService service = new PaymentService(stripe);
    service.charge(100.0);
}
```

In an Enterprise Application with 10,000 objects, manually writing the `new` logic in the `main` method becomes a nightmare. 

This is exactly why the **Spring Application Context (The IoC Container)** exists. It acts as an invisible factory that instantiates all your objects, figures out who needs who, and automatically injects them into each other at startup.
