# 03-Bean-Lifecycle — One-Page Cheat Sheet

## Bean Lifecycle Phases (12-Phase — Must Know)

| # | Phase | Hook Point | Runs When |
|---|---|---|---|
| 1 | `@ComponentScan` discovery | — | Startup |
| 2 | `BeanDefinition` registration | — | Startup |
| 3 | `BeanFactoryPostProcessor` | Modify definitions BEFORE instantiation | Startup |
| 4 | **Constructor** called | Your code | Instantiation |
| 5 | Dependency injection | `@Autowired` fields/setters | After constructor |
| 6 | `BeanPostProcessor.postProcessBeforeInitialization` | Framework code | Before init |
| 7 | **`@PostConstruct`** | Your code | After DI |
| 8 | `InitializingBean.afterPropertiesSet()` | Your code | After PostConstruct |
| 9 | Custom `init-method` | Your code | After afterPropertiesSet |
| 10 | `BeanPostProcessor.postProcessAfterInitialization` | Framework code — **AOP PROXY created here!** | After init |
| 11 | **Bean in use** | Handles requests | Runtime |
| 12 | **`@PreDestroy`** → `DisposableBean.destroy()` | Your code | Shutdown |

## Bean Scopes Quick Reference

| Scope | Instances | Created When | Destroyed When | Use Case |
|---|---|---|---|---|
| `singleton` (default) | 1 per context | Startup (eager) | Context close | Stateless services |
| `prototype` | New each time | On `getBean()` | **NEVER by Spring** ⚠️ | Stateful objects |
| `request` | 1 per HTTP request | Request arrives | Response sent | Request-scoped context |
| `session` | 1 per HTTP session | Session created | Session expires | User session data |
| `application` | 1 per ServletContext | App starts | App stops | App-wide config |

## Lifecycle Callbacks Comparison

```
PREFERRED (modern):          LEGACY (avoid for new code):
───────────────────          ─────────────────────────
@PostConstruct               InitializingBean.afterPropertiesSet()
@PreDestroy                  DisposableBean.destroy()
                             @Bean(initMethod = "init", destroyMethod = "cleanup")
```

## Prototype-in-Singleton Trap (ASCII)

```
SINGLETON OrderService created at startup
  └── PROTOTYPE ShoppingCart injected ONCE
       └── ALL requests share the SAME ShoppingCart instance ❌

FIX 1: Inject ObjectFactory<ShoppingCart>
FIX 2: Inject Provider<ShoppingCart> (JSR-330)
FIX 3: @Lookup method on the singleton
```

## Python → Java Quick Map

| Python | Spring Bean Lifecycle |
|---|---|
| `__init__()` | Constructor + `@PostConstruct` |
| `__del__()` | `@PreDestroy` (but don't rely on `__del__` either!) |
| Context manager `__enter__`/`__exit__` | `@PostConstruct` / `@PreDestroy` |
| Module-level singleton (Python default) | `@Scope("singleton")` (Spring default) |
| `def get_x(): return X()` on each call | `@Scope("prototype")` |
| No equivalent | `BeanPostProcessor` (framework-level hook) |

## 5 Traps to Avoid

1. **Prototype beans are NEVER destroyed by Spring** → you must manage cleanup yourself
2. **`@PostConstruct` runs BEFORE AOP proxies** → `this.method()` calls inside `@PostConstruct` won't be intercepted
3. **Prototype injected into singleton = stale instance** → use `ObjectFactory<T>` or `Provider<T>`
4. **Heavy logic in constructors** → move to `@PostConstruct` (DI is complete by then)
5. **Forgetting `@PreDestroy` for resource cleanup** → DB connections, thread pools, file handles leak
