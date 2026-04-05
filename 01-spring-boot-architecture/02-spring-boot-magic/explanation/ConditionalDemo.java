package explanation;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * ╔══════════════════════════════════════════════════════════════════════════════╗
 * ║ FILE: ConditionalDemo.java                                                   ║
 * ║ MODULE: 01-spring-boot-architecture / 02-spring-boot-magic                  ║
 * ║ GRADLE CMD: N/A for this doc-only folder; compile with javac and run with   ║
 * ║             java explanation.ConditionalDemo from the module root           ║
 * ║ PURPOSE: Simulate profile overlays, conditional beans, and auto-config back ║
 * ║ WHY IT EXISTS: Spring Boot decides beans at startup, not through scattered  ║
 * ║                if-statements inside services                                 ║
 * ║ PYTHON COMPARE: Like loading settings, applying environment overrides, and   ║
 * ║                choosing a dependency factory at startup                      ║
 * ║ USE CASES: dev vs prod data sources, feature flags, starter fallback, custom ║
 * ║            bean override                                                     ║
 * ║ ARCHITECTURAL ASCII DIAGRAM:                                                ║
 * ║   base properties + profile overlay                                          ║
 * ║            -> condition evaluator                                            ║
 * ║            -> bean registry                                                  ║
 * ║            -> application ready                                              ║
 * ║ HOW TO RUN: from 02-spring-boot-magic, run                                  ║
 * ║             javac explanation/ConditionalDemo.java                          ║
 * ║             java explanation.ConditionalDemo                                ║
 * ║ EXPECTED OUTPUT: one scenario with H2 + console logging, one with a custom  ║
 * ║                  PostgreSQL-backed bean + structured logging                ║
 * ║ RELATED FILES: README.md, MINDMAP.md, explanation/05-profiles-and-conditional-beans.md,
 * ║                explanation/AutoConfigurationDemo.java                       ║
 * ╚══════════════════════════════════════════════════════════════════════════════╝
 */
/**
 * Simulates the Spring Boot startup decision tree for profiles and conditional beans.
 *
 * <pre>
 * Python equivalent:
 * class Container:
 *     def start(self, settings):
 *         settings = load_base_settings()
 *         settings.update(profile_settings)
 *         bean = custom_bean or default_factory(settings)
 * </pre>
 *
 * ASCII architecture:
 * base properties + profile overlay -> condition evaluator -> bean registry -> app ready
 */
public final class ConditionalDemo {

    private ConditionalDemo() {
        // Utility class.
    }

    /**
     * Runs two startup simulations so profile overlays and conditional backoff are visible.
     *
     * <pre>
     * Python equivalent:
     * settings = load_base_settings()
     * settings.update(profile_settings)
     * data_source = custom_ds or default_ds
     * logger = StructuredLogger if profile == "prod" else ConsoleLogger
     * </pre>
     *
     * ASCII flow:
     * base props -> profile overlay -> evaluate conditions -> register beans
     *
     * @param args ignored; the demo is self-contained.
     */
    public static void main(String[] args) {
        var devRequest = new StartupRequest(
            "dev",
            Map.of(
                "database.type", "h2",
                "feature.audit.enabled", "false"
            ),
            Map.of(
                "logging.mode", "console"
            ),
            false
        );

        var prodRequest = new StartupRequest(
            "prod",
            Map.of(
                "database.type", "postgres",
                "feature.audit.enabled", "false"
            ),
            Map.of(
                "logging.mode", "structured",
                "feature.audit.enabled", "true"
            ),
            true
        );

        runScenario("DEVELOPMENT", devRequest);
        runScenario("PRODUCTION", prodRequest);

        /*
         * EXPECTED OUTPUT:
         * ================
         * === DEVELOPMENT STARTUP ===
         * Active profile: dev
         * DataSource bean: H2DataSource ...
         * Request logger bean: ConsoleRequestLogger ...
         *
         * === PRODUCTION STARTUP ===
         * Active profile: prod
         * DataSource bean: UserDefinedPrimaryDataSource ...
         * Request logger bean: StructuredRequestLogger ...
         */
    }

    /**
     * Prints the resolved bean choices for a single startup scenario.
     *
     * <pre>
     * Python equivalent:
     * print(settings)
     * print(selected_db)
     * print(selected_logger)
     * </pre>
     *
     * ASCII flow:
     * startup request -> resolve context -> print selected beans
     *
     * @param label readable scenario name.
     * @param request the startup inputs for this scenario.
     */
    private static void runScenario(String label, StartupRequest request) {
        var resolved = resolveContext(request);

        System.out.println();
        System.out.println("=== " + label + " STARTUP ===");
        System.out.println("Active profile: " + resolved.activeProfile);
        System.out.println("Merged properties: " + resolved.mergedProperties);
        System.out.println("DataSource bean: " + resolved.dataSourceBean);
        System.out.println("Request logger bean: " + resolved.requestLoggerBean);
        System.out.println("Audit publisher bean: " + resolved.auditPublisherBean);
        System.out.println("Outcome: " + resolved.outcome);
    }

    /**
     * Merges the base and profile-specific properties, then resolves the beans that Boot would create.
     *
     * <pre>
     * Python equivalent:
     * merged = {**base, **profile}
     * </pre>
     *
     * ASCII flow:
     * base props + profile props -> merge -> condition evaluation -> resolved beans
     *
     * @param request startup inputs for one profile.
     * @return the resolved bean set for that profile.
     */
    private static ResolvedContext resolveContext(StartupRequest request) {
        var mergedProperties = new LinkedHashMap<>(request.baseProperties);
        mergedProperties.putAll(request.profileProperties);

        var dataSourceBean = resolveDataSourceBean(request.customDataSourceBeanPresent, mergedProperties);
        var requestLoggerBean = resolveRequestLoggerBean(request.activeProfile, mergedProperties);
        var auditPublisherBean = resolveAuditPublisherBean(mergedProperties);

        var outcome = "Spring Boot registered the profile-specific beans and respected conditional backoff.";

        return new ResolvedContext(
            request.activeProfile,
            mergedProperties,
            dataSourceBean,
            requestLoggerBean,
            auditPublisherBean,
            outcome
        );
    }

    /**
     * Resolves the data source bean the same way Boot backs off from user-defined beans.
     *
     * <pre>
     * Python equivalent:
     * data_source = custom_ds if custom_ds else factory.create(db_type)
     * </pre>
     *
     * ASCII flow:
     * user bean present? -> yes => custom bean wins
     *                   -> no  => choose default by database.type
     *
     * @param customDataSourceBeanPresent whether the application already provided a bean.
     * @param properties merged environment properties.
     * @return the resolved data source bean name.
     */
    private static String resolveDataSourceBean(boolean customDataSourceBeanPresent, Map<String, String> properties) {
        var databaseType = properties.getOrDefault("database.type", "h2").toLowerCase(Locale.ROOT);

        // Custom beans win before Boot falls back to its defaults.
        if (customDataSourceBeanPresent) {
            return "UserDefinedPrimaryDataSource (@ConditionalOnMissingBean backed off)";
        }

        if ("postgres".equals(databaseType)) {
            return "PostgreSqlDataSource (@ConditionalOnClass matched)";
        }

        if ("mysql".equals(databaseType)) {
            return "MySqlDataSource (@ConditionalOnClass matched)";
        }

        return "H2DataSource (development fallback)";
    }

    /**
     * Resolves the request logger bean based on the active profile and merged properties.
     *
     * <pre>
     * Python equivalent:
     * logger = StructuredLogger if profile == "prod" else ConsoleLogger
     * </pre>
     *
     * ASCII flow:
     * profile or logging.mode -> choose logger implementation
     *
     * @param activeProfile the active Spring profile.
     * @param properties merged environment properties.
     * @return the resolved logger bean name.
     */
    private static String resolveRequestLoggerBean(String activeProfile, Map<String, String> properties) {
        var loggingMode = properties.getOrDefault("logging.mode", "console");
        if ("prod".equalsIgnoreCase(activeProfile) || "structured".equalsIgnoreCase(loggingMode)) {
            return "StructuredRequestLogger (@Profile(\"prod\") / environment specific)";
        }

        return "ConsoleRequestLogger (@Profile(\"dev\") default)";
    }

    /**
     * Resolves the audit publisher bean from the feature flag.
     *
     * <pre>
     * Python equivalent:
     * audit = AuditTrailPublisher() if settings.feature_audit_enabled else NoOpAuditTrailPublisher()
     * </pre>
     *
     * ASCII flow:
     * feature.audit.enabled -> true => real bean / false => no-op bean
     *
     * @param properties merged environment properties.
     * @return the resolved audit publisher bean name.
     */
    private static String resolveAuditPublisherBean(Map<String, String> properties) {
        var auditEnabled = "true".equalsIgnoreCase(properties.getOrDefault("feature.audit.enabled", "false"));
        return auditEnabled
            ? "AuditTrailPublisher (@ConditionalOnProperty matched)"
            : "NoOpAuditTrailPublisher (@ConditionalOnProperty backed off)";
    }

    private static final class StartupRequest {
        private final String activeProfile;
        private final Map<String, String> baseProperties;
        private final Map<String, String> profileProperties;
        private final boolean customDataSourceBeanPresent;

        private StartupRequest(
            String activeProfile,
            Map<String, String> baseProperties,
            Map<String, String> profileProperties,
            boolean customDataSourceBeanPresent
        ) {
            this.activeProfile = activeProfile;
            this.baseProperties = baseProperties;
            this.profileProperties = profileProperties;
            this.customDataSourceBeanPresent = customDataSourceBeanPresent;
        }
    }

    private static final class ResolvedContext {
        private final String activeProfile;
        private final Map<String, String> mergedProperties;
        private final String dataSourceBean;
        private final String requestLoggerBean;
        private final String auditPublisherBean;
        private final String outcome;

        private ResolvedContext(
            String activeProfile,
            Map<String, String> mergedProperties,
            String dataSourceBean,
            String requestLoggerBean,
            String auditPublisherBean,
            String outcome
        ) {
            this.activeProfile = activeProfile;
            this.mergedProperties = mergedProperties;
            this.dataSourceBean = dataSourceBean;
            this.requestLoggerBean = requestLoggerBean;
            this.auditPublisherBean = auditPublisherBean;
            this.outcome = outcome;
        }
    }
}
