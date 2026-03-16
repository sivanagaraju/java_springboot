# Module 4: Memory Management & Exceptions Exercises

This directory contains exercises designed to test your understanding of JVM memory architecture and robust exception handling.

## Exercise 1: Exception Handling (`Ex01_ExceptionHandling.java`)
**Goal:** Practice `try-catch-finally`, catching multiple exceptions, and observing the `finally` block execution.
**Tasks:**
1. Implement a method that parses an Integer from a String.
2. Catch `NumberFormatException` when the string is invalid.
3. Catch `NullPointerException` when the string is null.
4. Ensure a `finally` block logs exactly when the parsing attempt concludes.

## Exercise 2: Implementing Custom Exceptions (`Ex02_CustomException.java`)
**Goal:** Model a business failure precisely using custom runtime exceptions.
**Tasks:**
1. Create a custom exception called `InvalidTransactionException` extending `RuntimeException`.
2. Add fields to store a `transactionId` and an error `reason`.
3. Create a `PaymentProcessor` class with a `processPayment(String transactionId, double amount)` method.
4. Throw your custom exception if the amount is negative or zero.
5. In the main method, catch it and print the custom fields securely without printing a raw stack trace.
