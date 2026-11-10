package org.vttish.gymapplication.workloadreport.logging;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TracingHeadersInterceptor implements ClientHttpRequestInterceptor {

  private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
  private static final String PARENT_ID_HEADER = "X-Parent-Id";

  private static final String TRANSACTION_ID_KEY = "transactionId";
  private static final String SPAN_ID_KEY = "spanId";

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request,
      byte[] body,
      ClientHttpRequestExecution execution
  ) throws IOException {

    String transactionId = MDC.get(TRANSACTION_ID_KEY);
    String spanId = MDC.get(SPAN_ID_KEY);

    if (transactionId != null) {
      request.getHeaders().set(TRANSACTION_ID_HEADER, transactionId);
    }

    if (spanId != null) {
      request.getHeaders().set(PARENT_ID_HEADER, spanId);
    }

    return execution.execute(request, body);
  }
}
