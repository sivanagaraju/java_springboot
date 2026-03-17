import java.util.List;
import java.util.Collections;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║              BUILDER PATTERN — Demo                         ║
 * ║  Fluent API for step-by-step immutable object construction  ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * WHAT: Construct complex objects step-by-step with a fluent API.
 * WHY:  Readable construction, validation at build-time, immutability.
 * SPRING: ResponseEntity.ok().body(), UriComponentsBuilder.
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  HttpRequest.builder()                                   │
 * │    .url("https://api.example.com/users")                 │
 * │    .method("GET")          ← each setter returns 'this'  │
 * │    .header("Auth", token)  ← chaining                    │
 * │    .timeout(5000)          ← optional params              │
 * │    .build()                ← validates & creates          │
 * └──────────────────────────────────────────────────────────┘
 */
public class BuilderDemo {

    // ── Immutable HttpRequest built via Builder ────────────────
    static class HttpRequest {
        private final String url;
        private final String method;
        private final List<String> headers;
        private final int timeoutMs;
        private final String body;

        private HttpRequest(Builder b) {
            this.url       = b.url;
            this.method    = b.method;
            this.headers   = Collections.unmodifiableList(b.headers);
            this.timeoutMs = b.timeoutMs;
            this.body      = b.body;
        }

        // Public API
        public String getUrl()         { return url; }
        public String getMethod()      { return method; }
        public List<String> getHeaders() { return headers; }
        public int getTimeoutMs()      { return timeoutMs; }
        public String getBody()        { return body; }

        public static Builder builder() { return new Builder(); }

        @Override
        public String toString() {
            return method + " " + url + " (timeout=" + timeoutMs + "ms, "
                + headers.size() + " headers)";
        }

        /**
         * ┌────────────────────────────────────────────┐
         * │ Builder (inner static class)                │
         * │  - Collects parameters step by step         │
         * │  - Validates in build()                     │
         * │  - Returns immutable HttpRequest             │
         * │                                             │
         * │  builder() → .url() → .method() → .build()  │
         * │    each method returns 'this' for chaining  │
         * └────────────────────────────────────────────┘
         */
        static class Builder {
            private String url;
            private String method = "GET";
            private List<String> headers = new java.util.ArrayList<>();
            private int timeoutMs = 30000;    // default 30s
            private String body;

            public Builder url(String url)           { this.url = url; return this; }
            public Builder method(String method)     { this.method = method; return this; }
            public Builder header(String h)          { this.headers.add(h); return this; }
            public Builder timeout(int ms)           { this.timeoutMs = ms; return this; }
            public Builder body(String body)         { this.body = body; return this; }

            public HttpRequest build() {
                // Validation at build-time!
                if (url == null || url.isBlank()) {
                    throw new IllegalStateException("URL is required");
                }
                if (("POST".equals(method) || "PUT".equals(method)) && body == null) {
                    throw new IllegalStateException(method + " requires a body");
                }
                return new HttpRequest(this);
            }
        }
    }

    // ── MAIN ───────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("═══ BUILDER PATTERN DEMO ═══\n");

        // 1. Basic GET request
        System.out.println("1. Simple GET:");
        HttpRequest get = HttpRequest.builder()
            .url("https://api.example.com/users")
            .header("Accept: application/json")
            .build();
        System.out.println("  " + get);

        // 2. Full POST request with all options
        System.out.println("\n2. Full POST:");
        HttpRequest post = HttpRequest.builder()
            .url("https://api.example.com/users")
            .method("POST")
            .header("Content-Type: application/json")
            .header("Authorization: Bearer token123")
            .timeout(5000)
            .body("{\"name\": \"John\"}")
            .build();
        System.out.println("  " + post);
        System.out.println("  Body: " + post.getBody());
        System.out.println("  Headers: " + post.getHeaders());

        // 3. Validation demo
        System.out.println("\n3. Validation at build-time:");
        try {
            HttpRequest.builder().build(); // no URL!
        } catch (IllegalStateException e) {
            System.out.println("  ✅ Caught: " + e.getMessage());
        }

        try {
            HttpRequest.builder()
                .url("https://api.example.com/data")
                .method("POST")
                .build(); // POST with no body!
        } catch (IllegalStateException e) {
            System.out.println("  ✅ Caught: " + e.getMessage());
        }

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Builder = readable construction + validation + immutability.");
        System.out.println("Each setter returns 'this' for fluent chaining.");
        System.out.println("Spring uses this everywhere: ResponseEntity, UriComponentsBuilder.");
    }
}
