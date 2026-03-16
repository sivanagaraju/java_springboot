package exercises;

/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  EXERCISE 2    : HTTP Status Enum                                ║
 * ║  MODULE        : 00-java-foundation / 03-advanced-oop            ║
 * ║  GRADLE        : ./gradlew :00-java-foundation:run --args="Ex02_StatusEnum"  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  GOAL          : Build an Enum with custom fields and methods.   ║
 * ║  REQUIREMENTS  : 1. Enum named `HttpStatus`.                     ║
 * ║                  2. Constants: OK (200, "Success"),              ║
 * ║                     NOT_FOUND (404, "Not Found"),                ║
 * ║                     SERVER_ERROR (500, "Internal Error").        ║
 * ║                  3. Fields: `int code`, `String message`.        ║
 * ║                  4. A public method `isError()` that returns     ║
 * ║                     true if the code is 400 or greater.          ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
public class Ex02_StatusEnum {

    // TODO: Define the `HttpStatus` enum here
    public enum HttpStatus {
        OK(200, "Success"),
        NOT_FOUND(404, "Not Found"),
        SERVER_ERROR(500, "Internal Error");

        private final int code;
        private final String message;

        HttpStatus(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public boolean isError() {
            return this.code >= 400;
        }
    }

    public static void main(String[] args) {
        
        System.out.println("--- HTTP Status Checker ---");

        // TODO: Loop over the HttpStatus.values()
        // Print out the exact format: [200] Success -> isError: false
        for (HttpStatus status : HttpStatus.values()) {
            System.out.printf("[%d] %s -> isError: %b%n", 
                              status.getCode(), 
                              status.getMessage(), 
                              status.isError());
        }

        // EXPECTED OUTPUT:
        // [200] Success -> isError: false
        // [404] Not Found -> isError: true
        // [500] Internal Error -> isError: true
    }
}
