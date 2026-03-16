# Exception Handling

## Exception Hierarchy
- Throwable (root of ALL throwables)
  - Error (JVM-level, DO NOT catch)
    - OutOfMemoryError (Heap exhausted)
    - StackOverflowError (Infinite recursion)
    - VirtualMachineError
    - LinkageError (ClassDef changed after compile)
  - Exception (Application-level, DO catch)
    - Checked Exceptions (Compiler FORCES handling)
      - IOException (file/network I/O failure)
      - SQLException (database query failure)
      - ClassNotFoundException
      - Must: try-catch OR declare throws
    - RuntimeException (Unchecked — like Python)
      - NullPointerException (the billion-dollar mistake)
      - ArrayIndexOutOfBoundsException
      - IllegalArgumentException (bad method input)
      - IllegalStateException (wrong object state)
      - ClassCastException
      - ArithmeticException (division by zero)
      - UnsupportedOperationException

## Checked vs Unchecked Decision
- Checked: caller CAN reasonably recover (retry, fallback)
  - File not found → prompt user for correct path
  - Network timeout → retry with backoff
- Unchecked: programming error, should be fixed in code
  - NullPointer → fix the null check
  - ArrayIndexOutOfBounds → fix the loop

## Custom Exceptions
- Business domain exceptions
  - InsufficientFundsException extends RuntimeException
  - OrderNotFoundException extends RuntimeException
- When to use checked vs unchecked
  - Spring convention: almost always unchecked (RuntimeException)
  - Checked exceptions break functional pipelines (Streams)

## try-catch-finally
- try: code that might throw
- catch: handle specific exception types (most specific first)
- finally: ALWAYS runs (cleanup, release resources)
- Multi-catch: catch (IOException | SQLException e)

## try-with-resources (Java 7+)
- AutoCloseable interface = Python context manager
- Resources closed automatically in reverse declaration order
- Suppressed exceptions (close fails after body fails)
- Python equivalent: with open("file") as f:

## throws vs throw
- throw: CREATE and THROW an exception instance
  - throw new IllegalArgumentException("msg")
- throws: DECLARE that a method might throw (checked only)
  - public void read() throws IOException
- Python has no "throws" — all exceptions are implicit
