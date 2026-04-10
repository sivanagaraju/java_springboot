# Component Scanning

## Stereotype Annotations
- @Component — generic Spring bean
- @Service — business logic layer
  - No extra behavior vs @Component
  - Semantic meaning for developers
- @Repository — data access layer
  - Exception translation (SQL → Spring exceptions)
  - Semantic meaning for persistence
- @Controller — web layer
  - Used with @RequestMapping
- @RestController — @Controller + @ResponseBody

## @ComponentScan
- Default — scans from @SpringBootApplication package down
- Custom base packages
  - @ComponentScan("com.learning.service")
  - Multiple packages
- Include filters
  - Scan specific annotations
  - Scan specific types
- Exclude filters
  - Skip test configurations
  - Skip legacy components

## @Configuration + @Bean
- When to use
  - Third-party libraries (can't add @Component)
  - Complex construction logic
  - Conditional creation
- @Bean method
  - Method name = bean name
  - Return type = bean type
  - Can use parameters (auto-injected)
- Full vs Lite mode
  - @Configuration — full CGLIB proxy (inter-bean refs work)
  - @Component with @Bean — lite mode (no proxy)

## Conditional Beans
- @Profile
  - @Profile("dev") vs @Profile("prod")
  - Multiple profiles — @Profile({"dev", "test"})
  - Activate — spring.profiles.active=dev
- @ConditionalOnProperty
  - Bean only if property exists/matches
  - Feature flags
- @ConditionalOnClass
  - Bean only if class on classpath
  - Auto-configuration pattern
- @ConditionalOnMissingBean
  - Default that can be overridden
  - Used heavily in Spring Boot starters
