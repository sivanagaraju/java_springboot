# 04-Component-Scanning — One-Page Cheat Sheet

## Stereotype Annotations Quick Reference

| Annotation | Layer | Purpose | Python Equivalent |
|---|---|---|---|
| `@Component` | Generic | Base annotation — any Spring-managed bean | No equivalent |
| `@Service` | Business | Business logic layer | `class UserService:` |
| `@Repository` | Data | Data access layer + exception translation | SQLAlchemy `Session` wrapper |
| `@Controller` | Web | Spring MVC controller (returns views) | Flask `@app.route` with templates |
| `@RestController` | Web API | `@Controller` + `@ResponseBody` | FastAPI `@app.get("/")` |
| `@Configuration` | Config | Declares `@Bean` factory methods | Module with factory functions |

## Component Scanning Rules (ASCII)

```
@SpringBootApplication in com.myapp
     │
     ▼ scans
com.myapp.**          ← ✅ ALL sub-packages scanned
com.myapp.service     ← ✅ found
com.myapp.repo        ← ✅ found
com.other.service     ← ❌ NOT scanned (different root)

FIX: @ComponentScan(basePackages = {"com.myapp", "com.other"})
```

## @Configuration — Full vs Lite Mode

| Mode | When | Bean references | Inter-bean calls |
|---|---|---|---|
| **Full** (default) | `@Configuration` on class | CGLIB proxied | `beanA()` returns SAME singleton |
| **Lite** | `@Component` with `@Bean` methods | NOT proxied | `beanA()` creates NEW instance each call |

```java
@Configuration  // FULL mode — calling dataSource() always returns same instance
public class AppConfig {
    @Bean public DataSource dataSource() { return new HikariDataSource(); }
    @Bean public JdbcTemplate jdbcTemplate() { return new JdbcTemplate(dataSource()); }
    // dataSource() ↑ returns the SAME bean — CGLIB proxy intercepts the call
}
```

## Conditional Bean Annotations

| Annotation | Condition | Example |
|---|---|---|
| `@Profile("dev")` | Active only in profile | Dev-only mock services |
| `@ConditionalOnProperty` | Config property exists/matches | Feature flags |
| `@ConditionalOnClass` | Class on classpath | Auto-config for libraries |
| `@ConditionalOnMissingBean` | No existing bean of type | Default fallback beans |
| `@ConditionalOnBean` | Bean already exists | Dependent configuration |

## Python → Java Quick Map

| Python | Spring Component Scanning |
|---|---|
| `from services import UserService` | `@ComponentScan` auto-discovers `@Service UserService` |
| `if env == "test": use MockService` | `@Profile("test") @Service MockService` |
| `if feature_flag: enable_analytics()` | `@ConditionalOnProperty("analytics.enabled")` |
| Class decorator `@register` | `@Component` / `@Service` stereotype |
| No equivalent | `@Configuration` + CGLIB proxy for singleton guarantee |

## 5 Traps to Avoid

1. **Service outside scanned packages** → silently ignored, no error thrown — just "bean not found" later
2. **`@Component` with `@Bean` methods (Lite mode)** → inter-bean method calls create NEW instances, not singletons
3. **`@ComponentScan` with overly broad packages** → scans third-party JARs, startup takes minutes
4. **Forgetting `@Profile` on test mocks** → mock beans active in production!
5. **Custom annotation without `@Component` meta-annotation** → not auto-discovered by scanning
