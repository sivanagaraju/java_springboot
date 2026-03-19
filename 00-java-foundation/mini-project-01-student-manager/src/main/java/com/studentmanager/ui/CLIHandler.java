package com.studentmanager.ui;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: CLIHandler.java | MODULE: mini-project-01                           ║
 * ║ PURPOSE: Terminal UI management using java.util.Scanner.                  ║
 * ║ WHY IT EXISTS: To centralize UI interactions outside the model/service.   ║
 * ║ PYTHON COMPARE: A loop of input() calls and print() statements.           ║
 * ║                                                                          ║
 * ║ ARCHITECTURAL ASCII DIAGRAM:                                             ║
 * ║ [ MENU SELECTION ] -> [ PROCESSOR ] -> [ SERVICE ] -> [ RESULT TABLE ]   ║
 * ║       |                   |               |               |              ║
 * ║ User Choice           Input Check      Business logic     Final Output    ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

import com.studentmanager.model.Student;
import com.studentmanager.service.StudentService;
import com.studentmanager.service.StudentServiceImpl;
import com.studentmanager.exception.StudentNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * Layer 2 - UI Level: Interactive console wrapper for the student service.
 * Python Equivalent: def main_loop(): while True: choice = input(...)
 */
public class CLIHandler {
    private final StudentService service = new StudentServiceImpl();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Layer 3: Main UI loop control.
     * Python Equivalent: while running: ...
     */
    public void start() {
        System.out.println("=== STUDENT MANAGEMENT SYSTEM 1.0 ===");
        while (true) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1: addStudent(); break;
                case 2: displayAll(); break;
                case 3: updateGpa(); break;
                case 4: searchById(); break;
                case 5: filterByMajor(); break;
                case 6: { System.out.println("Goodbye!"); return; }
                default: System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n1. Add Student | 2. View All | 3. Update GPA | 4. Search ID | 5. Filter Major | 6. Exit");
    }

    /**
     * Layer 3: Mapping UI fields to actual Model constructors.
     * Python Equivalent: Student(id=id, name=name, gpa=gpa, major=major)
     */
    private void addStudent() {
        try {
            int id = readInt("Enter Student ID: ");
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            double gpa = readDouble("Enter GPA (0.0-4.0): ");
            System.out.print("Enter Major: ");
            String major = scanner.nextLine();

            service.addStudent(new Student(id, name, gpa, major));
            System.out.println("✅ Student added successfully!");
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }

    private void displayAll() {
        printStudentList(service.getAllStudents());
    }

    private void updateGpa() {
        try {
            int id = readInt("Enter Student ID to update: ");
            double gpa = readDouble("Enter NEW GPA: ");
            service.updateGpa(id, gpa);
            System.out.println("✅ GPA updated successfully!");
        } catch (StudentNotFoundException e) {
            System.err.println("❌ ERROR: " + e.getMessage());
        }
    }

    private void searchById() {
        try {
            int id = readInt("Enter ID: ");
            System.out.println(service.findById(id));
        } catch (StudentNotFoundException e) {
            System.err.println("❌ " + e.getMessage());
        }
    }

    private void filterByMajor() {
        System.out.print("Enter Major name: ");
        String major = scanner.nextLine();
        printStudentList(service.filterByMajor(major));
    }

    private void printStudentList(List<Student> list) {
        if (list.isEmpty()) {
            System.out.println("No records found.");
        } else {
            System.out.println("------------------------------------------------------------------");
            System.out.printf("%-10s %-20s %-10s %-15s\n", "ID", "NAME", "GPA", "MAJOR");
            System.out.println("------------------------------------------------------------------");
            list.forEach(s -> System.out.printf("%-10d %-20s %-10.2f %-15s\n", 
                s.getId(), s.getName(), s.getGpa(), s.getMajor()));
        }
    }

    // Helper utilities (Layer 4)
    private int readInt(String prompt) {
        System.out.print(prompt);
        int val = scanner.nextInt();
        scanner.nextLine(); // consumes newline
        return val;
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        double val = scanner.nextDouble();
        scanner.nextLine(); // consumes newline
        return val;
    }
}
