# Gradle Build Tool

## Why Gradle
- Modern alternative to Maven
- Groovy/Kotlin DSL (not XML)
- Incremental builds
- Build cache
- Parallel execution
- Python comparison: pip + setuptools + Makefile

## Core Concepts
- build.gradle
  - Plugins block
  - Repositories block
  - Dependencies block
  - Tasks block
- settings.gradle
  - Root project name
  - Module includes
- gradle.properties
  - JVM arguments
  - Project-wide settings

## Build Lifecycle
- Initialization Phase
  - Reads settings.gradle
  - Determines which projects participate
- Configuration Phase
  - Evaluates all build.gradle files
  - Creates task graph (DAG)
- Execution Phase
  - Runs requested tasks in dependency order

## Dependency Management
- Scopes
  - implementation (compile + runtime)
  - testImplementation (test only)
  - runtimeOnly (runtime, not compile)
  - compileOnly (compile, not runtime)
  - api (leaks to consumers)
- Repositories
  - mavenCentral()
  - google()
  - Custom / private
- BOM (Bill of Materials)
  - Spring dependency-management plugin
  - Version alignment across libraries

## Gradle Wrapper
- gradlew / gradlew.bat
  - Committed to version control
  - Ensures consistent Gradle version
  - No global Gradle install needed
- gradle-wrapper.properties
  - distributionUrl
  - Gradle version pinned

## Tasks
- Built-in Tasks
  - build
  - test
  - clean
  - dependencies
- Spring Boot Tasks
  - bootRun
  - bootJar
  - bootBuildImage
- Custom Tasks
  - Groovy DSL
  - doFirst / doLast
  - dependsOn

## Multi-Module Projects
- Root settings.gradle
  - include ':module-name'
- Shared dependencies
  - allprojects / subprojects blocks
- Inter-module dependencies
  - implementation project(':common-lib')

## Advanced
- Custom Tasks in Groovy
- Build Profiles (dev/test/prod)
- Spring Boot Gradle Plugin
  - bootJar vs jar
  - bootRun configuration
  - bootBuildImage (Docker)
