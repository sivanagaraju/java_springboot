# 04-Component-Scanning — Progressive Quiz Drill

## Round 1 — Core Recall (8 questions)
Answer from memory, then check the key.

**Q1:** What is the difference between `@Component`, `@Service`, `@Repository`, and `@Controller`?
**Q2:** What package does `@ComponentScan` scan by default with `@SpringBootApplication`?
**Q3:** You have `@Configuration` class with two `@Bean` methods. The second calls the first. Does it return the same instance or a new one?
**Q4:** What is the difference between Full mode and Lite mode in `@Configuration`?
**Q5:** How do you make a bean only active in the "test" profile?
**Q6:** What does `@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")` do?
**Q7:** Can you create your own custom stereotype annotation? How?
**Q8:** What is `@Repository`'s special behavior besides being a stereotype?

### Answer Key — Round 1
A1: Functionally identical at the DI level — all are `@Component` specializations. Semantic differences: `@Service` = business logic, `@Repository` = data access (adds exception translation), `@Controller` = web MVC (returns views), `@RestController` = REST API (`@Controller` + `@ResponseBody`). Spring may add layer-specific behavior.
A2: The PACKAGE of the class annotated with `@SpringBootApplication`, plus ALL sub-packages. E.g., `com.myapp.App` → scans `com.myapp.**`.
A3: The SAME instance — `@Configuration` uses CGLIB proxy (Full mode) to intercept inter-bean method calls and return the cached singleton.
A4: Full mode: `@Configuration` class, CGLIB proxied, inter-bean calls return same singleton. Lite mode: `@Component` class with `@Bean` methods, NOT proxied, inter-bean calls create new instances every time.
A5: `@Profile("test")` on the class or `@Bean` method. Spring only instantiates it when `spring.profiles.active=test`.
A6: The bean is only created if the property `feature.enabled` exists in configuration AND equals `"true"`. If the property is missing or has a different value, the bean is skipped entirely.
A7: Yes — create an annotation meta-annotated with `@Component` (or `@Service`, etc.):
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service  // meta-annotation
public @interface TrackedService {}
```
A8: `@Repository` enables **Spring's persistence exception translation** — it wraps vendor-specific exceptions (e.g., `HibernateException`, `SQLException`) into Spring's `DataAccessException` hierarchy. This is the only stereotype with built-in behavior.

---

## Round 2 — Apply & Compare (3 questions)

**Q1:** Translate this Python pattern to Spring component scanning:
```python
# services/__init__.py — explicit registration
from .user_service import UserService
from .order_service import OrderService
from .payment_service import PaymentService

# main.py
from services import UserService, OrderService, PaymentService
```

**Q2:** In Python you use environment variables for feature flags: `if os.getenv("ANALYTICS_ENABLED"): analytics.start()`. What's the Spring equivalent?

**Q3:** Python has no concept of "Lite mode" vs "Full mode" for config. Why does Java need this distinction?

### Answer Key — Round 2
A1: In Spring, NO explicit registration needed:
```java
// Just annotate each class — Spring auto-discovers them
@Service public class UserService { }
@Service public class OrderService { }
@Service public class PaymentService { }

// @SpringBootApplication in com.myapp → scans com.myapp.** automatically
// No __init__.py, no imports, no registration
```

A2:
```java
@Service
@ConditionalOnProperty(name = "analytics.enabled", havingValue = "true")
public class AnalyticsService {
    @PostConstruct
    public void start() { System.out.println("Analytics started"); }
}
```
In `application.yml`: `analytics.enabled: true`. The bean only exists when the flag is "true".

A3: In Python, calling a function always creates what the function returns — there's no concept of caching return values. In Java/Spring, `@Configuration` + CGLIB ensures that calling `dataSource()` multiple times within the same config class returns the SAME singleton instance. Without CGLIB (Lite mode), each call creates a NEW object — breaking the singleton guarantee. Python doesn't need this because Python services are typically module-level singletons by convention.

---

## Round 3 — Debug the Bug (3 scenarios)

**Scenario 1:** Your `@Service` class in `com.myapp.external.services` isn't being auto-discovered. Your `@SpringBootApplication` is in `com.myapp.core`. No errors at startup — but when another bean tries to inject it, you get `NoSuchBeanDefinitionException`. Why?

**Scenario 2:** You use `@Component` on a config class with `@Bean` methods. Two different beans inject the same dependency, but they get DIFFERENT instances. Your colleague using `@Configuration` doesn't have this problem. Why?

**Scenario 3:** You add `@Profile("prod")` to your production `DataSource` bean but forget to set the active profile. Now your app starts but crashes when accessing the database with `NoSuchBeanDefinitionException: DataSource`. Why isn't the default profile used?

### Answer Key — Round 3
S1: `com.myapp.external` is NOT a sub-package of `com.myapp.core`. Component scanning only goes DOWN from the `@SpringBootApplication` package. Fix: move `@SpringBootApplication` to `com.myapp`, or add `@ComponentScan(basePackages = {"com.myapp.core", "com.myapp.external"})`.

S2: `@Component` with `@Bean` = Lite mode. No CGLIB proxy. When bean methods call other bean methods, they execute normally (creating new instances). `@Configuration` with `@Bean` = Full mode. CGLIB proxy intercepts calls and returns cached singletons. Fix: change `@Component` to `@Configuration`.

S3: `@Profile("prod")` means the bean ONLY exists when the "prod" profile is active. No profile set = default profile, which is NOT "prod". The DataSource bean is never created. Fix: either (a) set `spring.profiles.active=prod`, or (b) create a fallback `@Bean @Profile("default")` DataSource, or (c) remove `@Profile` and use `@ConditionalOnProperty` instead for environment-specific behavior.

---

## Round 4 — Staff-Level Scenarios (2 questions)

**Q1 — Multi-Module Scanning:**
Your monolith has 5 Gradle sub-modules. Each module has services in different packages. How do you configure component scanning so the main app discovers beans from all modules without hardcoding every package?

**Q2 — Custom Auto-Configuration:**
You're building a shared library (used by 10 microservices) that provides a `MetricsCollector` bean. Teams should be able to override it with their own implementation. Design the auto-configuration class with proper conditionals.

### Answer Key — Round 4

A1: Convention: All modules use a shared root package (e.g., `com.company.app`). `@SpringBootApplication` in `com.company.app.Main` scans all sub-packages across modules. If packages differ, create a marker interface in a shared module:
```java
// shared-module
public interface ModuleMarker {}

// main app
@SpringBootApplication
@ComponentScan(basePackageClasses = ModuleMarker.class)
```
`basePackageClasses` is type-safe — refactoring-friendly.

A2:
```java
@AutoConfiguration
@ConditionalOnClass(MetricsCollector.class)
public class MetricsAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean(MetricsCollector.class) // teams can override
    public MetricsCollector defaultMetricsCollector() {
        return new DefaultMetricsCollector();
    }
}
```
Register in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. The `@ConditionalOnMissingBean` ensures: if a team defines their own `MetricsCollector`, the default is skipped.
