package explanation;

public class ExceptionsDemo {

    public static void main(String[] args) {
        System.out.println("--- Exception Hierarchy & Architecture Demo ---");
        
        Server server = new Server();
        
        // 1. Handling Checked Exceptions
        // Requires explicit try/catch or throws declaration
        try {
            server.connect();
        } catch (java.io.IOException e) {
            System.out.println("Checked Exception Caught: " + e.getMessage());
        }

        // 2. Handling Custom Business Exceptions
        try {
            server.authenticate("hacker", "wrong_password");
        } catch (AuthenticationFailedException e) {
            System.out.println("Security Alert! Failed login for user: " + e.getAttemptedUsername());
            System.out.println("Auth System Message: " + e.getMessage());
        }

        // 3. Try-With-Resources (AutoCloseable)
        System.out.println("\n--- Initiating File Stream ---");
        try (DatabaseConnection db = new DatabaseConnection()) {
            db.execute("SELECT * FROM USERS");
            // We throw a deliberate RuntimeException mid-execution
            throw new RuntimeException("Database timeout!");
        } catch (RuntimeException e) {
            System.out.println("Runtime Error Caught: " + e.getMessage());
        }
        // Notice the DatabaseConnection closes BEFORE the catch block logs the error!
        // This guarantees zero memory descriptor leaks.
    }

    // --- Custom Domain Exception ---
    public static class AuthenticationFailedException extends RuntimeException {
        private final String attemptedUsername;

        public AuthenticationFailedException(String message, String attemptedUsername) {
            super(message);
            this.attemptedUsername = attemptedUsername;
        }

        public String getAttemptedUsername() {
            return attemptedUsername;
        }
    }

    // --- Mock Server Component ---
    public static class Server {
        // Declaring a Checked Exception
        public void connect() throws java.io.IOException {
            System.out.println("Attempting to connect to external service...");
            throw new java.io.IOException("Network routing failed. Host unreachable.");
        }

        public void authenticate(String username, String password) {
            if ("wrong_password".equals(password)) {
                // Throwing our custom Unchecked Exception
                throw new AuthenticationFailedException("Invalid credentials provided", username);
            }
        }
    }

    // --- AutoCloseable Resource ---
    public static class DatabaseConnection implements AutoCloseable {
        
        public DatabaseConnection() {
            System.out.println("[DB] TCP Socket Opened.");
        }

        public void execute(String query) {
            System.out.println("[DB] Executing Query: " + query);
        }

        @Override
        public void close() {
            System.out.println("[DB] TCP Socket Closed Safely. Resource Reclaimed.");
        }
    }
}
