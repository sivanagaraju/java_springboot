# Component Scanning and Configuration

How does Spring actually know *which* classes to pick up and register into the IoC Container?

## The Component Scan

Spring Boot relies heavily on an automated process called **Component Scanning**. 

When your application boots up, the `@SpringBootApplication` annotation triggers a massive recursive scan originating from the package where your main class lives.

```java
package com.mycompany.app;

@SpringBootApplication
public class EnterpriseApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnterpriseApplication.class, args);
    }
}
```

Because `EnterpriseApplication` is in `com.mycompany.app`, Spring will scan that package and every single sub-package (e.g., `com.mycompany.app.services`, `com.mycompany.app.controllers`). 

**Architect Rule:** If you accidentally create a package *above* or *parallel* to your main application class (e.g., `com.mycompany.external`), Spring will **not** scan it. Your `@Service` classes inside it will be utterly ignored, resulting in `NoSuchBeanDefinitionException` at runtime.

## The Core Stereotypes

Spring provides several semantic variations of `@Component` (often called Stereotypes). Mechanically, they all mean the exact same thing to the IoC container: "Create an instance of me."

1. **`@Component`:** The generic base annotation.
2. **`@Service`:** Used for classes containing core business logic and algorithms.
3. **`@Repository`:** Used for classes connecting directly to a Database. (Spring natively translates raw SQL exceptions into unified Spring Database Exceptions when using this specific annotation).
4. **`@Controller` / `@RestController`:** Used for API endpoints facing the web network.

## `@Configuration` and `@Bean`

What if you need to put a class into the IoC Container, but you don't own the source code (e.g., an AWS S3 Client from a third-party library)? You cannot physically open their code and drop a `@Service` annotation on it.

This is where `@Configuration` enters.

```java
@Configuration
public class CloudConfig {

    // Spring executes this method at startup, takes the returned object, 
    // and registers it permanently into the IoC Map as a Singleton!
    @Bean
    public AmazonS3Client s3Client() {
        return new AmazonS3ClientBuilder()
            .withRegion("us-east-1")
            .build();
    }
}
```

Now, any of your own `@Service` classes can safely demand an `AmazonS3Client` in their constructor, and Spring will know exactly how to provide it.
