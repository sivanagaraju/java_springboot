# IoC Container

## What is IoC
- Inversion of Control principle
  - Traditional: your code controls object creation
  - IoC: framework controls object creation
  - "Hollywood Principle" — don't call us, we'll call you
- Problem it solves
  - `new ServiceA(new ServiceB(new RepoC()))` — rigid coupling
  - Cannot swap implementations for testing
  - Cannot change behavior without recompiling
- Python comparison
  - Python: `service = MyService(MyRepo())` — manual wiring
  - FastAPI: `Depends(get_db)` — closest to IoC, but per-request only
  - Spring: container wires EVERYTHING at startup

## BeanFactory vs ApplicationContext
- BeanFactory
  - Lazy initialization
  - Minimal features
  - Low memory footprint
  - Rarely used directly
- ApplicationContext
  - Eager initialization (all singletons at startup)
  - Event publishing
  - Internationalization (i18n)
  - AOP integration
  - Always use this in practice

## Configuration Styles
- XML Configuration (legacy)
  - `<bean id="..." class="...">`
  - No compile-time safety
  - No IDE refactoring support
- Annotation Configuration (modern)
  - @Component, @Service, @Repository
  - @Autowired for injection
  - Type-safe, IDE-friendly
- Java Configuration (recommended)
  - @Configuration class
  - @Bean methods
  - Full control + type safety
  - Best for third-party libraries
