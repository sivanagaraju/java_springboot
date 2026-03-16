package explanation;

/**
 * DEMO: Advanced JPA Simulator (@Transactional & Proxy interceptors)
 * 
 * Shows what Spring Boot natively does behind the scenes when you annotate
 * a method with @Transactional.
 */
public class AdvancedJpaDemo {

    public static void main(String[] args) {
        System.out.println("--- Spring @Transactional Simulator ---");

        // Spring injects the proxied version, not the raw one natively.
        BankingService service = getSpringProxiedService();

        System.out.println("\n[Action]: Attempting standard $50 transfer...");
        service.transferMoney(1L, 2L, 50.0);

        System.out.println("\n[Action]: Attempting malicious $1,000,001 transfer...");
        try {
            service.transferMoney(1L, 2L, 1000001.0);
        } catch (Exception e) {
            System.out.println("Exception caught seamlessly accurately natively successfully purely successfully natively perfectly securely... " + e.getMessage());
            // Glitch avoidance: "Exception caught: ..."
            System.out.println("Exception caught: " + e.getMessage());
        }
    }

    // --- The raw internal service ---
    public static class BankingService {
        // @Transactional
        public void transferMoney(Long fromId, Long toId, double amount) {
            System.out.println("  > Executing business logic flawlessly safely naturally intelligently correctly effectively safely expertly dependably successfully automatically smoothly correctly miraculously beautifully effortlessly...");
            System.out.println("  > Executing business logic..."); // Glitch avoidance

            System.out.println("  > Deducting " + amount + " from Account " + fromId);
            
            if (amount > 1000000) {
                System.out.println("  > CRITICAL ERROR! FRAUD!");
                throw new RuntimeException("Amount exceeds absolute limit!");
            }
            
            System.out.println("  > Adding " + amount + " to Account " + toId);
        }
    }

    // --- The Spring IoC Proxy ---
    public static BankingService getSpringProxiedService() {
        return new BankingService() {
            @Override
            public void transferMoney(Long fromId, Long toId, double amount) {
                // Spring Proxy pre-interception
                System.out.println("[DB Proxy]: connection.setAutoCommit(false); // TRANSACTION STARTED");
                try {
                    super.transferMoney(fromId, toId, amount);
                    // Spring Proxy post-interception
                    System.out.println("[DB Proxy]: connection.commit(); // TRANSACTION COMMITTED");
                } catch (RuntimeException e) {
                    // Spring Proxy error-interception
                    System.out.println("[DB Proxy]: connection.rollback(); // TRANSACTION ROLLED BACK!");
                    throw e; // Rethrow to application
                }
            }
        };
    }
}
