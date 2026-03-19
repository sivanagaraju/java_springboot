package com.studentmanager;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: MockingDemoTest.java | MODULE: mini-project-01                      ║
 * ║ PURPOSE: Demonstrating Mockito (Zero-knowledge Python bridge).            ║
 * ║ WHY IT EXISTS: To show how to isolate components using Mocks.             ║
 * ║ PYTHON COMPARE: from unittest.mock import Mock, patch.                    ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */

import com.studentmanager.model.Student;
import com.studentmanager.service.StudentService;
import com.studentmanager.exception.StudentNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Layer 2 - Test Level: Mocking demonstration using Mockito.
 * Python Equivalent: @patch('my_module.MyClass')
 */
@ExtendWith(MockitoExtension.class)
public class MockingDemoTest {

    // Layer 3: Create a Mock (Python: mock_service = Mock())
    @Mock
    private StudentService service;

    /**
     * Layer 3: Testing "Success" behavior with Mocks.
     * Scenario: Stubbing a return value.
     */
    @Test
    void testMockingReturnValue() throws StudentNotFoundException {
        // 1. Arrange (Stubbing)
        // Python: service.findById.return_value = Student(...)
        Student mockStudent = new Student(101, "Mock Data", 4.0, "Testing");
        when(service.findById(101)).thenReturn(mockStudent);

        // 2. Act
        Student result = service.findById(101);

        // 3. Assert
        assertEquals("Mock Data", result.getName());
        
        // Layer 4: Verification (Python: service.findById.assert_called_once_with(101))
        verify(service, times(1)).findById(101);
    }

    /**
     * Layer 3: Testing "Failure" behavior with Mocks.
     * Scenario: Stubbing an exception.
     */
    @Test
    void testMockingException() throws StudentNotFoundException {
        // 1. Arrange (L4 - Telling the mock to throw an error)
        // Python: service.findById.side_effect = StudentNotFoundException(...)
        when(service.findById(999)).thenThrow(new StudentNotFoundException("Not found"));

        // 2. Act & Assert
        assertThrows(StudentNotFoundException.class, () -> service.findById(999));
        
        // Ensure the call was actually made
        verify(service).findById(999);
    }
}
