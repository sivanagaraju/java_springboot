# Spring Boot Learning Repository — Complete Folder Structure

> **Your Profile:** Python/FastAPI expert | Data Engineering background | 12 years industry | New Java/Spring closure
> **Goal:** Production REST APIs, Microservices, Enterprise Architecture
> **Style:** Learn by doing — every concept has explanation + working Java + ASCII diagrams + exercises

This repository contains a full learning path for Spring Boot.
Start with the [IMPLEMENTATION_PLAN.md](IMPLEMENTATION_PLAN.md) to understand the structure.
Use the [PROGRESS_TRACKER.md](PROGRESS_TRACKER.md) to track your journey.

---

## How To Use This Repo Day-to-Day

### The learning loop for each topic

```
1. READ the README.md of the module (5 min — big picture)
2. READ the explanation/*.md file (10–15 min — concept + diagrams)
3. READ the explanation/*.java file (15 min — annotated code)
4. RUN the Java file (2 min — see it work)
5. READ the exercise README.md (5 min — understand the task)
6. CODE the exercise from scratch (30–60 min)
7. RUN your exercise, compare output to solution
8. CHECK PROGRESS_TRACKER.md — mark complete
```

### Terminal commands you'll use constantly

```bash
# Start a new Spring Boot project (use Spring Initializr web UI, then)
cd my-new-project && mvn spring-boot:run

# Run tests
mvn test

# Run a specific test class
mvn test -Dtest=EmployeeControllerTest

# Package
mvn clean package

# Run the jar directly
java -jar target/myapp-0.0.1-SNAPSHOT.jar

# View all Spring beans loaded
# Add to main: ApplicationContext ctx = SpringApplication.run(...)
# ctx.getBeanDefinitionNames() → print all

# Watch logs in real time (after jar is running)
tail -f logs/application.log
```