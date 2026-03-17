import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  NIO.2 API — Demo                                           ║
 * ║  Path, Files utility, directory walking                     ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * ┌───────────────────────────────────────────────────────────┐
 * │  NIO.2 Key Classes:                                       │
 * │                                                           │
 * │  Path     → represents a file/directory location          │
 * │  Files    → static utility for all file operations        │
 * │  Paths    → factory (deprecated, use Path.of())           │
 * │                                                           │
 * │  Path.of("src", "main")  →  src/main                     │
 * │  Files.readString(path)  →  entire file as String         │
 * │  Files.walk(dir)         →  Stream<Path> recursive        │
 * └───────────────────────────────────────────────────────────┘
 */
public class NIODemo {

    public static void main(String[] args) throws IOException {
        System.out.println("═══ NIO.2 API DEMO ═══\n");

        // Setup temp directory structure
        Path root = Files.createTempDirectory("nio-demo");
        Path subDir = Files.createDirectories(root.resolve("docs/reports"));
        Files.writeString(root.resolve("README.md"), "# Project Root");
        Files.writeString(root.resolve("app.java"), "public class App {}");
        Files.writeString(subDir.resolve("q1-report.csv"), "month,revenue\nJan,1000");
        Files.writeString(subDir.resolve("q2-report.csv"), "month,revenue\nApr,1500");
        Files.writeString(root.resolve("docs/guide.md"), "# User Guide");

        // 1. Path operations
        System.out.println("1. Path Operations:");
        Path p = root.resolve("docs/reports/q1-report.csv");
        System.out.println("  File name:  " + p.getFileName());
        System.out.println("  Parent:     " + p.getParent().getFileName());
        System.out.println("  Name count: " + p.getNameCount());
        System.out.println("  Absolute:   " + p.toAbsolutePath());

        // 2. File properties
        System.out.println("\n2. File Properties:");
        System.out.println("  Exists: " + Files.exists(p));
        System.out.println("  Size:   " + Files.size(p) + " bytes");
        System.out.println("  Is dir: " + Files.isDirectory(p));

        // 3. List directory (1 level)
        System.out.println("\n3. List Directory (root):");
        try (Stream<Path> listing = Files.list(root)) {
            listing.forEach(entry ->
                System.out.println("  " + (Files.isDirectory(entry) ? "📁" : "📄")
                    + " " + entry.getFileName()));
        }

        // 4. Walk entire tree
        System.out.println("\n4. Walk Tree (recursive):");
        try (Stream<Path> tree = Files.walk(root)) {
            tree.forEach(path -> {
                int depth = root.relativize(path).getNameCount();
                String indent = "  " + "  ".repeat(depth);
                System.out.println(indent + (Files.isDirectory(path) ? "📁" : "📄")
                    + " " + path.getFileName());
            });
        }

        // 5. Find specific files
        System.out.println("\n5. Find .csv Files:");
        try (Stream<Path> csvFiles = Files.walk(root)) {
            csvFiles.filter(path -> path.toString().endsWith(".csv"))
                    .forEach(path -> System.out.println("  Found: "
                        + root.relativize(path)));
        }

        // 6. Copy files
        System.out.println("\n6. File Copy:");
        Path backup = root.resolve("README.bak");
        Files.copy(root.resolve("README.md"), backup,
            StandardCopyOption.REPLACE_EXISTING);
        System.out.println("  Copied README.md → README.bak");
        System.out.println("  Backup content: " + Files.readString(backup));

        // Cleanup
        Files.walk(root).sorted(java.util.Comparator.reverseOrder())
             .forEach(path -> { try { Files.delete(path); } catch (IOException e) {} });

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Path.of() replaces new File().");
        System.out.println("Files.walk() + Stream = powerful recursive operations.");
        System.out.println("Always close Stream<Path> with try-with-resources.");
    }
}
