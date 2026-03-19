# Exercise README — JDBC Fundamentals

## Goals
1. Build CRUD operations using raw JDBC `PreparedStatement`
2. Implement batch processing with transaction control and chunking

## Exercise Files

| File | Focus |
|---|---|
| `Ex01_CrudOperations.java` | INSERT, SELECT, UPDATE, DELETE with `PreparedStatement` |
| `Ex02_BatchProcessing.java` | Batch inserts with `addBatch()`, chunking strategy |

## Prerequisites
- PostgreSQL running on `localhost:5432`
- Database `springmastery` created
- Table created:
```sql
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    salary DECIMAL(10,2) NOT NULL CHECK (salary > 0)
);
```

## Verification
- [ ] All methods use `PreparedStatement` (never `Statement`)
- [ ] All resources wrapped in `try-with-resources`
- [ ] Transactions used for batch operations (`setAutoCommit(false)`)
- [ ] `RETURN_GENERATED_KEYS` used for INSERT returning IDs
- [ ] Batch chunking prevents OutOfMemoryError for large datasets
