/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : SetterInjectionDemo.java                               ║
 * ║  MODULE : 05-spring-core / 02-dependency-injection                ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Demonstrates setter injection for optional      ║
 * ║                   dependencies                                     ║
 * ║  WHY IT EXISTS  : Some dependencies are truly optional — setter   ║
 * ║                   injection handles this cleanly                   ║
 * ║  PYTHON COMPARE : Python def __init__(self, cache=None)          ║
 * ║                   → Java @Autowired(required=false) on setter    ║
 * ║  USE CASES      : 1) Optional cache integration                   ║
 * ║                   2) Optional monitoring/metrics                   ║
 * ║                   3) Reconfigurable dependencies                   ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — Setter vs Constructor                            ║
 * ║                                                                    ║
 * ║    Constructor:                Setter:                             ║
 * ║    new Service(A, B, C)       new Service(A, B)                   ║
 * ║    → all required at once     service.setC(c)  ← optional        ║
 * ║    → final fields             → mutable field                     ║
 * ║    → immutable                → can be null                       ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Report generated with/without cache             ║
 * ║  RELATED FILES  : ConstructorInjectionDemo.java                   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Simple cache interface for demonstration.
 */
interface CacheProvider {
    String get(String key);
    void put(String key, String value);
}

/**
 * Report service with optional cache dependency via setter injection.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   class ReportService:
 *       def __init__(self, data_source: DataSource):
 *           self.data_source = data_source  # required
 *           self.cache = None               # optional
 *
 *       def set_cache(self, cache: CacheProvider):
 *           self.cache = cache
 * </pre>
 *
 * <p><b>ASCII — Optional dependency pattern:</b>
 * <pre>
 *   ReportService
 *       │
 *       ├── dataSource (REQUIRED — constructor)
 *       │   → NPE if missing = fail-fast
 *       │
 *       └── cache (OPTIONAL — setter)
 *           → null if missing = graceful degradation
 * </pre>
 */
@Service
class ReportService {

    private final String dataSource;  // required: final = constructor
    private CacheProvider cache;       // optional: mutable = setter

    /**
     * Required dependency via constructor.
     */
    public ReportService() {
        this.dataSource = "PostgreSQL";  // simplified for demo
        System.out.println("  → ReportService created (dataSource: " + dataSource + ")");
    }

    /**
     * Optional dependency via setter.
     * Spring skips this if no CacheProvider bean exists.
     *
     * @param cache the optional cache provider
     */
    @Autowired(required = false)
    public void setCacheProvider(CacheProvider cache) {
        this.cache = cache;
        System.out.println("  → Cache injected: " + cache.getClass().getSimpleName());
    }

    /**
     * Generates a report, using cache if available.
     *
     * <p><b>Flow:</b>
     * <pre>
     *   generateReport(name)
     *       │
     *       ▼
     *   cache != null? ──► YES: check cache → return if found
     *       │
     *       ▼ NO
     *   Query database → build report
     *       │
     *       ▼
     *   cache != null? ──► YES: store in cache
     *       │
     *       ▼
     *   Return report
     * </pre>
     *
     * @param reportName the report to generate
     * @return the generated report content
     */
    public String generateReport(String reportName) {
        // Graceful degradation — works with or without cache
        if (cache != null) {
            String cached = cache.get(reportName);
            if (cached != null) return cached;
        }

        String report = "Report: " + reportName + " (from " + dataSource + ")";

        if (cache != null) {
            cache.put(reportName, report);
        }

        return report;
    }
}
