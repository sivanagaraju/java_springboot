# 01-IoC-Container — One-Page Cheat Sheet

## IoC Container Quick Reference

| Concept | What It Does | Python Equivalent |
|---|---|---|
| `ApplicationContext` | Central IoC container — creates, wires, and manages beans | No direct equivalent — Python has no DI container |
| `BeanFactory` | Lazy, lightweight container (parent of ApplicationContext) | — |
| `@SpringBootApplication` | Combines `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration` | `app = FastAPI()` |
| `@Configuration` | Declares a class that defines `@Bean` methods | Module-level factory functions |
| `@Bean` | Registers a method's return value as a Spring-managed bean | `def get_db():` in FastAPI `Depends()` |
| `@ComponentScan` | Auto-discovers `@Component`-annotated classes in packages | No equivalent — Python imports explicitly |
| `@Import` | Pulls in additional `@Configuration` classes | `from module import config` |
| `@PropertySource` | Loads external `.properties` file into Environment | `python-decouple` config loading |

## Container Types Comparison

| Feature | BeanFactory | ApplicationContext |
|---|---|---|
| Bean creation | **Lazy** (on first `getBean()`) | **Eager** (on startup) |
| Event system | ❌ | ✅ `ApplicationEventPublisher` |
| AOP support | ❌ | ✅ Proxy creation at startup |
| i18n | ❌ | ✅ `MessageSource` |
| Environment/Profiles | ❌ | ✅ `@Profile`, `@Value` |
| **Use in production?** | Rarely | **Always** |

## Config Style Evolution (ASCII)

```
2004: XML Config         → 500-line applicationContext.xml
2007: Annotation Config  → @Component + @Autowired (Spring 2.5)
2014: Java Config        → @Configuration + @Bean (Spring 4.0+)
2016: Auto Config        → @SpringBootApplication (just works)

RULE: Use Java Config for 3rd-party libs, annotations for your code
```

## ApplicationContext Setup Patterns

```java
// Pattern 1: Spring Boot (99% of the time)
SpringApplication.run(MyApp.class, args);

// Pattern 2: Standalone Java Config
var ctx = new AnnotationConfigApplicationContext(AppConfig.class);

// Pattern 3: Legacy XML (read-only, don't write new XML)
var ctx = new ClassPathXmlApplicationContext("beans.xml");

// Pattern 4: Web application
// Automatically — Spring Boot creates WebApplicationContext
```

## Python → Java Quick Map

| Python / FastAPI | Spring IoC |
|---|---|
| `app = FastAPI()` | `@SpringBootApplication` main class |
| `from module import service` | `@ComponentScan` auto-discovers |
| `Depends(get_db)` | `@Autowired` / constructor injection |
| No concept | `BeanFactory` (low-level container) |
| `python-decouple` config | `@PropertySource` + `@Value` |
| Manual `import` | Auto-discovery via classpath scanning |

## 5 Traps to Avoid

1. **Calling `new MyService()` instead of letting Spring create it** → bypasses DI, AOP proxies, and lifecycle hooks
2. **Putting `@SpringBootApplication` in root package (`com`)** → scans EVERY class on classpath, startup takes minutes
3. **Using `BeanFactory` when you need events or AOP** → use `ApplicationContext` always
4. **Multiple `@SpringBootApplication` classes in same module** → ambiguous entry point, startup fails
5. **Forgetting that `ApplicationContext` is eager** → all singleton beans created at startup, not on first use
