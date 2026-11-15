package org.vttish.gymapplication.core.security.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.core.security.ClientIpResolver;

@Component
public class DefaultClientIpResolver implements ClientIpResolver {

  private static final String CLIENT_IP_HEADER = "X-Client-Ip";

  @Override
  public String resolve(HttpServletRequest request) {
    String clientIp = request.getHeader(CLIENT_IP_HEADER);

    if (clientIp != null) {
      return clientIp;
    }

    return request.getRemoteAddr();
  }
}
