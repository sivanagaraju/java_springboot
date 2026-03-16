# Collections & Data Structures — Concept Map

```markmap
# Collections Framework

## Interface Hierarchy
- **Iterable**
  - **Collection**
    - **List** (ordered, indexed, duplicates OK)
      - ArrayList
      - LinkedList
      - Vector (legacy)
    - **Set** (no duplicates)
      - HashSet
      - LinkedHashSet
      - TreeSet (sorted)
    - **Queue** / **Deque**
      - PriorityQueue
      - ArrayDeque
- **Map** (key-value pairs, NOT Collection!)
  - HashMap
  - LinkedHashMap
  - TreeMap (sorted keys)
  - Hashtable (legacy)

## ArrayList
- Backed by Object[]
- O(1) random access
- O(n) insert at middle
- Auto-grows: newCapacity = old * 1.5
- *Default initial capacity: 10*

## LinkedList
- Doubly-linked nodes
- O(1) insert/remove at head/tail
- O(n) random access
- Also implements Deque
- *Higher memory: each node = data + prev + next*

## HashSet / HashMap
- hash % bucketCount → bucket index
- **Collisions**: linked list → tree (>8)
- Load factor: 0.75 → rehash
- hashCode() + equals() contract
- *Red-black tree for bins ≥ 8 (Java 8+)*

## TreeSet / TreeMap
- Red-Black tree (self-balancing BST)
- O(log n) for all operations
- Elements must be Comparable
- *Natural ordering or custom Comparator*

## Collections Utility Class
- sort(), reverse(), shuffle()
- unmodifiableList()
- synchronizedList()
- frequency(), disjoint()

## Choosing a Collection
- Need index access? → **ArrayList**
- Need unique elements? → **HashSet**
- Need sorted unique? → **TreeSet**
- Need key-value pairs? → **HashMap**
- Need sorted key-value? → **TreeMap**
- Need insertion order? → **LinkedHashMap**
- Need FIFO queue? → **ArrayDeque**

## Interview Hot Spots
- HashMap internal structure
- hashCode/equals contract
- ConcurrentModificationException
- ArrayList vs LinkedList (ALWAYS ArrayList)
- fail-fast vs fail-safe iterators
```
