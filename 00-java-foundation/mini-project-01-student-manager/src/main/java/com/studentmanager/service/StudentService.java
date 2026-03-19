package com.studentmanager.service;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: StudentService.java | MODULE: mini-project-01                       ║
 * ║ PURPOSE: Interface for Student Management business operations.            ║
 * ║ WHY IT EXISTS: To decouple CLI logic from the storage implementation.     ║
 * ║ PYTHON COMPARE: Abstract Base Classes (ABC) define the method signature.   ║
 * ║                                                                          ║
 * ║ ARCHITECTURAL ASCII DIAGRAM:                                             ║
 * ║ [ UI LAYER ] -> [ SERVICE INTERFACE ] -> [ SERVICE IMPLEMENTATION ]      ║
 * ║      |                  |                        |                       ║
 * ║ Input Parsing     Logical Contract             Actual Storage             ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

import com.studentmanager.model.Student;
import com.studentmanager.exception.StudentNotFoundException;
import java.util.List;

/**
 * Layer 2 - Interface Level: Defines the methods every implementation must provide.
 * Python Equivalent: class StudentService(ABC):
 */
public interface StudentService {
    
    /**
     * Layer 3: Contract to add a Student.
     * Python Equivalent: def add_student(self, s: Student) -> None:
     */
    void addStudent(Student s);

    /**
     * Layer 3: Contract to retrieve all Students.
     * Python Equivalent: def get_all_students(self) -> list[Student]:
     */
    List<Student> getAllStudents();

    /**
     * Layer 3: Contract to find by unique ID.
     * Python Equivalent: def get_student_by_id(self, id: int) -> Student:
     */
    Student findById(int id) throws StudentNotFoundException;

    /**
     * Layer 3: Contract to update GPA by ID.
     * Python Equivalent: def update_gpa(self, id: int, gpa: float) -> None:
     */
    void updateGpa(int id, double gpa) throws StudentNotFoundException;

    /**
     * Layer 3: Contract for Java Stream-based filtering.
     * Python Equivalent: def filter_by_major(self, major: str) -> list[Student]:
     */
    List<Student> filterByMajor(String major);

    /**
     * Layer 3: Contract for removal by ID.
     * Python Equivalent: def remove_student(self, id: int) -> None:
     */
    void removeStudent(int id) throws StudentNotFoundException;
}
