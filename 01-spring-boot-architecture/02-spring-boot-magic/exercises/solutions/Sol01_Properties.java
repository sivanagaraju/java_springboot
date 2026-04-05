package exercises.solutions;

import java.util.HashMap;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: Sol01_Properties.java                                                              ║
 * ║ MODULE: 01-spring-boot-architecture / 02-spring-boot-magic / exercises/solutions         ║
 * ║ GRADLE: ./gradlew :01-spring-boot-architecture:run                                       ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ PURPOSE: Solution — Simulating @Value injection with default fallback                    ║
 * ║ DIFFICULTY: Beginner                                                                     ║
 * ║ PYTHON COMPARE: os.getenv('KEY', default_value) or reading config.ini                   ║
 * ╠══════════════════════════════════════════════════════════════════════════════════════════╣
 * ║ ASCII DIAGRAM: @Value Resolution Pipeline                                                ║
 * ║                                                                                          ║
 * ║   application.properties                                                                 ║
 * ║        │                                                                                 ║
 * ║        ▼  (loaded by Spring Environment on startup)                                      ║
 * ║   Spring PropertySources                                                                 ║
 * ║        │                                                                                 ║
 * ║        ▼  (@Value "${stripe.api.key}" resolved)                                          ║
 * ║   StripeService.apiKey = "sk_test_12345"                                                 ║
 * ║        │                                                                                 ║
 * ║        ▼  (@Value "${stripe.timeout.ms:5000}" — fallback 5000 if missing)                ║
 * ║   StripeService.timeoutMillis = 5000  (key not in properties → default used)             ║
 * ╚══════════════════════════════════════════════════════════════════════════════════════════╝
 */
public class Sol01_Properties {

    /**
     * Entry point simulating Spring Boot property resolution at startup.
     *
     * <p>Spring Boot does all of this automatically via {@code @Value} — this code
     * makes the mechanism explicit for learning purposes.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // Simulates the Map Spring builds internally from application.properties
        var applicationProperties = new HashMap<String, String>();
        applicationProperties.put("stripe.api.key", "sk_test_12345");
        // NOTE: stripe.timeout.ms is intentionally absent — tests the default fallback

        System.out.println("--- Booting Stripe Service ---");

        // WHY get() then null-check? This simulates Spring's @Value("${key:default}") syntax.
        // Spring resolves the property, falling back to the default after the colon.
        var apiKey = applicationProperties.get("stripe.api.key");

        // In Spring: @Value("${stripe.timeout.ms:5000}") — the :5000 is the default
        var timeoutStr = applicationProperties.get("stripe.timeout.ms");
        var timeoutMillis = (timeoutStr != null) ? Integer.parseInt(timeoutStr) : 5000;

        // WHY validate here? If apiKey is null, StripeService will fail at runtime
        // with a cryptic NullPointerException. Spring's @Value fails at startup instead.
        if (apiKey == null) {
            throw new IllegalStateException(
                "Required property 'stripe.api.key' is missing from application.properties");
        }

        var service = new StripeService(apiKey, timeoutMillis);
        service.charge();

        // EXPECTED OUTPUT:
        // --- Booting Stripe Service ---
        // Processing Payment via Stripe API Key: sk_test_12345
        // Using Network Timeout: 5000ms
    }

    // ─── Service ──────────────────────────────────────────────────────────────

    /**
     * Stripe payment gateway service.
     *
     * <p>In a real Spring Boot app this would use {@code @ConfigurationProperties}
     * for type-safe binding of multiple related properties:
     * <pre>
     *   {@code @Component}
     *   {@code @ConfigurationProperties(prefix = "stripe")}
     *   public class StripeProperties {
     *       private String apiKey;
     *       private int timeoutMs = 5000; // default
     *   }
     * </pre>
     *
     * <p><b>Python FastAPI equivalent:</b>
     * <pre>
     *   from pydantic_settings import BaseSettings
     *   class Settings(BaseSettings):
     *       stripe_api_key: str
     *       stripe_timeout_ms: int = 5000
     * </pre>
     */
    public static class StripeService {

        // WHY final? Configuration should not change after startup.
        // Mutable config fields can lead to race conditions in concurrent environments.
        private final String apiKey;
        private final int timeoutMillis;

        /**
         * Constructs the service with externally provided configuration.
         *
         * <p>In Spring Boot: {@code @Value("${stripe.api.key}")} on field or constructor param.
         *
         * @param apiKey        the Stripe secret key (non-null)
         * @param timeoutMillis the HTTP timeout in milliseconds (default 5000)
         */
        public StripeService(String apiKey, int timeoutMillis) {
            this.apiKey = apiKey;
            this.timeoutMillis = timeoutMillis;
        }

        /**
         * Initiates a payment charge via the Stripe API.
         */
        public void charge() {
            System.out.println("Processing Payment via Stripe API Key: " + apiKey);
            System.out.println("Using Network Timeout: " + timeoutMillis + "ms");
        }
    }
}

/*
 * COMMON MISTAKES
 * ═══════════════
 *
 * MISTAKE 1: Hardcoding API keys in source code
 *   WRONG: private final String apiKey = "sk_live_REAL_KEY";
 *   WHY BAD: Commits secrets to version control — catastrophic security breach.
 *   FIX: Always use @Value("${stripe.api.key}") and store keys in env vars or secrets manager.
 *
 * MISTAKE 2: Forgetting the default in @Value for optional properties
 *   WRONG: @Value("${stripe.timeout.ms}")  // throws BeanCreationException if missing
 *   CORRECT: @Value("${stripe.timeout.ms:5000}")  // colon separates key from default
 *
 * MISTAKE 3: Using int instead of Integer when a property might be missing
 *   If a property is optional and missing, Spring cannot inject into a primitive 'int'.
 *   Use Integer (wrapper) with a default: @Value("${timeout.ms:#{null}}")
 *   Or use @ConfigurationProperties which handles nulls gracefully.
 *
 * MISTAKE 4: Placing @Value on static fields
 *   WRONG: @Value("${key}") private static String value;
 *   WHY BAD: Spring cannot inject values into static fields via @Value.
 *   FIX: Use @Value on instance fields, or use @PostConstruct with a setter.
 */
