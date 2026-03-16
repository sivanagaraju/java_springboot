package exercises;

/**
 * EXERCISE 1: Exception Handling safely catching Runtime exceptions.
 * 
 * TASK:
 * 1. Implement the 'parseNumber' method correctly.
 * 2. If 'input' is null, catch NullPointerException and print "Error: Input is null."
 * 3. If 'input' is not a valid number, catch NumberFormatException and print "Error: Invalid number format."
 * 4. Use a finally block to print "Parsing attempt finished."
 */
public class Ex01_ExceptionHandling {

    public static void main(String[] args) {
        System.out.println("Test 1: Normal parsing");
        parseNumber("123");

        System.out.println("\nTest 2: Null parsing");
        parseNumber(null);

        System.out.println("\nTest 3: Invalid parsing");
        parseNumber("123abc");
    }

    public static void parseNumber(String input) {
        // TODO: Try to parse Integer.parseInt(input)
        // Catch NullPointerException
        // Catch NumberFormatException
        // Add a Finally block
        
        try {
            int result = Integer.parseInt(input);
            System.out.println("Successfully parsed: " + result);
        } catch (NullPointerException e) {
            System.out.println("Error: Input is null.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format.");
        } finally {
            System.out.println("Parsing attempt finished.");
        }
    }
}
