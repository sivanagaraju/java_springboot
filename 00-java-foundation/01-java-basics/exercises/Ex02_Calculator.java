/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex02_Calculator.java                                   ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="Ex02_Calcu..."
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Build a console calculator                     ║
 * ║  WHY IT EXISTS  : Practice looping, Scanner I/O, and switch.     ║
 * ║  PYTHON COMPARE : input() vs new Scanner(System.in)              ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises;

import java.util.Scanner;

public class Ex02_Calculator {
    public static void main(String[] args) {
        // Scanner is the Java equivalent to Python's input()
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Java Console Calculator ===");

        while (running) {
            System.out.print("\nEnter first number (or type 'quit' to exit): ");
            String input = scanner.next();
            
            if (input.equalsIgnoreCase("quit")) {
                running = false;
                continue;
            }

            // Converting String to double
            double num1;
            try {
                num1 = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
                continue;
            }

            System.out.print("Enter operator (+, -, *, /): ");
            String op = scanner.next();

            System.out.print("Enter second number: ");
            double num2 = scanner.nextDouble();

            // Java 14 Switch Expression for logic routing
            double result = switch (op) {
                case "+" -> num1 + num2;
                case "-" -> num1 - num2;
                case "*" -> num1 * num2;
                case "/" -> {
                    if (num2 == 0) {
                        System.out.println("Error: Division by zero");
                        yield Double.NaN;
                    }
                    yield num1 / num2;
                }
                default -> {
                    System.out.println("Unknown operator!");
                    yield Double.NaN;
                }
            };

            if (!Double.isNaN(result)) {
                System.out.println("Result: " + result);
            }
        }
        
        System.out.println("Calculator exited. Connection closed.");
        scanner.close(); // Always close resources to prevent memory leaks
    }
}
