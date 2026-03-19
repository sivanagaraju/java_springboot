/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : Ex02_MultiModuleSetup.java                             ║
 * ║  MODULE : 02-gradle-build-tool / 01-gradle-basics / exercises   ║
 * ║  GRADLE : ./gradlew :02-gradle-build-tool:run                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Exercise — understand multi-module Gradle     ║
 * ║                   builds. This file lives in a sub-module and   ║
 * ║                   demonstrates how settings.gradle includes it. ║
 * ║  WHY IT EXISTS  : Real Spring Boot projects have multiple       ║
 * ║                   modules (api, service, common). Gradle uses   ║
 * ║                   settings.gradle to wire them together.        ║
 * ║  PYTHON COMPARE : pyproject.toml with workspace packages or     ║
 * ║                   Python monorepo with multiple setup.py files  ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Multi-Module Project Structure                  ║
 * ║                                                                   ║
 * ║    root-project/                                                  ║
 * ║    ├── settings.gradle      ← includes all sub-modules           ║
 * ║    ├── build.gradle          ← root build (shared config)        ║
 * ║    │                                                              ║
 * ║    ├── common/                                                    ║
 * ║    │   ├── build.gradle      ← sub-module build                  ║
 * ║    │   └── src/main/java/    ← shared utilities                  ║
 * ║    │                                                              ║
 * ║    ├── api/                                                       ║
 * ║    │   ├── build.gradle      ← depends on :common                ║
 * ║    │   └── src/main/java/    ← REST controllers                  ║
 * ║    │                                                              ║
 * ║    └── service/                                                   ║
 * ║        ├── build.gradle      ← depends on :common                ║
 * ║        └── src/main/java/    ← business logic                    ║
 * ║                                                                   ║
 * ║    settings.gradle:                                               ║
 * ║      rootProject.name = 'my-app'                                  ║
 * ║      include 'common', 'api', 'service'                          ║
 * ║                                                                   ║
 * ║    api/build.gradle:                                              ║
 * ║      dependencies {                                               ║
 * ║          implementation project(':common')  ← module dependency  ║
 * ║      }                                                            ║
 * ║                                                                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :02-gradle-build-tool:run           ║
 * ║  EXPECTED OUTPUT: Module dependency resolution message           ║
 * ║  RELATED FILES  : annotated-settings.gradle, 07-dependency-     ║
 * ║                   management.md                                  ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

/**
 * Demonstrates a class that could belong to a multi-module project.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # In Python monorepo:
 *   # common/utils.py → imported by api/routes.py
 *   # from common.utils import format_response
 *
 *   # In Gradle multi-module:
 *   # :common module → imported by :api module
 *   # implementation project(':common') in build.gradle
 * </pre>
 *
 * <p><b>ASCII — Dependency Resolution Order:</b>
 * <pre>
 *   ./gradlew :api:build
 *       │
 *       ├── Resolve dependencies
 *       │   └── project(':common') found in settings.gradle
 *       │
 *       ├── Build :common FIRST (transitive dependency)
 *       │   └── compileJava → processResources → classes → jar
 *       │
 *       └── Build :api SECOND (depends on :common's jar)
 *           └── compileJava (with common.jar on classpath) → run
 * </pre>
 */
public class Ex02_MultiModuleSetup {

    /**
     * Simulates a utility method from a 'common' module.
     *
     * @param input raw input string
     * @return formatted output
     */
    public static String formatForDisplay(String input) {
        return "[FORMATTED] " + input.trim().toUpperCase();
    }

    /**
     * Main entry point — demonstrates cross-module usage.
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║  Multi-Module Gradle Exercise              ║");
        System.out.println("╚════════════════════════════════════════════╝");

        // Simulates calling a utility from the 'common' module
        String result = formatForDisplay("  hello from sub-module  ");
        System.out.println("Common utility result: " + result);

        // TODO 1: Create a settings.gradle file with:
        //   rootProject.name = 'multi-module-demo'
        //   include 'common', 'api'

        // TODO 2: Create common/build.gradle with the 'java-library' plugin.
        //   (java-library exposes API dependencies to consumers)

        // TODO 3: Create api/build.gradle with:
        //   dependencies { implementation project(':common') }

        // TODO 4: Move formatForDisplay() to the 'common' module
        //   and import it from the 'api' module.

        // TODO 5: Run ./gradlew :api:build and observe the dependency graph.
        //   Hint: ./gradlew :api:dependencies --configuration runtimeClasspath
    }
}
