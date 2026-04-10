/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  FILE   : BeanPostProcessorDemo.java                             ║
 * ║  MODULE : 05-spring-core / 03-bean-lifecycle                      ║
 * ║  GRADLE : ./gradlew :05-spring-core:bootRun                     ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  PURPOSE        : Shows how BeanPostProcessor intercepts every   ║
 * ║                   bean during creation — this is how Spring      ║
 * ║                   creates AOP proxies for @Transactional etc     ║
 * ║  WHY IT EXISTS  : BeanPostProcessor = the engine behind Spring  ║
 * ║                   magic — @Autowired, @Async, @Transactional     ║
 * ║  PYTHON COMPARE : Python: class decorator applied to ALL classes ║
 * ║                   → Java: BeanPostProcessor applied to ALL beans ║
 * ║  USE CASES      : 1) Custom annotation processing                ║
 * ║                   2) Timing bean creation                         ║
 * ║                   3) AOP proxy wrapping (internal Spring use)    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  ASCII DIAGRAM — BeanPostProcessor Flow                           ║
 * ║                                                                    ║
 * ║    For EVERY bean:                                                 ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    postProcessBeforeInitialization(bean, name)                     ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    @PostConstruct / afterPropertiesSet()                          ║
 * ║        │                                                           ║
 * ║        ▼                                                           ║
 * ║    postProcessAfterInitialization(bean, name)                      ║
 * ║        │   ← Can return a PROXY wrapping the original bean        ║
 * ║        ▼                                                           ║
 * ║    Bean ready (possibly wrapped in proxy)                          ║
 * ║                                                                    ║
 * ╠══════════════════════════════════════════════════════════════════╣
 * ║  HOW TO RUN     : ./gradlew :05-spring-core:bootRun               ║
 * ║  EXPECTED OUTPUT: Log messages for each bean processed           ║
 * ║  RELATED FILES  : BeanLifecycleDemo.java, 05-beanpostprocessor.md║
 * ╚══════════════════════════════════════════════════════════════════╝
 */
package com.learning.springcore.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Custom BeanPostProcessor that logs bean creation timing.
 *
 * <p><b>Python equivalent:</b>
 * <pre>
 *   # Python metaclass that wraps ALL classes (hypothetical)
 *   class TimingMeta(type):
 *       def __call__(cls, *args, **kwargs):
 *           start = time.time()
 *           instance = super().__call__(*args, **kwargs)
 *           print(f"{cls.__name__} created in {time.time()-start:.3f}s")
 *           return instance
 * </pre>
 *
 * <p><b>ASCII — BPP applies to ALL beans in the context:</b>
 * <pre>
 *   Bean1 ──► BPP.before() → @PostConstruct → BPP.after() → Ready
 *   Bean2 ──► BPP.before() → @PostConstruct → BPP.after() → Ready
 *   Bean3 ──► BPP.before() → @PostConstruct → BPP.after() → Ready
 *   ...every single bean goes through this pipeline
 * </pre>
 */
@Component
class TimingBeanPostProcessor implements BeanPostProcessor {

    /**
     * Called BEFORE @PostConstruct on every bean.
     *
     * @param bean the bean instance
     * @param beanName the name of the bean
     * @return the bean (possibly modified)
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        // Only log our custom beans, not Spring internals
        if (beanName.contains("lifecycle") || beanName.contains("Lifecycle")) {
            System.out.println("  [BPP] Before init: " + beanName);
        }
        return bean;
    }

    /**
     * Called AFTER @PostConstruct on every bean.
     * This is where Spring creates AOP proxies for @Transactional, @Async, etc.
     *
     * @param bean the bean instance
     * @param beanName the name of the bean
     * @return the bean (or a proxy wrapping it)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        if (beanName.contains("lifecycle") || beanName.contains("Lifecycle")) {
            System.out.println("  [BPP] After init: " + beanName +
                    " → class: " + bean.getClass().getSimpleName());
        }
        return bean;  // in production, Spring might return a PROXY here
    }
}
