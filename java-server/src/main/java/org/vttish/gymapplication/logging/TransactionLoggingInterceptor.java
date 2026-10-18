package org.vttish.gymapplication.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class TransactionLoggingInterceptor implements HandlerInterceptor {
  private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
  private static final String START_TIME_ATTRIBUTE = "startTime";

  @Override
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler
  ) throws Exception {
    String transactionId = resolveTransactionId(request);

    MDC.put("transactionId", transactionId);
    response.setHeader(TRANSACTION_ID_HEADER, transactionId);
    request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

    String requestQuery = request.getQueryString() != null ? "?" + request.getQueryString() : "";

    log.info("Incoming request: {} {}{}",
        request.getMethod(), request.getRequestURI(), requestQuery);

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      Exception ex
  ) throws Exception {
    try {
      long endTime = System.currentTimeMillis();
      Object startTimeAttr = request.getAttribute(START_TIME_ATTRIBUTE);
      long duration = (startTimeAttr instanceof Long startTime) ? endTime - startTime : -1;
      int status = response.getStatus();

      if (status >= 400 || ex != null) {
        log.error("Completed request: {} {} with status {} in {} ms (error: {})",
            request.getMethod(), request.getRequestURI(), status, duration,
            ex != null ? ex.getMessage() : "HTTP error");
      } else {
        log.info("Completed request: {} {} with status {} in {} ms",
            request.getMethod(), request.getRequestURI(), status, duration);
      }
    } catch (Exception e) {
      log.error("Failed to log request completion for request: {} {}",
          request.getMethod(), request.getRequestURI(), e);
    } finally {
      MDC.clear();
    }
  }

  private String resolveTransactionId(HttpServletRequest request) {
    String header = request.getHeader(TRANSACTION_ID_HEADER);

    return header == null || header.isBlank()
        ? UUID.randomUUID().toString()
        : header;
  }
}
