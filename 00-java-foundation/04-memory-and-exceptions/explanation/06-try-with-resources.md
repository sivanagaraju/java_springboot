# Try-With-Resources

Managing external resources (Database connections, Network sockets, File streams, Redis connections) is complex. If a developer forgets to close a resource, the system leaks memory descriptors until crash.

Java 7 fixed this via `try-with-resources`.

By directly declaring the resource within the `try(...)` clause, Java automatically invokes `.close()`.

```java
try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    // Read the file
    System.out.println(reader.readLine());
    
} catch (IOException e) {
    System.out.println("File read failed...");
}
// The compiler generates the final memory closure logic during compilation.
```

## `java.lang.AutoCloseable`
For this logic to work, the resource object must implement the `AutoCloseable` interface.

```java
public class RedisConnection implements AutoCloseable {
    
    public RedisConnection() {
        System.out.println("Connecting to Redis...");
    }

    public void ping() {
        System.out.println("PONG");
    }

    @Override
    public void close() {
        System.out.println("Disconnecting from Redis... TCP socket closed.");
    }
}
```

If we use this class in a `try-with-resources` block:

```java
try (RedisConnection redis = new RedisConnection()) {
    redis.ping();
}
```

The output will be:
```text
Connecting to Redis...
PONG
Disconnecting from Redis... TCP socket closed.
```

The resource is guaranteed to close even if `redis.ping()` throws a `RuntimeException`. This completely eliminates resource leaks.
