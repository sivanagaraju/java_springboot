# Python Bridge Patterns — spring-mastery

Quick-reference table for writing Python Bridge sections in explanation files.
Look up the Java concept, find the Python/FastAPI equivalent, use the narrative
guidance to explain the philosophical difference.

## Contents

- IoC and Dependency Injection
- Data Access (JPA, JDBC, Hibernate)
- Web Layer (REST Controllers)
- Configuration and Properties
- Validation
- Security and Auth
- Testing
- Async and Concurrency
- Exception Handling

---

## IoC and Dependency Injection

| Java / Spring | Python / FastAPI | Notes |
|--------------|-----------------|-------|
| `@Component` / `@Service` / `@Repository` | Registered via `Depends()` or manually | Spring scans automatically |
| Constructor injection | `def __init__(self, svc: Service = Depends())` | Both prefer constructor |
| `ApplicationContext` | No direct equivalent | Python uses explicit wiring |
| `@Bean` in `@Configuration` | `@app.on_event("startup")` setup | Spring does this at boot |
| `@Scope("prototype")` | New object per call (default Python) | Spring default is singleton |
| `@Profile("prod")` | `if os.getenv("ENV") == "prod":` | Spring profiles > env vars |
| `@Lazy` | Lazy imports or module-level lazy init | Spring default is eager |
| `@Conditional` | `if` checks in setup code | No annotation equivalent |

**Mental model difference:** In Spring, you never call `new ProductService()`.
The container builds every object and wires its dependencies. In Python/FastAPI,
you explicitly declare dependencies in function signatures. Spring's approach enables
AOP (interceptors, transactions, security) to be applied transparently — FastAPI's
approach is more explicit but doesn't support the same transparent proxy wrapping.

---

## Data Access (JPA, JDBC, Hibernate)

| Java / Spring | Python / FastAPI | Notes |
|--------------|-----------------|-------|
| `JpaRepository<Entity, ID>` | SQLAlchemy `Session.query()` | Spring auto-generates SQL |
| `findByNameAndPrice()` derived query | Explicit `filter()` chain in SQLAlchemy | Spring reads method names |
| `@Entity`, `@Table` | SQLAlchemy `Base`, `__tablename__` | Same concept, different syntax |
| `@OneToMany(fetch = LAZY)` | `relationship("Child", lazy="select")` | Both support lazy loading |
| `@Transactional` | `async with db.begin():` | Spring applies via AOP proxy |
| `EntityManager.persist()` | `db.add(obj)` | Same semantic |
| `EntityManager.flush()` | `db.flush()` | Same semantic |
| HikariCP pool | `create_engine(pool_size=10, max_overflow=5)` | Same concept |
| `@Version` optimistic lock | SQLAlchemy `__version_id_col__` | Same pattern |
| `@Lock(PESSIMISTIC_WRITE)` | `with_for_update()` in SQLAlchemy | Same SQL: SELECT FOR UPDATE |
| JPQL `SELECT p FROM Product p` | `session.query(Product)` | JPQL is OOP-style SQL |

**Mental model difference:** JPA/Hibernate tracks entity state automatically (Transient →
Persistent → Detached). Changes to a managed entity are automatically flushed to the DB
at transaction commit — you never call `UPDATE` explicitly. SQLAlchemy works similarly
with `session.add()` and `session.flush()`, but the lifecycle is more explicit.

---

## Web Layer (REST Controllers)

| Java / Spring | Python / FastAPI | Notes |
|--------------|-----------------|-------|
| `@RestController` | `@app.get()` / `APIRouter` | FastAPI function = Spring method |
| `@GetMapping("/path/{id}")` | `@router.get("/path/{id}")` | Same path param syntax |
| `@RequestBody ProductReq req` | `req: ProductReq` (Pydantic model) | Both auto-deserialize JSON |
| `@PathVariable Long id` | `id: int` in function after `{id}` in path | Same concept |
| `@RequestParam(defaultValue="name")` | `sort: str = Query(default="name")` | Same concept |
| `ResponseEntity<T>` | Return dict/tuple or `JSONResponse` | Spring is more explicit |
| `@RestControllerAdvice` | `@app.exception_handler(ExcType)` | Both centralise error handling |
| `@Valid` on `@RequestBody` | Pydantic validates on instantiation | Both fail before handler runs |
| `BindingResult` | FastAPI returns 422 automatically | Spring gives you more control |
| `HttpServletRequest` | Starlette `Request` object | Both give access to raw request |

**Mental model difference:** FastAPI uses Python functions as handlers — one function,
one endpoint. Spring uses class methods grouped in a `@RestController`. Spring's class-level
`@RequestMapping` lets you group all `/products/**` routes in one place, which helps
organisation in large APIs with 50+ endpoints.

---

## Configuration and Properties

| Java / Spring | Python / FastAPI | Notes |
|--------------|-----------------|-------|
| `application.properties` | `.env` file or config YAML | Spring supports both |
| `@Value("${property}")` | `os.environ.get("PROPERTY")` | Spring is type-safe |
| `@ConfigurationProperties(prefix="app")` | `class Settings(BaseSettings):` | Same concept |
| Spring profiles (`-Dspring.profiles.active=prod`) | `ENV=prod uvicorn main:app` | Spring profiles > env vars |
| `@Profile("test")` bean | `@pytest.fixture` overriding dependency | Different mechanism |
| `spring.datasource.url` | `DATABASE_URL` env var | Spring auto-configures DataSource |

---

## Validation

| Java / Spring (Bean Validation) | Python / FastAPI (Pydantic) |
|--------------------------------|----------------------------|
| `@NotNull` | `field: SomeType` (required by default) |
| `@NotBlank` | `field: str = Field(min_length=1)` |
| `@Size(min=1, max=50)` | `field: str = Field(min_length=1, max_length=50)` |
| `@Min(0)` / `@Max(100)` | `field: int = Field(ge=0, le=100)` |
| `@Email` | `field: EmailStr` |
| `@Pattern(regexp="[A-Z]{3}")` | `field: str = Field(pattern=r"[A-Z]{3}")` |
| Custom `ConstraintValidator` | Pydantic `@field_validator` |
| `@Valid` on controller param | FastAPI validates automatically |
| Validation groups | Pydantic model inheritance |

---

## Security and Auth

| Java / Spring Security | Python / FastAPI |
|-----------------------|-----------------|
| Filter chain (15 default filters) | `@app.middleware("http")` chain |
| `OncePerRequestFilter` | `BaseHTTPMiddleware` |
| `UserDetailsService` | `get_current_user` dependency |
| `BCryptPasswordEncoder` | `passlib.CryptContext(schemes=["bcrypt"])` |
| `@PreAuthorize("hasRole('ADMIN')")` | `Security(get_current_user, scopes=["admin"])` |
| `SecurityContextHolder` | FastAPI `Depends(get_current_user)` |
| `UsernamePasswordAuthenticationFilter` | No equivalent (manual in FastAPI) |
| Spring Security CORS config | FastAPI `CORSMiddleware` |
| `AuthenticationManager` | FastAPI manually verifies credentials |

---

## Testing

| Java / JUnit 5 + Mockito | Python / pytest |
|--------------------------|----------------|
| `@Test` | `def test_*():` |
| `@BeforeEach` | `@pytest.fixture` (auto-use) |
| `@AfterEach` | `yield` fixture cleanup |
| `mock(ProductService.class)` | `MagicMock()` / `mocker.Mock()` |
| `when(x.method()).thenReturn(val)` | `x.method.return_value = val` |
| `verify(x, times(1)).method()` | `x.method.assert_called_once_with(...)` |
| `MockMvc` | FastAPI `TestClient` |
| `@SpringBootTest` | `pytest` + full app + `httpx.AsyncClient` |
| `@DataJpaTest` | `pytest` + SQLite/test DB fixture |
| `assertThat(x).isEqualTo(y)` (AssertJ) | `assert x == y` |

---

## Exception Handling

| Java / Spring | Python |
|--------------|--------|
| `RuntimeException` (unchecked) | All Python exceptions (all unchecked) |
| Checked exceptions (`throws IOException`) | No equivalent in Python |
| `@RestControllerAdvice` handler | `@app.exception_handler(ExcType)` |
| `ResponseStatusException(404, msg)` | `raise HTTPException(status_code=404, detail=msg)` |
| `try-with-resources` | Python `with` context manager |
| Custom exception extends `RuntimeException` | Custom exception extends `Exception` |
