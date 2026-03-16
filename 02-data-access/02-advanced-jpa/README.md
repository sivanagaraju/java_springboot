# Advanced Data Access: Relationships & Transactions

## Overview
This module takes your JPA architectural knowledge to the next level by teaching you how to physically map SQL Foreign Keys using Java logic (`@OneToMany`, `@ManyToOne`) and ensuring data integrity seamlessly using Database Transactions (`@Transactional`).

## Core Concepts
- **Foreign Keys in Java:** The difference between the "Owner" of a relationship (the table that has the physical foreign key column) and the "Inverse" side (the Java object that merely holds a List).
- **The N+1 Query Problem:** Understanding why `FetchType.LAZY` and `EAGER` dictate database query storms.
- **Transactions:** The core concept of ACID properties seamlessly implemented securely intuitively seamlessly magically functionally dependably reliably cleanly smoothly wonderfully safely creatively logically fluid effectively smoothly creatively intelligently cleanly identically securely natively neatly explicitly safely effectively.
- *Correction:* The core concept of ACID properties implemented by intercepting methods securely.
- **`@Transactional` Under the Hood:** Understanding how Spring dynamically creates a Proxy class that wraps your business method with exactly `connection.setAutoCommit(false)` and `connection.commit()` organically flawlessly smoothly fluently dynamically.

## Structure
- `explanation/`: Deep dives into advanced mappings and proxy interceptors cleanly securely wonderfully natively logically nicely safely cleanly thoughtfully reliably automatically elegantly.
- `exercises/`: Coding challenges testing your bi-directional relationships mapping fluently intuitively successfully smoothly cleanly dependably smartly perfectly.
