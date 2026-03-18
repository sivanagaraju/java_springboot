# 02 - Spring Boot Magic (Starters & Auto-Configuration)

Welcome to the second module. Now that you understand how the underlying Spring Framework IoC Container works, it's time to learn how **Spring Boot** automates it.

> **Python Bridge:** Spring Boot is to the Spring Framework what Django is to raw Python web libraries: an extremely opinionated framework designed to get an enterprise app running in 5 seconds flat with zero configuration mapping. 

## 🎯 Why This Module Matters
Vanilla Spring forces you to manually configure hundreds of lines of XML or Java `@Configuration` beans to wire Database connection pools, JSON parsers, and embedded Tomcat Web Servers. This was notoriously known as "XML Hell." 

Spring Boot eliminates this boilerplate using two core architectural mechanisms: **Starters** and **Auto-Configuration**. By mastering these, you will understand how Boot "magically" sets up your application safely and correctly.

## 📚 What You Will Learn
1. **Spring Boot Goals:** Why it exists (Convention over Configuration).
2. **Auto-Configuration:** How Spring Boot uses `@Conditional` logic to dynamically guess what infrastructure dependencies to instantiate based on your classpath.
3. **Starters:** How Maven/Gradle "starter" dependencies solve library version conflicts ("Dependency Hell") permanently.
4. **Application Properties:** How to inject custom variables using `@Value` and override Boot's embedded defaults using `application.properties`.

## 📂 Directory Structure
- `/explanation`: Start here. Contains conceptual breakdowns, Mermaid charts, and Interview QA. Read 01 through 04.
- `/exercises`: Hands-on coding exercises. Test your knowledge by overriding Auto-Configuration and wiring custom properties cleanly.

## 🚀 How to Proceed
Start by deeply reading the `/explanation` files sequentially. Review the Java Demo files. Finally, complete the tasks inside `/exercises/README.md`.
