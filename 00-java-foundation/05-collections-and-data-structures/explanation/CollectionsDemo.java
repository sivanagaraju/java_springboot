package explanation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CollectionsDemo {

    public static void main(String[] args) {
        System.out.println("--- Collections Hierarchy Demo ---");

        // 1. Lists (Ordered, Duplicates allowed)
        List<String> arrayList = new ArrayList<>(); // O(1) Index Access
        arrayList.add("Alice");
        arrayList.add("Bob");
        arrayList.add("Alice"); // Duplicate permitted!
        System.out.println("List: " + arrayList);

        // 2. Sets (Unordered, Unique)
        Set<String> hashSet = new HashSet<>(); // O(1) Lookup
        hashSet.add("X-Ray");
        hashSet.add("Alpha");
        hashSet.add("Bravo");
        hashSet.add("Alpha"); // Duplicate Rejected!
        System.out.println("HashSet (Chaotic Order): " + hashSet);

        // 3. Sorted Set (Red-Black Tree)
        Set<String> treeSet = new TreeSet<>(hashSet); // O(log N) Lookup
        System.out.println("TreeSet (Sorted Order): " + treeSet);

        // 4. Queue (FIFO)
        Queue<String> taskQueue = new LinkedList<>();
        taskQueue.offer("Task 1"); // Safer than add()
        taskQueue.offer("Task 2");
        System.out.println("Queue Processing: " + taskQueue.poll()); // Removes Task 1

        // 5. Maps (Key-Value Pairs, NOT a Collection inherently)
        Map<Integer, String> employeeMap = new HashMap<>(); // O(1) Access
        employeeMap.put(101, "Dave");
        employeeMap.put(102, "Eve");
        System.out.println("Map: " + employeeMap);
        
        // Concurrent Map (Thread Safe, Segment Locked)
        Map<Integer, String> threadSafeMap = new ConcurrentHashMap<>();
        threadSafeMap.put(200, "System Process");
    }
}
