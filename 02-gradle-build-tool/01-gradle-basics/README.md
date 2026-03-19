# 01 — Gradle Basics

## Overview

This sub-module covers the foundational concepts of Gradle that every Spring Boot developer must understand. You will learn how Gradle structures builds, manages dependencies, and orchestrates tasks — and how all of this compares to the Python tools you already know.

## Core Concepts Covered

1. **What is Gradle?** — Gradle vs Maven vs pip/poetry; why the Spring ecosystem chose Gradle
2. **The Gradle Wrapper** — Why `gradlew` is committed to Git and how it guarantees reproducible builds
3. **Build Lifecycle** — The three phases: Initialization → Configuration → Execution
4. **Tasks** — The build task DAG, built-in tasks, and how `./gradlew bootRun` works
5. **Dependencies** — Scopes (`implementation`, `testImplementation`, `runtimeOnly`) and repositories
6. **Multi-Module Projects** — `settings.gradle`, `include`, and shared dependency blocks
7. **Dependency Management** — BOM (Bill of Materials) and the Spring dependency-management plugin

## Structure

- `explanation/` — Deep-dive markdown files for each concept, with Mermaid diagrams and Python comparisons
- `exercises/` — Hands-on tasks to build custom Gradle configurations

## Mindmap

See [MINDMAP.md](MINDMAP.md) for a visual overview of all Gradle Basics concepts.
