package org.vttish.gymapplication.aspect;

import java.util.Arrays;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

  @Pointcut("execution(* org.vttish.gymapplication.service.impl.*.*(..))")
  public void serviceImplementationMethods() {
  }

  @Around("serviceImplementationMethods()")
  public Object logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
    Class<?> targetClass = joinPoint.getTarget().getClass();
    Logger logger = LoggerFactory.getLogger(targetClass);
    String methodName = joinPoint.getSignature().getName();
    Object[] args = joinPoint.getArgs();

    if (logger.isDebugEnabled()) {
      logger.debug("Executing {}() with arguments: {}", methodName, Arrays.toString(args));
    } else if (logger.isInfoEnabled()) {
      logger.info("Executing {}()", methodName);
    }

    long startTime = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - startTime;

      if (logger.isDebugEnabled()) {
        logger.debug("Finished {}() in {} ms. Returned: {}", methodName, executionTime, result);
      } else if (logger.isInfoEnabled()) {
        logger.info("Finished {}() in {} ms", methodName, executionTime);
      }

      return result;

    } catch (Throwable ex) {
      logger.error("Exception in {}() with cause: {}", methodName, ex.getMessage(), ex);
      throw ex;
    }
  }
}
