# Python Bridge Reference — Java/Spring ↔ Python/FastAPI

Every Java concept in this repo must be compared to its Python/FastAPI equivalent.
Use this file as a lookup when writing Python Bridge sections.

## Contents

- Dependency Injection
- ORM / Data Access
- Web Layer
- Configuration
- Validation
- Security / Auth
- Testing
- Concurrency
- Error Handling

---

## Dependency Injection

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| `@Autowired` constructor injection | `def __init__(self, repo: Repo = Depends())` |
| `@Component`, `@Service`, `@Repository` | Registered manually or via FastAPI `Depends()` |
| `ApplicationContext` (IoC container) | No direct equivalent — Python uses manual wiring or DI frameworks like `dependency-injector` |
| Bean lifecycle (`@PostConstruct`, `@PreDestroy`) | `@app.on_event("startup")` / `@app.on_event("shutdown")` |
| `@Scope("prototype")` | New instance per call — Python default function behaviour |
| `@Scope("singleton")` | Module-level singleton — Python module import caches instances |

**Key difference:** Spring manages the entire object graph automatically. Python/FastAPI
requires explicit dependency declaration in function signatures or manual registration.

---

## ORM / Data Access

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| `JpaRepository<Entity, ID>` | SQLAlchemy `Session` or `AsyncSession` |
| `@Entity`, `@Table`, `@Column` | SQLAlchemy `Base`, `Column`, `relationship()` |
| `@OneToMany(mappedBy="...")` | SQLAlchemy `relationship("Child", back_populates="parent")` |
| `@Transactional` | `async with db.begin():` or SQLAlchemy `with Session() as session:` |
| `EntityManager.persist()` | `db.add(obj)` |
| `EntityManager.flush()` | `db.flush()` |
| JPQL: `SELECT p FROM Product p WHERE p.price > :min` | SQLAlchemy: `session.query(Product).filter(Product.price > min_val)` |
| Spring Data derived queries: `findByPriceGreaterThan(BigDecimal price)` | No equivalent — write explicit SQLAlchemy queries |
| HikariCP connection pool | SQLAlchemy `create_engine(pool_size=10)` |
| `@Version` (optimistic locking) | SQLAlchemy `__version_id_col__` or manual version column |

---

## Web Layer

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| `@RestController` | `@app.get("/path")` function or `APIRouter` |
| `@GetMapping("/products/{id}")` | `@router.get("/products/{id}")` |
| `@RequestBody ProductRequest req` | `req: ProductRequest` (Pydantic model as parameter) |
| `@PathVariable Long id` | `id: int` in function signature after `{id}` in path |
| `@RequestParam String sort` | `sort: str = Query(default="name")` |
| `ResponseEntity<T>` | Return tuple `(data, status_code)` or `JSONResponse` |
| `@RestControllerAdvice` | FastAPI `@app.exception_handler(ExceptionType)` |
| `@Valid` on request body | Pydantic automatic validation on model instantiation |
| `HttpServletRequest` | `Request` object from Starlette |

---

## Configuration

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| `application.properties` / `application.yml` | `.env` file or `pydantic-settings` `BaseSettings` |
| `@Value("${property.name}")` | `os.environ.get("PROPERTY_NAME")` or `settings.property_name` |
| `@ConfigurationProperties(prefix="app")` | `class Settings(BaseSettings): app_timeout: int = 30` |
| `@Profile("prod")` | `if settings.ENV == "prod":` |
| Spring profiles (`-Dspring.profiles.active=prod`) | `ENV=prod python -m uvicorn main:app` |

---

## Validation

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| `@NotNull` | `field: str` (required by default in Pydantic) |
| `@NotBlank` | `field: str = Field(min_length=1)` |
| `@Size(min=1, max=100)` | `field: str = Field(min_length=1, max_length=100)` |
| `@Min(0)` / `@Max(100)` | `field: int = Field(ge=0, le=100)` |
| `@Email` | `field: EmailStr` (pydantic[email]) |
| `@Pattern(regexp="...")` | `field: str = Field(pattern=r"...")` |
| Custom `@Constraint` + `ConstraintValidator` | Pydantic `@validator` or `@field_validator` |
| Bean Validation groups | Pydantic model inheritance or discriminated unions |

---

## Security / Auth

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| Spring Security filter chain | Starlette middleware (`@app.middleware("http")`) |
| `OncePerRequestFilter` | Starlette `BaseHTTPMiddleware` |
| `UserDetailsService.loadUserByUsername()` | FastAPI dependency that queries the user store |
| `BCryptPasswordEncoder` | `passlib.context.CryptContext(schemes=["bcrypt"])` |
| `@PreAuthorize("hasRole('ADMIN')")` | FastAPI `Security(get_current_user, scopes=["admin"])` |
| JWT via `jjwt` library | `python-jose` or `PyJWT` |
| `SecurityContextHolder.getContext().getAuthentication()` | FastAPI `current_user: User = Depends(get_current_user)` |
| CORS configuration in `SecurityFilterChain` | FastAPI `CORSMiddleware` |

---

## Testing

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| JUnit 5 `@Test` | `pytest` test function |
| `@BeforeEach` setup | `@pytest.fixture` |
| `@AfterEach` teardown | `yield` fixture or `@pytest.fixture(autouse=True)` |
| Mockito `mock(ProductService.class)` | `unittest.mock.MagicMock()` or `pytest-mock` `mocker.Mock()` |
| `when(service.find(1L)).thenReturn(product)` | `service.find.return_value = product` |
| `verify(service, times(1)).find(1L)` | `service.find.assert_called_once_with(1)` |
| `MockMvc` — test controllers without server | `TestClient` from FastAPI/Starlette |
| `@SpringBootTest` — full integration test | `pytest` with actual FastAPI `app` + `httpx.AsyncClient` |
| `@DataJpaTest` — slice test for JPA layer | `pytest` with SQLite/PostgreSQL session fixture |

---

## Concurrency

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| `synchronized` method | `asyncio.Lock()` or `threading.Lock()` |
| `ExecutorService` + `Future` | `asyncio.gather()` or `concurrent.futures.ThreadPoolExecutor` |
| Virtual threads (Java 21) | `asyncio` coroutines or `anyio` task groups |
| `CompletableFuture.supplyAsync()` | `asyncio.create_task()` |
| `@Async` Spring method | FastAPI `BackgroundTasks.add_task()` |
| Thread pool size config | `uvicorn --workers N` |

---

## Error Handling

| Java / Spring | Python / FastAPI |
|--------------|-----------------|
| `RuntimeException` (unchecked) | Python exceptions (all unchecked) |
| Checked exceptions (`throws IOException`) | No equivalent — Python has no checked exceptions |
| `@RestControllerAdvice` + `@ExceptionHandler` | FastAPI `@app.exception_handler(ExcType)` |
| `ResponseStatusException(HttpStatus.NOT_FOUND, msg)` | `raise HTTPException(status_code=404, detail=msg)` |
| Custom exception class extending `RuntimeException` | Custom exception class extending `Exception` |
| `try-with-resources` (`try (Connection c = ...) {}`) | Python `with` statement (context manager) |
