# Thread Basics: Lifecycle, Creation, and Management

## What Is a Thread?

A thread is the smallest unit of execution within a process. Java supports **true multithreading** — multiple threads execute simultaneously on different CPU cores.

```
PROCESS vs THREAD:

  ┌─────────────────────────────────────────────────────────┐
  │  PROCESS (JVM Instance)                                  │
  │  ┌───────────────────────────────────────────────────┐   │
  │  │  Shared Heap Memory (objects, arrays)             │   │
  │  ├───────────┬───────────┬───────────┬───────────────┤   │
  │  │  Thread 1 │  Thread 2 │  Thread 3 │   Thread N    │   │
  │  │  ┌─────┐  │  ┌─────┐  │  ┌─────┐  │   ┌─────┐    │   │
  │  │  │Stack│  │  │Stack│  │  │Stack│  │   │Stack│    │   │
  │  │  │(own)│  │  │(own)│  │  │(own)│  │   │(own)│    │   │
  │  │  └─────┘  │  └─────┘  │  └─────┘  │   └─────┘    │   │
  │  └───────────┴───────────┴───────────┴───────────────┘   │
  └─────────────────────────────────────────────────────────┘

  Key insight:
    - Each thread has its OWN stack (local variables, method calls)
    - All threads SHARE the heap (objects, fields)
    - Sharing = DANGER (race conditions)
```

## Thread Lifecycle

```
THREAD STATE MACHINE:

  ┌───────┐    start()    ┌──────────┐   CPU schedules  ┌─────────┐
  │  NEW  │──────────────▶│ RUNNABLE │─────────────────▶│ RUNNING │
  └───────┘               └──────────┘                  └────┬────┘
                               ▲                             │
                               │                             │
                 notify() /    │     ┌───────────────────────┤
                 lock released │     │                       │
                               │     ▼                       │
                          ┌────────────┐                     │
                          │  BLOCKED / │   wait()/sleep()    │
                          │  WAITING / │◀────────────────────┤
                          │  TIMED_WAIT│                     │
                          └────────────┘                     │
                                                             │
                                                             ▼
                                                      ┌─────────────┐
                                                      │ TERMINATED  │
                                                      │ (run() done)│
                                                      └─────────────┘
```

## Creating Threads (3 Ways)

### Way 1: Extend Thread (AVOID — inflexible)

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}
MyThread t = new MyThread();
t.start();
```

### Way 2: Implement Runnable (PREFERRED — composition over inheritance)

```java
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}
Thread t = new Thread(new MyTask());
t.start();
```

### Way 3: Lambda Runnable (MODERN — most concise)

```java
Thread t = new Thread(() -> {
    System.out.println("Running in: " + Thread.currentThread().getName());
});
t.start();
```

```
WHY RUNNABLE > EXTENDS THREAD:

  ┌────────────────────────────────────────────────────────────┐
  │  extends Thread:                                            │
  │    - Class can only extend ONE class (single inheritance)   │
  │    - Tightly couples task logic with thread mechanism       │
  │    - Can't submit to ExecutorService                        │
  │                                                             │
  │  implements Runnable:                                       │
  │    - Interface → can also extend other classes              │
  │    - Separates WHAT to run from HOW to run                 │
  │    - Can submit to ExecutorService (thread pool)           │
  │    - Lambdas make it even cleaner                          │
  └────────────────────────────────────────────────────────────┘
```

## Key Thread Methods

```
┌─────────────────┬────────────────────────────────────────────────┐
│  Method         │  Purpose                                       │
├─────────────────┼────────────────────────────────────────────────┤
│  start()        │  Begin execution (calls run() in new thread)  │
│  run()          │  Task code (DON'T call directly — runs on     │
│                 │  current thread, not a new one!)                │
│  join()         │  Wait for this thread to finish                │
│  join(millis)   │  Wait with timeout                             │
│  sleep(millis)  │  Pause current thread (static method)          │
│  interrupt()    │  Signal thread to stop (cooperative)           │
│  isAlive()      │  Check if thread is still running              │
│  setDaemon(true)│  Mark as daemon (dies when all user threads end)│
│  getName()      │  Get thread's name                             │
│  getState()     │  Get current state (NEW, RUNNABLE, etc.)       │
└─────────────────┴────────────────────────────────────────────────┘

⚠️ COMMON MISTAKE: Calling run() instead of start()
   thread.run()  → Executes on CURRENT thread (no multithreading!)
   thread.start() → Creates NEW thread, then calls run()
```

## Daemon Threads

```java
Thread daemon = new Thread(() -> {
    while (true) { /* background task */ }
});
daemon.setDaemon(true);  // MUST set before start()
daemon.start();
// JVM exits when all USER threads finish.  
// Daemon threads are killed automatically.
```

---

## Interview Questions

**Q1: What happens if you call `run()` instead of `start()`?**
> `run()` executes the task on the current thread — no new thread is created. `start()` creates a new OS thread and schedules `run()` to execute on it. Calling `start()` twice on the same Thread object throws `IllegalThreadStateException`.

**Q2: What is the difference between daemon and user threads?**
> User threads keep the JVM alive. Daemon threads are background helpers (GC is a daemon thread). The JVM exits when all user threads finish, killing any remaining daemon threads. Set daemon status BEFORE calling `start()`.

**Q3: How does `join()` work?**
> `join()` blocks the calling thread until the target thread completes. It's how you wait for a thread to finish its work. `join(timeout)` waits with a maximum time limit. Common pattern: start multiple threads, then join all of them to wait for completion.
