import java.util.ArrayList;
import java.util.List;

public class Ex02_ShapeHierarchy {

    // TODO 1: Create an abstract class 'Shape'.
    // Define a protected String field 'color'.
    // Create a constructor that initializes 'color'.
    // Create a public getter for 'color'.
    // Create an abstract public method 'double calculateArea()'.

    // TODO 2: Create a concrete class 'Circle' that extends 'Shape'.
    // Define a private double field 'radius'.
    // Create a constructor that accepts 'color' and 'radius'. (Remember to call super!)
    // Override 'calculateArea()' returning Math.PI * radius * radius.

    // TODO 3: Create a concrete class 'Rectangle' that extends 'Shape'.
    // Define private double fields 'width' and 'height'.
    // Create a constructor that accepts 'color', 'width', and 'height'.
    // Override 'calculateArea()' returning width * height.

    public static void main(String[] args) {
        System.out.println("--- Starting Hierarchy Test ---");
        
        // UNCOMMENT the following code to test your implementation
        
        /*
        // Notice the runtime polymorphism here!
        List<Shape> myShapes = new ArrayList<>();
        myShapes.add(new Circle("Red", 5.0));
        myShapes.add(new Rectangle("Blue", 4.0, 6.0));
        myShapes.add(new Circle("Green", 2.0));

        double totalArea = 0;
        for (Shape shape : myShapes) {
            double area = shape.calculateArea(); // Dynamic Dispatch!
            System.out.println(shape.getColor() + " shape area: " + String.format("%.2f", area));
            totalArea += area;
        }

        System.out.println("Total combined area: " + String.format("%.2f", totalArea));
        // Expected total output should be around 115.10
        */

        System.out.println("If the code above runs successfully and tests match expectations, you pass!");
    }
}
