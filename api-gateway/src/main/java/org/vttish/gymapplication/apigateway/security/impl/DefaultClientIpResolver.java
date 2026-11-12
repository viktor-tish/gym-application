package org.vttish.gymapplication.apigateway.security.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.apigateway.security.ClientIpResolver;

@Component
public class DefaultClientIpResolver implements ClientIpResolver {

  @Override
  public String resolve(HttpServletRequest request) {
    return request.getRemoteAddr();
  }
}
