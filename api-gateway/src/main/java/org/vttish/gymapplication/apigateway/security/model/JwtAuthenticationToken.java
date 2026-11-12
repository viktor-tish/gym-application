package org.vttish.gymapplication.apigateway.security.model;

import java.util.Collection;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

  private final transient UserPrincipal userPrincipal;
  private final transient JwtMetadata jwtMetadata;

  public JwtAuthenticationToken(
      UserPrincipal userPrincipal,
      JwtMetadata jwtMetadata,
      Collection<? extends GrantedAuthority> authorities
  ) {
    super(authorities);
    this.userPrincipal = userPrincipal;
    this.jwtMetadata = jwtMetadata;
    super.setAuthenticated(true);
  }

  @Override
  public @Nullable Object getCredentials() {
    return null;
  }

  @Override
  public @Nullable Object getPrincipal() {
    return userPrincipal;
  }

  public UserPrincipal userPrincipal() {
    return userPrincipal;
  }

  public JwtMetadata jwtMetadata() {
    return jwtMetadata;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof JwtAuthenticationToken that)) {
      return false;
    }

    if (!super.equals(obj)) {
      return false;
    }

    return Objects.equals(userPrincipal, that.userPrincipal) && Objects.equals(jwtMetadata, that.jwtMetadata);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), userPrincipal, jwtMetadata);
  }
}
