# JSON Processing — Jackson ObjectMapper

## Diagram: Jackson Serialization Pipeline

```mermaid
flowchart TD
    A["HTTP Request\n{\"name\":\"John\",\"age\":25}"] --> B["Spring @RequestBody\ntriggers Jackson"]
    B --> C["ObjectMapper.readValue()\nJSON → Java object"]
    C --> D["User{name='John', age=25}\nDeserializedObject"]

    E["Java Object\nUser{name='John', age=25}"] --> F["ObjectMapper.writeValueAsString()\nJava object → JSON"]
    F --> G["HTTP Response\n{\"name\":\"John\",\"age\":25}"]

    H["Jackson annotations control mapping:"] --> I["@JsonProperty('user_name')\nrename field in JSON"]
    H --> J["@JsonIgnore\nexclude field from JSON"]
    H --> K["@JsonFormat(pattern='yyyy-MM-dd')\ndate formatting"]
    H --> L["@JsonInclude(NON_NULL)\nomit null fields"]
```

## Why Jackson?

```
┌────────────────────────────────────────────────────────────┐
│ Spring Boot uses Jackson by default for ALL JSON:           │
│                                                              │
│ @PostMapping("/users")                                      │
│ public User create(@RequestBody User user) { ... }          │
│                     ↑                                        │
│              Jackson deserializes JSON → User automatically  │
│                                                              │
│ @GetMapping("/users/{id}")                                   │
│ public User get(@PathVariable Long id) { return user; }     │
│                                            ↑                 │
│              Jackson serializes User → JSON automatically    │
└────────────────────────────────────────────────────────────┘
```

---

## 1. ObjectMapper Basics

```java
ObjectMapper mapper = new ObjectMapper();

// Java → JSON (Serialization)
User user = new User("John", 25, "john@mail.com");
String json = mapper.writeValueAsString(user);
// {"name":"John","age":25,"email":"john@mail.com"}

// JSON → Java (Deserialization)
String input = """
    {"name":"Jane","age":30,"email":"jane@mail.com"}
    """;
User jane = mapper.readValue(input, User.class);

// JSON → File
mapper.writeValue(new File("user.json"), user);

// File → Java
User fromFile = mapper.readValue(new File("user.json"), User.class);

// JSON → List
String jsonArray = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
List<User> users = mapper.readValue(jsonArray,
    new TypeReference<List<User>>() {});  // TypeReference for generics!
```

---

## 2. Key Annotations

```java
public class Product {
    @JsonProperty("product_name")     // JSON key ≠ field name
    private String name;

    @JsonIgnore                        // excluded from JSON
    private String internalCode;

    @JsonFormat(pattern = "yyyy-MM-dd") // date format
    private LocalDate createdAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)  // skip null fields
    private String description;
}
```

```
Annotation Map:
┌──────────────────────────┬─────────────────────────────────┐
│ @JsonProperty("name")    │ Rename field in JSON output     │
│ @JsonIgnore              │ Exclude from serialization      │
│ @JsonFormat              │ Date/time format pattern        │
│ @JsonInclude(NON_NULL)   │ Skip null fields                │
│ @JsonCreator             │ Custom constructor for deser    │
│ @JsonValue               │ Use single field as JSON value  │
│ @JsonManagedReference    │ Handle circular references      │
│ @JsonBackReference       │ (parent/child bidirectional)    │
└──────────────────────────┴─────────────────────────────────┘
```

---

## 3. Spring Boot Auto-Configuration

```
application.yml:
  spring.jackson:
    date-format: "yyyy-MM-dd HH:mm:ss"
    serialization:
      INDENT_OUTPUT: true           # pretty print
      WRITE_DATES_AS_TIMESTAMPS: false
    deserialization:
      FAIL_ON_UNKNOWN_PROPERTIES: false  # don't crash on extra fields
    default-property-inclusion: NON_NULL

These configure the GLOBAL ObjectMapper that Spring injects everywhere.
Custom config:
  @Bean
  public ObjectMapper objectMapper() {
      return new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
```

---

## Python Bridge

| Java Jackson | Python Equivalent |
|---|---|
| `ObjectMapper.writeValueAsString(obj)` | `json.dumps(obj.__dict__)` or Pydantic `.json()` |
| `ObjectMapper.readValue(json, User.class)` | `json.loads(json_str)` or Pydantic `User.parse_raw(json)` |
| `@JsonProperty("user_name")` | Pydantic `Field(alias="user_name")` |
| `@JsonIgnore` | Pydantic `Field(exclude=True)` |
| `@JsonFormat(pattern="yyyy-MM-dd")` | Pydantic `validator` or `json_encoders` |
| `@JsonInclude(NON_NULL)` | Pydantic `model_config = {'exclude_none': True}` |
| `@RequestBody User user` | FastAPI `async def create(user: UserSchema)` |

**Critical Difference:** Pydantic (Python) validates and coerces types at runtime — `User(age="25")` becomes `User(age=25)`. Jackson (Java) is stricter — type mismatches throw `JsonMappingException`. Pydantic's approach is more forgiving for external APIs; Jackson's strictness catches data contract violations earlier. Spring Boot's `@Valid` annotation adds the explicit validation layer that Pydantic includes by default.

---

## 🎯 Interview Questions

**Q1: How does @RequestBody work with Jackson?**
> Spring's `MappingJackson2HttpMessageConverter` intercepts the HTTP request body. It reads the `Content-Type` header (must be `application/json`), then calls `ObjectMapper.readValue()` to deserialize the JSON string into the method parameter type. No explicit ObjectMapper code needed.

**Q2: How do you handle Java 8 date/time types with Jackson?**
> Register the `JavaTimeModule`: `mapper.registerModule(new JavaTimeModule())`. Without it, `LocalDate/LocalDateTime` serialize as arrays `[2024, 3, 15]` instead of strings `"2024-03-15"`. Also disable `WRITE_DATES_AS_TIMESTAMPS`.

**Q3: What's the TypeReference trick for generics?**
> Due to type erasure, `mapper.readValue(json, List.class)` loses the generic type. `new TypeReference<List<User>>() {}` creates an anonymous subclass that preserves the generic type at runtime, allowing Jackson to deserialize each element as `User`.
