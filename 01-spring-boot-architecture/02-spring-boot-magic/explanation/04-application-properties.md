# Application Properties

Because Spring Boot relies heavily on "Auto-Configuration" guessing what your app needs natively, how do you *tell* it to behave differently?

The answer is the `application.properties` (or `application.yml`) file.

## The Global Configuration Map

When Spring Boot starts up, it reads this physical file from your `src/main/resources/` directory and loads every single key-value pair directly into the global Spring **Environment**.

By default, the `@Conditional` auto-configuration classes constantly sniff these Environment property values. 

For example:

```properties
# Overrides the default embedded Tomcat port (8080) to 9090.
server.port=9090

# Tells the Auto-Configured DataSource to connect to PostgreSQL instead of in-memory H2.
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=admin
spring.datasource.password=secret123

# Overrides exactly how verbose logging should functionally be.
logging.level.org.springframework.web=DEBUG
```

## Injecting Custom Properties (`@Value`)

You can also create your own custom business keys inside the file:
```properties
app.tax-rate=0.07
app.feature.new-checkout=true
```

Then you can cleanly inject these raw Strings or primitives natively into any of your own Spring Beans using the `@Value` annotation:

```java
@Service
public class CheckoutService {

    // Spring parses the properties file and injects 0.07 into this field.
    @Value("${app.tax-rate}")
    private double taxRate;

    public double calculateTotal(double subtotal) {
        return subtotal + (subtotal * taxRate);
    }
}
```

## Profile Properties

Spring Boot also allows you to define multiple properties files for different deployment environments (`dev`, `staging`, `prod`):
- `application-dev.properties` (Used when testing locally)
- `application-prod.properties` (Used when running on real servers)

By injecting an environment variable to the JVM (`-Dspring.profiles.active=prod`), Spring will perfectly load your core file AND overlay the `prod` file dynamically.
