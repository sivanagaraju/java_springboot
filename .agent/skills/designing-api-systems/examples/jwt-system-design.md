# JWT Authentication System — Staff Engineer Design Answer

> Gold-standard example of how to write system design content at Staff Engineer level.
> Notice: opens with the problem, shows HTTP contract first, then implementation,
> then scale considerations, then failure modes, then interview questions.

## Contents

- The Problem JWT Solves
- Python Bridge
- System Architecture (C4Container)
- HTTP Contract (what the client sees)
- Spring Boot Implementation
- Production Failure Modes
- Scale Considerations (100M users)
- Interview Questions (Senior + Staff Engineer)

---

## The Problem JWT Solves

Before JWT, every microservice that needed to know "who is this user?" had to call
the Auth Service to look up the session. At Netflix's scale (200M+ users, billions
of requests per day), this meant the Auth Service was the bottleneck for every
single API call in the system. Every page load, every search, every play button —
all waited for one service.

JWT shifts this model. The Auth Service issues a cryptographically signed token once
at login. Every downstream service can verify the token's authenticity using a public
key — no network call, no database lookup. A signature check takes microseconds.
A network call to a centralised session service takes milliseconds and adds a single
point of failure. At Netflix's scale, that difference is measured in billions of
dollars of infrastructure cost.

The trade-off: tokens cannot be revoked before they expire. If a user's account is
compromised, the attacker's stolen token remains valid for up to 15 minutes. This is
why access tokens are kept short-lived (15 minutes) and why critical operations
(password change, payment) should validate against the database even if the JWT is valid.

---

## Python Bridge

> **Python FastAPI equivalent:** `python-jose` library with `JWT.decode()`, or
> `PyJWT` for standalone token operations. FastAPI's `Depends(get_current_user)`
> is the equivalent of Spring Security's automatic JWT filter.

```python
# FastAPI JWT validation
from jose import jwt, JWTError
from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")

async def get_current_user(token: str = Depends(oauth2_scheme)):
    try:
        payload = jwt.decode(token, PUBLIC_KEY, algorithms=["RS256"])
        user_id = payload.get("sub")
        if user_id is None:
            raise HTTPException(status_code=401)
        return user_id
    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid token")
```

```java
// Spring Security JWT validation (automatic via filter chain)
// Configure once — Spring applies to every request automatically
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
    return http.build();
}

// No explicit JWT validation code needed in controllers — Spring does it
@GetMapping("/api/products")
@PreAuthorize("hasRole('USER')")
public List<ProductResponse> listProducts() {
    // Only reachable if JWT is valid and has ROLE_USER
    return productService.findAll();
}
```

| Concept | Python/FastAPI | Java/Spring |
|---------|---------------|-------------|
| JWT decode | `jwt.decode(token, key, algorithms=[...])` | Spring Security JwtDecoder (auto) |
| Current user extraction | `Depends(get_current_user)` | `@AuthenticationPrincipal Jwt jwt` |
| Token validation | Manual `try/except` in dependency | Automatic in Security filter chain |
| Role check | `if "admin" not in payload["roles"]` | `@PreAuthorize("hasRole('ADMIN')")` |
| Invalid token | Raise `HTTPException(401)` | Spring returns 401 automatically |

---

## System Architecture (C4Container)

```mermaid
C4Container
    title JWT Authentication System — Container View

    Person(user, "API Client", "Browser, mobile app, or service")

    System_Boundary(platform, "Spring Boot API Platform") {
        Container(gateway, "API Gateway", "Spring Cloud Gateway", "JWT validation, rate limiting, routing")
        Container(auth, "Auth Service", "Spring Boot 3.2", "Login, token issue, refresh, revocation")
        Container(api, "Product API", "Spring Boot 3.2", "Business logic, JWT claims used for authorization")
        ContainerDb(userdb, "User DB", "PostgreSQL", "User accounts, BCrypt hashes, refresh tokens")
        Container(cache, "Token Cache", "Redis", "Token blacklist, rate limit counters")
    }

    System_Ext(jwks, "JWKS Endpoint", "Auth Service public key distribution")

    Rel(user, gateway, "All requests", "HTTPS + Bearer JWT")
    Rel(gateway, jwks, "Fetch public key on startup", "HTTPS")
    Rel(gateway, auth, "Route /auth/**", "HTTP")
    Rel(gateway, api, "Route /api/** with verified JWT", "HTTP + X-User headers")
    Rel(auth, userdb, "Read/write users and refresh tokens", "JDBC")
    Rel(auth, cache, "Blacklist revoked tokens", "Redis")
    Rel(gateway, cache, "Check token blacklist + rate limits", "Redis")
    Rel(api, userdb, "Read/write business data", "JPA")
```

---

## HTTP Contract (what the client sees)

### Login
```
POST /auth/login
Content-Type: application/json

{"email": "user@example.com", "password": "secret"}

200 OK
Set-Cookie: refresh_token=eyJ...; HttpOnly; Secure; SameSite=Strict; Max-Age=604800
Content-Type: application/json

{
  "access_token": "eyJhbGciOiJSUzI1NiJ9...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

### Authenticated Request
```
GET /api/products
Authorization: Bearer eyJhbGciOiJSUzI1NiJ9...

200 OK
[{"id": 1, "name": "Laptop", "price": 999.99}]
```

### Expired Token
```
GET /api/products
Authorization: Bearer eyJ... (expired)

401 Unauthorized
{
  "type": "https://api.example.com/errors/token-expired",
  "title": "Access Token Expired",
  "status": 401,
  "detail": "Access token expired at 2024-01-15T10:30:00Z. Please refresh."
}
```

### Refresh
```
POST /auth/refresh
Cookie: refresh_token=eyJ...

200 OK
Set-Cookie: refresh_token=eyJ_NEW...; HttpOnly; Secure; SameSite=Strict
{"access_token": "eyJ_NEW_ACCESS...", "expires_in": 900}
```

---

## Spring Boot Implementation

```java
/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : JWTAuthFilter.java                                    ║
 * ║  MODULE : 11-jwt-oauth2 / 01-jwt-deep-dive                      ║
 * ║  GRADLE : ./gradlew :11-jwt-oauth2:bootRun                      ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Extract and validate JWT on every request     ║
 * ║  WHY IT EXISTS  : SecurityFilterChain needs a filter to read    ║
 * ║                   JWT before Spring Security checks permissions ║
 * ║  PYTHON COMPARE : Depends(get_current_user) in FastAPI          ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — JWT Filter Position in Filter Chain            ║
 * ║                                                                  ║
 * ║   HTTP Request                                                   ║
 * ║       │                                                          ║
 * ║       ▼                                                          ║
 * ║   [ SecurityContextPersistenceFilter ]                          ║
 * ║       │                                                          ║
 * ║       ▼                                                          ║
 * ║   [ JWTAuthFilter ]    ← YOU ARE HERE                          ║
 * ║       │ Extract token, validate signature, set Authentication   ║
 * ║       ▼                                                          ║
 * ║   [ AuthorizationFilter ]  ← Checks SecurityContext for roles  ║
 * ║       │                                                          ║
 * ║       ▼                                                          ║
 * ║   [ YourController ]                                             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Skip if no Bearer token — Spring Security will reject if endpoint is protected
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String userId = jwtUtil.extractUserId(token);

            // Only set authentication if not already set (avoid re-processing)
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(userId);

                if (jwtUtil.isValid(token, user)) {
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (ExpiredJwtException e) {
            // Let the request continue — AuthorizationFilter will reject it
            // This gives us a proper 401 response from the filter chain
        } catch (JwtException e) {
            response.setStatus(401);
            return;
        }

        chain.doFilter(request, response);
    }
}
```

---

## Production Failure Modes

| Failure | What the client sees | Root cause | Fix |
|---------|---------------------|-----------|-----|
| Auth Service down | `503 Service Unavailable` on login/refresh | Single point of failure | Multiple Auth Service instances + health check |
| Redis down | Gateway rejects all requests (if blacklist check fails closed) | Redis used for token blacklist lookup | Fail open for blacklist, fail closed for rate limit |
| JWKS endpoint unreachable | Gateway can't validate tokens | Public key fetch failed | Cache public key with long TTL; retry on startup |
| Clock skew between services | `401` on valid tokens (exp check fails) | Server clocks not synchronized | Add 30s leeway in JWT decoder; use NTP |
| Token stolen and used before expiry | Attacker has 15 minutes of access | Stateless JWT can't be instantly revoked | Add stolen token to Redis blacklist; short 15min lifetime |
| Refresh token reuse (rotation violation) | Both legitimate user and attacker locked out | Attacker used refresh token, rotated it | Revoke all user tokens; force re-login |

---

## Interview Questions

### Senior Data/Software Engineer Level

**Q1: What is the purpose of the `exp` claim in JWT, and what happens if two servers
have a 5-minute clock difference?**
> The `exp` (expiration) claim is a Unix timestamp. The JWT decoder compares it to the
> server's current time. If Server A issued a token expiring at 10:30:00 and Server B's
> clock shows 10:30:03, Server B rejects the token as expired even though 3 seconds
> remain. Fix: add a clock skew tolerance of 30–60 seconds in the JWT decoder
> (`JwtDecoderBuilder.clockSkew(Duration.ofSeconds(30))`). More importantly: use NTP
> to keep server clocks synchronized — clock drift is a production incident waiting to happen.

**Q2: A developer proposes storing the user's email in the JWT payload to avoid a
database lookup on every request. What are the trade-offs?**
> Storing email in JWT avoids a DB lookup — good for performance. But: JWT payload is
> base64-encoded, not encrypted — the email is readable by anyone who holds the token.
> More critically, if a user changes their email, every existing JWT has the old email
> until it expires (up to 15 minutes of inconsistency). For high-frequency display
> purposes, the trade-off is acceptable. For authorisation decisions (e.g., "is this
> email verified?"), use a short-lived claim or validate against the database on
> sensitive operations.

### Staff Engineer / System Design Level

**Q3: Design a JWT-based authentication system for a fintech platform with 100M users.
Walk through what breaks first as you scale, and how you'd address each.**

> The stateless property of JWT is the key advantage at scale — no session lookup means
> the Auth Service is not in the hot path of every request. But three things break:
>
> First, **token revocation**. Stateless means you cannot instantly revoke a stolen
> access token. Mitigation: keep access tokens short-lived (15 minutes), implement
> refresh token rotation (detect theft), maintain a Redis blacklist for critical
> revocations (force-logout, account compromise). Accept that the 15-minute window
> is a business risk — quantify it rather than trying to eliminate it.
>
> Second, **public key distribution**. Every Gateway instance fetches the JWKS on
> startup. At 1,000 gateway instances scaling rapidly, the JWKS endpoint becomes a
> target. Mitigation: cache the public key aggressively (1-hour TTL), serve JWKS
> through CDN, rotate keys with 48-hour overlap.
>
> Third, **refresh token storage**. 100M users × 3 devices each = 300M refresh token
> records. A relational DB table with an index on the token string will work up to
> ~50M rows — above that, consider sharding by userId or moving to Redis with TTL.
> Redis is simpler but loses tokens on flush (mitigation: Redis persistence + replica).

**Q4: Netflix uses RS256 for JWTs but stores no user sessions. An actor account is
compromised. The security team wants to revoke access immediately. What are your options
and the trade-offs of each?**

> With stateless RS256 JWTs, immediate revocation requires introducing statefulness.
> Four options, in order of complexity:
>
> 1. **Short lifetime + wait for expiry** — If access tokens are 15 minutes, maximum
>    exposure is 15 minutes. For most cases, this is acceptable. Cheapest solution.
>
> 2. **Redis token blacklist** — Store compromised token JTI (JWT ID) in Redis with TTL
>    matching token expiry. Gateway checks blacklist on every request. Cost: one Redis
>    lookup per request. At Netflix scale (~10M concurrent users), this is significant
>    latency and Redis memory. Acceptable for emergency revocations.
>
> 3. **Per-user token generation counter** — Store `tokenVersion: 3` in the DB.
>    Include it in JWT claims. On every request, verify the JWT's `version` matches the
>    DB value. Increment `tokenVersion` to revoke all existing tokens for a user.
>    Cost: one DB lookup per request — worse than Redis blacklist. But only needed for
>    high-privilege operations or after revocation.
>
> 4. **Opaque tokens for critical operations** — Use JWT for most operations (stateless,
>    fast). For account management, payments, admin actions — require an opaque token
>    that IS validated against the database. Two-tier auth system.
>
> Netflix's actual answer: combination of (1) and (2). Short 15-minute access tokens
> plus emergency Redis blacklist for confirmed compromised accounts. They accept the
> 15-minute risk as the engineering cost of stateless scale.
