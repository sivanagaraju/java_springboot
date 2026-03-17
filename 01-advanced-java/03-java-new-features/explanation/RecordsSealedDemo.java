/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  RECORDS & SEALED CLASSES — Demo                            ║
 * ║  Modern data modeling in Java 16+/17+                       ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  Record = immutable data carrier (auto equals/hash/str) │
 * │  Sealed = controlled inheritance (exhaustive switch)     │
 * │  Combined = Algebraic Data Types (like Rust/Kotlin)      │
 * └──────────────────────────────────────────────────────────┘
 */
public class RecordsSealedDemo {

    // ═══ RECORDS ═══
    // Simple record — replaces 40+ lines of POJO boilerplate
    record User(String name, int age, String email) {}

    // Record with validation (compact constructor)
    record Product(String name, double price) {
        Product {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
            if (price < 0) throw new IllegalArgumentException("Price must be >= 0");
            name = name.strip();
        }

        // Custom method
        String displayPrice() {
            return String.format("$%.2f", price);
        }
    }

    // Record implementing interface
    record Point(double x, double y) implements Comparable<Point> {
        double distanceFromOrigin() {
            return Math.sqrt(x * x + y * y);
        }

        @Override
        public int compareTo(Point other) {
            return Double.compare(this.distanceFromOrigin(), other.distanceFromOrigin());
        }
    }

    // ═══ SEALED CLASSES ═══
    sealed interface Shape permits Circle, Rectangle, Triangle {}
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    // Exhaustive switch — no default needed!
    static double area(Shape shape) {
        return switch (shape) {
            case Circle c    -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t  -> 0.5 * t.base() * t.height();
        };
    }

    // ═══ ADT EXAMPLE: Result Type ═══
    sealed interface Result<T> permits Success, Failure {}
    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(String error, Exception cause) implements Result<T> {
        Failure(String error) { this(error, null); }
    }

    static <T> String handleResult(Result<T> result) {
        return switch (result) {
            case Success<T> s -> "✅ " + s.value();
            case Failure<T> f -> "❌ " + f.error();
        };
    }

    public static void main(String[] args) {
        System.out.println("═══ RECORDS & SEALED CLASSES DEMO ═══\n");

        // 1. Records basics
        System.out.println("1. Records:");
        var user = new User("Alice", 25, "alice@mail.com");
        System.out.println("  user.name() = " + user.name());
        System.out.println("  toString:    " + user);

        var u2 = new User("Alice", 25, "alice@mail.com");
        System.out.println("  equals:      " + user.equals(u2));
        System.out.println("  hashCode ==  " + (user.hashCode() == u2.hashCode()));

        // 2. Validated record
        System.out.println("\n2. Validated Record:");
        var prod = new Product("  Widget  ", 9.99);
        System.out.println("  name: '" + prod.name() + "' (stripped!)");
        System.out.println("  price: " + prod.displayPrice());

        try {
            new Product("", 5.0);
        } catch (IllegalArgumentException e) {
            System.out.println("  Validation: " + e.getMessage());
        }

        // 3. Sealed class + exhaustive switch
        System.out.println("\n3. Sealed Classes:");
        Shape[] shapes = { new Circle(5), new Rectangle(4, 6), new Triangle(3, 8) };
        for (Shape s : shapes) {
            System.out.printf("  %-25s area = %.2f%n", s, area(s));
        }

        // 4. Result ADT
        System.out.println("\n4. Result ADT:");
        Result<String> ok   = new Success<>("Data loaded");
        Result<String> fail = new Failure<>("Network timeout");
        System.out.println("  " + handleResult(ok));
        System.out.println("  " + handleResult(fail));

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Records for data. Sealed for controlled type hierarchies.");
        System.out.println("Together = algebraic data types with exhaustive matching.");
    }
}
