package explanation;

import java.util.Optional;

/**
 * DEMO: How JPA conceptually links Java Objects to Database Operations
 * 
 * NOTE: This is pseudo-code demonstrating the API. To run this natively,
 * passing this code through a real Spring Boot Container + H2 Database is required.
 */
public class JpaDemo {

    public static void main(String[] args) {
        System.out.println("--- Spring Data JPA Engine Simulator ---");
        
        // Imagine: Spring IoC automatically injects the interface proxy here.
        UserRepository repository = getSimulatedProxy();

        // 1. CREATE
        System.out.println("\n[Saving] Translates to: INSERT INTO users (name, email) VALUES ...");
        User alice = new User(null, "Alice", "alice@test.com");
        repository.save(alice); // Hibernate populates the Auto-Incremented ID on this physical object now!

        // 2. READ (Derived Query Method)
        System.out.println("\n[Querying] Translates to: SELECT * FROM users WHERE email = 'bob@test.com'");
        Optional<User> bob = repository.findByEmail("bob@test.com");
        
        if (bob.isPresent()) {
            System.out.println("Found Bob: " + bob.get().getName());
        } else {
            System.out.println("Bob not found.");
        }
    }

    // --- Entity ---
    public static class User {
        private Long id; // @Id @GeneratedValue
        private String name;
        private String email;

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
    }

    // --- Interface ---
    public interface UserRepository {
        // Built-in methods conceptually from JpaRepository
        void save(User entity);
        Optional<User> findById(Long id);
        
        // Derived Method conceptually parsed by Spring
        Optional<User> findByEmail(String email);
    }

    // Proxy Simulator mimicking what Spring generates at Startup
    public static UserRepository getSimulatedProxy() {
        return new UserRepository() {
            public void save(User entity) {
                System.out.println("Hibernate Engine inserting User object into DB...");
            }
            public Optional<User> findById(Long id) {
                return Optional.empty();
            }
            public Optional<User> findByEmail(String email) {
                if (email.equals("bob@test.com")) {
                    return Optional.of(new User(2L, "Bob", email));
                }
                return Optional.empty();
            }
        };
    }
}
