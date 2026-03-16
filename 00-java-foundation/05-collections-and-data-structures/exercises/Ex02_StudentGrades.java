package exercises;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * EXERCISE 2: Student Grades
 * 
 * TASK:
 * 1. Initialize a HashMap 'gradeBook' mapping String -> Integer.
 * 2. Add students: "Alice" (95), "Bob" (82), "Charlie" (88).
 * 3. Look up and print Bob's grade.
 * 4. Initialize a PriorityQueue of Integers to sort the grades. 
 *    (By default PriorityQueue sorts ascending).
 * 5. Add all the grades from the map into the PriorityQueue.
 * 6. Poll the queue until empty to print grades from lowest to highest.
 */
public class Ex02_StudentGrades {

    public static void main(String[] args) {
        
        // TODO: 1 & 2. Create gradeBook Map and add students
        Map<String, Integer> gradeBook = new HashMap<>();
        gradeBook.put("Alice", 95);
        gradeBook.put("Bob", 82);
        gradeBook.put("Charlie", 88);

        // TODO: 3. Look up Bob's grade
        System.out.println("Bob's Grade: " + gradeBook.get("Bob"));

        // TODO: 4 & 5. Create PriorityQueue and add all grades
        Queue<Integer> gradeQueue = new PriorityQueue<>();
        gradeQueue.addAll(gradeBook.values());

        System.out.println("\nGrades sorted lowest to highest:");
        // TODO: 6. Poll the queue
        while (!gradeQueue.isEmpty()) {
            System.out.println(gradeQueue.poll());
        }
    }
}
