# Dependency Injection

## Constructor Injection (PREFERRED)
- Immutable — final fields
- Fail-fast — missing bean = startup error
- Testable — just pass mocks to constructor
- No @Autowired needed (single constructor)
- Spring team recommended approach

## Setter Injection
- Mutable — can change after construction
- Optional dependencies — @Autowired(required=false)
- Circular dependency workaround
- Python equivalent — property setter

## Field Injection (AVOID)
- Hidden dependencies — not visible in constructor
- Cannot test without reflection
- Tight coupling to Spring container
- IntelliJ warns about this

## @Qualifier
- Problem — multiple beans of same type
- Solution — name the specific bean
- @Qualifier("emailNotifier")
- Works with constructor + setter + field

## @Primary
- Default bean when multiple candidates
- No @Qualifier needed at injection point
- Overridden by @Qualifier when specified
- Python equivalent — default parameter value

## Circular Dependencies
- A depends on B, B depends on A
- Constructor injection — fails fast (good!)
- Setter injection — handles lazily (hides problem)
- @Lazy — deferred proxy creation
- Best fix — redesign (extract common dependency)
