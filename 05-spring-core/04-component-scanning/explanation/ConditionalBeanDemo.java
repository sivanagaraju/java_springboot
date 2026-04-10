/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ConditionalBeanDemo.java                               ║
 * ║  MODULE : 05-spring-core / 04-component-scanning                  ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Shows @Profile and @Conditional for            ║
 * ║                   environment-based bean selection                ║
 * ║  WHY IT EXISTS  : Same app must behave differently in dev/prod  ║
 * ║                   without code changes — only config changes     ║
 * ║  PYTHON COMPARE : Python: if os.getenv("ENV") == "prod"         ║
 * ║                   → Java: @Profile("prod") on @Bean             ║
 * ║  USE CASES      : 1) H2 for dev, PostgreSQL for prod           ║
 * ║                   2) Mock email for dev, real email for prod     ║
 * ║                   3) Feature flags via properties                 ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Profile Selection                                ║
 * ║                                                                    ║
 * ║    spring.profiles.active = ?                                      ║
 * ║        │                                                           ║
 * ║        ├── "dev"  → DevDatabase + MockEmail + VerboseLogging      ║
 * ║        ├── "prod" → PostgreSQL  + SmtpEmail + MinimalLogging      ║
 * ║        └── "test" → H2Memory   + NoOpEmail + TestLogging         ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║               --args="--spring.profiles.active=dev"               ║
 * ║  EXPECTED OUTPUT: Profile-specific bean messages                  ║
 * ║  RELATED FILES  : 04-conditional-beans.md                        ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.scanning;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Interface for database connection — different implementations per profile.
 */
interface DatabaseConnection {
    String info();
}

/**
 * Dev database — H2 in-memory for fast development.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   if os.getenv("ENV") == "dev":
 *       db = sqlite3.connect(":memory:")
 * </pre>
 */
@Service
@Profile("dev")
class DevDatabaseConnection implements DatabaseConnection {
    @Override
    public String info() { return "H2 In-Memory (dev)"; }
}

/**
 * Production database — PostgreSQL with connection pooling.
 */
@Service
@Profile("prod")
class ProdDatabaseConnection implements DatabaseConnection {
    @Override
    public String info() { return "PostgreSQL + HikariCP (prod)"; }
}

/**
 * Default database — used when no profile is active.
 */
@Service
@Profile("default")
class DefaultDatabaseConnection implements DatabaseConnection {
    @Override
    public String info() { return "H2 Embedded (default profile)"; }
}

/**
 * Conditional feature flag bean — only created if property is set.
 *
 * <p><b>ASCII — Conditional Flow:</b>
 * <pre>
 *   app.feature.analytics.enabled = true?
 *       │
 *       ├── YES → AnalyticsService bean created
 *       │
 *       └── NO  → AnalyticsService bean NOT created
 * </pre>
 */
@Configuration
class FeatureFlagConfig {

    @Bean
    @ConditionalOnProperty(name = "app.feature.analytics.enabled", havingValue = "true")
    public AnalyticsService analyticsService() {
        System.out.println("  → AnalyticsService ENABLED via feature flag");
        return new AnalyticsService();
    }
}

class AnalyticsService {
    public void track(String event) {
        System.out.println("  📊 Analytics: " + event);
    }
}
