package exercises;

/**
 * EXERCISE 1: Refactoring to Constructor Injection
 * 
 * TASK:
 * 1. The 'UserService' currently physically instantiates a 'RealUserRepository' using 'new'.
 * 2. Delete the tight coupling.
 * 3. Add a constructor to 'UserService' that accepts the 'UserRepository' Interface.
 * 4. Make the private field 'final' to mathematically guarantee immutability natively.
 * 5. In the main() method, inject the 'MockUserRepository' manually instead of the Real one.
 */
public class Ex01_ConstructorInjection {

    public static void main(String[] args) {
        
        // TODO: 5. Manually inject the MockUserRepository into the UserService here.
        System.out.println("--- Booting Application ---");
        UserService service = new UserService(); // Fix this compilation error once refactored.
        
        service.saveUser("Alice");
    }

    // --- The Interface ---
    public interface UserRepository {
        void save(String username);
    }

    // --- A Real Database Connection ---
    public static class RealUserRepository implements UserRepository {
        public void save(String username) {
            System.out.println("[REAL DB] Establishing TCP Connection... Saving " + username);
        }
    }

    // --- A Fast Mock for Testing ---
    public static class MockUserRepository implements UserRepository {
        public void save(String username) {
            System.out.println("[MOCK DB] Skipped Network. Safely intercepted " + username);
        }
    }

    // --- The Service to Refactor ---
    public static class UserService {
        
        // TODO: 1. Remove this tight coupling.
        // TODO: 2. Add 'final' to the field.
        private UserRepository repository = new RealUserRepository();

        // TODO: 3. Add a Constructor that accepts the UserRepository.

        public void saveUser(String username) {
            System.out.print("Processing business logic... ");
            repository.save(username);
        }
    }
}
