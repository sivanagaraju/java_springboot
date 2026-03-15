/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_TypeConversion.java                               ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="Ex01_TypeC..."
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Practice implicit vs explicit type casting     ║
 * ║  WHY IT EXISTS  : Type truncation bugs are common in Java.       ║
 * ║  PYTHON COMPARE : Python auto-promotes float/int. Java does too, ║
 * ║                   but going backward down sizes requires casting.║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics.exercises;

public class Ex01_TypeConversion {
    public static void main(String[] args) {
        System.out.println("--- Exercise 1: Type Conversion ---");
        
        // 1. Widening (Implicit) - No data loss
        int score = 100;
        double decimalScore = score;
        System.out.println("Widening: int " + score + " -> double " + decimalScore);

        // 2. Narrowing (Explicit Cast Required) - Potential data loss!
        double exactPrice = 99.99;
        // int roundedPrice = exactPrice; // Compile error!
        int truncatedPrice = (int) exactPrice;
        System.out.println("Narrowing: double " + exactPrice + " -> int " + truncatedPrice);

        // 3. Overflow Trap
        // byte goes from -128 to 127
        int largeNumber = 130;
        byte wrapped = (byte) largeNumber;
        System.out.println("Overflow trap: int " + largeNumber + " cast to byte is " + wrapped);
        // Output will be -126 because of integer bit overflow!
    }
}
