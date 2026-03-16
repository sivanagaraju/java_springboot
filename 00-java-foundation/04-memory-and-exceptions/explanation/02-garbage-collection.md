# Garbage Collection: The Reachability Engine

At a junior level, Garbage Collection is a magical background thread that somehow deletes variables you aren't using anymore.
To a Java Architect, Garbage Collection is an intricate mathematical graph-traversal engine that exclusively identifies "reachable" memory. Everything else is aggressively purged.

## The Reachability Graph

The Garbage Collector (GC) does not hunt for "dead" objects to delete. It hunts for "live" objects to keep. Anything not proven absolutely alive is mathematically orphaned memory and destroyed.

### GC Roots
The JVM tracks a set of specific physical memory anchors known as **GC Roots**. These definitively include:
1. Local variables presently on active Thread Stacks.
2. Static variables presently attached to loaded Classes.
3. Active JNI (Java Native Interface) references.

The GC executes a massive **Mark and Sweep** algorithm:
1. **MARK Phase:** The GC starts at every single GC Root. It follows every object reference perfectly, marking every object it touches as "Alive."
2. **SWEEP Phase:** The GC physically scans the entire physical Heap memory perfectly. Any object that does not possess an "Alive" mark is immediately deleted from physical memory.

## The Generational Hypothesis

The JVM architects noticed a universal mathematical law in modern software engineering:
> "Most objects die young. Older objects tend to live forever."

To massively optimize the GC, the Heap is partitioned into Generations:

### 1. The Young Generation (Eden + Survivor Spaces)
- Every single new object initially physically spawns in the **Eden Space**.
- Eden fills up extraordinarily quickly. When it is full, a **Minor GC** occurs.
- A Minor GC is highly optimized and localized. It mathematically scans only the Young Generation.
- Surviving objects are physically moved to a "Survivor" space.

### 2. The Old Generation (Tenured)
- If an object survives enough Minor GCs (typically 15 cycles natively), the JVM historically proves the object is "long-lived" (e.g., a Database Connection Pool, a Spring Singleton Bean).
- It physically promotes the object to the **Old Generation**.
- The Old Generation is massive. When it fills up, a **Major GC (Full GC)** occurs.
- **Architect Warning:** A Major GC forces a catastrophic "Stop The World" pause. The JVM literally physically freezes every single executing application thread completely until the Massive Sweep is completed.

Modern GC Algorithms (like G1GC, ZGC, and Shenandoah) exist specifically to partition the Heap mathematically to avoid these massive "Stop The World" application freezes.
