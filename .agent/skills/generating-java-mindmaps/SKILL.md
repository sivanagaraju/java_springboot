---
name: generating-java-mindmaps
description: Generates MINDMAP.md files using pure Markdown list syntax (Markmap format)
  for the spring-mastery learning repository. Use when creating or updating any MINDMAP.md
  file for a Java or Spring Boot module, sub-topic, or mini-project. Triggers on phrases
  like "create mindmap", "generate MINDMAP.md", "write the mindmap for X", "build the
  mind map for module X".
---

# Generating Java Mindmaps

## Quick start

Read [references/markmap-format.md](references/markmap-format.md) before writing
anything — it contains the exact syntax, the repo's specific MINDMAP.md template,
common failures, and a complete correct example from the actual repo.

## Workflow checklist

Copy into your response and tick off as you complete each item:

```
Mindmap Generation Progress:
- [ ] Step 1: Read markmap-format.md
- [ ] Step 2: Identify scope (module-level / sub-topic / mini-project)
- [ ] Step 3: List all explanation/*.md files and their key concepts
- [ ] Step 4: List all exercises with their learning objectives
- [ ] Step 5: Write the Markdown list structure (pure lists, no mermaid)
- [ ] Step 6: Add "Interview QA Focus" section at the bottom
- [ ] Step 7: Add links to all referenced files using relative paths
- [ ] Step 8: Verify — no mermaid block, no mermaid mindmap syntax, only Markdown lists
```

## Feedback loop

After writing, verify:
- Does the file contain ANY `mermaid` fenced block? If yes → FAIL, delete it
- Does it use `mindmap` keyword? If yes → FAIL, that's mermaid mindmap syntax
- Are all file references using relative paths with correct filenames?
- Is there an "Interview QA Focus" section listing key question themes?

Fix any failure before finishing.

## The repo's MINDMAP.md format

This repo uses **pure Markdown list trees** — NOT mermaid, NOT markmap fenced blocks.
The VS Code Markmap extension reads standard Markdown headings and lists directly.

```markdown
# [Sub-topic Name] — Concept Mindmap

## `Module/Sub-topic Path`
- **[`file-name.md`](explanation/file-name.md)** & **[`DemoFile.java`](explanation/DemoFile.java)**
  - Concept 1
    - Detail A
    - Detail B
  - Concept 2

### Interview QA Focus
- Key question theme 1
- Key question theme 2
```

See [references/markmap-format.md](references/markmap-format.md) for the full template
and a complete worked example.

## Hard rules

- NEVER use a `mermaid` fenced block inside a MINDMAP.md
- NEVER use the `mindmap` keyword — it is mermaid mindmap syntax
- ALWAYS use pure Markdown headings and list items
- ALWAYS link to actual files using relative paths
- ALWAYS include an "Interview QA Focus" section
- ALWAYS bold the file names with `**[`filename`](path)**` pattern

## Do not use this skill when

- Writing `explanation/*.md` content → use `generating-java-content`
- Writing `.java` demo files → use `generating-java-demos`
- Building mini-project scaffolds → use `building-java-miniprojects`
- Writing exercises → use `writing-java-exercises`
