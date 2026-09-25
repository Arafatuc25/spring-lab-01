package kz.iitu.springlab.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Order(0)
public class CacheAspect {

    private static final Logger log =
            LoggerFactory.getLogger(CacheAspect.class);

    private final Map<String, Object> cache =
            new ConcurrentHashMap<>();

    @Around("@annotation(kz.iitu.springlab.audit.SimpleCache)")
    public Object cacheResult(
            ProceedingJoinPoint pjp) throws Throwable {

        String key = createKey(pjp);

        if (cache.containsKey(key)) {
            log.info("[CACHE] HIT: {}", key);

            return cache.get(key);
        }

        log.info("[CACHE] MISS: {}", key);

        Object result = pjp.proceed();

        cache.put(key, result);

        log.info("[CACHE] SAVED: {}", key);

        return result;
    }

    private String createKey(ProceedingJoinPoint pjp) {
        return pjp.getSignature().toLongString()
                + Arrays.deepToString(pjp.getArgs());
    }
}