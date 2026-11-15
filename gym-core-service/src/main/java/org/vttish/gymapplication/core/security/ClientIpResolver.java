package org.vttish.gymapplication.core.security;

import jakarta.servlet.http.HttpServletRequest;

public interface ClientIpResolver {

  String resolve(HttpServletRequest request);
}
