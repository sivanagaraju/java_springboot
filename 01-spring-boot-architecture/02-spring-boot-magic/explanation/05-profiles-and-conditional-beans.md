# 05 - Profiles and Conditional Beans

Spring Boot becomes truly powerful when startup is not treated as a fixed script. Profiles, conditional beans, and auto-configuration work together so one codebase can behave differently in dev, test, staging, and production without branching inside the business logic.

## Why This Exists
If every environment required a separate code path, the application would drift quickly. Spring Boot solves that by making startup decisions declarative: load a profile, merge the matching properties, evaluate conditions, and register only the beans that make sense for the current environment.

## Python Bridge

| Concept | Python / FastAPI | Spring Boot |
|---|---|---|
| Environment settings | `pydantic-settings` or `.env` files | `application.properties` and `application-dev.properties` |
| Environment switch | `if settings.env == "prod"` | `@Profile("prod")` |
| Optional dependency | `if cache is None: cache = InMemoryCache()` | `@ConditionalOnMissingBean` |
| Feature toggle | `if settings.feature_x_enabled:` | `@ConditionalOnProperty` |
| Startup wiring | `Depends(...)` and factory functions | `ApplicationContext` and auto-configuration |

Python usually expresses the decision in explicit startup code or settings objects. Spring Boot moves the same decision into the container so the startup policy is declarative and easier to override.

## Startup Decision Tree

```mermaid
flowchart TD
    A[SpringApplication.run()] --> B[Load base application.properties]
    B --> C[Apply profile-specific overlay]
    C --> D[Evaluate @ConditionalOnClass]
    D --> E[Evaluate @ConditionalOnProperty]
    E --> F[Evaluate @ConditionalOnMissingBean]
    F --> G[Register beans in ApplicationContext]
    G --> H[Application ready]

    C --> I[application-dev.properties]
    C --> J[application-prod.properties]
    E --> K[Feature bean enabled]
    E --> L[Feature bean skipped]
    F --> M[Boot default bean created]
    F --> N[User bean wins and Boot backs off]
```

## Working Model in Java

The runnable simulation lives in [ConditionalDemo.java](./ConditionalDemo.java). The core logic is simple:

```java
var merged = new LinkedHashMap<>(baseProperties);
merged.putAll(profileProperties);

var dataSourceBean = customDataSourceBeanPresent
    ? "UserDefinedPrimaryDataSource"
    : switch (merged.get("database.type")) {
        case "postgres" -> "PostgreSqlDataSource";
        default -> "H2DataSource";
    };
```

That small block captures the same idea Spring Boot uses at scale: merge environment-specific settings, check whether the user already supplied a bean, then either create a default or back off.

## Real-World Use Cases

- Local development uses H2 while production uses PostgreSQL, with no code branches in controllers or services.
- A company ships a default cache bean in a starter, but teams can override it with Redis by defining their own bean.
- Feature flags enable or disable background jobs, audit publishers, or scheduled tasks without changing the business code.

## Anti-Patterns and Fixes

- Hardcoding `if ("prod".equals(profile))` in a service class. The fix is to move the decision to `@Profile` or `@ConditionalOnProperty`.
- Creating duplicate beans without a backoff rule. The fix is to use `@ConditionalOnMissingBean` so Boot respects user-defined overrides.
- Treating profiles like business logic. The fix is to keep profiles for environment concerns and keep domain rules in the service layer.

## Interview Questions

### Conceptual
**Q: Why does Spring Boot use conditional bean registration instead of creating every possible bean up front?**
> **A:** Because many beans are environment-specific or mutually exclusive. Boot creates only the beans that match the current startup context, which reduces conflicts and makes custom overrides possible.

**Q: What problem do profiles solve that plain properties do not?**
> **A:** Profiles let the same application load different configuration overlays for different environments. That makes it easy to change infrastructure without changing code.

### Scenario / Debug
**Q: A production service unexpectedly uses H2 instead of PostgreSQL. What do you check first?**
> **A:** Check the active profile, the profile-specific property file, and whether a user-defined `DataSource` bean is preventing auto-configuration from creating the expected bean.

### Quick Fire
**Q: What is the practical purpose of `@ConditionalOnMissingBean`?**
> **A:** It lets Spring Boot provide a safe default while still backing off when the application defines its own implementation.

**Q: Where should environment-specific behavior live?**
> **A:** In profiles, conditional configuration, and externalized properties, not inside core business methods.
