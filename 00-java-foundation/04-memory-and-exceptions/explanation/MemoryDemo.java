package explanation;

public class MemoryDemo {

    // 1. Static Variable: Lives in the Metaspace (Method Area)
    private static int globalCounter = 0;

    public static void main(String[] args) {
        
        System.out.println("--- Stack vs Heap Architecture Demo ---");

        // 2. Primitive Local Variable: Lives on the Main Thread Stack
        int mainThreadId = 1;

        // 3. Object Reference & Allocation
        // 'userRef' lives on the Stack
        // 'new User()' object lives on the Heap
        User userRef = new User(100, "Alice");

        // Execute method to observe Stack Frames
        processUser(userRef);

        // Force a Garbage Collection request (Warning: JVM might ignore this)
        // This is strictly for demonstration to show finalization
        System.gc();
        
        try {
            Thread.sleep(1000); // Give GC time to run
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Main method concluding.");
    }

    public static void processUser(User passedRef) {
        // 'passedRef' is a NEW reference on the Stack, pointing to the SAME Heap object.
        System.out.println("Processing User: " + passedRef.getName());

        // This creates a completely Unreachable Object on the Heap immediately after this method ends.
        User temporaryUser = new User(999, "Orphaned Bob");
        
        // When processUser() ends, its Stack Frame is popped.
        // 'temporaryUser' reference is destroyed.
        // The Heap object "Orphaned Bob" becomes eligible for Garbage Collection.
    }

    // Inner class defining our data model
    public static class User {
        private int id; // Primitive inside an Object -> Lives on Heap
        private String name; // Reference inside an Object -> Lives on Heap, points to String Pool
        
        // Massive array to consume physical Heap Memory quickly
        private byte[] memoryHog = new byte[1024 * 1024 * 10]; // 10 Megabytes

        public User(int id, String name) {
            this.id = id;
            this.name = name;
            System.out.println("User Created allocating 10MB: " + this.name);
        }

        public String getName() {
            return name;
        }

        // Overriding finalize to physically observe the Garbage Collector sweeping the object.
        // NOTE: finalize() is deprecated in modern Java. Used here purely for educational tracing.
        @Override
        protected void finalize() throws Throwable {
            System.out.println(">>> GARBAGE COLLECTOR SWEEPING OBJECT: " + this.name + " (Reclaiming 10MB) <<<");
            super.finalize();
        }
    }
}
