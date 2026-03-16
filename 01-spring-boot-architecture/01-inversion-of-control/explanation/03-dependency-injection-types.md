# Types of Dependency Injection (DI)

Dependency Injection is not a framework; it is an architectural pattern. Spring Boot primarily provides three methods to resolve and inject dependencies.

## 1. Field Injection (The Anti-Pattern)

**Mechanics:**
Injecting the dependency directly onto the private structural field using the `@Autowired` annotation.

```java
@Service
public class OrderService {

    @Autowired
    private PaymentGateway gateway; // BAD! Field Injection

    public void process() {
        gateway.charge();
    }
}
```

**Why is it terrible?**
1. **Hidden Dependencies:** The class doesn't explicitly declare its requirements to the outside world.
2. **Immutability Impossible:** The `gateway` field cannot be marked `final`, meaning it can be dangerously modified or nullified at runtime.
3. **Testing is a Nightmare:** You cannot instantiate this class natively in a Unit Test without booting up the massive 5-second Spring Context, because the field is entirely private. You are utterly forced to rely on heavy reflection frameworks like Mockito just to test one method.
4. **Circular Dependencies:** Field injection easily covers up fundamentally broken architectural circular dependencies (Service A depends on Service B, which depends on Service A), causing catastrophic `StackOverflowErrors` at startup.

**Architect Rule:** Never use Field Injection. It has been strictly deprecated conceptually by the Spring engineering team.

## 2. Setter Injection (The Rare Case)

**Mechanics:**
The dependency is injected via a public setter method.

```java
@Service
public class OrderService {
    private PaymentGateway gateway;

    @Autowired
    public void setPaymentGateway(PaymentGateway gateway) {
        this.gateway = gateway;
    }
}
```

**Why use it?**
Only use Setter Injection if the dependency is purely optional and the class can completely function safely without it. If the `OrderService` absolutely physically requires a `PaymentGateway` to fundamentally work, do not use Setter Injection, as the object might exist in an invalid `null` state temporarily.

## 3. Constructor Injection (The Enterprise Standard)

**Mechanics:**
The dependency is injected precisely during the physical instantiation of the class via its Constructor.

```java
@Service
public class OrderService {
    
    // GOOD: The field is marked final correctly! It is completely Immutable.
    private final PaymentGateway gateway;

    // GOOD: The class loudly declares its dependencies publicly.
    @Autowired // Note: In modern Spring, this annotation is entirely Optional if there's only 1 constructor.
    public OrderService(PaymentGateway gateway) {
        this.gateway = gateway;
    }

    public void process() {
        gateway.charge();
    }
}
```

**Why is it perfect?**
1. **Guaranteed Initialization:** The `OrderService` object physically mathematically cannot be instantiated until the `PaymentGateway` object is handed to it. Natively preventing `NullPointerExceptions` completely.
2. **Immutability:** You can natively declare the field `final`, cleanly securing the dependency thread-safely structurally.
3. **Easy Testing:** You can natively type `new OrderService(new MockGateway())` instantly in a JUnit Test with zero seconds of Spring Boot penalty.

**Architect Rule:** Absolutely exclusively strictly definitively natively use Constructor Injection exclusively for 100% of required dependencies perfectly functionally correctly efficiently natively.
