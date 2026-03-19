package com.studentmanager.service;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: StudentServiceImpl.java | MODULE: mini-project-01                   ║
 * ║ PURPOSE: Concrete business logic for managing student records.            ║
 * ║ WHY IT EXISTS: To optimize performance (O(1) lookups) with Maps.          ║
 * ║ PYTHON COMPARE: A Python class that uses a dict {id: Student}.            ║
 * ║                                                                          ║
 * ║ ARCHITECTURAL ASCII DIAGRAM:                                             ║
 * ║ [ INTERFACE ] -> [ IMPL CLASS ] -> [ MAP (O(1)) ] | [ LIST (O(n)) ]      ║
 * ║      |                   |                  |             |              ║
 * ║ Definition         Business Logic         ID-based      Seq-indexed      ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

import com.studentmanager.model.Student;
import com.studentmanager.exception.StudentNotFoundException;
import com.studentmanager.exception.InvalidStudentDataException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Layer 2 - Implementation Level: Acts as the core repository and logic engine.
 * Python Equivalent: class StudentServiceImpl(StudentService):
 */
public class StudentServiceImpl implements StudentService {
    
    // Layer 4 - Memory-efficient dual storage pattern
    private final Map<Integer, Student> studentMap = new HashMap<>(); // O(1) Search
    private final List<Student> studentList = new ArrayList<>();    // Insertion Order

    /**
     * Layer 3: Explicitly check for ID uniqueness.
     * Python Equivalent: if id in self.student_map: raise ValueError
     */
    @Override
    public void addStudent(Student s) {
        if (studentMap.containsKey(s.getId())) {
             throw new InvalidStudentDataException("Student ID " + s.getId() + " already exists.");
        }
        studentMap.put(s.getId(), s);
        studentList.add(s);
    }

    @Override
    public List<Student> getAllStudents() {
        return new ArrayList<>(studentList);
    }

    /**
     * Layer 3: O(1) search performance using HashMap.
     * Python Equivalent: return self.student_map.get(id)
     */
    @Override
    public Student findById(int id) throws StudentNotFoundException {
        Student s = studentMap.get(id);
        if (s == null) {
             throw new StudentNotFoundException("Student not found with ID: " + id);
        }
        return s;
    }

    /**
     * Layer 3: Updates state in both the List and Map.
     * Python Equivalent: self.student_map[id].gpa = new_gpa
     */
    @Override
    public void updateGpa(int id, double gpa) throws StudentNotFoundException {
        Student s = findById(id);
        s.setGpa(gpa); // Setter performs validation
    }

    /**
     * Layer 3: Deep Practitioner Java Stream approach.
     * Python Equivalent: [s for s in students if s.major == major]
     */
    @Override
    public List<Student> filterByMajor(String major) {
        return studentList.stream()
            .filter(s -> s.getMajor().equalsIgnoreCase(major.trim()))
            .collect(Collectors.toList());
    }

    /**
     * Layer 3: Handles atomic removal from all structures.
     * Python Equivalent: del self.student_map[id]; self.student_list.remove(s)
     */
    @Override
    public void removeStudent(int id) throws StudentNotFoundException {
        Student s = findById(id);
        studentMap.remove(id);
        studentList.remove(s);
    }
}
