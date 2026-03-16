package exercises;

import java.util.HashMap;
import java.util.Map;

/**
 * EXERCISE 1: Simulating @Value from application.properties
 * 
 * TASK:
 * 1. Read the provided 'MockSpringEnvironment' map simulating an application.properties file.
 * 2. Complete the setup of the 'StripeService' using theoretical @Value injection.
 * 3. Extract the 'stripe.api.key' from the map.
 * 4. Extract 'stripe.timeout.ms' from the map.
 * 5. Important: Provide a default fallback of 5000ms if 'stripe.timeout.ms' does not exist!
 */
public class Ex01_Properties {

    public static void main(String[] args) {
        
        // This Map simulates loaded application.properties in Spring Memory
        Map<String, String> applicationProperties = new HashMap<>();
        applicationProperties.put("stripe.api.key", "sk_test_12345");
        // Application forgot to set stripe.timeout.ms!

        System.out.println("--- Booting Stripe Service ---");
        
        // TODO: 3. Extract the values from applicationProperties map.
        // TODO: 5. Ensure the timeout correctly falls back to "5000" if null.
        String apiKey = applicationProperties.get("stripe.api.key");
        
        String timeoutStr = applicationProperties.get("stripe.timeout.ms");
        int timeoutMillis = (timeoutStr != null) ? Integer.parseInt(timeoutStr) : 5000;

        // Creating the Service (In real Spring, the IoC does this passing the @Value fields automatically)
        StripeService service = new StripeService(apiKey, timeoutMillis);
        
        service.charge();
    }

    public static class StripeService {
        
        // In real Spring: @Value("${stripe.api.key}")
        private final String apiKey;
        
        // In real Spring: @Value("${stripe.timeout.ms:5000}") 
        private final int timeoutMillis;

        public StripeService(String apiKey, int timeoutMillis) {
            this.apiKey = apiKey;
            this.timeoutMillis = timeoutMillis;
        }

        public void charge() {
            System.out.println("Processing Payment via Stripe API Key: " + apiKey);
            System.out.println("Using Network Timeout: " + timeoutMillis + "ms");
        }
    }
}
