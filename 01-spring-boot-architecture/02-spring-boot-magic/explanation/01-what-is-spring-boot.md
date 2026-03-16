# What exactly is Spring Boot?

If you understand the Spring IoC Container and Dependency Injection, you know what **Spring Framework** is. 
So what is **Spring Boot**?

## The Problem with Vanilla Spring
In the early 2010s, building a Spring application was a nightmare of XML configuration.
To set up a simple web application connecting to a database, you had to manually construct hundreds of lines of XML just to tell Spring:
1. "Here is where the Tomcat Web Server is."
2. "Here is the Database Driver."
3. "Here is the Hibernate ORM connection pool."
4. "Wire them all together in exactly this specific order."

Developers spent 3 days configuring servers and 1 hour writing actual business logic. It was notoriously called "XML Hell."

## The Spring Boot Revolution
Spring Boot was entirely engineered to eliminate configuration. It is an extension of the Spring Framework explicitly designed with the philosophy of **"Convention over Configuration."**

Spring Boot's main goals:
1. Provide radically faster and radically accessible "getting started" experiences for all Spring development.
2. Be highly opinionated out of the box (it assumes you want to do things the normal way).
3. Provide a massive range of non-functional features that are absolutely required for large classes of enterprise projects (embedded servers, security, metrics, health checks, externalized configuration).
4. Absolutely **zero** XML configuration natively.

## How does it do it?
Spring Boot achieves this via two massive architectural mechanisms:
1. **Starters:** Pre-packaged dependency management.
2. **Auto-Configuration:** Mechanical intelligent guessing over what your application needs based on the libraries you currently have on your Classpath.
