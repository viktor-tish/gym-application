package org.vttish.gymapplication.gateway.security.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.gateway.security.ClientIpResolver;

@Component
public class DefaultClientIpResolver implements ClientIpResolver {

  @Override
  public String resolve(HttpServletRequest request) {
    return request.getRemoteAddr();
  }
}
