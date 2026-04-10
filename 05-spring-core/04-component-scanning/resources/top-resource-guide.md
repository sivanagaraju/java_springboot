# 04-Component-Scanning — Top Resource Guide

External learning resources only. No internal repo links.

## Official Documentation

- **Spring Framework Reference — Classpath Scanning and Managed Components**
  https://docs.spring.io/spring-framework/reference/core/beans/classpath-scanning.html

- **Spring Framework Reference — Java-based Container Configuration**
  https://docs.spring.io/spring-framework/reference/core/beans/java.html

- **Spring Boot Reference — Auto-configuration**
  https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.auto-configuration

## Books

- **"Spring in Action, 7th Edition"** by Craig Walls (Manning, 2024)
  — Chapter 1 Section 1.2 covers @Component, @Configuration, and scanning.
  — Best explanation of Full vs Lite mode.

- **"Spring Boot in Practice"** by Somnath Musib (Manning, 2022)
  — Chapter 2 covers auto-configuration internals.
  — Great for understanding @Conditional annotations.

## Blogs — Highest Signal

- **Baeldung** (baeldung.com)
  - "Spring Component Scanning" — comprehensive include/exclude filter guide
  - "Spring Profiles" — @Profile with YAML multi-document config
  - "Spring @Configuration vs @Component" — Full vs Lite mode explained

- **Spring.io** official blog
  - "Creating Your Own Auto-configuration" — essential for library authors

## Videos

- **Dan Vega** YouTube — "Spring Boot Component Scanning Deep Dive"
  — Excellent visual walkthrough of scanning behavior

- **Amigoscode** — "Spring Profiles and Conditional Beans"
  — Hands-on demo of profile-based configuration

## Concepts Checklist — Before Moving to 05-Spring-Events

- [ ] Know the 5 stereotype annotations and their semantic meaning
- [ ] Understand which packages `@ComponentScan` scans by default
- [ ] Can explain Full vs Lite mode for `@Configuration`
- [ ] Know how to create custom stereotype annotations
- [ ] Understand `@Profile` and when beans are activated
- [ ] Can use `@ConditionalOnProperty` for feature flags
- [ ] Know that `@Repository` adds persistence exception translation
