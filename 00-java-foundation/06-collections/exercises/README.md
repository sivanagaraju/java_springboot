# Collections — Exercises

## Prerequisites
Read all explanation files in `../explanation/` before attempting.

## Exercises

### Ex01 — Frequency Counter (`Ex01_FrequencyCounter.java`)
Implement: `countWordFrequency`, `topNWords`, `groupAnagrams`, `findFirstNonRepeating` using `HashMap`, `LinkedHashMap`, and Stream operations.

**Skills:** `HashMap.merge()`, `Collectors.groupingBy`, `LinkedHashMap` insertion order, entry sorting
**Time estimate:** 35 minutes

### Ex02 — LRU Cache (`Ex02_LRUCache.java`)
Build an O(1) LRU cache using `LinkedHashMap(capacity, loadFactor, accessOrder=true)` and `removeEldestEntry()`.

**Skills:** `LinkedHashMap`, access-order mode, `removeEldestEntry`, `O(1)` complexity guarantee
**Time estimate:** 25 minutes

## Solutions
See `solutions/` directory after attempting.

## Connection to Spring Boot
- Ex01 → `@Cacheable` / `CacheManager` — Spring's caching abstraction
- Ex02 → LRU is the most common eviction policy in `Caffeine` cache (used with Spring Cache)
- Frequency maps → analytics endpoints aggregating request counts by endpoint/user
