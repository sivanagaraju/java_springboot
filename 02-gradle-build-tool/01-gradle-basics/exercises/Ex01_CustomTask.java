/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex01_CustomTask.java                                   ║
 * ║  MODULE : 02-gradle-build-tool / 01-gradle-basics / exercises   ║
 * ║  GRADLE : ./gradlew :02-gradle-build-tool:run                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — understand how Gradle compiles and ║
 * ║                   runs Java source files using the 'application'║
 * ║                   plugin. This file is a simple main() to test  ║
 * ║                   your custom build.gradle configuration.       ║
 * ║  WHY IT EXISTS  : Gradle needs a Java file to demonstrate the   ║
 * ║                   build lifecycle (compile → process → classes)  ║
 * ║  PYTHON COMPARE : In Python, you run `python app.py` directly.  ║
 * ║                   In Java, Gradle compiles first, then runs.    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Gradle Build Lifecycle                          ║
 * ║                                                                   ║
 * ║    ./gradlew run                                                  ║
 * ║        │                                                          ║
 * ║        ├── 1. compileJava                                         ║
 * ║        │       └── javac Ex01_CustomTask.java → .class            ║
 * ║        │                                                          ║
 * ║        ├── 2. processResources                                    ║
 * ║        │       └── copies application.properties → build/         ║
 * ║        │                                                          ║
 * ║        ├── 3. classes (depends on 1 + 2)                          ║
 * ║        │       └── all .class files ready in build/classes/       ║
 * ║        │                                                          ║
 * ║        └── 4. run (depends on 3)                                  ║
 * ║                └── java -cp build/classes Ex01_CustomTask.main()  ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :02-gradle-build-tool:run           ║
 * ║  EXPECTED OUTPUT: "Hello from Gradle exercise!" + timestamp     ║
 * ║  RELATED FILES  : annotated-build.gradle, 01-what-is-gradle.md ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

import java.time.LocalDateTime;

/**
 * Simple Java application to test Gradle build configuration.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python — just run directly:
 *   python app.py
 *
 *   # Java — Gradle handles compile + run:
 *   ./gradlew :module:run
 * </pre>
 *
 * <p><b>ASCII — Why Gradle exists (Java vs Python):</b>
 * <pre>
 *   Python world:                    Java world:
 *   ┌──────────────┐                ┌──────────────────────┐
 *   │ app.py       │                │ App.java             │
 *   │ (interpreted)│                │ (must compile first!) │
 *   └──────┬───────┘                └──────┬───────────────┘
 *          │                                │
 *          ▼                                ▼
 *   python app.py                    javac App.java
 *   ← done!                               │
 *                                          ▼
 *                                    java App
 *                                    ← done!
 *
 *   Gradle automates: compile + run + dependency download + testing
 * </pre>
 */
public class Ex01_CustomTask {

    /**
     * Main entry point — prints a greeting with the current timestamp.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  Hello from Gradle exercise!              ║");
        System.out.println("║  Compiled and run by: ./gradlew run       ║");
        System.out.println("║  Timestamp: " + LocalDateTime.now() + "    ║");
        System.out.println("╚════════════════════════════════════════════╝");

        // TODO 1: Modify this file and re-run ./gradlew run.
        // Observe that Gradle recompiles (compileJava) before running.

        // TODO 2: Add a command-line argument parser.
        // Accept a --name flag and print "Hello, <name>!".
        // Hint: Check args.length and args[0].

        // TODO 3: Try running ./gradlew build instead of ./gradlew run.
        // What extra tasks does 'build' execute compared to 'run'?
        // Hint: Run ./gradlew build --console=verbose to see all tasks.
    }
}
