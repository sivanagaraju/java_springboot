# 01-IoC-Container — Top Resource Guide

External learning resources only. No internal repo links.

## Official Documentation

- **Spring Framework Reference — IoC Container** — Chapters 1-2 (Core Container)
  https://docs.spring.io/spring-framework/reference/core/beans.html

- **Spring Boot Reference — SpringApplication** — Bootstrap and auto-configuration
  https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.spring-application

## Books

- **"Spring in Action, 7th Edition"** by Craig Walls (Manning, 2024)
  — The definitive Spring book. Chapter 1 covers IoC and ApplicationContext.
  — Focus: annotation-based config, component scanning, Java Config

- **"Pro Spring 6"** by Iuliana Cosmina et al. (Apress, 2023)
  — Deep dive into BeanFactory vs ApplicationContext internals.
  — Chapter 3 covers all config styles (XML, annotation, Java)

## Blogs — Highest Signal

- **Baeldung** (baeldung.com) — most comprehensive Spring tutorial site
  - "The Spring ApplicationContext" — full lifecycle walkthrough
  - "Spring @ComponentScan – Filter Types" — include/exclude patterns
  - "Spring Java-based Configuration" — @Configuration deep dive

- **Spring.io** official blog
  - "Understanding Spring Boot Auto-Configuration"
  - "Spring Framework 6.0 — What's New"

## Videos

- **Spring Framework Official YouTube** — "Spring Tips" series by Josh Long
  — Short, practical episodes covering IoC patterns

- **Amigoscode** — "Spring Boot Tutorial" (free, 3 hours)
  — Great for visual learners transitioning from Python

## Concepts Checklist — Before Moving to 02-Dependency-Injection

- [ ] Can explain IoC in one sentence ("Container creates objects, not you")
- [ ] Know why `ApplicationContext` is preferred over `BeanFactory`
- [ ] Understand the 3 config styles and when to use each
- [ ] Can create a bean using `@Configuration` + `@Bean`
- [ ] Know that `@SpringBootApplication` = `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`
- [ ] Understand eager vs lazy bean initialization
- [ ] Can explain the Python → Spring IoC mental model
