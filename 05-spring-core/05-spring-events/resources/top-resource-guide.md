# 05-Spring-Events — Top Resource Guide

External learning resources only. No internal repo links.

## Official Documentation

- **Spring Framework Reference — Application Events**
  https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events

- **Spring Framework Reference — @EventListener and @TransactionalEventListener**
  https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html

- **Spring Boot Reference — Application Events and Listeners**
  https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.spring-application.application-events-and-listeners

## Books

- **"Spring in Action, 7th Edition"** by Craig Walls (Manning, 2024)
  — Section on application events and the observer pattern in Spring.

- **"Cloud Native Spring in Action"** by Thomas Vitale (Manning, 2022)
  — Chapter on event-driven patterns with Spring — bridges to messaging systems.
  — Best for understanding when to go from in-process events to Kafka/RabbitMQ.

- **"Implementing Domain-Driven Design"** by Vaughn Vernon (Addison-Wesley, 2013)
  — Chapter 8 covers Domain Events — the conceptual foundation for Spring Events.

## Blogs — Highest Signal

- **Baeldung** (baeldung.com)
  - "Spring Events" — comprehensive tutorial with sync/async examples
  - "Spring @EventListener Annotation" — modern annotation-based approach
  - "Transaction-Bound Events in Spring" — @TransactionalEventListener deep dive

- **Reflectoring** (reflectoring.io)
  - "Spring Application Events" — clean architecture perspective on events

- **Spring.io** official blog
  - "Better Application Events in Spring Framework 4.2"

## Videos

- **Spring I/O Conference** — "Event-driven Spring" talks (multiple years)
  — Conference-grade talks on production event patterns

- **Java Brains** YouTube — "Spring Events" video in Spring playlist
  — Practical walkthrough of custom events

## Concepts Checklist — Before Moving to 06-Spring-Boot-Fundamentals

- [ ] Can create a custom event using a Java record
- [ ] Know how to publish events with `ApplicationEventPublisher`
- [ ] Understand sync vs async event listeners
- [ ] Can explain `@TransactionalEventListener` and its 4 phases
- [ ] Know the built-in Spring Boot lifecycle events (especially ApplicationReadyEvent)
- [ ] Understand the loose coupling benefit — publisher doesn't know listeners
- [ ] Can design an event chain where listeners publish new events
- [ ] Know the async exception handling trap and how to fix it
