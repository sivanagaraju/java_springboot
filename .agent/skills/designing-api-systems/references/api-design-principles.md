# API Design Principles Reference

## Contents

- REST Constraints and What They Actually Mean
- HTTP Method Semantics (with retry implications)
- HTTP Status Code Guide
- Error Response Format (RFC 7807 Problem Details)
- Idempotency — the most important API property
- Pagination Design (cursor vs offset)
- API Versioning Strategies
- Rate Limiting Patterns
- API Security Layers

---

## REST Constraints and What They Actually Mean

REST has 6 architectural constraints. Understanding these separates API designers
from API copiers.

| Constraint | What it means in practice | What breaks if violated |
|-----------|--------------------------|------------------------|
| **Stateless** | Server holds NO client session state. Every request carries all context. | Cannot scale horizontally — sticky sessions required |
| **Uniform interface** | Resource-based URLs, HTTP methods as verbs, standardised responses | Clients must learn your custom protocol |
| **Client-server** | Clear separation — client handles UI, server handles data | Tight coupling prevents independent evolution |
| **Cacheable** | Responses declare if they can be cached (`Cache-Control` header) | Unnecessary server load; poor performance |
| **Layered system** | Client doesn't know if it's talking to gateway, load balancer, or origin | Breaking assumption when headers leak internal topology |
| **Code on demand** (optional) | Server can send executable code (e.g. JS) | Rarely used in APIs |

**The stateless constraint is the most violated:** storing session state in the server
prevents horizontal scaling. JWT exists precisely to restore statelessness to auth.

---

## HTTP Method Semantics (with retry implications)

This is where most API bugs originate. Understanding idempotency and safety
determines whether your API is retry-safe.

| Method | Safe? | Idempotent? | Use for | Retry safe? |
|--------|-------|-------------|---------|-------------|
| GET | ✅ | ✅ | Read a resource | Yes — always retry |
| HEAD | ✅ | ✅ | Read headers only | Yes |
| OPTIONS | ✅ | ✅ | CORS preflight | Yes |
| PUT | ❌ | ✅ | Replace a resource entirely | Yes — sending twice = same result |
| DELETE | ❌ | ✅ | Remove a resource | Yes — deleting a deleted resource = 404 or 204 |
| PATCH | ❌ | ❌* | Partial update | Only if designed as idempotent (use conditional requests) |
| POST | ❌ | ❌ | Create or action | NOT safe to retry without idempotency key |

**The POST problem:** A client sends POST /orders. The network drops. The client
retries. Two orders are created. This is why Stripe invented the idempotency key.

**Safe** = has no side effects (read-only). **Idempotent** = same result if sent N times.

---

## HTTP Status Code Guide

What each code means for the client's retry logic:

```
2xx — Success
  200 OK           — GET, PUT, PATCH, DELETE success. Return resource.
  201 Created      — POST created a new resource. Include Location header.
  202 Accepted     — Async operation accepted, not yet complete.
  204 No Content   — DELETE success. No body.

3xx — Redirection
  301 Moved Permanently  — Client should update bookmarks. Cacheable.
  302 Found              — Temporary redirect. Do not cache.
  304 Not Modified       — Cache is still valid (ETag/Last-Modified match).

4xx — Client error (do NOT retry without fixing the request)
  400 Bad Request        — Malformed request. Fix your request.
  401 Unauthorized       — No valid credentials. Get a token.
  403 Forbidden          — Valid token, insufficient permissions.
  404 Not Found          — Resource does not exist.
  409 Conflict           — Business rule conflict (duplicate, wrong state).
  410 Gone               — Resource permanently deleted.
  422 Unprocessable      — Request is valid JSON but fails validation.
  429 Too Many Requests  — Rate limited. Check Retry-After header.

5xx — Server error (MAY retry with backoff)
  500 Internal Server Error  — Unexpected server failure.
  502 Bad Gateway            — Gateway received invalid response from upstream.
  503 Service Unavailable    — Server overloaded or maintenance. Check Retry-After.
  504 Gateway Timeout        — Upstream timeout.
```

**Critical for API consumers:** 5xx errors are retry-safe (the server failed, the
client's request was correct). 4xx errors are NOT retry-safe (the request is wrong).

---

## Error Response Format (RFC 7807 Problem Details)

This is the standard Netflix, Stripe, and most mature APIs use. Do not invent
custom error formats — use RFC 7807.

```json
{
  "type": "https://api.yourdomain.com/errors/insufficient-funds",
  "title": "Insufficient Funds",
  "status": 422,
  "detail": "Account balance $50.00 is less than transfer amount $200.00",
  "instance": "/api/transfers/tx_1234567890",
  "traceId": "abc123def456",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

Spring Boot implementation:
```java
// Use spring-boot-starter-web's built-in RFC 7807 support (Spring Boot 3.x)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY,
            ex.getMessage()
        );
        problem.setType(URI.create("https://api.example.com/errors/insufficient-funds"));
        problem.setTitle("Insufficient Funds");
        problem.setProperty("traceId", MDC.get("traceId"));
        return problem;
    }
}
```

**Why not a custom format?** Client SDKs can be generated from RFC 7807. Error
handling is standardised. The `type` URI is machine-readable. Log correlation is
built in via `traceId`.

---

## Idempotency — The Most Important API Property

Idempotency means: sending the same request N times produces the same result as
sending it once. This is critical for retry safety in distributed systems.

### The Stripe Idempotency Key Pattern

```
POST /v1/charges
Idempotency-Key: a3e4b567-c890-4def-a123-456789bcde01
Content-Type: application/json

{
  "amount": 2000,
  "currency": "usd",
  "source": "tok_visa"
}
```

Stripe stores the idempotency key + response in Redis for 24 hours. If the same
key is sent again, Stripe returns the cached response without re-executing the charge.

Spring Boot implementation:
```java
@PostMapping("/orders")
public ResponseEntity<OrderResponse> createOrder(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @Valid @RequestBody CreateOrderRequest request) {

    // Check cache first
    Optional<OrderResponse> cached = idempotencyCache.get(idempotencyKey);
    if (cached.isPresent()) {
        return ResponseEntity.ok(cached.get());  // Return cached response
    }

    // Execute the operation
    OrderResponse response = orderService.create(request);

    // Cache for 24 hours
    idempotencyCache.put(idempotencyKey, response, Duration.ofHours(24));

    return ResponseEntity.status(201)
        .header("Idempotency-Key", idempotencyKey)
        .body(response);
}
```

**When to use:** Any POST that creates or modifies financial records, orders,
notifications, or any operation where duplicate execution has real consequences.

---

## Pagination Design (cursor vs offset)

### Offset Pagination (simple, has problems at scale)
```
GET /api/products?page=5&size=20
```
Problem: If items are added/deleted between page requests, items appear twice or
are skipped. At high offsets, the DB must scan `offset * size` rows — expensive.

### Cursor-Based Pagination (Netflix, GitHub, Stripe standard)
```
GET /api/products?limit=20&after=prod_abc123
GET /api/products?limit=20&before=prod_xyz789
```

```json
{
  "data": [...],
  "pagination": {
    "has_more": true,
    "next_cursor": "prod_xyz789",
    "prev_cursor": "prod_abc123"
  }
}
```

Spring Boot implementation:
```java
@GetMapping("/products")
public PagedResponse<ProductResponse> listProducts(
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(required = false) String after) {

    // Cursor points to the last seen product ID
    List<Product> products = productRepo.findByIdGreaterThan(
        after != null ? decodeId(after) : 0L,
        PageRequest.of(0, limit + 1)  // fetch one extra to detect has_more
    );

    boolean hasMore = products.size() > limit;
    if (hasMore) products.remove(products.size() - 1);

    return new PagedResponse<>(
        products.stream().map(ProductResponse::from).toList(),
        hasMore,
        hasMore ? encodeId(products.get(products.size() - 1).getId()) : null
    );
}
```

**Why cursor beats offset at scale:** No row scanning. Stable under concurrent
inserts. Works with NoSQL databases (DynamoDB, Cassandra). Netflix exclusively
uses cursor pagination for their content APIs.

---

## API Versioning Strategies

| Strategy | Example | Used by | Trade-offs |
|----------|---------|---------|-----------|
| URI versioning | `/v1/products`, `/v2/products` | Stripe, Twitter | Simple, visible in logs. Versioned URLs are bookmarkable. Old versions stay as separate routes. |
| Header versioning | `API-Version: 2024-01-15` | Stripe (also does this!), GitHub | Cleaner URLs. Harder to test in browser. Requires header awareness. |
| Media type | `Accept: application/vnd.api+json;version=2` | GitHub GraphQL | Most correct. Requires sophisticated content negotiation. |
| Query param | `/products?version=2` | Google (some APIs) | Easy to test. Pollutes query string. |

**Stripe's approach:** Both URI AND date-based header versioning. URI for major
breaking changes (/v1/). Date-based header (`Stripe-Version: 2024-01-01`) for minor
field additions/removals. Old versions are maintained for years.

Spring Boot URI versioning:
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductControllerV1 { ... }

@RestController
@RequestMapping("/api/v2/products")
public class ProductControllerV2 { ... }
```

---

## Rate Limiting Patterns

### Token Bucket (AWS API Gateway, Kong)
Each client has a bucket of tokens. Each request consumes a token. Tokens refill
at a fixed rate. Allows short bursts above the sustained rate.

### Sliding Window (Stripe)
Count requests in the last N seconds using a sliding window. More accurate than
fixed window but requires more memory.

```
Client A makes 100 requests in 1 minute → 429 Too Many Requests
Response header: Retry-After: 45 (seconds until window resets)
```

Spring Boot + Redis rate limiter:
```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Long> redis;
    private static final int MAX_REQUESTS = 100;
    private static final int WINDOW_SECONDS = 60;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String clientId = extractClientId(request); // IP or API key
        String key = "rate:" + clientId;

        Long count = redis.opsForValue().increment(key);
        if (count == 1) {
            redis.expire(key, Duration.ofSeconds(WINDOW_SECONDS));
        }

        if (count > MAX_REQUESTS) {
            response.setStatus(429);
            response.setHeader("Retry-After",
                String.valueOf(redis.getExpire(key, TimeUnit.SECONDS)));
            response.getWriter().write("{\"error\": \"rate_limit_exceeded\"}");
            return;
        }

        response.setHeader("X-RateLimit-Remaining",
            String.valueOf(MAX_REQUESTS - count));
        chain.doFilter(request, response);
    }
}
```

**Design decision:** Should the rate limiter fail open (allow traffic) or fail
closed (block traffic) when Redis is unavailable? Stripe's answer: fail open with
logging. Blocking legitimate customers during a Redis outage is worse than allowing
some extra traffic during that window.

---

## API Security Layers

Defence in depth — apply these in order from outermost to innermost:

```
Internet
    │
    ▼
[ TLS/HTTPS ]          ← Encrypts transport. Required everywhere.
    │
    ▼
[ API Gateway ]        ← Rate limiting, IP allowlisting, DDoS protection
    │
    ▼
[ Authentication ]     ← Who are you? (JWT, API key, OAuth2 token)
    │
    ▼
[ Authorization ]      ← What can you do? (Roles, scopes, resource ownership)
    │
    ▼
[ Input Validation ]   ← Is the request well-formed? (@Valid, schema validation)
    │
    ▼
[ Business Rules ]     ← Does the operation make sense? (balance > 0, item in stock)
    │
    ▼
[ Database ]           ← Parameterized queries, row-level security
```

Never skip a layer because an outer one exists. A misconfigured gateway can fail
open. An API key can be leaked. Defense in depth catches failures at each boundary.
