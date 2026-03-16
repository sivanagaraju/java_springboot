# Module 1: Inversion of Control & Application Architecture

## Overview
This module explores the absolute foundational architectural constraints of Spring Boot: the **IoC Container**. 
Before writing any REST APIs or connecting to Databases, you must understand how Spring instantiates, wires, and manages your Java objects automatically in the background. Understanding IoC separates "framework users" from "framework engineers."

## Core Concepts
- **Tight Coupling:** The problem of classes calling `new` on their dependencies, making testing impossible and architectures brittle.
- **Inversion of Control (IoC):** The principle that dependencies should be structurally provided from the outside.
- **The Application Context:** Spring's massive internal registry mapping your Beans conceptually into a global dictionary.
- **Dependency Injection (DI):** The mechanism Spring uses to populate dependencies. We strictly enforce **Constructor Injection** and explicitly prohibit Field Injection.
- **Component Scanning:** The recursive automated process Spring uses to find `@Service`, `@Component`, `@Repository`, and `@RestController` annotations at boot up.
- **Bean Scopes:** Understanding that Singletons are mathematically stateless, and why introducing state to them is fatal in multi-threaded environments.

## Directory Structure
- `explanation/`: Deep dives into IoC concepts, DI rules, scoping, and mechanical Spring mechanics.
- `exercises/`: Code challenges explicitly guiding you to refactor poorly coupled code into strongly bound architecturally pure IoC classes.
