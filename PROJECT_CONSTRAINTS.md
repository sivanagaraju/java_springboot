Sadly# Project Constraints and AI Guardrails

This file serves as a memory buffer to prevent recurring mistakes and enforce quality patterns. All AI agents working on this repo must review these rules before making changes.

## 🚩 Critical Safety Rules

### 1. Mermaid C4 Syntax (ARCHITECTURE.md / README.md)
- **Constraint:** Do NOT use standard arrows (`->` or `-->`) inside `C4Context` or `C4Component` Mermaid blocks.
- **Enforcement:** Always use the macro `Rel(from, to, "label")`.
- **Reason:** Standard Mermaid arrows cause lexical errors in C4-specific renderers.

### 2. Java Compatibility (1.11 Version)
- **Constraint:** Do NOT use Java 14+ specific syntax (e.g., Switch expressions with `->` or Multi-line text blocks `"""`).
- **Enforcement:** Use traditional `switch` and explicit string concatenation.
- **Reason:** Current Environment is set to Java 11.0.17.

## 📏 Deep Practitioner Patterns

### 1. 4-Layer Commenting
- **Layer 1 (Header):** Context, Module, and "Python Bridge".
- **Layer 2 (Class):** Responsibility and "Python Equivalent".
- **Layer 3 (Method):** Logic contract and Mocking notes.
- **Layer 4 (Inline):** Only for non-obvious/complex logic.

### 2. Python Bridge Mentality
- All documentation must highlight the transition from Python's dynamic/scripting approach to Java's static/layered approach.
- Example: Compare `pytest` state to `JUnit` lifecycle.
