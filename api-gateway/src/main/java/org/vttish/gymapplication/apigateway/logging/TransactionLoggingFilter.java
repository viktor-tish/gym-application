package org.vttish.gymapplication.apigateway.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransactionLoggingFilter extends OncePerRequestFilter {

  private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
  private static final String PARENT_ID_HEADER = "X-Parent-Id";

  private static final String TRANSACTION_ID_KEY = "transactionId";
  private static final String PARENT_ID_KEY = "parentId";
  private static final String SPAN_ID_KEY = "spanId";

  private static final String ROOT_PARENT = "root";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    long startTimeNanos = System.nanoTime();
    String transactionId = resolveTransactionId(request, response);
    String parentId = resolveParentId(request);
    String spanId = newId();

    String method = request.getMethod();
    String uri = request.getRequestURI();

    try (
        MDC.MDCCloseable ignoredTransaction = MDC.putCloseable(TRANSACTION_ID_KEY, transactionId);
        MDC.MDCCloseable ignoredParent = MDC.putCloseable(PARENT_ID_KEY, parentId);
        MDC.MDCCloseable ignoredSpan = MDC.putCloseable(SPAN_ID_KEY, spanId)
    ) {
      log.info("Incoming request: {} {}{}",
          request.getMethod(), request.getRequestURI(), resolveQuery(request));

      try {
        filterChain.doFilter(request, response);
      } catch (Exception ex) {
        log.error("Completed request: {} {} failed in {} ms",
            method, uri, elapsedMillis(startTimeNanos), ex);
        throw ex;
      }

      logCompletion(method, uri, response.getStatus(), elapsedMillis(startTimeNanos));
    }
  }

  private void logCompletion(String method, String uri, int status, String elapsed) {
    String completePattern = "Completed request: {} {} with status {} in {} ms";

    if (status >= 500) {
      log.error(completePattern, method, uri, status, elapsed);
    } else if (status >= 400) {
      log.warn(completePattern, method, uri, status, elapsed);
    } else {
      log.info(completePattern, method, uri, status, elapsed);
    }
  }

  private String resolveTransactionId(HttpServletRequest request, HttpServletResponse response) {
    String header = request.getHeader(TRANSACTION_ID_HEADER);

    if (header == null || header.isBlank()) {
      String generated = newId();
      log.info("No {} header; generated transaction id: {}", TRANSACTION_ID_HEADER, generated);

      response.setHeader(TRANSACTION_ID_HEADER, generated);

      return generated;
    }

    try {
      return UUID.fromString(header.strip()).toString();
    } catch (IllegalArgumentException ex) {
      String generated = newId();
      log.warn("Invalid {} header; generated transaction id: {}", TRANSACTION_ID_HEADER, generated);

      response.setHeader(TRANSACTION_ID_HEADER, generated);

      return generated;
    }
  }

  private String resolveParentId(HttpServletRequest request) {
    String header = request.getHeader(PARENT_ID_HEADER);

    if (header == null || header.isBlank()) {
      log.info("No {} header; proceeding with parent id: {}", PARENT_ID_HEADER, ROOT_PARENT);

      return ROOT_PARENT;
    }

    try {
      return UUID.fromString(header.strip()).toString();
    } catch (IllegalArgumentException ex) {
      log.warn("Invalid {} header; proceeding with parent id: {}", PARENT_ID_HEADER, ROOT_PARENT);

      return ROOT_PARENT;
    }
  }

  private String newId() {
    return UUID.randomUUID().toString();
  }

  private String resolveQuery(HttpServletRequest request) {
    String query = request.getQueryString();

    return query != null ? "?" + query : "";
  }

  private static String elapsedMillis(long startTimeNanos) {
    long micros = TimeUnit.NANOSECONDS.toMicros(System.nanoTime() - startTimeNanos);

    return "%d.%03d".formatted(micros / 1000, micros % 1000);
  }
}
