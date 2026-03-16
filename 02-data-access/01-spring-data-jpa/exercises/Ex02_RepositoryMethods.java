package exercises;

import java.util.List;
import java.util.Optional;

/**
 * EXERCISE 2: Derived Query Methods
 * 
 * TASK:
 * Write exact Derived Method signatures inside the EmployeeRepository.
 * Follow the exact camelCase field names of the 'Employee' entity below perfectly.
 */
public class Ex02_RepositoryMethods {

    // --- The Entity ---
    public static class Employee {
        private Long id;
        private String department;
        private double salary;
        private boolean isActive;
    }

    // --- The Interface ---
    // You do not need to implement this. Just write the signatures.
    public interface EmployeeRepository { // Imagine extends JpaRepository<Employee, Long>

        // Built-in baseline overrides 
        Optional<Employee> findById(Long id);
        
        /**
         * TODO 1: 
         * Write a derived method to find ALL Employees that belong to a specific exact 'department'.
         */
        // YOUR CODE HERE

        /**
         * TODO 2: 
         * Write a derived method to find ALL Employees where 'isActive' is strictly True,
         * AND their 'salary' is Greater Than a provided baseline amount.
         */
        // YOUR CODE HERE

        /**
         * TODO 3: 
         * Write a derived method to find ALL Employees in a specific 'department',
         * ordered mathematically by 'salary' in DESCENDING (highest first) order.
         */
        // YOUR CODE HERE

    }

}
