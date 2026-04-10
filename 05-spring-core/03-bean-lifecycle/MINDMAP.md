# Bean Lifecycle

## Creation Flow (12 Phases)
- Phase 1 — @ComponentScan finds class
- Phase 2 — BeanDefinition registered
- Phase 3 — BeanFactoryPostProcessor modifies definitions
- Phase 4 — Constructor called (instantiation)
- Phase 5 — Dependencies injected
- Phase 6 — BeanPostProcessor.postProcessBeforeInitialization()
- Phase 7 — @PostConstruct
- Phase 8 — InitializingBean.afterPropertiesSet()
- Phase 9 — Custom init-method
- Phase 10 — BeanPostProcessor.postProcessAfterInitialization()
  - AOP proxy created HERE
- Phase 11 — Bean READY for use
- Phase 12 — @PreDestroy on shutdown

## Bean Scopes
- Singleton (default)
  - One instance per ApplicationContext
  - Shared across all injection points
  - Created at startup (eager)
  - Python equivalent — module-level singleton
- Prototype
  - New instance EVERY time requested
  - Not managed after creation (no @PreDestroy)
  - Use for stateful beans
- Request (web only)
  - One per HTTP request
  - Python equivalent — FastAPI Depends(get_db)
- Session (web only)
  - One per HTTP session
  - User-specific state

## Lifecycle Callbacks
- @PostConstruct
  - Runs after dependency injection
  - Initialize resources, validate config
  - Python equivalent — `__post_init__` in dataclass
- @PreDestroy
  - Runs before bean destruction
  - Close resources, flush caches
  - Python equivalent — `__del__` or context manager
- InitializingBean interface
  - afterPropertiesSet() method
  - Spring-specific (couples to framework)
- Custom init/destroy
  - @Bean(initMethod="init", destroyMethod="cleanup")
  - Framework-agnostic
- BeanPostProcessor
  - Custom processing for ALL beans
  - How Spring creates AOP proxies
  - How @Async works under the hood
