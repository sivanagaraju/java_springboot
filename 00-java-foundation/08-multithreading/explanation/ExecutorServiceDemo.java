/**
 * ╔════════════════════════════════════════════════════════════════════════════╗
 * ║  FILE   : ExecutorServiceDemo.java                                       ║
 * ║  MODULE : 00-java-foundation / 08-multithreading                         ║
 * ║  GRADLE : ./gradlew :00-java-foundation:run -PmainClass=ExecutorServiceDemo ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates bounded thread pools, Future, timeout,    ║
 * ║                   cancellation, and graceful shutdown                    ║
 * ║  WHY IT EXISTS  : Raw Thread creation does not scale, coordinate, or     ║
 * ║                   shut down cleanly in production systems                ║
 * ║  PYTHON COMPARE : concurrent.futures.ThreadPoolExecutor                  ║
 * ║  USE CASES      : fan-out API calls, file processing, report jobs,       ║
 * ║                   async service orchestration                            ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM                                                            ║
 * ║                                                                          ║
 * ║   Caller submits tasks                                                    ║
 * ║          │                                                               ║
 * ║          ▼                                                               ║
 * ║   [ExecutorService] ----queues----> [Worker-1]                           ║
 * ║          │                           [Worker-2]                           ║
 * ║          │                           [Worker-3]                           ║
 * ║          │                               │                                ║
 * ║          ├---- Future.get() / timeout ---┤                                ║
 * ║          └---- shutdown() / awaitTermination()                           ║
 * ║                                                                          ║
 * ╠════════════════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :00-java-foundation:run -PmainClass=ExecutorServiceDemo ║
 * ║  EXPECTED OUTPUT: Three short tasks finish on reused workers, one slow   ║
 * ║                   task times out, and the pool shuts down cleanly        ║
 * ║  RELATED FILES  : 04-executor-service.md, ExecutorDemo.java              ║
 * ╚════════════════════════════════════════════════════════════════════════════╝
 */
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Shows why {@code ExecutorService} is the production-grade API for concurrency.
 *
 * <p>Python equivalent:
 * <pre>
 *   from concurrent.futures import ThreadPoolExecutor
 *
 *   with ThreadPoolExecutor(max_workers=3) as pool:
 *       futures = [pool.submit(task) for task in tasks]
 * </pre>
 * The mental model is the same, but Java developers meet this API constantly
 * through web request pools, scheduled jobs, and background work executors.
 *
 * <p>Architecture view:
 * <pre>
 *   [Caller]
 *      |
 *      v
 *   [ExecutorService] ----> [bounded worker pool] ----> [Future results]
 *      |
 *      +---- lifecycle control: shutdown / awaitTermination / cancel
 * </pre>
 */
public final class ExecutorServiceDemo {

    private ExecutorServiceDemo() {
        // Utility demo class.
    }

    /**
     * Runs the pool demo, timeout demo, and shutdown lifecycle in sequence.
     *
     * <p>Flow:
     * <pre>
     *   main()
     *     -> runBoundedPoolDemo()
     *     -> runTimeoutDemo()
     *     -> printOperationalTakeaways()
     * </pre>
     *
     * @param args command-line arguments; unused in this demo
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws ExecutionException if a submitted job fails
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        try {
            runBoundedPoolDemo(pool);
            runTimeoutDemo(pool);
            printOperationalTakeaways();
        } finally {
            shutdownPool(pool);
        }

        /*
         * EXPECTED OUTPUT:
         * ================
         * === BOUNDED POOL ===
         * inventory-sync finished on pool-1-thread-1
         * price-recalc finished on pool-1-thread-2
         * email-digest finished on pool-1-thread-3
         *
         * === FUTURE TIMEOUT AND CANCEL ===
         * Waiting for slow task...
         * Timed out waiting for slow-export, cancelling it.
         *
         * === WHY THIS BEATS RAW THREADS ===
         * Pool size stays bounded, threads are reused, and shutdown is explicit.
         */
    }

    /**
     * Demonstrates a fixed-size worker pool handling multiple tasks safely.
     *
     * @param pool the executor that owns the worker threads
     * @throws InterruptedException if invokeAll is interrupted
     * @throws ExecutionException if any job fails
     */
    private static void runBoundedPoolDemo(ExecutorService pool)
        throws InterruptedException, ExecutionException {
        System.out.println("=== BOUNDED POOL ===");

        List<Callable<String>> jobs = List.of(
            task("inventory-sync", 250),
            task("price-recalc", 150),
            task("email-digest", 200)
        );

        for (Future<String> result : pool.invokeAll(jobs)) {
            System.out.println(result.get());
        }
    }

    /**
     * Demonstrates timeout handling and cancellation with a slow task.
     *
     * @param pool the executor that runs the slow job
     * @throws InterruptedException if the waiting thread is interrupted
     * @throws ExecutionException if the task fails before timing out
     */
    private static void runTimeoutDemo(ExecutorService pool)
        throws InterruptedException, ExecutionException {
        System.out.println("\n=== FUTURE TIMEOUT AND CANCEL ===");

        Future<String> slowTask = pool.submit(task("slow-export", 900));

        try {
            System.out.println("Waiting for slow task...");
            System.out.println(slowTask.get(200, TimeUnit.MILLISECONDS));
        } catch (TimeoutException timeout) {
            System.out.println("Timed out waiting for slow-export, cancelling it.");
            slowTask.cancel(true);
        }
    }

    /**
     * Prints the production lesson behind the demo output.
     */
    private static void printOperationalTakeaways() {
        System.out.println("\n=== WHY THIS BEATS RAW THREADS ===");
        System.out.println("Pool size stays bounded, threads are reused, and shutdown is explicit.");
        System.out.println("This is the same reason servlet containers and Spring schedulers use executors.");
    }

    /**
     * Performs graceful shutdown and escalates to forced shutdown if needed.
     *
     * @param pool the executor to terminate
     * @throws InterruptedException if the calling thread is interrupted while waiting
     */
    private static void shutdownPool(ExecutorService pool) throws InterruptedException {
        pool.shutdown();

        if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
            List<Runnable> abandoned = pool.shutdownNow();
            System.out.println("Forced shutdown, abandoned tasks: " + abandoned.size());
        }
    }

    /**
     * Builds a task that simulates work and reports which worker executed it.
     *
     * <p>Python equivalent:
     * <pre>
     *   def task(name, delay):
     *       time.sleep(delay)
     *       return f"{name} on {threading.current_thread().name}"
     * </pre>
     *
     * @param name logical task name
     * @param workMillis simulated work duration in milliseconds
     * @return a callable that reports completion details
     */
    private static Callable<String> task(String name, long workMillis) {
        return () -> {
            String threadName = Thread.currentThread().getName();
            sleep(workMillis);
            return name + " finished on " + threadName;
        };
    }

    /**
     * Sleeps while preserving interruption status for cooperative cancellation.
     *
     * @param millis duration to sleep
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
    }
}
