# OOP Fundamentals - Exercises

This directory contains practical exercises designed to test your understanding of Java's core Object-Oriented Programming concepts.

## How to use these exercises

Each java file contains an incomplete class structure with a `main` method that tests the implementation. Your goal is to fill in the missing code so that the file compiles successfully and the `main` method executes without throwing errors.

1.  Open the exercise file.
2.  Read the `TODO` comments.
3.  Write the required Java code using the exact access modifiers, methods, and class structures requested.
4.  Run the file:
    ```bash
    javac Ex01_BankAccount.java
    java Ex01_BankAccount
    ```

## Exercises Index

1.  **`Ex01_BankAccount.java`:** Focuses on **Encapsulation**. You will create a tightly secured BankAccount class using `private` fields and strict `public` getters/setters that validate bounds.
2.  **`Ex02_ShapeHierarchy.java`:** Focuses on **Inheritance and Abstraction**. You will build an abstract base class with concrete child implementations (`Circle`, `Rectangle`, `Triangle`) and demonstrate runtime polymorphism.
3.  **`Ex03_PaymentInterface.java`:** Focuses on **Interfaces and Polymorphism**. You will build a capability contract (`Payable`) and implement it across unrelated domains (Credit Card, Net Banking, UPI).
