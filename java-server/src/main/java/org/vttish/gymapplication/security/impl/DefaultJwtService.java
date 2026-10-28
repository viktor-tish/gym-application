package org.vttish.gymapplication.security.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vttish.gymapplication.exception.UnauthenticatedException;
import org.vttish.gymapplication.security.JwtService;
import org.vttish.gymapplication.security.Role;
import org.vttish.gymapplication.security.UserPrincipal;

@Service
public class DefaultJwtService implements JwtService {
  private final SecretKey signingKey;
  private final long expirationMs;

  public DefaultJwtService(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration-ms}") long expirationMs
  ) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.expirationMs = expirationMs;
  }

  @Override
  public String generateToken(UserPrincipal principal) {
    Instant now = Instant.now();
    Instant validity = now.plusMillis(expirationMs);

    return Jwts.builder()
        .subject(principal.username())
        .claim("role", principal.role().name())
        .issuedAt(java.util.Date.from(now))
        .expiration(java.util.Date.from(validity))
        .signWith(signingKey)
        .compact();
  }

  @Override
  public UserPrincipal parseToken(String token) {
    if (token == null || token.isBlank()) {
      throw new UnauthenticatedException("JWT token is missing");
    }

    try {
      Claims claims = Jwts.parser()
          .verifyWith(signingKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      String username = claims.getSubject();
      String roleName = claims.get("role", String.class);

      if (username == null || roleName == null) {
        throw new UnauthenticatedException("Invalid or expired JWT token");
      }

      Role role = Role.valueOf(roleName);

      return new UserPrincipal(username, role);
    } catch (JwtException | IllegalArgumentException ex) {
      throw new UnauthenticatedException("Invalid or expired JWT token");
    }
  }
}
