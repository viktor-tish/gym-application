package org.vttish.gymapplication.gateway.security;

import jakarta.servlet.http.HttpServletRequest;

public interface ClientIpResolver {

  String resolve(HttpServletRequest request);
}
