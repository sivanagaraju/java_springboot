import java.io.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  SERIALIZATION — Demo                                       ║
 * ║  Object ↔ Bytes (and why you should use JSON instead)       ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │ Java Serialization:  Object → bytes → file → bytes → Object │
 * │                                                          │
 * │ ⚠️ Security risk! Use JSON (Jackson) in production.     │
 * │ This demo exists so you understand the mechanism.        │
 * └──────────────────────────────────────────────────────────┘
 */
public class SerializationDemo {

    // Serializable class with transient field
    static class Employee implements Serializable {
        private static final long serialVersionUID = 1L;

        private String name;
        private int age;
        private double salary;
        private transient String password;  // NOT serialized!

        Employee(String name, int age, double salary, String password) {
            this.name = name; this.age = age;
            this.salary = salary; this.password = password;
        }

        @Override
        public String toString() {
            return "Employee{name='" + name + "', age=" + age
                + ", salary=" + salary + ", password='" + password + "'}";
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("═══ SERIALIZATION DEMO ═══\n");

        java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("serial", ".ser");
        String filePath = tempFile.toString();

        // 1. Serialize
        System.out.println("1. Serializing Employee:");
        Employee emp = new Employee("Alice", 30, 85000.0, "secret123");
        System.out.println("  Before: " + emp);

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(emp);
        }
        System.out.println("  Written to: " + filePath);
        System.out.println("  File size: " + java.nio.file.Files.size(tempFile) + " bytes");

        // 2. Deserialize
        System.out.println("\n2. Deserializing Employee:");
        Employee restored;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            restored = (Employee) ois.readObject();
        }
        System.out.println("  After:  " + restored);
        System.out.println("  ⚠️ Notice: password is null (transient field!)");

        // 3. Why JSON is better
        System.out.println("\n3. Why Use JSON Instead:");
        System.out.println("  ✅ Human-readable (can inspect/debug)");
        System.out.println("  ✅ Language-independent (any language reads JSON)");
        System.out.println("  ✅ No security risk (no code execution on parse)");
        System.out.println("  ✅ Spring Boot default (Jackson auto-configured)");
        System.out.println("  ❌ Java serialization: binary, Java-only, security risk");

        // Cleanup
        java.nio.file.Files.deleteIfExists(tempFile);

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Know Java serialization for interviews and legacy code.");
        System.out.println("Use Jackson (JSON) for all new code.");
        System.out.println("transient = excluded from serialization.");
    }
}
