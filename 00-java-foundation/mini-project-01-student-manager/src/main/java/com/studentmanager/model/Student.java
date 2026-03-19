package com.studentmanager.model;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Student.java | MODULE: mini-project-01                              ║
 * ║ PURPOSE: Data entity/POJO representing a Student in the system.           ║
 * ║ WHY IT EXISTS: To encapsulate student state with type-safe properties.    ║
 * ║ PYTHON COMPARE: Like a Pydantic Model (BaseModel) or a dataclass.         ║
 * ║                                                                          ║
 * ║ ARCHITECTURAL ASCII DIAGRAM:                                             ║
 * ║ [ RAW DATA ] -> [ VALIDATOR ] -> [ STUDENT OBJECT ] -> [ COLLECTION ]    ║
 * ║       |               |                 |                    |           ║
 * ║    User In        Field Rule         Encapsulated          ArrayList     ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

import com.studentmanager.exception.InvalidStudentDataException;

/**
 * Layer 2 - Class Level: Encapsulates state for a Student record.
 * Python Equivalent: @dataclass class Student: id: int; name: str; gpa: float
 */
public class Student {
    private int id;
    private String name;
    private double gpa;
    private String major;

    /**
     * Layer 3 - Constructor: Validates data before instantiation.
     * Python Equivalent: __init__(self, id, name, gpa, major)
     */
    public Student(int id, String name, double gpa, String major) {
        validateData(name, gpa); // Layer 4 - Inline Validation
        this.id = id;
        this.name = name;
        this.gpa = gpa;
        this.major = major;
    }

    // --- Standard Getters/Setters (Layer 3 & 4) ---

    public int getId() { return id; }

    public String getName() { return name; }

    public String getMajor() { return major; }

    public double getGpa() { return gpa; }

    /**
     * Layer 3: Setting GPA must enforce business rules.
     * Python Equivalent: @gpa.setter def gpa(self, value):
     */
    public void setGpa(double gpa) {
        validateGpa(gpa); // Layer 4
        this.gpa = gpa;
    }

    /**
     * Layer 4 - Private Validation Logic
     */
    private void validateData(String name, double gpa) {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidStudentDataException("Name cannot be empty.");
        }
        validateGpa(gpa);
    }

    private void validateGpa(double gpa) {
        if (gpa < 0.0 || gpa > 4.0) {
            throw new InvalidStudentDataException("GPA must be between 0.0 and 4.0. Found: " + gpa);
        }
    }

    @Override
    public String toString() {
        return String.format("Student[ID=%d, Name='%s', GPA=%.2f, Major='%s']", id, name, gpa, major);
    }
}
