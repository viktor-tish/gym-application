package org.vttish.gymapplication.apigateway.security;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import org.vttish.gymapplication.apigateway.security.properties.InternalJwtProperties;

@Component
public class InternalJwtScopePolicy {

  private record Compiled(PathPattern pattern, InternalJwtProperties.Rule rule) {}

  private final List<Compiled> compiledRules;

  public InternalJwtScopePolicy(InternalJwtProperties properties) {
    PathPatternParser parser = PathPatternParser.defaultInstance;

    this.compiledRules = properties.rules().stream()
        .map(rule -> new Compiled(parser.parse(rule.path()), rule))
        .toList();
  }

  public Optional<InternalJwtProperties.Rule> match(String method, String path) {
    PathContainer container = PathContainer.parsePath(path);

    return compiledRules.stream()
        .filter(compiled ->
          compiled.rule()
              .methods()
              .contains(InternalJwtProperties.Verb.valueOf(method.toUpperCase(Locale.ROOT)))
        )
        .filter(c -> c.pattern().matches(container))
        .findFirst()
        .map(Compiled::rule);
  }
}
