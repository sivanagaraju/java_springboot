# 03-Bean-Lifecycle — Top Resource Guide

External learning resources only. No internal repo links.

## Official Documentation

- **Spring Framework Reference — Bean Lifecycle Callbacks**
  https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html

- **Spring Framework Reference — Bean Scopes**
  https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html

- **Spring Framework Reference — Container Extension Points** (BPP, BFPP)
  https://docs.spring.io/spring-framework/reference/core/beans/factory-extension.html

## Books

- **"Spring in Action, 7th Edition"** by Craig Walls (Manning, 2024)
  — Chapter 1 Section 1.3 covers bean lifecycle and scopes.
  — Clear diagrams of the initialization sequence.

- **"Pro Spring 6"** by Iuliana Cosmina et al. (Apress, 2023)
  — Chapter 4 is THE deep dive — covers all 12 phases with code examples.
  — Best coverage of BeanPostProcessor internals.

## Blogs — Highest Signal

- **Baeldung** (baeldung.com)
  - "Spring Bean Lifecycle" — comprehensive walkthrough with diagrams
  - "Spring Bean Scopes" — prototype vs singleton with examples
  - "The Spring @PostConstruct and @PreDestroy Annotations"

- **Vlad Mihalcea** (vladmihalcea.com)
  - "Spring Bean Lifecycle Callback Methods" — with Hibernate context

## Videos

- **Java Brains** YouTube — "Spring Bean Lifecycle" (video 12-15 in Spring playlist)
  — Excellent visual explanation of the 12-phase lifecycle

- **Amigoscode** — "Spring Bean Scopes Explained"
  — Hands-on demo of singleton vs prototype with real examples

## Concepts Checklist — Before Moving to 04-Component-Scanning

- [ ] Can draw the 12-phase bean lifecycle from memory
- [ ] Know the callback execution order: constructor → @PostConstruct → afterPropertiesSet → init-method
- [ ] Understand why prototype beans skip @PreDestroy
- [ ] Can explain the prototype-in-singleton trap and 3 fixes
- [ ] Know that AOP proxies are created at Phase 10 (after @PostConstruct)
- [ ] Understand BeanPostProcessor vs BeanFactoryPostProcessor
- [ ] Can implement a custom BeanPostProcessor
