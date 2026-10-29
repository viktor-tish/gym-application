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

    MDC.put("transactionId", transactionId);
    response.setHeader(TRANSACTION_ID_HEADER, transactionId);

    String requestQuery = request.getQueryString() != null
        ? "?" + request.getQueryString()
        : "";

    log.info("Incoming request: {} {}{}",
        request.getMethod(), request.getRequestURI(), requestQuery);

    Exception caughtException = null;
    try {
      filterChain.doFilter(request, response);
    } catch (Exception ex) {
      caughtException = ex;
      throw ex;
    } finally {
      try {
        long duration = System.currentTimeMillis() - startTime;
        int status = response.getStatus();

        if (status >= 400 || caughtException != null) {
          log.error("Completed request: {} {} with status {} in {} ms (error: {})",
              request.getMethod(), request.getRequestURI(), status, duration,
              caughtException != null ? caughtException.getMessage() : "HTTP error");
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
  }

  private String resolveTransactionId(HttpServletRequest request) {
    String header = request.getHeader(TRANSACTION_ID_HEADER);

    return header == null || header.isBlank()
        ? UUID.randomUUID().toString()
        : header;
  }
}
