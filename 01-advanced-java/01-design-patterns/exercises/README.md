# Design Patterns — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting these exercises.

## Exercises

### Ex01 — Notification Factory (`Ex01_NotificationFactory.java`)
Build a Factory Method system for creating notification channels. Supports runtime registration of new types via a `ConcurrentHashMap`-based registry.

**Skills:** Factory Method, `Supplier<T>`, `ConcurrentHashMap`
**Time estimate:** 25 minutes

### Ex02 — Pizza Builder (`Ex02_PizzaBuilder.java`)
Create an immutable `Pizza` class with a fluent Builder. Validates required fields in `build()`. Includes preset pizzas.

**Skills:** Builder pattern, immutability, validation, fluent API
**Time estimate:** 20 minutes

### Ex03 — Pricing Strategy (`Ex03_PricingStrategy.java`)
Implement a `PricingCalculator` with swappable strategies: regular, bulk discount, seasonal. Lambda strategy for one-off cases.

**Skills:** Strategy, `@FunctionalInterface`, lambdas
**Time estimate:** 20 minutes

## Solutions
See `solutions/` directory. Attempt each exercise before viewing.

## Difficulty Progression
- Ex01: Medium — registry pattern, thread safety
- Ex02: Easy-Medium — Builder structure
- Ex03: Easy — Strategy with functional interface

## Connection to Spring Boot
- Ex01 → `BeanFactory` / `ApplicationContext` bean creation
- Ex02 → `ResponseEntity.ok().header().body()` builder chain
- Ex03 → `AuthenticationProvider` strategy, `Comparator` strategy
