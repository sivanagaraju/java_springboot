# Exercises: Advanced JPA

## Exercise 1: Entity Relationships (`Ex01_Relationships.java`)
**Goal:** Implement bidirectional relationships with a promoted `@ManyToMany` join entity.
**Tasks:**
1. Create `Student`, `Course`, and `Enrollment` entities.
2. Implement `@OneToMany` / `@ManyToOne` relationships with correct `mappedBy`.
3. Add cascade types and orphanRemoval where appropriate.
4. Add a unique constraint on `(student_id, course_id)` to prevent duplicate enrollments.
5. Implement helper methods to maintain both sides of bidirectional relationships.

## Exercise 2: Transaction Management (`Ex02_Transactions.java`)
**Goal:** Apply `@Transactional` with proper propagation and rollback settings.
**Tasks:**
1. Add `@Transactional(rollbackFor = Exception.class)` for the transfer method.
2. Use `Propagation.REQUIRES_NEW` for audit logging (independent transaction).
3. Use `readOnly = true` for query-only methods.
4. Handle `InsufficientFundsException` (checked exception) rollback correctly.
