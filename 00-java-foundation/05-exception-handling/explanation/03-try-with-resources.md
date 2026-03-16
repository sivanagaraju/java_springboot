# try-with-resources: Automatic Resource Management

## The Problem: Resource Leaks

Every time you open a file, database connection, or network socket, the OS allocates a **file descriptor**. If you forget to close it, you leak that descriptor. Leak enough of them and your application crashes with `Too many open files`.

```
NAIVE APPROACH (error-prone):
┌──────────────────────────────────────────────────┐
│  FileInputStream fis = new FileInputStream(path);│
│  // ... use fis ...                              │
│  fis.close();  ← What if an exception occurs     │
│                  before this line? RESOURCE LEAK! │
└──────────────────────────────────────────────────┘

OLD FIX (verbose):
┌──────────────────────────────────────────────────┐
│  FileInputStream fis = null;                      │
│  try {                                            │
│      fis = new FileInputStream(path);            │
│      // use fis                                   │
│  } finally {                                      │
│      if (fis != null) {                          │
│          try { fis.close(); }                    │
│          catch (IOException e) { /* suppress */ } │
│      }                                            │
│  }                                                │
│  // 10 lines of boilerplate for ONE resource!     │
└──────────────────────────────────────────────────┘

MODERN (Java 7+):
┌──────────────────────────────────────────────────┐
│  try (var fis = new FileInputStream(path)) {     │
│      // use fis                                   │
│  } // ← fis.close() called AUTOMATICALLY here    │
│  // Even if exception occurs!                    │
└──────────────────────────────────────────────────┘
```

## How It Works: AutoCloseable

Any class that implements `AutoCloseable` (or its parent `Closeable`) can be used in try-with-resources:

```java
public interface AutoCloseable {
    void close() throws Exception;
}
```

**Close order**: Resources are closed in **reverse declaration order**:

```java
try (
    var conn = getConnection();       // opened 1st, closed 3rd
    var stmt = conn.prepareStatement(sql);  // opened 2nd, closed 2nd
    var rs   = stmt.executeQuery()    // opened 3rd, closed 1st
) {
    // use rs, stmt, conn
}
// Close order: rs → stmt → conn (LIFO — like a stack)
```

```
CLOSE ORDER (reverse of declaration):
┌───────────────────────────────────────────────────────┐
│  OPEN:    conn ──▶ stmt ──▶ rs                         │
│  CLOSE:   rs   ──▶ stmt ──▶ conn   (reverse / LIFO)   │
│                                                        │
│  This ensures child resources close before parents.   │
│  You can't close a Connection while a Statement       │
│  is still using it.                                    │
└───────────────────────────────────────────────────────┘
```

## Suppressed Exceptions

What if BOTH the try body and `close()` throw exceptions?

```java
try (var res = new FailingResource()) {
    throw new IOException("body failed");
}
// close() also throws: RuntimeException("close failed")
```

```
EXCEPTION FLOW:
┌──────────────────────────────────────────────────┐
│  try body throws:  IOException("body failed")    │
│  close() throws:   RuntimeException("close failed") │
│                                                  │
│  PRIMARY exception:   IOException("body failed") │
│  SUPPRESSED exception: RuntimeException("close") │
│                                                  │
│  Access suppressed:                              │
│    e.getSuppressed()  → [RuntimeException]       │
└──────────────────────────────────────────────────┘
```

The body exception is the **primary** (thrown to caller). The close exception is **suppressed** (attached via `addSuppressed()`). This is much better than the old finally approach where the close exception would replace the body exception entirely.

## Python Comparison

```python
# Python context manager = Java try-with-resources
with open("file.txt") as f:
    data = f.read()
# f.close() called automatically — IDENTICAL concept

# Python custom context manager:
class MyResource:
    def __enter__(self):
        return self
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.close()

# Java equivalent:
# class MyResource implements AutoCloseable {
#     public void close() { ... }
# }
```

---

## Interview Questions

**Q1: What interface must a class implement to be used in try-with-resources?**
> `AutoCloseable` (defines `close() throws Exception`). Its sub-interface `Closeable` (from `java.io`) restricts to `close() throws IOException`. All I/O classes, JDBC connections, and Spring-managed resources implement one of these.

**Q2: What are suppressed exceptions and why do they matter?**
> When the try body throws exception A, and `close()` then throws exception B, exception B is not lost — it's attached to A via `addSuppressed()`. Before try-with-resources (in manual finally blocks), exception B would **replace** exception A entirely, losing the root cause. Suppressed exceptions prevent this data loss.

**Q3: In what order are resources closed in try-with-resources?**
> Reverse declaration order (LIFO). If you declare `conn`, then `stmt`, then `rs`, close order is `rs` → `stmt` → `conn`. This ensures child resources are released before their parents, which is critical for database resources.
