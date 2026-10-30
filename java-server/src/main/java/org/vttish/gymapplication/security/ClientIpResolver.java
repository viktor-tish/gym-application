package org.vttish.gymapplication.security;

import jakarta.servlet.http.HttpServletRequest;

public interface ClientIpResolver {

  String resolve(HttpServletRequest request);
}
