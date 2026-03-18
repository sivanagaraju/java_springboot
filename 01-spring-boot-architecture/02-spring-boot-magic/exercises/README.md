# Module 2 Exercises: Spring Boot Magic

These exercises force you to understand how Spring Boot uses external configuration strings structurally to conditionally control runtime execution without compiling new Java code.

## 🛠️ Exercise 1: Application Properties (`Ex01_Properties.java`)
**Goal:** Prove you understand how an IoC Bean can consume external configuration strings perfectly.
**Tasks:**
1. A fake Spring Environment object exists mimicking `application.properties`.
2. Given a mock `StripePaymentService`, modify it to retrieve the `stripe.api.key` and `stripe.timeout.ms`.
3. Provide default values internally in case the properties file is entirely missing that key.

## 🧪 Exercise 2: Understanding Conditions (`Ex02_CustomCondition.java`)
**Goal:** Manually build a miniature version of what Spring Boot does mechanically under the hood.
**Tasks:**
1. You have two classes: `MySQLDatabase` and `InMemH2Database`.
2. Read a generic Map (simulating the `application.properties`) to see if the user declared the property `database.type=mysql`.
3. Write an if/else block (simulating `@ConditionalOnProperty`) to completely instantiate and return the correct database mechanically.
