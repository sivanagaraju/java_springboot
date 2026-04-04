# JWT & OAuth2 Deep Dive Reference

## Contents

- JWT Structure and Cryptography
- JWT Flow (Login to Protected Request)
- Signing Algorithms: HS256 vs RS256
- Token Lifecycle and Refresh Strategy
- JWT Security Vulnerabilities
- OAuth2 Grant Types
- Spring Boot JWT Implementation
- Staff Engineer JWT Questions

---

## JWT Structure and Cryptography

A JWT has three Base64URL-encoded parts separated by dots:

```
eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ1c2VyXzEyMyIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE3MDU1NzY0MDAsImV4cCI6MTcwNTU3NzMwMH0.SIGNATURE
```

**Header** (algorithm declaration):
```json
{"alg": "RS256", "typ": "JWT"}
```

**Payload** (claims — do NOT store sensitive data here, it is base64-encoded NOT encrypted):
```json
{
  "sub": "user_123",           // Subject — who this token is for
  "roles": ["ROLE_USER"],      // Custom claim — application-specific
  "iss": "https://auth.example.com",  // Issuer
  "aud": "api.example.com",    // Audience — who should accept this token
  "iat": 1705576400,           // Issued at (Unix timestamp)
  "exp": 1705577300,           // Expires at (Unix timestamp)
  "jti": "unique-token-id"     // JWT ID — prevents replay attacks
}
```

**Signature** (cryptographic proof):
For HS256: `HMAC-SHA256(base64url(header) + "." + base64url(payload), secret)`
For RS256: `RSA-SHA256(base64url(header) + "." + base64url(payload), privateKey)`

**Critical fact:** The payload is NOT encrypted — it is only encoded. Anyone with
the token can base64-decode and read the payload. Never store: passwords, PII
(email, phone), credit card numbers, or secrets in JWT claims.

---

## JWT Flow (Login to Protected Request)

```
LOGIN FLOW:
──────────
Client                           Auth Service                    Database
  │                                    │                             │
  │── POST /auth/login ──────────────►│                             │
  │   {email, password}               │──SELECT user WHERE email ──►│
  │                                   │◄── {hash, roles, id} ───────│
  │                                   │ BCrypt.verify(password, hash)│
  │                                   │ Generate JWT (RS256, 15min)  │
  │                                   │ Generate Refresh Token (7d)  │
  │                                   │ Store refresh in DB          │
  │◄── 200 OK ────────────────────────│                             │
  │   {accessToken, refreshToken}     │                             │

AUTHENTICATED REQUEST FLOW:
───────────────────────────
Client                     API Gateway / JWT Filter              Protected API
  │                                    │                             │
  │── GET /api/products ──────────────►│                             │
  │   Authorization: Bearer {jwt}      │ Verify JWT signature        │
  │                                   │ (public key, no DB lookup)  │
  │                                   │ Check expiry                 │
  │                                   │ Extract claims               │
  │                                   │──────────────────────────►│
  │                                   │                 │ Process request
  │◄──────────────────────────────────│◄────────────────│
  │   200 OK + products               │                 │

TOKEN REFRESH FLOW:
───────────────────
Client                          Auth Service                    Database
  │                                    │                             │
  │── POST /auth/refresh ─────────────►│                             │
  │   {refreshToken}                   │── SELECT WHERE token ──────►│
  │                                   │◄── {userId, expiry, rotated}─│
  │                                   │ Validate not expired          │
  │                                   │ Validate not already rotated  │
  │                                   │ Mark old token as rotated     │
  │                                   │ Issue new refresh token       │
  │                                   │ Issue new access token        │
  │◄── 200 OK ────────────────────────│                             │
  │   {newAccessToken, newRefreshToken}│                             │
```

---

## Signing Algorithms: HS256 vs RS256

| Property | HS256 | RS256 |
|----------|-------|-------|
| Type | Symmetric (shared secret) | Asymmetric (public/private key pair) |
| Secret distribution | Single secret shared by all verifiers | Private key stays on Auth Service; public key shared freely |
| Verification | Any service with the secret | Any service with the public key (safe to expose) |
| Key compromise | ALL tokens potentially compromised | Only private key compromise is catastrophic |
| Key rotation | Requires coordinating secret update across all services | Rotate private key; publish new public key (JWKS endpoint) |
| Use when | Single service ecosystem | Microservices; external token consumers |

**Standard pattern for microservices:** RS256 with a JWKS endpoint.

```java
// Spring Boot RS256 JWT verification via JWKS
@Bean
public JwtDecoder jwtDecoder() {
    // Fetch public keys from Auth Service's JWKS endpoint
    // Spring rotates keys automatically when Auth Service publishes new ones
    return NimbusJwtDecoder.withJwkSetUri("https://auth.example.com/.well-known/jwks.json")
        .build();
}
```

The `/.well-known/jwks.json` endpoint (standard URL from RFC 7517) returns:
```json
{
  "keys": [{
    "kty": "RSA",
    "use": "sig",
    "kid": "key-2024-01",
    "n": "...",
    "e": "AQAB"
  }]
}
```

---

## Token Lifecycle and Refresh Strategy

### Recommended Lifetimes
- Access token: 15 minutes (short — limits exposure if stolen)
- Refresh token: 7 days (longer — balances UX with security)
- Remember-me refresh token: 30 days (stored in HttpOnly cookie)

### Refresh Token Rotation (Netflix, Stripe pattern)
Every time a refresh token is used, it is invalidated and a new one is issued.
If an attacker steals and uses a refresh token, the legitimate user's next refresh
attempt will fail (their token was rotated by the attacker), alerting the system.

```java
@Transactional
public TokenPair refresh(String refreshToken) {
    RefreshToken stored = refreshRepo.findByToken(refreshToken)
        .orElseThrow(() -> new InvalidTokenException("Token not found"));

    if (stored.isRotated()) {
        // Token was already used — possible theft detected
        // Revoke ALL refresh tokens for this user (nuclear option)
        refreshRepo.deleteAllByUserId(stored.getUserId());
        securityEventService.logPossibleTokenTheft(stored.getUserId());
        throw new TokenTheftException("Refresh token already used");
    }

    // Rotate: invalidate old, issue new
    stored.setRotated(true);
    refreshRepo.save(stored);

    String newRefresh = generateSecureRandom();
    refreshRepo.save(new RefreshToken(stored.getUserId(), newRefresh, LocalDateTime.now().plusDays(7)));

    String newAccess = jwtUtil.generate(stored.getUserId());
    return new TokenPair(newAccess, newRefresh);
}
```

---

## JWT Security Vulnerabilities

### 1. Algorithm Confusion Attack (alg:none)
Early JWT libraries accepted `"alg": "none"` in the header, meaning no signature
required. An attacker could forge any token.

Fix: Always specify allowed algorithms explicitly:
```java
JwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey)
    .signatureAlgorithm(SignatureAlgorithm.RS256)  // Whitelist only RS256
    .build();
```

### 2. Token Storage: localStorage vs HttpOnly Cookie

```
localStorage:
  + Accessible from JavaScript (easy to use in SPA)
  - Vulnerable to XSS — any injected script can steal the token
  - Not recommended for tokens with elevated privilege

HttpOnly Cookie:
  + Not accessible from JavaScript (XSS cannot steal it)
  - Vulnerable to CSRF (browser auto-sends cookies)
  - Fix: SameSite=Strict cookie attribute + CSRF token for state-changing requests
  + Recommended for high-security contexts (banking, admin panels)
```

Stripe's approach: access token in memory (JavaScript variable), refresh token
in HttpOnly cookie. Memory clears on page close (automatic logout). Cookie
is used to get a new access token on page load.

### 3. JWT Expiry Not Checked
```java
// WRONG — does not check expiry
Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

// RIGHT — Spring Security JWT decoder checks expiry automatically
// Or if using jjwt directly:
Claims claims = Jwts.parserBuilder()
    .setSigningKey(secret)
    .build()
    .parseClaimsJws(token)  // throws ExpiredJwtException if exp is in the past
    .getBody();
```

### 4. Missing `aud` Claim Validation
A JWT issued for service A could be used at service B if `aud` is not validated.

```java
// Validate audience claim
@Bean
public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer("https://auth.example.com"));
    return decoder;
}
```

---

## OAuth2 Grant Types

| Grant Type | Use case | Flow |
|-----------|---------|------|
| **Authorization Code** | Web apps, mobile apps with server | User → Auth Server → Code → Token |
| **Authorization Code + PKCE** | SPAs, mobile (no client secret) | Same + code challenge prevents interception |
| **Client Credentials** | M2M (service-to-service) | Client secret → Token (no user involved) |
| **Device Code** | TVs, IoT, CLI tools | Polling + device code (no browser) |
| ~~Implicit~~ | Deprecated — use Auth Code + PKCE | Do not use |
| ~~Password~~ | Deprecated — use Auth Code | Do not use |

**Client Credentials in microservices:**
```java
// Service-to-service OAuth2 (Client Credentials)
@Bean
public WebClient internalApiClient(OAuth2AuthorizedClientManager clientManager) {
    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
        new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientManager);
    oauth2.setDefaultClientRegistrationId("inventory-service");
    return WebClient.builder().apply(oauth2.oauth2Configuration()).build();
}

// application.properties
// spring.security.oauth2.client.registration.inventory-service.client-id=...
// spring.security.oauth2.client.registration.inventory-service.authorization-grant-type=client_credentials
// spring.security.oauth2.client.registration.inventory-service.scope=inventory:read
```

---

## Staff Engineer JWT Questions

**Q: Design a JWT authentication system for 100M users. What breaks first as you scale?**

Start with the stateless property of JWT — this is the key advantage at scale.
No session lookup per request. But three things break as scale increases:

1. **Token revocation:** Stateless means you cannot revoke a 15-minute access token.
   Options: (a) short lifetime + refresh token rotation, (b) Redis token blacklist
   (but now you have a stateful distributed system), (c) make auth decisions in the
   gateway so the token only needs to last until the gateway validates it.

2. **Public key distribution:** At 100M users, your JWKS endpoint gets hit on every
   new gateway instance startup. Cache public keys aggressively. Rotate keys with a
   24-hour overlap so old tokens remain valid during rotation.

3. **Refresh token storage:** 100M users × potentially 5 devices × refresh token =
   500M rows. Use Redis with TTL, not a relational DB table. Partition by userId.

**Q: A user reports they were logged out unexpectedly after changing their password on another device. Why and how do you fix it?**

Changing password doesn't invalidate existing JWTs (they're stateless). The user
wasn't logged out — their old tokens from other devices are still valid until they
expire. This is a known JWT trade-off. Fix options:
(a) Include a `passwordVersion` claim in JWT; check against DB on critical operations
(b) Maintain a per-user token generation counter; tokens with old counter are rejected
(c) Use short-lived access tokens + forced refresh after password change invalidates refresh tokens

**Q: Why does Stripe use both URI versioning (/v1/) AND date-based versioning (Stripe-Version header)?**

URI versioning: breaking changes to core API semantics — resources moved,
deleted, fundamentally changed. These break existing integrations and require
a migration path. Stripe gives years of deprecation notice.

Date-based header versioning: additive or minor breaking changes — new optional
fields, changed defaults, removed deprecated fields. Stripe locks your version
at your first API call date. You upgrade by updating the header and updating
your integration. This lets Stripe iterate rapidly without forcing everyone
to migrate for small changes.
