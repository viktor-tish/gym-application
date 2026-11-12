package org.vttish.gymapplication.security.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.security.ClientIpResolver;

@Component
public class DefaultClientIpResolver implements ClientIpResolver {

  @Override
  public String resolve(HttpServletRequest request) {
    String clientIp = request.getHeader("X-Client-Ip");

    if (clientIp != null) {
      return clientIp;
    }

    return request.getRemoteAddr();
  }
}
