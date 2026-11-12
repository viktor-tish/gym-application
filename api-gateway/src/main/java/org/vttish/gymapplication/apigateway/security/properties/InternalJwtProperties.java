package org.vttish.gymapplication.apigateway.security.properties;

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
    @NotEmpty
    List<Rule> rules
) {

  public record Rule(

      @NotEmpty
      Set<String> methods,

      @NotBlank
      String path,

      @NotEmpty
      List<String> audiences,

      @NotEmpty
      List<String> scopes
  ) {
  }
}
