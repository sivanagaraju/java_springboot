/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : HowJavaWorks.java                                      ║
 * ║  MODULE : 00-java-foundation / 01-java-basics                    ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run --args="HowJavaWorks"║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates basic Java structure and execution║
 * ║  WHY IT EXISTS  : Java separates compilation from execution.     ║
 * ║  PYTHON COMPARE : Python scripts run top-to-bottom. Java needs   ║
 * ║                   a class and a main() method entry point.       ║
 * ║  USE CASES      : Understanding the entry point of every Java app║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                   ║
 * ║    javac HowJavaWorks.java  ->  HowJavaWorks.class               ║
 * ║    java HowJavaWorks        ->  JVM executes main()              ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run --args="HowJavaWorks"
 * ║  EXPECTED OUTPUT: Hello from the JVM!                            ║
 * ║  RELATED FILES  : VariablesDemo.java                             ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.basics;

/**
 * The fundamental structure of a Java program.
 *
 * <p><b>Layer responsibility:</b> This class serves as the entry point.
 * Every Java application must have at least one class with a main method.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Top level script
 *   if __name__ == "__main__":
 *       print("Hello from the Python Interpreter!")
 * </pre>
 *
 * <p><b>Why public class:</b> The class name MUST exactly match the
 * filename (HowJavaWorks.java) and be public so the JVM can find it.
 */
public class HowJavaWorks {

    /**
     * The main entry point called by the JVM.
     *
     * <p><b>Python equivalent:</b>
     * <pre>
     *   import sys
     *   def main(args=sys.argv):
     *       print("Hello from the JVM!")
     * </pre>
     *
     * <p><b>Flow:</b>
     * <pre>
     *   JVM Starts
     *       │
     *       ▼
     *   Finds public static void main(String[] args)
     *       │
     *       ▼
     *   Executes instructions
     * </pre>
     *
     * @param args command-line arguments passed to the program
     */
    public static void main(String[] args) {
        // System.out implies standard output stream.
        // println prints a line and appends a newline character.
        System.out.println("Hello from the JVM!");
    }
}
