# Staff Engineer Patterns Reference

Company-specific API and system design patterns for Amazon, Meta, Google,
Microsoft, Netflix, and Stripe. Use these when writing interview questions
and real-world use cases.

## Contents

- Amazon — Distributed Systems and API Gateway Patterns
- Meta/Facebook — GraphQL, TAO, Thrift
- Google — Protocol Buffers, SLO/SLI, Cloud Endpoints
- Microsoft — Azure API Management, MSAL, Long-polling
- Netflix — API Gateway (Zuul), Circuit Breaker (Hystrix/Resilience4j), gRPC, Chaos
- Stripe — Idempotency Keys, API Versioning, Webhook Security
- Cross-Company Staff Engineer Question Bank

---

## Amazon — Distributed Systems and API Gateway Patterns

### API Gateway and Request Routing
Amazon API Gateway sits in front of all Lambda and EC2-backed APIs. Key design:
- Throttling at the stage level (10,000 RPS default per account)
- Burst limit (5,000 concurrent requests) for handling traffic spikes
- Usage plans per API key for B2B customers
- Request/response transformation in API Gateway (without Lambda) using VTL templates

### SQS Idempotency for Distributed Operations
SQS delivers messages at-least-once. Every SQS consumer must be idempotent.
Amazon's standard: use the SQS message ID as the idempotency key in a DynamoDB
conditional write.

```java
// Amazon-style idempotent SQS consumer
@SqsListener("order-events")
public void processOrder(OrderEvent event, @Header("MessageId") String messageId) {
    try {
        // DynamoDB conditional write — fails if messageId already exists
        dynamoDB.putItem(PutItemRequest.builder()
            .tableName("processed-messages")
            .item(Map.of("messageId", AttributeValue.fromS(messageId)))
            .conditionExpression("attribute_not_exists(messageId)")
            .build());
    } catch (ConditionalCheckFailedException e) {
        // Already processed — skip
        return;
    }
    orderService.process(event); // Now safe to execute once
}
```

### DynamoDB Single-Table Design
Amazon's internal APIs often use DynamoDB with single-table design. The pattern:
- `PK = USER#userId`, `SK = ORDER#orderId` for user-order relationships
- `PK = ORDER#orderId`, `SK = METADATA` for order details
- GSI on `GSI1PK = STATUS#pending`, `GSI1SK = createdAt` for status queries

**Interview question framing:** "Design a DynamoDB schema for an order management
system that can query: all orders for a user, all pending orders sorted by date,
and fetch a single order by ID — all in O(1) or O(log n) time."

---

## Meta/Facebook — GraphQL, TAO, Thrift

### Why Facebook Invented GraphQL
REST APIs have an over-fetching/under-fetching problem. The Facebook mobile app
in 2012 was making dozens of REST API calls to render a single news feed item
(user info, posts, comments, likes, reactions — each a separate request).
GraphQL was invented to let the client declare exactly what data it needs in one request.

```graphql
# Client declares what it needs — no more, no less
query {
  user(id: "123") {
    name
    profilePicture(size: SMALL)
    friends(first: 5) {
      name
    }
    recentPosts(limit: 3) {
      title
      likeCount
    }
  }
}
```

**When to use GraphQL over REST:** Multiple client types with different data needs
(mobile vs desktop), complex relational data, frequent API changes that would
require API versioning in REST.

**When NOT to use GraphQL:** Simple CRUD APIs, file uploads, APIs consumed by
simple clients, when caching is critical (GraphQL POST requests can't be cached
by CDN).

### TAO — The Association and Object Graph Cache
Facebook's data layer is not a traditional database. TAO is a distributed cache
built on top of MySQL that stores objects (users, posts) and associations
(friendships, likes). Key property: consistent reads guaranteed within 1 second.
Teaches the lesson: at Facebook scale, even consistency is a product decision,
not a database guarantee.

### Thrift for Internal Service Communication
Before gRPC existed, Facebook built Thrift for internal RPC between services.
Key advantage over REST: binary encoding (smaller payload), strict type safety,
code generation for client SDKs in any language.

---

## Google — Protocol Buffers, SLO/SLI, Cloud Endpoints

### Protocol Buffers (protobuf) and gRPC
Google uses protobuf for almost all internal service communication and many
external APIs (Google Maps, Cloud APIs).

```protobuf
syntax = "proto3";

service ProductService {
  rpc CreateProduct(CreateProductRequest) returns (Product);
  rpc GetProduct(GetProductRequest) returns (Product);
  rpc ListProducts(ListProductsRequest) returns (ListProductsResponse);
}

message CreateProductRequest {
  string name = 1;
  double price = 2;
  int32 stock_quantity = 3;
}
```

**REST vs gRPC trade-offs:**

| Aspect | REST + JSON | gRPC + protobuf |
|--------|-----------|----------------|
| Payload size | Larger (text JSON) | Smaller (binary) |
| Type safety | No — strings can be wrong | Yes — schema enforced |
| Browser support | Native | Requires gRPC-Web proxy |
| Human readability | Readable in curl | Requires protobuf tools |
| Streaming | SSE or WebSocket workarounds | Native bidirectional streaming |
| Netflix moved to gRPC | ← for internal service-to-service | ← reason: 8x bandwidth reduction |

### SLO/SLI/SLA Definitions (Google Site Reliability Engineering)
- **SLI (Service Level Indicator):** A measurable metric. "P99 latency for /products GET"
- **SLO (Service Level Objective):** Internal target. "P99 latency < 200ms for 99.9% of requests"
- **SLA (Service Level Agreement):** External contractual commitment with financial penalties

Spring Boot + Micrometer SLI implementation:
```java
// Measure P99 latency as an SLI
@GetMapping("/products/{id}")
@Timed(value = "api.product.get", percentiles = {0.5, 0.95, 0.99})
public ProductResponse getProduct(@PathVariable Long id) {
    return productService.findById(id);
}
// Exposes: api.product.get.p99, api.product.get.p95 in metrics endpoint
```

---

## Microsoft — Azure API Management, MSAL, Long-polling

### Azure API Management (APIM) Patterns
APIM is Microsoft's API gateway. Key patterns used in enterprise Spring Boot APIs:
- **Policy-based transformation:** Add headers, validate JWT, throttle — without
  touching the Spring Boot code
- **Backend health monitoring:** APIM health-checks each Spring Boot instance
- **Developer portal:** Auto-generates API documentation from OpenAPI spec

### MSAL and OAuth2 in Enterprise
Microsoft's MSAL library implements OAuth2 + OIDC for enterprise identity (Azure AD).
Key difference from public OAuth2: enterprise tokens carry AAD group claims
and service principal identities.

```java
// Spring Security + Azure AD / MSAL integration
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                azureAdJwtConverter()  // Converts AAD roles claim → Spring GrantedAuthority
            ))
        );
        return http.build();
    }
}
```

### Long-polling vs Webhooks vs WebSockets
Microsoft Teams and Outlook use all three. Choice guide:

| Pattern | Use when | Problem |
|---------|---------|---------|
| Polling | Simple, client controls timing | Server load, latency |
| Long-polling | Near-real-time without WebSocket complexity | Ties up server threads |
| Webhooks | Server-to-server events, client is a server | Client needs public HTTPS endpoint |
| WebSockets | Real-time bidirectional (chat, live collab) | More complex connection management |
| SSE (Server-Sent Events) | Server pushes to browser, one direction | Browser only, no binary |

---

## Netflix — API Gateway, Circuit Breaker, gRPC, Chaos Engineering

### Zuul / API Gateway Architecture
Netflix's API Gateway (Zuul, now moved to Spring Cloud Gateway) handles:
- JWT validation before requests reach microservices
- Rate limiting per user, per device type
- A/B testing via routing (10% of requests → new service version)
- Request aggregation (fan-out to multiple services, fan-in response)

```java
// Spring Cloud Gateway JWT validation filter
@Component
public class JWTValidationFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange.getRequest());
        if (token == null || !jwtUtil.isValid(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        // Forward validated claims to downstream services as headers
        ServerHttpRequest mutated = exchange.getRequest().mutate()
            .header("X-User-Id", jwtUtil.extractUserId(token))
            .header("X-User-Roles", jwtUtil.extractRoles(token))
            .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }
}
```

### Resilience4j Circuit Breaker (Hystrix successor)
Netflix invented the circuit breaker pattern. When a downstream service is failing,
stop trying to call it — fail fast and return a fallback response.

```java
@Service
public class RecommendationService {

    @CircuitBreaker(name = "recommendations", fallbackMethod = "defaultRecommendations")
    @TimeLimiter(name = "recommendations")
    public CompletableFuture<List<Movie>> getRecommendations(String userId) {
        return CompletableFuture.supplyAsync(() ->
            recommendationClient.get(userId)  // Calls downstream ML service
        );
    }

    // Fallback: return popular movies when circuit is open
    public CompletableFuture<List<Movie>> defaultRecommendations(String userId, Exception ex) {
        return CompletableFuture.supplyAsync(() -> movieRepo.findTopByPopularity(20));
    }
}
```

Circuit states: CLOSED (normal) → OPEN (failing, use fallback) → HALF-OPEN (testing recovery)

### Chaos Engineering (Netflix Chaos Monkey)
Netflix deliberately kills random service instances in production to verify resilience.
This forces the engineering culture to build systems that tolerate failure rather than
assuming reliability. The principle: failures in production are inevitable — practice
surviving them in a controlled way.

**Staff engineer interview question:** "How would you design a deployment process
that ensures zero-downtime for a Spring Boot API? What are the failure modes of
a rolling deployment and how do you detect them before they affect users?"

---

## Stripe — Idempotency Keys, API Versioning, Webhook Security

### Stripe's API Design Principles (widely regarded as the gold standard)

1. **Idempotency keys on all POST endpoints.** Client generates a UUID, sends it
   as `Idempotency-Key` header. Stripe caches the response for 24 hours. Same key
   = same response, no duplicate charge.

2. **Date-based API versioning.** `Stripe-Version: 2024-01-15` in the header.
   Each version is immutable — Stripe never changes the behaviour of a past version.
   Customers control when to upgrade.

3. **RFC 7807 error format** (before it was RFC 7807 — Stripe invented this pattern).
   Every error has `type`, `code`, `message`, `param` fields.

4. **Webhook signature verification.** Every webhook is signed with HMAC-SHA256.
   Clients must verify the signature before processing.

```java
// Stripe-style webhook verification in Spring Boot
@PostMapping("/webhooks/stripe")
public ResponseEntity<Void> handleWebhook(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String signature) {

    // Verify HMAC-SHA256 signature
    String secret = stripeWebhookSecret;
    String expectedSig = "sha256=" + hmacSha256(secret, payload);

    if (!MessageDigest.isEqual(
            expectedSig.getBytes(StandardCharsets.UTF_8),
            signature.getBytes(StandardCharsets.UTF_8))) {
        return ResponseEntity.status(400).build();
    }

    // Safe to process
    processStripeEvent(payload);
    return ResponseEntity.ok().build();
}
```

**Why timing-safe comparison (`MessageDigest.isEqual`) instead of `equals()`?**
String `equals()` short-circuits on the first mismatch, creating a timing oracle.
An attacker can measure response times to guess the correct signature one byte at
a time. `MessageDigest.isEqual` always compares all bytes regardless of mismatch position.

---

## Cross-Company Staff Engineer Question Bank

These are real-style questions for Amazon/Meta/Google/Microsoft/Netflix/Stripe
Staff Engineer interviews. Each requires a 15–30 minute whiteboard discussion.

### System Design — Authentication

**Design a JWT authentication system for a platform with 100M users.**
Key discussion points:
- Token storage: HttpOnly cookie (CSRF risk) vs localStorage (XSS risk) — why HttpOnly wins
- Access token lifetime: 15 minutes (frequent refresh) vs 1 hour (less requests)
- Refresh token rotation: each use issues a new refresh token (detect token theft)
- Token revocation: stateless JWT can't be revoked — options: short lifetime, token blacklist in Redis, opaque tokens for critical operations
- RS256 vs HS256: RS256 allows public key distribution to microservices without sharing secret
- At 100M users: Redis cluster for token blacklist, horizontal Auth Service, CDN for public keys

**Design a rate limiter for a public API.**
Key discussion points:
- Fixed window: simple but burst at window edge (allow 200 req in 2 seconds straddling window)
- Sliding window: accurate but memory-intensive
- Token bucket: allows bursts, smooth sustained rate
- Redis Lua script for atomic increment+expire
- Distributed rate limiting: per-node vs global counter (network overhead trade-off)
- Fail-open vs fail-closed when Redis is unavailable (Stripe answer: fail open + log)

### System Design — API Architecture

**Netflix uses gRPC for internal services but REST for client APIs. Why?**
Internal (gRPC): binary encoding saves 8x bandwidth at Netflix's scale,
strict schema prevents contract breakage, native streaming for recommendation feeds.
External (REST): browsers can't use gRPC natively, REST is cacheable at CDN layer,
REST errors are human-readable (better DX), existing client SDKs.

**How would you design Stripe's idempotency key system?**
- Client generates UUIDs (never reuse keys across different requests)
- Stripe stores: key → (status, response, expiry) in Redis with 24h TTL
- If in-flight: return 409 Conflict with "another request is processing this key"
- If completed: return original response immediately (never re-execute)
- If expired: treat as new request (24h window)
- Database: idempotency_keys table for durability beyond Redis TTL

### System Design — Resilience

**How do you design a payment API that is safe to retry?**
Every payment endpoint must be idempotent. The pattern:
1. Client generates a UUID before calling
2. Server stores UUID + result atomically (use DB transaction or Redis SET NX)
3. Second call with same UUID returns cached result
4. Distinguish between "charge succeeded" and "charge is in progress" states
5. Add `If-Match: etag` for update operations (optimistic concurrency)

**Your API is receiving 10x normal traffic. Walk through what breaks first.**
1. Application thread pool exhaustion (Tomcat default: 200 threads)
2. Database connection pool exhaustion (HikariCP: 10 connections by default)
3. Database CPU / lock contention
4. JVM GC pressure (heap growth from request objects)
5. Downstream service cascading failure (if you call external APIs)
Order of mitigation: rate limit at gateway → scale horizontally → add circuit breaker → cache read-heavy data

### Amazon Leadership Principles in Technical Answers

When answering at Amazon, frame answers with LP language:
- "Customer Obsession": "The client's retry logic is affected by our 5xx vs 4xx distinction"
- "Dive Deep": "Let me walk through exactly what happens in the Hikari pool under load"
- "Bias for Action": "I would instrument this first, then optimize based on data"
- "Frugality": "This solution uses Redis which adds operational cost — the simpler alternative is..."
