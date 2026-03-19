package com.studentmanager;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: StudentServiceTest.java | MODULE: mini-project-01                   ║
 * ║ PURPOSE: Functional verification of the Service Layer logic.              ║
 * ║ WHY IT EXISTS: To ensure O(1) searches and logic work as expected.        ║
 * ║ PYTHON COMPARE: Like a pytest file with @pytest.fixture and assert.       ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

import com.studentmanager.model.Student;
import com.studentmanager.service.StudentService;
import com.studentmanager.service.StudentServiceImpl;
import com.studentmanager.exception.StudentNotFoundException;
import com.studentmanager.exception.InvalidStudentDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Layer 2 - Test Level: Core integration tests for the student management logic.
 * Python Equivalent: def test_student_manager_logic():
 */
public class StudentServiceTest {

    private StudentService service;

    /**
     * Layer 3: Setup fresh state per test.
     * Python Equivalent: @pytest.fixture
     */
    @BeforeEach
    void setUp() {
        service = new StudentServiceImpl();
        service.addStudent(new Student(1, "Alice", 3.8, "CS"));
        service.addStudent(new Student(2, "Bob", 3.5, "IT"));
    }

    /**
     * Layer 3: O(1) lookup test.
     * Python Equivalent: assert service.find(1).name == 'Alice'
     */
    @Test
    void testFindByIdSuccess() throws StudentNotFoundException {
        // Arrange & Act (AAA Pattern)
        Student s = service.findById(1);
        
        // Assert
        assertEquals("Alice", s.getName(), "Should retrieve the correct student name");
        assertEquals(3.8, s.getGpa(), 0.01, "GPA should match within delta");
    }

    /**
     * Layer 3: Custom Exception verification.
     * Python Equivalent: with pytest.raises(StudentNotFoundException):
     */
    @Test
    void testFindByIdNotFound() {
        // Assert (Inlined Act & Assert for exceptions)
        assertThrows(StudentNotFoundException.class, () -> service.findById(999));
    }

    /**
     * Layer 3: Double ID constraint verification.
     * Python Equivalent: with pytest.raises(ValidationError):
     */
    @Test
    void testAddDuplicateId() {
        // Arrange
        Student duplicate = new Student(1, "Clara", 3.0, "Math");
        
        // Assert
        assertThrows(InvalidStudentDataException.class, () -> service.addStudent(duplicate));
    }

    /**
     * Layer 3: Edge Case - Null/Empty Major.
     */
    @Test
    void testFilterByMajorCaseInsensitive() {
        // Act
        List<Student> results = service.filterByMajor("  cs  "); // Leading/trailing spaces
        
        // Assert
        assertEquals(1, results.size(), "Should handle whitespace and casing in major filter");
    }

    /**
     * Layer 3: Edge Case - Update GPA boundaries.
     */
    @Test
    void testUpdateGpaInvalidBoundaries() {
        // Assert (L4 - Exception check for invalid domain value)
        assertThrows(InvalidStudentDataException.class, () -> service.updateGpa(1, 5.0));
        assertThrows(InvalidStudentDataException.class, () -> service.updateGpa(1, -1.0));
    }

    /**
     * Layer 3: Streams API Filtering test.
     * Python Equivalent: assert len([s for s in students if s.major == 'CS']) == 1
     */
    @Test
    void testFilterByMajor() {
        // Act
        List<Student> csStudents = service.filterByMajor("CS");
        List<Student> emptyList = service.filterByMajor("Bio");
        
        // Assert
        assertEquals(1, csStudents.size());
        assertEquals(0, emptyList.size());
    }

    /**
     * Layer 3: Removal logic verification.
     */
    @Test
    void testRemoveStudent() throws StudentNotFoundException {
        // Act
        service.removeStudent(1);
        
        // Assert
        assertEquals(1, service.getAllStudents().size(), "List size should decrease");
        assertThrows(StudentNotFoundException.class, () -> service.findById(1));
    }
}
