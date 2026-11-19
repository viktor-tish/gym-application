package org.vttish.gymapplication.gateway.security.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.security.internal-jwt")
@Validated
public record InternalJwtProperties(

    @NotBlank
    String secret,

    @NotNull
    long expirationMs,

    @Valid
    List<Rule> rules
) {

  public record Rule(

      @NotEmpty
      Set<Verb> methods,

      @NotBlank
      String path,

      @NotEmpty
      List<String> audiences,

      @NotEmpty
      List<String> scopes
  ) {
  }

  public enum Verb {
    GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS
  }
}
