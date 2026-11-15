package org.vttish.gymapplication.core.logging;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

@Aspect
@Component
public class LoggingAspect {

  @Around("execution(public * *(..)) && @within(loggable)")
  @SuppressWarnings({"java:S1172", "java:S1181", "java:S2139"})
  public Object logTargetAccess(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
    Logger logger = LoggerFactory.getLogger(getTargetClass(joinPoint));
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    if (logger.isDebugEnabled()) {
      logger.debug("Executing {}() with arguments: {}", methodName, Arrays.toString(args));
    } else if (logger.isInfoEnabled()) {
      logger.info("Executing {}()", methodName);
    }

    long startTime = System.nanoTime();
    try {
      Object result = joinPoint.proceed();
      long executionTimeUs = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);

      if (logger.isDebugEnabled()) {
        logger.debug("Finished {}() in {} ms. Returned: {}",
            methodName, formatMillis(executionTimeUs), result);
      } else if (logger.isInfoEnabled()) {
        logger.info("Finished {}() in {} ms", methodName, formatMillis(executionTimeUs));
      }

      return result;

    } catch (Throwable ex) {
      long executionTimeUs = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTime);

      logger.warn("Failed {}() after {} ms: {}", methodName,
          formatMillis(executionTimeUs), ex.toString());

      throw ex;
    }
  }

  private Class<?> getTargetClass(ProceedingJoinPoint joinPoint) {
    Object target = joinPoint.getTarget();

    return target != null ? ClassUtils.getUserClass(target)
        : joinPoint.getSignature().getDeclaringType();
  }

  private String formatMillis(long micros) {
    return "%d.%03d".formatted(micros / 1000, micros % 1000);
  }
}
