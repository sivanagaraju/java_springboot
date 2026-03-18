package exercises;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Ex01_ConstructorInjection.java                                                     ║
 * ║ PURPOSE & CONCEPT: Refactoring to Constructor Injection                                  ║
 * ║ The 'UserService' currently physically instantiates a 'RealUserRepository' using 'new'.  ║
 * ║ This exercise eliminates tight coupling by applying Dependency Injection.                ║
 * ║                                                                                          ║
 * ║ ┌──────────────────────────────────────────────────────────────────────────────────────┐ ║
 * ║ │ ASCII DIAGRAM: Refactored Constructor Injection                                      │ ║
 * ║ ├──────────────────────────────────────────────────────────────────────────────────────┤ ║
 * ║ │ [Main] ──(instantiates)──► [MockUserRepository]                                      │ ║
 * ║ │   │                                                                                  │ ║
 * ║ │   └──────(injects into)──► [UserService]                                             │ ║
 * ║ └──────────────────────────────────────────────────────────────────────────────────────┘ ║
 * ║                                                                                          ║
 * ║ PYTHON COMPARE:                                                                          ║
 * ║ Passing a mock database into `UserService.__init__(self, db)` for easy unit testing.     ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 * 
 * TASK:
 * 1. Delete the tight coupling in 'UserService'.
 * 2. Add a constructor to 'UserService' that accepts the 'UserRepository' Interface.
 * 3. Make the private field 'final' to mathematically guarantee immutability natively.
 * 4. In the main() method, inject the 'MockUserRepository' manually instead of the Real one.
 */
public class Ex01_ConstructorInjection {

    public static void main(String[] args) {
        
        // Booting Application
        System.out.println("--- Booting Application ---");
        // TODO: 5. Manually inject the MockUserRepository into the UserService here.
        var mockRepo = new MockUserRepository();
        var service = new UserService(mockRepo); // Fix this compilation error once refactored.
        
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
        
        // 2. Add 'final' to the field.
        private final UserRepository repository;

        // 3. Add a Constructor that accepts the UserRepository.
        public UserService(UserRepository repository) {
            this.repository = repository;
        }

        public void saveUser(String username) {
            System.out.print("Processing business logic... ");
            repository.save(username);
        }
    }
}
