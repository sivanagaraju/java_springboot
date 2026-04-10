# Spring Core

## IoC Container
- What is IoC
  - Inversion of Control principle
  - Hollywood Principle — "Don't call us, we'll call you"
  - Framework controls object creation
- BeanFactory vs ApplicationContext
  - BeanFactory — lazy, minimal
  - ApplicationContext — eager, full-featured
  - Always use ApplicationContext
- XML vs Annotation Config
  - XML — legacy, no IDE support
  - Annotations — modern, type-safe
  - Java Config — @Configuration + @Bean

## Dependency Injection
- Constructor Injection (PREFERRED)
  - Immutable dependencies
  - Fail-fast on missing beans
  - Easy to test (just pass mocks)
- Setter Injection
  - Optional dependencies
  - Mutable — can be changed after init
- Field Injection (AVOID)
  - Cannot test without reflection
  - Hides dependencies
  - Spring-coupled code
- @Qualifier
  - Choose between multiple implementations
  - Named beans
- @Primary
  - Default when multiple candidates exist
  - Overridden by @Qualifier

## Bean Lifecycle
- Creation Flow
  - @ComponentScan → find → register
  - BeanFactoryPostProcessor
  - Constructor (instantiation)
  - BeanPostProcessor.before
  - @PostConstruct
  - afterPropertiesSet()
  - Custom init-method
  - BeanPostProcessor.after (AOP proxy created here)
- Scopes
  - Singleton (default) — one per context
  - Prototype — new instance every injection
  - Request — one per HTTP request
  - Session — one per HTTP session
- Destruction
  - @PreDestroy
  - DisposableBean.destroy()
  - Custom destroy-method

## Component Scanning
- Stereotype Annotations
  - @Component — generic
  - @Service — business logic
  - @Repository — data access + exception translation
  - @Controller — web layer
- @ComponentScan
  - Base packages
  - Include/Exclude filters
- @Configuration + @Bean
  - Third-party library integration
  - Complex object creation
  - Conditional creation
- @Conditional
  - @Profile — env-based
  - @ConditionalOnProperty
  - @ConditionalOnClass

## Spring Events
- Built-in Events
  - ContextRefreshedEvent
  - ApplicationReadyEvent
  - ContextClosedEvent
- Custom Events
  - Extend ApplicationEvent (legacy)
  - Any POJO (Spring 4.2+)
  - @EventListener annotation
- Async Events
  - @Async + @EventListener
  - @EnableAsync
  - Thread pool configuration
- Event-Driven Architecture
  - Loose coupling
  - Observer pattern
  - Python equivalent — blinker signals
