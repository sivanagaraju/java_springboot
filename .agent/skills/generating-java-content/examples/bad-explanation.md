# BAD EXPLANATION EXAMPLE — What NOT to Do

Study every annotated failure so you know what the content standards are guarding against.

---

# Spring @Transactional

<!-- FAIL: Opens with a dry definition, not WHY it exists -->
The @Transactional annotation in Spring is used to manage transactions. When you put
this annotation on a method, Spring will manage the transaction for you.

<!-- FAIL: Only 1 paragraph. Minimum 3. No WHY story. -->

## How to Use It

<!-- FAIL: No Mermaid diagram anywhere in this file -->
<!-- FAIL: No Python Bridge section -->

Just add @Transactional to your method:

<!-- FAIL: Code has no 4-layer commenting. No WHY comments. -->
```java
@Transactional
public void saveProduct(Product product) {
    productRepo.save(product);
}
```

<!-- FAIL: Code is trivial — shows nothing about how transactions work -->

## Use Cases

<!-- FAIL: Generic "many companies" statement — no domain, no consequence -->
Many companies use @Transactional in their Spring applications. It is very useful
for database operations.

<!-- FAIL: No specific domain, no named industry type, no consequence of not using it -->

## Tips

<!-- FAIL: Only 1 tip. Minimum 3 anti-patterns. -->
<!-- FAIL: No code showing wrong vs right pattern. -->
Don't forget to add @Transactional to your service methods.

## Questions

<!-- FAIL: Definitional question — explicitly forbidden -->
<!-- FAIL: Answers are one sentence — far below minimum depth -->
<!-- FAIL: No grouping (Conceptual / Scenario / Debug / Quick Fire) -->

**Q: What does @Transactional do?**
A: It manages database transactions.

**Q: Where should you put @Transactional?**
A: On service methods.

<!-- FAIL: Zero scenario or debug questions -->
<!-- FAIL: No Quick Fire questions -->
<!-- FAIL: Only 2 questions, minimum is 3 -->

---

# Annotated Failure List

| Failure | Standard Violated |
|---------|------------------|
| Dry definition opening, no WHY story | Content quality — WHY first |
| Single opening paragraph | Minimum 3 substantial paragraphs |
| No Mermaid diagram | Standard 1 — mandatory |
| No Python Bridge section | Standard 2 — mandatory |
| No 4-layer comments in code | Java commenting standard |
| "Many companies" — no domain/consequence | Use cases standard |
| Only 1 anti-pattern (tip) | Minimum 3 anti-patterns |
| No code for wrong vs right pattern | Anti-pattern format |
| Definitional questions ("What does X do?") | Interview bank — explicitly forbidden |
| One-sentence answers | Minimum 5 sentences at Senior level |
| No question grouping | Standard 3 — format required |
| Only 2 questions | Minimum 3 per file |
| No Quick Fire section | Standard 3 — all 3 groups required |
