---
name: generating-java-demos
description: Generates production-quality Java demo files for the spring-mastery learning
  repository. Use when creating any explanation/*.java or demos/*.java file that demonstrates
  a concept. Triggers on phrases like "write the Java demo for", "create the demo file",
  "write a runnable Java example of", "generate the .java file for module X".
---

# Generating Java Demos

## Quick start

1. Read [references/java-demo-standards.md](references/java-demo-standards.md) — all rules
2. Study [examples/ProductServiceImpl.java](examples/ProductServiceImpl.java) — gold-standard output

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
Java Demo Generation Progress:
- [ ] Step 1: Read java-demo-standards.md
- [ ] Step 2: Write Layer 1 file header (╔══╗ box with ASCII diagram)
- [ ] Step 3: Confirm package declaration (com.learning.*)
- [ ] Step 4: Write all imports (stdlib → third-party → jakarta → spring)
- [ ] Step 5: Write Layer 2 class javadoc (responsibility + Python equiv + ASCII layer)
- [ ] Step 6: Write all public methods with Layer 3 javadoc
- [ ] Step 7: Add Layer 4 inline comments for complex/non-obvious logic
- [ ] Step 8: Add EXPECTED OUTPUT comment block at end of main() if present
- [ ] Step 9: Verify — no javax.*, no Maven refs, uses Java 21 features where relevant
- [ ] Step 10: Verify — every public method has @param @return @throws if applicable
```

## Feedback loop

After writing, verify the file by mentally running the 4-layer check:
- Layer 1: File header with ╔══╗ box AND ASCII diagram? ✓/✗
- Layer 2: Class javadoc with Python equivalent AND ASCII architecture? ✓/✗
- Layer 3: Every public method has contract description + @param + @return? ✓/✗
- Layer 4: Complex logic has WHY comments (not WHAT comments)? ✓/✗

Fix any ✗ before finishing.

## Hard rules

- Every .java file needs all 4 comment layers — no exceptions
- ASCII diagrams are MANDATORY in Layers 1, 2, and 3 (Mermaid does not render in Java)
- Use `jakarta.persistence.*` not `javax.persistence.*`
- Use Java 21 features where they genuinely make the code cleaner
- Package must be `com.learning.<module>`
- Gradle commands only — never Maven
- Use `EmptyOperator` pattern thinking: demonstrate one concept clearly per file
- Add `// EXPECTED OUTPUT:` comment block at end of `main()` methods

## Do not use this skill when

- Writing `explanation/*.md` files → use `generating-java-content`
- Writing `MINDMAP.md` files → use `generating-java-mindmaps`
- Building mini-project scaffolds → use `building-java-miniprojects`
- Writing exercise files → use `writing-java-exercises`
