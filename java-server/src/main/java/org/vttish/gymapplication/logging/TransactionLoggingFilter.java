package org.vttish.gymapplication.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class TransactionLoggingFilter extends OncePerRequestFilter {

  private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String transactionId = resolveTransactionId(request);
    long startTime = System.currentTimeMillis();

    response.setHeader(TRANSACTION_ID_HEADER, transactionId);

    try (MDC.MDCCloseable ignored = MDC.putCloseable("transactionId", transactionId)) {
      log.info("Incoming request: {} {}{}",
          request.getMethod(), request.getRequestURI(), resolveQuery(request));

      try {
        filterChain.doFilter(request, response);
      } catch (Exception ex) {
        log.error("Completed request: {} {} failed in {} ms",
            request.getMethod(), request.getRequestURI(),
            System.currentTimeMillis() - startTime, ex);
        throw ex;
      }

      long duration = System.currentTimeMillis() - startTime;
      int status = response.getStatus();

      if (status >= 400) {
        log.error("Completed request: {} {} with status {} in {} ms",
            request.getMethod(), request.getRequestURI(), status, duration);
      } else {
        log.info("Completed request: {} {} with status {} in {} ms",
            request.getMethod(), request.getRequestURI(), status, duration);
      }
    }
  }

  private String resolveTransactionId(HttpServletRequest request) {
    String header = request.getHeader(TRANSACTION_ID_HEADER);

    return header == null || header.isBlank()
        ? UUID.randomUUID().toString()
        : header;
  }

  private String resolveQuery(HttpServletRequest request) {
    String query = request.getQueryString();

    return query != null ? "?" + query : "";
  }
}
