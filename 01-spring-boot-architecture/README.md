# 01 - Spring Boot Architecture

Welcome to the foundational module of the Senior Java Spring Boot learning path. Before you write a single line of REST API code, you must intimately understand the engine that powers the entire framework.

## Why This Matters
Spring Boot often feels like "magic" to beginners. You type an annotation, and suddenly a database connects and a web server boots. However, there is no magic in software—only highly abstracted mechanics. By mastering this module, you will understand exactly how Spring Boot's engine works "under the hood." 

This knowledge marks the critical difference between a Junior who copies and pastes code from StackOverflow, and a Senior who can debug deep systemic architectural crashes safely and deliberately.

## Modules

### `01-inversion-of-control`
The exact mechanical foundation of the Spring Framework.
- Understand the dangers of Tight Coupling (`new` keyword).
- Master the Application Context (IoC Container).
- Learn why **Constructor Injection** is the only enterprise-standard DI method.
- Understand Component Scanning and Bean Lifecycles.

### `02-spring-boot-magic`
How Spring Boot physically automates the IoC Container.
- Master the philosophy of "Convention over Configuration."
- Discover how `@Conditional` statements power Auto-Configuration.
- Understand how curated Maven Starters eliminate "Version Hell."
- Master the use of `application.properties` and profile environments.

## How to Proceed
1. Open the `01-inversion-of-control` directory. Read its `README.md` and proceed sequentially through the explanations and hands-on exercises.
2. Move to `02-spring-boot-magic` and do exactly the same.
