# Spring Boot Architecture Progressive Quiz Drill

This drill checks whether Spring Boot architecture feels mechanical rather than magical.

```mermaid
flowchart LR
    A[IoC Recall] --> B[Apply the Layering]
    B --> C[Debug Proxy and Boot Behavior]
    C --> D[Production Architecture Scenario]
```

## Round 1 - Core Recall

**Q1.** What problem does IoC solve that manual object creation does not?

**Q2.** Why does Spring Boot auto-configuration use conditions instead of always registering every bean?

**Q3.** What is the difference between a controller and a service, and why does that separation matter?

**Q4.** Why do profiles exist, and what architectural problem do they solve?

## Round 2 - Apply and Compare

**Q5.** You have request validation, business rules, and SQL access in one controller method. How would you split that across Spring layers, and why?

**Q6.** You need one bean in `dev` and a different one in `prod`. Would you use `if` statements inside the service or profile/conditional bean registration? Why?

**Q7.** A team wants to put DTO mapping logic into repositories because "it is faster." Is that a good architectural trade-off? Explain.

## Round 3 - Debug the Bug

**Q8.** What is wrong with this design?

```java
@RestController
public class OrderController {
    @Autowired OrderRepository repo;

    @PostMapping("/orders")
    public void create(@RequestBody OrderRequest request) {
        validate(request);
        repo.save(new Order(request.total()));
        sendEmail();
    }
}
```

**Q9.** Why can this startup configuration behave unexpectedly?

```java
@Bean
@Profile("prod")
PaymentGateway gateway() { ... }
```

The active profile is never set anywhere.

**Q10.** What architectural issue appears when a service directly constructs its own collaborators with `new`?

## Round 4 - Staff-Level Scenario

**Q11.** A platform team has six Spring Boot services, but every service structures packages differently. What shared architectural boundaries would you standardize first?

**Q12.** A production outage shows that one custom bean silently replaced Boot's default bean. What would you review to make those overrides safer and more visible?

---

## Answer Key

### Round 1 - Core Recall

**A1.** IoC moves object creation and wiring into the container, so dependencies are configured consistently and replaced more easily. That improves testability, lifecycle management, and separation of responsibilities.

**A2.** Boot uses conditions so it only registers beans that make sense for the current classpath, properties, and environment. That prevents conflicting or wasteful configuration and lets user-defined beans override defaults.

**A3.** A controller handles transport concerns such as HTTP input/output. A service owns business rules and orchestration. Mixing them makes testing, reuse, and maintenance harder.

**A4.** Profiles let the application vary configuration by environment without scattering conditionals through business code. They are ideal for swapping infrastructure choices like loggers, databases, or integrations.

### Round 2 - Apply and Compare

**A5.** Put HTTP mapping and request parsing in the controller, business rules in a service, and persistence in a repository. That keeps transport, policy, and data-access concerns from bleeding into one another.

**A6.** Use profiles or conditional beans. That keeps the variation at startup and configuration time instead of mixing environment logic into runtime business code.

**A7.** Usually no. Repositories should focus on persistence. Mapping belongs closer to the service boundary unless a very specific projection or performance requirement justifies a narrower repository return type.

### Round 3 - Debug the Bug

**A8.** The controller owns validation, persistence, and side effects all at once. That breaks layering, makes testing awkward, and couples HTTP handling to business workflows that belong in a service.

**A9.** If no active profile is set, the `prod` bean may never register. The application can fall back to another bean or fail at startup depending on the rest of the configuration.

**A10.** The service bypasses the container, which means you lose dependency injection, configuration control, lifecycle management, and easy test substitution.

### Round 4 - Staff-Level Scenario

**A11.** Standardize package boundaries such as controller, service, repository, dto, config, exception, and security. Those boundaries make cross-service onboarding and review much easier.

**A12.** Review conditional annotations, bean names, `@Primary`, profile activation, and startup visibility. The goal is to make bean replacement intentional instead of accidental.
