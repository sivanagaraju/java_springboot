# 02-Dependency-Injection — Progressive Quiz Drill

## Round 1 — Core Recall (8 questions)
Answer from memory, then check the key.

**Q1:** How many types of dependency injection does Spring support? Name them.
**Q2:** Why is constructor injection preferred over field injection?
**Q3:** When do you NOT need `@Autowired` on a constructor?
**Q4:** You have `interface NotificationService` with two implementations: `EmailNotification` and `SmsNotification`. You inject `NotificationService` without any qualifier. What happens?
**Q5:** What's the difference between `@Primary` and `@Qualifier`?
**Q6:** How do you inject ALL implementations of an interface into one bean?
**Q7:** What does `@RequiredArgsConstructor` (Lombok) generate?
**Q8:** Can you inject a `prototype`-scoped bean into a `singleton`-scoped bean? What's the trap?

### Answer Key — Round 1
A1: Three: Constructor injection (recommended), Setter injection (optional deps), Field injection (anti-pattern).
A2: Constructor injection: (1) fields can be `final` (immutable), (2) dependencies are visible in the constructor signature, (3) testable with `new Service(mockA, mockB)`, (4) catches circular dependencies at startup.
A3: Since Spring 4.3 — if a class has exactly ONE constructor, Spring auto-detects it. No `@Autowired` needed.
A4: `NoUniqueBeanDefinitionException` — Spring finds two candidates and doesn't know which to pick. Fix: add `@Primary` on one, or `@Qualifier` at the injection point.
A5: `@Primary` marks a bean as the "default" — used when no qualifier is specified. `@Qualifier("name")` selects a specific bean by its name — overrides `@Primary`. Think: `@Primary` = "default", `@Qualifier` = "explicit override".
A6: Inject `List<NotificationService>` — Spring collects ALL beans of that type into the list.
A7: A constructor with parameters for all `final` fields. This enables constructor injection without writing the constructor manually.
A8: Yes, but it's a trap! The prototype bean is injected ONCE at singleton creation time. All requests get the SAME prototype instance. Fix: inject `ObjectFactory<PrototypeBean>` or `Provider<PrototypeBean>`.

---

## Round 2 — Apply & Compare (3 questions)
Translate between Python/FastAPI and Spring.

**Q1:** Translate this Python dependency injection to Spring:
```python
class StripePayment:
    def charge(self, amount): return f"Stripe: ${amount}"

class PayPalPayment:
    def charge(self, amount): return f"PayPal: ${amount}"

def get_payment(provider: str = "stripe"):
    if provider == "stripe": return StripePayment()
    return PayPalPayment()

@app.post("/pay")
def pay(amount: float, payment = Depends(get_payment)):
    return payment.charge(amount)
```

**Q2:** In Python you test with `unittest.mock.MagicMock()`. How do you test a Spring service with mock dependencies WITHOUT loading the Spring context?

**Q3:** In FastAPI, `Depends()` creates a NEW dependency per request. In Spring, constructor-injected beans are typically singletons. When would you want per-request injection in Spring?

### Answer Key — Round 2
A1:
```java
public interface PaymentService { String charge(double amount); }

@Service @Primary
public class StripePayment implements PaymentService {
    public String charge(double amount) { return "Stripe: $" + amount; }
}

@Service("paypal")
public class PayPalPayment implements PaymentService {
    public String charge(double amount) { return "PayPal: $" + amount; }
}

@RestController
public class PayController {
    private final PaymentService payment; // gets @Primary (Stripe)
    public PayController(PaymentService payment) { this.payment = payment; }
    
    @PostMapping("/pay")
    public String pay(@RequestParam double amount) { return payment.charge(amount); }
}
```

A2: Pure JUnit + Mockito, no Spring context needed:
```java
@Test
void testOrderService() {
    PaymentService mockPayment = mock(PaymentService.class);
    when(mockPayment.charge(100.0)).thenReturn("Charged $100");
    
    OrderService service = new OrderService(mockPayment);
    String result = service.processOrder(100.0);
    
    assertEquals("Charged $100", result);
    verify(mockPayment).charge(100.0);
}
```
This is WHY constructor injection matters — you can `new` the service with mocks.

A3: Use `@RequestScope` — creates a new bean instance per HTTP request. Use cases: (1) per-request audit context, (2) per-request tenant resolver in multi-tenant apps, (3) per-request user context. Spring Boot creates a new instance for each request and destroys it when the response is sent.

---

## Round 3 — Debug the Bug (3 scenarios)

**Scenario 1:** Your `@Service` class uses field injection and works fine in production. But your JUnit test creates the service with `new OrderService()` and gets `NullPointerException` on every method call. What's wrong and how do you fix the test AND the service?

**Scenario 2:** You add a new `@Service` class that implements `PaymentService`. Suddenly the app refuses to start with `NoUniqueBeanDefinitionException`. The original service worked fine before. What happened?

**Scenario 3:** Your `OrderService` injects `InventoryService` and `InventoryService` injects `OrderService`. With field injection this worked. You refactored to constructor injection and now get `BeanCurrentlyInCreationException`. Why?

### Answer Key — Round 3
S1: Field injection requires Spring to use reflection. When you `new OrderService()`, the `@Autowired` fields are `null`. Fix: refactor to constructor injection — then your test creates `new OrderService(mockRepo, mockPayment)` with all dependencies provided.

S2: Before, there was only ONE `PaymentService` implementation — Spring auto-selected it. Adding a second creates ambiguity. Fix: add `@Primary` on the default implementation, or `@Qualifier` at the injection point. This is why you should always design for multiple implementations from the start.

S3: Constructor injection detected the circular dependency! `OrderService(InventoryService)` requires `InventoryService` to exist. But `InventoryService(OrderService)` requires `OrderService` to exist. Neither can be created first. With field injection, Spring creates empty shells THEN injects — hiding the cycle. Fix: break the cycle by extracting shared logic into a third service, or use `@Lazy` on one constructor parameter.

---

## Round 4 — Staff-Level Scenarios (2 questions)

**Q1 — Architecture Decision:**
Your team debates between constructor injection with Lombok `@RequiredArgsConstructor` vs explicit constructors. What are the trade-offs? What would you recommend for a team of 20 developers?

**Q2 — Advanced Injection:**
You need a service that selects different implementations at RUNTIME based on request data (e.g., country → different tax calculator). `@Qualifier` doesn't work because it's compile-time. What pattern do you use?

### Answer Key — Round 4

A1: Trade-offs:
- **Lombok**: Less boilerplate, faster to write, auto-updates when fields change. Risk: generated code is invisible, debugging is harder, Lombok version upgrades can break builds.
- **Explicit**: More verbose but self-documenting, visible in code review, works without extra tooling.
- **Recommendation**: Use Lombok `@RequiredArgsConstructor` in service/repository layers (high churn, many fields). Use explicit constructors in `@Configuration` classes (few beans, readability matters). Document the convention in team coding standards.

A2: Use the **Strategy Pattern with a Map injection**:
```java
@Service
public class TaxService {
    private final Map<String, TaxCalculator> calculators;
    
    public TaxService(List<TaxCalculator> all) {
        this.calculators = all.stream()
            .collect(Collectors.toMap(TaxCalculator::getCountryCode, Function.identity()));
    }
    
    public double calculate(String country, double amount) {
        return calculators.getOrDefault(country, calculators.get("DEFAULT"))
            .calculate(amount);
    }
}
```
Spring injects ALL `TaxCalculator` implementations. The service builds a lookup map. Runtime selection without `if-else` chains.
