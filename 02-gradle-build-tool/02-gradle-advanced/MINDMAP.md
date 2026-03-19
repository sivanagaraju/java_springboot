# Gradle Advanced

## Custom Tasks
- Groovy DSL syntax
  - task keyword
  - doFirst / doLast
  - inputs / outputs
- Task Types
  - Copy tasks
  - Exec tasks
  - Custom classes
- Use Cases
  - Code generation
  - Environment validation
  - Database migration scripts
  - Version file generation

## Build Profiles
- Spring Profiles Integration
  - application-dev.yml
  - application-prod.yml
- Gradle Properties
  - gradle.properties
  - -P command line flag
  - System properties
- Environment-Specific Config
  - Database URLs
  - Logging levels
  - Feature flags

## Spring Boot Gradle Plugin
- bootJar Task
  - Creates executable fat JAR
  - Embeds dependencies
  - Contains embedded Tomcat
- jar Task (standard)
  - Regular library JAR
  - No embedded server
  - No dependency bundling
- bootRun Task
  - Dev server with classpath
  - JVM args configuration
  - Passing Spring profiles
- bootBuildImage
  - Cloud Native Buildpacks
  - Docker image without Dockerfile
  - Layer optimization
- Layered JARs
  - Dependencies layer
  - Application layer
  - Snapshot dependencies
  - Docker cache efficiency
