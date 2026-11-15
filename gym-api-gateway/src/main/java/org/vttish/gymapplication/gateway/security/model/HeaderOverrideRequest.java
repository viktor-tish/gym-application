package org.vttish.gymapplication.gateway.security.model;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class HeaderOverrideRequest extends HttpServletRequestWrapper {

  private final Map<String, String> overrides;

  public HeaderOverrideRequest(HttpServletRequest request, Map<String, String> overrides) {
    super(request);
    this.overrides = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    this.overrides.putAll(overrides);
  }

  @Override
  public String getHeader(String name) {
    return overrides.containsKey(name) ? overrides.get(name) : super.getHeader(name);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    if (overrides.containsKey(name)) {
      String value = overrides.get(name);

      return value == null
          ? Collections.emptyEnumeration()
          : Collections.enumeration(List.of(value));
    }

    return super.getHeaders(name);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    Set<String> names = new LinkedHashSet<>(
        Collections.list(super.getHeaderNames())
    );

    overrides.forEach((key, value) -> {
      if (value == null) {
        names.remove(key);
      } else {
        names.add(key);
      }
    });

    return Collections.enumeration(names);
  }
}
