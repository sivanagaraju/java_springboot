/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  JACKSON JSON — Demo                                        ║
 * ║  ObjectMapper: Java objects ↔ JSON conversion               ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * NOTE: Requires Jackson dependency:
 *   implementation 'com.fasterxml.jackson.core:jackson-databind:2.17.0'
 *   (Spring Boot includes this automatically)
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  ObjectMapper                                            │
 * │  ┌──────────────────┐   ┌──────────────────────┐       │
 * │  │ writeValueAsString│   │ readValue             │       │
 * │  │  Java → JSON      │   │  JSON → Java          │       │
 * │  └──────────────────┘   └──────────────────────┘       │
 * │                                                          │
 * │  Spring auto-does this for @RequestBody/@ResponseBody   │
 * └──────────────────────────────────────────────────────────┘
 *
 * Since Jackson is a dependency, this demo shows the CONCEPTS.
 * The actual code below is pseudo-code explanations.
 */
public class JacksonJsonDemo {

    /**
     * In a real Spring project, this is what Jackson does:
     *
     * // POJO
     * public class User {
     *     private String name;
     *     private int age;
     *     @JsonIgnore
     *     private String password;
     *     // getters, setters
     * }
     *
     * // Serialize (Java → JSON):
     * ObjectMapper mapper = new ObjectMapper();
     * User user = new User("John", 25, "secret");
     * String json = mapper.writeValueAsString(user);
     * // → {"name":"John","age":25}  (password ignored!)
     *
     * // Deserialize (JSON → Java):
     * String input = "{\"name\":\"Jane\",\"age\":30}";
     * User jane = mapper.readValue(input, User.class);
     *
     * // List handling:
     * String jsonArray = "[{\"name\":\"A\"},{\"name\":\"B\"}]";
     * List<User> users = mapper.readValue(jsonArray,
     *     new TypeReference<List<User>>() {});
     *
     * // Spring Boot — all automatic:
     * @PostMapping("/users")
     * public User create(@RequestBody User user) {
     *     // Jackson already deserialized JSON → User
     *     return userService.save(user);
     *     // Jackson serializes User → JSON for response
     * }
     */
    public static void main(String[] args) {
        System.out.println("═══ JACKSON JSON DEMO (Concepts) ═══\n");

        System.out.println("Jackson Core Operations:");
        System.out.println("  1. writeValueAsString(obj) → Java object to JSON string");
        System.out.println("  2. readValue(json, Type.class) → JSON string to Java object");
        System.out.println("  3. writeValue(file, obj) → Java object to JSON file");
        System.out.println("  4. readValue(file, Type.class) → JSON file to Java object");

        System.out.println("\nKey Annotations:");
        System.out.println("  @JsonProperty(\"name\") → custom JSON key name");
        System.out.println("  @JsonIgnore → exclude field from JSON");
        System.out.println("  @JsonFormat(pattern=\"yyyy-MM-dd\") → date format");
        System.out.println("  @JsonInclude(NON_NULL) → skip null fields");

        System.out.println("\nSpring Boot Integration:");
        System.out.println("  @RequestBody → Jackson deserializes HTTP body → Java object");
        System.out.println("  @ResponseBody → Jackson serializes Java object → HTTP body");
        System.out.println("  Configured via spring.jackson.* properties");

        System.out.println("\nCommon Gotchas:");
        System.out.println("  1. Need default constructor for deserialization");
        System.out.println("  2. Use TypeReference<List<T>>() for generic types");
        System.out.println("  3. Register JavaTimeModule for LocalDate/LocalDateTime");
        System.out.println("  4. Set FAIL_ON_UNKNOWN_PROPERTIES=false for flexibility");

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Jackson is the JSON engine behind Spring Boot.");
        System.out.println("Add jackson-databind dependency (Spring Boot includes it).");
        System.out.println("Know annotations: @JsonProperty, @JsonIgnore, @JsonFormat.");
    }
}
