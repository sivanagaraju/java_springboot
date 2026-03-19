# 02 — Gradle Build Tool

## Why This Module Matters

Every Java project needs a build tool to compile code, manage dependencies, run tests, and package artifacts. **Gradle** is the modern standard for Spring Boot projects.

If you come from Python, think of Gradle as the combination of `pip + setuptools + Makefile + tox` — but with a powerful, programmable Groovy/Kotlin DSL instead of declarative config files.

## Python/FastAPI Comparison

| Concern | Python | Java/Gradle |
|---|---|---|
| Dependency file | `requirements.txt` / `pyproject.toml` | `build.gradle` |
| Lock file | `poetry.lock` / `pip freeze` | `gradle.lockfile` (optional) |
| Install deps | `pip install -r requirements.txt` | `./gradlew build` |
| Run app | `python main.py` / `uvicorn` | `./gradlew bootRun` |
| Run tests | `pytest` | `./gradlew test` |
| Package | `python -m build` → `.whl` | `./gradlew bootJar` → `.jar` |
| Multi-module | Monorepo with `pip -e .` | `settings.gradle` + `include` |
| Task runner | `Makefile` / `invoke` | Gradle task DAG (built-in) |

## Module Structure

- **[01-gradle-basics/](01-gradle-basics/)** — Core Gradle concepts: build file structure, tasks, dependencies, wrapper, multi-module
- **[02-gradle-advanced/](02-gradle-advanced/)** — Custom tasks, build profiles, Spring Boot plugin internals

## Mindmap

See [MINDMAP.md](MINDMAP.md) for a visual overview of all Gradle concepts covered in this module.
