# Gradle Basics

## What is Gradle
- Build automation tool
- Groovy DSL (not XML like Maven)
- Supports Java, Kotlin, Groovy, Scala
- Used by Spring Boot, Android, Netflix
- Python equivalent: pip + setuptools + Makefile

## Build File Structure
- build.gradle
  - plugins
  - group / version
  - repositories
  - dependencies
  - tasks
- settings.gradle
  - rootProject.name
  - include modules
- gradle.properties
  - JVM args
  - Custom properties

## Build Lifecycle
- Initialization
  - Reads settings.gradle
  - Identifies participating projects
- Configuration
  - Evaluates build.gradle
  - Builds task DAG
- Execution
  - Runs selected tasks

## Dependency Scopes
- implementation
  - Compile + Runtime
  - NOT exposed to consumers
- api
  - Compile + Runtime
  - Exposed to consumers
- testImplementation
  - Test compile + test runtime
- runtimeOnly
  - Runtime only, not compile
- compileOnly
  - Compile only, not runtime
  - Like Lombok

## Tasks
- Built-in
  - build
  - test
  - clean
  - dependencies
  - jar
- Spring Boot Plugin
  - bootRun
  - bootJar
  - bootBuildImage
- Task Dependencies
  - dependsOn
  - mustRunAfter
  - finalizedBy

## Gradle Wrapper
- gradlew / gradlew.bat
- gradle-wrapper.properties
- Committed to Git
- No global install required
- Reproducible builds

## Multi-Module
- settings.gradle includes
- Root build.gradle
  - allprojects block
  - subprojects block
- Inter-module deps
  - project(':module')

## Dependency Management
- BOM (Bill of Materials)
- Spring dependency-management plugin
- Version catalog (libs.versions.toml)
