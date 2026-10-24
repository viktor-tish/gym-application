package org.vttish.gymapplication.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

public record PublicEndpoint(HttpMethod method, String pattern) {
  private static final PathMatcher PATH_MATCHER = new AntPathMatcher();
  private static final UrlPathHelper URL_PATH_HELPER = UrlPathHelper.defaultInstance;

  public static PublicEndpoint of(HttpMethod method, String pattern) {
    return new PublicEndpoint(method, pattern);
  }

  public static PublicEndpoint anyMethod(String pattern) {
    return new PublicEndpoint(null, pattern);
  }

  public boolean matches(HttpServletRequest request) {
    if (request == null) {
      return false;
    }

    boolean methodMatches = method == null || method.name().equalsIgnoreCase(request.getMethod());
    String path = URL_PATH_HELPER.getPathWithinApplication(request);
    return methodMatches && PATH_MATCHER.match(pattern, path);
  }
}
