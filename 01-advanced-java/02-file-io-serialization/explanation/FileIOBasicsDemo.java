import java.io.*;
import java.nio.file.*;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║  FILE I/O BASICS — Demo                                     ║
 * ║  Byte streams, character streams, try-with-resources        ║
 * ╚══════════════════════════════════════════════════════════════╝
 *
 * ┌──────────────────────────────────────────────────────────┐
 * │  I/O Stream Decision:                                    │
 * │  Text data (.txt, .csv, .json)  → Reader / Writer        │
 * │  Binary data (.png, .pdf, .zip) → InputStream / Output   │
 * │  Modern code → Files.readString() / Files.writeString()  │
 * └──────────────────────────────────────────────────────────┘
 */
public class FileIOBasicsDemo {

    public static void main(String[] args) throws IOException {
        System.out.println("═══ FILE I/O BASICS DEMO ═══\n");
        Path tempDir = Files.createTempDirectory("io-demo");

        // 1. Classic character writing
        System.out.println("1. FileWriter + BufferedWriter:");
        Path textFile = tempDir.resolve("sample.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(textFile.toFile()))) {
            bw.write("Line 1: Hello from FileWriter");
            bw.newLine();
            bw.write("Line 2: Buffered for performance");
            bw.newLine();
            bw.write("Line 3: Auto-closed via try-with-resources");
        }
        System.out.println("  Written to: " + textFile);

        // 2. Classic character reading
        System.out.println("\n2. BufferedReader (line by line):");
        try (BufferedReader br = new BufferedReader(new FileReader(textFile.toFile()))) {
            String line;
            int lineNum = 1;
            while ((line = br.readLine()) != null) {
                System.out.println("  " + lineNum++ + ": " + line);
            }
        }

        // 3. NIO.2 one-liners (Java 11+)
        System.out.println("\n3. NIO.2 One-Liners:");
        Path nioFile = tempDir.resolve("nio-sample.txt");
        Files.writeString(nioFile, "Written with Files.writeString()!\nSecond line.");
        String content = Files.readString(nioFile);
        System.out.println("  Content: " + content.replace("\n", " | "));

        // 4. Reading all lines
        System.out.println("\n4. Files.readAllLines():");
        var lines = Files.readAllLines(nioFile);
        lines.forEach(l -> System.out.println("  → " + l));

        // 5. Stream-based reading
        System.out.println("\n5. Files.lines() with Stream:");
        try (var stream = Files.lines(textFile)) {
            stream.filter(l -> l.contains("Buffered"))
                  .forEach(l -> System.out.println("  Found: " + l));
        }

        // Cleanup
        Files.walk(tempDir).sorted(java.util.Comparator.reverseOrder())
             .forEach(p -> { try { Files.delete(p); } catch (IOException e) {} });

        System.out.println("\n═══ KEY TAKEAWAY ═══");
        System.out.println("Use NIO.2 Files.readString()/writeString() for simple cases.");
        System.out.println("Use BufferedReader/Writer for large file processing.");
        System.out.println("ALWAYS use try-with-resources for stream cleanup.");
    }
}
