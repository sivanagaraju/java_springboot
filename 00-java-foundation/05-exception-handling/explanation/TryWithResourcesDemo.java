/**
 * ====================================================================
 * FILE    : TryWithResourcesDemo.java
 * MODULE  : 05 — Exception Handling
 * PURPOSE : Demonstrate AutoCloseable, resource lifecycle, suppressed exceptions
 * ====================================================================
 *
 * PYTHON COMPARISON:
 *   Python: with open("file.txt") as f:    ← context manager
 *   Java:   try (var f = new FileReader()) ← try-with-resources
 *
 * RESOURCE LIFECYCLE:
 *
 *   try (Resource r = new Resource()) {
 *       r.use();
 *   }
 *
 *   ┌──────────────────────────────────────────┐
 *   │  1. Resource OPENED (constructor runs)    │
 *   │  2. try body EXECUTES                     │
 *   │  3. Resource CLOSED (close() called auto) │
 *   │  4. catch block runs (if exception)       │
 *   │                                           │
 *   │  NOTE: close() runs BEFORE catch!         │
 *   └──────────────────────────────────────────┘
 *
 * MULTIPLE RESOURCES — CLOSE ORDER (LIFO):
 *
 *   try (R1 a = ...; R2 b = ...; R3 c = ...) { }
 *
 *   OPEN ORDER:   a → b → c
 *   CLOSE ORDER:  c → b → a  (reverse / stack-like)
 *
 *   ┌────┐   ┌────┐   ┌────┐
 *   │ a  │──▶│ b  │──▶│ c  │   OPEN: left to right
 *   └────┘   └────┘   └────┘
 *   ┌────┐   ┌────┐   ┌────┐
 *   │ a  │◀──│ b  │◀──│ c  │   CLOSE: right to left
 *   └────┘   └────┘   └────┘
 *
 * ====================================================================
 */
public class TryWithResourcesDemo {

    // ═══════════════════════════════════════════════════════════════
    // Custom AutoCloseable resource (simulates file/connection)
    // ═══════════════════════════════════════════════════════════════
    //
    // Any class implementing AutoCloseable can be used in
    // try-with-resources. The close() method is called automatically.
    //
    // Python equivalent:
    //   class MyResource:
    //       def __enter__(self): return self
    //       def __exit__(self, *args): self.close()
    //
    static class SimulatedResource implements AutoCloseable {
        private final String name;
        private boolean open;

        public SimulatedResource(String name) {
            this.name = name;
            this.open = true;
            System.out.println("  [" + name + "] OPENED");
        }

        public void doWork() {
            if (!open) throw new IllegalStateException(name + " is already closed!");
            System.out.println("  [" + name + "] doing work...");
        }

        @Override
        public void close() {
            open = false;
            System.out.println("  [" + name + "] CLOSED (auto by try-with-resources)");
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // Resource that fails on close (for suppressed exception demo)
    // ═══════════════════════════════════════════════════════════════
    static class FailingResource implements AutoCloseable {
        private final String name;

        public FailingResource(String name) {
            this.name = name;
            System.out.println("  [" + name + "] OPENED");
        }

        public void doWork() {
            throw new RuntimeException("Body failed in " + name);
        }

        @Override
        public void close() {
            throw new RuntimeException("Close failed in " + name);
        }
    }

    public static void main(String[] args) {

        System.out.println("=== BASIC try-with-resources ===");

        // ── Single resource — auto-closed ───────────────────────────
        //
        // EXECUTION FLOW:
        //   1. new SimulatedResource("DB Connection") → OPENED
        //   2. conn.doWork() → doing work
        //   3. } ← end of try → conn.close() called AUTOMATICALLY
        //
        try (var conn = new SimulatedResource("DB Connection")) {
            conn.doWork();
        }
        System.out.println("  After try block — resource is guaranteed closed\n");

        System.out.println("=== MULTIPLE RESOURCES (close order) ===");

        // ── Three resources — closed in REVERSE order ───────────────
        //
        // OPEN ORDER:   conn → stmt → rs
        // CLOSE ORDER:  rs → stmt → conn  (LIFO)
        //
        //   Why LIFO? The inner resource (result set) DEPENDS on
        //   the outer resource (connection). You must close the
        //   child before the parent.
        //
        try (
            var conn = new SimulatedResource("Connection");
            var stmt = new SimulatedResource("Statement");
            var rs   = new SimulatedResource("ResultSet")
        ) {
            rs.doWork();
        }
        System.out.println();

        System.out.println("=== RESOURCE CLOSED EVEN ON EXCEPTION ===");

        // ── Exception in body — resource still closes! ──────────────
        //
        // FLOW:
        //   1. Resource opened
        //   2. Body throws RuntimeException
        //   3. close() called BEFORE catch block!
        //   4. catch block executes
        //
        try (var res = new SimulatedResource("FileStream")) {
            System.out.println("  About to throw...");
            throw new RuntimeException("Something went wrong!");
        } catch (RuntimeException e) {
            System.out.println("  Caught: " + e.getMessage());
            System.out.println("  (Resource was closed BEFORE this catch ran)");
        }
        System.out.println();

        System.out.println("=== SUPPRESSED EXCEPTIONS ===");

        // ── Both body AND close() throw exceptions ──────────────────
        //
        // EXCEPTION FLOW:
        //   ┌──────────────────────────────────────────────┐
        //   │  Body throws:  RuntimeException("Body failed")│
        //   │  close() throws: RuntimeException("Close failed")│
        //   │                                              │
        //   │  PRIMARY:    "Body failed" ← thrown to caller │
        //   │  SUPPRESSED: "Close failed" ← attached to primary│
        //   │                                              │
        //   │  Access: e.getSuppressed()[0].getMessage()   │
        //   └──────────────────────────────────────────────┘
        //
        try (var res = new FailingResource("FlakeyDB")) {
            res.doWork();
        } catch (RuntimeException e) {
            System.out.println("  Primary exception:    " + e.getMessage());
            Throwable[] suppressed = e.getSuppressed();
            for (Throwable t : suppressed) {
                System.out.println("  Suppressed exception: " + t.getMessage());
            }
        }
        System.out.println();

        System.out.println("=== COMPARISON: OLD vs NEW APPROACH ===");
        System.out.println("  ┌──────────────────────────────────────────────┐");
        System.out.println("  │  OLD (pre-Java 7):                           │");
        System.out.println("  │    Resource r = null;                        │");
        System.out.println("  │    try { r = new Resource(); r.use(); }      │");
        System.out.println("  │    finally {                                 │");
        System.out.println("  │      if (r != null) {                        │");
        System.out.println("  │        try { r.close(); }                    │");
        System.out.println("  │        catch (Exception e) { /* lost! */ }   │");
        System.out.println("  │      }                                       │");
        System.out.println("  │    }                                         │");
        System.out.println("  │    → 10+ lines, exception from close LOST   │");
        System.out.println("  │                                              │");
        System.out.println("  │  NEW (Java 7+):                              │");
        System.out.println("  │    try (var r = new Resource()) { r.use(); } │");
        System.out.println("  │    → 1 line, close exception SUPPRESSED      │");
        System.out.println("  └──────────────────────────────────────────────┘");
    }
}
