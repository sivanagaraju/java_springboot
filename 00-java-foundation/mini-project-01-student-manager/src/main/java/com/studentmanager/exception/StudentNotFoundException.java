package com.studentmanager.exception;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: StudentNotFoundException.java | MODULE: mini-project-01             ║
 * ║ PURPOSE: Checked exception for business-level search failures.            ║
 * ║ WHY IT EXISTS: To force the UI layer to explicitly handle missing data.   ║
 * ║ PYTHON COMPARE: class NotFoundError(Exception):                           ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

/**
 * Layer 2 - Class Level: Checked exception (extends Exception).
 * Python Equivalent: Custom error class.
 */
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
