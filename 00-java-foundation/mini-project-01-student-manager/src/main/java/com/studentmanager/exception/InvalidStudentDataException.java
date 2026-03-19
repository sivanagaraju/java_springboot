package com.studentmanager.exception;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: InvalidStudentDataException.java | MODULE: mini-project-01            ║
 * ║ PURPOSE: Unchecked exception for malformed data or domain violations.      ║
 * ║ WHY IT EXISTS: To halt execution on invalid GPA, names, or double IDs.     ║
 * ║ PYTHON COMPARE: class ValidationError(ValueError):                        ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

/**
 * Layer 2 - Class Level: Unchecked (extends RuntimeException).
 * Python Equivalent: ValueError subclass.
 */
public class InvalidStudentDataException extends RuntimeException {
    public InvalidStudentDataException(String message) {
        super(message);
    }
}
