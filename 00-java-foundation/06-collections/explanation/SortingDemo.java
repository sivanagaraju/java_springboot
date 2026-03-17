/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           SORTING DEMO — Comparable & Comparator            ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Demonstrates:                                              ║
 * ║  1. Comparable (natural ordering inside class)              ║
 * ║  2. Comparator factory methods (external ordering)          ║
 * ║  3. Multi-level sorting with thenComparing                  ║
 * ║  4. PriorityQueue with custom ordering                      ║
 * ║  5. TreeMap/TreeSet with Comparator                         ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class SortingDemo {

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 1: Comparable — Natural Order Inside Class  │
    // └──────────────────────────────────────────────────────┘

    /**
     * Employee with natural ordering by ID.
     *
     * compareTo() contract:
     * ┌──────────────┬───────────────────────┐
     * │ Return Value │ Meaning               │
     * ├──────────────┼───────────────────────┤
     * │ negative     │ this comes BEFORE other│
     * │ zero         │ this EQUALS other      │
     * │ positive     │ this comes AFTER other │
     * └──────────────┴───────────────────────┘
     *
     * ⚠️ TRAP: Never use subtraction for compareTo!
     *    Integer.MAX_VALUE - (-1) = overflow → wrong result
     *    Always use Integer.compare() or Double.compare()
     */
    static class Employee implements Comparable<Employee> {
        int id;
        String name;
        String department;
        double salary;

        Employee(int id, String name, String department, double salary) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.salary = salary;
        }

        @Override
        public int compareTo(Employee other) {
            // ✅ SAFE: Integer.compare avoids overflow
            return Integer.compare(this.id, other.id);
        }

        @Override
        public String toString() {
            return String.format("Employee{id=%d, name='%s', dept='%s', salary=%.0f}",
                    id, name, department, salary);
        }
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 2: Comparator Factory Methods (Java 8+)    │
    // └──────────────────────────────────────────────────────┘

    /**
     * Comparator composition chain:
     *
     * Input: [Alice/Eng/$130k, Bob/Mkt/$120k, Carol/Eng/$140k, Dave/Mkt/$110k]
     *
     * Step 1: comparing(dept)     → group by department alphabetically
     * Step 2: thenComparing(sal)  → within dept, sort by salary
     * Step 3: .reversed()         → flip everything
     *
     * Result: Mkt/$120k, Mkt/$110k, Eng/$140k, Eng/$130k
     *         ────────────────────  ────────────────────
     *         Mkt first (reversed)  Eng second (reversed)
     */
    static void demonstrateComparatorChaining() {
        System.out.println("═══ Comparator Chaining ═══");

        java.util.List<Employee> employees = java.util.List.of(
            new Employee(3, "Alice", "Engineering", 130000),
            new Employee(1, "Bob", "Marketing", 120000),
            new Employee(4, "Carol", "Engineering", 140000),
            new Employee(2, "Dave", "Marketing", 110000)
        );

        // ── Natural order (by ID, from Comparable) ──
        java.util.List<Employee> byId = new java.util.ArrayList<>(employees);
        java.util.Collections.sort(byId);  // uses compareTo
        System.out.println("By ID (natural): " + byId);

        // ── Single field Comparator ──
        java.util.List<Employee> byName = new java.util.ArrayList<>(employees);
        byName.sort(java.util.Comparator.comparing(e -> e.name));
        System.out.println("By Name: " + byName);

        // ── Multi-level: dept, then salary descending ──
        java.util.List<Employee> multiSort = new java.util.ArrayList<>(employees);
        multiSort.sort(
            java.util.Comparator.comparing((Employee e) -> e.department)
                .thenComparingDouble(e -> e.salary)
                .reversed()
        );
        System.out.println("Dept↓ then Salary↓: " + multiSort);
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 3: PriorityQueue with Custom Comparator    │
    // └──────────────────────────────────────────────────────┘

    /**
     * PriorityQueue is a MIN-HEAP by default.
     *
     * After inserting [30, 10, 20, 5, 15]:
     *
     * Min-heap (default):        Max-heap (reversed):
     *         5                          30
     *        / \                        / \
     *      10   20                    15   20
     *      / \                        / \
     *    30   15                     5   10
     *
     * poll() → 5,10,15,20,30       poll() → 30,20,15,10,5
     */
    static void demonstratePriorityQueue() {
        System.out.println("\n═══ PriorityQueue ═══");

        // Min-heap (default)
        java.util.PriorityQueue<Integer> minHeap = new java.util.PriorityQueue<>();
        for (int val : new int[]{30, 10, 20, 5, 15}) {
            minHeap.offer(val);
        }

        System.out.print("Min-heap poll order: ");
        while (!minHeap.isEmpty()) {
            System.out.print(minHeap.poll() + " ");
        }
        System.out.println();

        // Max-heap (reversed)
        java.util.PriorityQueue<Integer> maxHeap =
            new java.util.PriorityQueue<>(java.util.Comparator.reverseOrder());
        for (int val : new int[]{30, 10, 20, 5, 15}) {
            maxHeap.offer(val);
        }

        System.out.print("Max-heap poll order: ");
        while (!maxHeap.isEmpty()) {
            System.out.print(maxHeap.poll() + " ");
        }
        System.out.println();

        // Priority by Employee salary (lowest salary = highest priority)
        java.util.PriorityQueue<Employee> salaryQueue =
            new java.util.PriorityQueue<>(
                java.util.Comparator.comparingDouble(e -> e.salary)
            );
        salaryQueue.offer(new Employee(1, "Alice", "Eng", 130000));
        salaryQueue.offer(new Employee(2, "Bob", "Mkt", 90000));
        salaryQueue.offer(new Employee(3, "Carol", "Eng", 110000));

        System.out.println("Lowest salary first: " + salaryQueue.poll());
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 4: ArrayDeque as Stack and Queue            │
    // └──────────────────────────────────────────────────────┘

    /**
     * ArrayDeque — circular buffer, replaces both Stack and LinkedList.
     *
     * As Queue (FIFO):          As Stack (LIFO):
     * ┌─────────────────┐       ┌─────────────────┐
     * │ offer → [A,B,C] │       │ push → [C,B,A]  │
     * │ poll  ← A       │       │ pop  ← C        │
     * └─────────────────┘       └─────────────────┘
     *
     * ✅ Always prefer ArrayDeque over Stack class
     *    Stack extends Vector (synchronized, slow, legacy)
     */
    static void demonstrateDeque() {
        System.out.println("\n═══ ArrayDeque ═══");

        // As Queue (FIFO)
        java.util.Deque<String> queue = new java.util.ArrayDeque<>();
        queue.offer("first");
        queue.offer("second");
        queue.offer("third");
        System.out.println("Queue poll: " + queue.poll());   // "first"
        System.out.println("Queue peek: " + queue.peek());   // "second"

        // As Stack (LIFO) — replaces java.util.Stack
        java.util.Deque<String> stack = new java.util.ArrayDeque<>();
        stack.push("bottom");
        stack.push("middle");
        stack.push("top");
        System.out.println("Stack pop:  " + stack.pop());    // "top"
        System.out.println("Stack peek: " + stack.peek());   // "middle"
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  SECTION 5: TreeMap/TreeSet — Always Sorted          │
    // └──────────────────────────────────────────────────────┘

    /**
     * TreeMap internal structure (Red-Black Tree):
     *
     *            [Bob:85]              ← root
     *           /        \
     *     [Alice:92]   [Carol:78]
     *       /               \
     *  [   ...   ]      [Dave:88]
     *
     * Keys are ALWAYS in sorted order during iteration.
     * Requires: Comparable OR explicit Comparator
     */
    static void demonstrateTreeMapSet() {
        System.out.println("\n═══ TreeMap/TreeSet ═══");

        // TreeMap with natural String ordering
        java.util.TreeMap<String, Integer> scores = new java.util.TreeMap<>();
        scores.put("Carol", 78);
        scores.put("Alice", 92);
        scores.put("Dave", 88);
        scores.put("Bob", 85);

        // Always iterates in key order
        System.out.println("TreeMap (sorted keys): " + scores);
        System.out.println("First entry: " + scores.firstEntry());
        System.out.println("Last entry:  " + scores.lastEntry());

        // Case-insensitive TreeMap
        java.util.TreeMap<String, Integer> caseInsensitive =
            new java.util.TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        caseInsensitive.put("Alice", 1);
        caseInsensitive.put("alice", 2);  // overwrites! same key case-insensitively
        System.out.println("Case-insensitive size: " + caseInsensitive.size()); // 1
    }

    // ┌──────────────────────────────────────────────────────┐
    // │  MAIN — Run All Demos                                │
    // └──────────────────────────────────────────────────────┘

    public static void main(String[] args) {
        demonstrateComparatorChaining();
        demonstratePriorityQueue();
        demonstrateDeque();
        demonstrateTreeMapSet();
    }
}
