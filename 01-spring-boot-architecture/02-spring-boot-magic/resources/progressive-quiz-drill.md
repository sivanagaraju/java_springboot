# Spring Boot Magic Progressive Quiz Drill

This drill checks whether Boot feels predictable: classpath, properties, profiles, and conditional beans.

```mermaid
flowchart LR
    A[Core Recall] --> B[Apply the Decision Rules]
    B --> C[Debug the Startup Bug]
    C --> D[Staff-Level Scenario]
```

## Round 1 - Core Recall

**Q1.** Why did Spring Boot add auto-configuration instead of making every project wire infrastructure beans manually?

**Q2.** Why do profiles exist, and why is that better than scattering `if` statements across services?

**Q3.** What problem does `@ConditionalOnMissingBean` solve in the Boot startup model?

**Q4.** Why is the conditions report important when a bean does not appear?

## Round 2 - Apply and Compare

**Q5.** You need one `DataSource` in dev and another in prod. Would you solve that in the service layer or with profiles and configuration? Why?

**Q6.** A library auto-configures a bean, but your app needs a custom version. What should happen at startup, and why?

**Q7.** You are choosing between `@Profile`, `@ConditionalOnProperty`, and `@ConditionalOnMissingBean`. Which problem does each one solve best?

## Round 3 - Debug the Bug

**Q8.** What is wrong with this design?

```java
@Service
public class ReportService {
    @Autowired private ReportRepository repo;
}
```

**Q9.** Why might this auto-configuration not behave the way the team expects?

```java
@Bean
public ObjectMapper mapper() {
    return new ObjectMapper();
}
```

The team also uses Boot starters that already configure JSON support.

**Q10.** Why is this profile setup risky?

```java
@Profile("prod")
@Bean
PaymentGateway gateway() { ... }
```

No profile is set in deployment, and the app is expected to start with the prod gateway.

## Round 4 - Staff-Level Scenario

**Q11.** A platform team has dozens of Boot apps and one app suddenly starts using a custom bean instead of the default one. What startup questions would you ask first?

**Q12.** A production incident happened because the app worked locally, but a missing property disabled a bean in staging. What should the team standardize to make that safer?

---

## Answer Key

### Round 1 - Core Recall

**A1.** Boot reduces startup friction by wiring common infrastructure automatically when the classpath and properties show that the app needs it. That lets teams move faster without abandoning deterministic configuration.

**A2.** Profiles let the app choose environment-specific configuration at startup. That keeps environment variation outside business code and makes the active runtime mode easier to reason about.

**A3.** `@ConditionalOnMissingBean` lets Boot back off when the application already provides its own bean. That prevents duplicate infrastructure beans and makes overrides intentional.

**A4.** The conditions report tells you why a bean was or was not created. It is one of the fastest ways to debug Boot startup behavior.

### Round 2 - Apply and Compare

**A5.** Use profiles and configuration. Environment wiring belongs at startup, not inside business logic.

**A6.** The custom bean should win, and Boot should back off cleanly. That keeps the application override explicit instead of having two competing beans.

**A7.** `@Profile` switches whole configurations by environment, `@ConditionalOnProperty` toggles behavior from external settings, and `@ConditionalOnMissingBean` lets app beans override auto-configured defaults.

### Round 3 - Debug the Bug

**A8.** Field injection hides dependencies and makes testing harder. Constructor injection is the preferred Boot/Spring style because it makes the dependency graph explicit and immutable.

**A9.** Boot starters may already create a JSON mapper with sensible defaults. Defining another bean can unintentionally replace that behavior or create conflicting configuration.

**A10.** If no active profile is set, the bean may never load. The app can fail at startup or fall back to another bean, depending on the rest of the configuration.

### Round 4 - Staff-Level Scenario

**A11.** Ask which beans are conditional, whether a property changed, whether a custom bean was added, whether a profile changed, and whether the conditions report shows a backoff or an accidental override.

**A12.** Standardize profile naming, required properties, startup checks, and conditions-report review. Those practices make the runtime shape visible before the incident reaches production.
