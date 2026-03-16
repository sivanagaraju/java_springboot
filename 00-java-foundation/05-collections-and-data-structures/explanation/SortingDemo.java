package explanation;

import java.util.*;

public class SortingDemo {

    public static void main(String[] args) {
        System.out.println("--- Comparable vs Comparator Demo ---");

        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(103, "Charlie", 50000));
        employees.add(new Employee(101, "Alice", 60000));
        employees.add(new Employee(102, "Bob", 45000));

        // 1. Natural Order (Comparable)
        // sort() relies on the Employee's compareTo() method (sorting by ID).
        Collections.sort(employees);
        System.out.println("Sorted by Natural Order (ID):");
        employees.forEach(System.out::println);

        // 2. Custom Order (Comparator Anonymous Inner Class)
        employees.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                return e1.getName().compareTo(e2.getName());
            }
        });
        System.out.println("\nSorted by Name (Anonymous Comparator):");
        employees.forEach(System.out::println);

        // 3. Custom Order (Modern Comparator Lambda)
        employees.sort(Comparator.comparingDouble(Employee::getSalary).reversed());
        System.out.println("\nSorted by Salary Descending (Lambda Comparator):");
        employees.forEach(System.out::println);
    }

    // A Domain Class implementing Comparable functionally models "Natural Order"
    public static class Employee implements Comparable<Employee> {
        private int id;
        private String name;
        private double salary;

        public Employee(int id, String name, double salary) {
            this.id = id;
            this.name = name;
            this.salary = salary;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public double getSalary() { return salary; }

        @Override
        public int compareTo(Employee other) {
            // Natively sorts by ID completely predictably.
            return Integer.compare(this.id, other.id);
        }

        @Override
        public String toString() {
            return "Employee{" + "id=" + id + ", name='" + name + "', salary=" + salary + '}';
        }
    }
}
