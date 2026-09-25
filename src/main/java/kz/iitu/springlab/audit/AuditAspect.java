package kz.iitu.springlab.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
@Order(1)
public class AuditAspect {

    private static final Logger log =
            LoggerFactory.getLogger(AuditAspect.class);

    @Around(
            value = "@annotation(audited)",
            argNames = "pjp,audited"
    )
    public Object audit(
            ProceedingJoinPoint pjp,
            Audited audited) throws Throwable {

        String action = audited.action();
        String timestamp = Instant.now().toString();

        if (audited.logArguments()) {
            log.info(
                    "[AUDIT] start {} time={} args={}",
                    action,
                    timestamp,
                    Arrays.toString(pjp.getArgs())
            );
        } else {
            log.info(
                    "[AUDIT] start {} time={}",
                    action,
                    timestamp
            );
        }

        try {
            Object result = pjp.proceed();

            log.info("[AUDIT] {} success", action);

            return result;
        } catch (Throwable ex) {
            log.error(
                    "[AUDIT] {} failure: {}",
                    action,
                    ex.getMessage()
            );

            throw ex;
        }
    }
}